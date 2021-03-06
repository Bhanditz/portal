<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="europeana" tagdir="/WEB-INF/tags"%>

<c:set var="concept" value="${contextualItem}" />
<c:if test="${!empty concept.labels && (inContext == 1 || !concept.showInContext)}">

  <c:set var="contextElOpen"><li></c:set>
  <c:set var="contextElClose"></li></c:set>
  <c:set var="contextElContent" value=""/>
  
  <c:if test="${inContext == 1 && autoGeneratedTags == 0}">
    <c:set var="contextElOpen"><span class="contextual-body"  id="${concept.htmlId}"></c:set>
    <c:set var="contextElClose"></span></c:set>
  </c:if>

  <c:if test="${autoGeneratedTags == 0}">
  
      <c:set var="sectionContent" value=""/>
  
      <c:url var="searchUrl" value="${model.portalServer}/search.html"><c:param name="query">skos_concept:"${concept.about}"</c:param></c:url>
      <c:set var="linkMarkup">
        <a href="${searchUrl}" class="europeana"><c:forEach items="${concept.labels}" var="item" varStatus="t">${item}<c:if test="${!t.last}">, </c:if></c:forEach></a>
      </c:set>
      
      <c:choose>
        <c:when test="${!empty concept.prefLabelLang && !empty concept.altLabelLang}">
       	 
      	 <c:set var="sectionContent" value="${linkMarkup};"/>
         
         <c:forEach items="${concept.altLabelLang}" var="item" varStatus="t">
            <c:url var="labelLinkUrl" value="${model.portalServer}/search.html"><c:param name="query">what:"${item}"</c:param></c:url>
            <c:set var="labelLink">
              <a href="${labelLinkUrl}">${item}</a>
            </c:set>
        	<c:set var="sectionContent">${sectionContent} ${labelLink}<c:if test="${!t.last}">;</c:if></c:set>
        	
         </c:forEach>
        </c:when>
        <c:otherwise><c:set var="sectionContent">${sectionContent}${linkMarkup}</c:set></c:otherwise>
      </c:choose>
      
      <c:set var="contextElContent" value="${contextElContent}${sectionContent}"/>
      
  </c:if>
  <%--
      commenting this out because david didn't want bare URLs to show-up in the
      metadata section. what does this section display anyway?
      <c:if test="${!empty concept.noteLang}">
        <p>
          <c:forEach items="${concept.noteLang}" var="item" varStatus="t">
            ${item}<c:if test="${!t.last}"><br/></c:if>
          </c:forEach>
        </p>
      </c:if>
  --%>

    <c:if test="${!empty concept.notationLang && autoGeneratedTags == 1}">
        <c:set var="msg_key" value="context_concept_Notation_t" />
        <c:if test="${fn:length(concept.broaderLinks) > 1}">
          <c:set var="msg_key" value="context_concept_Notations_t" />
        </c:if>
        <c:set var="sectionContent"><spring:message code="${msg_key}" />:
	        <c:forEach  items="${concept.notationLang}"
	        		    var="item"
	        		    varStatus="t"
	        >${item}<c:if test="${!t.last}"><br/></c:if></c:forEach></c:set>
	   <c:set var="contextElContent" value="${contextElContent} ${sectionContent}"/>
    </c:if>


    <c:if test="${!empty concept.broaderLinks && autoGeneratedTags == 1}">
        <c:set var="msg_key" value="context_concept_Broader_t" />
        <c:if test="${fn:length(concept.broaderLinks) > 1}">
          <c:set var="msg_key" value="context_concept_Broaders_t" />
        </c:if>
        <c:set var="sectionContent"><li><spring:message code="${msg_key}" />:
        	<europeana:optionalMapList map="${concept.broaderLinks}" /></li></c:set>
        <c:set var="contextElContent" value="${contextElContent} ${sectionContent}"/>
    </c:if>

    <c:if test="${!empty concept.narrowerLinks && autoGeneratedTags == 1}">
        <c:set var="sectionContent" value=""/>
        
        <c:set var="msg_key" value="context_concept_Narrower_t" />
        <c:if test="${fn:length(concept.narrowerLinks) > 1}">
          <c:set var="msg_key" value="context_concept_Narrowers_t" />
        </c:if>
        
        <%--c:set var="narrowConceptContent"><europeana:optionalMapList map="${concept.narrowerLinks}" /></c:set --%>        
		<%-- c:set var="sectionContent">content length: ${fn:length(narrowConceptContent) }</c:set --%> 
        
        <c:set var="sectionContent">${sectionContent}<spring:message code="${msg_key}" />:
        <europeana:optionalMapList map="${concept.narrowerLinks}" /></c:set>
        
        <c:set var="contextElContent" value="${contextElContent} ${sectionContent}"/>
        <c:set var="sectionContent" value=""/>
        
    </c:if>

    <c:if test="${!empty concept.relatedLinks && autoGeneratedTags == 1}">
        <c:set var="msg_key" value="context_concept_Related_t" />
        <c:if test="${fn:length(concept.relatedLinks) > 1}">
          <c:set var="msg_key" value="context_concept_Relateds_t" />
        </c:if>
        <c:set var="sectionContent"><spring:message code="${msg_key}" />:
        <europeana:optionalMapList map="${concept.relatedLinks}" /></c:set>
        <c:set var="contextElContent" value="${contextElContent} ${sectionContent}"/>
    </c:if>

<%--
    <c:if test="${!empty concept.broadMatchLinks && autoGeneratedTags == 1}">
        <c:set var="msg_key" value="context_concept_broadMatch_t" />
        <c:if test="${fn:length(concept.broadMatchLinks) > 1}">
          <c:set var="msg_key" value="context_concept_broadMatches_t" />
        </c:if>
        <c:set var="sectionContent"><spring:message code="${msg_key}" />:
        <europeana:optionalMapList map="${concept.broadMatchLinks}" /></c:set>
        <c:set var="contextElContent" value="${contextElContent} ${sectionContent}"/>
    </c:if>

    <c:if test="${!empty concept.narrowMatchLinks  && autoGeneratedTags == 1}">
        <c:set var="msg_key" value="context_concept_narrowMatch_t" />
        <c:if test="${fn:length(concept.narrowMatchLinks) > 1}">
          <c:set var="msg_key" value="context_concept_narrowMatches_t" />
        </c:if>
        <c:set var="sectionContent"><spring:message code="${msg_key}" />:
        <europeana:optionalMapList map="${concept.narrowMatchLinks}" /></c:set>
        <c:set var="contextElContent" value="${contextElContent} ${sectionContent}"/>
    </c:if>
 --%>

    <c:if test="${!empty concept.relatedMatchLinks  && autoGeneratedTags == 1}">
        <c:set var="msg_key" value="context_concept_relatedMatch_t" />
        <c:if test="${fn:length(concept.relatedMatchLinks) > 1}">
          <c:set var="msg_key" value="context_concept_relatedMatches_t" />
        </c:if>
        <c:set var="sectionContent"><spring:message code="${msg_key}" />:
        <europeana:optionalMapList map="${concept.relatedMatchLinks}" /></c:set>
        <c:set var="contextElContent" value="${contextElContent} ${sectionContent}"/>
    </c:if>
    
    <c:if test="${!empty concept.exactMatchLinks  && autoGeneratedTags == 1}">
        <c:set var="msg_key" value="context_concept_exactMatch_t" />
        <c:if test="${fn:length(concept.exactMatchLinks) > 1}">
          <c:set var="msg_key" value="context_concept_exactMatches_t" />
        </c:if>
        <c:set var="sectionContent"><spring:message code="${msg_key}" />:
        <europeana:optionalMapList map="${concept.exactMatchLinks}" /></c:set>
        <c:set var="contextElContent" value="${contextElContent} ${sectionContent}"/>
    </c:if>
    
    <c:if test="${!empty concept.closeMatchLinks  && autoGeneratedTags == 1}">
        <c:set var="msg_key" value="context_concept_closeMatch_t" />
        <c:if test="${fn:length(concept.closeMatchLinks) > 1}">
          <c:set var="msg_key" value="context_concept_closeMatches_t" />
        </c:if>
        <c:set var="sectionContent"><spring:message code="${msg_key}" />:
        <europeana:optionalMapList map="${concept.closeMatchLinks}" /></c:set>
        <c:set var="contextElContent" value="${contextElContent} ${sectionContent}"/>
    </c:if>
  ${contextElOpen}${contextElContent}${contextElClose}
</c:if>
