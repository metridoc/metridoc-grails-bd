<%@page import="metridoc.penn.bd.Library"%>
<%@ page contentType="text/html;charset=ISO-8859-1" %>
<g:set var="libraries" value="${metridoc.penn.bd.Library.list()}" />
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1"/>
<meta name="layout" content="bd_main"/>
<title>Summary</title>
</head>
<body>
  <div class="body">
  <table class="list summary" cellspacing="0">
  <thead>
    <tr>
      <th class="mainColHeader" rowspan="2">Borrowing</th>
      <th class="borrow" colspan="3">Month</th>
      <th class="borrow" colspan="3">Year</th>
      <th class="borrow" colspan="3">Turnaround Time</th>
    </tr>
    <tr>
      <th class="borrow">Items</th>
      <th class="borrow">Fill Rate</th>
      <th class="borrow">Last Year</th>
      <th class="borrow">Items</th>
      <th class="borrow">Fill Rate</th>
      <th class="borrow">Last Year</th>
      <th class="borrow">Req - Rec</th>
      <th class="borrow">Req - Shp</th>
      <th class="borrow">Shp - Rec</th>
    </tr>
    </thead>
    <tbody>
     <g:set var="currentDataMap" value="${summaryData.get(-1l) != null ? summaryData.get(-1l).borrowing: [:]}" />
    <g:render template="summary_row"
		model="[currentDataMap:currentDataMap, 
				index:0, 
				libName: 'All Libraries',
				lending: false]" />
    
<g:each var="library" status="i" in="${libraries}">
    <g:set var="currentDataMap" value="${summaryData.get(library.getId().longValue()) != null ? summaryData.get(library.getId().longValue()).borrowing: [:]}" />
     <g:render template="summary_row"
		model="[currentDataMap:currentDataMap, 
				index:(i+1), 
				libName: library.catalogCodeDesc,
				lending: false]" />
</g:each>
    
    </tbody></table>
    <br>
    <table class="list summary" cellspacing="0">
  <thead>
    <tr>
      <th class="mainColHeader" rowspan="2">Lending</th>
      <th class="lend" colspan="3">Month</th>
      <th class="lend" colspan="3">Year</th>
      <th class="lend" colspan="3">Turnaround Time</th>
    </tr>
    <tr>
      <th class="lend">Items</th>
      <th class="lendVoid">Fill Rate</th>
      <th class="lend">Last Year</th>
      <th class="lend">Items</th>
      <th class="lendVoid">Fill Rate</th>

      <th class="lend">Last Year</th>
      <th class="lend">Req - Rec</th>
      <th class="lend">Req - Shp</th>
      <th class="lend">Shp - Rec</th>
    </tr>
    </thead>
    <tbody>
    <g:set var="currentDataMap" value="${summaryData.get(-1l) != null ? summaryData.get(-1l).lending: [:]}" />
    <g:render template="summary_row"
		model="[currentDataMap:currentDataMap, 
				index:0, 
				libName: 'All Libraries',
				lending: true]" />
    
<g:each var="library" status="i" in="${libraries}">
    <g:set var="currentDataMap" value="${summaryData.get(library.getId().longValue()) != null ? summaryData.get(library.getId().longValue()).lending: [:]}" />
     <g:render template="summary_row"
		model="[currentDataMap:currentDataMap, 
				index:(i+1), 
				libName: library.catalogCodeDesc,
				lending: true]" />
</g:each>
  </tbody>
</table>
  </div>
</body>
</html>