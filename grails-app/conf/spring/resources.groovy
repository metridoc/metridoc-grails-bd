import org.apache.commons.dbcp.BasicDataSource

import org.codehaus.groovy.grails.commons.ConfigurationHolder

beans = {	
	def config = ConfigurationHolder.config
	
	def connectionParams = config.dataSource.properties
	ezbDataSource(BasicDataSource) {
		driverClassName = config.ezbDataSource.driverClassName
		url = config.ezbDataSource.url
		username = config.ezbDataSource.username
		password = config.ezbDataSource.password
		maxActive = 25
		maxIdle = 15
		minIdle = 3
		initialSize = 3
		minEvictableIdleTimeMillis = 60000
		timeBetweenEvictionRunsMillis = 60000
		maxWait = 10000
		validationQuery = "select 1"
		testOnBorrow=true
	}
	
	bdDataSource(BasicDataSource) {	
		driverClassName = config.dataSource.driverClassName 	
		url = config.dataSource.url 	
		username = config.dataSource.username 	
		password = config.dataSource.password 	
		maxActive = 25
		maxIdle = 15
		minIdle = 3
		initialSize = 3
		minEvictableIdleTimeMillis = 60000
		timeBetweenEvictionRunsMillis = 60000
		maxWait = 10000
		validationQuery = "select 1"
		testOnBorrow=true
	}
}
