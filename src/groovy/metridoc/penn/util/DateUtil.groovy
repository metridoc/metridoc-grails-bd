package metridoc.penn.util

import java.util.Date;

class DateUtil {
	private static FY_START_MONTH = Calendar.JULY;
	
	public static Date getDate(int year, int month, int day, int hourOfDay, int minute, int second){
		def calendar = Calendar.getInstance()
		calendar.set(year, month, day, hourOfDay, minute, second)
		return calendar.getTime()//new Date(calendar.getTimeInMillis())
	}
	
	public static Date getDateStartOfDay(year, month, day){
		return DateUtil.getDate(year, month, day, 0, 0, 0)
	}
	
	public static Date getDateEndOfDay(int year, int month, int day){
		return DateUtil.getDate(year, month, day, 23, 59, 59)
	}
	
	public static int getFiscalYear(int year, int month){
		return month < FY_START_MONTH?year:year+1
	}
	public static int getLastDayOfMonth(int year, int month){
		def calendar = Calendar.getInstance()
		calendar.set(Calendar.YEAR, year)
		calendar.set(Calendar.MONTH, month)
		return calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
	}
	
	private static Date getFiscalYearStartDate(int fiscalYear){
		return getDateStartOfDay(fiscalYear - 1, FY_START_MONTH, 1)
	}
	
	private static Date getFiscalYearEndDate(int fiscalYear){
		def endMonth =  FY_START_MONTH - 1;
		return getDateEndOfDay(fiscalYear, endMonth, getLastDayOfMonth(fiscalYear, endMonth))
	}
}
