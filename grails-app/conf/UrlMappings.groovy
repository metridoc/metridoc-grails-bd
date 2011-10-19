class UrlMappings {

	static mappings = {
		"/$controller/$action?/$id?"{
			constraints {
				// apply constraints here
			}
		}
		"/"(controller:"borrowDirect", action:"index")
		"500"(view:'/error')
	}
}
