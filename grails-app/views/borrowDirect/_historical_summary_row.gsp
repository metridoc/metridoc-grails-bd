<tr class="${ (index % 2) == 0 ? 'even' : 'odd'}">
     	<td>${libName}</td>
    	 <g:each var="i" in="${(currentFiscalYear .. minFiscalYear)}">
    	 	<td class="dataCell"><g:formatNumber number="${currentDataMap.items[i] != null ? currentDataMap.items[i]:0 }" format="###,###,##0" /></td>
    	 	<td class="dataCell">
    	 	<g:if test="${currentDataMap.fillRates[i] != null && currentDataMap.fillRates[i] > -1}">
    	 		<g:formatNumber number="${currentDataMap.fillRates[i]}" format="0.00" />
    	 	</g:if>
    	 	<g:else>--</g:else>
    	 </td>
    	 </g:each>  	
</tr>