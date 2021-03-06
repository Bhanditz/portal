js.utils.registerNamespace( 'eu.europeana.lightbox' );

eu.europeana.lightbox = function(){
	
	var self		= this;
	self.cmp		= null;
	self.origImgW	= null;
	self.origImgH	= null;
	self.infoW		= 250;
	self.infoH		= 30;
	self.infoHx		= 30;
	self.zoomed		= false;
	self.brdr		= 60;
	self.showingEl	= 'IMG'; /* either IMG or IFRAME */
	
	var init = function(initOb) {

		var cmp				= self.cmp	= initOb.cmp;
		var src				= initOb.src;
		var carouselData	= initOb.data;
		var onNav			= initOb.onNav;
				
		if(carouselData){
			
			// create inner navigation object
			
			var NavOb = function(){
				var self = this;
				self.index		= 0;
				var subModel	= [];	// index tracking
				var subModelMap	= {};	// index tracking
				var imgIndex	= 0;
				
				for(var i=0; i<carouselData.length; i++){
					if(carouselData[i].external && carouselData[i].external.type == "image"){
						imgIndex ++;
						subModel[subModel.length] = i;
					}
					else if(carouselData[i].external && carouselData[i].external.type == "video" 
						&&
						(
							carouselData[i].external.url.indexOf(eu.europeana.fulldoc.vimeoDetect)>-1 
							||
							carouselData[i].external.url.indexOf(eu.europeana.fulldoc.dailyMotionDetect)>-1 
						)
					){
						imgIndex ++;
						subModel[subModel.length] = i;						
					}
					else if(
						carouselData[i].external.type == 'sound'
						&&
						(
							carouselData[i].external.url.indexOf(eu.europeana.fulldoc.soundcloudDetect) > -1
							||
							carouselData[i].external.url.indexOf(eu.europeana.fulldoc.audioBooDetect)>-1
						)
					){
						imgIndex ++;
						subModel[subModel.length] = i;												
					} 
					else if(carouselData[i].external.audio_boo){
						imgIndex ++;
						subModel[subModel.length] = i;												
					} 
					subModelMap[i] = (imgIndex-1); 
				}

				
				var nav = function(direction){
					var newIndex = self.index + direction;
					if(newIndex>=subModel.length){
						newIndex = 0;
					}
					else if(newIndex < 0){
						newIndex = subModel.length-1;
					}
				
					self.index = newIndex;

					com.google.analytics.europeanaEventTrack("Europeana Portal", "Europeana Lightbox", "External (lightbox nav)");
					
					var imgUrl    = carouselData[subModel[self.index]].external.url;
					var imgRights = carouselData[subModel[self.index]].external.rights;
					switchImg(imgUrl, imgRights);
					
					if(typeof onNav != "undefined"){
						onNav(imgUrl);
					}
				}
				

				return {
					"prev" : function(){
						nav(-1);
					},
					"next" : function(){
						nav(1);
					},
					"current" : function(){
						return self.index;
					},
					"goTo" : function(i){						
						self.index = subModelMap[i];
						
						switchImg(
								carouselData[subModel[self.index]].external.url,
								carouselData[subModel[self.index]].external.rights
						);
					}
				};
			};
			self.navOb = new NavOb();
			
		}
		
		imgMeasure(src, function(w, h){
			self.origImgW = w;
			self.origImgH = h;
			cmp.find('#lightbox_image').attr('src', src);
		});
	};

	var switchImg = function(url, rights){

		var img = self.cmp.find('#lightbox_image');
		
		if(img.attr('src')==url){
			return;
		}

		self.cmp.find('.rights-item').html(rights.length > 0 ? rights : carouselData.defaultRightsVal);
		
		if(url.indexOf(eu.europeana.fulldoc.telDetect) > -1 ){
			url = url + (url.indexOf('?') > -1 ? '&' : '?') + 'hidebackbtn=true';
		}		

		
		imgMeasure(url, function(w, h){
			self.origImgW = w;
			self.origImgH = h;
			img.attr("src", url);
			layout();
		});
	};
	
	var switchTypeIfNeeded = function(){
				
		var img = self.cmp.find('#lightbox_image');
				
		if(
			(img.attr('src') 
				&&
				(
					img.attr('src').indexOf(eu.europeana.fulldoc.vimeoDetect) > -1
					||
					img.attr('src').indexOf(eu.europeana.fulldoc.dailyMotionDetect) > -1
					||
					img.attr('src').indexOf(eu.europeana.fulldoc.telDetect) > -1
				)
			)
			||
			(img.attr('src') && img.attr('src').indexOf(eu.europeana.fulldoc.soundcloudDetect) > -1)
			//(carouselData[eu.europeana.fulldoc.getCarouselIndex()].external.url.indexOf(eu.europeana.fulldoc.soundcloudDetect) > -1)
		){
			
			if(img[0].nodeName.toUpperCase() == 'IMG'){
				
				var elIframe = $('<iframe id="lightbox_image" src="' + img.attr('src') + '" scrolling="No">');
	
				img.remove();
				//elIframe.fitVids();
				
				if(img.attr('src').indexOf(eu.europeana.fulldoc.telDetect) > -1 ){
					var src = elIframe.attr('src');
					elIframe.attr('src', src + (src.indexOf('?') > -1 ? '&' : '?' ) + 'hidebackbtn=true');
					elIframe.load(function(){
						if($('#lightbox .go-fullscreen').length==0){
							elIframe.before('<div class="go-fullscreen" onclick="eu.europeana.fulldoc.lightboxOb.fsBtnClick()" style="z-index:5; position:absolute; top:0.75em; left:1em; color:white;"><img src="http://www.theeuropeanlibrary.org/tel4/img/full-scr-transparent.png" style="width:1.5em;" /><span style="position:relative; top:-0.4em; left:0.5em;">' + window.lightboxMsg[0] + '</span></div>');
							elIframe[0].contentWindow.postMessage('hide-back', '*');	// cross-domain fix							
						}
					});
						
				}
				self.cmp.find('#lightbox_info').before(elIframe);

			}
		}
		else if(img[0].nodeName.toUpperCase() == 'IMG' && img.attr('src').indexOf(eu.europeana.fulldoc.audioBooDetect) > -1 ){ 
			// switch iframes and images to a div
			var src = img.attr('src').replace('.mp3', '/embed/v2?eid=AQAAAO5kOlMCmAIA&player_theme=light&link_color=steelblue&image_option=none&show_title=true');

			var elDiv = $(''
				+ '<div id="lightbox_image" '
				+   'class="ab-player" '
				+   'data-boourl="' + src + '" '
				+   'data-boowidth="100%" '
		//		+   'data-maxheight="150" '
				+   'data-maxheight="300" '
				+   'data-iframestyle="background-color:transparent; display:block; box-shadow:0 0 1px 1px rgba(0, 0, 0, 0.15); min-width:349px; max-width:700px;" '
				+   'style="background-color:transparent; height:300px">'
				+   '<a href="' + img.attr('src').replace('.mp3', '') + '" target="_new">'
				+   '	listen to ' + carouselData[ self.navOb ? self.navOb.current : 0 ].title + ' on Audioboo'
				+   '</a>'
				+ '</div>'
			);
			
			img.remove();
			self.cmp.find('#lightbox_info').before(elDiv);
			
			(function(){
				var po = document.createElement("script");
				po.type = "text/javascript";
				po.async = true;
				po.src = "https://d15mj6e6qmt1na.cloudfront.net/cdn/embed.js";
				var s = document.getElementsByTagName("script")[0]; s.parentNode.insertBefore(po, s);
			})();
			
		}
		else{
			if(img.attr('src') && img[0].nodeName.toUpperCase() != 'IMG'){
 				var elImg = $('<img id="lightbox_image" src="' + img.attr('src') + '">');
 				img.remove();
 				self.cmp.find('#lightbox_info').before(elImg);				
 			}
 			return;
		}

		return self.cmp.find('#lightbox_image')[0].nodeName.toUpperCase();
	};
	
	var imgMeasure = function(src, callback){
		
		if(	(src.indexOf(eu.europeana.fulldoc.vimeoDetect)>-1)
			||
			src.indexOf(eu.europeana.fulldoc.soundcloudDetect) > -1
		){
			if(typeof callback != "undefined"){
				src.indexOf(eu.europeana.fulldoc.vimeoDetect)>-1 ? callback(2000, 2000) : callback(600, 280);				
			}
			return;
		}

		// sizing for iframe
		if( src.indexOf(eu.europeana.fulldoc.audioBooDetect) > -1 ){
			if(typeof callback != "undefined"){
				callback(600, 300);
			}
			return;			
		}
		else if( src.indexOf(eu.europeana.fulldoc.dailyMotionDetect) > -1 ){
			if(typeof callback != "undefined"){
				callback(600, 300);
			}
			return;
		}
		else if( src.indexOf(eu.europeana.fulldoc.telDetect) > -1 ){
			
			if(typeof callback != "undefined"){
				callback(0, 0);
			}
			return;
		}

		
		$('<div id="hidden_img" style="visibility:hidden;"><img src="' + src + '" /></div>').appendTo('body').imagesLoaded(
			function($images, $proper, $broken){
				var w = $proper.width();
				var h = $proper.height();
				$('#hidden_img').remove();
				if(typeof callback != "undefined"){
					callback(w, h);				
				}
			}
		);
	};
	
	var exitFullscreen = function(closing){
		$('.overlaid-content').removeClass('fullscreen');
		$('.go-fullscreen img').attr('src', 'http://www.theeuropeanlibrary.org/tel4/img/full-scr-transparent.png');
		$('.go-fullscreen span').html(window.lightboxMsg[0]);
		
		$('#lightbox>.close').show();
		if(!closing){
			layout();			
		}
	};

	var enterFullscreen = function(){
		var el = $('iframe#lightbox_image');
		el.removeAttr('style');
		el.css('width',  '100%');
		el.css('height', '100%');
		
		$('.go-fullscreen img').attr('src', 'http://www.theeuropeanlibrary.org/tel4/img/exit-full-scr-transparent.png');
		$('.go-fullscreen span').html(window.lightboxMsg[1]);
		
		$('.overlaid-content').addClass('fullscreen');
		$('#lightbox>.close').hide();
	};

	var layout = function(){
		

		if($('.overlaid-content').hasClass('fullscreen')){
			return;
		}
		
		
		/*	Strategy if self.showingEl = IMG:
		 * 
		 * 	1) get layout by taking 2 hypothetical aspect ratios:
		 * 		- info underneath
		 * 		- info on the right
		 * 	and compare with window aspect ratio (the closest will show the image at its largest).
		 * 
		 * 	2) recursively reduce img dimensions until img + info fit available space
		 * 
		 *  Otherwise:
		 *  
		 *  - info always goes underneath
		 * 
		 */ 
		self.showingEl	= switchTypeIfNeeded();
		
		var	img		= self.cmp.find('#lightbox_image'),
			info	= self.cmp.find('#lightbox_info');
		
		var	imgW	= self.origImgW,
			imgH	= self.origImgH,
			infoW	= self.infoW,
			infoH	= self.infoH,
			infoHx	= self.infoHx;
			brdr	= self.brdr;
			
		
		if(imgW==0 && imgH==0){ // = use all the space available
			imgW = $(window).width()  + (brdr);
			imgH = $(window).height() + (brdr);			
		}
		
		var	aspectWin	= ($(window).width()-brdr) / ($(window).height()-brdr);
		var	aspectUnder	= imgW / (imgH + infoH);
		var	aspectRight	= (imgW + infoW) / imgH;
		var	aspectImg	= imgW / imgH;
		var	aspectZoom	= imgW / imgH;
		
		
		var aspectClosest	= null;
		$.each([aspectUnder, aspectRight], function(){
		  if (aspectClosest == null || Math.abs(this - aspectWin) < Math.abs(aspectClosest - aspectWin)) {
			  aspectClosest = this;
		  }
		});
		
		var isRight			= (self.showingEl == 'IFRAME') ? false : aspectClosest == aspectRight;
		var availableHeight =	parseInt(
			$(window).height()
			- ( self.zoomed ? 0 : (isRight ? 0 : infoH) )
		) - brdr;

		var availableWidth =	parseInt(
			$(window).width()
			- ( self.zoomed ? 0 : (isRight ? infoW : 0) )
		) - brdr;
		
		var testDimensions = function(w, h, rec){
			
			var projectedHeight	= (w/aspectImg);
			var projectedWidth	= (h*aspectImg);
			
			if(rec > 30){
				// Avoid infinite recursion - stop at 30
				return [projectedWidth, projectedHeight];
			}
			if(projectedHeight > availableHeight || projectedWidth > availableWidth){
				return testDimensions(	w*0.9, h*0.9, rec+1 );
			}
			else{
				return [projectedWidth, projectedHeight];
			}
		};
		
		var dim = testDimensions(imgW, imgH, 0);
		
		
		var availableHeight =	parseInt($(window).height()) - brdr;
		var availableWidth =	parseInt($(window).width()) - brdr;
		
		
		var isZoomable = false;
		var dimZ = testDimensions(imgW, imgH, 0);
		if( self.showingEl != 'IFRAME' && (dim[0]*dim[1]) < (dimZ[0]*dimZ[1]) ){
			isZoomable = true;
		}
		
		// set img
		
		
		img.css('width',		dim[0] + "px");
		img.css('min-width',	dim[0] + "px");	/*	additional rule needed for ie8	*/
		
		
		if(self.showingEl == 'IFRAME'){
			img.css('height',		(dim[1]-70) + "px");
		}
		
		$("#lightbox").css("visibility", "visible");
		
		// set meta
		
		$(".hide_show_meta").hide();

		if(self.zoomed){
			info.css('display', 'none');
		}
		else{
			if(isRight){
				info.css('display', 'inline-block');
				info.css('width',	infoW + 'px');
				info.css('height',	img.height() + 'px');
			}
			else{
				info.css('display',	'block');
				info.css('width',	dim[0] + 'px');
				info.css('height',	infoH + 'px');
			}	
		}
		
		// align zoom & nav
		if(self.navOb){
			self.cmp.find('#nav-next').css('right', self.zoomed ? 0 : isRight ? self.infoW + 'px' : '0');
		}
		
		var zoom = self.cmp.find('#zoomIn');
		if(isZoomable){
			zoom.show();		
			zoom.css('right', isRight ? self.infoW + 'px' : '0');
		}
		else if(!self.zoomed){
			zoom.hide();
		}
		else if(self.zoomed){
			zoom.css('right', 0);
		}
		
		
		// meta show / hide
		
		if(!self.zoomed){
			var both	= self.cmp.find('.hide_show_meta');
			
			var showBoth = function(){
				both.show();
				self.cmp.find('ul').addClass('collapsed');				
			};

			var hideBoth = function(){
				both.hide();
				self.cmp.find('ul').removeClass('collapsed');				
			};
			
			
			if(isRight){
				hideBoth();
				info.css('overflow-y', 'auto');
			}
			else{
				showBoth();
				
				var show	= self.cmp.find('.showMeta');
				var hide	= self.cmp.find('.hideMeta');

				show.hide();
				hide.hide();

				// if info is hidden...
				
				
				/* chrome +3 for border */
				if(
					( info.find('ul')[0].offsetHeight > info.height()+3 ) || self.showingEl == 'IFRAME' 
				){
					
					var showShow = function(){
						show.show();
						hide.hide();
						info.css('overflow-y', 'hidden');
					};
					
					var showHide = function(){
						hide.show();
						show.hide();
						info.css('overflow-y', 'auto');
					};

					
					info.height() > self.infoH ? showHide() : showShow();

					hide.unbind('click').click(function(){
						info.css('height', self.infoH + 'px');
						showShow();
					});
					
					hide.unbind('touchstart').bind('touchstart', function(e){
						$(e.target).click();
					    e.stopPropagation();
					    e.preventDefault();
					});

					
					

					// calculate the height of 1em
					var em = $('<div class="test" style="height:1em;">&nbsp;</div>').appendTo(info);
					em = em.height();
					info.find('.test').remove();
					
					
					// get natural height of list
					
					var list = info.find('ul'); 
					list.css('height', 'auto');
					
					var availHeight		= $(window).height() - (img.height() + brdr); 
					var naturalHeight	= list.height() + (2 * em);

					list.css('height', '100%');
					
					// BUG: when info under, expanded, scrolled then page resized (narrower), list is still "scrolled"
					//var ref = info.css('overflow-y');
					//info.css('overflow-y', 'auto');
					//info.scrollTop();
					//info.css('overflow-y', ref);
					
					if(availHeight >= naturalHeight){
						
						info.css('height', naturalHeight + 1);  // add the one to stop a .2 pixel height variation causing a scrollbar to appear 
						hideBoth();
					}
					
					
					show.unbind('click').click(function(){
						info.css('height',	Math.min(availHeight + em, naturalHeight) + 'px');
						showHide();
					});

					show.unbind('touchstart').bind('touchstart', function(e){
						$(e.target).click();
					    e.stopPropagation();
					    e.preventDefault();
					});
					
					if(self.showingEl == 'IFRAME'){
						showBoth();
						info.css('height', self.infoH + 'px');
						showShow();
						return;
					}

					
				}
			}			
		}
	};
	
	var showLightbox = function(callback){
		layout();
		
		if(callback){
			callback();
		}
	};

	
	return {
		"init" : function(cmp, src, navOb){
			init(cmp, src, navOb);
		},
		
		"layout" : function(){
			layout();
		},
		
		"showLightbox" : function(callback){
			showLightbox(callback);
			
			if(self.navOb){
				
				var navNext		= self.cmp.find('#nav-next');
				var navPrev		= self.cmp.find('#nav-prev');
				
				navPrev.add(navNext).bind('touchstart', function(e){
					$(e.target).click();
				    e.stopPropagation();
				    e.preventDefault();
				});
				
				
				navNext.add(navPrev).css('display', 'inline-block');
				self.cmp.unbind('swipe');
				
				if(! $("html").hasClass("ie8")){
					self.cmp.swipe({
						swipeStatus:function(event, phase, direction, distance, fingerCount) {
							if(phase=="end"){
								if(direction == "left"){
									self.navOb.prev();
								}
								else if(direction == "right"){
									self.navOb.next();
								}
							}
						},
						triggerOnTouchEnd:false,
						threshold:100
						}
					);
				}
				
			}
			
			if(window.addthis_config && !$('#lightbox-info .icon-share').parent().hasClass('addthis_button') ){
				$('#lightbox div.shares-link').html(window.addthis_html);  
				addthis.button( $('#lightbox div.shares-link')[0], window.addthis_config, window.addthis_share );
			}
			
		},

		"goTo" : function(i){
			self.navOb.goTo(i);
		},
		
		"setCmp" : function(cmp){
			self.cmp = cmp;
		},
		
		"getCmp" : function(callback){
			return self.cmp;
		},
		
		"next" : function(){
			self.navOb.next();
		},
		
		"prev" : function(){
			self.navOb.prev();
		},
		
		"zoom" : function(){
			self.cmp.find('#zoomIn').toggleClass('active');
			self.zoomed = !self.zoomed;
			layout();
		},

		"fsBtnClick" : function(){
			if(!$('.overlaid-content').hasClass('fullscreen')){
				enterFullscreen();
			}
			else{
				exitFullscreen();
			}
		},

		"exitFullscreen" : function(){
			exitFullscreen();
		}
		
	};
};
