<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="europeana" tagdir="/WEB-INF/tags"%>

<c:set var="timespan" value="${contextualItem}" />
<c:if test="${!empty timespan.labels && (inContext == 1 || !timespan.showInContext)}">
  <c:if test="${inContext == 1}">
    <c:set var="title">
      <c:choose>
        <c:when test="${timespan.matchUrl}">${timespan.prefLabelLang[0]}</c:when>
        <c:otherwise>${value.value}</c:otherwise>
      </c:choose>
    </c:set>
    <div class="contextual-header" id="${concept.htmlId}">${title}</div>
  </c:if>

  <div<c:if test="${inContext == 1}"> class="contextual-body"</c:if>>
    <p>
      <c:url var="searchUrl" value="/search.html">
        <c:param name="query">edm_timespan:"${timespan.about}"</c:param>
      </c:url>
      <a href="${searchUrl}" id="${fn:replace(timespan.about, '/', '.')}">
        <c:forEach items="${timespan.labels}" var="item" varStatus="t">
          ${item}<c:if test="${!t.last}">, </c:if>
        </c:forEach>
      </a>

      <a href="${timespan.about}" target="_blank" class="icon-external-right"></a>

      <c:if test="${!empty timespan.prefLabelLang && !empty timespan.altLabelLang}">
        (<c:forEach items="${timespan.altLabelLang}" var="item" varStatus="t">
          ${item}<c:if test="${!t.last}">, </c:if>
        </c:forEach>)
      </c:if>
    </p>

    <c:if test="${!empty timespan.noteLang}">
      <p>
        <c:forEach items="${timespan.noteLang}" var="item" varStatus="t">
          ${item}<c:if test="${!t.last}"><br/></c:if>
        </c:forEach>
      </p>
    </c:if>

    <c:if test="${!empty timespan.isPartOfLinks}">
      <p>
        <spring:message code="context_isPartOf_t" />: 
        <europeana:optionalMapList map="${timespan.isPartOfLinks}" />
      </p>
    </c:if>

    <c:if test="${!empty timespan.beginLang || !empty timespan.endLang}">
      <p>
        <c:if test="${!empty timespan.beginLang}">
          <spring:message code="context_timespan_begin_t" />: 
          <c:forEach items="${timespan.beginLang}" var="label" varStatus="t"><c:if test="${!t.first}">, </c:if>${label}</c:forEach>
        </c:if>

        <c:if test="${!empty timespan.beginLang && !empty timespan.endLang}">
          &mdash;
        </c:if>

        <c:if test="${!empty timespan.endLang}">
          <spring:message code="context_timespan_end_t" />: 
          <c:forEach items="${timespan.endLang}" var="label" varStatus="t"><c:if test="${!t.first}">, </c:if>${label}</c:forEach>
        </c:if>
      </p>
    </c:if>
  </div>
</c:if>
