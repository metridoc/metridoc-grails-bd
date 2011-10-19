<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="java.util.Date"%>
<% Date prevDate = !StringUtils.isEmpty(commandBean.(fieldNamePrefix+"_value")) ? new Date().parse(dateFormat, commandBean.(fieldNamePrefix+"_value")):null; %>
<calendar:datePicker name="${fieldNamePrefix}" years="${minYear}, ${currentYear }"
value="${prevDate}" />