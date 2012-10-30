<!-- facet-items -->
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:forEach var="facet_item" items="${facet.links}">

	<c:set var="checkedValue" value='' />
	
	<c:if test="${facet_item.remove}" >
		<c:set var="checkedValue" value='checked="checked"' />
	</c:if>
	

	<c:set var="label">
		<c:choose>
			<c:when test="${!empty facet_item.title}">${facet_item.title}</c:when>
			<c:otherwise>${facet_item.value}</c:otherwise>
		</c:choose>
	</c:set>
  
	<li>
		<h4>
		
			<input type="checkbox" ${checkedValue} id="cb-${label}" name="cb-${label}"/>
				
			<a  href="/${model.portalName}/${model.pageName}?query=${fn:replace(model.query, '"', '&quot;')}${fn:replace(facet_item.url, '"', '&quot;')}${rowsParam}"
				title="${fn:escapeXml(facet_item.value)}" rel="nofollow">
				
				<label for="cb-${label}" style="display:inline"> &nbsp;${label} (${facet_item.count}) </label>
			</a>
			
		</h4>
		
		
	</li>
</c:forEach>
<!-- /facet-items -->
