grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"
grails.project.dependency.resolution = {
    inherits("global")
    log "warn" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
    repositories {
        grailsPlugins()
        grailsHome()
        grailsCentral()
        mavenCentral()
    }
    dependencies {
         runtime 'mysql:mysql-connector-java:5.1.13'
		 compile 'org.apache.poi:poi-ooxml:3.8-beta3' 
    }

    plugins {
        build(":tomcat:$grailsVersion") {
            export = false
        }
        compile(":calendar:1.2.1")
        runtime(":hibernate:$grailsVersion")
    }
}
