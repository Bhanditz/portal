<%-- query action --%>

<c:set var="query_action" value="/${model.portalName}/search.html"/>
<c:if test="'map.html' == ${model.pageName}">
	<c:set var="query_value" value="/${model.portalName}/map.html"/>
</c:if>
<c:if test="'timeline.html' == ${model.pageName}">
	<c:set var="query_value" value="/${model.portalName}/timeline.html"/>
</c:if>

<%-- query value --%>

<c:set var="query_value" value=""/>
<c:if test="${not empty model.query}">
	<c:set var="query_value" value="${model.query}"/>
</c:if>

<%-- form --%>
<div class="row">
	<form id="query-search" action="${query_action}" method="get">
		<fieldset>
		
			<div class="nine columns">
				<input type="text" name="query" role="search" id="query-input" title="<spring:message code='SearchTerm_t'/>" value="${query_value}" maxlength="175" placeholder="<spring:message code="query_heading_t"/>"/>
			</div>
			
			<div class="three columns">
				<input id="submit-query" type="submit" class="submit-button" value="<spring:message code='Search_t'/>"/>
			</div>
			
			<%-- map search link --%>
	
			<c:if test="${model.debug} && 'map.html' == ${model.pageName}">
				<input type="checkbox" id="box_search"/>
				<label for="box_search"><spring:message code='MapBoxedSearch_t'/></label>
			</c:if>
	
			<%-- embedded search --%>
	
			<c:if test="${model.embedded}">
				<input type="hidden" name="embedded" value="${model.embeddedString}"/>
				<input type="hidden" name="embeddedBgColor" value="${model.embeddedBgColor}"/>
				<input type="hidden" name="embeddedForeColor" value="${model.embeddedForeColor}"/>
				<input type="hidden" name="embeddedLogo" value="${model.embeddedLogo}"/>
				<input type="hidden" name="rswUserId" value="${model.rswUserId}"/>
				<input type="hidden" name="rswDefqry" value="${model.rswDefqry}"/>
				<input type="hidden" name="lang" value="${model.locale}"/>
			</c:if>
		</fieldset>
		
		<%-- additional feature links for the search box --%>
		<c:if test="${not model.embedded}">
			<%-- refine search link --%>
	 		<c:set var="refinedEnabled" value=" disabled" />
			<c:if test="${!empty model.enableRefinedSearch && model.enableRefinedSearch}">
				<c:set var="refinedEnabled" value="" />
			</c:if>
	
			<a href="" id="refine-search" class="nofollow${refinedEnabled}"><spring:message code="RefineYourSearch_t" /></a>
	
			<%-- save search link --%>
	 	
			<c:if test="${model.user} && 'search.html' == ${model.pageName}">
				<spring:message code='SaveToMyEuropeana_t'/>
				<c:if test="${model.briefBeanView}">
					<input type="text" value="${model.briefBeanView.pagination.presentationQuery.queryToSave}"/>
				</c:if>
				<c:if test="${model.query}">
					<input type="text" value="${model.query}"/>
				</c:if>
			 </c:if>
	
			<%-- help link --%>
			<a id="search-help" class="hide-on-phones" href="/${model.portalName}/usingeuropeana.html"><spring:message code='rswHelp_t'/></a>
		</c:if>
	</form>
</div>
