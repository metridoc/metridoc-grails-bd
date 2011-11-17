package metridoc.penn.bd

import metridoc.penn.util.DateUtil;

class ServiceController {
	def borrowDirectService
	
	private static final DATE_FORMAT = "yyyyMMdd";
    def systemWide = { 
		def dateFrom = DateUtil.getDate(params.dateFrom, DATE_FORMAT)
		def dateTo = DateUtil.getDate(params.dateTo, DATE_FORMAT)
		if(dateFrom != null && dateTo != null){
			dateTo = DateUtil.getDateEndOfDay(dateTo);
			def itemTimes = params.int("itemTimes");
			if(itemTimes == null){
				itemTimes = 1
			}
						
			response.setHeader("Content-Disposition", "attachment;filename=\"multiple_items.xlsx\"");
			response.setContentType("application/vnd.ms-excel")
			borrowDirectService.dumpDataMultipleItems(dateFrom, dateTo, itemTimes, response.outputStream, params.serviceKey)
		}else{
			render("Incorrect dates");
		}
	}
	
	def dataDump = {
		def library_id = params.int("libraryId")
		def dateFrom = DateUtil.getDate(params.dateFrom, DATE_FORMAT)
		def dateTo = DateUtil.getDate(params.dateTo, DATE_FORMAT)
		if(dateFrom != null && dateTo != null && library_id != null){
			response.setHeader("Content-Disposition", "attachment;filename=\"my_library_dump.xlsx\"");
			response.setContentType("application/vnd.ms-excel")
			borrowDirectService.dumpDataLibrary(library_id, dateFrom, dateTo, response.outputStream, params.serviceKey)
		}else{
			render("Incorrect parameters");
		}
	}
}
