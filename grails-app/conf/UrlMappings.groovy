/*
  *Copyright 2013 Trustees of the University of Pennsylvania. Licensed under the
  *	Educational Community License, Version 2.0 (the "License"); you may
  *	not use this file except in compliance with the License. You may
  *	obtain a copy of the License at
  *
  *http://www.osedu.org/licenses/ECL-2.0
  *
  *	Unless required by applicable law or agreed to in writing,
  *	software distributed under the License is distributed on an "AS IS"
  *	BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
  *	or implied. See the License for the specific language governing
  *	permissions and limitations under the License.  */

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
