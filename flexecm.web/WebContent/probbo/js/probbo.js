/**
 * 
 */

$(function() {
	initUI();
});

function initUI() {
	$("#screenType").change(function() {
		$(".deviceShell").removeClass().addClass("deviceShell").addClass($(this).val());
	});
	
	$(".panel .effect").click(function() {
		$(".panel .content").hide();
		$(".panel .content.effect").show();
	});
	
	
	$(".txt-font-size").blur(function() {
		if ($(".focus")) {
			if (isNaN($(this).val())) {
				return;
			}
			var fixed = parseFloat($(this).val()).toFixed(2)
			$(this).val(fixed);
			$(".focus").css("font-size", fixed + "em");
		}
	});
	
	$(".txt-content").blur(function() {
		if ($(".focus.text")) {
			$(".focus.text").html($(this).val());
		}
	});
	$(".txt-align").blur(function() {
		if ($(".focus.text")) {
			$(".focus.text").css("text-align",$(this).val());
		}
	});
}

var scenes =[{}];

function addText() {
	var div = $("<div class='text draggable'>我们看一下这个文字到底又多长呢</div>");
	
	div.css("width", "18em");
	
	div.css("font-size", "1em");
	div.css("left", "0");
	div.css("top", 0);
	$(".txt-align").val("left");
	
	$(".txt-loc-x").val(0);
	$(".txt-loc-y").val(0);
	
	
	$(div).click(function() {
		bindFocus($(this));
	});
	$(".screen").append(div);
	
	$(div).dragmove(function() {
		$(".txt-loc-x").val((parseInt($(this).css("left"))/14).toFixed(2));
		$(".txt-loc-y").val((parseInt($(this).css("top"))/14).toFixed(2));
	});
}

function bindFocus(div) {
	if ($(div).hasClass("focus")) return;
	
	$(".focus").removeClass("focus");
	$(div).addClass("focus");
	
	if ($(div).hasClass("text")) {
		$(".txt-content").val($(div).html());
		$(".txt-font-size").val((parseInt($(div).css("font-size"))/14).toFixed(2));
	}
}


//Draggable functionality for DOM elements
//Source: github.com/ByNathan/jQuery.dragmove
//Version: 1.0

(function($) {
 $.fn.dragmove = function(onmove) {
     return this.each(function() {
         var $document = $(document),
             $this = $(this),
             active,
             startX,
             startY;
         $this.on('mousedown touchstart', function(e) {
             active = true;
             startX = e.originalEvent.pageX - $this.offset().left;
             startY = e.originalEvent.pageY - $this.offset().top;	
             if ('mousedown' == e.type)
                 click = $this;
             if ('touchstart' == e.type)
                 touch = $this;
             if (window.mozInnerScreenX == null)
                 return false;	
         });
         
         $document.on('mousemove touchmove', function(e) {
             if ('mousemove' == e.type && active) {
            	 click.offset({ 
            		 left: e.originalEvent.pageX - startX,
            		 top: e.originalEvent.pageY - startY 
            	 });
            	 if (onmove) {
            		 onmove.bind(click)();
            	 }
             }
             
             if ('touchmove' == e.type && active) {
            	 touch.offset({
            		 left: e.originalEvent.pageX - startX,
            		 top: e.originalEvent.pageY - startY
            	 });
             }
             
         }).on('mouseup touchend', function() {
             active = false;
         });
     });
 };

})(jQuery);


