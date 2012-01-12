import metridoc.penn.bd.BorrowDirectService;

class UrlMappings {

	static mappings = {
		"/borrowDirect/reports/dataDump/$dateFrom/$dateTo/$libraryId"{
			controller = "service"
			action="dataDump"
			serviceKey = BorrowDirectService.BD_SERVICE_KEY
		}
		"/EZBorrow/reports/dataDump/$dateFrom/$dateTo/$libraryId"{
			controller = "service"
			action="dataDump"
			serviceKey = BorrowDirectService.EZB_SERVICE_KEY
		}

		"/$controller/$action?/$id?"{
			constraints {
				// apply constraints here
			}
		}
		"/"(controller:"borrowDirect", action:"index")
		"500"(view:'/error')
	}
}
