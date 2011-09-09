package metridoc.penn.bd

class Library {
	int id
	char catalogCode
	String catalogCodeDesc
	
	static mapping = {
		table 'bd_institution'
		id column: 'library_id'
		catalogCodeDesc column: 'institution'
		version false
	}
}
