package metridoc.penn.bd

class EZBorrowController extends BdEzbController{
	public EZBorrowController(){
		serviceKey = BorrowDirectService.EZB_SERVICE_KEY;
	}
	def index(){
		super.index();
	}
	def data_dump(DataDumpCommand cmd){
		super.data_dump(cmd)
	}
	def data_dump_mult(DataDumpMultCommand cmd){
		super.data_dump_mult(cmd)
	}
	def summary(){
		super.summary()
	}
	def lc_report(){
		super.lc_report()
	}
	def lib_data_summary(LibReportCommand cmd){
		super.lib_data_summary(cmd)
	}
	def historical_summary(){
		super.historical_summary()
	} 
}
