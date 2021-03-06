/**
 * Galleria Classic Theme 2012-08-08
 * http://galleria.io
 *
 * Licensed under the MIT license
 * https://raw.github.com/aino/galleria/master/LICENSE
 *
 */

(function($) {

/*global jQuery, Galleria */

Galleria.addTheme({
    name: 'europeana',
    author: 'Andy MacLean',
    css: 'galleria.europeana.css',
    defaults: {
        transition: 'slide',
        thumbCrop:  'height',

        // set this to false if you want to show the caption all the time:
        _toggleInfo: false
    },
    init: function(options) {
        Galleria.requires(1.28, 'This version of Classic theme requires Galleria 1.2.8 or later');
	
        /* europeana */
    	var thisGallery		= this;
    	var dataSource		= this._options.dataSource;
    	var carouselId		= this.$('container').parent().attr("id");
    	var carouselMode	= $('#' + carouselId).hasClass('europeana-carousel');
    	var borderedMode	= $('#' + carouselId).hasClass('europeana-bordered');
    	

    	thisGallery._options.europeana = {};
    	var europeana = thisGallery._options.europeana;
    	
    	
    	var info		= thisGallery.$( 'container' ).find(".galleria-info");
		var navLeft		= thisGallery.$( 'container' ).find(".galleria-image-nav-left");
		var navRight	= thisGallery.$( 'container' ).find(".galleria-image-nav-right");
    	
    	
    	if(carouselMode){
    		
        	var headerSelector = '#' + carouselId + '-header';
        	var footerSelector = '#' + carouselId + '-footer';
        	europeana.header = 	$('#' + carouselId).parent().find(headerSelector);
        	europeana.footer = 	$('#' + carouselId).parent().find(footerSelector);
        	
        	if(europeana.footer && europeana.header){
        		europeana.footer.append( $('#' + carouselId + ' .galleria-info-description') );
        		europeana.header.append( $('#' + carouselId + ' .galleria-info-title') );
        	}
        	else{
        		this.$('container').css("visibility", "hidden");
        		this.$('container').find(".galleria-thumbnails-list").css("display", "none");
        		Galleria.log("Error in Galleria.europeana.js: expected elements " + headerSelector + " and " + footerSelector + " to be present in DOM");
        		return;
        	}

    		/* create accelerators element and store in europeana config (extend the jquery object) */
    		europeana.header.accelerators = $( '<div class="accelerators"></div>' ).appendTo(europeana.header);
    		
        	europeana.thumbRatios = [];
        	for(var i=0; i<dataSource.length; i++){
        		europeana.thumbRatios[i] = null;
        	}
        	
			var thumbNavLeft =	thisGallery.$( 'container' ).find(".galleria-thumb-nav-left");
			var thumbNavRight =	thisGallery.$( 'container' ).find(".galleria-thumb-nav-right");
			
			europeana.setActive = function(index){

				var thumbs = thisGallery.$( 'container' ).find('.galleria-thumbnails'); 
				
				thumbs.find('.galleria-image').removeClass('active');
				thumbs.find('img').css("opacity", "0.6");
				thumbs.find('.galleria-image').eq(index).addClass('active');
				thumbs.find('img').eq(index).css("opacity", "1");

				/*
				 * set info cause a window resize (and a reset) on ipad
				 * 
				 * need to put the info into the images anyway.....
				 * 
				 * thisGallery._options.europeana.setInfo(index);
				 */
				
				thisGallery._options.europeana.header.setAccelerator(Math.floor( index / thisGallery._options.carouselSteps));
				
				if(index + (thisGallery._options.carouselSteps) < dataSource.length){			
					//thisGallery._carousel.set(index);
				}
				
			};
			europeana.setInfo = function(index){
				europeana.header.find(".galleria-info-title").html(dataSource[index].title);
				europeana.footer.find(".galleria-info-description").html(dataSource[index].description);
				
				europeana.footer.find(".galleria-info-description").click(function(){
					if(	dataSource[index].link){
						window.open(dataSource[index].link, "_new");
						
						// todo" add hover state / icon
					}
					else{
						return;
					}
				});
			};
			
			
			europeana.header.prev = function(e){
				e.preventDefault();
				if(	thumbNavLeft.hasClass("disabled")){
	                e.preventDefault();
		       		e.stopPropagation();
					return;
				}
				else{
	                thisGallery._carousel.set( thisGallery._carousel.current - thisGallery._options.carouselSteps);
	                thisGallery._options.europeana.setActive(thisGallery._carousel.current);		
					
					// disable if first
    				if(thisGallery._carousel.current - (thisGallery._options.carouselSteps) < 0){
    					thumbNavLeft.addClass("disabled");
    				}
    				else if(dataSource.length > 1){
   						thumbNavLeft.removeClass("disabled");    			
    				}

				}
			};
			
			thumbNavLeft.unbind('click');
			thumbNavLeft.bind('click', function(e){
                europeana.header.prev(e);
			});

			europeana.header.next = function(e){
				e.preventDefault();
				if(	thumbNavRight.hasClass("disabled")){
	                e.preventDefault();
		       		e.stopPropagation();
					return;
				}
				else{
	                thisGallery._carousel.set( thisGallery._carousel.current + thisGallery._options.carouselSteps);
	                thisGallery._options.europeana.setActive(thisGallery._carousel.current);
	                
					// disable if last
    				if(thisGallery._carousel.current + (thisGallery._options.carouselSteps) >= dataSource.length){
    					thumbNavRight.addClass("disabled");
    				}
    				else if(dataSource.length > 1){
   						thumbNavRight.removeClass("disabled");    			
    				}
				}				
			};
			
			thumbNavRight.unbind('click');
			thumbNavRight.bind('click', function(e){
                e.preventDefault();
				europeana.header.next(e);
			});
			
			europeana.setInfo(0);
    	}
    	else if(borderedMode){
    		
    		thisGallery._options.responsive = true;
    		thisGallery._options.responsive = false;
    		
    		// Doesn't seem to matter
    		//alert(    			thisGallery._options.responsive )

        		$(window).resize( function() {
        			Galleria.log("Bordered mode resize");
        			thisGallery.$(	'container' ).parent().css("height", 1.2 * parseInt(thisGallery.$( 'container' ).css("height")) + "px");
        		});
    	}
    	else{
    		/* non-carousel non-bordered (index page) */
			thisGallery._options.responsive = false;

    		var stage		= thisGallery.$( 'container' ).find(".galleria-stage");
			var title		= thisGallery.$( 'container' ).find(".galleria-info-title");
			var thumbs		= thisGallery.$( 'container' ).find(".galleria-thumbnails-container");

			/* add ctrls */
			var ctrls		= $('<div class="img-ctrls"></div>').appendTo(info);
			var ctrlLB		= $('<span class="icon-scaleup"></span>').appendTo(ctrls);
			var ctrlPlay	= $('<span class="icon-play"></span>').appendTo(ctrls);
			var ctrlPause	= $('<span class="icon-pause"></span>').appendTo(ctrls);

    		thisGallery.bind("image", function(e) {
    			dataSource[e.index].lb ? ctrlLB.show() : ctrlLB.hide();
    		});

			
			ctrlLB.click(function(){
				//alert( dataSource[thisGallery.getIndex()].lb   );
				thisGallery.openLightbox();
			});
			ctrlPlay.click(function(){
				thisGallery.next();
				thisGallery.play();
				ctrlPlay.addClass("active");
				navLeft.css("display", "none");
				navRight.css("display", "none");
			});
			ctrlPause.click(function(){     					
				thisGallery.pause();
				ctrlPlay.removeClass("active");
				navLeft.css("display", "block");
				navRight.css("display", "block");
			});
			
			
    		if(dataSource.length > 1){ // will always be the case on index page for this type of galleria
    			stage.after(info);
    			info.append(title);
    			
    			/* Layout non-carousel (single image mode) */
    			
    			var layoutSingle = function(){

        			/* stretch images and pull to top */
        			var imagesC	= stage.find(".galleria-images");
        			var images	= stage.find(".galleria-image img");
        			
        			var pullUp = parseInt(images.eq(0).css("top"));
        			imagesC.css("top", "-" + pullUp +  "px"); // shift whole gallery up to overcome pre-set 'top' value of all images
        			
        			images.css("width",		images.eq(0).parent().css("width") );
        			images.css("height",	"auto");
        			images.css("display",	"block");
        			images.css("left",		"0px");
        			images.removeAttr("width");
        			images.removeAttr("height");
        			
        			/* position info */
        			var infoTop = Math.min(
        				parseInt(images.eq(0).css("height")),
        				parseInt(images.eq(0).parent().css("height")),
        				images.eq(0).height(),
        				images.eq(0).parent().height()
        			);
        			info	.css("top", infoTop - info.height() + "px");
           			thumbs	.css("top", (infoTop + 3) + "px");
    			};
    			//  controls
    			
        		thisGallery.bind("loadfinish", function(e) {
        			layoutSingle();	
        			navLeft.css("left", "0px");
        			navRight.css("right", "0px");
         		 });
        		$(window).resize( function() {
        			Galleria.log("Resize landing page carousel");
        			thisGallery.next();	/* call to "next" repositions info and thumbnails */
        			return;
        		});
    		}
    		else{
    			/* we're showing a single image */
    			//alert("IS THIS EVER USED?")
				
				//var thumbs	=	this.$( 'container' ).find(".galleria-thumbnails-container");
				//var info 	=	this.$( 'container' ).find(".galleria-info");

				/* use extra that no thumbs gives us - move the stage down */
				var extraHeight = parseInt(thumbs.css("height"));
				thumbs.css("height", "0px");
				stage.css("bottom", parseInt(stage.css("bottom"))	- (extraHeight / 2) + "px");
				stage.css("top",	parseInt(stage.css("top"))		+ (extraHeight / 2) + "px");

				// position info
				
				info.css("top",			"auto");
				info.css("position",	"absolute");
				info.css("width",		stage.css("width"));
				info.css("left",		stage.css("left"));
				info.css("bottom",		stage.css("bottom"));

				// hide navigation
				
				navLeft.css("display", "none");
				navRight.css("display", "none");
				
				// custom full doc options
				this.$( 'container' ).css("border-radius", "10px 10px 0px 0px");
    		}
    		
    		$(window).resize( function() {
    			Galleria.log("Landing page galleria resize");
    			thisGallery.$(	'container' ).parent().css("height", thisGallery.$( 'container' ).css("height"));
    		});
    	}
    	
    	/* end europeana */


        // cache some stuff
        var touch = Galleria.TOUCH,
            click = touch ? 'touchstart' : 'click';

        // show loader & counter with opacity
		if(options.carouselMode != true){
	  	      this.$('loader,counter').show().css('opacity', 0.4);
		}

        // some stuff for non-touch browsers
		if (! touch ) {
			this.addIdleState( this.get('image-nav-left'), { left:-50 });
			this.addIdleState( this.get('image-nav-right'), { right:-50 });
			this.addIdleState( this.get('counter'), { opacity:0 });
			
			if(this.carouselMode){
				this.addIdleState( this.get('.galleria-thumb-nav-left'), { left:-50 });		/* TODO: Andy - this doesn't work */
				this.addIdleState( this.get('.galleria-thumb-nav-right'), { right:-50 });				
			}
		}
        
        // bind some stuff
        this.bind('thumbnail', function(e) {
            if (! touch ) {
                // fade thumbnails
                $(e.thumbTarget).css('opacity', 0.6).parent().hover(function() {
                    $(this).not('.active').children().stop().fadeTo(100, 1);
                }, function() {
                    $(this).not('.active').children().stop().fadeTo(400, 0.6);
                });

                if ( e.index === this.getIndex() ) {
                    $(e.thumbTarget).css('opacity',1);
                }
            } else {
                $(e.thumbTarget).css('opacity', this.getIndex() ? 1 : 0.6);
            }
        });

        this.bind('loadstart', function(e) {
        	/* europeana */
        	if(!carouselMode){
                if (!e.cached) {
                    this.$('loader').show().fadeTo(200, 0.4);
                }
        	}
        	/* end europeana */
            $(e.thumbTarget).css('opacity',1).parent().siblings().children().css('opacity', 0.6);
        });

        this.bind('loadfinish', function(e) {
            this.$('loader').fadeOut(200);
        });


        
        
        
		info.css("visibility", "visible");
		navLeft.css("visibility", "visible");
		navRight.css("visibility", "visible");
		
        if(!carouselMode){
        	return;
        }

        
        

		  /* SWIPE START */
			
		(function( images ) {
		var swipeStart = [0,0],
			swipeStop = [0,0],
			limitX = 30,
			limitY = 100,
			multi = false,
			tid = 0,
			data,
			ev = {
				start: 'touchstart',
				move: 'touchmove',
				stop: 'touchend'
			},
			getData = function(e) {
			var result	= e.originalEvent.touches ? e.originalEvent.touches[0] : e;
				return e.originalEvent.touches ? e.originalEvent.touches[0] : e;
			},
			moveHandler = function( e ) {
				if ( e.originalEvent.touches && e.originalEvent.touches.length > 1 ) {
					return;
				}
				data = getData( e );
				swipeStop = [ data.pageX, data.pageY ];

				if ( !swipeStart[0] ) {
						swipeStart = swipeStop;
				}

				if ( Math.abs( swipeStart[0] - swipeStop[0] ) > 10 ) {
						e.preventDefault();
				}
			},
			upHandler = function( e ) {
				images.unbind( ev.move, moveHandler );

				// if multitouch (possibly zooming), abort
				if (false && ( e.originalEvent.touches && e.originalEvent.touches.length ) || multi ) {
					multi = !multi;
					return;
				}
				if ( Galleria.utils.timestamp() - tid < 1000 &&
					 Math.abs( swipeStart[0] - swipeStop[0] ) > limitX &&
					 Math.abs( swipeStart[1] - swipeStop[1] ) < limitY ) {
						e.preventDefault();
						if( swipeStart[0] > swipeStop[0] ){
							europeana.header.next(e);
						}
						else{
							europeana.header.prev(e);
						}
					}
					swipeStart = swipeStop = [0,0];
				};

        images.bind(ev.start, function(e) {
            if ( e.originalEvent.touches && e.originalEvent.touches.length > 1 ) {
                return;
            }
            data = getData(e);
            tid = Galleria.utils.timestamp() + 1000;
            swipeStart = swipeStop = [ data.pageX, data.pageY ];
            images.bind(ev.move, moveHandler ).one(ev.stop, upHandler);

        });

    }( thisGallery.$( 'thumbnails' ).find('.galleria-image')   ));
    
    
    /* SWIPE END */
    

	thisGallery._options.responsive = false; /* disable default responsive handling (and assume that we ARE responsive) */
    
    this.$('loader').hide();
    
	var containerHeight = parseInt( this.$( 'container' ).css("height") );
	var containerWidth = parseInt( this.$( 'container' ).css("width") );
	
	/* Action */
	
	this.$( 'thumbnails' ).find('.galleria-image').each(function(i, ob){
		$(ob).unbind('click');
		/*
		$(ob).click(function(e, a){
       		e.stopPropagation();
       		europeana.setActive(i);
		});
		*/		
	});
	
	/* Styling & info:
	 * 
	 * Size the images according to container width.
	 * Calculate carousel step
	 * Create the accelerators accordingly
	 *  */
	var expectedCallBackCount = 0;
	var completedCallBackCount = 0;
	
    var setThumbStyle = function(thumb, thumbOb, index){
    	var imgBoxW = thisGallery._options.europeana.imgBoxW;
    	var tParent	= thumb.parent();
		var imgBox = containerHeight;
		
		tParent.css("width",	imgBoxW + "px");
		tParent.css("height",	imgBox + "px");

		imgW = thumb.width();
		imgH = thumb.height();
		
		var ratio;
		
		if(thisGallery._options.europeana.thumbRatios[index]){
			// caching the ratio avoids successive number rounding distorting the aspect ratio
			ratio = thisGallery._options.europeana.thumbRatios[index];
		}
		else{
			thisGallery._options.europeana.thumbRatios[index] = imgW / imgH;
			ratio = imgW / imgH;
		}
		
		if(imgW > imgH){ // landscape
			var marginR = parseInt(tParent.css("margin-right"));
			var newW	= imgBoxW-marginR;
			
			if(newW/ratio > containerHeight){
				var newH = containerHeight / ratio;
				newW = newH *  ratio;
			}

			thumb.attr("width", newW + "px");
			thumb.attr("height", newW/ratio + "px");

			thumb.css("width", newW + "px");
			thumb.css("height", newW/ratio + "px");
		}
		else{ // portrait or perfect square
			var newH = (containerHeight - 2 * 7);
			var newW = newH * ratio;

			thumb.attr("height",	newH + "px");
			thumb.attr("width",		newW + "px");
						
			thumb.css("height",		newH + "px");
			thumb.css("width",		newW + "px");
		}

		thumb.css("max-width",	 "100%");
		thumb.css("max-height",	 "100%");
		
		
		/* Gallery.updateCarousel() looks 1st for property "outerWidth" when calculating the total width of the thumbnail list.
		 * If the width is too short the last item(s) will wrap and never be viewable.
		 * If the width is too big there is spare space at the end of the scroll.
		 * 
		 * Here we help updateCarousel() by setting "outerWidth" to our box dimension plus the relative component margins and offsets. 
		 *  */
		var offset = 2;		
		offset += parseInt( tParent.css("margin-right") );
		thumbOb.outerWidth = imgBoxW + offset;
		
		/* Vertical centering of individual images */
		if(imgBox > thumb.height()){//parseInt(thumb.css("height"))){
			var top = (imgBox - parseInt(thumb.css("height")) ) / 2;
			thumb.css("top", top + "px");
		}
		

		completedCallBackCount ++;	// bump callback counter
		
		if(completedCallBackCount == expectedCallBackCount){
			
			/* Do this once only  */
			thisGallery.updateCarousel();

			/* And since we have access to the dom, fix the navigation icons */
			
			var navRight	= tParent.closest(".galleria-carousel").find(".galleria-thumb-nav-right");
			var navLeft		= tParent.closest(".galleria-carousel").find(".galleria-thumb-nav-left");
			navRight.css	("top", (containerHeight - 124)/2 + "px");
			navLeft.css		("top", (containerHeight - 124)/2 + "px");    	

			completedCallBackCount = 0;
		}
    };

    var callSetThumbStyle = function(){
    	
    	/* scaling */
    	var imgBoxW = 0;
			
    	/* update containerWidth & containerHeight */
    	containerHeight = parseInt( thisGallery.$( 'container' ).parent().css("height") );
    	containerWidth = parseInt( thisGallery.$( 'container' ).parent().css("width") );
		var thumbnailsList = thisGallery.$( 'container' ).find('.galleria-thumbnails-list');		
		var reduce = 0;
		reduce += parseInt(thumbnailsList.css('margin-left'));
		reduce += parseInt(thumbnailsList.css('marginRight'));
		reduce += parseInt(thumbnailsList.parent().css('right'));
		reduce += parseInt(thumbnailsList.parent().css('left'));
		reduce = 80; /* hard coded: 30+30+10+10, the margins and the left values of containers.... */
		
		var containerWidth_R = containerWidth-reduce;
		
		var maxItems = (
				containerWidth_R
				- 
				(
						containerWidth_R % containerHeight
				)
				) / containerHeight;
		
		maxItems = Math.max(maxItems, 1); // avoid division by zero!
		
		imgBoxW = containerWidth_R / maxItems;		
		imgBoxW -= parseInt(thumbnailsList.find('.galleria-image:first').css("margin-right"));
		
		/* store steps and width data in europeana config */
		thisGallery._options.carouselSteps = maxItems;
    	thisGallery._options.europeana.imgBoxW = imgBoxW;

    	/* add accelerators: bound to listeners and stored in europeana config */
    	europeana.header.find('.accelerator').remove();
    	europeana.header.accelerators.items = [];
    	europeana.header.setAccelerator = function(index){
			europeana.header.find('.accelerator').removeClass('active');
			europeana.header.find('.accelerator').eq(index).addClass('active');	
    	};
    	
    	var acceleratorsNeeded = Math.ceil( $(thisGallery._thumbnails).length / thisGallery._options.carouselSteps);

    	for(var i=0; i<acceleratorsNeeded; i++){
    		europeana.header.accelerators.items[europeana.header.accelerators.items.length]
    		=
    		$('<div class="accelerator' + (i==0 ? ' active' : '') + '"></div>').appendTo(europeana.header.accelerators)[0].onclick = function(index){
    			return function(){
    				var active = index * thisGallery._options.carouselSteps;
    				thisGallery._carousel.set(active);
					thisGallery._options.europeana.setActive(active);
    			};
    		}(i);
    	}

    	/* iterate thumbs & call setStyle on each */

        $(thisGallery._thumbnails).each(function( i, thumb ) {

        	if(thumb.ready){

	    		thisGallery.trigger({
	    			type: Galleria.THUMBNAIL,
	                      thumbTarget: thumb.image,
	                      index: i,
	                      galleriaData: dataSource
	    		});

        	}
        	else{
        		expectedCallBackCount ++;
        		thisGallery.bind("thumbnail", function(e) {
        			if(e.index == i){
            	        	setThumbStyle($(e.thumbTarget), thumb, i);
        			}
        	    });
        	}
        });
    };
    
    callSetThumbStyle();
    
    
    var onResize = function(){
		thisGallery.$('container').css("height", thisGallery.$('container').parent().css("height"));
		thisGallery.$('container').css("width", thisGallery.$('container').parent().css("width"));
		callSetThumbStyle();
		thisGallery._carousel.set(0);
		thisGallery._options.europeana.setActive(0);    	
    };

    
    /* If this carousel lives in a collapsible container then it needs to refresh when that container has been opened.
     * Intercept the custom event here
     */
	$(window).bind('collapsibleExpanded', function(event, elements) {
		var sectionIsParent = false;
		$(elements).each(function(i, e){
	    	var parent = thisGallery.$('container')[0];
	    	while(parent != null ){
	    		if( parent == e ){
	    			sectionIsParent = true;
	    		}
	    		parent = parent.parentNode;
	    	}
		});
		if(sectionIsParent){
			onResize();
		}
	});
	
	$(window).resize( function() {
		onResize();
     });
           
	
    }
});

}(jQuery));
