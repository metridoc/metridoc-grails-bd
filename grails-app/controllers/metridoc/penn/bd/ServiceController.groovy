package metridoc.penn.bd

import metridoc.penn.util.DateUtil;

class ServiceController {
	def borrowDirectService
	
	private static final DATE_FORMAT = "yyyyMMdd";

	def dataDump = {
		def library_id = params.int("libraryId")
		def dateFrom = DateUtil.getDate(params.dateFrom, DATE_FORMAT)
		def dateTo = DateUtil.getDate(params.dateTo, DATE_FORMAT)
		if(dateFrom != null && dateTo != null && library_id != null){
			def library = borrowDirectService.getLibraryById(params.serviceKey, library_id)
			def libname = library != null?library.institution:""
			response.setHeader("Content-Disposition", "attachment;filename=\""+libname+"_data_dump_"+params.dateFrom+"-"+params.dateTo+".xlsx\"");
			response.setContentType("application/vnd.ms-excel")
			//we added 1 since dateTo without time will convert to 12am of the last day, thus skipping all data for
            //the last day
            borrowDirectService.dumpDataLibrary(library_id, dateFrom, dateTo + 1, response.outputStream, params.serviceKey)
		}else{
			render("Incorrect parameters");
		}
	}
}
