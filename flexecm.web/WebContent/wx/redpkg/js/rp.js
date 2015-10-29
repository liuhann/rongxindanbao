$(function() {

   RedPackageDirector.load(function(){
        RedPackageDirector.play();
   });

});


var RedPackageDirector = (function(window) {

    var WIDTH = $(window).width();
    var HEIGHT = $(window).height();

    var res = {
        MAP1_BG : "images/001_bg.jpg",
        MAP1_PR_PEOPLE: "images/001_people.png",
        MAP2_BG : "images/002_bg.jpg",
        MAP2_TEXT: "images/002_text.png",
        MAP2_WORKING_GIF: "images/003_people.gif",
        MAP3_TEXT: "images/003_text.png",
        MAP4_BG: "images/004_bg.jpg",
        MAP4_TEXT: "images/004_text.png",
        MAP5_BG: "images/005_bg.jpg",
        MAP5_TEXT: "images/005_text.png",
        MAP6_BG: "images/006_bg.jpg",
        MAP6_TEXT: "images/006_text.png",
        MAP7_BG : "images/007_bg.jpg",
        MAP7_CAR : "images/007_car.png",
        MAP7_TEXT : "images/007_text.png",
        MAP7_LOGO : "images/007_logo.png",
        MAP8_SMOKE1 : "images/008_smoke01.png",
        MAP8_SMOKE2 : "images/008_smoke02.png"
    };

    function play() {
        showClickNext(scene2);
    }

    function scene2() {
        console.log("scene 2");
        $("body").unbind();
        var bg = setBg(res.MAP2_BG, "circleout");
        clear();
        setTimeout(function() {
            addFixedSprite(res.MAP2_TEXT, 0.1, 0.05, 0.8, {
                "z-index": "21"
            }, ["animated","fadeInUp"]);
        }, 700);

        setTimeout(function() {
            showClickNext(scene3);
        }, 1200);
        //
    }

    function scene3() {
        clear();
        addFixedSprite(res.MAP3_TEXT, 0.1, 0.05, 0.7, {
            "z-index": "31"
        }, ["animated","fadeInUp"]);
        setTimeout(function() {
            addSprite(res.MAP2_WORKING_GIF, 0.4, 0.4 , 0.3, {
                "z-index": "22"
            });
        }, 400);

        setTimeout(function() {
            showClickNext(scene4);
        }, 1200);
    }

    function scene4() {
        clear();
        setBg(res.MAP4_BG, "zoomFade");

        setTimeout(function() {
            addFixedSprite(res.MAP4_TEXT, 0.1, 0.2, 0.4, {
                "z-index": "41"
            },  ["animated","fadeInUp"]);
        }, 1500);


        setTimeout(function() {
            showClickNext(scene5);
        }, 2500);
    }

    function scene5() {
        clear();
        setBg(res.MAP5_BG, "slideOutLeft");
        setTimeout(function() {
            addFixedSprite(res.MAP5_TEXT, 0.1, 0.2, 0.5, {
                "z-index": "51"
            },  ["animated","fadeInUp"]);
        }, 1500);

        setTimeout(function() {
            showClickNext(scene6);
        }, 2500);
    }

    function scene6() {
        clear();
        setBg(res.MAP6_BG, "slideOutLeft");
        setTimeout(function() {
            addFixedSprite(res.MAP6_TEXT, 0.1, 0.2, 0.5, {
                "z-index": "51"
            },  ["animated","fadeInUp"]);
        }, 1500);

        setTimeout(function() {
            showClickNext(scene7);
        }, 2500);
    }


    function scene7() {
        clear();
        setBg(res.MAP7_BG, "slideOutLeft");
        setTimeout(function() {
            addFixedSprite(res.MAP7_CAR, 0.2, 0.3, 0.6, {
                "z-index": "71"
            },  ["animated","fadeIn"]);
        }, 500);

        setTimeout(function() {
            addFixedSprite(res.MAP7_TEXT, 0.15, 0.1, 0.7, {
                "z-index": "72"
            },  ["animated","fadeInUp"]);
        }, 1000);

        setTimeout(function() {
            addFixedSprite(res.MAP7_LOGO, 0.4, 0.63, 0.2, {
                "z-index": "75"
            },  ["animated","flipInX"]);
        }, 2500);

        setTimeout(function() {
            var label = addAnimatedSprite([res.MAP8_SMOKE1, res.MAP8_SMOKE2],"smoking",200, 0.15, 0.55, 0.7, 0.3);
            label.css("z-index", "79");
        }, 4000);

        setTimeout(function() {
            scene8();
        }, 6000);
    }

    function scene8() {

    }



    function showClickNext(cb) {
        var div = $("<div class='next'><span>轻触到下一幕</span></div>");
        div.css("font-size", "20px");
        div.css("left", (WIDTH-100)/2);
        div.css("bottom", HEIGHT/6);

        $("body").append(div);

        $("body").unbind();

        $("body").bind("click", function(e) {
            e.preventDefault();
            cb();
        });
    }


    function clear() {
        $("body").unbind();
        $(".next").remove();
        $(".sprite").remove();
        $(".label").remove();
    }

    function load(cb) {
        setBg(res.MAP1_BG);
        var f = [];
        for(var img in res) {
            f.push(res[img]);
        }
        var yy = WIDTH/640 * 600;
        console.log(yy);

        var progress = addSprite("images/001_people.png", 0.3, yy-81);
        progress.append("<div class='percent' style='position: absolute;left: 14px;top: 14px;'></div>");

        var pasted = $("<div style='position:absolute; height: 4px;background-color: #ffec00; width: 20px;'></div>");
        $("body").append(pasted);
        pasted.css("left", WIDTH*0.3);
        pasted.css("top", yy);
        pasted.css("-webkit-transition", "width .6s linear");

        preloadPictures(f, function(loaded) {
            progress.find(".percent").html(Math.floor(loaded/f.length*100) + "%");
            pasted.css("width",0.3 + (loaded/f.length)*0.4 * WIDTH + "px");
            moveTo(progress, 0.3 + (loaded/f.length)*0.4, yy, "1000ms");
        }, function() {
            cb();
        });
    }

    function moveTo(sprite, x, y, dura) {
        var rx = x, ry = y;
        if (rx<1) rx = rx * WIDTH;
        if (ry<1) ry = ry * HEIGHT;
        $(sprite).css("-webkit-transform", "translate(" + rx  + "px, " + (ry-81) + "px)");

        $(sprite).data("x", x);
        $(sprite).data("y", y);

        if (dura) {
            $(sprite).css("transition-duration", dura);
        }
    }

    function addSprite(url, x, y, width, styles, clazzs) {
        var sprite = $("<div class='sprite'><img></div>");
        var rx = x, ry = y;
        if (rx<1) rx = rx * WIDTH;
        if (ry<1) ry = ry * HEIGHT;

        sprite.data("x", rx);
        sprite.data("y", ry);

        sprite.css("-webkit-transform", "translate(" + rx  + "px, " + ry + "px)");
        sprite.find("img").attr("src", url);

        if (width) {
            sprite.find("img").attr("width", width*WIDTH);
        }

        apply(sprite, styles, clazzs);
        $("body").append(sprite);
        return sprite;
    }

    function addFixedSprite(url, x, y, width, styles, clazzs) {
        var sprite = $("<div class='label'><img></div>");
        var rx = x, ry = y;
        if (rx<1) rx = rx * WIDTH;
        if (ry<1) ry = ry * HEIGHT;

        sprite.css("left", rx);
        sprite.css("top", ry);
        sprite.find("img").attr("src", url);

        if (width) {
            sprite.find("img").attr("width", width*WIDTH);
        }
        apply(sprite, styles, clazzs);
        $("body").append(sprite);
        return sprite;
    }


    function addAnimatedSprite(frames, name, intevals, x, y, width, height) {
        var sprite = $("<div class='animatedSprite'></div>");
        var rx = x, ry = y;
        if (rx<1) rx = rx * WIDTH;
        if (ry<1) ry = ry * HEIGHT;
        sprite.css("left", rx);
        sprite.css("top", ry);
        sprite.css("width", width*WIDTH);
        sprite.css("height", height*HEIGHT);

        sprite.css("background-image", "url('" + frames[0] + "')");
        var animatedStyle = "@-webkit-keyframes " + name + " { ";
        for(var i=0; i<frames.length; i++) {
            animatedStyle += (i * 100/frames.length) + "% { "
            + "background-image: url('" + frames[i] + "');"
            + "} "
        }
        animatedStyle += "}" ;
        animatedStyle += "." + name + " { -webkit-animation-name: " + name + ";}";

        $("body").append("<style>" + animatedStyle + "</style>");

        sprite.addClass(name);
        sprite.addClass("animated infinite");
        sprite.css("-webkit-animation-duration" , intevals);

        $("body").append(sprite);
        return sprite;
    }

    function apply(ele, styles, clazzs) {
        if (styles) {
            for(var key in styles) {
                ele.css(key, styles[key]);
            }
        }
        if (clazzs) {
            for(var i=0; i<clazzs.length; i++) {
                ele.addClass(clazzs[i]);
            }
        }
    }

    function removeSprite(sprite) {
        $(sprite).remove();
    }

    function setBg(url , effect) {
        var div = $("<div class='background'></div>");
        var img = $("<img>");
        img.attr("src", url);
        img.attr("width", $(window).width());
        img.css("-webkit-transform", "translateY(-80px);");
        div.append(img);

        if (effect==="circleout") {
            placeAbove();
            var old = $(".background");
            div.css("-webkit-clip-path","circle(10% at 60% 50%)");
            div.css("-webkit-transition", "-webkit-clip-path .6s linear");
            $("body").append(div);
            setTimeout(function() {
                div.css("-webkit-clip-path","circle(80%)");
            }, 100);
            setTimeout(function() {
                old.remove();
            }, 1000);
        } else if (effect=="zoomFade") {
            placeAbove();
            var old = $(".background");
            div.css("opacity", "0");
            div.css("-webkit-transform", "scale(1.5)");
            setTransitionTime(div, "1200ms");
            $("body").append(div);

            setTimeout(function() {
                setTransitionTime(old, "1200ms");
                old.css("opacity",1);
                old.css("-webkit-transform", "scale(4)");

                div.css("-webkit-transition-delay",.5);
                div.css("opacity", "1");
                div.css("-webkit-transform", "scale(1)");
            }, 100);
        } else if (effect==="slideOutLeft") {
            placeAbove();
            var old = $(".background");
            div.css("-webkit-transform", "translateX("+ WIDTH + "px)");

            $("body").append(div);
            setTransitionTime(old, "800ms");
            setTransitionTime(div, "800ms");
            setTimeout(function() {
                old.css("-webkit-transform", "translateX(-" + WIDTH+ "px)");
                div.css("-webkit-transform", "translateX(0)");
            }, 100);

            setTimeout(function() {
                old.remove();
            }, 1000);

        } else {
            $("body").append(div);
        }

        function placeAbove() {
            var zo = $(".background").css("z-index");
            if (zo) {
                div.css("z-index", parseInt(zo) + 1);
            } else {
                div.css("z-index", -1);
            }
        }
        return div;
    }

    function setTransitionTime(div, time, effect) {
        //var ends = time  + ((effect==null)?"linear":effect);
        div.css("-webkit-transition", "opacity .5s linear, -webkit-transform .5s linear");

        //$(div).css("-webkit-transition", "-webkit-transform .8s linear");
    }

    function preloadPictures(pictureUrls, ci, callback) {
        var i,
            j,
            loaded = 0;

        for (i = 0, j = pictureUrls.length; i < j; i++) {
            (function (img, src) {
                img.onload = function () {
                    if (++loaded == pictureUrls.length && callback) {
                        callback();
                    }
                    ci(loaded);
                };

                // Use the following callback methods to debug
                // in case of an unexpected behavior.
                img.onerror = function () {};
                img.onabort = function () {};

                img.src = src;
            } (new Image(), pictureUrls[i]));
        }
    };

    return {
        load: load,
        play: play
    }

}(window));