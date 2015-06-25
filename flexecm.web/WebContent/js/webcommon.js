
$(document).ready(function() {
	$("#rnd-code-img").click(function() {
		$("#rnd-code-img").attr("src", "/rndimg?" + new Date().getTime());
	});
	$.ajaxSetup ({
		// Disable caching of AJAX responses
		cache: false
	});
});

/**
 * 通用的查看项目方法。 根据企业和用户项目类型不同，加载不同的内容页。
 * 只能加载在 #ccontent 中， css设置如此(之后可以修改)
 * @param loan
 */
function viewLoan(loan, cb) {
	$("#ccontent").hide();

	console.log(loan);

	var div = $('<div class="loading" style="line-height: 400px;text-align: center;font-size: 16px;">正在载入页面</div>');
	$("#ccontent").after(div);
	div.show();
	
	$("#ccontent").data("loan", loan);
	
	var auditStatus;
	switch (loan.audit) {
	case 1:
		auditStatus = "<b style='color:#35B558'>等待初审</b>";
		break;
	case 2:
		auditStatus = "<b style='color:#35B558'>等待复审</b>";
		break;
	case 3:
		auditStatus = "<b style='color:#35B558'>审核通过</b>";
		break;
	case 10:
		auditStatus = "<b style='color:#35B558'>还款完结</b>";
		break;
	case -1:
		auditStatus = "<b style='color:#df0202'>已驳回</b>";
		break;
	default:
		break;
	}
	
	if (loan.type==1) { //company
		$("#ccontent").html("");
		loadPages($("#ccontent"), 
				["sub/creq-1.html",
				 "sub/creq-2.html",
				 "sub/creq-3.html",
				 "sub/creq-4.html",
				 "sub/creq-5.html",
				 "sub/loan-approve-info.html"],
				 ["基本信息","借款历史","担保情况","企业信息","经营情况", "审批情况" + auditStatus],
				function(url,div) {
					if (url==null) {
						var c = new formCheck("#ccontent ");
						c.init(loan);
						c.readOnly();
						$("img.field").css("width", "480").css("height", "360");

						finished(loan);
					}
				});
	} else if (loan.type==2) { //person
		$("#ccontent").html("");
		if (loan.mate3) {
			loadPages($("#ccontent"), 
					[
					 "sub/ureq-1.html",
					 "sub/ureq-2.html",
					 "sub/ureq-3.html",
					 "sub/ureq-4.html",
					 "sub/ureq-5.html",
					 "sub/ureq-3.html?mate=1",
					 "sub/ureq-4.html?mate=1",
					 "sub/ureq-5.html?mate=1",
					 "sub/ureq-6.html",
					 "sub/loan-approve-info.html"
					],
					[
					 "基本信息",
					 "贷款需求",
					 "资产情况",
					 "借款历史",
					 "其他金融资产",
					 "配偶资产情况",
					 "配偶借款历史",
					 "配偶其他金融资产",
					 "家庭成员状况",
					 "审批情况" + auditStatus
					],
					function(url,div) {
						if (url=="sub/ureq-3.html?mate=1") {
							var c = new formCheck(div);
							c.init(loan.mate3);
							c.readOnly();
						} else if (url=="sub/ureq-4.html?mate=1") {
							var c = new formCheck(div);
							c.init(loan.mate4);
							c.readOnly();
						} else if (url=="sub/ureq-5.html?mate=1") {
							var c = new formCheck(div);
							c.init(loan.mate5);
							c.readOnly();
						} else {
							var c = new formCheck(div);
							c.init(loan);
							c.readOnly();
						}
 						if (url==null) {
 							var c = new formCheck("#ccontent");
							c.readOnly();
							finished(loan);
						}
				});
		} else {
			loadPages($("#ccontent"), 
					[
					 "sub/ureq-1.html",
					 "sub/ureq-2.html",
					 "sub/ureq-3.html",
					 "sub/ureq-4.html",
					 "sub/ureq-5.html",
					 "sub/ureq-6.html",
					 "sub/approve-info.html"
					],
					[
					 "基本信息",
					 "贷款需求",
					 "资产情况",
					 "借款历史",
					 "其他金融资产",
					 "家庭成员状况",
					 "审批情况" + auditStatus
					],
					function(url,div) {
						if (url==null) {
							var c = new formCheck("#ccontent");
							c.init(loan);
							c.readOnly();
							finished(loan);
						}
				});
		}
	}
	
	function finished(loan) {
		$("#ccontent").next(".loading").remove();
		$("#ccontent").show();
		if (cb) {
			cb(loan);
		}
	}
}

function initAuditInfo(loan) {
	
}


var _cached_html = {};

function cachePage(url) {
	var id = "rnd" + genPass();
	var div = $("<div></div");
	div.attr("id", id);
	div.appendTo("body");
	div.load(url, function() {
		_cached_html[url] = id;
	});
}

/**
 * 在容器中加载一个单一页面。  目前加入了缓存机制(考虑以后可以去除？) 
 * 加载过程中展示正在载入页面 信息
 * @param container
 * @param url      加载的url
 * @param callback  页面加载完成后调用
 */
function loadPage(container, url, callback) {
	$(container).show();
	$(".xdsoft_datetimepicker").remove();
	
	$(container).html('<div class="loading" style="line-height: 400px;text-align: center;font-size: 16px;">正在载入页面</div>');
	$(container).load(url, function() {
		var id = "rnd" + genPass();
		if (callback) {
			callback();
		}
	});
}

/**
 * 在一个容器中先后加载、初始化多个页面
 * @param container  容器
 * @param urls     页面url列表
 * @param titles   每个页面的标题 (目前主要是因为个人和配偶的表单相同而标题不同才产生了这个需求)
 * @param callback  每个页面加载后产生的回调函数。 有n个页面就触发n次。返回的是 刚刚加载的url和页面$(dom)。全部页面都加载完再触发一次，2个参数都为null
 */
function loadPages(container, urls, titles, callback) {
	if (urls.length==0) {
		if (callback) {
			callback();
		}
		return;
	}
	
	var url = urls.shift();
	if (titles) {
		var title = titles.shift();
		if (title) {
			container.append("<h2>" + title +"</h2>");
		}
	}
	
	if (_cached_html[url]!=null) {
		container.append($("#" + _cached_html[url]).html());
	} else {
		var div = $("<div class='page'></div>");
		div.attr("id", "rnd" + genPass());
		container.append(div);
		$(div).load(url, function() {
			loadPages(container, urls, titles, callback);
			if (callback) {
				callback(url, $(div));
			}
		});
	}
}


var formCheck = function(selector) {
	var form = $(selector);
	
	form.find("input").each(function() {
		if ($(this).data("checked")!=null) {
			$(this).focusout(function() {
				check($(this));
			});
		}
	});
	
	form.find("input[type='radio']").click(function() {
		if ($(this).val() == "1") {
			$("." + $(this).attr("name")).show();
		} else {
			$("." + $(this).attr("name")).hide();
		}
	});
	
	form.find("select.switch").change(function() {
		if ($(this).val() == "-1") {
			$("." + $(this).attr("id")).show();
		} else {
			$("." + $(this).attr("id")).hide();
		}
	});
	
	form.find(".sublist").each(function() {
		var tb = $(this);
		
		tb.find("a.addsub").unbind().click(function() {
			var ff1 = new formCheck($(this).parents(".editable"));
			var data = ff1.getRequest();
			addSubListItem(tb, data);
			ff1.clear();
		});
	});
	
	form.find("input.date").each(function() {
		$(this).datetimepicker({
			format:'Y-m-d',
			timepicker:false,
			lang:'ch'
		});
	});

	$("a.imgupload").each(function() {
		var btnid = $(this).attr("id");
		initUploader(btnid, "/service/fin/upload","jpg,gif,png" ,function(up, file, r) {
			var img = $('<img class="field" style="height:40px; width: 50px;" src="/service/fin/preview?id='
				+ r.response + '">');
			img.data("field", btnid);
			img.data("picdata", r.response);
			$("#" + btnid).next("img").remove();
			$("#" + btnid).after(img);
		});
	});

	$("a.attachfile").each(function() {
		var btnid = $(this).attr("id");
		initUploader(btnid, "/service/fin/upload","*" ,function(up, file, r) {
			var a = $('<a class="field" href="/service/fin/preview?id='
				+ r.response + '"> ' + file.name + '</a>');
			a.data("field", btnid);
			a.data("picdata", r.response);
			$("#" + btnid).next("a.field").remove();
			$("#" + btnid).after(a);
		});
	});

	function addSubListItem(list, data) {
		var cloned = $(list).find(".template").clone().removeClass("template").addClass("row item");
		cloned.find(".fill").each(function() {
			$(this).html(data[$(this).data("field")]);
		});
		cloned.find("a.removesub").click(function(){
			$(this).parents(".row.item").remove();
		});
		$(list).find(".head").after(cloned);
	}
	return {
		readOnly: function() {

			$(form).find("input[type='text']").each(function() {
				$(this).replaceWith($(this).val());
			});


			$(form).find("input,textarea").attr("disabled", true);
			$(form).find("input,textarea").addClass("disabled");
			$(".edita").hide();
			$(form).find("select").each(function() {
				var text = $(this).find("option:selected").text();
				$(this).replaceWith(text);
			});
			
			$(form).find("input[type='radio']").each(function() {
				if ($(this).attr("checked")!="checked") {
					$(this).next("i").remove();
				}
				$(this).remove();
			});
			$(form).find("a.imgupload").remove();
			$(form).find("a.attachfile").remove();
		},
		
		showBtn: function(btn) {
			(form).find("a.btn").hide();
			var btns = btn.split(" ");
			for (var i = 0; i < btns.length; i++) {
				$(form).find("a." + btns[i]).show();
			}
		},
		
		editable: function(btn) {
			$(form + "input").attr("disabled", false);
			$(form + "input").css("border", "1px solid #ddd");
		},
		
		clear: function() {
			$(form).find("input").each(function() {
				if ($(this).attr("type")=="text") {
					$(this).val("");
				}
			});
		},

		init:function(data) { //用data初始化这个表单
			if (data) {
				console.log(data);
				$(form).find("input,textarea").each(function() {
					if ($(this).attr("type")=="text") {
						$(this).val("");
					}
					if ($(this).attr("id")!=null && data[$(this).attr("id")]) {
						$(this).val(data[$(this).attr("id")]);
						if($(this).data("format")=="date") {
							var d=new Date(data[$(this).attr("id")]); 
							$(this).val(d.format('yyyy-MM-dd hh:mm'));
						}
					}
					if ($(this).attr("type")=="radio") {
						if ($(this).attr("value")==data[$(this).attr("name")]) {
							$(this).prop('checked', true);
							//$(this).attr("checked", "checked");
						} else {
							$(this).prop('checked', false);
						}

						if (data[$(this).attr("name")] == "1" || data[$(this).attr("name")] == null) {
							$("." + $(this).attr("name")).show();
						} else {
							$("." + $(this).attr("name")).hide();
						}
					}
				});
				
				
				$(form).find("select").each(function() {
					if ($(this).attr("id")!=null && data[$(this).attr("id")]) {
						
						if (!optionExists($(this), data[$(this).attr("id")])) {
							$(this).append("<option>" + data[$(this).attr("id")] + "</option>");
						};
						$(this).val(data[$(this).attr("id")]);

						if (data[$(this).attr("id")] == "-1") {
							$("." + $(this).attr("id")).show();
						} else {
							$("." + $(this).attr("id")).hide();
						}
					}
					function optionExists(t,val) {
						  return $(t).find("option[value='" + val + "']").length !== 0;
					}
					
				});
				
				$(form).find(".fill").each(function() {
					$(this).html(data[$(this).data("field")]);
				});
				
				$(form).find(".sublist").each(function(){
					var listData = data[$(this).attr("id")];
					if (listData==null) return;
					for(var i=0; i<listData.length; i++) {
						addSubListItem($(this), listData[i])
					}
				});
				
				$(form).find("a.imgupload").each(function() {
					if (data[$(this).attr("id")]) {
						var img = $('<img class="field" style="height:45px; width: 60px;" src="/service/fin/preview?id='
								+ data[$(this).attr("id")] + '">');
						$(this).after(img);
					}
				});

				$(form).find("a.attachfile").each(function() {
					if (data[$(this).attr("id")]) {
						var a = $("<a>下载查看</a>")
							.attr("href", "/service/fin/preview?id=" + data[$(this).attr("id")])
							.attr("target", "_blank");
						$(this).after(a);
					}
				});
			}
		},
		getRequest: function() { //从表单抽取请求数据
			var request = {};
			$(form).find("input, select,textarea").each(function() {
				//过滤掉子表单的情况
				if ($(this).parentsUntil($(form), ".sublist").length!=0) return;
				if ($(this).attr("id")!=null) {
					if ($(this).attr("id").indexOf("html5_")>-1) return; //去除上传控件产生的input
					request[$(this).attr("id")] = $(this).val();
				}
				if ($(this).attr("type")=="radio") {
					request[$(this).attr("name")] = $("input[name='" + $(this).attr("name") + "']:checked").val()
				}
				if ($(this).attr("type")=="checkbox") {
					if (!$(this).has(":checked")) {
						request[$(this).attr("id")] = null;
					}
				}
			});
			
			$(form).find(".sublist").each(function(){
				//抽取子表单的
				var listData = [];
				$(this).find(".row.item").each(function(){
					var o = {};
					$(this).find(".fill").each(function() {
						o[$(this).data("field")] = $(this).html();
					});
					listData.push(o);
				});
				request[$(this).attr("id")] = listData;
			});
			
			$(form).find(".field").each(function() {
				if ($(this).data("field")) {
					request[$(this).data("field")] = $(this).data("picdata");
				}
			});

			return request;
		},
		test: function() {
			var result = true
			$(form).find("input").each(function() {
				if (result && $(this).attr("id")!=null) {
					if (check($(this))!=true) {
						alert(check($(this)));
						result = false;
					}
				}
			});
			return result;
		}
	}
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


/**
 * 
 * @param tbid table 的selector
 * @param data 表格数据 list
 * @param cell  每个单元格回调函数。 当td 定义cal时进行回调    传入td、entry和data-cal="xxx"的数据
 * @param cb  当翻页到某个page时，传入当时页码。可以调用获取后台更新表格数据
 */

function initTable(tbid, data, cell, cb) {
	$(tbid + " .row.item").remove();
	$(tbid + " .empty").show();
	for(var i=0; i<data.list.length; i++) {
		$(tbid).next(".empty").hide();
		var cloned = $(tbid + " .template").clone().removeClass("template").addClass("item row");

		var entry = data.list[i];
		cloned.data("entry", entry);
		
		cloned.find("div.cell").each(function() {
			if ($(this).data("f")) {
				$(this).html(entry[$(this).data("f")]);
			}
			if ($(this).data("eval")) {
				$(this).html(eval($(this).data("eval")));
			}
			if ($(this).data("cal") && cell) {
				cell($(this), entry, $(this).data("cal"));
			}
			if ($(this).data("t")) {
				for(var k in entry) {
					$(this).html($(this).html().replace("{{" + k + "}}", entry[k]));
				}
			}
		});
		$(tbid).append(cloned);
	}
	
	var number = Math.floor(data.start/data.per) + 1;
	var pc = Math.floor(data.size/data.per)+1;
	
	$(tbid).find(".pager").remove();
	
	if (pc>1 && $(tbid).find(".pager").length==0) {
		$("<div class='pager'></div>").insertAfter($(tbid))
		.pager({ pagenumber: number, pagecount: pc, buttonClickCallback: 
			function(pageclickednumber){
				if (cb) {
					cb(pageclickednumber);
				}
			}
		});
	}
}


function check(t) {
	var checkin = $(t).data("checked");
	if (!checkin) return true;
	if (checkin=="notnull") {
		if ($(t).val()=="") {
			return wrong();
		} else {
			return right();
		}
	} 
	if (checkin.indexOf("length")>-1) {
		var length = parseInt(checkin.substring(7));
		if ($(t).val().length<length) {
			return wrong();
		} else {
			return right();
		}
	}
	
	if (checkin=="mobile") {
		if (isMobile($(t).val())) {
			return right();
		} else {
			return wrong();
		}
	}
	
	if (checkin=="email") {
		if (isEmail($(t).val())) {
			return right();
		} else {
			return wrong();
		}
	}
	
	if (checkin=="number+") {
		if (isPlusNumber($(t).val())) {
			return right();
		} else {
			return wrong();
		}
	}
	if (checkin=="pcode") {
		if (IdCardValidate($(t).val())) {
			return right();
		} else {
			return wrong();
		}
	}
	
	function right() {
		$("em." + $(t).attr("id")).html("格式正确");
		$("em." + $(t).attr("id")).addClass("sucess").removeClass("fail").css("color","green");
		return true;
	}
	function wrong() {
		$("em." + $(t).attr("id")).html($(t).data("inval"));
		$("em." + $(t).attr("id")).addClass("fail").removeClass("sucess").css("color", "red");
		return $(t).data("inval");
	}

	function isPlusNumber(str) {
		var r = /^[0-9]*[1-9][0-9]*$/; 
		return r.test(str); 
	}
	function isEmail(str) {
		var reg = /^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+(\.[a-zA-Z0-9_-])+/; 
		return reg.test(str); 
	}

	function isMobile(str) {
		var reg = /^(13|14|15|18|17)\d{9}$/;
		return reg.test(str);
	}

	function isChina(s) {//判断字符是否是中文字符
		var reg= /[\u4E00-\u9FA5]|[\uFE30-\uFFA0]/gi; 
		return reg.test(s);
	}

	function IdCardValidate(idCard) { 
	    idCard = trim(idCard.replace(/ /g, ""));               //去掉字符串头尾空格                     
	    if (idCard.length == 15) {   
	        return isValidityBrithBy15IdCard(idCard);       //进行15位身份证的验证    
	    } else if (idCard.length == 18) {   
	        var a_idCard = idCard.split("");                // 得到身份证数组   
	        if(isValidityBrithBy18IdCard(idCard)&&isTrueValidateCodeBy18IdCard(a_idCard)){   //进行18位身份证的基本验证和第18位的验证
	            return true;   
	        }else {   
	            return false;   
	        }   
	    } else {   
	        return false;   
	    }   
	}   
	/**  
	 * 判断身份证号码为18位时最后的验证位是否正确  
	 * @param a_idCard 身份证号码数组  
	 * @return  
	 */  
	function isTrueValidateCodeBy18IdCard(a_idCard) {   
		var ValideCode = [ 1, 0, 10, 9, 8, 7, 6, 5, 4, 3, 2 ];            // 身份证验证位值.10代表X   
		var Wi = [ 7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2, 1 ];    // 加权因子   
	    var sum = 0;                             // 声明加权求和变量   
	    if (a_idCard[17].toLowerCase() == 'x') {   
	        a_idCard[17] = 10;                    // 将最后位为x的验证码替换为10方便后续操作   
	    }   
	    for ( var i = 0; i < 17; i++) {   
	        sum += Wi[i] * a_idCard[i];            // 加权求和   
	    }   
	    valCodePosition = sum % 11;                // 得到验证码所位置   
	    if (a_idCard[17] == ValideCode[valCodePosition]) {   
	        return true;   
	    } else {   
	        return false;   
	    }   
	}   
	/**  
	  * 验证18位数身份证号码中的生日是否是有效生日  
	  * @param idCard 18位书身份证字符串  
	  * @return  
	  */  
	function isValidityBrithBy18IdCard(idCard18){   
	    var year =  idCard18.substring(6,10);   
	    var month = idCard18.substring(10,12);   
	    var day = idCard18.substring(12,14);   
	    var temp_date = new Date(year,parseFloat(month)-1,parseFloat(day));   
	    // 这里用getFullYear()获取年份，避免千年虫问题   
	    if(temp_date.getFullYear()!=parseFloat(year)   
	          ||temp_date.getMonth()!=parseFloat(month)-1   
	          ||temp_date.getDate()!=parseFloat(day)){   
	            return false;   
	    }else{   
	        return true;   
	    }   
	}   
	  /**  
	   * 验证15位数身份证号码中的生日是否是有效生日  
	   * @param idCard15 15位书身份证字符串  
	   * @return  
	   */  
	  function isValidityBrithBy15IdCard(idCard15){   
	      var year =  idCard15.substring(6,8);   
	      var month = idCard15.substring(8,10);   
	      var day = idCard15.substring(10,12);   
	      var temp_date = new Date(year,parseFloat(month)-1,parseFloat(day));   
	      // 对于老身份证中的你年龄则不需考虑千年虫问题而使用getYear()方法   
	      if(temp_date.getYear()!=parseFloat(year)   
	              ||temp_date.getMonth()!=parseFloat(month)-1   
	              ||temp_date.getDate()!=parseFloat(day)){   
	                return false;   
	        }else{   
	            return true;   
	        }   
	  }   
	//去掉字符串头尾空格   
	function trim(str) {   
	    return str.replace(/(^\s*)|(\s*$)/g, "");   
	}  
	//不需要验证的返回true
	return true;
}

function encode(t) {
	for(var k in t) {
		t[k] = encodeURI(t[k]).replace(/\+/g,'%2B');
	}
	return t;
}

Date.prototype.format = function(fmt) {         
    var o = {         
    "M+" : this.getMonth()+1, //月份         
    "d+" : this.getDate(), //日         
    "h+" : this.getHours()%12 == 0 ? 12 : this.getHours()%12, //小时         
    "H+" : this.getHours(), //小时         
    "m+" : this.getMinutes(), //分         
    "s+" : this.getSeconds(), //秒         
    "q+" : Math.floor((this.getMonth()+3)/3), //季度         
    "S" : this.getMilliseconds() //毫秒         
    };         
    var week = {         
    "0" : "/u65e5",         
    "1" : "/u4e00",         
    "2" : "/u4e8c",         
    "3" : "/u4e09",         
    "4" : "/u56db",         
    "5" : "/u4e94",         
    "6" : "/u516d"        
    };         
    if(/(y+)/.test(fmt)){         
        fmt=fmt.replace(RegExp.$1, (this.getFullYear()+"").substr(4 - RegExp.$1.length));         
    }         
    if(/(E+)/.test(fmt)){         
        fmt=fmt.replace(RegExp.$1, ((RegExp.$1.length>1) ? (RegExp.$1.length>2 ? "/u661f/u671f" : "/u5468") : "")+week[this.getDay()+""]);         
    }         
    for(var k in o){         
        if(new RegExp("("+ k +")").test(fmt)){         
            fmt = fmt.replace(RegExp.$1, (RegExp.$1.length==1) ? (o[k]) : (("00"+ o[k]).substr((""+ o[k]).length)));         
        }         
    }         
    return fmt;         
}       

_js_loaded = [];

//按次序加载js文件，加载完成后执行cb方法
function loadJS(list, cb) {
	if (list.length==0) {
		cb();
	}
	var jsurl = list.pop();
	if (_js_loaded.indexOf(jsurl)>-1) {
		loadJS(list, cb);
	}
	$.ajax({
		  url: jsurl,
		  dataType: "script",
		  success:function() {
			  _js_loaded.push(jsurl);
			  loadJS(list, cb);
		  }
	});
}

function loadCss(css) {
	$("<link>").attr({ rel: "stylesheet",
		type: "text/css",
		href: css
	}).appendTo("head");
}

function initUploader(btnid, uploadurl, types, cb) {
	var uploader = new plupload.Uploader({
	 	runtimes : 'html5,flash,silverlight,html4',
	    browse_button : btnid, // you can pass in id...
	    url : uploadurl,
	    filters : {
	        max_file_size : '10mb',
	        mime_types: [
	            {title : "选择文件", extensions : types}
	        ]
	    },
	    // Flash settings
	    flash_swf_url : '/plupload/js/Moxie.swf',
	 
	    init: {
	        FilesAdded: function(up, files) {
	        	 uploader.start();
	        },
	 
	        UploadProgress: function(up, file) {
	        	//$(".fileUploader1Container span").html(file.percent + "%");
	        },
	 
	        Error: function(up, err) {
	        	cb(up, error);
	        },
	        FileUploaded: function(up,file,r) {
	        	cb(up, file, r);
	        }
	    }
	});
	uploader.init();	
}

(function($) {

    $.fn.pager = function(options) {

        var opts = $.extend({}, $.fn.pager.defaults, options);

        return this.each(function() {

        // empty out the destination element and then render out the pager with the supplied options
            $(this).empty().append(renderpager(parseInt(options.pagenumber), parseInt(options.pagecount), options.buttonClickCallback));
            
            // specify correct cursor activity
            $('.pages li').mouseover(function() { document.body.style.cursor = "pointer"; }).mouseout(function() { document.body.style.cursor = "auto"; });
        });
    };

    // render and return the pager with the supplied options
    function renderpager(pagenumber, pagecount, buttonClickCallback) {

        // setup $pager to hold render
        var $pager = $('<ul class="pages"></ul>');

        // add in the previous and next buttons
        $pager.append(renderButton('第一页', pagenumber, pagecount, buttonClickCallback)).append(renderButton('上一页', pagenumber, pagecount, buttonClickCallback));

        // pager currently only handles 10 viewable pages ( could be easily parameterized, maybe in next version ) so handle edge cases
        var startPoint = 1;
        var endPoint = 9;

        if (pagenumber > 4) {
            startPoint = pagenumber - 4;
            endPoint = pagenumber + 4;
        }

        if (endPoint > pagecount) {
            startPoint = pagecount - 8;
            endPoint = pagecount;
        }

        if (startPoint < 1) {
            startPoint = 1;
        }

        // loop thru visible pages and render buttons
        for (var page = startPoint; page <= endPoint; page++) {

            var currentButton = $('<li class="page-number">' + (page) + '</li>');

            page == pagenumber ? currentButton.addClass('pgCurrent') : currentButton.click(function() { buttonClickCallback(this.firstChild.data); });
            currentButton.appendTo($pager);
        }

        // render in the next and last buttons before returning the whole rendered control back.
        $pager.append(renderButton('下一页', pagenumber, pagecount, buttonClickCallback)).append(renderButton('最后一页', pagenumber, pagecount, buttonClickCallback));

        return $pager;
    }

    // renders and returns a 'specialized' button, ie 'next', 'previous' etc. rather than a page number button
    function renderButton(buttonLabel, pagenumber, pagecount, buttonClickCallback) {

        var $Button = $('<li class="pgNext">' + buttonLabel + '</li>');

        var destPage = 1;

        // work out destination page for required button type
        switch (buttonLabel) {
            case "第一页":
                destPage = 1;
                break;
            case "上一页":
                destPage = pagenumber - 1;
                break;
            case "下一页":
                destPage = pagenumber + 1;
                break;
            case "最后一页":
                destPage = pagecount;
                break;
        }

        // disable and 'grey' out buttons if not needed.
        if (buttonLabel == "第一页" || buttonLabel == "上一页") {
            pagenumber <= 1 ? $Button.addClass('pgEmpty') : $Button.click(function() { buttonClickCallback(destPage); });
        }
        else {
            pagenumber >= pagecount ? $Button.addClass('pgEmpty') : $Button.click(function() { buttonClickCallback(destPage); });
        }

        return $Button;
    }

    // pager defaults. hardly worth bothering with in this case but used as placeholder for expansion in the next version
    $.fn.pager.defaults = {
        pagenumber: 1,
        pagecount: 1
    };

})(jQuery);
