<%@ page contentType="text/html;charset=ISO-8859-1"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
<meta name="layout" content="bd_main" />
<title>LC report</title>
</head>
<body>
	<div class="bd_body">
	<div style="text-align: right">
	 <g:if test="${isHistorical}">
	 <a href="http://datafarm.library.upenn.edu/BDLCAllLibrary10.html" target="_lc_historical">Older Data</a>
	 <g:each var="i" in="${(minFiscalYear .. currentFiscalYear-1)}">
		 |&nbsp;<g:if test="${i != reportFiscalYear}">
		 	<g:if test="${libId}"><g:link action="lc_report" params="[fiscalYear:i, library:libId]">FY${ i }</g:link>
		 	</g:if>
		 	<g:else>
		 		<g:link action="lc_report" params="[fiscalYear:i]">FY${ i }</g:link>
		 	</g:else>
		 	</g:if>
		 <g:else>FY${ i }</g:else>
      </g:each>
      </g:if>
	</div>
	<div class="pageTitle">
	<g:if test="${libName != null}">${libName}&nbsp;:&nbsp;</g:if>LC report for fiscal year ${reportFiscalYear}</div>
	<div style='font-style: italic;'>
	Other=${callNoCountPerType.other}
	:: Fordiss=${callNoCountPerType.fordiss}
	:: Diss=${callNoCountPerType.diss}
	:: Dewey=${callNoCountPerType.dewey}
	:: Theses=${callNoCountPerType.thesis}
	:: LC=${callNoCountPerType.lc}
	</div>
		<table class="list" cellspacing="0">
			<tr>
				<th>BIN : CODE : CATEGORY</th>
				<th>Count</th>
			</tr>
			<g:each var="bucketItem" status="i" in="${bucketItems}">
			<g:set var="countPerBucket" value="${callNoCounts.get(bucketItem.getIdAsString())}" />
				<tr class="${ (i % 2) == 0 ? 'even' : 'odd'}">
					<td>${bucketItem.getIdAsString()}&nbsp;:&nbsp;${bucketItem.bucketCode}&nbsp;:&nbsp;${bucketItem.bucketCategory}</td>
					<td class="dataCell"><g:formatNumber number="${countPerBucket!=null?countPerBucket:0}" format="###,###,##0" /></td>
				</tr>
			</g:each>
		</table>
	</div>
</body>
</html>