
var gift = null;

$(document).ready(function() {
	showGifts();
});

var gift = null;
function showGifts() {
	
	$(".pend").show();
	$("#gift-info").hide();
	
	var wbid = query_string("id");
	if (wbid==null) {
		location.href =  "/404.html";
	}
	
	$.getJSON("/service/repost", 
		{
			"wbid": wbid,
			"limit": 70
		}, function(data) {
			$(".pend").hide();
			$("#gift-info").show();
			
			gift = data;
			
			$("#gift-name").html(gift.prize);
			$(".uname").html(gift.from.sn);
			$(".mt").html(gift.desc);
			$("div.u-pic img").attr("src", gift.from.al);
			
			var cloned = $("#gift-info");
        		
    		cloned.find(".status").html(gift.text.httpHtml());
    		
    		cloned.find(".repost").html(gift.reposts);
    		if (gift.mpic!=null) {
    			cloned.find(".preview").css("background-image", "url('" + gift.lpic + "')");
    		}
    		cloned.find("#linkweibo").attr("href", "http://api.weibo.com/2/statuses/go?id=" + gift._id + "&uid=" + gift.from.uid);
    		
    		if (gift.cu=="3244273155" || gift.cu==gift.from.uid) {
    			cloned.find(".admin").show();
    			initLuckyBtn(cloned);
    		}
    		
    		for(var i=0; i<gift.list.length&&i<8; i++) {
    			var fans = gift.list[i];
    			
    			var fandiv = $("#gift-info .fans .template").clone();
    			fandiv.removeClass("template").addClass("fan");
    			fandiv.find("img").attr("src", fans.al);
    			fandiv.find(".sn").html(fans.rn);
    			
    			$("#gift-info .fans").append(fandiv);
    		}
    		
    		if (gift.active) {
    		} else {
    			cloned.find("div.result").show();
    			
    			if (gift.fansNames!=null) {  //成功完成抽奖的情况
    				var rns = gift.fansNames.split(",");
    				cloned.find("div.result .luck_rns").html(rns[0]);
    				if (rns.length>1) {
    					cloned.find("div.result .luck_rns_count").html( "等" + rns.length + "人");
    				} 
    				cloned.find("a.luck_detail").click(function() {
    					var gift = $(this).parents("div.gift").data("gift");
    					showLuckDetailDialog(gift);
    				});
    			} else {  //被手动关闭
    				cloned.find("div.result").html("已关闭");
    			}
    		}
	});
}

var bilateralUsers = null;

function atBilateral() {
	
	if (bilateralUsers==null) {
		WB2.anyWhere(function(W){
			W.parseCMD("/friendships/friends/bilateral.json", function(sResult, bStatus){
				bilateralUsers = sResult.users;
				appendRandom();
			},
			{
				"uid": gift.cu.uid
			},
			{
				method: 'get'
			});
		});
	} else {
		appendRandom();
	}
	
	function appendRandom() {
		if (bilateralUsers.length==0) {
			alert("你没有互粉的好友, 难道就是传说中的帅到没朋友?");
			return;
		}
		
		var i = Math.floor(Math.random()*bilateralUsers.length);
		$("#repost_status").val($("#repost_status").val() + ",@" + bilateralUsers[i].screen_name);
	}
}

function initLuckyBtn(cloned) {
	
	//以下初始化抽奖按钮
	cloned.find("a.btn_lucky_go").show();
	
	cloned.find("a.btn_lucky_go").click(function() {
		$("#lucy_go_contaner").show();
		
		$("#lucy_go_contaner ul.lucky_method li.options").click(function() {
			$("#lucy_go_contaner ul.lucky_method li.options").removeClass("selected");
			$(this).addClass("selected");
		});
		
		$.getJSON("/service/repost/export", {
			wbid: gift._id
		}, function(list) {
			gift.total = list.length;
			gift.list = list;
			renderFansWall(gift.list);
		});
	});
}

function renderFansWall(list) {
	$("#repost-wall .luckier").remove();
	
	for(var i=0; i<list.length|| i<70; i++) {
		var repost = list[i];
		var cloned = $("#lucy_go_contaner ul.fans-wall li.template").clone();
		cloned.removeClass("template").addClass("luckier");

		cloned.find("img.head").attr("src", repost.al);
		cloned.find(".name").html(repost.rn);
		
		cloned.find("img.head").attr("title", formate_time(repost.created));
		
		cloned.data("index", i);
		
		cloned.click(function() {
			var index = parseInt($(this).data("index"));
			gift.total --;
			gift.list.splice(index, 1);
			$("#lucker-wall li").remove();
			renderFansWall(gift.list);
		});
		
		$("#repost-wall").append(cloned);
	}
}

function luckyGo() {
	var text  = "截至今晚8点半,";
	text += "共有" + gift.total + "个有效转发," 
	text += "当日福彩3D中奖号码 +000为" + gift.seed + ","
	text += "二者相除取余为" +  (gift.seed % gift.total + 1) + "," 
	text += "即第" +  (gift.seed % gift.total + 1) + "位转发用户是 中奖， TA就是 @" + gift.list[gift.seed % gift.total].rn  + " 恭喜您了！";  
	
	var luckier = gift.list[gift.seed % gift.total];
	
	var cloned = $("#lucy_go_contaner ul.fans-wall li.template").clone();
	cloned.removeClass("template").addClass("luckier");
	
	cloned.find(".front img.head").attr("src", luckier.al);
	cloned.find(".front .name").html(luckier.rn);
	cloned.find("i.seq").remove();
	
	$("#lucker-wall").append(cloned);
	
	$("#post-result textarea").val(text);
}

function importWeibo() {
	
	WB2.anyWhere(function(W){
	    W.parseCMD('/statuses/user_timeline.json', function(oResult, bStatus) {
	        if(bStatus) {
	        	var weibos = oResult.statuses;
	        	for(var i=0; i<weibos.length; i++) {
	        		var status = weibos[i];
	        		
	        		if (status.text.indexOf(gift.gift)>-1) {
	        			if (!in_array(status.idstr, gift.list, "wbid"))  {
		        			$.post("/service/gift/add", {
		        				"wbid": status.id,
		        				"mpic": status.bmiddle_pic,
		        				"lpic": status.original_pic,
		        				"pic": status.thumbnail_pic,
		        				"text": status.text,
		        				"from": status.user.id,
		        				"sn": status.user.screen_name,
		        				"head": status.user.avatar_large,
		        				"desc": status.user.description,
		        				"gender": status.user.gender,
		        				"created": parse_time(status.created_at).getTime()
		        			}, function(m) {
		        				showGifts();
		        			});
	        			}
	        		}
	        	}
	        }
	    }, {}, {
	        method : 'get'
	    });
	});
}

function goImportOther() {
	$(".tablist li").removeClass("current");
	$("#tab_import_others").addClass("current");
	
	$(".container").hide();
	
	$("#import-list").show();
	
}

function importRead() {
	
	WB2.anyWhere(function(W){
	    W.parseCMD('/statuses/show.json', function(oResult, bStatus) {
	        if(bStatus) {
	        	var status = oResult;
	        	
    			$.post("/service/gift/add", {
    				"wbid": status.id,
    				"mpic": status.bmiddle_pic,
    				"lpic": status.original_pic,
    				"pic": status.thumbnail_pic,
    				"text": status.text,
    				"from": status.user.id,
    				"sn": status.user.screen_name,
    				"head": status.user.avatar_large,
    				"desc": status.user.description,
    				"gender": status.user.gender,
    				"created":  parse_time(status.created_at).getTime()
    			}, function(m) {
    				alert("数据已导入");
    			});
	        } else {
	        	alert("错误的weibo");
	        }
	    }, {
	    	id: $("#import_wb_id").val()
	    }, {
	        method : 'get'
	    });
	});
}

function repostCommit() {
	//先转发  后关注
	WB2.anyWhere(function(W){
	    W.parseCMD("/statuses/repost.json", function(sResult, bStatus){
	    	$.post("/service/repost/local/add", {
	    		"rid": sResult.id,
	    		"wbid": gift._id,
	    		"text": sResult.text,
	    		"created": parse_time(sResult.created_at).getTime(),
	    		"uid": sResult.user.id,
	    		"sn": sResult.user.screen_name,
	    		"head": sResult.user.profile_image_url
	    	}, function() {
	    		$("#repost_container").slideUp('fast');
	    	});
	    	
	    	if (gift.cu!=gift.from.uid) {
	    		WB2.anyWhere(function(W){
	    			W.parseCMD("/friendships/create.json", function(sResult, bStatus){
	    				
	    			}, 
	    			{
	    				uid: gift.from,
	    			},
	    			{
	    				method: 'post'
	    			});
	    		});
	    	}
	    }, 
	    {
	        id: gift._id,
	        status: encodeURI($("#repost_status").val()),
	        is_comment:3
	    },
	    {
	        method: 'post'
	    });
	});
}

function showDialog(id, width) {
	$(".thickdiv").show();
	$("#" + id).show();
	
	$("#" + id).css("left", parseInt(($(window).width()-width)/2));
	$("#" + id).css("width", width);
}

function closeDialog() {
	$(".thickdiv").hide();
	$(".thickbox").hide();
}

function hideFarward() {
	$("#repost_container").slideUp('fast');
}

String.prototype.httpHtml = function(){
	var reg = /(http:\/\/|https:\/\/)((\w|=|\?|\.|\/|&|-)+)/g;
	return this.replace(reg, '<a href="$1$2" target="_blank">$1$2</a>');
};


function parse_time(str) {
	var months = 'Jan_Feb_Mar_Apr_May_Jun_Jul_Aug_Sep_Oct_Nov_Dec'.split('_');
	var parts = str.split(" ");
	var year = parts[5];
	
	var month = $.inArray(parts[1], months);
	var date = parts[2];
	var seqs = parts[3].split(":");
	
	var hours = seqs[0];
	var mins = seqs[1];
	var sec = seqs[2];
	
	return new Date(year, month, date, hours, mins, sec);
}

function query_string(key) {
	return (document.location.search.match(new RegExp("(?:^\\?|&)"+key+"=(.*?)(?=&|$)"))||['',null])[1];
}


function in_array(value, array, key) {
	for(var i=0; i<array.length; i++) {
		if (array[i][key]==value) {
			return true;
		}
	}
}

function formate_time(time) {
	var d = new Date(time);
	return d.getFullYear() + "-" + (d.getMonth()+1) + "-" + d.getDate() + " " + d.getHours() + ":" + ((d.getMinutes()<10)?("0" + d.getMinutes()): d.getMinutes()); 
}

