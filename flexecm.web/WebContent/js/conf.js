var cu = null;

$(document).ready(function() {
	weiboConfig();
});

function getReposts() {
	navOn("reposts");
	show_pendding();
	$.getJSON("/service/repost/my", {}, function(list) {
		hide_pendding();
		$("#repost-list tr.cloned").remove();
		$("#repost-list").show();
		for ( var i = 0; i < list.length; i++) {
			var repost = list[i];
			
			var cloned = $("#repost-list tr.template").clone();
			cloned.removeClass("template").addClass("cloned");
			
			cloned.data("repost", repost);
			
			cloned.find("td.dav").html(repost.post.dav);
			cloned.find("td.content").html(repost.post.desc);
			cloned.find("td.time").html(humTime(repost.t));
			cloned.find("td.code").html(repost.code);
			if (repost.post.status==1) {
				cloned.find("td.status").html("即将抽奖");
			} else {
				cloned.find("td.status").html("已结束");
			}
			
			cloned.find("a.view").show();
			cloned.find("a.view").attr("href", "/dav/i.html?" + repost.seq);
			
			$("#repost-list table").append(cloned);
		}
	});
}

function weiboConfig() {
	$.getJSON("/service/user/info", {}, function(data) {
		if (data.cu==null) {
			alert("你未登陆(或登录已超时)");
			location.href = "/";
			return;
		} else {
			navOn("weibo");
			$("#weibo-config").show();
			$("#weibo-head").attr("src", data.al);
			$("#weibo-name").html(data.rn);
		}
	});
}

function cancelWeibo() {
	if (confirm("您将取消微博对神购网的授权，同时清除所有神购网的个人记录，是否确定？")) {
		location.href = "/service/weibo/cancel";
	}
}

function postConfig() {
	navOn("post");
	$("#receive-config").show();
	
	show_pendding();
	
	$.getJSON("/service/repost/receiveLocation", {}, function(data) {
		hide_pendding();
		$("#post-receiver").val(data.rn);
		$("#post-location-detail").val(data.detail);
		$("#post-mobile").val(data.mobile);
		$("#post-phone").val(data.phone);
	});
}

function updatePostConfig() {
	show_pendding();
	$.post("/service/repost/receiveLocation", {
		"rn": $("#post-receiver").val(),
		"detail": $("#post-location-detail").val(),
		"mobile": $("#post-mobile").val(),
		"phone": $("#post-phone").val()
	}, function() {
		hide_pendding("地址已更新");
	});
}


function getGifts() {
	navOn("reposts");
	show_pendding();
	$.getJSON("/service/repost/gift", {}, function(list) {
		hide_pendding();
		$("#repost-list tr.cloned").remove();
		$("#repost-list").show();
		
		for ( var i = 0; i < list.length; i++) {
			var repost = list[i];
			
			var cloned = $("#repost-list tr.template").clone();
			cloned.removeClass("template").addClass("cloned");
			
			cloned.data("repost", repost);
			
			cloned.find("td.dav").html(repost.post.dav);
			cloned.find("td.content").html(repost.post.desc);
			cloned.find("td.time").html(humTime(repost.t));
			cloned.find("td.code").html(repost.code);
			cloned.find("td.status").html("已中奖");
			
			cloned.find("a.view").show();
			cloned.find("a.view").attr("href", "/dav/i.html?" + repost.seq);
			
			$("#repost-list table").append(cloned);
		}
	});
} 

function getBooks() {
	navOn("books");
	show_pendding();
	$("#books-list").show();
	$.getJSON("/service/books", {}, function(list) {
		hide_pendding();
		var total = 0;
		$("#books-list table tr.cloned").remove();
		for ( var i = 0; i < list.length; i++) {
			var book  = list[i];
			
			total ++;
			var cloned = $("#books-list tr.template").clone();
			cloned.removeClass("template").addClass("cloned");
			cloned.find(".title").html(book.sale.title);
			cloned.find(".time").html(humanity_time(book.sale.time));
			cloned.find(".code").html(book.o);
			cloned.find("a.go").show().attr("href", "/book.html?id=" + book.m);
			$("#books-list table").append(cloned);
		}
	});
}

function getDeals() {
	navOn("deals");
	show_pendding();
	$("#deals-list").show();
	$.getJSON("/service/deals", {}, function(list) {
		hide_pendding();
		var total = 0;
		$("#books-list table tr.cloned").remove();
		for ( var i = 0; i < list.length; i++) {
			var book  = list[i];
			total ++;
			var cloned = $("#books-list tr.template").clone();
			cloned.removeClass("template").addClass("cloned");
			cloned.find(".title").html(book.sale.title);
			cloned.find(".time").html(humanity_time(book.sale.time));
			cloned.find(".code").html(book.o);
			cloned.find("a.go").show().attr("href", "/book.html?id=" + book.m);
			$("#books-list table").append(cloned);
		}
	});
}


function humanity_time(millsec) {
	var date = new Date(millsec),
	diff = ((date.getTime() - (new Date()).getTime()) / 1000),
	day_diff = Math.floor(diff / 86400),
	today = (date.getDate()== (new Date()).getDate());
	
	if (diff<0) {
		return "已结束";
	}  else  if (day_diff==0) {
		if (today) {
			return "今天" + date.getHours() + ":" + ((date.getMinutes()<10)? ("0" + date.getMinutes()) : date.getMinutes());
		} else {
			return "明天" + date.getHours() + ":" + ((date.getMinutes()<10)? ("0" + date.getMinutes()) : date.getMinutes());
		}
	} else  {
		return (date.getMonth()+1) + "月" + date.getDate() + " 日" + date.getHours() + ":" + ((date.getMinutes()<10)?("0" + date.getMinutes()): date.getMinutes());
	} 
}




function formate_time(time) {
	var d = new Date(time);
	return d.getFullYear() + "-" + (d.getMonth()+1) + "-" + d.getDate() + " " + d.getHours() + ":" + ((d.getMinutes()<10)?("0" + d.getMinutes()): d.getMinutes()); 
}

function navOn(cl) {
	$(".view").hide();
	$("#" + cl).show();
	$(".nav li.link").removeClass("on");
	$(".nav li[for='" + cl + "']").addClass("on");
	$(".nav li." + cl).addClass("on");
}

function show_pendding() {
	$("#pendding").show();
}

function hide_pendding() {
	$("#pendding").hide();
}

function humTime(millsec) {
	var date = new Date(millsec),
		diff = (((new Date()).getTime() - date.getTime()) / 1000),
		day_diff = Math.floor(diff / 86400);
			
	if ( isNaN(day_diff) || day_diff < 0 || day_diff >= 31 )
		return;
	if (day_diff>=31) {
		return date.getFullYear() + "年" + (date.getMonth()+1) + "月" + date.getDate() + "日";
	}
	return day_diff == 0 && (
			//diff < 60 && "刚刚" ||
			diff < 120 && "1 分钟前" ||
			diff < 3600 && Math.floor( diff / 60 ) + " 分钟前" ||
			diff < 7200 && "1 小时前" ||
			diff < 86400 && Math.floor( diff / 3600 ) + " 小时前") ||
		day_diff == 1 && ("昨天" + date.getHours() + ":" + (date.getMinutes()<10?("0"+date.getMinutes()):date.getMinutes())) ||
		day_diff < 7 && day_diff + " 天前" ||
		day_diff < 31 && Math.ceil( day_diff / 7 ) + " 周前";
}



(function($){
    $.fn.hoverDelay = function(options){
        var defaults = {
            hoverDuring: 500,
            outDuring: 500,
            hoverEvent: function(){
                $.noop();
            },
            outEvent: function(){
                $.noop();
            }
        };
        var sets = $.extend(defaults,options || {});
        var hoverTimer, outTimer;
        return $(this).each(function(){
            $(this).hover(function(){
                clearTimeout(outTimer);
                hoverTimer = setTimeout(sets.hoverEvent, sets.hoverDuring);
            },function(){
                clearTimeout(hoverTimer);
                outTimer = setTimeout(sets.outEvent, sets.outDuring);
            });
        });
    }
})(jQuery);
