<%@page import="metridoc.penn.bd.LibReportCommand"%>
<%@page import="metridoc.penn.bd.DataDumpMultCommand"%>
<%@page import="metridoc.penn.bd.DataDumpCommand"%>
<%@page import="metridoc.penn.bd.Library"%>
<%@ page contentType="text/html;charset=ISO-8859-1"%>
<g:set var="currentYear" value="${Calendar.getInstance().get(Calendar.YEAR)}" />
<g:set var="libraries" value="${Library.list()}" />
<g:set var="dataDumpCommand" value="${request.dataDumpCommand != null? request.dataDumpCommand:new DataDumpCommand()}" />
<g:set var="dataDumpMultCommand" value="${request.dataDumpMultCommand != null? request.dataDumpMultCommand:new DataDumpMultCommand()}" />
<g:set var="libReportCommand" value="${request.libReportCommand != null ? request.libReportCommand: new LibReportCommand()}" />
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
<meta name="layout" content="bd_main" />
<title>BorrowDirect Data Repository</title>
<script>
	function selectionChanged(val){
		var disabledVal = (val == ${LibReportCommand.UNFILLED_REQUESTS}) ? '':'disabled';
		document.lib_data_summary_form.from_month.disabled = disabledVal
		document.lib_data_summary_form.from_day.disabled = disabledVal
		document.lib_data_summary_form.from_year.disabled = disabledVal
		document.lib_data_summary_form.to_month.disabled = disabledVal
		document.lib_data_summary_form.to_day.disabled = disabledVal
		document.lib_data_summary_form.to_year.disabled = disabledVal
		document.lib_data_summary_form.sortBy.disabled = disabledVal
	}
</script>
</head>
<body>
	<div class="bd_body">
		<table width="700" height="179" border="0" cellpadding="12"
			cellspacing="0">
			<tr><td align="center" class="sectionTitle">
				DOWNLOAD MY LIBRARY'S DATA TO EXCEL
			</td></tr>
			<tr>
				<td valign="top">
				<g:hasErrors bean="${dataDumpCommand}">
  					<div class="errorMsg">
  						<g:if test="${hasErrors(bean:dataDumpCommand,field:'password','errors')}">
    						Invalid Password!
						</g:if>
						<g:else>
						All parameters are required! 
						</g:else>
  					</div>
				</g:hasErrors>
				<g:form name="data_dump_form" method="post" action="data_dump">
						<div class='formRow'>
							1.My Library Data Dump:
						</div>
						<div class='formRow'>
							Data For:
							<g:select name="library" from="${libraries}" value="${dataDumpCommand.library}" optionKey="id"
									optionValue="catalogCodeDesc" /> 
						</div>
						<div class='formRow'>
						Specify Dates: From: <g:render
									template="date_chooser"
									model="[currentYear:currentYear, fieldNamePrefix:'from', commandBean: dataDumpCommand]" />
								To: <g:render
									template="date_chooser"
									model="[currentYear:currentYear, fieldNamePrefix:'to', commandBean: dataDumpCommand]" /> </div>
								
						<div class='formRow'>
						<% def errorClass = dataDumpCommand.errors.hasFieldErrors("password") ? 'errorField' : ""; %>
							Library ID: <input type="password" name="password" class="${errorClass}" />
						</div>
						<div class='formRow'>
						<center>
								<input type="submit" name="Submit" value="Submit">
								<input type="reset" name="Reset" value="Reset">
						</center>
						</div>
					</g:form>
					<hr>
					<g:hasErrors bean="${dataDumpMultCommand}">
  					<div class="errorMsg">
						Invalid input parameters!
  					</div>
					</g:hasErrors>
					<g:form name="data_dump_mult_form" method="post" action="data_dump_mult">
					<div class='formRow'>
						2. Multiple Items Data Dump [System-Wide]: 
						</div>
						<div class='formRow'>
						Specify Dates: From: <g:render
									template="date_chooser"
									model="[currentYear:currentYear, fieldNamePrefix:'from', commandBean: dataDumpMultCommand]" />
								To: <g:render
									template="date_chooser"
									model="[currentYear:currentYear, fieldNamePrefix:'to', commandBean: dataDumpMultCommand]" /> </div>
								
						<div class='formRow'>
						<% errorClass = dataDumpMultCommand.errors.hasFieldErrors("itemTimes") ? 'errorField' : ""; %>
						Number of Times Items Borrowed
								&gt;= <input type="text" name="itemTimes" value="${fieldValue(bean:dataDumpMultCommand,field:'itemTimes')}" class="${errorClass}"  />
								
						</div>
				
					<div class='formRow'>
						<center>
							<input type="submit" name="Submit" value="Submit"> 
							<input type="reset" name="Reset" value="Reset">
						</center>
						</div>
					</g:form>
				</td>
			</tr>
			<tr><td align="center" class="sectionTitle">
				ALL-LIBRARY REPORT OPTIONS
			</td></tr>
			<tr>
    <td>
    <div class="formRow">
       1. Summary Dashboard [filled request, filled rate and turnaround times]:
        <g:link action="summary">Current Year</g:link>               
        <a href="http://datafarm.library.upenn.edu/bdez/BDhist.html">Historical</a>
        </div>
        <hr/>
        <div  class="formRow">
       2. LC Class Dashboard [filled requests grouped by LC Class | first letter]:
        <g:link action="lc_report">Current Year</g:link>
		<a href="http://datafarm.library.upenn.edu/BDLCAllLibrary10.html">Historical</a> 
    	</div>
       </td>
  </tr>
			
			<tr><td align="center" class="sectionTitle">
				MY LIBRARY REPORT OPTIONS
			</td></tr>			
  <tr>
    <td>
    <g:form name="lib_data_summary_form" method="post" action="lib_data_summary">
		<div class='formRow'>Select Your Library: 
              <g:select name="library" from="${libraries}" value="${libReportCommand.library}" optionKey="id"
									optionValue="catalogCodeDesc" /> 
				</div>				
									<hr/>
		<div class='formRow'>
              <input name="reportType" type="radio" class="radio" value="${LibReportCommand.SUMMARY}"     
<%= libReportCommand.getReportType() == LibReportCommand.SUMMARY ? "checked=\"checked\"":"" %> onclick="selectionChanged(this.value)"/> 
Summary Dashboard [filled request, filled rate and turnaround times]
      	</div>
        <hr/>       
		<div class='formRow'>
			  <input name="reportType" type="radio" class="radio" value="${LibReportCommand.LC_CLASS}" 
                <%= libReportCommand.getReportType() == LibReportCommand.LC_CLASS ? "checked=\"checked\"":"" %> onclick="selectionChanged(this.value)"/> LC Class Dashboard [filled requests grouped by LC Class | first letter]
        </div>
        <hr/>
        <div class='formRow'>
        <input name="reportType" type="radio" class="radio" value="${LibReportCommand.UNFILLED_REQUESTS}" 
              <%= libReportCommand.getReportType() == LibReportCommand.UNFILLED_REQUESTS ? "checked=\"checked\"":"" %> onclick="selectionChanged(this.value)"/>
              List My Unfilled Requests &nbsp; [Please select date range for unfilled requests.]&nbsp; Sort By:
                
                <g:select name="sortBy" from="${sortByOptions}" value="${libReportCommand.sortBy}"
          		 valueMessagePrefix="datafarm.bd.unfilled.req.sortBy" />
              </div>
 	<div class='formRow'>
						Specify Dates: From: <g:render
									template="date_chooser"
									model="[currentYear:currentYear, fieldNamePrefix:'from', commandBean: libReportCommand]" />
								To: <g:render
									template="date_chooser"
									model="[currentYear:currentYear, fieldNamePrefix:'to', commandBean: libReportCommand]" /> </div>
	 <div class='formRow'>
         <center> <input type="submit" name="Submit" value="Submit">
      <input type="reset" name="Reset" value="Reset"> </center>
  </div>
  </g:form>
    </td>
  </tr>
</table>		
	</div>
	<script>selectionChanged(${libReportCommand.getReportType()})</script>
</body>
</html>