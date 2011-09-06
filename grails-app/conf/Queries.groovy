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
			cn.call_number as callNumberUnf,
			bl.supplier_code as supplierCode,
			IF(bl.supplier_code = 'List Exhausted', 1, 0) as isUnfilled,
			br.catalog_code_desc as borrower,
			lndr.catalog_code_desc as lender,
			min(shdl.ship_rec_date) as shipDate
			from {table_prefix}_bibliography_load bl
			left join {table_prefix}_ship_rec_date_load shdl on bl.request_number = shdl.request_number
			left join {table_prefix}_patron_type pt on bl.patron_type = pt.patron_type
			left join {table_prefix}_catalog_code br on bl.borrower = br.catalog_library_id
			left join {table_prefix}_catalog_code lndr on bl.lender = lndr.catalog_library_id
			left join {table_prefix}_call_number_load cn on bl.request_number = cn.request_number
			where bl.request_date between ? and ? and (bl.borrower = ? or bl.lender = ?) and NOT (bl.borrower <=> bl.lender)
			and cn.holdings_seq=1
			group by bl.request_number
		'''	
		dataDumpMultipleItems = '''
		select bl.title,
			bl.publication_year as publicationYear,
			bl.isbn,
			bl.call_number as callNumber,
			cn.call_number as callNumberUnf,
			IF(bl.supplier_code = 'List Exhausted', 1, 0) as isUnfilled,
			br.catalog_code_desc as borrower,
			count(bl.request_number) as itemTimes
			from {table_prefix}_bibliography_load bl
			left join {table_prefix}_catalog_code br on bl.borrower = br.catalog_library_id
			left join {table_prefix}_call_number_load cn on bl.request_number = cn.request_number
			where bl.request_date between ? and ? and NOT (bl.borrower <=> bl.lender)
			and cn.holdings_seq=1
			group by bl.borrower, bl.call_number, bl.publication_year, bl.isbn, isUnfilled, bl.title 
			having count(bl.request_number) >= ?
		'''
		countsPerLibrary = '''
			select IFNULL({lib_role},-1) as {lib_role}, count(*) as requestsNum from {table_prefix}_bibliography_load where request_date between ? and ? and NOT (borrower <=> lender) {add_condition} group by {lib_role} WITH ROLLUP;
		'''
		countsPerLibraryMonthlyFilled = '''
			select IFNULL({lib_role},-1) as {lib_role}, month(request_date), count(*) as requestsNum from {table_prefix}_bibliography_load where request_date between ? and ? and NOT (supplier_code <=> 'List Exhausted') and NOT (borrower <=> lender) {add_condition} group by {lib_role}, month(request_date) WITH ROLLUP;
		'''
		countsPerLibraryUnfilled = '''
			select IFNULL(borrower,-1) as borrower, count(distinct bl.request_number) as requestsNum from {table_prefix}_bibliography_load bl inner join {table_prefix}_print_date_load pd on bl.request_number = pd.request_number
			where request_date between ? and ? and supplier_code = 'List Exhausted' and pd.library_id = ? and borrower != pd.library_id group by borrower WITH ROLLUP;
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
			from  {table_prefix}_bibliography_load bl 
			left join {table_prefix}_ship_rec_date_load bshl on bl.request_number = bshl.request_number 
			where request_date between ? and ? and NOT (supplier_code <=> 'List Exhausted') and NOT (bl.borrower <=> bl.lender) {add_condition}
			group by bl.request_number ) sub_data
			group by {lib_role} WITH ROLLUP
		'''
		
		requestedCallNos = '''
			select call_number from {table_prefix}_bibliography_load where request_date between ? and ? and call_number is not null and NOT (supplier_code <=> 'List Exhausted') and NOT (borrower <=> lender)
		'''
		
		countsPerPickupLocations = '''
			select pickup_location, count(request_number) from {table_prefix}_bibliography_load where request_date
			between ? and ? and borrower=? and NOT (supplier_code <=> 'List Exhausted') and NOT (borrower <=> lender) group by pickup_location WITH ROLLUP
		'''
		
		countsPerShelvingLocations = '''
			select supplier_code, count(request_number) from {table_prefix}_bibliography_load where request_date
			between ? and ? and lender=? and NOT (supplier_code <=> 'List Exhausted') and NOT (borrower <=> lender) group by supplier_code WITH ROLLUP
		'''
		
		libraryUnfilledRequests = '''
		select bl.request_number,
		title, 
		cn.call_number as callNo,
		publication_year as publicationYear, 
		isbn,
		GROUP_CONCAT(DISTINCT lr.catalog_code_desc ORDER BY pd.print_date SEPARATOR ', ') as lender
		from  {table_prefix}_bibliography_load bl 
		left join {table_prefix}_call_number_load cn on bl.request_number = cn.request_number
		left join {table_prefix}_print_date_load pd on bl.request_number = pd.request_number
		left join {table_prefix}_catalog_code lr on pd.library_id = lr.catalog_library_id
		where request_date
			between ? and ? and bl.supplier_code = 'List Exhausted' and bl.borrower = ? and NOT (bl.borrower <=> bl.lender) 
			and cn.holdings_seq = 1 group by bl.request_number order by 
		'''
	}
}

borrowdirect.db.column.borrower = 'borrower'
borrowdirect.db.column.lender = 'lender'

borrowdirect.db.column.title = 'title'
borrowdirect.db.column.callNo = 'callNo'
borrowdirect.db.column.publicationYear = 'publication_year'
borrowdirect.db.column.isbn = 'isbn'
