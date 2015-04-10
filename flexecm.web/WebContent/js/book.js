var countdown = false;
var seed = 0;
var bottom = 0;

var currentSale = null;

var books = [];
var delas = [];

$(document).ready(function() {
	var id = query_string("id");
	if (id==null || id=="" || id=="null") {
		$("#hide-all span").html("指定的抢购不存在");
		return;
	}
	$.getJSON("/service/sale", 
			{"id": id}, function(sale) {
		currentSale = sale;
		$("#main").show();
		
		$("#hide-all").hide();
		$("#sale-info .title").html(sale.title);
		$("#sale-info .subtitle").html(sale.subtitle);
		$("#sale-info .bigprice").html(sale.price);
		$("#sale-info .original").html(sale.oprice);
		$("#sale-info .buydate").html(formate_time(sale.time));
		$("#sale-info .saleurl").attr("href", sale.url);
		
		$.get("/service/seller/sale/content", {
				"id": sale.content
			}, function(content) {
				$("#content").html(content);
			}
		);
		if (sale.preview) {
			$(".splash img").attr("src", "/service/preview?id=" + sale.preview);
		}
		if (sale.cu!=null) {
			$("#site-map .item.user").show();
			$("#site-map .uname").html(sale.cu);
		} else {
			$("#site-map .item.login").show();
		}
		
		
		var now = new Date();
		var dura = sale.time - now.getTime();
		if (dura<0) {
			$("#sale-info .time-left .day").html("0");
			$("#sale-info .time-left .hours").html("0");
			$("#sale-info .time-left .minutes").html("0");
		} else {
			$("#sale-info .time-left .day").html(time_dura_days(dura));
			$("#sale-info .time-left .hours").html(time_dura_hours(dura));
			$("#sale-info .time-left .minutes").html(time_dura_min(dura));
		}
		
		if (sale.online==false) { //0.未上线 则按钮为灰。
			$("#btn_book").addClass("disabled");
			$("#btn_book").html("审核后可预约");
			return;
		}
		
		if (!sale.o) { //未预约
			if (sale.until<now.getTime()) { //现在已过预约期限
				$("#btn_book").addClass("disabled");
				$("#btn_book").html("预约已结束");
			} else {
				$("#btn_book").html("预约");
			}
			return;
		} else {
			$("#btn_book").addClass("disabled");
			$("#btn_book").html("已预约");
			$("#book_code").html("预约号 " + sale.o);
		}
		
		if (dura<0) {  
			if (sale.f) {
				$("#btn_book").addClass("disabled");
				$("#btn_book").html("已售罄");
				$("#sale-info .time-left").html("已经抢购结束");
				$("#sale-info .time-left").css("background-color", "#888");
			}
			
			if (sale.d) {
				$("#btn_book").addClass("disabled");
				$("#btn_book").html("抢购成功");
				$("#book_code").html("神购码: " + sale.d);
				return;
			} else {
				if (!sale.f) {
					$("#btn_book").removeClass("disabled");
					$("#btn_book").html("抢购");
					$("#btn_book").unbind();
					$("#btn_book").click(function() {
						buy();
					});
				}
			}
		} else {
			$("#sale-info .time-left .day").html(time_dura_days(dura));
			$("#sale-info .time-left .hours").html(time_dura_hours(dura));
			$("#sale-info .time-left .minutes").html(time_dura_min(dura));
			
			if (in_8_hour(new Date(sale.time))) { //还有8小时并已预约则开始倒数
				timeCountDown();
			}
		}
	}).fail(function() {
		$("#hide-all span").html("指定的抢购不存在");
	});
	
	$("#books-container").hoverDelay({
				hoverDuring: 300,
				outDuring: 500,
				hoverEvent: function(){
					$("#books-container").find("dd").show();
					if($("#books-container").data("list")==null) {
						$.getJSON("/service/books", {}, function(list) {
							$("#books-container").data("list", list);
							var total = 0;
							for ( var i = 0; i < list.length; i++) {
								var book  = list[i];
								if (book.sale.time<new Date().getTime()) continue;
								total ++;
								var cloned = $("#books-container li.sale.template").clone();
								cloned.removeClass("template");
								cloned.find(".title a").html(book.sale.title);
								cloned.find(".price").html(book.sale.price);
								cloned.find(".title a").attr("href", "/book.html?id=" + book.m);
								cloned.find(".time").html(humanity_time(book.sale.time));
								$("#books-container div.list ul").append(cloned);
							}
							
							$("#books-container .total").html(total);
						});
					}
				},
		    outEvent: function(){
		    	$($("#books-container")).find("dd").hide();
		    }
	});
	
	$("#deals-container").hoverDelay({
		hoverDuring: 300,
		outDuring: 500,
		hoverEvent: function(){
			$("#deals-container").find("dd").show();
			if($("#deals-container").data("list")==null) {
				$.getJSON("/service/deals", {}, function(list) {
					$("#deals-container").data("list", list);
					for ( var i = 0; i < list.length; i++) {
						var book  = list[i];
						var cloned = $("#deals-container li.sale.template").clone();
						cloned.removeClass("template");
						cloned.find(".title a").html(book.sale.title);
						cloned.find(".price").html(book.sale.price);
						cloned.find(".title a").attr("href", "/book.html?id=" + book.m);
						cloned.find(".time").html(book.order);
						$("#deals-container div.list ul").append(cloned);
					}
					$("#deals-container .total").html(list.length);
				});
			}
		},
	    outEvent: function(){
	    	$($("#deals-container")).find("dd").hide();
	    }
	});
});

function timeCountDown() {
	var remains = currentSale.time - new Date().getTime();
	if (remains<0) {
		$("#btn_book").removeClass("disabled");
		$("#btn_book").html("抢购");
		$("#btn_book").unbind();
		$("#btn_book").click(function() {
			buy();
		});
	} else {
		$("#sale-info .time-left .day").html(time_dura_days(remains));
		$("#sale-info .time-left .hours").html(time_dura_hours(remains));
		$("#sale-info .time-left .minutes").html(time_dura_min(remains));
		$("#btn_book").html(time_dura_hours(remains) + "小时" +time_dura_min(remains) + "分" +time_dura_second(remains) + "秒后");
		setTimeout(timeCountDown, 1000);
	}
}

function buy() {
	if ($("#btn_book").hasClass("disabled")) return;
	
	$("#btn_book").addClass("disabled");
	$("#btn_book").html("抢购中，请稍候");
	
	if (currentSale.d==null) {
		$.post("/service/buy", {
			"id": currentSale.seq
		}, function(data) {
			var r = JSON.parse(data);
			if (r.status==200) {
				$("#btn_book").addClass("disabled");
				$("#btn_book").html("抢购成功");
				$("#book_code").html("神购码: " + r.o);
			} else if (r.status==400) {
				alert("还未到抢购时间！  ");
				location.href =  location.href;
			} else {
				$("#btn_book").addClass("disabled");
				$("#btn_book").html("已售罄");
			}
		}).fail(function() {
				$("#btn_book").addClass("disabled");
				$("#btn_book").html("已售罄");
		});
	}
	
}

function in_8_hour(d) {
	return (d.getTime() - new Date().getTime())<8*60*60*1000; 
}

function time_dura_days(dura) {
	return Math.floor(dura/(1000*60*60*24));
}
function time_dura_hours(dura) {
	return Math.floor((dura%(1000*60*60*24))/(1000*60*60));
}
function time_dura_min(dura) {
	return Math.floor((dura%(1000*60*60))/(1000*60));
}
function time_dura_second(dura) {
	return Math.floor((dura%(1000*60))/(1000));
}


function book() {
	if ($("#btn_book").hasClass("disabled")) return;
	if (currentSale ==null) {
		alert("请等待页面加载完成");
		return;
	}
	
	if (currentSale.seq==null) {
		alert("请通过审批后才能预约");
		return;
	}
	if (currentSale.cu==null) {
		loginDialog();
		return;
	}
	
	if($("#agreement").attr("checked")!="checked") {
		alert("请勾选阅读并同意 预约及购买规则");
		return;
	}
	var id = currentSale.seq;
	
	$("#btn_book").addClass("disabled");
	$("#btn_book").html("正在预约");
	$.post("/service/book", {
		"id": id
	}, function(data) {
		location.href  = location.href;
	}).fail(function() {
		$("#btn_book").html("预约");
		$("#btn_book").removeClass("disabled");
	});
}

function loginDialog() {
	$("#dialog").show();
}

function closeDialog() {
	$("#dialog").hide();
}

function formate_time(time) {
	var d = new Date(time);
	return d.getFullYear() + "-" + (d.getMonth()+1) + "-" + d.getDate() + " " + d.getHours() + ":" + ((d.getMinutes()<10)?("0" + d.getMinutes()): d.getMinutes()); 
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


function query_string(key) {
	return (document.location.search.match(new RegExp("(?:^\\?|&)"+key+"=(.*?)(?=&|$)"))||['',null])[1];
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