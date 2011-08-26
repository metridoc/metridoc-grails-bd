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
  <div class="body_bd">
  <table class="list summary" cellspacing="0">
  <thead>
    <tr>
      <th class="mainColHeader" rowspan="2">Borrowing</th>
      <th colspan="3">Month</th>
      <th colspan="3">Year</th>
      <th colspan="3">Turnaround Time</th>
    </tr>
    <tr>
      <th>Items</th>
      <th>Fill Rate</th>
      <th>Last Year</th>
      <th>Items</th>
      <th>Fill Rate</th>
      <th>Last Year</th>
      <th>Req - Rec</th>
      <th>Req - Shp</th>
      <th>Shp - Rec</th>
    </tr>
    </thead>
    <tbody>
     <g:set var="currentDataMap" value="${summaryData.get(-1l) != null ? summaryData.get(-1l).borrowing: [:]}" />
    <g:render template="summary_row"
		model="[currentDataMap:currentDataMap, 
				index:0, 
				libName: 'All Libraries',
				lending: false]" />
<g:set var="rowOffset" value="${0}"/>    
<g:each var="library" status="i" in="${libraries}">
<g:if test="${libraryId == null || libraryId != library.getId() }">
    <g:set var="currentDataMap" value="${summaryData.get(library.getId().longValue()) != null ? summaryData.get(library.getId().longValue()).borrowing: [:]}" />
     <g:render template="summary_row"
		model="[currentDataMap:currentDataMap, 
				index:(i+1-rowOffset), 
				libName: library.catalogCodeDesc,
				lending: false]" />
</g:if>
<g:else>
<g:set var="rowOffset" value="${1}"/>
</g:else>
</g:each>
    
    </tbody></table>
    <br>
    <g:if test="${ summaryData.pickupData != null}">
    	<table class="list" cellspacing="0">
    	<tr><th>Pickup Locations</th><th>Items</th></tr>
    	<g:each var="pickupLocation" status="i" in="${summaryData.pickupData}">
    	<tr class="${ (i % 2) == 0 ? 'even' : 'odd'}">
    		<td>${pickupLocation.getAt(0)}</td>
    		<td class="dataCell"><g:formatNumber number="${pickupLocation.getAt(1)}" format="###,###,##0" /></td>
    	<tr>
    	</g:each>
    	</table>
    
    </g:if>
    <br>
    <table class="list summary" cellspacing="0">
  <thead>
    <tr>
      <th class="mainColHeader" rowspan="2">Lending</th>
      <th colspan="3">Month</th>
      <th colspan="3">Year</th>
      <th colspan="3">Turnaround Time</th>
    </tr>
    <tr>
      <th>Items</th>
      <th>Fill Rate</th>
      <th>Last Year</th>
      <th>Items</th>
      <th>Fill Rate</th>

      <th>Last Year</th>
      <th>Req - Rec</th>
      <th>Req - Shp</th>
      <th>Shp - Rec</th>
    </tr>
    </thead>
    <tbody>
    <g:set var="currentDataMap" value="${summaryData.get(-1l) != null ? summaryData.get(-1l).lending: [:]}" />
    <g:render template="summary_row"
		model="[currentDataMap:currentDataMap, 
				index:0, 
				libName: 'All Libraries',
				lending: true]" />
    
<g:set var="rowOffset" value="${0}"/>    
<g:each var="library" status="i" in="${libraries}">
<g:if test="${libraryId == null || libraryId != library.getId() }">
    <g:set var="currentDataMap" value="${summaryData.get(library.getId().longValue()) != null ? summaryData.get(library.getId().longValue()).lending: [:]}" />
     <g:render template="summary_row"
		model="[currentDataMap:currentDataMap, 
				index:(i+1-rowOffset), 
				libName: library.catalogCodeDesc,
				lending: true]" />
</g:if>
<g:else>
<g:set var="rowOffset" value="${1}"/>
</g:else>
</g:each>
  </tbody>
</table>
  </div>
</body>
</html>