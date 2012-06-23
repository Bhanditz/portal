(function() {
	
	var $login_response = jQuery('#login-response'),
		login_boxes = [ jQuery('#login'), jQuery('#request-password'), jQuery('#register') ];
	
	showBox( window.location.hash );
	addListeners();	
	
	
	function checkForResponse() {
		
		if ( $login_response.html().length > 0 ) {
			
			$login_response.show();
			js.utils.flashHighlight( $login_response, '#ffff00', '#fff', 1500);
			
		}
		
	}
	
	
	function addListeners() {
		
		jQuery('#login-boxes a').each(function( index, value ) {
			
			jQuery(value).bind('click', toggleBoxes );
			
		});
		
	}
	
	
	function toggleBoxes(e) {
		
		var i,
			ii = login_boxes.length,
			target = jQuery(this).attr('href'),
			$target = jQuery(target);
		
		e.preventDefault();
		
		for ( i = 0; i < ii; i += 1 ) {
			
			if ( login_boxes[i].is(':visible') ) {
				
				$login_response.hide();
				
				login_boxes[i].fadeOut(function() {
					
					$target.fadeIn(function(){
						$target.find('input:text').eq(0).focus();
					});
					
				});
				
				break;
				
			}
			
		}
		
		
	}
	
	function showBox( hash ) {
		
		var i,
			ii = login_boxes.length,
			$target = login_boxes[0];
		
		if ( hash ) {
			
			$target = jQuery(hash);
			
			for ( i = 0; i < ii; i += 1 ) {
				
				if ( login_boxes[i].is(':visible') ) {
					
					login_boxes[i].hide();
					break;
					
				}
				
			}
			
			$target.show();
			
		} else {
			
			$target.show();
			
		}
		
		$target.find('input:text').eq(0).focus();
		checkForResponse();
		
	}
	
})();