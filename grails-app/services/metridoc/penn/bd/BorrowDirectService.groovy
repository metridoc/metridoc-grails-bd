package metridoc.penn.bd

import groovy.sql.GroovyResultSetExtension;
import groovy.sql.Sql;

import java.sql.Connection;
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
	
	def getSummaryDashboardData(){
		Sql sql = new Sql(dataSource);
		def result = [:]
		getDataPerLibrary(sql, true, result);
		getDataPerLibrary(sql, false, result);
		log.info(result)
		return result
	}
	
	def getDataPerLibrary(sql, isBorrow, result){
		def libRoleColumn; 
		def keyForSection;
		if(isBorrow){
			libRoleColumn = config.borrowdirect.db.column.borrower
			keyForSection = 'borrowing'
		}else{
			libRoleColumn = config.borrowdirect.db.column.lender
			keyForSection = 'lending'
		}
		def currentDate = Calendar.getInstance();
		def currentYear = currentDate.get(Calendar.YEAR);
		def currentMonth = currentDate.get(Calendar.MONTH);
		def currentFiscalYear = DateUtil.getFiscalYear(currentYear, currentMonth)
		Date currentFiscalYearStart = DateUtil.getFiscalYearStartDate(currentFiscalYear)
		Date currentFiscalYearEnd = DateUtil.getFiscalYearEndDate(currentFiscalYear)
		
		def query = getQueryWithRole(config.queries.borrowdirect.countsPerLibrary, libRoleColumn)
		def filledQuery = getQueryWithRole(config.queries.borrowdirect.countsPerLibraryFilled, libRoleColumn)
		//currentFiscalYear
		def sqlParams = [currentFiscalYearStart, currentFiscalYearEnd]
		sql.eachRow(query, sqlParams, {
			def libData = getLibDataMap(it.getAt(0), result)
			libData.get(keyForSection).currentFiscalYear = it.requestsNum
		})
		//turnaround
		def turnaroundQuery = getQueryWithRole(config.queries.borrowdirect.turnaroundPerLibrary, libRoleColumn)
		sql.eachRow(turnaroundQuery, sqlParams, {
			def libData = getLibDataMap(it.getAt(0).longValue(), result)
			def currentMap = libData.get(keyForSection)
			currentMap.turnaroundReqRec = it.turnaroundReqRec
			currentMap.turnaroundReqShp = it.turnaroundReqShp
			currentMap.turnaroundShpRec = it.turnaroundShpRec
		})
	
		if(isBorrow){
			//borrowing:yearFillRate
			sql.eachRow(filledQuery, 
				sqlParams, {
				def libData = getLibDataMap(it.getAt(0), result)
				def currentMap = libData.get(keyForSection)
				currentMap.yearFillRate = (currentMap.currentFiscalYear != 0? 
				it.requestsNum /(float)currentMap.currentFiscalYear:0)
			})
		}
		//lastFiscalYear
		sqlParams = [DateUtil.getFiscalYearStartDate(currentFiscalYear - 1),
			 DateUtil.getFiscalYearEndDate(currentFiscalYear - 1)]
		sql.eachRow(query, sqlParams, {
			def libData = getLibDataMap(it.getAt(0), result)
			libData.get(keyForSection).lastFiscalYear = it.requestsNum
		})
		
		//currentMonth
		def dateFrom =  DateUtil.getDateStartOfDay(currentYear, currentMonth, 1);
									 
		def dateTo = DateUtil.getDateEndOfDay(currentYear, currentMonth, 
										 currentDate.get(Calendar.DAY_OF_MONTH));
		
		sqlParams = [dateFrom, dateTo]
		sql.eachRow(query, sqlParams, {
			def libData = getLibDataMap(it.getAt(0), result)
			libData.get(keyForSection).currentMonth = it.requestsNum
		})
		
		if(isBorrow){
		//borrowing:monthFillRate
			sql.eachRow(filledQuery, sqlParams, {
				def libData = getLibDataMap(it.getAt(0), result)
				def currentMap = libData.get(keyForSection)
				currentMap.monthFillRate = (currentMap.currentMonth != 0?
				it.requestsNum /(float)currentMap.currentMonth:0)
			})
		}
		//lastYearMonth
		dateFrom =  DateUtil.getDateStartOfDay(currentYear - 1, currentMonth, 1);
									 
		dateTo = DateUtil.getDateEndOfDay(currentYear - 1, currentMonth,
										 currentDate.get(Calendar.DAY_OF_MONTH));
		
		sqlParams = [dateFrom, dateTo]
		sql.eachRow(query, sqlParams, {
			def libData = getLibDataMap(it.getAt(0), result)
			libData.get(keyForSection).lastYearMonth = it.requestsNum
		})
		return result
	}
	
	private String getQueryWithRole(query, libRoleColumn){
		return query.replaceAll("\\{lib_role\\}", libRoleColumn)
	}
	
	private void runReport(reportGenerator, query, inputParams, outstream){
		Sql sql = new Sql(dataSource);
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
