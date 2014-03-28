/*global jQuery, eu */
/*jslint browser: true, white: true */
(function( $, eu ) {
	'use strict';

	var displayAjaxFeedback = function( html ) {
		eu.europeana.ajax.methods.addFeedbackContent( html );
		eu.europeana.ajax.methods.showFeedbackContainer();
	},

	updateMyEuropeanaDb = function( ajax_data, ajax_feedback ) {
		if ( !eu.europeana.vars.user ) {
			return;
		}

		eu.europeana.ajax.methods.user_panel( 'save', ajax_data, ajax_feedback );
	},


	/**
	 * panel menus
	 */
	panelMenu = {
		$panel_links: $('#panel-links li'),
		default_panel: 'login',
		active_panel: null,
		hash: null,

		checkHash: function() {
			var hash = window.location.hash.substring(1),
			speed = 'slow';

			if ( hash !== panelMenu.hash ) {
				if ( hash === '' ) {
					panelMenu.hash = panelMenu.default_panel;
					window.location.hash = panelMenu.hash;
					speed = 0;
				} else {
					panelMenu.hash = hash;
				}

				panelMenu.setActivePanel( speed );
			}
		},

		addHashListener: function() {
			if ( window.onhashchange !== undefined ) {
				$(window).on('hashchange', panelMenu.checkHash);
			} else {
				eu.europeana.timer.addCallback({
					timer: 100,
					fn: panelMenu.checkHash,
					context: this
				});
			}
		},

		addPanelMenuListener: function() {
			$('#panel-links a').on('click', panelMenu.handlePanelMenuClick );
		},

		init: function() {
			if ( eu.europeana.vars.user ) {
				panelMenu.default_panel = 'user-information';
			}

			panelMenu.addHashListener();
			panelMenu.checkHash();
		},

		/**
		 * @param {string|int} speed
		 * speed at which to animate scrollTop
		 */
		setActivePanel: function( speed ) {
			panelMenu.setActivePanelLink();
			$('#' + panelMenu.hash).fadeIn();

			switch ( panelMenu.hash ) {
				case'login': $('#j_username').focus(); break;
				case'register': $('#register_email').focus(); break;
				case'request-password': $('#forgot_email').focus(); break;
			}

			$('body').animate({scrollTop:0}, speed);
		},

		setActivePanelLink: function() {
			panelMenu.$panel_links.each(function() {
				var $elm = $(this),
					$a = $('<a>'),
					$panel_a = $elm.find('a'),
					panel = $elm.attr('data-settings-panel'),
					panel_text = $elm.attr('data-settings-panel-text');

				if ( panel === panelMenu.hash ) {
					if ( $a.length > 0 ) {
						$elm.text( panel_text );
					}
				} else {
					if ( $panel_a.length < 1 ) {
						$('#' + panel).hide();
						$a.attr('href', '#' + panel).text(panel_text);
						$elm.html($a);
					}
				}
			});
		}
	},


	/**
	 * portal language
	 */
	pl = {
		addPortalLanguageListener: function() {
			$('#portalLanguage').on( 'change', this.handlePortalLanguageChange );
		},

		handlePortalLanguageChange: function() {
			$( this ).closest('form').submit();
		},

		init: function() {
			this.addPortalLanguageListener();
		}
	},


	/**
	 * translate keyword languages
	 */
	tkl = {
		animation_delay: 12,
		$translate_keyword_languages: $( '#translate-keyword-languages input' ),
		translate_keyword_languages_count: 0,
		translate_keyword_language_limit: 3,
		translate_keyword_language_user_limit: 6,
		translate_keywords_disabled: false,
		cookie_field: 'languagesSearch',
		cookie_value_delimeter: ',',
		$clear_selection: $( '#clear-selection' ),
		ajax_feedback: {
			success: function() {
				displayAjaxFeedback( $('<span>').text(eu.europeana.vars.msg.save_language_search) );
			},
			failure: function() {
				displayAjaxFeedback( $('<span>', {'class':'error'}).text(eu.europeana.vars.msg.save_language_search_failed) );
			}
		},
		ajax_data: {
			modificationAction: "user_language_search",
			languageSearch: null
		},

		addClearSelectionListener: function() {
			tkl.$clear_selection.on( 'click', tkl.handleClearSelectionClick );
		},

		addTranslateKeywordLanguagesListener: function() {
			this.$translate_keyword_languages.each(function() {
				$(this).on( 'click', tkl.handleTranslateKeywordsClick );
			});
		},

		/**
		 * add a value to the cookie only when it does not already exist
		 * in the cookie
		 *
		 * @param {string} value
		 */
		addValueToCookie: function( value ) {
			var cookie_value = this.getCookie();

			$.cookie.raw = true;

			if ( cookie_value === undefined ) {
				cookie_value = value;
			} else if ( $.inArray( value, cookie_value ) === -1 ) {
				cookie_value.push( value );
				cookie_value = cookie_value.join( this.cookie_value_delimeter );
			}

			$.cookie( this.cookie_field, cookie_value );
			tkl.ajax_data.languageSearch = cookie_value;
			updateMyEuropeanaDb( tkl.ajax_data, tkl.ajax_feedback );
		},

		checkCurrentCookie: function() {
			var cookie_value = this.getCookie();

			this.$translate_keyword_languages.each(function() {
				var $elm = $(this);

				if ( $.inArray( $elm.val(), cookie_value ) > -1 ) {
					$elm.prop('checked', true);
					tkl.translate_keyword_languages_count += 1;
				}

				if ( tkl.translate_keyword_languages_count
					>= tkl.translate_keyword_language_limit
				) {
					return false;
				}

				return true;
			});

			this.checkDisabledState();
		},

		checkDisabledState: function() {
			if ( tkl.translate_keyword_languages_count
				>= tkl.translate_keyword_language_limit
			) {
				tkl.disableTranslateKeywords();
				tkl.toggleDisabledKeywordsMessage();
			} else if ( tkl.translate_keywords_disabled ) {
				tkl.enableTranslateKeywords();
				tkl.toggleDisabledKeywordsMessage();
			}
		},

		checkLimit: function() {
			if ( eu.europeana.vars.user ) {
				this.translate_keyword_language_limit = this.translate_keyword_language_user_limit;
			}
		},

		disableTranslateKeywords: function() {
			var ii = tkl.$translate_keyword_languages.size(),
				r = tkl.randsort(ii);

			tkl.$translate_keyword_languages.each(function(i) {
				var $elm = $(this);

				if ( $elm.prop('checked') === false ) {
					setTimeout(function(){
						$elm.closest('label').addClass('disabled');
						$elm.prop('disabled', true);
					}, r[i] * tkl.animation_delay);
				}
			});

			tkl.translate_keywords_disabled = true;
		},

		enableTranslateKeywords: function() {
			var ii = tkl.$translate_keyword_languages.size(),
				r = tkl.randsort(ii);

			tkl.$translate_keyword_languages.each(function(i) {
				var $elm = $(this);

				setTimeout(function(){
					$elm.closest('label').removeClass('disabled');
					$elm.prop('disabled', false);
				}, r[i] * tkl.animation_delay);
			});

			tkl.translate_keywords_disabled = false;
		},

		getCookie: function() {
			return $.cookie( this.cookie_field )
				? $.cookie( this.cookie_field ).split( this.cookie_value_delimeter )
				: undefined;
		},

		handleClearSelectionClick: function() {
			$.removeCookie( tkl.cookie_field );
			tkl.translate_keyword_languages_count = 0;
			tkl.checkCurrentCookie();

			tkl.ajax_data.languageSearch = null;
			updateMyEuropeanaDb( tkl.ajax_data, tkl.ajax_feedback );
		},

		handleTranslateKeywordsClick: function() {
			var $elm = $(this);

			if ( $elm.prop('checked') ) {
				tkl.translate_keyword_languages_count += 1;
				tkl.addValueToCookie( $elm.val() );
			} else {
				tkl.translate_keyword_languages_count -= 1;
				tkl.removeValueFromCookie( $elm.val() );
			}

			tkl.checkDisabledState();
		},

		init: function() {
			tkl.addTranslateKeywordLanguagesListener();
			tkl.addClearSelectionListener();
			tkl.checkLimit();
			tkl.checkCurrentCookie();
		},

		randsort: function(c) {
			var i, n, o = [];

			for ( i = 0; i < c; i += 1 ) {
				n = Math.floor( Math.random() * c );

				if ( $.inArray( n, o ) > 0 ) {
					i -= 1;
				} else {
					o.push(n);
				}
			}

			return o;
		},

		removeValueFromCookie: function( value ) {
			var i,
			cookie_value = this.getCookie(),
			new_cookie_value = [];

			if ( cookie_value !== undefined && $.inArray( value, cookie_value ) > -1 ) {
				for ( i = 0; i < cookie_value.length; i += 1 ) {
					if ( cookie_value[i] !== value ) {
						new_cookie_value.push( cookie_value[i] );
					}
				}

				cookie_value = new_cookie_value.join( this.cookie_value_delimeter );
				$.cookie( this.cookie_field, cookie_value );

				tkl.ajax_data.languageSearch = cookie_value;
				updateMyEuropeanaDb( tkl.ajax_data, tkl.ajax_feedback );
			}
		},

		toggleDisabledKeywordsMessage: function() {
			$('#disabled-translate-keywords-msg').slideToggle();
		}
	},


	/**
	 * translate language item
	 */
	til = {
		$translate_language_item: $('#translate-language-item'),
		$language_item: $('#language-item'),
		$language_items: $('#language-item option'),
		cookie_field: 'languageItem',
		ajax_feedback: {
			success: function() {
				displayAjaxFeedback( $('<span>').text(eu.europeana.vars.msg.save_language_item) );
			},
			failure: function() {
				displayAjaxFeedback( $('<span>', {'class':'error'}).text(eu.europeana.vars.msg.save_language_item_failed) );
			}
		},
		ajax_data: {
			modificationAction: "user_language_item",
			languageItem: null
		},

		addLanguageItemListener: function() {
			til.$language_item.on( 'change',  til.handleLanguageItemChange );
		},

		addTranslateLanguageItemListener: function() {
			til.$translate_language_item.on( 'click',  til.handleTranslateLanguageItemClick );
		},

		checkCookie: function() {
			var cookie_value = $.cookie( this.cookie_field );

			if ( eu.europeana.vars.user && cookie_value !== undefined ) {
				this.$translate_language_item.prop('checked', true);

				this.$language_items.each(function() {
					var $elm = $(this);

					if ( $elm.val() === cookie_value ) {
						$elm.prop('selected', true);
						return false;
					}

					return true;
				});
			}
		},

		handleLanguageItemChange: function() {
			var $elm = $(this);

			til.$translate_language_item.prop('checked', true);
			$.cookie( til.cookie_field, $elm.val() );

			til.ajax_data.languageItem = $elm.val();
			updateMyEuropeanaDb( til.ajax_data, til.ajax_feedback );
		},

		handleTranslateLanguageItemClick: function() {
			var $elm = $(this),
			cookie_value = null;

			if ( $elm.prop('checked') ) {
				cookie_value = til.$language_item.val();
				$.cookie( til.cookie_field, cookie_value );
			} else {
				$.removeCookie( til.cookie_field );
			}

			til.ajax_data.languageItem = cookie_value;
			updateMyEuropeanaDb( til.ajax_data, til.ajax_feedback );
		},

		init: function() {
			this.addLanguageItemListener();
			this.addTranslateLanguageItemListener();
			this.checkCookie();
		}
	},


	/**
	 * user panels
	 */
	userPanels = {
		addApiSubmitListener: function() {
			$('#myeuropeana').on(
				'submit',
				'.api-key-save-form',
				{},
				this.handleApiKeySubmit
			);
		},

		addRemoveItemListener: function() {
			$('#myeuropeana').on('click',
				'.remove-saved-item',
				{ modification_action : 'saved_item' },
				this.handleRemoveUserPanelItem
			);
		},

		addRemoveSearchListener: function() {
			$('#myeuropeana').on('click',
				'.remove-saved-search',
				{ modification_action : 'saved_search' },
				this.handleRemoveUserPanelItem
			);
		},

		addRemoveTagListener: function() {
			$('#myeuropeana').on('click',
				'.remove-saved-tag',
				{ modification_action : 'social_tag' },
				this.handleRemoveUserPanelItem
			);
		},

		removeUserPanelItemSuccess: function( item, ajax_feedback ) {
			var $elm;

			eu.europeana.ajax.methods.addFeedbackContent( item.feedback_html );
			eu.europeana.ajax.methods.showFeedbackContainer();

			ajax_feedback.count = parseInt( ajax_feedback.$count.html(), 10 );
			ajax_feedback.$count.html( ajax_feedback.count - 1 );

			if ( item.removeSelector ) {
				$elm = $(item.removeSelector).eq(0);
				$elm.slideToggle(function() {
					$elm.remove();
				});
			}

			if ( ( ajax_feedback.count - 1 ) === 0 ) {
				item.$panel.append( item.no_saved_msg );
			}
		},


		/**
		 * @param {object} evt
		 * jQuery Event object
		 */
		handleRemoveUserPanelItem : function( evt ) {
			evt.preventDefault();

			var modification_action = evt.data.modification_action,
			self = this,
			$elm = $(this),
			ajax_feedback,
			ajax_data,
			item = {
				count : 0,
				$count : {},
				$panel : {},
				removed_msg : '',
				no_saved_msg : ''
			};

			switch ( modification_action ) {
				case 'saved_search' :
					item.$count = $('#saved-searches-count');
					item.$panel = $('#saved-searches');
					item.feedback_html = $('<span>').text(eu.europeana.vars.msg.saved_search_removed);
					item.no_saved_msg = eu.europeana.vars.msg.no_saved_searches;
					item.removeSelector = '.saved-search.' + $elm.attr('id');
					break;

				case 'saved_item' :
					item.$count = $('#saved-items-count');
					item.$panel = $('.saved-items');
					item.feedback_html = $('<span>').text(eu.europeana.vars.msg.saved_item_removed);
					item.no_saved_msg = eu.europeana.vars.msg.no_saved_items;
					item.removeSelector = '.saved-item.' + $elm.attr('id');
					break;

				case 'social_tag' :
					item.$count = jQuery('#saved-tags-count');
					item.$panel = jQuery('#saved-tags');
					item.feedback_html = $('<span>').text(eu.europeana.vars.msg.saved_tag_removed);
					item.no_saved_msg = eu.europeana.vars.msg.no_saved_tags;
					item.removeSelector = '.saved-tag.' + $elm.attr('id');
					break;
			}

			ajax_feedback = {
				count : item.count,
				$count : item.$count,
				success : function() {
					displayAjaxFeedback( item.feedback_html );
					userPanels.removeUserPanelItemSuccess.call( self, item, ajax_feedback );
				},
				failure : function() {
					var text = eu.europeana.vars.msg.error_occurred + ' ' +
					eu.europeana.vars.msg.item_not_removed;

					displayAjaxFeedback( $('<span>', {'class':'error'}).text(text) );
				}
			};

			ajax_data = {
				modificationAction : modification_action,
				id : parseInt( $elm.attr('id'), 10 )
			};

			eu.europeana.ajax.methods.user_panel( 'remove', ajax_data, ajax_feedback );
		},

		handleApiKeySubmit : function( evt ) {
			var $elm = $(this),
			ajax_feedback = {
				success: function() {
					displayAjaxFeedback( $('<span>').text(eu.europeana.vars.msg.saved_apikey) );
				},
				failure: function() {
					displayAjaxFeedback( $('<span>', {'class':'error'}).text(eu.europeana.vars.msg.saved_apikey_failed) );
				}
			},
			ajax_data = {
				modificationAction : "api_key",
				apikey : $elm.find("input[name='apikey-id']").val(),
				appName : $elm.find("input[name='apikey-application-name']").val()
			};

			evt.preventDefault();
			eu.europeana.ajax.methods.user_panel( 'save', ajax_data, ajax_feedback );
		},

		init: function() {
			this.addRemoveSearchListener();
			this.addRemoveItemListener();
			this.addRemoveTagListener();
			this.addApiSubmitListener();
		}
	};


	pl.init();
	tkl.init();
	til.init();
	panelMenu.init();
	userPanels.init();

}( jQuery, eu ));
