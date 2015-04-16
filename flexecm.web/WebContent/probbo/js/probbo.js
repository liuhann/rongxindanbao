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
	
	$(".txt-font-size").change(function() {
		$(".focus").css("font-size", $(this).val() + "em");
	});
	
	$(".txt-content").keyup(function() {
		$(".focus.text").html($(this).val());
	});
	
	$(".txt-align").change(function() {
		if ($(".focus.text")) {
			$(".focus.text").css("text-align",$(this).val());
		}
	});

	$(".txt-loc-x, .txt-loc-y").change(function() {
		$(".focus").css("top", $(".txt-loc-y").val() + "rem");
		$(".focus").css("left", $(".txt-loc-x").val() + "rem");
	});
	
	$(".ip-bg-color").change(function() {
		if ($(".focus").length>0) {
			$(".focus").css("background-color", $(this).val());
		} else {
			$(".screen").css("background-color", $(this).val());
		}
	});
	
	$(".ip-txt-color").change(function() {
		if ($(".focus").length>0) {
			$(".focus").css("color", $(this).val());
		}
	});
	
	$(".screen").click(function() {
		removeFocus();
	});
	
	$(".show-sec").change(function() {
		$(".focus").data("showi", $(this).val());
	});
	$(".showEffect").change(function() {
		$(".focus").data("showe", $(this).val());
	});
	$(".showDura").change(function() {
		$(".focus").data("showd", $(this).val());
	});
	$(".hide-sec").change(function() {
		$(".focus").data("hidei", $(this).val());
	});
	$(".hideEffect").change(function() {
		$(".focus").data("hidee", $(this).val());
	});
	$(".hideDura").change(function() {
		$(".focus").data("hided", $(this).val());
	});
	
	for( e in SHOW_EFFECTS) {
		$("select.showEffect").append("<option value='" + e + "'>" + SHOW_EFFECTS[e] + "</option>");
	}
	for( e in HIDE_EFFECTS) {
		$("select.hideEffect").append("<option value='" + e + "'>" + HIDE_EFFECTS[e] + "</option>");
	}
	for( e in DURAS) {
		$("select.showDura").append("<option value='" + e + "'>" + DURAS[e] + "</option>");
		$("select.hideDura").append("<option value='" + e + "'>" + DURAS[e] + "</option>");
	}
	$("select.showEffect").change(function(){
		$(".focus").removeClass($(".focus").data("showeff"));
		$(".focus").removeClass($(".focus").data("showdura"));
		$(".focus").addClass($(".showDura").val());
		$(".focus").addClass($(".showEffect").val());
		$(".focus").data("showeff", $(".showEffect").val());
		$(".focus").data("showdura", $(".showDura").val());
	});
	removeFocus();
}

function addText() {
	var div = $("<div data-type='文字' class='text draggable'>内容</div>");
	div.css("font-size", "1em");
	div.css("left", "0");
	div.css("top", 0);
	div.css("color", "#000");
	div.css("background-color", "#fff");
	
	div.data("showi", 0);
	div.data("showe", "none");
	div.data("showd", "animdura05");
	
	div.data("hidei", -1);
	div.data("hidee", "none");
	div.data("hided", "animdura05");
	
	$(".txt-align").val("left");
	$(".txt-font-size").val(1.0);
	$(".txt-loc-x").val(0);
	$(".txt-loc-y").val(0);
	
	$(div).click(function(event) {
		event.stopPropagation();
		bindFocus($(this));
	});
	
	$(".screen").append(div);
	
	bindFocus(div);
	
	$(div).dragmove(function() {
		$(".txt-loc-x").val((parseInt($(this).css("left"))/14).toFixed(1));
		$(".txt-loc-y").val((parseInt($(this).css("top"))/14).toFixed(1));
	});
}

function bindFocus(div) {
	if ($(div).hasClass("focus")) return;
	
	$(".focus").removeClass("focus");
	$(div).addClass("focus");
	
	if ($(div).hasClass("text")) {
		$(".panel .properties>div").hide();
		$(".panel .properties>div.text").show();
		
		$(".txt-loc-x").val((parseInt($(div).css("left"))/14).toFixed(1));
		$(".txt-loc-y").val((parseInt($(div).css("top"))/14).toFixed(1));
		$(".txt-content").val($(div).html());
		$(".txt-font-size").val((parseInt($(div).css("font-size"))/14).toFixed(1));
		
		$(".ip-txt-color").val(eval($(div).css("color")));
		$(".ip-bg-color").val(eval($(div).css("background-color")));
		
		$(".show-sec").val($(div).data("showi"));
		$(".showEffect").val($(div).data("showe"));
		$(".showDura").val($(div).data("showd"));

		$(".hide-sec").val($(div).data("hidei"));
		$(".hideEffect").val($(div).data("hidee"));
		$(".hideDura").val($(div).data("hided"));
	}
}


function removeFocus() {
	$(".focus").removeClass("focus");
	
	$(".panel .properties>div").hide();
	$(".panel .properties>div.slide").show();
	
	$(".ip-bg-color").val(eval($(".screen").css("background-color")));
}


function pageActors() {
	$(".panel ul.tab li.selected").removeClass("selected");
	$(".panel .actors").addClass("selected");
	
	$(".panel .content").hide();
	$(".panel .content.actors").show();
	
	$(".actors.content ul li").remove();

	$(".screen div").each(function() {
		var li = $("<li><i class='vis icon-eye-1'></i> <span></span></li>");
		li.find("span").html($(this).data("type") + $(this).html());

		li.data("target", $(this));
		
		li.find("i").click(function(event) {
			event.stopPropagation();
			if ($(this).hasClass("icon-eye-1")) {
				$(this).removeClass("icon-eye-1").addClass("icon-eye-off");
				$(this).parents("li").data("target").hide();
			} else {
				$(this).addClass("icon-eye-1").removeClass("icon-eye-off");
				$(this).parents("li").data("target").show();
			}
		});
		li.click(function() {
			if ($(this).hasClass("selected")) {
				$(this).removeClass("selected");
				removeFocus();
			} else {
				$(".actors ul li.selected").removeClass("selected");
				$(this).addClass("selected");
				bindFocus($(this).data("target"));
			}
		});
		$(".actors.content ul").append(li);
	});
}

function pageAttrs() {
	$(".panel ul.tab li.selected").removeClass("selected");
	$(".panel .properties").addClass("selected");
	
	$(".panel .content").hide();
	$(".panel .content.properties").show();
}

var SHOW_EFFECTS = {
	"none": "无效果",
	"fadeInDown": "向下淡入",
	"fadeInUp": "向上淡入"
};

var HIDE_EFFECTS = {
		"none": "无效果",
		"fadeOutDown": "向下淡出",
		"fadeOutUp": "向上淡出"
	};

var DURAS = {
	"animdura05": "0.5",
	"animdura1": "1",
	"animdura2": "2",
	"animdura3": "3",
	"animdura4": "4",
	"animdura5": "5",
	"animdura6": "6"
};

var _switch_points = [
    {
    	"sinces": 0,
    	"desc": "页面启动"
    }
];

function saveAs() {
	var data = {
		'id': genPass(),
		'slides': []
	}
	var slide1 = {
		"background": eval($(".screen").css("background-color")),
		"actors": []
	}
	$(".screen div").each(function() {
		var actor = {
		};
		
		if ($(this).hasClass("text")) {
			actor.x = (parseInt($(this).css("left"))/14).toFixed(1);
			actor.y = (parseInt($(this).css("top"))/14).toFixed(1);
			
			actor.text = $(this).html();
			actor.fontSize = (parseInt($(this).css("font-size"))/14).toFixed(1);
			actor.fontColor = eval($(this).css("color"));
			actor.bgColor = eval($(this).css("background-color"));
			actor.showStart = $(this).data("showi");
			actor.showEffect = $(this).data("showe");
			actor.showDura = $(this).data("showd");
			
			actor.hideStart = $(this).data("hidei");
			actor.hideEffect = $(this).data("hidee");
			actor.hideDura = $(this).data("hided");
		}
		slide1.actors.push(actor);
	});
	data.slides.push(slide1);
	
	$.post("/service/probbo/add", {
		"json": JSON.stringify(data)
	}, function  () {
		alert(data.id);
	})
	
	console.log(data);
}

function genPass(){
	var x="123456789poiuytrewqasdfghjklmnbvcxzQWERTYUIPLKJHGFDSAZXCVBNM";
 	var tmp="";
 	var ran = Math.random();
 	for(var i=0;i< 10;i++) {
 		ran *=10;
		tmp += x.charAt(Math.ceil(ran)%x.length);
 	}
 	return tmp;
}

function rgb(r, g, b) {
	  return "#" + ((1 << 24) + (r << 16) + (g << 8) + b).toString(16).slice(1);
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


