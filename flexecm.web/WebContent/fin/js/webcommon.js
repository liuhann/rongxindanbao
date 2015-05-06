
$(document).ready(function() {
	$("#rnd-code-img").click(function() {
		$("#rnd-code-img").attr("src", "/rndimg?" + new Date().getTime());
	});
});

var formCheck = function(selector) {
	var form = $(selector);
	
	form.find("input").each(function() {
		if ($(this).data("checked")!=null) {
			$(this).focusout(function() {
				check($(this));
			});
		}
	});
	
	form.find(".sublist").each(function() {
		var tb = $(this);
		tb.find(".template").hide();
		
		tb.find("a.addsub").click(function() {
			var fc = new formCheck(tb.find(".editable"));
			var r = fc.getRequest();
			var cloned = tb.find(".tepmplate").removeClass("template").clone();
			var ww = new formCheck(cloned);
			ww.init(r);
			cloned.after(tb.find(".head"));
		});
		
		tb.find("a.removesub").click(function(){
			
		});
	})
	
	return {
		readOnly: function(btn) {
			$(form).find("input").attr("disabled", true);
			$(form).find("input").css("border", "none");
		},
		
		showBtn: function(btn) {
			(form).find("a.btn").hide();
			$(form).find("a." + btn).show();
		},
		
		editable: function(btn) {
			$(form + "input").attr("disabled", false);
			$(form + "input").css("border", "1px solid #ddd");
		},
		init:function(data) { //用data初始化这个表单
			if (data) {
				$(form).find("input").each(function() {
					if ($(this).attr("type")=="text") {
						$(this).val("");
					}
					if ($(this).attr("id")!=null && data[$(this).attr("id")]) {
						$(this).val(data[$(this).attr("id")]);
					}
				});
				
				$(form).find(".sublist").each(function(){
					var listData = data[$(this).attr("id")];
					if (listData==null) return;
					for(var i=0; i<listData.length; i++) {
						var listItem = listData[i];
						var cloned = $(this).find(".tepmplate").removeClass("template").clone();
						$(this).find(".fill").each(function() {
							$(this).html(listItem[$(this).data("field")]);
						});
						cloned.after($(this).find(".head"));
					}
				});
			}
		},
		getRequest: function() { //从表单抽取请求数据
			var request = {};
			$(form).find("input, select").each(function() {
				if ($(this).parents(".sublist").length!=0) return;
				if ($(this).attr("id")!=null) {
					request[$(this).attr("id")] = $(this).val();
				}
				if ($(this).attr("name")!=null) {
					request[$(this).attr("name")] = $(this).val();
				}
			});
			
			$(form).find(".sublist").each(function(){
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

var subItemTable = function(tbid, list) {
	var tb = $(tbid);
	tb.find(".template").hide();
	
	tb.find("a.addsub").click(function() {
		var fc = new formCheck(tb.find(".editable"));
		var r = fc.getRequest();
		
		var cloned = tb.find(".tepmplate").removeClass("template").clone();
		
		var ww = new formCheck(cloned);
		ww.init(r);
		
		cloned.after(tb.find(".head"));
	});
	
	tb.find("a.removesub").click(function(){
		
	});
};

function initTable(tbid, data, cell, cb) {
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
				cell($(this), entry, $(this).data("cal"));
			}
			$(tbid).append(cloned);
		});
	}
	
	var number = Math.floor(data.start/data.per) + 1;
	var pc = Math.floor(data.size/data.per)+1;
	
	$(tbid).find(".pager").remove();
	
	if (pc>1 && $(tbid).find(".pager").length==0) {
		$("<div class='pager'></div").appendTo($(tbid))
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

function initUploader(btnid, uploadurl, cb) {
	var uploader = new plupload.Uploader({
	 	runtimes : 'html5,flash,silverlight,html4',
	    browse_button : btnid, // you can pass in id...
	    url : uploadurl,
	    filters : {
	        max_file_size : '10mb',
	        mime_types: [
	            {title : "选择图形格式文件", extensions : "jpg,gif,png"}
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
