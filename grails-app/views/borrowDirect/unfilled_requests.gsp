<%@ page contentType="text/html;charset=ISO-8859-1"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
<meta name="layout" content="bd_main" />
<title>Unfilled Requests</title>
</head>
<body>
	<div class="body">
	<g:if test="${ reportData.size() > 0 }">
		<table class="list" cellspacing="0">
			<tr>
				<th>Borrower</th>
				<th>Title</th>
				<th>CallNo</th>
				<th>Publication Year</th>
				<th>Isbn</th>
			</tr>
			<g:each var="reportItem" status="i" in="${reportData}">
				<tr class="${ (i % 2) == 0 ? 'even' : 'odd'}">
					<td>${reportItem.borrower}</td>
					<td>${reportItem.title}</td>
					<td>${reportItem.callNo}</td>
					<td>${reportItem.publicationYear}</td>
					<td>${reportItem.isbn}</td>
				</tr>
			</g:each>
		</table>
		</g:if>
		<g:else>
			<div>There is no unfilled requests for requested date range.</div>
		</g:else>
	</div>
</body>
</html>