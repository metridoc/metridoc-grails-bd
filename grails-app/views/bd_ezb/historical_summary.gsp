<%@ page contentType="text/html;charset=ISO-8859-1" %>
<g:set var="libraries" value="${metridoc.penn.bd.Library.list()}" />
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1"/>
<meta name="layout" content="bd_main"/>
<title>Historical Summary</title>
</head>
<body>
    <div class="body_bd">
     <div style="text-align: right;">
     <a href="http://datafarm.library.upenn.edu/bdez/BDhist.html" target="_historical">Older Data</a>
     </div>
  <table class="list summary" cellspacing="0">
  <thead>
    <tr>
      <th class="mainColHeader" rowspan="2">Borrowing</th>
      <g:each var="i" in="${(reportData.currentFiscalYear .. reportData.minFiscalYear)}">
		  <th colspan="2">
		  	<g:if test="${i == reportData.currentFiscalYear}">
		  		<g:link action="summary">${ i }</g:link>
		  	</g:if>
		  	<g:else>
		  		<g:link action="summary" params="[fiscalYear:i]">${ i }</g:link>
		  	</g:else>
		  </th>
      </g:each> 
    </tr>
    <tr>
      <g:each var="i" in="${(reportData.currentFiscalYear .. reportData.minFiscalYear)}">
		  <th>Items</th> <th>Fill Rate</th>
      </g:each>  
    </tr>
    </thead>
    <tbody>
     <g:set var="currentDataMap" value="${reportData.get(-1l) != null ? reportData.get(-1l).borrowing: [:]}" />
    <g:render template="historical_summary_row"
		model="[currentDataMap:currentDataMap, 
				index:0, 
				libName: 'All Libraries',
				lending: false, 
				minFiscalYear:reportData.minFiscalYear,
				currentFiscalYear:reportData.currentFiscalYear]" /> 
<g:each var="library" status="i" in="${libraries}">
   <g:set var="currentDataMap" value="${reportData.get(library.getId().longValue()) != null ? reportData.get(library.getId().longValue()).borrowing: [:]}" />
     <g:render template="historical_summary_row"
		model="[currentDataMap:currentDataMap, 
				index:(i+1), 
				libName: library.catalogCodeDesc,
				lending: false,
				minFiscalYear:reportData.minFiscalYear,
				currentFiscalYear:reportData.currentFiscalYear]" />
</g:each>
    </tbody></table>
    <br>
     <table class="list summary" cellspacing="0">
  <thead>
    <tr>
      <th class="mainColHeader" rowspan="2">Lending</th>
      <g:each var="i" in="${(reportData.currentFiscalYear .. reportData.minFiscalYear)}">
		  <th colspan="2">
		  	<g:if test="${i == reportData.currentFiscalYear}">
		  		<g:link action="summary">${ i }</g:link>
		  	</g:if>
		  	<g:else>
		  		<g:link action="summary" params="[fiscalYear:i]">${ i }</g:link>
		  	</g:else>
		  </th>
      </g:each> 
    </tr>
    <tr>
      <g:each var="i" in="${(reportData.currentFiscalYear .. reportData.minFiscalYear)}">
		  <th>Items</th> <th>Fill Rate</th>
      </g:each>  
    </tr>
    </thead>
    <tbody>
     <g:set var="currentDataMap" value="${reportData.get(-1l) != null ? reportData.get(-1l).lending: [:]}" />
    <g:render template="historical_summary_row"
		model="[currentDataMap:currentDataMap, 
				index:0, 
				libName: 'All Libraries',
				lending: true, 
				minFiscalYear:reportData.minFiscalYear,
				currentFiscalYear:reportData.currentFiscalYear]" /> 
<g:each var="library" status="i" in="${libraries}">
   <g:set var="currentDataMap" value="${reportData.get(library.getId().longValue()) != null ? reportData.get(library.getId().longValue()).lending: [:]}" />
     <g:render template="historical_summary_row"
		model="[currentDataMap:currentDataMap, 
				index:(i+1), 
				libName: library.catalogCodeDesc,
				lending: true,
				minFiscalYear:reportData.minFiscalYear,
				currentFiscalYear:reportData.currentFiscalYear]" />
</g:each>
    </tbody></table>
  </div>
</body>
</html>