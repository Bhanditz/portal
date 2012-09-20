<!-- original-context -->
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="eu" tagdir="/WEB-INF/tags" %>

<%-- <#if model.urlRefMms> --%>
<c:set var="href_attributes">class="underline external item-metadata" target="_blank" rel="nofollow rdfs:seeAlso cc:attributionURL cc:morePermissions" resource="${model.urlRef}"</c:set>

<div class="clear"><spring:message code="ViewItemAt_t" /></div>

<c:set var="urlRefId" value="urlRefIsShownAt" />
<c:if test="${model.urlRefIsShownBy}">
  <c:set var="urlRefId" value="urlRefIsShownBy" />
</c:if>

<a id="${urlRefId}" href="/${model.portalName}/redirect.html?shownAt=<eu:encode url="${model.urlRef}" />&amp;provider=${model.document.dataProvider[0]}&amp;id=${model.document.about}" ${href_attributes}>
  ${model.shownAtProvider}
</a>
<!-- original-context -->
