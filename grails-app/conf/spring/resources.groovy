/*
 * Copyright 2013 Trustees of the University of Pennsylvania Licensed under the
 * 	Educational Community License, Version 2.0 (the "License"); you may
 * 	not use this file except in compliance with the License. You may
 * 	obtain a copy of the License at
 *
 * http://www.osedu.org/licenses/ECL-2.0
 *
 * 	Unless required by applicable law or agreed to in writing,
 * 	software distributed under the License is distributed on an "AS IS"
 * 	BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * 	or implied. See the License for the specific language governing
 * 	permissions and limitations under the License.
 */



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
		maxActive = 10
		maxIdle = 5
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
		maxActive = 10
		maxIdle = 5
		minIdle = 3
		initialSize = 3
		minEvictableIdleTimeMillis = 60000
		timeBetweenEvictionRunsMillis = 60000
		maxWait = 10000
		validationQuery = "select 1"
		testOnBorrow=true
	}
}
