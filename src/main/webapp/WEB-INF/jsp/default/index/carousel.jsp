<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>


<%--
list would be a collection of carousel items coming from the messages.properties file based on tags with actual content,
e.g., total possible is 12 items, yet they may only fill 7 items, thus the collection should only contain the 7
        
marcoms can provide a local european.eu link within the portal or an external http:// link,
this if statement is currently used to determine if the item.anchorUrl should have /${model.portalName}/ appended
or not, something similar could be determined in the backend instead
<#if !"${url}"?contains('http://')> <#assign url>/${model.portalName}/${url}</#assign> </#if>

x = ordinal nr
${item.url} = a local/external interpretation of the notranslate_carousel-item-x-a_url
${item.anchorUrl} = notranslate_carousel-item-x-a_url
${item.anchorTitle} = carousel-item-x-a_title
${item.anchorTarget} = notranslate_carousel-item-x_a_target

${item.imgUrl} = notranslate_carousel-item-x_img_url
${item.imgAlt} = carousel-item-x_img_alt
${item.imgWidth} = notranslate_carousel-item-x_img_width
${item.imgHeight} = notranslate_carousel-item-x_img_height
--%>


<div class="row carousel-row">

	<c:if test='${!empty model.carouselItems}'>
	
	    <script type="text/javascript">
	        var carouselData = [];   
	
			<c:forEach var="item" items="${model.carouselItems}">
			
				carouselData[carouselData.length] = {
					image:			"/${model.portalName}<spring:message code='${item.imgUrl}'/>",
					title:			"<spring:message code='${item.anchorTitle}'/>",
					description:	"<spring:message code='${item.imgAlt}'/>",
					link:			"${item.url}"
				};
				
				<%-- NOT USED 
					${item.anchorTarget}
					${item.imgWidth}
					${item.imgHeight}					
				--%>
			</c:forEach>
			
		</script>
		
		<div class="carousel-wrap">

			<div class="carousel-spacer">
			
				<div id="carousel-1">
				
					<c:forEach var="item" items="${model.carouselItems}">
						<a href="${item.url}">
							<img	src		= "/${model.portalName}<spring:message code='${item.imgUrl}'/>"
									title	= "<spring:message code='${item.anchorTitle}'/>"
									alt		= "<spring:message code='${item.imgAlt}'/>"
									class	= "hidden"
							/>
						</a>
					</c:forEach>
								
				</div>
				
			</div>		
			
		</div>
		
		<div class="hide-cell-on-phones">
		
			<div class="blurb-spacer">
		
				<h2>Europe's Heritage - Collected for You!</h2>
				
				<p>
				Explore more than 20 million items from a range of Europe's leading galleries, libraries, archives and museums.
				</p>
				
				<p>
				From <a class="europeana">The Girl with the Pearl Earring</a> to <a class="europeana">Newton's Laws of Motion</a>, from the music of <a class="europeana">Mozart</a> to the TV-news of times gone by, it's all here!
				</p>
				
				<p>
				Start your Europeana adventure now and find something amazing! 				
				</p>
				
			</div>

			<div class="social-shares">
			
			
			<!-- AddThis Button BEGIN -->
 			<%--
			<div id="addthis_new" class="addthis_toolbox addthis_default_style xxaddthis_32x32_style">
			
				<a class="addthis_button_facebook" >
					<span class="icon-facebook"></span>
				</a>
				
				<a class="addthis_button_twitter" >
					<span class="icon-twitter"></span>
				</a>
				
				<a id="pinterest-share" class="addthis_button_pinterest" >
					<span class="icon-pinterest-2"></span>
				</a>

				<a class="addthis_button_preferred_4"></a>
				<a class="addthis_button_compact"></a>
				<a class="addthis_counter addthis_bubble_style"></a>
			</div>
 			 --%>
			
			

			
			
			
			

<%--
			<script type="text/javascript" src="http://s7.addthis.com/js/300/addthis_widget.js#pubid=xa-5069655a56d28488"></script>

addthis.addEventListener('addthis.ready', addthisReady);


$('#pinterest-share').removeClass('at300b');
$('#pinterest-share span').removeClass('at_PinItButton');
$('#pinterest-share span').addClass('icon-pinterest-2');
			
 --%>
			
				<span class="label">Share with your friends:</span>

				<a	title	="<spring:message code="europeana-facebook-title_t" />"
					href	="<spring:message code="europeana-facebook-url" />"
					target	="<spring:message code="europeana-facebook-target" />"
					rel		="me"
					>
					<span aria-hidden="true" class="icon-facebook"></span>
				</a>

				<a	title	="<spring:message code="europeana-twitter-title_t" />"
					href	="<spring:message code="europeana-twitter-url" />"
					target	="<spring:message code="europeana-twitter-target" />"
					rel		="me"
					>
					<span aria-hidden="true" class="icon-twitter"></span>
				</a>
				
				<a	title	="<spring:message code="europeana-pinterest-title_t" />"
					href	="<spring:message code="europeana-pinterest-url" />"
					target	="<spring:message code="europeana-pinterest-target" />"
					rel		="me"
					>
					<span aria-hidden="true" class="icon-pinterest-2"></span>
				</a>
				
				
				<span id="shares-link" aria-hidden="true">
					<span title="<spring:message code="Share_item_link_alt_t" />">
						<span aria-hidden="true" class="icon-share"></span>
					</span>
				</span>
				
				
				<a	title	="<spring:message code="europeana-pinterest-title_t" />"
					href	="<spring:message code="europeana-pinterest-url" />"
					target	="<spring:message code="europeana-pinterest-target" />"
					rel		="me"
					>
					<span aria-hidden="true" class="icon-mail"></span>
				</a>				

				
			</div>
				
		</div>
		
	</c:if>	

</div>

