package metridoc.penn.bd

import groovy.sql.GroovyResultSetExtension;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
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
class LibraryDataReportGenerator {
			
	private static String[] BIBLIOGRAPHY_DUMP_FIELD_HEADERS = ["BORROWER",
		"LENDER",
		"REQUEST NUMBER",
		"PICK UP LOCATION",
		"REQUEST DATE",
		"SHIP DATE",
		"RECEIVED DATE",
		"STATUS",
		"PATRON TYPE",
		"AUTHOR",
		"TITLE",
		"PUBLISHER",
		"PUBLICATION PLACE",
		"PUBLICATION YEAR",
		"ISBN",
		"OCLC",
		"LCCN",
		"CALL NUMBER"];
	
	private Workbook workbook;
	private Sheet sheet;
	private int currentRowIndex;
	private int currentCellIndex;
	
	public LibraryDataReportGenerator(){
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
		//lender
		addCell(row, currentRowData.lender);	
		//request number
		addCell(row, currentRowData.requestNumber);
		//pickup location
		addCell(row, currentRowData.pickupLocation);
		//request date
		addCell(row, currentRowData.requestDate);
		//ship date
		addCell(row,currentRowData.shipDate);
		//received date
		addCell(row, currentRowData.processDate);
		//status
		addCell(row, ReportGeneratorHelper.getStatus(currentRowData.isUnfilled));//currentRowData.supplierCode));
		//patron type
		addCell(row, currentRowData.patronType);
		//author
		addCell(row, currentRowData.author);
		//title
		addCell(row, currentRowData.title);
		//publisher
		addCell(row, currentRowData.publisher);
		//publication place
		addCell(row, currentRowData.publicationPlace);
		//publication year
		addCell(row, currentRowData.publicationYear);
		//isbn
		addCell(row, currentRowData.isbn);
		//oclc
		addCell(row, currentRowData.oclc);
		//lccn
		addCell(row, currentRowData.lccn);
		//call number
		addCell(row, currentRowData.callNumber);
		currentRowIndex++;
	}
}
