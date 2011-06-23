<!DOCTYPE html>
<html>
<head>
<title><g:layoutTitle default="Datafarm" />
</title>
<link rel="stylesheet" href="${resource(dir:'css',file:'main.css')}" />
<link rel="shortcut icon"
	href="${resource(dir:'images',file:'favicon.ico')}" type="image/x-icon" />
<g:layoutHead />
<g:javascript library="application" />
</head>
<body>
	<div id="spinner" class="spinner" style="display: none;">
		<img src="${resource(dir:'images',file:'spinner.gif')}"
			alt="${message(code:'spinner.alt',default:'Loading...')}" />
	</div>
<body>
	<table class="mainContainer">
		<tr class="header">
			<td width="500" bgcolor="#CC0000"><g:link action=""><span>Borrow
					Direct Data Repository</span></g:link>
			</td>
			<td width="250" bgcolor="#333366" align="center"><span>
					Penn Library DATA FARM</span>
			</td>

		</tr>
    <tr>
      <td align="center" colspan="2"><a href="mailto:datafarm@pobox.upenn.edu">Report a Problem</a>
      &nbsp;|&nbsp;&nbsp;
      <g:link action="lost_password">Lost Your Password?</g:link>
      &nbsp;&nbsp;|&nbsp;&nbsp;
      <g:link action="notes">Notes</g:link>
      </td>
    </tr>
    <tr>
    <td colspan="2">
    	<g:layoutBody />
    </td>
    </tr>
        <tr>
          <td class="footer" colspan="2" align="center">University of Pennsylvania Library | Data Farm</td>
        </tr>
  </table>
</body>
</html>