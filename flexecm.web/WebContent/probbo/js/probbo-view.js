/**
 * 
 */

$(function() {
	$.getJSON("/service/probbo/get", {
		'id' : query_string("id")
	}, function (json) {
		json.type = "mobile";
		play(json);
	});
});

function play(json) {
	$("html").css("font-size", $(window).width()/18);
	initSlide(json.slides[0]);
}


function initSlide(slide) {
	
	var slideContainer = $("<div class='slide'></div>");
	$("body").append(slideContainer);
	
	for(var i=0; i<slide.actors.length; i++) {
		var actor = slide.actors[i];
		var sprite = $("<div></div>");
		if (actor.type="text") {
			sprite.html(actor.text);
		}
		slideContainer.append(sprite);
	}
}

function query_string(key) {
	return (document.location.search.match(new RegExp("(?:^\\?|&)"+key+"=(.*?)(?=&|$)"))||['',null])[1];
}

