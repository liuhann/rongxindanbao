/**
 * Created by Administrator on 2015/7/2.
 */
$(function() {
    var size = $(window).width() / 38;
    $("html").css("font-size", size);
    $(".loading").hide();
    $(".wrapper").show();
});



function runToNumber(div, money) {
    if (money==0) {
        $(div).html(formatMoney(money));
        return;
    }
    var stacks = [];
    for(var i=10; i>=1; i--) {
        stacks.push(money/10 *i);
    }
    showStacks(stacks, div);
}

function showStacks(stacks, div) {
    if (stacks.length==0) return;
    var p = stacks.pop();
    $(div).html(formatMoney(p));
    setTimeout(function() {
        showStacks(stacks, div);
    }, 50);

}

function formatMoney(number, places, symbol, thousand, decimal) {
    number = number || 0;
    places = !isNaN(places = Math.abs(places)) ? places : 2;
    symbol = symbol !== undefined ? symbol : "";
    thousand = thousand || ",";
    decimal = decimal || ".";
    var negative = number < 0 ? "-" : "",
        i = parseInt(number = Math.abs(+number || 0).toFixed(places), 10) + "",
        j = (j = i.length) > 3 ? j % 3 : 0;
    return symbol + negative + (j ? i.substr(0, j) + thousand : "") + i.substr(j).replace(/(\d{3})(?=\d)/g, "$1" + thousand) + (places ? decimal + Math.abs(number - i).toFixed(places).slice(2) : "");
}


$.fn.bindtouch = function(cb) {
    attachEvent($(this), cb);
};

function attachEvent(src, cb) {
    $(src).unbind();
    var isTouchDevice = 'ontouchstart' in window || navigator.msMaxTouchPoints;
    if (isTouchDevice) {
        $(src).bind("touchstart", function(event) {
            $(this).data("touchon", true);
            $(this).addClass("pressed");
        });
        $(src).bind("touchend", function() {
            $(this).removeClass("pressed");
            if ($(this).data("touchon")) {
                cb.bind(this)();
            }
            $(this).data("touchon", false);
        });
        $(src).bind("touchmove", function() {
            $(this).data("touchon", false);
            $(this).removeClass("pressed");
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
