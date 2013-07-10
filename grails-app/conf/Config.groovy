
grails.config.locations = [ Queries,
	                             "file:${userHome}/.grails/BdDataSource.groovy",
								 "file:${userHome}/.grails/BdPasswords.groovy"]
	
grails.project.groupId = appName
grails.mime.file.extensions = true
grails.mime.use.accept.header = false
grails.mime.types = [ html: ['text/html','application/xhtml+xml'],
                      xml: ['text/xml', 'application/xml'],
                      text: 'text/plain',
                      js: 'text/javascript',
                      rss: 'application/rss+xml',
                      atom: 'application/atom+xml',
                      css: 'text/css',
                      csv: 'text/csv',
                      all: '*/*',
                      json: ['application/json','text/json'],
                      form: 'application/x-www-form-urlencoded',
                      multipartForm: 'multipart/form-data'/*,
					  excel: 'application/vnd.ms-excel'*/
                    ]

grails.views.default.codec = "html"//"none" // none, html, base64
grails.views.gsp.encoding = "UTF-8"
grails.converters.encoding = "UTF-8"
grails.views.gsp.sitemesh.preprocess = true
grails.scaffolding.templates.domainSuffix = 'Instance'

grails.json.legacy.builder = false
grails.enable.native2ascii = true
grails.logging.jul.usebridge = true
grails.spring.bean.packages = []

grails.exceptionresolver.params.exclude = ['password']

environments {
    production {
        grails.serverURL = "http://www.changeme.com"
    }
    development {
        grails.serverURL = "http://localhost:8080/${appName}"
    }
    test {
        grails.serverURL = "http://localhost:8080/${appName}"
    }

}
datafarm.title.ext = " - BETA"
datafarm.minFiscalYear=2011
datafarm.minCalYear=2010
log4j = {
    appenders {
        console name:'stdout', layout:pattern(conversionPattern: '%d %c{2} %m%n')
        'null' name:'stacktrace'
    }

    error  'org.codehaus.groovy.grails.web.servlet',
           'org.codehaus.groovy.grails.web.pages',
           'org.codehaus.groovy.grails.web.sitemesh',
           'org.codehaus.groovy.grails.web.mapping.filter',
           'org.codehaus.groovy.grails.web.mapping',
           'org.codehaus.groovy.grails.commons',
           'org.codehaus.groovy.grails.plugins',
           'org.codehaus.groovy.grails.orm.hibernate',
           'org.springframework',
           'org.hibernate',
           'net.sf.ehcache.hibernate'

    warn   'org.mortbay.log', 'grails.app'
	
	debug  'org.hibernate.SQL', 'grails.app', 'grails.app.services'
}
grails.validateable.packages=['metridoc.penn.bd']
