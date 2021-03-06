<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="europeana" tagdir="/WEB-INF/tags"%>

<c:set var="agent" value="${contextualItem}" />
<c:if test="${!empty agent.labels && (inContext == 1 || !agent.showInContext)}">
  
  <c:set var="agentElOpen"><li></c:set>
  <c:set var="agentElClose"></li></c:set>
  <c:set var="agentElContent"></li></c:set>
  
  <c:if test="${inContext == 1 && autoGeneratedTags == 0}">
  	<c:set var="agentElOpen"><span class="contextual-body"  id="${concept.htmlId}"></c:set>
  	<c:set var="agentElClose"></span></c:set>
  </c:if>

  <c:if test="${autoGeneratedTags == 0}">
	  <c:url var="searchUrl" value="${model.portalServer}/search.html">
	    <c:param name="query">edm_agent:"${agent.about}"</c:param>
	  </c:url>
	  <c:set var="sectionContent"><a href="${searchUrl}" id="${fn:replace(agent.about, '/', '.')}"><c:forEach items="${agent.labels}" var="item" varStatus="t">${item}<c:if test="${!t.last}">, </c:if></c:forEach></a></c:set>
	  <c:set var="agentElContent">${agentElContent} ${sectionContent}</c:set>
  </c:if>
    
<%--
  <a href="${agent.about}" target="_blank" class="icon-external-right"></a>
 --%>
 
  <c:if test="${autoGeneratedTags == 0}"> 
    <c:if test="${!empty agent.prefLabelLang}">
      <c:set var="sectionContent">
    	  (<c:forEach items="${agent.prefLabelLang}" var="item" varStatus="t">${item}<c:if test="${!t.last}">; </c:if></c:forEach>)
      </c:set>
      <c:set var="agentElContent">${agentElContent} ${sectionContent}</c:set>
    </c:if>
  </c:if>


  <c:if test="${autoGeneratedTags == 1}">
    <c:if test="${!empty agent.altLabelLang}">
      <c:set var="sectionContent">
    	  (<c:forEach items="${agent.altLabelLang}" var="item" varStatus="t">${item}<c:if test="${!t.last}">; </c:if></c:forEach>)
      </c:set>
      <c:set var="agentElContent">${agentElContent} ${sectionContent}</c:set>
    </c:if>
  </c:if>


  <c:if test="${!empty agent.beginLang || !empty agent.endLang}">
    <c:set var="sectionContent" value=""/>
    
    <c:if test="${!empty agent.beginLang}">
      <c:set var="sectionContent">
        <spring:message code="context_agent_begin_t" />: 
        <c:forEach items="${agent.beginLang}" var="label" varStatus="t"><c:if test="${!t.first}">, </c:if>${label}</c:forEach>
      </c:set>
    </c:if>

    <c:if test="${!empty agent.beginLang && !empty agent.endLang}"><c:set var="sectionContent">${sectionContent}&mdash;</c:set></c:if>

    <c:if test="${!empty agent.endLang}">
      <c:set var="sectionContent">${sectionContent}
        <spring:message code="context_agent_end_t" />: 
        <c:forEach items="${agent.endLang}" var="label" varStatus="t"><c:if test="${!t.first}">, </c:if>${label}</c:forEach>
      </c:set>
    </c:if>
    <c:set var="agentElContent">${agentElContent} ${sectionContent}</c:set>
  </c:if>


  <c:if test="${!empty agent.noteLang}">
    <c:set var="sectionContent"><c:forEach items="${agent.noteLang}" var="item" varStatus="t"><c:if test="${!t.first}"><br/></c:if>${item}</c:forEach></c:set>
    <c:set var="agentElContent">${agentElContent} ${sectionContent}</c:set>
  </c:if>


  <c:if test="${!empty agent.edmIsRelatedToLang}">
    <c:set var="sectionContent" value=""/>
    <c:set var="msg_key" value="context_concept_Related_t" />
    <c:if test="${fn:length(concept.edmIsRelatedToLang) > 1}">
      <c:set var="msg_key" value="context_concept_Relateds_t" />
    </c:if>
    <c:set var="sectionContent"><spring:message code="${msg_key}" />: 
      <c:forEach items="${agent.edmIsRelatedToLang}" var="label" varStatus="t"><c:if test="${!empty label}"><c:if test="${!t.first}">, </c:if>${label}</c:if></c:forEach></c:set>
    <c:set var="agentElContent">${agentElContent} ${sectionContent}</c:set>
  </c:if>

  <c:if test="${!empty agent.rdaGr2DateOfBirthLang}">
    <c:set var="sectionContent"><spring:message code="context_agent_dateOfBirth_t" />: 
      <c:forEach items="${agent.rdaGr2DateOfBirthLang}" var="label" varStatus="t">
        <c:if test="${!empty label}"><c:if test="${!t.first}">, </c:if>${label}</c:if></c:forEach></c:set>
    <c:set var="agentElContent">${agentElContent} ${sectionContent}</c:set>
  </c:if>

  <c:if test="${!empty agent.rdaGr2DateOfDeathLang}">
    <c:set var="sectionContent"><spring:message code="context_agent_dateOfDeath_t" />: 
      <c:forEach items="${agent.rdaGr2DateOfDeathLang}" var="label" varStatus="t">
        <c:if test="${!empty label}"><c:if test="${!t.first}">, </c:if>${label}</c:if></c:forEach></c:set>
    <c:set var="agentElContent">${agentElContent} ${sectionContent}</c:set>
  </c:if>

<%-- 
          <c:if test="${!empty timespan.isPartOfLinks}">
            <p>
              <spring:message code="context_isPartOf_t" />: 
              <europeana:optionalMapList map="${timespan.isPartOfLinks}" />
            </p>
          </c:if>

--%>
${agentElOpen}${agentElContent}${agentElClose}

</c:if>
