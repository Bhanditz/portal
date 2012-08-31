<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>


<!-- 

	ANDY
	CSS to clean:		feature-frame
 
 -->
 
 
<div class="six columns">
	<h4 id="collapse-header-2">
		<span class="left"><spring:message code="featured-content-title_t" /></span>
		<span class="collapse-icon"></span>
		<a class="feed-link" href="http://pinterest.europeana.eu.feed" target="_blank" title="RSS Feed"></a>
	</h4>

 
	<div class="collapse-content">
		<c:if test="${!empty model.featuredItem}">

			<div class="feed-block">
				<div class="feed-box">
					<div class="feed-cell feed-image">
						<h3 class="show-on-phones">
							<a	href=	"/${model.portalName}<spring:message code="${model.featuredItem.anchorUrl}" />"
								title=	"<spring:message code="${model.featuredItem.anchorTitle}" />"
								target=	"<spring:message code="${model.featuredItem.anchorTarget}" />">
									<spring:message code="${model.featuredItem.heading}" />
							</a>
						</h3>

							<a	href=	"/${model.portalName}<spring:message code="${model.featuredItem.anchorUrl}" />"
								title=	"<spring:message code="${model.featuredItem.anchorTitle}" />"
								target=	"<spring:message code="${model.featuredItem.anchorTarget}" />"
								class=	"image">
								<img	src=	"/${model.portalName}<spring:message code="${model.featuredItem.imgUrl}" />"
										alt=	"<spring:message code="${model.featuredItem.imgAlt}" />"/>
							</a>

					</div>
					
					<div class="feed-cell">
						<h3 class="hide-on-phones featured-text">
							<a	href=	"/${model.portalName}<spring:message code="${model.featuredItem.anchorUrl}" />"
								title=	"<spring:message code="${model.featuredItem.anchorTitle}" />"
								target=	"<spring:message code="${model.featuredItem.anchorTarget}" />">
									<spring:message code="${model.featuredItem.heading}" />
							</a>
						</h3>
						<p class="featured-text">
							<spring:message code="${model.featuredItem.p}" />
						</p>					
					</div>
							
				</div>
			</div>

		</c:if>
	</div>
</div>



<div class="six columns" id="section-featured-partner">


	<c:if test="${!empty model.featuredPartner}">
	
		<div class="row">
			<div class="twelve columns">
				<h4 id="section-header-featured-partner"><spring:message code="featured-partner-title_t" /></h4>
			</div>
		</div>

		<div class="feed-block">		
			<div class="feed-box">
				<div class="feed-cell feed-image">
					<h3 class="show-on-phones">
						<a	href=	"<spring:message	code="${model.featuredPartner.anchorUrl}" />"
							title=	"<spring:message	code="${model.featuredPartner.anchorTitle}" />"
							target=	"<spring:message	code="${model.featuredPartner.anchorTarget}" />">
								<spring:message code="${model.featuredPartner.heading}" />
						</a>
					</h3>
						<a	href=	"<spring:message code="${model.featuredPartner.anchorUrl}" />"
							title=	"<spring:message code="${model.featuredPartner.anchorTitle}" />"
							target=	"<spring:message code="${model.featuredPartner.anchorTarget}" />"
							class="image">
							<img	src=	"<spring:message	code="${model.featuredPartner.imgUrl}" />"
									alt=	"<spring:message	code="${model.featuredPartner.imgAlt}" />"/>
						</a>
				</div>
				
				<div class="feed-cell">
					<h3 class="hide-on-phones featured-text">
						<a	href=	"<spring:message code="${model.featuredPartner.anchorUrl}" />"
							title=	"<spring:message code="${model.featuredPartner.anchorTitle}" />"
							target=	"<spring:message code="${model.featuredPartner.anchorTarget}" />">
								<spring:message code="${model.featuredPartner.heading}" />
						</a>
					</h3>
					<p class="featured-text">
						<spring:message code="${model.featuredPartner.p}" />
					</p>
					
					<ul class="featured-partner-links featured-text">
						<li>						
							<a	href	="<spring:message code="notranslate_featured-partner-view_link_t" />"
								target	="<spring:message code="notranslate_featured-partner-view_target_t" />">
								<spring:message code="featured-partner-view_text_t" />
							</a>
						</li>
						<li>						

							<a	href	="<spring:message code="notranslate_featured-partner-browse_link_t" />"
								target	="<spring:message code="notranslate_featured-partner-browse_target_t" />">
								<spring:message code="featured-partner-browse_text_t" />
							</a>

						</li>						
						<li>			
							<a	href	="<spring:message code="${model.featuredPartner.visitLink}" />"
								target	="<spring:message code="notranslate_featured-partner-visit_target_t" />">
								<spring:message code="featured-partner-visit_text_t" />
							</a>
						</li>

					</ul>
					
				</div>			
			</div>
		</div>
		
	</c:if>
	
</div>

