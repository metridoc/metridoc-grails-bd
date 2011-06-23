package metridoc.penn.bd

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.codehaus.groovy.grails.commons.ConfigurationHolder;

class BorrowDirectController {
	def borrowDirectService
    def index = { }
	def lost_password = {
		render(view:'lost_password', model:[])
	}
	def notes = {
		render(view:'notes', model:[])
	}
	
	def data_dump = { DataDumpCommand cmd ->
		if(!cmd.hasErrors()){
			def library_id = cmd.library				
			def dateFrom = getDate(cmd.from_year, cmd.from_month, cmd.from_day, 0, 0, 0)
			def dateTo = getDate(cmd.to_year, cmd.to_month, cmd.to_day, 23, 59, 59)
			response.setHeader("Content-Disposition", "attachment;filename=\"my_library_dump.xlsx\"");
			response.setContentType("application/vnd.ms-excel")
			borrowDirectService.dumpDataLibrary(library_id, dateFrom, dateTo, response.outputStream)	
			return null
		}else{
			request.dataDumpCommand = cmd
			render(view:'index', model:[])
		}
	}
	
	def data_dump_mult = { DataDumpMultCommand cmd ->
		if(!cmd.hasErrors()){
			def dateFrom = getDate(cmd.from_year, cmd.from_month, cmd.from_day, 0, 0, 0)
			def dateTo = getDate(cmd.to_year, cmd.to_month, cmd.to_day, 23, 59, 59)
						
			response.setHeader("Content-Disposition", "attachment;filename=\"multiple_items.xlsx\"");
			response.setContentType("application/vnd.ms-excel")
			borrowDirectService.dumpDataMultipleItems(dateFrom, dateTo, cmd.itemTimes, response.outputStream)
			return null
		}else{
			request.dataDumpMultCommand = cmd
			render(view:'index', model:[])
		}
	}
	
	def dashboard = { DashboardCommand cmd ->
		//TODO:impl
		println "hello"	
	}
	
	private Date getDate(year, month, day, hourOfDay, minute, second){
		def calendar = Calendar.getInstance()
		calendar.set(year, month, day, hourOfDay, minute, second)
		return new Date(calendar.getTimeInMillis())
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
		from_day(min:0, max:31)
		
		to_year(min:0)
		to_month(min:0, max:11)
		to_day(min:0, max:31)
		
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
		from_day(min:0, max:31)
		
		to_year(min:0)
		to_month(min:0, max:11)
		to_day(min:0, max:31)
	}
}

class DashboardCommand {
	int from_year = -1
	int from_month = -1
	int from_day = -1
	
	int to_year = -1
	int to_month = -1
	int to_day = -1
	
	int reportType = 0
	
	static constraints = {
		from_year(min:0)
		from_month(min:0, max:11)
		from_day(min:0, max:31)
		
		to_year(min:0)
		to_month(min:0, max:11)
		to_day(min:0, max:31)
		
		reportType(min:0, max:2)
	}
}