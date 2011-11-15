import metridoc.penn.bd.BorrowDirectService;

class UrlMappings {

	static mappings = {
		"/borrowDirect/report/$action?/$dateFrom/$dateTo/$itemTimes?"{
			controller = "service"
			serviceKey = BorrowDirectService.BD_SERVICE_KEY
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
