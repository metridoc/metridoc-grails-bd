<% def  val = commandBean.(fieldNamePrefix +"_month")
   def 	errorClass = commandBean.errors.hasFieldErrors(fieldNamePrefix +"_month") ? 'errorField' : "";
%>
<g:select name="${fieldNamePrefix}_month" from="${0..11}" value="${val}"
          noSelection="['-1':'Month']"  valueMessagePrefix="datafarm.month" class="${errorClass}"/>
<% 
    errorClass = commandBean.errors.hasFieldErrors(fieldNamePrefix +"_day") ? 'errorField' : "";
    val = commandBean.(fieldNamePrefix +"_day")
%>
<g:select name="${fieldNamePrefix}_day" from="${1..31}" value="${val}"
          noSelection="['-1':'Day']" class="${errorClass}"/>
<% 
   	errorClass = commandBean.errors.hasFieldErrors(fieldNamePrefix +"_year") ? 'errorField' : ""; 
   	val = commandBean.(fieldNamePrefix +"_year");
%>
<g:select name="${fieldNamePrefix}_year" from="${2000..currentYear}" value="${val}"
          noSelection="['-1':'Year']" class="${errorClass}"/>