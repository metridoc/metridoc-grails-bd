package metridoc.penn.bd

import edu.upennlib.collmanagement.BucketService;
import edu.upennlib.collmanagement.CallNoService.CallNoCounts;

import metridoc.penn.util.DateUtil;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.codehaus.groovy.grails.commons.ConfigurationHolder;

class BorrowDirectController {
	def indexPageModel = [sortByOptions:LibReportCommand.sortByOptions]
	def borrowDirectService
    def index = { 
		return indexPageModel }
	def lost_password = {
		render(view:'lost_password', model:[])
	}
	def notes = {
		render(view:'notes', model:[])
	}
	
	def data_dump = { DataDumpCommand cmd ->
		if(!cmd.hasErrors()){
			def library_id = cmd.library				
			def dateFrom = DateUtil.getDateStartOfDay(cmd.from_year, cmd.from_month, cmd.from_day)
			def dateTo = DateUtil.getDateEndOfDay(cmd.to_year, cmd.to_month, cmd.to_day)
			response.setHeader("Content-Disposition", "attachment;filename=\"my_library_dump.xlsx\"");
			response.setContentType("application/vnd.ms-excel")
			borrowDirectService.dumpDataLibrary(library_id, dateFrom, dateTo, response.outputStream)	
			return null
		}else{
			request.dataDumpCommand = cmd
			render(view:'index', model:indexPageModel)
		}
	}
	
	def data_dump_mult = { DataDumpMultCommand cmd ->
		if(!cmd.hasErrors()){
			def dateFrom = DateUtil.getDateStartOfDay(cmd.from_year, cmd.from_month, cmd.from_day)
			def dateTo = DateUtil.getDateEndOfDay(cmd.to_year, cmd.to_month, cmd.to_day)
						
			response.setHeader("Content-Disposition", "attachment;filename=\"multiple_items.xlsx\"");
			response.setContentType("application/vnd.ms-excel")
			borrowDirectService.dumpDataMultipleItems(dateFrom, dateTo, cmd.itemTimes, response.outputStream)
			return null
		}else{
			request.dataDumpMultCommand = cmd
			render(view:'index', model:indexPageModel)
		}
	}
	def summary = {
		def currentDate = Calendar.getInstance();
		def currentFiscalYear = DateUtil.getFiscalYear(currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH))
		
		def model = [summaryData: borrowDirectService.getSummaryDashboardData(null), 
					reportName:"Summary for fiscal year " + currentFiscalYear]
		return model
	}
	
	def lc_report = {
		def currentDate = Calendar.getInstance();
		def currentFiscalYear = DateUtil.getFiscalYear(currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH))
		CallNoCounts counts = borrowDirectService.getRequestedCallNoCounts(null)
		return [callNoCounts:counts!=null?counts.getCountPerBucket():[:], 
			    callNoCountPerType:counts!=null?counts.getCountPerType():[:],
				bucketItems: BucketService.getInstance().getBucketItems(), 
				reportName:"LC report for fiscal year " + currentFiscalYear]
	}
	
	def lib_data_summary = { LibReportCommand cmd ->	
		if(!cmd.hasErrors()){
			if(cmd.reportType == LibReportCommand.SUMMARY){
				def libId = cmd.library
				def currentDate = Calendar.getInstance();
				def currentFiscalYear = DateUtil.getFiscalYear(currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH))
				def libName = Library.read(libId).getCatalogCodeDesc()
				def model = [summaryData: borrowDirectService.getSummaryDashboardData(libId),
							reportName:libName + ": Summary for fiscal year " + currentFiscalYear,
							libraryId: libId]
				
				render(view:'summary', model:model)
				
			}else if(cmd.reportType == LibReportCommand.LC_CLASS){
				def libId = cmd.library
				def currentDate = Calendar.getInstance();
				def currentFiscalYear = DateUtil.getFiscalYear(currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH))
				CallNoCounts counts = borrowDirectService.getRequestedCallNoCounts(libId)
				def libName = Library.read(libId).getCatalogCodeDesc()
				def model = [callNoCounts:counts!=null?counts.getCountPerBucket():[:],
						callNoCountPerType:counts!=null?counts.getCountPerType():[:],
						bucketItems: BucketService.getInstance().getBucketItems(),
						reportName:libName + " : LC report for fiscal year " + currentFiscalYear]
				render(view:'lc_report', model:model)
				
			}else{
				def dateFrom = DateUtil.getDateStartOfDay(cmd.from_year, cmd.from_month, cmd.from_day)
				def dateTo = DateUtil.getDateEndOfDay(cmd.to_year, cmd.to_month, cmd.to_day)
			
				def data = borrowDirectService.getUnfilledRequests(dateFrom, dateTo, cmd.library, cmd.sortBy)
				def reportHeader = 'Request Date Range: ' + ReportGeneratorHelper.getStringValue(dateFrom) + ' - ' + ReportGeneratorHelper.getStringValue(dateTo) 
				render(view:'unfilled_requests', model:[reportData: data, reportName:reportHeader])
			}
			return null
		}else{
			request.libReportCommand = cmd
			render(view:'index', model:indexPageModel)
		}
	}
}

class DataDumpCommand {
	int library = -1
	int from_year = -1
	int from_month = -1
	int from_day = -1
	
	int to_year = -1
	int to_month = -1
	int to_day = -1
	
	String password
	
	static constraints = {
		library(min:0)
		from_year(min:0)
		from_month(min:0, max:11)
		from_day(min:1, max:31)
		
		to_year(min:0)
		to_month(min:0, max:11)
		to_day(min:01, max:31)
		
		password(validator: { val, obj ->
			def realPassword = ConfigurationHolder.config.passwords[obj.library]
			!StringUtils.isEmpty(val) && !StringUtils.isEmpty(realPassword) && DigestUtils.md5Hex(val) == realPassword
		})
	}
}

class DataDumpMultCommand {
	int from_year = -1
	int from_month = -1
	int from_day = -1
	
	int to_year = -1
	int to_month = -1
	int to_day = -1
	
	int itemTimes = 0
	
	static constraints = {
		from_year(min:0)
		from_month(min:0, max:11)
		from_day(min:1, max:31)
		
		to_year(min:0)
		to_month(min:0, max:11)
		to_day(min:1, max:31)
	}
}

class LibReportCommand {
	public static final int SUMMARY = 0;
	public static final int LC_CLASS = 1;
	public static final int UNFILLED_REQUESTS = 2;

	static config = ConfigurationHolder.config
	public static sortByOptions = [config.borrowdirect.db.column.title,
		config.borrowdirect.db.column.callNo,
		config.borrowdirect.db.column.publicationYear,
		config.borrowdirect.db.column.isbn]
	
	int library = -1
	int from_year = -1
	int from_month = -1
	int from_day = -1
	
	int to_year = -1
	int to_month = -1
	int to_day = -1
	
	int reportType = 0
	def sortBy
	
	static constraints = {
		library(min:0)
		reportType(min:0, max:2)
		
		from_year(validator: { val, obj -> 
			return validateDateFields(val ,obj, 0, Integer.MAX_VALUE)
		})
		from_month(validator: { val, obj -> 
			return validateDateFields(val ,obj, 0, 11)
		})
		from_day(validator: { val, obj -> 
			return validateDateFields(val ,obj, 1, 31)
		})
		
		to_year(validator: { val, obj -> 
			return validateDateFields(val ,obj, 0, Integer.MAX_VALUE)
		})
		to_month(validator: { val, obj -> 
			return validateDateFields(val ,obj, 0, 11)
		})
		to_day(validator: { val, obj -> 
			return validateDateFields(val ,obj, 1, 31)
		})
		sortBy(validator: { val, obj -> 
			if(obj.reportType == LibReportCommand.UNFILLED_REQUESTS){
				return val != null && sortByOptions.contains(val)
			}
			return true
		})
	}
	
	static boolean validateDateFields(val ,obj, min, max){
		if(obj.reportType == LibReportCommand.UNFILLED_REQUESTS){
			return val >= min && val <= max
		}
		return true
	}
}