<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="europeana" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>


<div id="additional-info" class="sidebar" about="${model.document.id}">

	<h1 id="phone-object-title" class="show-on-phones" aria-hidden="true">${model.objectTitle}</h1>

<%--

	<%@ include file="/WEB-INF/jsp/default/fulldoc/content/sidebar-left/image.jsp" %>
	
--%>

	<div id="carousel-1" class="europeana-bordered">
		<script type="text/javascript">
			var carouselData = [];
			carouselData[0] = {
				image:			decodeURI("${model.thumbnailUrl}").replace(/&amp;/g, '&'),
				title:			"${model.objectTitle}"
			};
			carouselData[1] = {"image":"http://europeanastatic.eu/api/image?type=IMAGE&uri=http%3A%2F%2Fmedia1.vgregion.se%2Fvastarvet%2FVGM%2FFotobilder%2FBilder+3%2F18%2F1M16_B145142_572.jpg&size=FULL_DOC","title":"Stadsvy"},{"image":"http://europeanastatic.eu/api/image?type=IMAGE&uri=http%3A%2F%2Fmedia1.vgregion.se%2Fvastarvet%2FVGM%2FFotobilder%2FBilder+3%2F18%2F1M16_B145142_572.jpg&size=FULL_DOC?x=y","title":"StadsvyXXX"};

		</script>
	</div>
	
	
	
	<%@ include file="/WEB-INF/jsp/default/fulldoc/macros/rights.jsp" %>
	
	<%-- Original context link --%>
    <%@ include file="/WEB-INF/jsp/default/fulldoc/content/sidebar-left/original-context.jsp" %>
	<br>
	
	<div class="clear"></div>
	
	<%-- Shares link --%>
	<a href="" id="shares-link" class="icon-share action-link" title="<spring:message code="Share_item_link_alt_t" />" rel="nofollow">
		<span class="action-title"><spring:message code="Share_item_link_t" /></span>
	</a>
	
	<%-- Citation link --%>
   	<a href="" id="citation-link" class="icon-cite action-link" title="<spring:message code="AltCiteInfo_t" />" rel="nofollow">
   		<span class="action-title"><spring:message code="Cite_Button_t" /></span>
   	</a>
	
	<div id="citation">
		<c:forEach items="${model.citeStyles}" var="citeStyle">
			<div class="header">
				<div class="heading"><spring:message code="Cite_Header_t" />
					<a href="" class="close-button" title="<spring:message code="Close_Button_t" />" rel="nofollow">&nbsp;</a>
				</div>
			</div>
		
			<div id="citations">
				<div class="citation">
					${citeStyle.citeText}
				</div>
				<div class="citation">
					&lt;ref&gt;${citeStyle.citeText}&lt;/ref&gt;
				</div>
			</div>
		</c:forEach>
	</div>


	<%-- Save page to myeuropeana --%>
	<c:if test="${model.user}">
	  <a href="" id="item-save" rel="nofollow" class="block-link bold"><spring:message code="SaveToMyEuropeana_t" /></a>
	</c:if>


	<%-- Embed link --%>
   	<a href="" id="item-embed" class="icon-cite action-link" title="<spring:message code="embed_t" />" rel="nofollow">
   		<span class="action-title"><spring:message code="embed_t" /></span>
   	</a>
   	
	<%--
	<div id="embed-link-wrapper">
		<a href="${model.embedRecordUrl}" id="item-embed" class="block-link bold" target="_blank" rel="nofollow"><spring:message code="embed_t" /></a>
	</div>    
	--%>

	<%-- Add tag --%>
	<c:if test="${model.user}">
	  <form id="item-save-tag">
	    <fieldset>
	      <label for="add-tag" class="bold"><spring:message code="AddATag_t" /></label><br />
	      <input type="text" id="item-tag" maxlength="50" />
	      <input type="submit" class="submit-button" value="<spring:message code="Add_t" />" />
	    </fieldset>
	  </form>
	</c:if>


	<c:choose>
		<c:when test="${fn:contains(model.currentUrl, '&format=label')}">
			<c:set var="switchlabelLink">${fn:replace(model.currentUrl, '&format=label', '')}</c:set>
			<c:set var="switchlabelTitle">Normal format</c:set>
		</c:when>
		<c:otherwise>
			<c:set var="switchlabelLink">${model.currentUrl}&format=label</c:set>
			<c:set var="switchlabelTitle">Label format</c:set>
		</c:otherwise>
	</c:choose>

	<%-- Format labels --%>
   	<a href="${switchlabelLink}" id="format-link" class="icon-info action-link" title="${switchlabelTitle}" rel="nofollow">
   		<span class="action-title">${switchlabelTitle}</span>
   	</a>
   	
   	<div class="clear"></div>
   	
   	
</div>


