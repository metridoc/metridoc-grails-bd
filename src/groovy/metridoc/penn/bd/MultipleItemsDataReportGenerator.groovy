package metridoc.penn.bd

import groovy.sql.GroovyResultSetExtension;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

/**
*
* @author Narine Ghochikyan
*
*/
class MultipleItemsDataReportGenerator {	
	private static String[] BIBLIOGRAPHY_DUMP_FIELD_HEADERS = [
		"BORROWER",
		"TITLE",
		"CALLNO",
		"PUBLICATION YEAR",
		"ISBN",
		"STATUS",
		"COUNT"];
	
	private Workbook workbook;
	private Sheet sheet;
	private int currentRowIndex;
	private int currentCellIndex;
	
	public MultipleItemsDataReportGenerator(){
		workbook = new SXSSFWorkbook();
		sheet = workbook.createSheet();
		ReportGeneratorHelper.createBibliographyDumpHeader(BIBLIOGRAPHY_DUMP_FIELD_HEADERS, sheet, 0, 0);
		currentRowIndex = 1;
	}
	
	def write(OutputStream out) throws IOException{
		workbook.write(out);
		out.flush();
		out.close();
	}
	def addCell(row, cellData){
		Cell cell = row.createCell(currentCellIndex);
		cell.setCellValue(ReportGeneratorHelper.getStringValue(cellData));
		currentCellIndex++;
		
	}
	def addRowData(currentRowData){
		Row row = sheet.createRow(currentRowIndex);
		currentCellIndex = 0;
		//borrower
		addCell(row, currentRowData.borrower);	
		//title
		addCell(row, currentRowData.title);	
		//call number
		addCell(row, currentRowData.callNumber);
		//publication year
		addCell(row, currentRowData.publicationYear);
		//isbn
		addCell(row, currentRowData.isbn);
		//status
		addCell(row, ReportGeneratorHelper.getStatus(currentRowData.isUnfilled));
		// itemTimes
		addCell(row, currentRowData.itemTimes);
		currentRowIndex++;
	}
}