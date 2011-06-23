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
			/*bl.oclc,*/
			bl.call_number as callNumber,
			bl.supplier_code as supplierCode,
			IF(lower(bl.supplier_code) = 'list exhausted', 1, 0) as isUnfilled,
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
			IF(lower(bl.supplier_code) = 'list exhausted', 1, 0) as isUnfilled,
			br.catalog_code_desc as borrower,
			count(bl.request_number) as itemTimes
			from bd_bibliography_load bl
			left join bd_catalog_code br on bl.borrower = br.catalog_library_id
			where bl.request_date between ? and ?
			group by bl.borrower, bl.call_number, bl.publication_year, bl.isbn, isUnfilled, bl.title 
			having count(bl.request_number) >= ?
		'''
	}
}