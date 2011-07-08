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
	
	DataSource dataSource
	def config = ConfigurationHolder.config
	
    def dumpDataLibrary(library_id, from, to, outstream) {
		def reportGenerator = new LibraryDataReportGenerator();
		runReport(reportGenerator,  config.queries.borrowdirect.dataDumpByLibrary, [from, to, library_id, library_id], outstream)
    }
	
	def dumpDataMultipleItems(from, to, minTimes, outstream) {
		def reportGenerator = new MultipleItemsDataReportGenerator();
		runReport(reportGenerator,  config.queries.borrowdirect.dataDumpMultipleItems, [from, to, minTimes], outstream)
	}
	
	def getSummaryDashboardData(libId){
		Sql sql = new Sql(dataSource);
		def result = [:]
		
		def currentDate = Calendar.getInstance();
		def currentYear = currentDate.get(Calendar.YEAR);
		def currentMonth = currentDate.get(Calendar.MONTH);
		def currentFiscalYear = DateUtil.getFiscalYear(currentYear, currentMonth)
		Date currentFiscalYearStart = DateUtil.getFiscalYearStartDate(currentFiscalYear)
		Date currentFiscalYearEnd = DateUtil.getFiscalYearEndDate(currentFiscalYear)
	
		def dates = [:]
		dates.currentFiscalYear = [currentFiscalYearStart, currentFiscalYearEnd];
		
		dates.lastFiscalYear = [DateUtil.getFiscalYearStartDate(currentFiscalYear - 1),
			 DateUtil.getFiscalYearEndDate(currentFiscalYear - 1)]
		
		dates.currentMonth = [ DateUtil.getDateStartOfDay(currentYear, currentMonth, 1),
							   DateUtil.getDateEndOfDay(currentYear, currentMonth, 
										 currentDate.get(Calendar.DAY_OF_MONTH))]
		
		dates.lastYearMonth = [ DateUtil.getDateStartOfDay(currentYear - 1, currentMonth, 1),
								DateUtil.getDateEndOfDay(currentYear - 1, currentMonth,
								DateUtil.getLastDayOfMonth(currentYear - 1, currentMonth))]
		
		loadDataPerLibrary(sql, true, result, dates, libId);
		loadDataPerLibrary(sql, false, result, dates, libId);
		if(libId != null){
			loadPickupData(sql, result, dates.currentFiscalYear, libId)
		}
		log.debug(result)
		return result
	}
	
	def loadPickupData(sql, result, currentFiscalYearDates, libId){
		def query = config.queries.borrowdirect.countsPerPickupLocations
		def sqlParams = [currentFiscalYearDates[0], currentFiscalYearDates[1], libId]
		result.pickupData = sql.rows(query, sqlParams)
	}
	
	def getRequestedCallNoCounts(libId){
		def currentDate = Calendar.getInstance();
		def currentFiscalYear = DateUtil.getFiscalYear(currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH))
		Date currentFiscalYearStart = DateUtil.getFiscalYearStartDate(currentFiscalYear)
		Date currentFiscalYearEnd = DateUtil.getFiscalYearEndDate(currentFiscalYear)
		
		Sql sql = new Sql(dataSource);
		CallNoCounts counts;
		def sqlParams = [currentFiscalYearStart, currentFiscalYearEnd]
		def query = config.queries.borrowdirect.requestedCallNos
		if(libId != null){
			query += " and " + config.borrowdirect.db.column.borrower + " = " + libId
		}
		log.debug("Runnig query for callnos: " + query + "\n params="+sqlParams)
		ResultSet resultSet = sql.query(query, sqlParams, {
			counts = CallNoService.getCounts(it)
		})
		return counts
	}
	
	def loadDataPerLibrary(sql, isBorrowing, result, dates, libId){
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
		
		def allQuery = getAdjustedQuery(config.queries.borrowdirect.countsPerLibrary, libRoleColumn, additionalCondition)
		def query = getAdjustedQuery(config.queries.borrowdirect.countsPerLibraryFilled, libRoleColumn, additionalCondition)
		//currentFiscalYear
		def sqlParams = dates.currentFiscalYear
		log.debug("Runnig query for currentFiscalYear: " + query + " params="+sqlParams)
		sql.eachRow(query, sqlParams, {
			def libData = getLibDataMap(it.getAt(0), result)
			libData.get(keyForSection).currentFiscalYear = it.requestsNum
		})
		//turnaround
		def turnaroundQuery = getAdjustedQuery(config.queries.borrowdirect.turnaroundPerLibrary, libRoleColumn, additionalCondition)
		log.debug("Runnig query for turnaround: " + turnaroundQuery + " params="+sqlParams)
		sql.eachRow(turnaroundQuery, sqlParams, {
			def libData = getLibDataMap(it.getAt(0).longValue(), result)
			def currentMap = libData.get(keyForSection)
			currentMap.turnaroundReqRec = it.turnaroundReqRec
			currentMap.turnaroundReqShp = it.turnaroundReqShp
			currentMap.turnaroundShpRec = it.turnaroundShpRec
		})
	
		if(isBorrowing){
			//borrowing:yearFillRate
			log.debug("Runnig query for yearFillRate: " + allQuery + " params="+sqlParams)
			sql.eachRow(allQuery, 
				sqlParams, {
				def libData = getLibDataMap(it.getAt(0), result)
				def currentMap = libData.get(keyForSection)
				if(currentMap.currentFiscalYear == null){
					currentMap.currentFiscalYear = 0;
				}
				currentMap.yearFillRate = (it.requestsNum != 0? 
				currentMap.currentFiscalYear/(float)it.requestsNum :-1)
			})
		}
		//lastFiscalYear
		sqlParams = dates.lastFiscalYear 
		log.debug("Runnig query for lastFiscalYear: " + query+ " params="+sqlParams)
		sql.eachRow(query, sqlParams, {
			def libData = getLibDataMap(it.getAt(0), result)
			libData.get(keyForSection).lastFiscalYear = it.requestsNum
		})
		
		//currentMonth
		sqlParams = dates.currentMonth
		log.debug("Runnig query for currentMonth: " + query+ " params="+sqlParams)
		sql.eachRow(query, sqlParams, {
			def libData = getLibDataMap(it.getAt(0), result)
			libData.get(keyForSection).currentMonth = it.requestsNum
		})
		
		if(isBorrowing){
		//borrowing:monthFillRate
			log.debug("Runnig query for monthFillRate: " + allQuery+ " params="+sqlParams)
			sql.eachRow(allQuery, sqlParams, {
				def libData = getLibDataMap(it.getAt(0), result)
				def currentMap = libData.get(keyForSection)
				if(currentMap.currentMonth == null){
					currentMap.currentMonth = 0;
				}
				currentMap.monthFillRate = (it.requestsNum != 0?
				currentMap.currentMonth/(float)it.requestsNum:-1)
			})
		}
		//lastYearMonth
		sqlParams = dates.lastYearMonth
		log.debug("Runnig query for lastYearMonth: " + query + " params="+sqlParams)
		sql.eachRow(query, sqlParams, {
			def libData = getLibDataMap(it.getAt(0), result)
			libData.get(keyForSection).lastYearMonth = it.requestsNum
		})
		log.debug("Done for " + keyForSection)
		return result
	}
	def getUnfilledRequests(dateFrom, dateTo, libId, orderBy){
		Sql sql = new Sql(dataSource);
		def query = config.queries.borrowdirect.libraryUnfilledRequests + orderBy
		def sqlParams = [dateFrom, dateTo, libId]
		log.debug("Runnig query for unfilled requests " + query + "\nparams = " + sqlParams)
		return sql.rows(query, sqlParams)
	}
	private String getAdjustedQuery(query, libRoleColumn, additionalCondition){
		def result = query.replaceAll("\\{lib_role\\}", libRoleColumn)
		result.replaceAll("\\{add_condition\\}", additionalCondition)
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
			container.put(libId, ['borrowing':[:], 'lending':[:]])
		}
		return container.get(libId)
	}
}
