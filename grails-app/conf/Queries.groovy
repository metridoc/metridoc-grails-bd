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
			br.institution as borrower,
			lndr.institution as lender,
			min(shdl.ship_date) as shipDate
			from {table_prefix}_bibliography bl
			left join {table_prefix}_ship_date shdl on bl.request_number = shdl.request_number
			left join {table_prefix}_patron_type pt on bl.patron_type = pt.patron_type
			left join {table_prefix}_institution br on bl.borrower = br.library_id
			left join {table_prefix}_institution lndr on bl.lender = lndr.library_id
			left join {table_prefix}_call_number cn on bl.request_number = cn.request_number
			where bl.request_date between ? and ? and (bl.borrower = ? or bl.lender = ?) and NOT (bl.borrower <=> bl.lender)
			group by bl.request_number
		'''	 /*and cn.holdings_seq=1*/
		dataDumpMultipleItems = '''
		select bl.title,
			bl.publication_year as publicationYear,
			bl.isbn,
			bl.call_number as callNumber,
			cn.call_number as callNumberUnf,
			IF(bl.supplier_code = 'List Exhausted', 1, 0) as isUnfilled,
			br.institution as borrower,
			count(distinct bl.request_number) as itemTimes
			from {table_prefix}_bibliography bl
			left join {table_prefix}_institution br on bl.borrower = br.library_id
			left join {table_prefix}_call_number cn on bl.request_number = cn.request_number
			where bl.request_date between ? and ? and NOT (bl.borrower <=> bl.lender)
			group by bl.borrower, bl.call_number, bl.publication_year, bl.isbn, isUnfilled, bl.title 
			having count(distinct bl.request_number) >= ?
		''' /*and cn.holdings_seq=1*/
		
		countsPerLibrary = '''
			select IFNULL({lib_role},-1) as {lib_role}, count(*) as requestsNum from {table_prefix}_bibliography where request_date between ? and ? and NOT (borrower <=> lender) {add_condition} group by {lib_role} WITH ROLLUP;
		'''
		countsPerLibraryMonthlyFilled = '''
			select IFNULL({lib_role},-1) as {lib_role}, month(request_date), count(*) as requestsNum from {table_prefix}_bibliography where request_date between ? and ? and NOT (supplier_code <=> 'List Exhausted') and NOT (borrower <=> lender) {add_condition} group by {lib_role}, month(request_date) WITH ROLLUP;
		'''
		countsPerLibraryUnfilled = '''
			select IFNULL(borrower,-1) as borrower, count(distinct bl.request_number) as requestsNum from {table_prefix}_bibliography bl inner join {table_prefix}_print_date pd on bl.request_number = pd.request_number
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
					min(bshl.ship_date) as ship_date
			from  {table_prefix}_bibliography bl 
			left join {table_prefix}_ship_date bshl on bl.request_number = bshl.request_number 
			where request_date between ? and ? and NOT (supplier_code <=> 'List Exhausted') and NOT (bl.borrower <=> bl.lender) {add_condition}
			group by bl.request_number ) sub_data
			group by {lib_role} WITH ROLLUP
		'''
		
		requestedCallNos = '''
			select call_number from {table_prefix}_bibliography where request_date between ? and ? and call_number is not null and NOT (supplier_code <=> 'List Exhausted') and NOT (borrower <=> lender)
		'''
		
		countsPerPickupLocations = '''
			select pickup_location, count(request_number) from {table_prefix}_bibliography where request_date
			between ? and ? and borrower=? and NOT (supplier_code <=> 'List Exhausted') and NOT (borrower <=> lender) group by pickup_location WITH ROLLUP
		'''
		
		countsPerShelvingLocations = '''
			select supplier_code, count(request_number) from {table_prefix}_bibliography where request_date
			between ? and ? and lender=? and NOT (supplier_code <=> 'List Exhausted') and NOT (borrower <=> lender) group by supplier_code WITH ROLLUP
		'''
		
		libraryUnfilledRequests = '''
		select bl.request_number,
		title, 
		cn.call_number as callNo,
		publication_year as publicationYear, 
		isbn,
		GROUP_CONCAT(DISTINCT lr.institution ORDER BY pd.print_date SEPARATOR ', ') as lender
		from  {table_prefix}_bibliography bl 
		left join {table_prefix}_call_number cn on bl.request_number = cn.request_number
		left join {table_prefix}_print_date pd on bl.request_number = pd.request_number
		left join {table_prefix}_institution lr on pd.library_id = lr.library_id
		where request_date
			between ? and ? and bl.supplier_code = 'List Exhausted' and bl.borrower = ? and NOT (bl.borrower <=> bl.lender) 
			group by bl.request_number order by 
		''' /*and cn.holdings_seq=1*/
		
		historicalCountsPerLibFilled = '''
		select IFNULL({lib_role},-1) as {lib_role},
		CASE WHEN MONTH(request_date)>={fy_start_month} THEN YEAR(request_date)+1
		ELSE YEAR(request_date) END AS fiscal_year,
		count(*) as requestsNum
		from {table_prefix}_bibliography b where NOT (supplier_code <=> 'List Exhausted') and NOT (borrower <=> lender) 
		{add_condition}
		group by fiscal_year, b.{lib_role} WITH ROLLUP
		'''
		
		historicalCountsPerLibAll = '''
		select IFNULL({lib_role},-1) as {lib_role},
		CASE WHEN MONTH(request_date)>={fy_start_month} THEN YEAR(request_date)+1
		ELSE YEAR(request_date) END AS fiscal_year,
		count(*) as requestsNum
		from {table_prefix}_bibliography where NOT (borrower <=> lender) 
		{add_condition}
		group by fiscal_year, {lib_role} WITH ROLLUP
		'''
		
		historicalCountsPerLibraryUnfilled = '''
		select IFNULL(borrower,-1) as borrower, 
		CASE WHEN MONTH(request_date)>={fy_start_month} THEN YEAR(request_date)+1
		ELSE YEAR(request_date) END AS fiscal_year,
		count(distinct bl.request_number) as requestsNum 
		from {table_prefix}_bibliography bl inner join {table_prefix}_print_date pd on bl.request_number = pd.request_number
		where supplier_code = 'List Exhausted' and pd.library_id = ? and borrower != pd.library_id group by fiscal_year, borrower WITH ROLLUP;
	'''
		libraryList = '''select * from {table_prefix}_institution {add_condition} order by institution'''
		libraryById = '''select * from {table_prefix}_institution where library_id=?'''
	}
}

borrowdirect.db.column.borrower = 'borrower'
borrowdirect.db.column.lender = 'lender'

borrowdirect.db.column.title = 'title'
borrowdirect.db.column.callNo = 'callNo'
borrowdirect.db.column.publicationYear = 'publication_year'
borrowdirect.db.column.isbn = 'isbn'
