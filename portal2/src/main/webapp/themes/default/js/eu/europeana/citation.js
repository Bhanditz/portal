/**
 *  citation.js
 *
 *  @package	eu.europeana
 *  @author		dan entous <contact@gmtpluosone.com>
 *  @author		andy maclean
 *  @created	2011-07-07 11:14 GMT +1
 *  @version	2012-11-27 22:22 ECT
 */

js.utils.registerNamespace( 'eu.europeana.citation' );

eu.europeana.citation = {
	
	options : {
		container : '.iframe-wrap',
		html : ''
	},
	
	init : function() {
		
		var self = this;
	
		this.options.html = '' 
			+	'<div class="external-services-container-wrapper">'
			+		'<div class="external-services-container">'
			+			'<a rel="nofollow" title="' + eu.europeana.vars.msg.cite.close + '" class="close-button icon-remove" href="">&nbsp;</a>' 
			+			'<div id="citation-tabs">';
			
			var html = '';
			$( "#citations .citation").each(function(i, ob){
				html += ''
					+			'<a href="#" class="tab-header">'
					+				(i==0 ? eu.europeana.vars.msg.cite.citation : eu.europeana.vars.msg.cite.footnote)
					+			'</a>'
					+			'<div class="section" id="citestyle' + i + '">'
					+				'<div class="content">'
					+					'<div class="content-inner">'
					+						eu.europeana.vars.msg.cite.citation_header					
					+						'<span class="copy">'
					+ 						 	ob.innerHTML
					+						'</span>'
					+					'</div>'
					+				'</div>'
					+			'</div>';
			});
			
			this.options.html += html
			+				'</div>'
			+			'</div>'
			+		'</div>';
			+	'</div>';

		jQuery('#citation-link').bind( 'click', { self : self }, this.handleCitationClick );
	},
	
	
	handleCitationClick : function( e ) {

		com.google.analytics.europeanaEventTrack("Europeana Portal", "Wikipedia Citation", $('head link[rel="canonical"]').attr('href') );

		e.preventDefault();
		
		var self = e.data.self;
		
		$(self.options.container).html(self.options.html);
		$(self.options.container + ' .close-button').bind('click', { self : self }, self.toggleCitation);
		
		self.toggleCitation(e);
	},

	
	toggleCitation : function( e ) {
		e.preventDefault();
		
		$(".overlaid-content").css('visibility', 'visible');

		eu.europeana.citation.addTheTabs(e);
	},
	
	
	selectElementContents : function(el) {
	    var range;
	    if (window.getSelection && document.createRange) {
	        range = document.createRange();
	        var sel = window.getSelection();
	        if(typeof sel != 'undefined' && sel != null){
		        range.selectNodeContents(el);
		        try{
		        	sel.removeAllRanges();
		        }catch(e){}
		        try{
		        	sel.addRange(range);
		        }catch(e){}
	        }
	    }
	    else if (document.body && document.body.createTextRange) {
	    	try{
		        range = document.body.createTextRange();
		        range.moveToElementText(el);
		        range.select();
	    	}
	    	catch(e){}
	    }
	},
	
	addTheTabs : function(e){
		
		var self = e.data.self;
		
		var callback = function(index, id){
			eu.europeana.citation.selectElementContents(   $(self.options.container).find('.section.active .content-inner .copy')[0]   );					
		};
		eu.europeana.citation.tabs = new AccordionTabs( $('#citation-tabs'), callback );			
		
		
		$(".iframe-wrap, .close-button").unbind("click").each(function(i, ob){
			$(ob).click(function(e){
				if(e.target == ob){
					$(".overlaid-content").css('visibility', 'hidden');					
				}
				e.preventDefault();
			});
		});

	}
};



