/**
 * 
 */

$(document).ready(function() {
	$("#rnd-code-img").click(function() {
		$("#rnd-code-img").attr("src", "/rndimg?" + new Date().getTime());
	});
});

var formCheck = function(form) {

	if (form==null) {
		form = "";
	}
	
	$(function() {
		$(form + "input").each(function() {
			if ($(this).attr("id")!=null) {
				$(this).focusout(function() {
					check($(this));
				});
			}
		});
	});
	
	
	function check(t) {
		var checkin = $(t).data("checked");
		if (checkin) {
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
		}
		//不需要验证的返回true
		return true;
		
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
	}
	
	return {
		readOnly: function(btn) {
			$(form + "input").attr("disabled", true);
			$(form + "input").css("border", "none");
			
			$(form + " a.btn").hide();
			$(form + " a." + btn).show();
		},
		editable: function(btn) {
			$(form + "input").attr("disabled", false);
			$(form + "input").css("border", "1px solid #ddd");
			
			$(form + " a.btn").hide();
			$(form + " a." + btn).show();
		},
		check:check,
		init:function(data) {
			$(form + "input").each(function() {
				$(this).val("");
				if ($(this).attr("id")!=null && data[$(this).attr("id")]) {
					$(this).val(data[$(this).attr("id")]);
				}
			});
		},
		getRequest: function(test) {
			var request = {};
			$(form + "input," + form + " select").each(function() {
				if ($(this).attr("id")!=null) {
					request[$(this).attr("id")] = $(this).val();
				}
			});
			return request;
		},
		test: function() {
			var result = true
			$(form + "input").each(function() {
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

function initTable(tbid, data, cell) {
	$(tbid + " .row.item").remove();
	$(tbid + " .empty").show();
	
	for(var i=0; i<data.list.length; i++) {
		$(tbid).next(".empty").hide();
		var cloned = $(tbid + " .template").clone().removeClass("template").addClass("item row");

		cloned.data("entry", entry);
		var entry = data.list[i];
		
		cloned.find("div.cell").each(function() {
			if ($(this).data("f")) {
				$(this).html(entry[$(this).data("f")]);
			}
			
			if ($(this).data("eval")) {
				$(this).html(eval($(this).data("eval")));
			}
			if ($(this).data("cal") && cell) {
				cell($(this), data.list[i]);
			}
			$(tbid).append(cloned);
		});
	}
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

//按次序加载js文件，加载完成后执行cb方法
function loadJS(list, cb) {
	if (list.length==0) {
		cb();
	}
	var jsurl = list.pop();
	$.ajax({
		  url: jsurl,
		  dataType: "script",
		  success:function() {
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
