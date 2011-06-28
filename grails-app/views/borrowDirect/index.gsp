<%@page import="metridoc.penn.bd.DashboardCommand"%>
<%@page import="metridoc.penn.bd.DataDumpMultCommand"%>
<%@page import="metridoc.penn.bd.DataDumpCommand"%>
<%@page import="metridoc.penn.bd.Library"%>
<%@ page contentType="text/html;charset=ISO-8859-1"%>
<g:set var="currentYear" value="${Calendar.getInstance().get(Calendar.YEAR)}" />
<g:set var="dataDumpCommand" value="${request.dataDumpCommand != null? request.dataDumpCommand:new DataDumpCommand()}" />
<g:set var="dataDumpMultCommand" value="${request.dataDumpMultCommand != null? request.dataDumpMultCommand:new DataDumpMultCommand()}" />
<g:set var="dashboardCommand" value="${request.dashboardCommand != null? request.dashboardCommand: new DashboardCommand()}" />
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
<meta name="layout" content="bd_main" />
<title>BorrowDirect Data Repository</title>
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
							<g:select name="library" from="${Library.list()}" value="${dataDumpCommand.library}" optionKey="id"
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
					<g:form name="form3" method="post" action="data_dump_mult">
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
        <a href="http://datafarm.library.upenn.edu/bdez/lcc/LCCBD.html">Current Year</a>
		<a href="http://datafarm.library.upenn.edu/BDLCAllLibrary10.html">Historical</a> 
    	</div>
       </td>
  </tr>
			
			<tr><td align="center" class="sectionTitle">
				MY LIBRARY REPORT OPTIONS
			</td></tr>			
  <tr>
    <td>
    <g:form name="form3" method="post" action="lib_data_summary">
		<div class='formRow'>Select Your Library: 
              <g:select name="library" from="${Library.list()}" value="" optionKey="id"
									optionValue="catalogCodeDesc" /> 
				</div>					
									<hr/>
		<div class='formRow'>
              1. Summary Dashboard [filled request, filled rate and turnaround times]:
              <input name="myRpt" type="radio" value="0" checked="checked" /> Current Year 
      	</div>
        <hr/>       
		<div class='formRow'>
			  2. LC Class Dashboard [filled requests grouped by LC Class | first letter]:                
                <input name="myRpt" type="radio" value="1" /> Current Year
        </div>
        <hr/>
        <div class='formRow'>3. List 
              <input name="myRpt" type="radio" value="2">
              My Unfilled Requests &nbsp; [Please select date range for unfilled requests.]&nbsp;Sort By:
                
                  <g:select name="sortBy" from="${sortByOptions}" value="" optionKey="id"
									optionValue="catalogCodeDesc" />
                  <!--
                  <select name="sortBy" size="1">
                    <option selected>Sort By...</option>
                    <option value = '1'>Title</option>
                    <option value = '2'>Call Number</option>
                    <option value = '3'>Imprint Date</option>
                    <option value = '4'>ISBN</option>
                  </select>
		-->
              </div>
        

 	<div class='formRow'>
						Specify Dates: From: <g:render
									template="date_chooser"
									model="[currentYear:currentYear, fieldNamePrefix:'from', commandBean: dashboardCommand]" />
								To: <g:render
									template="date_chooser"
									model="[currentYear:currentYear, fieldNamePrefix:'to', commandBean: dashboardCommand]" /> </div>
	 <div class='formRow'>
         <center> <input type="submit" name="Submit" value="Submit">
      <input type="reset" name="Reset" value="Reset"> </center>
  </div>
  </g:form>
    </td>
  </tr>
</table>		
	</div>
</body>
</html>