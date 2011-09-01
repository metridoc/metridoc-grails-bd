<tr class="${ (index % 2) == 0 ? 'even' : 'odd'}">
     	<td>${libName}</td>
    	<td class="dataCell"><g:formatNumber number="${currentDataMap.turnaroundReqRec!= null?currentDataMap.turnaroundReqRec:0 }" format="0.00" /></td>
    	<td class="dataCell"><g:formatNumber number="${currentDataMap.turnaroundReqShp!= null?currentDataMap.turnaroundReqShp:0 }" format="0.00" /></td>
    	<td class="dataCell"><g:formatNumber number="${currentDataMap.turnaroundShpRec!= null?currentDataMap.turnaroundShpRec:0 }" format="0.00" /></td>
    	<td class="dataCell"><g:formatNumber number="${currentDataMap.currentFiscalYear != null && currentDataMap.currentFiscalYear[-1]!= null?currentDataMap.currentFiscalYear[-1]:0 }" format="###,###,##0" /></td>
    	
    	<g:if test="${!lending}">
    		<td class="dataCell">
    		<g:if test="${currentDataMap.yearFillRate != null && currentDataMap.yearFillRate > -1}">
    		<g:formatNumber number="${currentDataMap.yearFillRate}" format="0.00" />
    		</g:if>
    		<g:else>
				--
			</g:else>
    		</td>
    	</g:if>
		<g:else>
			<td>&nbsp;</td>
		</g:else>
    	<td class="dataCell"><g:formatNumber number="${currentDataMap.lastFiscalYear != null && currentDataMap.lastFiscalYear[-1] != null?currentDataMap.lastFiscalYear[-1]:0 }" format="###,###,##0" /></td>
    	 <g:each var="month" status="i" in="${monthsOrder}">
			<%-- Month in result is 1 based, whereas in Java month is 0 based --%>
    	 	<td class="dataCell"><g:formatNumber number="${currentDataMap.currentFiscalYear != null && currentDataMap.currentFiscalYear[month+1]!=null ? currentDataMap.currentFiscalYear[month+1]:0 }" format="###,###,##0" /></td>
    	 	<td class="dataCell"><g:formatNumber number="${currentDataMap.lastFiscalYear != null && currentDataMap.lastFiscalYear[month+1] != null ? currentDataMap.lastFiscalYear[month+1]:0 }" format="###,###,##0" /></td>
    	 </g:each>  	
</tr>