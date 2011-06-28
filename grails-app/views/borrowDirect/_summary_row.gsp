<tr class="${ (index % 2) == 0 ? 'even' : 'odd'}">
     	<td>${libName}</td>
    	<td class="dataCell"><g:formatNumber number="${currentDataMap.currentMonth != null?currentDataMap.currentMonth:0 }" format="###,###,##0" /></td>
    	<g:if test="${!lending}">
    		<td class="dataCell"><g:formatNumber number="${currentDataMap.monthFillRate!= null?currentDataMap.monthFillRate:0 }" format="0.00" /></td> 	
		</g:if>
		<g:else>
			<td>&nbsp;</td>
		</g:else>
    	
    	<td class="dataCell"><g:formatNumber number="${currentDataMap.lastYearMonth!= null?currentDataMap.lastYearMonth:0 }" format="###,###,##0" /></td>
    	<td class="dataCell"><g:formatNumber number="${currentDataMap.currentFiscalYear!= null?currentDataMap.currentFiscalYear:0 }" format="###,###,##0" /></td>
    	<g:if test="${!lending}">
    		<td class="dataCell"><g:formatNumber number="${currentDataMap.yearFillRate!= null?currentDataMap.yearFillRate:0 }" format="0.00" /></td>
    	</g:if>
		<g:else>
			<td>&nbsp;</td>
		</g:else>
    	<td class="dataCell"><g:formatNumber number="${currentDataMap.lastFiscalYear!= null?currentDataMap.lastFiscalYear:0 }" format="###,###,##0" /></td>
    	<td class="dataCell"><g:formatNumber number="${currentDataMap.turnaroundReqRec!= null?currentDataMap.turnaroundReqRec:0 }" format="0.00" /></td>
    	<td class="dataCell"><g:formatNumber number="${currentDataMap.turnaroundReqShp!= null?currentDataMap.turnaroundReqShp:0 }" format="0.00" /></td>
    	<td class="dataCell"><g:formatNumber number="${currentDataMap.turnaroundShpRec!= null?currentDataMap.turnaroundShpRec:0 }" format="0.00" /></td>
</tr>