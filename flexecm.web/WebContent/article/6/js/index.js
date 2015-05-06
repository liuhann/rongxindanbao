/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
var app = {
    // Application Constructor
    initialize: function() {
        this.bindEvents();
    },
    bindEvents: function() {
        document.addEventListener('deviceready', this.onDeviceReady, false);
    },
    onDeviceReady: function() {
    }
};

$(document).ready(function() {
	var w = $("#screen").width();
	var oh = parseInt($("#header").css("height"));
	
	$("#header").css("height", $("#screen").height()/2);
	$("#header").css("top", oh-$("#screen").height()/2);
	$("#header").data("ov", $("#screen").height()/2-oh);
	
	$("#menu").data("translatex", w/2-75);
	$("#menu").css("transform", "translate3d(" + (w/2-75) + "px, 0px, 0px)");
	$("#menu li:first()").addClass("op1");
	
	var myElement = document.getElementById('header');
	var mc = new Hammer(myElement);
	mc.get('pan').set({ direction: Hammer.DIRECTION_ALL });
	
	mc.on("pandown panup panend panstart", function(ev) {
		if (ev.type=="panstart") {
			var nott = $("#menu li:not(.op1)");
			console.log("nott : " + nott.length);
			$("#menu li.op1").after(nott);
			$("#menu").data("translatex", w/2-75);
			$("#menu").css("transform", "translate3d(" + (w/2-75) + "px, 0px, 0px)");
		}
		
		if (ev.type=="pandown" || ev.type=="panup") {
			$("#header").css("-webkit-transition-duration", "0");
			$("#menu").css("-webkit-transition-duration", "0");
			var ov = $("#header").data("ov");
			if (ev.deltaY<ov) {
				var translateX = $("#menu").data("translatex"); 
				if (ev.deltaY>ov/3) {
					$("#menu").addClass("slide");
					var inc = Math.floor(ov/3*2/$("#menu li").length);
					var seq = Math.floor((ev.deltaY-ov/3)/inc);
					
					if (seq>$("#menu li").length-1) {
						seq = $("#menu li").length-1;
					}
					if (!$("#menu li:nth-child(" + (seq+1) + ")").hasClass("op1")) {
						$("#menu li.op1").removeClass("op1");
						$("#menu li:nth-child(" + (seq+1) + ")").addClass("op1");
					}
					translateX = w/2  - 75 - 150*seq;
					
					$("#menu").data("translatex", translateX);
				}
				
				$("#header").css("transform", "translate(0px, " + ev.deltaY + "px) translateZ(0px)");
				$("#menu").css("transform", "translate3d(" + translateX + "px , -" + ev.deltaY/2 + "px, 0px)");
				console.log($("#menu").css("transform"));
			}
		}
		if (ev.type=="panend") {
			$("#menu").removeClass("slide");
			$("#header").css("-webkit-transition-duration", "200ms");
			$("#menu").css("-webkit-transition-duration", "200ms");
			
			$("#header").css("transform", "translate(0px, 0px) translateZ(0px)");
			$("#menu").css("transform", "translate3d(" + $("#menu").data("translatex") + "px, 0px, 0px)");
			
		}
	});
});


