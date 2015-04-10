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
