package metridoc.penn.bd

class Library {
	int id
	String catalogCode
	String catalogCodeDesc
	
	static mapping = {
		table 'bd_catalog_code'
		id column: 'catalog_library_id'
		version false
	}
}
