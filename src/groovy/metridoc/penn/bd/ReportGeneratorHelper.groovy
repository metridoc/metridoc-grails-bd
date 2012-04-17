package metridoc.penn.bd

import java.text.SimpleDateFormat;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;

/**
*
* @author Narine
*
*/
class ReportGeneratorHelper {
	
	private static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");// HH:mm:ss");
	
	private static String STATUS_UNFILLED = "UNF";
	private static String STATUS_FILLED = "REC";
	
	static String getStatus(isUnfilled){
		return isUnfilled>0?STATUS_UNFILLED:STATUS_FILLED
	}
	
	static void createBibliographyDumpHeader(String[] headers, Sheet sh, int rowIndex, int startColumnIndex){
		Row row = sh.createRow(rowIndex);
		for (int i = 0; i < headers.length; i++) {
			Cell cell = row.createCell(startColumnIndex + i);
				cell.setCellValue(headers[i]);
		}
	}
	
	static String getStringValue(Object obj){
		if(obj instanceof Date){
			return DATE_FORMAT.format((Date)obj);
		}else{
			return obj != null ? obj.toString() : "";
		}
	}
}
