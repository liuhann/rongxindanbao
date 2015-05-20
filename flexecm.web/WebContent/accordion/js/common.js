
$(function() {
	
	$(".accordion .question").bindtouch(function() {
		if ($(this).hasClass("current")) {
			trasform($(this), "translateY(0)");
			trasform($(this).siblings(".question"), "translateY(0)");
			trasform($(this).siblings(".selection"), "translateY(0)");
			$(this).removeClass("current");
			$(".selection ul").hide();
		} else {
			$(".accordion .question.current").removeClass("current");
			
			trasform($(this), "scale(1.1) translateY(0)");
			trasform($(this).prevAll(".question"), "translateY(0)");
			trasform($(this).nextAll(".question"), "translateY(200px)");
			
			$(this).addClass("current");
			$(".selection ul").hide();
			$("#mchoice_" + ($(this).prevAll(".question").length+1)).show();
			trasform($(this).siblings(".selection"), "translateY(-" + ($(this).nextAll(".question").length) * $(this).outerHeight() + "px)");
			//$(this).siblings(".selection").css("-webkit-transform","translateY(-" + ($(this).nextAll(".question").length) * $(this).outerHeight() + "px)");
		}
	});
	
	$("input[type='checkbox']").click(function() {
		var content = "<span>" + $(this).parent().next("span").html() + "</span>";
		if ($(this).attr("checked")=="checked") {
			$(".current .selected").append(content);
		} else {
			$(".current .selected").html($(".current .selected").html().replace(content, ""));
		}
	});
});

function trasform(t, style) {
	$(t).css("transition", "transform .3s cubic-bezier(.215, .61, .355, 1)");
	$(t).css("-ms-transition", "-ms-transform .3s cubic-bezier(.215, .61, .355, 1)");
	$(t).css("-webkit-transition", "-webkit-transform .3s cubic-bezier(.215, .61, .355, 1)");
	
	$(t).css("-webkit-transform", style);
	$(t).css("-ms-transform", style);
	$(t).css("transform", style);
}


$.fn.bindtouch = function(cb, nobubble) {
	attachEvent($(this), cb, nobubble);
};

function attachEvent(src, cb, nobubble) {
	$(src).unbind();
	var isTouchDevice = 'ontouchstart' in window || navigator.msMaxTouchPoints;
	if (isTouchDevice) {
		$(src).bind("touchstart", function(event) {
			$(this).data("touchon", true);
			$(this).addClass("pressed");
			if (nobubble) {
				event.stopPropagation();
			}
		});
		$(src).bind("touchend", function() {
			$(this).removeClass("pressed");
			if ($(this).data("touchon")) {
				cb.bind(this)();
			}
			$(this).data("touchon", false);
			if (nobubble) {
				event.stopPropagation();
			}
		});
		$(src).bind("touchmove", function() {
			$(this).data("touchon", false);
			$(this).removeClass("pressed");
			if (nobubble) {
				event.stopPropagation();
			}
		});
	} else {
		$(src).bind("mousedown", function() {
			$(this).addClass("pressed");
			$(this).data("touchon", true);
		});
		$(src).bind("mouseup", function() {
			$(this).removeClass("pressed");
			$(this).data("touchon", false);
			cb.bind(this)();
		});
	}
}