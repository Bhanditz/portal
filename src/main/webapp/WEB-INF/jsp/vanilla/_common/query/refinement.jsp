<form method="get" action="${query_action}">		
	<fieldset>
		<input type="text" name="rq" id="rq" value="" class="${className}" title="<spring:message code="RefineYourSearch_t"/>">
		<input type="hidden" name="query" <c:if test="${!empty model.query}">value="${model.query}"</c:if>/>
		<input type="submit" class="submit-button" value="<spring:message code="RefineYourSearch_t"/>" />
		<c:if test="${model.debug && model.pageName == 'map.html'}">
			<input type="checkbox" id="box_search_refine"/>
			<label for="box_search_refine"><spring:message code="MapBoxedSearch_t"/></label>
		</c:if>		
		<a href="" id="close-refine-search" rel="nofollow">Hide Refine Search</a>
		<%--
			@willem jan
			2011-09-21 13.46 GMT +1
			if a qf value has " in it they should be converted to &quo; so the html passes validation
			also what are the differences between all of these qf values and are they all needed?
			existing query terms
		--%>
		
		<c:if test="${!empty model.refinements}">
			<list model.refinements as refinement>					
				<input type="hidden" name="qf" value="${refinement}"/>					
			</list>
		</c:if>
		

		
		
		<c:if test="${!empty model.providersForInclusion}">			
			<list model.providersForInclusion as providerForInclusion>								
				<input type="hidden" name="qf" value="${providerForInclusion}" />
			</list>				
		</c:if>
		
		<%--
		<c:if test="${!empty model.facetsForInclusion}">			
			<list model.facetsForInclusion as facetsForInclusion>								
				<input type="hidden" name="qf" value="${facetsForInclusion}" />						
			</list>				
		</c:if>
		 --%>
		 
		<%--
		<c:if test="${!empty model.embedded}">
			<input type="hidden" name="embedded"			value="${model.embeddedString}" />
			<input type="hidden" name="embeddedBgColor"		value="${model.embeddedBgColor}" />
			<input type="hidden" name="embeddedForeColor"	value="${model.embeddedForeColor}" />
			<input type="hidden" name="embeddedLogo"		value="${model.embeddedLogo}" />
			<input type="hidden" name="rswUserId"			value="${model.rswUserId}" />
			<input type="hidden" name="rswDefqry"			value="${model.rswDefqry}" />
			<input type="hidden" name="lang"				value="${model.locale}"/>				
		</c:if>
		--%>
	</fieldset>
</form>
