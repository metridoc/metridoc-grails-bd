<%@page import="org.codehaus.groovy.grails.commons.ConfigurationHolder"%>
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
<div align="center">
	<table class="mainContainer">
		<tr class="header">
			<!-- 500 -->
			<td width="70%" bgcolor="#CC0000"><g:link action=""><span>Borrow
					Direct Data Repository${ConfigurationHolder.config.datafarm.title.ext}</span></g:link>
			</td><!-- 250 -->
			<td width="30%" bgcolor="#333366" align="center"><span>
					Penn Library DATA FARM</span>
			</td>

		</tr>
    <tr>
      <td align="center" colspan="2">
       <g:if test="${showTopLinks}">
      <a href="mailto:datafarm@pobox.upenn.edu">Report a Problem</a>
      &nbsp;|&nbsp;&nbsp;
      <g:link action="notes">Notes</g:link>
      </g:if>
      <g:elseif test="${reportName != null}">
      	${reportName}
      </g:elseif>
      </td>
      
    </tr>
    <tr>
    <td colspan="2" align="center">
    	<g:layoutBody />
    </td>
    </tr>
        <tr>
          <td class="footer" colspan="2" align="center">University of Pennsylvania Library | Data Farm</td>
        </tr>
  </table>
 </div>
</body>
</html>