<%@page import="metridoc.penn.util.DateUtil"%>
<%@page import="metridoc.penn.bd.Library"%>
<%@ page contentType="text/html;charset=ISO-8859-1" %>
<g:set var="libraries" value="${metridoc.penn.bd.Library.list()}" />
<g:set var="monthsOrder" value="${summaryData.displayMonthsOrder}" />
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
      <th class="mainColHeader" rowspan="3">Borrowing</th>
      <th colspan="3" rowspan="2">Turnaround Time</th>
      <th colspan="3" rowspan="2">Year</th>
      <th colspan="${monthsOrder.size()*2 }">Filled Items Per Month</th>
    </tr>
    <tr>
      <g:each var="month" status="i" in="${monthsOrder}">
		  <th colspan="2"><g:message code="datafarm.month.${month}" /></th>
      </g:each> 
    </tr>
    <tr>
      <th>Req - Rec</th>
      <th>Req - Shp</th>
      <th>Shp - Rec</th>
      <th>Items</th>
      <th>Fill Rate</th>
      <th>Last Year</th>
     <% for(int i = 0; i < monthsOrder.size(); i++){ %>
    	<th>Item</th><th>Last Year</th>
	<% } %>
    </tr>
    </thead>
    <tbody>
     <g:set var="currentDataMap" value="${summaryData.get(-1l) != null ? summaryData.get(-1l).borrowing: [:]}" />
    <g:render template="summary_row"
		model="[currentDataMap:currentDataMap, 
				index:0, 
				libName: 'All Libraries',
				lending: false, 
				monthsOrder:monthsOrder]" />
<g:set var="rowOffset" value="${0}"/>    
<g:each var="library" status="i" in="${libraries}">
<g:if test="${libraryId == null || libraryId != library.getId() }">
    <g:set var="currentDataMap" value="${summaryData.get(library.getId().longValue()) != null ? summaryData.get(library.getId().longValue()).borrowing: [:]}" />
     <g:render template="summary_row"
		model="[currentDataMap:currentDataMap, 
				index:(i+1-rowOffset), 
				libName: library.catalogCodeDesc,
				lending: false,
				monthsOrder:monthsOrder]" />
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
    			<g:if test="${ pickupLocation.getAt(0) != null}">
    				<td>${pickupLocation.getAt(0)}</td>
    			</g:if>
    			<g:elseif test="${ i == (summaryData.pickupData.size()-1)}">
    				<td style="font-weight: bold;">Total</td>
    			</g:elseif>
    			<g:else>
    				<td>N/A</td>
    			</g:else>
    		<td class="dataCell"><g:formatNumber number="${pickupLocation.getAt(1)}" format="###,###,##0" /></td>
    	<tr>
    	</g:each>
    	</table>
    
    </g:if>
    <br>
    <table class="list summary" cellspacing="0">
  <thead>
    <tr>
      <th class="mainColHeader" rowspan="3">Lending</th>
      <th colspan="3" rowspan="2">Turnaround Time</th>
      <th colspan="3" rowspan="2">Year</th>
      <th colspan="${monthsOrder.size()*2 }">Filled Items Per Month</th>
    </tr>
    <tr>
      <g:each var="month" status="i" in="${monthsOrder}">
		  <th colspan="2"><g:message code="datafarm.month.${month}" /></th>
      </g:each> 
    </tr>
    <tr>
      <th>Req - Rec</th>
      <th>Req - Shp</th>
      <th>Shp - Rec</th>
      <th>Items</th>
      <th>Fill Rate</th>
      <th>Last Year</th>
     <% for(int i = 0; i < monthsOrder.size(); i++){ %>
    	<th>Item</th><th>Last Year</th>
	<% } %>
    </tr>
    </thead>
    <tbody>
    <g:set var="currentDataMap" value="${summaryData.get(-1l) != null ? summaryData.get(-1l).lending: [:]}" />
    <g:render template="summary_row"
		model="[currentDataMap:currentDataMap, 
				index:0, 
				libName: 'All Libraries',
				lending: true,
				monthsOrder:monthsOrder]" />
    
<g:set var="rowOffset" value="${0}"/>    
<g:each var="library" status="i" in="${libraries}">
<g:if test="${libraryId == null || libraryId != library.getId() }">
    <g:set var="currentDataMap" value="${summaryData.get(library.getId().longValue()) != null ? summaryData.get(library.getId().longValue()).lending: [:]}" />
     <g:render template="summary_row"
		model="[currentDataMap:currentDataMap, 
				index:(i+1-rowOffset), 
				libName: library.catalogCodeDesc,
				lending: true,
				monthsOrder:monthsOrder]" />
</g:if>
<g:else>
<g:set var="rowOffset" value="${1}"/>
</g:else>
</g:each>
  </tbody>
</table>
<br>
    <g:if test="${ summaryData.shelvingData != null}">
    	<table class="list" cellspacing="0">
    	<tr><th>Shelving Locations</th><th>Items</th></tr>
    	<g:each var="shelvingLocation" status="i" in="${summaryData.shelvingData}">
    	<tr class="${ (i % 2) == 0 ? 'even' : 'odd'}">
    		<g:if test="${ shelvingLocation.getAt(0) != null}">
    			<td>${shelvingLocation.getAt(0)}</td>
    		</g:if>
    		<g:elseif test="${ i == (summaryData.shelvingData.size()-1)}">
    			<td style="font-weight: bold;">Total</td>
    		</g:elseif>
    		<g:else>
    			<td>N/A</td>
    		</g:else>
    		<td class="dataCell"><g:formatNumber number="${shelvingLocation.getAt(1)}" format="###,###,##0" /></td>
    	<tr>
    	</g:each>
    	</table> 
    </g:if>
  </div>
</body>
</html>