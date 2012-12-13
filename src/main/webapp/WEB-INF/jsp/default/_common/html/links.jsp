<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<link rel="shortcut icon" href="/${model.portalName}/favicon.ico" />
<link rel="canonical" href="${model.metaCanonicalUrl}" />
<c:if test="${model[document]}">
  <c:url var="aboutUrl" value="/record${model.document.about}.html" />
  <link rel="canonical" href="${model.document.edmLandingPage[0]}" vocab="http://schema.org/" typeof="CreativeWork" about="${aboutUrl}" property="url http://www.europeana.eu/schemas/edm/landingPage" />
</c:if>
<link rel="search" type="application/opensearchdescription+xml" href="http://api.europeana.eu/api/opensearch.xml" title="Europeana Search" />
<link rel="publisher" href="<spring:message code="europeana-google-url"/>">

<c:choose>
	<c:when test="${!empty model.debug && !model.debug}">
		<%@ include file="/WEB-INF/jsp/default/_common/html/css/production-css.jsp" %>
	</c:when>
	<c:otherwise>
		<%@ include file="/WEB-INF/jsp/default/_common/html/css/debug-css.jsp" %>
	</c:otherwise>
</c:choose>
<%-- c:if test="${!empty model.headerContent}">${model.headerContent}</c:if --%>