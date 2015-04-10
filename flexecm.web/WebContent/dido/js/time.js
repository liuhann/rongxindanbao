var office = new Object();

office.date = new Date();

office.preday = function() {
	office.date = new Date(office.date.getTime() - 24*60*60*1000); 
	if (office.currentTab!=null) {
		office.currentTab.load();
	}
};

office.nextday = function() {
	office.date = new Date(office.date.getTime() + 24*60*60*1000);
	if (office.currentTab!=null) {
		office.currentTab.load();
	}
};

office.switchView = function(v) {
	$('div.pages div.views').slideUp('fast');
	$(v).slideDown('fast');
	$('div.pagebar div.back').slideDown('fast');
	$('div.pagebar div.addItem').slideUp('fast');
};

office.switchBack = function() {
	$('div.pages div.views').slideUp('fast');
	$('div.pages div.list').slideDown('fast');
	$('div.pagebar div.back').slideUp('fast');
	$('div.pagebar div.addItem').slideDown('fast');
};


office.today = function() {
	office.date = new Date();
	if (office.currentTab!=null) {
		office.currentTab.load();
	}
};

office.getDateFormat = function(dd) {
	return dd.getFullYear() + "-" + (dd.getMonth()+1) + "-" + dd.getDate();
};


office.currentTab = null;

office.currentUser = null;

office.time = {
	items:null,
	load:function() {
		office.currentTab = office.time;
		
		$('div.running div.timeOper a').live('click', function(data) {
			office.time.stopItem($(this).attr('id'));
		});
		$('div.pending div.timeOper a').live('click', function(data) {
			office.time.startItem($(this).attr('id'));
		});
		$('a.details').live('click', function(data) {
			office.time.itemDetailView($(this).parents('div.timeItem').data("timedata"));
		});
		
		
		if (isIE6()) {
			$('#timelist').css("width",600);
		}
		
		var vNum = Math.random();
		vNum = Math.floor(vNum*10);
		
		$('#hintinfo p').html(office.time.hitKeys[vNum] + "<br>" + office.time.hitInfos[vNum]);
		
			
		$.getJSON("/service/key/get",
				{"r":new Date().getTime(),
					"kind":"timer",
					"key":"items"
				},
				
				function(data) {
					office.currentUser = data.user;
					if (office.currentUser.indexOf('guest.')>-1) {
						//匿名用户
						$('#loginLink').show();
						$('#helloLink').hide();
						$('#logoutLink').hide();
					} else {
						$('#loginLink').hide();
						$('#helloLink').html('您好 ' + office.currentUser);
						$('#logoutLink').show();
						$('#helloLink').show();
					}
					
					office.time.items = $.parseJSON(data.value);
					
					if (office.time.makeAutoStop()) {
						office.time.callToUpdate(
								function() {
								}
						);
					}

					office.time.displayStastics();
					office.time.listDay(office.getDateFormat(office.date));
					
				});
		
		/*
		$.getJSON("/service/office/time/info", 
				{"r":new Date().getTime()},
				function(data){
					$('#yesterdayTime').html(office.time.formatDura(data.yesterday));
					$('#weekTime').html(office.time.formatDura(data.week));
					$('#totalTime').html(office.time.formatDura(data.total));
					$('#itemCount').html(data.count);
				}
		);
		*/
	},
	
	showed: 0 ,
	showMore:function() {
		var c = 0;
		for ( var i = office.time.items.length-1-office.time.showed; i >=0; i--) {
			
			office.time.addMoreUITime(office.time.items[i]);
			c++;
			
			if (c==10) break;
		}
	},
	
	listDay:function(date) {
		$("div.timeItem").remove();
		
		var filtered = filterFromArrayByKey(office.time.items, "date", date);
		var hasItem = false;

		for ( var i = 0; i < filtered.length; i++) {
			hasItem = true;
			office.time.addUITime(filtered[i]);
		}
		if (!hasItem) {
			$('#hintinfo').show();
		} else {
			$('#hintinfo').hide();
		}
		
		$('#addTimeDiv').fadeOut(300);
		
		if (!office.time.schedued) {
			office.time.updateTime();
			office.time.schedued = true;
		}
		//$('div.flipPageBar span.currentDaySpan').html(office.getDateFormat(office.date));
	},
	schedued:false,
	
	displayStastics : function() {
		
		var yesterDayTotal = 0;
		var theWeekTotal = 0;
		var allTotal = 0;
		
		var yesterDayStart = new Date(new Date().getTime() - 24 * 60 * 60 * 1000).setHours(0, 1, 1, 1);
		var todayStart = yesterDayStart + 24 * 60 * 60 * 1000;
		
		var weekStart =  new Date(new Date().getTime() - new Date().getDay()*24 * 60 * 60 * 1000).setHours(0, 1, 1, 1);
		
		for ( var i = 0; i < office.time.items.length; i++) {
			if (office.time.items[i].created>yesterDayStart && office.time.items[i].created<todayStart) {
				yesterDayTotal +=office.time.items[i].dura;
			}
			
			if (office.time.items[i].created>weekStart) {
				theWeekTotal += office.time.items[i].dura;
			}
			
			allTotal += office.time.items[i].dura;
			
		}
		$('#itemCount').html(office.time.items.length);
		
		$('#yesterdayTime').html(office.time.formatDura(yesterDayTotal));
		$('#weekTime').html(office.time.formatDura(theWeekTotal));
		$('#totalTime').html(office.time.formatDura(allTotal));
	},
	
	addMoreUITime: function(o) {
		var timed = $("tr.moreTemplate").clone();
		timed.removeClass('moreTemplate').addClass("moreTimeItem");
		timed.show();
		
		timed.find('td.dura').html(office.time.formatDura(o.dura));
		timed.find('td.desc').html(o.desc);
		timed.find('td.date').html(office.getDateFormat(new Date(o.created)));
		
		$('#moreInfoTable').append(timed);
		office.time.showed ++;
	},
	
	addUITime: function(o) {
		var timed = $('div.timeTemplate').clone();
		timed.removeClass('timeTemplate').addClass("timeItem");
		timed.attr("id", "item-" + o.id);
		timed.data("timedata", o);
		
		
		$('#showMore').before(timed);
		//$('#timelist').append(timed);
		
		if (isIE6()) {
			timed.css("width", $('#timelist').css("width"));
		}
		
		timed.find('div.timeOper a').attr("id", o.id);
		
		var dura = 0;
		if (o.laststart!=0) {  //表示正在运行的任务
			$('div.running').removeClass("running").addClass("pending");
			timed.addClass("running");
			dura = o.dura + (new Date().getTime()-o.laststart);
		} else { 
			timed.addClass("pending");
			dura = o.dura;
		}
		timed.find('div.timeOper span').html(office.time.formatDura(dura));
		timed.find('span.warning').hide();
		
		timed.find('span.timeStart').html("启动于 " + office.time.formatHour(o.created));
		timed.find('span.timePending').html(" 中断了" + ((o.checks==null)?0:o.checks.length) + "次");
		
		if(isIE6()) {
			timed.find('span.timeStart').hide();
			timed.find('span.timePending').hide();
		}
		
		timed.find('span.desc').html(o.desc);
		timed.fadeIn('fast');
		
		office.time.showed ++;
	},
	
	itemDetailView:function(data) {
		office.time.currentEdit = data;
		$('#editTime').val(data.desc);
		$('#autoStop').val((data.autostop==null)?"0":data.autostop);
		$('#editDuraTime').val(office.time.formatDura(data.dura));
		
		office.switchView('div.details');
	},
	
	currentEdit:null,
	
	updateItem: function () {
		if (office.time.currentEdit!=null) {
			
			for ( var i = 0; i < office.time.items.length; i++) {
				if (office.time.items[i].id==office.time.currentEdit.id) {
					office.time.items[i].desc = $('#editTime').val();
					office.time.items[i].dura = office.time.formatedToMill($("#editDuraTime").val());
					
					if (!isNaN($('#autoStop').val())) {
						office.time.items[i].autostop =  $('#autoStop').val();
					}
					
					$('#item-' + office.time.currentEdit.id).find("span.desc").html($('#editTime').val());
					$('#item-' + office.time.currentEdit.id).find("div.timeOper span").html(office.time.formatDura(office.time.items[i].dura));
					break;
				}
			}
			
			office.time.currentEdit = null;
		} else {
			var newItem = {};
			newItem.id = new Date().getTime();
			newItem.created = newItem.id; 
			newItem.desc = $('#editTime').val();
			
			if (isNaN($('#autoStop').val())) {
				newItem.autostop = 8;
			} else {
				newItem.autostop =  $('#autoStop').val();
			}
			
			newItem.dura = office.time.formatedToMill($("#editDuraTime").val());
			newItem.date = office.getDateFormat(new Date());
			newItem.checks = [];
			newItem.laststart = 0;
			office.time.items.push(newItem);
			office.time.addUITime(newItem);
		}
		
		$('#itemedit').slideUp('fast');
		office.time.callToUpdate(
				function() {
				}
		)
	},
	
	
	removeItem:function() {
		if (office.time.currentEdit!=null) {
			
			office.time.items = removeFromArrayByKey(office.time.items, "id", office.time.currentEdit.id);
			
			office.time.callToUpdate(
					function() {
					}
			)
			$('#item-' + office.time.currentEdit.id).fadeOut("slow");
			$('#itemedit').slideUp('fast');
			office.time.currentEdit = null;
		}
	},
	
	
	showAddTime:function() {
		$('#hintinfo').slideUp('fast');
		$('#itemedit').slideDown('fast');
		//$('#addTimeDiv').fadeIn(300);
	},
	hideAddTime:function() {
		$('#addTimeDiv').fadeOut(300);
	},
	
	adding: false,
	
	
	makeAutoStop:function() {
		var result = false;
		for ( var i = 0; i < office.time.items.length; i++) {
			
			if (office.time.items[i].laststart!=0) { //running ?
				var tillNowDura = new Date().getTime() - office.time.items[i].laststart; 
				var autostop = parseFloat(office.time.items[i].autostop) * 60 * 60 * 1000;
				
				if (tillNowDura>autostop) {
					
					if (office.time.items[i].checks==null) {
						office.time.items[i].checks = [];
					}
					office.time.items[i].checks.push(office.time.items[i].laststart + "-" + (autostop + office.time.items[i].laststart));
					office.time.items[i].dura += autostop; 
					office.time.items[i].laststart = 0;
					result = true;
				}
			}
		}
		
		return result;
	},
	
	
	callToUpdate: function(cb) {
		
		$.post("/service/key/update",
				{
					"kind":"timer",
					"key":"items",
					"value":  JSON.stringify(office.time.items)
				},
				function(data) {
					cb;
				}
		);
	},
	
	arrayStopAll: function() {
		for ( var i = 0; i < office.time.items.length; i++) {
			
			if (office.time.items[i].laststart!=0) {
				if (office.time.items[i].checks==null) {
					office.time.items[i].checks = [];
				}
				
				var tillNowDura = new Date().getTime() - office.time.items[i].laststart;
				
				var autostop = 1000000000000000;
				if (!isNaN(office.time.items[i].autostop)) {
					autostop = parseFloat(office.time.items[i].autostop) * 60 * 60 * 1000;
				}
				
				var added = (tillNowDura>autostop)? autostop:tillNowDura;
				
				office.time.items[i].checks.push(office.time.items[i].laststart + "-" + (added + office.time.items[i].laststart));
				office.time.items[i].dura += added; 
				office.time.items[i].laststart = 0;
				
			}
		}
	},
	
	stopItem: function() {
		
		office.time.arrayStopAll();
		
		office.time.callToUpdate(
				function() {
					//将  中断x次部分的数字更新
					/*
			   		$("div.running div.timeStatics span.timePending").each(function() {
			   			$(this).html(parseInt($(this).html()) + 1);
			   		});
			   		*/
			   		
				}
		)
		$("div.running").removeClass("running").addClass("pending");
	},

	startItem:function(id) {
		
		office.time.arrayStopAll();
		
		for ( var i = 0; i < office.time.items.length; i++) {
			
			if (office.time.items[i].id==id) {
				office.time.items[i].laststart = new Date().getTime();
			}
		}
		
		

		$.post("/service/key/update",
				{
					"kind":"timer",
					"key":"items",
					"value":  JSON.stringify(office.time.items)
				},
				function(data) {
					//将  中断x次部分的数字更新
					/*
					$("div.running div.timeStatics span.timePending").each(function() {
			   			$(this).html(parseInt($(this).html()) + 1);
			   		});
					*/
					$("div.running").removeClass("running").addClass("pending");
			   		

					jQuery.each($("div.timeItem"),
							function() {
								var timedata = $(this).data("timedata");
								if (timedata==null) return;
								
								if (timedata.id==id) {
									$(this).addClass("running").removeClass("pending");
								}
							}
					);
				}
		);
	},
	
	updateTime:function() {
		var total = 0;

		jQuery.each($("div.timeItem"),
			function() {
				var timedata = $(this).data("timedata");
				if (timedata==null) return;
				
				if ($(this).hasClass("running")) {
					var duraString = $(this).find('div.timeOper span').html();
					var duraTime = office.time.formatedToMill(duraString) + 60*1000;
					var autostop = parseFloat(timedata.autostop) * 60 * 60 * 1000;
					
					var tillNow = new Date().getTime() - timedata.laststart;
					
					if (autostop!=0 && tillNow>=autostop) {
						$(this).find('div.timeStatics span.warning').show();
						$(this).removeClass("running").addClass("pending");
						office.time.stopItem();
					}
					
					$(this).find('div.timeOper span').html(office.time.formatDura(duraTime));
					total += duraTime;
				} else {
					total += timedata.dura;
				}
			}
		 );
		$('div.nav div.total').html(office.time.formatDura(total));
		setTimeout('office.time.updateTime()', 60*1000);
	},
	
	
	hideEdit: function() {
		$('#itemedit').slideUp('fast');
		office.time.adding = false;
	},

	formatDura:function(mill) {
		var d3 = new Date(parseInt(mill));
		return ((d3.getDate()-1)*24 + (d3.getHours()-8)) + ":" 
		+ ((d3.getMinutes()<10)?("0"+d3.getMinutes()):d3.getMinutes()); 
	},
	
	formatHour:function (mill) {
		var t = new Date(mill);
		return t.getHours() + ":" +  ((t.getMinutes()<10)?("0"+t.getMinutes()):t.getMinutes());
	},
	
	formatedToMill: function(formated) {
		var sr = formated.split(":");
		if (sr.length!=2) return 0;
		
		return parseInt(sr[0])*60*60*1000 + parseInt(sr[1].charAt(0)=='0'?sr[1].charAt(1):sr[1])*60*1000;
	},
	
	hideInfo: function() {
		$('#hintinfo').slideUp('fast');
	},
	
	hitKeys:[
	     '时间小贴士一.设立明确的目标',
	     '时间小贴士二.学会列清单',
	     '时间小贴士三.做好“时间日志”',
	     '时间小贴士四.制订有效的计划。',
	     '时间小贴士五.遵循20:80定律',
	     '时间小贴士六.安排“不被干扰”时间',
	     '时间小贴士七.确立个人的价值观',
	     '时间小贴士八.严格规定完成期限',
	     '时间小贴士九.学会充分授权',
	     '时间小贴士十.同-类的事情最好一次做完'
	],
	         
	hitInfos:[
	          "时间管理的目的是让你在最短时间内实现更多你想要实现的目标。把手头4到10个目标写出来，找出一个核心目标，并依次排列重要性，然后依照你的目标设定详细的计划，并依照计划进行。",
	          "把自己所要做的每一件事情都写下来，列一张总清单，这样做能让你随时都明确自己手头上的任务。在列好清单的基础上进行目标切割。",
	          "你花了多少时间在哪些事情，把它详细地，记录下来，每天从刷牙开始，洗澡，早上穿衣花了多少时间，早上搭车的时间，平上出去拜访客户的时间，把每天花的时间一一记录下来，做了哪些事，你会发现浪费了哪些时间。当你找到浪费时间的根源，你才有办法改变。",
	          "绝大多数难题都是由未经认真思考虑的行动引起的。在制订有效的计划中每花费1小时，在实施计划中就可能节省3-4小时，并会得到更好的结果。如果你没有认真作计划，那么实际上你正计划着失败。",
	          "用你80%的时间来做20%最重要的事情。生活中肯定会有一些突发困扰和迫不及待要解决的问题，如果你发现自己天天都在处理这些事情，那表示你的时间管理并不理想。一定要了解，对你来说，哪些事情是最重要的，是最有生产力的。",
	          "假如你每天能有一个小时完全不受任何人干扰地思考一些事情，或是做一些你认为最重要的事情，这一个小时可以抵过你一天的工作效率，甚至可能比三天的工作效率还要好。",
	          "假如价值观不明确，就很难知道什么对你是最重要的，当你的价值观不明确时，就无法做到合理地分配时间。时间管理的重点不在管理时间，而在于如何分配时间。你永远没有时间做每件事，但永远有时间做对你来说最重要的事。",
	          "巴金森(C.Noarthcote Parkinson)在其所著的《巴金森法则》中写下这段话“你有多少时间完成工作，工作就会自动变成需要那么多时间。”如果你有一整天的时间可以做某项工作，你就会花一天的时间去做它。而如果你只有一小时的时间可以做这项工作，你就会更迅速有效地在一小时内做完它。",
	          "列出你目前生活中所有觉得可以授权的事情，把它们写下来，找适当的人来授权。",
	          "假如你在做纸上作业，那段时间都做纸上作业；假如你是在思考，用一段时间只作思考；打电话的话，最好把电话累积到某一时间一次把它打完。"
	],
	
	other:null
};

function isIE6() {
	return jQuery.browser.msie && jQuery.browser.version=="6.0";
}



function filterFromArrayByKey(array, key, value) {
	var dumped = [];
	for ( var i = 0; i < array.length; i++) {
		if (array[i][key]==value) {
			dumped.push(array[i]);
		} else {
			
		}
	}
	return dumped;
}

var JSON;if(!JSON)JSON={};(function(){var n="number",m="object",l="string",k="function";"use strict";function f(a){return a<10?"0"+a:a}if(typeof Date.prototype.toJSON!==k){Date.prototype.toJSON=function(){var a=this;return isFinite(a.valueOf())?a.getUTCFullYear()+"-"+f(a.getUTCMonth()+1)+"-"+f(a.getUTCDate())+"T"+f(a.getUTCHours())+":"+f(a.getUTCMinutes())+":"+f(a.getUTCSeconds())+"Z":null};String.prototype.toJSON=Number.prototype.toJSON=Boolean.prototype.toJSON=function(){return this.valueOf()}}var cx=/[\u0000\u00ad\u0600-\u0604\u070f\u17b4\u17b5\u200c-\u200f\u2028-\u202f\u2060-\u206f\ufeff\ufff0-\uffff]/g,escapable=/[\\\"\x00-\x1f\x7f-\x9f\u00ad\u0600-\u0604\u070f\u17b4\u17b5\u200c-\u200f\u2028-\u202f\u2060-\u206f\ufeff\ufff0-\uffff]/g,gap,indent,meta={"\b":"\\b","\t":"\\t","\n":"\\n","\f":"\\f","\r":"\\r",'"':'\\"',"\\":"\\\\"},rep;function quote(a){escapable.lastIndex=0;return escapable.test(a)?'"'+a.replace(escapable,function(a){var b=meta[a];return typeof b===l?b:"\\u"+("0000"+a.charCodeAt(0).toString(16)).slice(-4)})+'"':'"'+a+'"'}function str(i,j){var f="null",c,e,d,g,h=gap,b,a=j[i];if(a&&typeof a===m&&typeof a.toJSON===k)a=a.toJSON(i);if(typeof rep===k)a=rep.call(j,i,a);switch(typeof a){case l:return quote(a);case n:return isFinite(a)?String(a):f;case"boolean":case f:return String(a);case m:if(!a)return f;gap+=indent;b=[];if(Object.prototype.toString.apply(a)==="[object Array]"){g=a.length;for(c=0;c<g;c+=1)b[c]=str(c,a)||f;d=b.length===0?"[]":gap?"[\n"+gap+b.join(",\n"+gap)+"\n"+h+"]":"["+b.join(",")+"]";gap=h;return d}if(rep&&typeof rep===m){g=rep.length;for(c=0;c<g;c+=1)if(typeof rep[c]===l){e=rep[c];d=str(e,a);d&&b.push(quote(e)+(gap?": ":":")+d)}}else for(e in a)if(Object.prototype.hasOwnProperty.call(a,e)){d=str(e,a);d&&b.push(quote(e)+(gap?": ":":")+d)}d=b.length===0?"{}":gap?"{\n"+gap+b.join(",\n"+gap)+"\n"+h+"}":"{"+b.join(",")+"}";gap=h;return d}}if(typeof JSON.stringify!==k)JSON.stringify=function(d,a,b){var c;gap="";indent="";if(typeof b===n)for(c=0;c<b;c+=1)indent+=" ";else if(typeof b===l)indent=b;rep=a;if(a&&typeof a!==k&&(typeof a!==m||typeof a.length!==n))throw new Error("JSON.stringify");return str("",{"":d})};if(typeof JSON.parse!==k)JSON.parse=function(text,reviver){var j;function walk(d,e){var b,c,a=d[e];if(a&&typeof a===m)for(b in a)if(Object.prototype.hasOwnProperty.call(a,b)){c=walk(a,b);if(c!==undefined)a[b]=c;else delete a[b]}return reviver.call(d,e,a)}text=String(text);cx.lastIndex=0;if(cx.test(text))text=text.replace(cx,function(a){return"\\u"+("0000"+a.charCodeAt(0).toString(16)).slice(-4)});if(/^[\],:{}\s]*$/.test(text.replace(/\\(?:["\\\/bfnrt]|u[0-9a-fA-F]{4})/g,"@").replace(/"[^"\\\n\r]*"|true|false|null|-?\d+(?:\.\d*)?(?:[eE][+\-]?\d+)?/g,"]").replace(/(?:^|:|,)(?:\s*\[)+/g,""))){j=eval("("+text+")");return typeof reviver==="function"?walk({"":j},""):j}throw new SyntaxError("JSON.parse");}})();


function removeFromArrayByKey(array, key, value) {
	var dumped = [];
	for ( var i = 0; i < array.length; i++) {
		if (array[i][key]==value) {
			
		} else {
			dumped.push(array[i]);
		}
	}
	return dumped;
}



