package metridoc.penn.bd

import edu.upennlib.collmanagement.CallNoService;
import edu.upennlib.collmanagement.CallNoService.CallNoCounts;
import groovy.sql.GroovyResultSetExtension;
import groovy.sql.Sql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import metridoc.penn.util.DateUtil;

import org.codehaus.groovy.grails.commons.ConfigurationHolder;
import org.hibernate.Session;

/**
*
* @author Narine Ghochikyan
*
*/
class BorrowDirectService {
	public static String BD_SERVICE_KEY = 'bd'
	public static String EZB_SERVICE_KEY = 'ezb'
	
	DataSource dataSource
	def config = ConfigurationHolder.config
	
    def dumpDataLibrary(library_id, from, to, outstream, serviceKey) {
		def reportGenerator = new LibraryDataReportGenerator();
		runReport(reportGenerator, prepareQuery(config.queries.borrowdirect.dataDumpByLibrary, serviceKey), [from, to, library_id, library_id], outstream)
    }
	
	def dumpDataMultipleItems(from, to, minTimes, outstream, serviceKey) {
		def reportGenerator = new MultipleItemsDataReportGenerator();
		runReport(reportGenerator,  prepareQuery(config.queries.borrowdirect.dataDumpMultipleItems, serviceKey), [from, to, minTimes], outstream)
	}
	
	def getSummaryDashboardData(libId, fiscalYear, serviceKey){
		Sql sql = new Sql(dataSource);
		def result = [:]
		def currentFiscalYear = fiscalYear;
		Date currentFiscalYearEnd;
		Date lastFiscalYearEnd;
		if(currentFiscalYear == null){
			def currentDate = Calendar.getInstance();
			def currentYear = currentDate.get(Calendar.YEAR);
			def currentMonth = currentDate.get(Calendar.MONTH);
			currentFiscalYear = DateUtil.getFiscalYear(currentYear, currentMonth)
			result.currentMonth = currentMonth
			
			lastFiscalYearEnd = DateUtil.getDate(currentYear-1, currentMonth,currentDate.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
			currentFiscalYearEnd = DateUtil.getDate(currentYear, currentMonth,currentDate.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
		}else{
			result.currentMonth = DateUtil.getFiscalYearEndMonth();
			currentFiscalYearEnd = DateUtil.getFiscalYearEndDate(currentFiscalYear)
			lastFiscalYearEnd =  DateUtil.getFiscalYearEndDate(currentFiscalYear - 1)
		}
		
		result.fiscalYear = currentFiscalYear
		Date currentFiscalYearStart = DateUtil.getFiscalYearStartDate(currentFiscalYear)
	
		def dates = [:]
		dates.currentFiscalYear = [currentFiscalYearStart, currentFiscalYearEnd];
		
		dates.lastFiscalYear = [DateUtil.getFiscalYearStartDate(currentFiscalYear - 1), lastFiscalYearEnd]
		
		loadDataPerLibrary(sql, true, result, dates, libId, serviceKey);
		loadDataPerLibrary(sql, false, result, dates, libId, serviceKey);
		if(libId != null){
			loadPickupData(sql, result, dates.currentFiscalYear, libId, serviceKey)
			loadShelvingData(sql, result, dates.currentFiscalYear, libId, serviceKey)
		}
		log.debug(result)
		return result
	}
	
	def loadPickupData(sql, result, currentFiscalYearDates, libId, serviceKey){
		def query = prepareQuery(config.queries.borrowdirect.countsPerPickupLocations, serviceKey)
		def sqlParams = [currentFiscalYearDates[0], currentFiscalYearDates[1], libId]
		result.pickupData = sql.rows(query, sqlParams)
	}
	
	def loadShelvingData(sql, result, currentFiscalYearDates, libId, serviceKey){
		def query = prepareQuery(config.queries.borrowdirect.countsPerShelvingLocations, serviceKey)
		def sqlParams = [currentFiscalYearDates[0], currentFiscalYearDates[1], libId]
		result.shelvingData = sql.rows(query, sqlParams)
	}
	
	def getRequestedCallNoCounts(libId, serviceKey){
		def currentDate = Calendar.getInstance();
		def currentFiscalYear = DateUtil.getFiscalYear(currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH))
		Date currentFiscalYearStart = DateUtil.getFiscalYearStartDate(currentFiscalYear)
		Date currentFiscalYearEnd = DateUtil.getFiscalYearEndDate(currentFiscalYear)
		
		Sql sql = new Sql(dataSource);
		CallNoCounts counts;
		def sqlParams = [currentFiscalYearStart, currentFiscalYearEnd]
		def query = prepareQuery(config.queries.borrowdirect.requestedCallNos, serviceKey)
		if(libId != null){
			query += " and " + config.borrowdirect.db.column.borrower + " = " + libId
		}
		log.debug("Runnig query for callnos: " + query + "\n params="+sqlParams)
		ResultSet resultSet = sql.query(query, sqlParams, {
			counts = CallNoService.getCounts(it)
		})
		return counts
	}
	
	def loadDataPerLibrary(sql, isBorrowing, result, dates, libId, tablePrefix){
		def libRoleColumn; 
		def keyForSection;
		def additionalCondition = ""
		if(isBorrowing){
			if(libId != null){
				additionalCondition = "and " + config.borrowdirect.db.column.borrower+"="+libId
				libRoleColumn = config.borrowdirect.db.column.lender
			}else{
				libRoleColumn = config.borrowdirect.db.column.borrower
			}
			keyForSection = 'borrowing'
		}else{
		if(libId != null){
			additionalCondition = "and " + config.borrowdirect.db.column.lender+"="+libId
			libRoleColumn = config.borrowdirect.db.column.borrower
		}else{
			libRoleColumn = config.borrowdirect.db.column.lender
		}
			keyForSection = 'lending'
		}
		
		def allQuery = getAdjustedQuery(config.queries.borrowdirect.countsPerLibrary, libRoleColumn, additionalCondition, tablePrefix)
		def query = getAdjustedQuery(config.queries.borrowdirect.countsPerLibraryMonthlyFilled, libRoleColumn, additionalCondition, tablePrefix)

		def allLibDataSection = getLibDataMap(-1l, result).get(keyForSection)
		//currentFiscalYear
		def sqlParams = dates.currentFiscalYear
		log.debug("Runnig query for currentFiscalYear: " + query + " params="+sqlParams)
		sql.eachRow(query, sqlParams, {
			def libData = getLibDataMap(it.getAt(0), result)
			int currentKey = it.getAt(1) != null ? it.getAt(1) : -1
			libData.get(keyForSection).currentFiscalYear.put(currentKey, it.requestsNum)
			if(it.getAt(0) > 0){
				//count sum for each month (row All Libraries)
				def prevValue = allLibDataSection.currentFiscalYear.get(currentKey);
				if( prevValue == null){
					prevValue = 0;
				}
				allLibDataSection.currentFiscalYear.put(currentKey, it.requestsNum + prevValue)
				if(currentKey == -1 && !isBorrowing && libId != null){
					//if there is lending fillRate and for current library it.getAt(0) year number is > 0
					//set default yearFillRate=1, if there were any unfilled requests
					//this number will be updated later (section '//lending: yearFillRate for lib')
					libData.get(keyForSection).yearFillRate = 1
				}
			}
		})
		//turnaround
		def turnaroundQuery = getAdjustedQuery(config.queries.borrowdirect.turnaroundPerLibrary, libRoleColumn, additionalCondition, tablePrefix)
		log.debug("Runnig query for turnaround: " + turnaroundQuery + " params="+sqlParams)
		sql.eachRow(turnaroundQuery, sqlParams, {
			def libData = getLibDataMap(it.getAt(0).longValue(), result)
			def currentMap = libData.get(keyForSection)
			currentMap.turnaroundReqRec = it.turnaroundReqRec
			currentMap.turnaroundReqShp = it.turnaroundReqShp
			currentMap.turnaroundShpRec = it.turnaroundShpRec
		})
	
		if(isBorrowing){ 
			if(libId == null){//No lender info for unfilled items 
				//borrowing:yearFillRate
				log.debug("Runnig query for yearFillRate: " + allQuery + " params="+sqlParams)
				sql.eachRow(allQuery, 
					sqlParams, {
					def libData = getLibDataMap(it.getAt(0), result)
					def currentMap = libData.get(keyForSection)
					if(currentMap.currentFiscalYear.get(-1) == null){
						currentMap.currentFiscalYear.put(-1, 0);
					}
					currentMap.yearFillRate = (it.requestsNum != 0? 
					currentMap.currentFiscalYear.get(-1)/(float)it.requestsNum :-1)
				})
			}
		}else if(libId != null){
			//lending: yearFillRate for lib
			def unfilledReqsQuery = getAdjustedQuery(config.queries.borrowdirect.countsPerLibraryUnfilled, libRoleColumn, additionalCondition, tablePrefix)
			def unfilledReqsParams = [dates.currentFiscalYear[0], dates.currentFiscalYear[1], libId] 
			log.debug("Runnig query for yearFillRate: lending: " + unfilledReqsQuery + " params="+unfilledReqsParams)
			sql.eachRow(unfilledReqsQuery,
				unfilledReqsParams, {
				def libData = getLibDataMap(it.getAt(0), result)
				def currentMap = libData.get(keyForSection)
				if(currentMap.currentFiscalYear.get(-1) == null){
					currentMap.currentFiscalYear.put(-1, 0);
				}
				def allRequests = currentMap.currentFiscalYear.get(-1) + it.requestsNum
				currentMap.yearFillRate = (allRequests != 0?
				currentMap.currentFiscalYear.get(-1)/(float)allRequests :-1)
			})
		
		}
		//lastFiscalYear
		sqlParams = dates.lastFiscalYear 
		log.debug("Runnig query for lastFiscalYear: " + query+ " params="+sqlParams)
		sql.eachRow(query, sqlParams, {
			def libData = getLibDataMap(it.getAt(0), result)
			int currentKey = it.getAt(1) != null ? it.getAt(1) : -1
			libData.get(keyForSection).lastFiscalYear.put(currentKey, it.requestsNum)
			if(it.getAt(0) > -1){
				def prevValue = allLibDataSection.lastFiscalYear.get(currentKey);
				if( prevValue == null){
					prevValue = 0;
				}
				allLibDataSection.lastFiscalYear.put(currentKey, it.requestsNum + prevValue)
			}
		})
		log.debug("Done for " + keyForSection)
		return result
	}
	def getUnfilledRequests(dateFrom, dateTo, libId, orderBy, serviceKey){
		Sql sql = new Sql(dataSource);
		def query = prepareQuery(config.queries.borrowdirect.libraryUnfilledRequests, serviceKey) + orderBy
		def sqlParams = [dateFrom, dateTo, libId]
		log.debug("Runnig query for unfilled requests " + query + "\nparams = " + sqlParams)
		return sql.rows(query, sqlParams)
	}
	def getMonthsInDisplayOrder(currentMonth){
		def monthFrom = currentMonth
		def result = [];
		if(monthFrom < DateUtil.FY_START_MONTH){
			for(int i = monthFrom; i >= 0; i--) {
				result[result.size()] = i;
			} 
			monthFrom = Calendar.DECEMBER
		}
		for(int i = monthFrom; i >= DateUtil.FY_START_MONTH; i-- ) {
			result[result.size()] = i;
		}
		return result
	}
	private String getAdjustedQuery(query, libRoleColumn, additionalCondition, tablePrefix){
		def result = query.replaceAll("\\{lib_role\\}", libRoleColumn)
		result = result.replaceAll("\\{add_condition\\}", additionalCondition)
		return prepareQuery(result, tablePrefix);
	}
	private String prepareQuery(query, tablePrefix){
		return query.replaceAll("\\{table_prefix\\}", tablePrefix)
	}
	
	private void runReport(reportGenerator, query, inputParams, outstream){
		Sql sql = new Sql(dataSource);
		log.debug("Runnig report query : " + query + "\n params="+inputParams)
		sql.eachRow(query, inputParams, {
			reportGenerator.addRowData(it)
		})
		reportGenerator.write(outstream);
	}
	
	private getLibDataMap(libId, container){
		if(container.get(libId) == null){
			container.put(libId, ['borrowing':['currentFiscalYear':[:], 'lastFiscalYear':[:]], 
				'lending':['currentFiscalYear':[:], 'lastFiscalYear':[:]]])
		}
		return container.get(libId)
	}
}
