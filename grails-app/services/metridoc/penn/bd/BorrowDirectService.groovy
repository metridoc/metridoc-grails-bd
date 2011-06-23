package metridoc.penn.bd

import groovy.sql.GroovyResultSetExtension;
import groovy.sql.Sql;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

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
	
	private void runReport(reportGenerator, query, inputParams, outstream){
		Sql sql = new Sql(dataSource);
		sql.eachRow(query, inputParams, {
			reportGenerator.addRowData(it)
		})
		reportGenerator.write(outstream);
	}	
}
