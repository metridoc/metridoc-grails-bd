package metridoc.penn.bd

import edu.upennlib.collmanagement.BucketService;
import edu.upennlib.collmanagement.CallNoService.CallNoCounts;

import metridoc.penn.util.DateUtil;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.codehaus.groovy.grails.commons.ConfigurationHolder;

class BdEzbController {
	def borrowDirectService
	def serviceKey = BorrowDirectService.BD_SERVICE_KEY;
	
	def getIndexPageModel(){
		return [sortByOptions:LibReportCommand.sortByOptions, 
				showTopLinks:true, 
				currentFiscalYear: DateUtil.getCurrentFiscalYear(), 
				libraries: borrowDirectService.getLibraryList(serviceKey),
				serviceKey:serviceKey]
	}
	
    def index = { 
		def model = getIndexPageModel() 
		render(view:'/bd_ezb/index', model:model)
	}
	
	def lost_password = {
		render(view:'/bd_ezb/lost_password', model:[serviceKey:serviceKey])
	}
	def notes = {
		render(view:'/bd_ezb/notes', model:[serviceKey:serviceKey])
	}
	
	def data_dump = { DataDumpCommand cmd ->
		if(!cmd.hasErrors()){
			def library_id = cmd.library				
			def dateFrom = DateUtil.getDateStartOfDay(cmd.from_year, cmd.from_month, cmd.from_day)
			def dateTo = DateUtil.getDateEndOfDay(cmd.to_year, cmd.to_month, cmd.to_day)
			response.setHeader("Content-Disposition", "attachment;filename=\"my_library_dump.xlsx\"");
			response.setContentType("application/vnd.ms-excel")
			borrowDirectService.dumpDataLibrary(library_id, dateFrom, dateTo, response.outputStream, serviceKey)	
			return null
		}else{
			request.dataDumpCommand = cmd
			render(view:'/bd_ezb/index', model:getIndexPageModel())
		}
	}
	
	def data_dump_mult = { DataDumpMultCommand cmd ->
		if(!cmd.hasErrors()){
			def dateFrom = DateUtil.getDateStartOfDay(cmd.from_year, cmd.from_month, cmd.from_day)
			def dateTo = DateUtil.getDateEndOfDay(cmd.to_year, cmd.to_month, cmd.to_day)
						
			response.setHeader("Content-Disposition", "attachment;filename=\"multiple_items.xlsx\"");
			response.setContentType("application/vnd.ms-excel")
			borrowDirectService.dumpDataMultipleItems(dateFrom, dateTo, cmd.itemTimes, response.outputStream, serviceKey)
			return null
		}else{
			request.dataDumpMultCommand = cmd
			render(view:'/bd_ezb/index', model:getIndexPageModel())
		}
	}
	def summary = {
		def fiscalYear = params.fiscalYear != null ? params.int('fiscalYear'):null;
		def data = borrowDirectService.getSummaryDashboardData(null, fiscalYear, serviceKey)
		data.displayMonthsOrder = borrowDirectService.getMonthsInDisplayOrder(data.currentMonth)
		def model = [summaryData: data, 
					reportName:"Summary for fiscal year " + data.fiscalYear,
					libraries: borrowDirectService.getLibraryList(serviceKey),
					serviceKey:serviceKey]
		render(view:'/bd_ezb/summary', model:model)
	}
	
	def lc_report = {
		def fiscalYear = params.fiscalYear != null ? params.int('fiscalYear'):null;
		def data =  borrowDirectService.getRequestedCallNoCounts(null, serviceKey, fiscalYear)
		CallNoCounts counts = data.counts
		boolean isHistorical = (data.reportFiscalYear != data.currentFiscalYear)
		def model = [callNoCounts:counts!=null?counts.getCountPerBucket():[:], 
			    callNoCountPerType:counts!=null?counts.getCountPerType():[:],
				bucketItems: BucketService.getInstance().getBucketItems(), 
				//reportName:"LC report for fiscal year " + data.reportFiscalYear, 
				currentFiscalYear: data.currentFiscalYear,
				minFiscalYear: data.minFiscalYear,
				reportFiscalYear: data.reportFiscalYear,
				isHistorical:isHistorical,
				serviceKey:serviceKey]
		render(view:'/bd_ezb/lc_report', model:model)
	}
	
	def lib_data_summary = { LibReportCommand cmd ->	
		if(!cmd.hasErrors()){
			if(cmd.reportType == LibReportCommand.SUMMARY){
				def libId = cmd.library
				String libName = borrowDirectService.getLibraryById(serviceKey, libId).institution
				def fiscalYear = null;
//				if(params.Submit2 != null){
//					fiscalYear = 2011
//				}
				def data = borrowDirectService.getSummaryDashboardData(libId, fiscalYear, serviceKey)
				data.displayMonthsOrder = borrowDirectService.getMonthsInDisplayOrder(data.currentMonth)
				def model = [summaryData: data,
							reportName:libName + ": Summary for fiscal year " + data.fiscalYear,
							libraryId: libId,
							libraries: borrowDirectService.getLibraryList(serviceKey),
							serviceKey:serviceKey]
				render(view:'/bd_ezb/summary', model:model)
				
			}else if(cmd.reportType == LibReportCommand.LC_CLASS){
				def libId = cmd.library
				def data = borrowDirectService.getRequestedCallNoCounts(libId, serviceKey, null)
				CallNoCounts counts = data.counts;
				def libName = borrowDirectService.getLibraryById(serviceKey, libId).institution//Library.read(libId).getCatalogCodeDesc()
				def model = [callNoCounts:counts!=null?counts.getCountPerBucket():[:],
						callNoCountPerType:counts!=null?counts.getCountPerType():[:],
						bucketItems: BucketService.getInstance().getBucketItems(),
						libName: libName,
						reportFiscalYear: data.reportFiscalYear,
						serviceKey:serviceKey]
				render(view:'/bd_ezb/lc_report', model:model)
				
			}else{
				def dateFrom = DateUtil.getDateStartOfDay(cmd.from_year, cmd.from_month, cmd.from_day)
				def dateTo = DateUtil.getDateEndOfDay(cmd.to_year, cmd.to_month, cmd.to_day)

				def data = borrowDirectService.getUnfilledRequests(dateFrom, dateTo, cmd.library, cmd.sortBy, serviceKey)
				def libName = borrowDirectService.getLibraryById(serviceKey, cmd.library).institution//Library.read(cmd.library).getCatalogCodeDesc()
				def reportHeader = 'Unfilled requests for ' + libName + ' : ' + ReportGeneratorHelper.getStringValue(dateFrom) + ' - ' + ReportGeneratorHelper.getStringValue(dateTo) 
				render(view:'/bd_ezb/unfilled_requests', model:[reportData: data, reportName:reportHeader, serviceKey:serviceKey])
			}
			return null
		}else{
			request.libReportCommand = cmd
			render(view:'/bd_ezb/index', model:getIndexPageModel())
		}
	}
	def historical_summary = {
		def data = borrowDirectService.getHistoricalData(serviceKey)
		render(view:'/bd_ezb/historical_summary', model:[reportData: data,
			libraries: borrowDirectService.getLibraryList(serviceKey), serviceKey:serviceKey])
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
	
	//String password
	
	static constraints = {
		library(min:0)
		from_year(min:0)
		from_month(min:0, max:11)
		from_day(min:1, max:31)
		
		to_year(min:0)
		to_month(min:0, max:11)
		to_day(min:01, max:31)
		
//		password(validator: { val, obj ->
//			def realPassword = ConfigurationHolder.config.passwords[obj.library]
//			!StringUtils.isEmpty(val) && !StringUtils.isEmpty(realPassword) && DigestUtils.md5Hex(val) == realPassword
//		})
	}
}

class DataDumpMultCommand {
	int from_year = -1
	int from_month = -1
	int from_day = -1
	
	int to_year = -1
	int to_month = -1
	int to_day = -1
	
	int itemTimes = 1
	
	static constraints = {
		from_year(min:0)
		from_month(min:0, max:11)
		from_day(min:1, max:31)
		
		to_year(min:0)
		to_month(min:0, max:11)
		to_day(min:1, max:31)
		itemTimes(min:0)
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