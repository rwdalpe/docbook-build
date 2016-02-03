/*

Copyright (C) 2014 Robert Winslow Dalpe

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.

*/

$(document).ready(function() {
	var widescreen = (window.matchMedia("(min-width: 50em)").matches || false),
		tocSelector = "article.book > div.lists-of-titles > div.toc";
	
	$(tocSelector).slicknav({
		label : "",
		allowParentLinks : true,
		closeOnClick : function() { return !widescreen },
		init : function() {
			$("div.toc").addClass("js-on");
			$("article.book").addClass("js-on");
		},
		open : function(trigger) {
			if (trigger[0].className.indexOf("slicknav_btn") > -1 && !widescreen) {
				$(".slicknav_menu").css("height", "60%");
			}
		},
		close : function(trigger) {
			if (trigger[0].className.indexOf("slicknav_btn") > -1 && !widescreen) {
				$(".slicknav_menu").css("height", "");
			}
		}
	});
	
	$(window).resize(function() {
		var mediaMatch = (window.matchMedia("(min-width: 50em)").matches || false);
		if(mediaMatch != widescreen) {
			widescreen = mediaMatch;
			
			if(mediaMatch) {
				$(".slicknav_menu").css("height", "100%");
				$(tocSelector).slicknav("open");
			} else {
				$(".slicknav_menu").css("height", "");
				$(tocSelector).slicknav("close");
			}
		}
	});
	
	if(widescreen) {
		$(tocSelector).slicknav("open");
	}

    var navs = $(".slicknav_item");
    var hiddenNavs = [];
    $(".slicknav_nav").prepend("<fieldset><label for='search'>Search TOC:</label> <input name='search' type='text' class='search'/>")
    $(".slicknav_nav").on('change', 'input.search', function(val) {
        var hidden = hiddenNavs.pop();

        while(hidden)
            hidden.css('height', 'auto');
            hidden = hiddenNavs.pop();
        });

        $.each(navs, function(nav) {
            if(val.trim() !== "" && !nav.text().contains(val)) {
                nav.css('height', '0');
                hiddenNavs.push(nav);
            }
        });
    });

});