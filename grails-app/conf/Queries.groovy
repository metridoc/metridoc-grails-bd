queries{
	borrowdirect{
		dataDumpByLibrary = '''
			select bl.request_number as requestNumber,
			bl.pickup_location as pickupLocation,
			bl.request_date as requestDate,
			bl.process_date as processDate,
			pt.patron_type_desc as patronType,
			bl.author,
			bl.title,
			bl.publisher,
			bl.publication_place as publicationPlace,
			bl.publication_year as publicationYear,
			bl.isbn,
			bl.lccn,
			bl.oclc,
			bl.call_number as callNumber,
			bl.supplier_code as supplierCode,
			IF(bl.supplier_code = 'List Exhausted', 1, 0) as isUnfilled,
			br.catalog_code_desc as borrower,
			lndr.catalog_code_desc as lender,
			min(shdl.ship_rec_date) as shipDate
			from bd_bibliography_load bl
			left join bd_ship_rec_date_load shdl on bl.request_number = shdl.request_number
			left join bd_patron_type pt on bl.patron_type = pt.patron_type
			left join bd_catalog_code br on bl.borrower = br.catalog_library_id
			left join bd_catalog_code lndr on bl.lender = lndr.catalog_library_id
			where bl.request_date between ? and ? and (bl.borrower = ? or bl.lender = ?)
			group by bl.request_number
		'''	
		dataDumpMultipleItems = '''
		select bl.request_number as requestNumber,
			bl.pickup_location as pickupLocation,
			bl.request_date as requestDate,
			bl.process_date as processDate,
			bl.author,
			bl.title,
			bl.publication_year as publicationYear,
			bl.isbn,
			bl.lccn,
			bl.call_number as callNumber,
			IF(bl.supplier_code = 'List Exhausted', 1, 0) as isUnfilled,
			br.catalog_code_desc as borrower,
			count(bl.request_number) as itemTimes
			from bd_bibliography_load bl
			left join bd_catalog_code br on bl.borrower = br.catalog_library_id
			where bl.request_date between ? and ?
			group by bl.borrower, bl.call_number, bl.publication_year, bl.isbn, isUnfilled, bl.title 
			having count(bl.request_number) >= ?
		'''
		countsPerLibrary = '''
			select IFNULL({lib_role},-1) as {lib_role}, count(*) as requestsNum from bd_bibliography_load where request_date between ? and ? {add_condition} group by {lib_role} WITH ROLLUP;
		'''
		countsPerLibraryFilled = '''
			select IFNULL({lib_role},-1) as {lib_role}, count(*) as requestsNum from bd_bibliography_load where request_date between ? and ? and supplier_code != 'List Exhausted' {add_condition} group by {lib_role} WITH ROLLUP;
		'''
		turnaroundPerLibrary = '''
		select IFNULL({lib_role},-1) as {lib_role}, AVG( DATEDIFF(process_date, ship_date)) as turnaroundShpRec, 
		AVG(DATEDIFF(ship_date, request_date))as turnaroundReqShp, 
		AVG(DATEDIFF(process_date, request_date)) as turnaroundReqRec
		from 
			(select bl.request_number, 
					bl.{lib_role}, 
					bl.request_date, 
					bl.process_date, 
					min(bshl.ship_rec_date) as ship_date
			from  bd_bibliography_load bl 
			left join bd_ship_rec_date_load bshl on bl.request_number = bshl.request_number 
			where request_date between ? and ? {add_condition}
			group by bl.request_number ) sub_data
			group by {lib_role} WITH ROLLUP
		'''
		
		requestedCallNos = '''
			select call_number from bd_bibliography_load where request_date between ? and ? and call_number is not null
		'''
		
		countsPerPickupLocations = '''
			select IFNULL(pickup_location,"Total"), count(request_number) from bd_bibliography_load where request_date
			between ? and ? and borrower=? and pickup_location is not null group by pickup_location WITH ROLLUP
		'''
	}
}
//select borrower, count(*), AVG(DATEDIFF(process_date, request_date)) from bd_bibliography_load where request_date between '2001-01-01' and '2011-05-05' group by borrower;
//select request_number, process_date, request_date, DATEDIFF(process_date, request_date) from  bd_bibliography_load;
//select request_date, process_date, ship_date, DATEDIFF(process_date, ship_date), 
//DATEDIFF(ship_date, request_date) , DATEDIFF(process_date, request_date) from 
//(select bl.request_number, bl.request_date, bl.process_date, min(bshl.ship_rec_date) as ship_date
//		from  bd_bibliography_load bl left join bd_ship_rec_date_load bshl on bl.request_number = bshl.request_number group by bl.request_number ) a



borrowdirect.db.column.borrower = 'borrower'
borrowdirect.db.column.lender = 'lender'
