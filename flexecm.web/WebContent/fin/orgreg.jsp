<%@ page contentType="text/html;charset=UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>组织注册</title>

<SCRIPT type=text/javascript src="js/jquery-1.11.2.min.js"></SCRIPT>
<SCRIPT type=text/javascript src="datepicker/jquery.datetimepicker.js"></SCRIPT>


<!--css -->
<link type="text/css" rel="stylesheet" href="css/base.css"><!--通用CSS样式 -->
<link type="text/css" rel="stylesheet" href="css/top.css">

<link type="text/css" rel="stylesheet" href="datepicker/jquery.datetimepicker.css">
<!--jquery -->

<style type="text/css">

#content {
	height: auto;
	width: 1150px;
	margin-right: auto;
	margin-left: auto;
}
.bg {
	background-color: #efefed;
}
.regform {
	border-top-width: 1px;
	border-right-width: 1px;
	border-bottom-width: 1px;
	border-left-width: 1px;
	border-top-style: solid;
	border-top-color: #21a3e0;
	background-color: #FFF;
}
.regform .yinying {
	clear: both;
	background-image: url(img/rongzirukouP-1_003.png);
	background-repeat: repeat-x;
	height: 8px;
	width: auto;
}
.regform .title span {
	font-size: 20px;
	font-weight: bold;
	color: #333333;
}
.regform .title {
	text-align: center;
	height: auto;
	width: auto;
	padding-top: 32px;
	padding-bottom: 32px;
	border-bottom-width: 1px;
	border-bottom-style: solid;
	border-bottom-color: #e5e5e5;
}
.regform .form-content {
	height: auto;
	width: auto;
	padding-top: 24px;
	padding-bottom: 120px;
	padding-left: 222px;
}

.regform .form-content table tr td {
	height: 30px;
}

.regform .form-content table tr td input {
	height: 40px;
	width: 310px;
	border: 1px solid #dedede;
	line-height: 40px;
	border-radius: 2px;
	padding-right: 10px;
	padding-left: 10px;
}

.regform .form-content table tr td span {
	font-size: 14px;
	color: #666666;
}
.regform .form-content table tr td b {
	color: #e03121;
	margin-right: 5px;
}
.regform .form-content table tr td em {
	display: block;
	padding: 2px 0px;
	font-size:12px;
	min-height:10px;
}
.regform .form-content table tr td em.fail {
	color: #e03121;
}

.regform .form-content table tr td em.sucess {
	color: green;
}

.regform .form-content table tr td .active-btn {
	background-image: url(img/big-btn-bg.png);
	background-repeat: repeat-x;
	border-radius: 6px;
	height: 46px;
	font-size: 16px;
	color: #fff;
	margin-top: 36px;
	width: 333px;
}

.regform .form-content table tr td .unactive-btn {
  background-image: url(img/big-btn-bg-grey.png);
  background-repeat: repeat-x;
}

.regform .form-content table tr td select {
  width: 170px;
  margin-right: 13px;
  padding: 10px;
}
.regform .form-content table tr td select option {
}

.regform .form-content table tr td .form-check {
  width: 130px;
  margin-left: 6px;
  background-image: url(img/big-btn-bg-grey.png);
  background-repeat: repeat-x;
  height: 42px;
  border-top: none;
}

.step2 {
	display: none;
}

.active-btn {
	cursor: pointer;
}
a {
	cursor: pointer;
}

</style>

<script type="text/javascript">


function reg() {
	
	$("input").each(function() {
		if ($(this).attr("id")!=null) {
			check($(this));
		}
	});
	
	if ($("em.fail").length>0) {
		return;
	} else {
		var request = {};
		$("input").each(function() {
			if ($(this).attr("id")!=null) {
				request[$(this).attr("id")] = $(this).val();
			}
		});
		
		$("#register-btn").html("正在注册");
		$("#register-btn").removeClass("active-btn").addClass("unactive-btn");
		
		request.type = "company";
		$.post("/service/fin/account/reg",  request , function() {
			alert("注册成功");
			location.href = "orgrequest.jsp";
		}).fail(function() {
			alert("用户名已经被注册了，请更换");
			$("#register-btn").html("去借款");
			$("#register-btn").removeClass("unactive-btn").addClass("active-btn");
			$(".step2").hide();
			$(".step1").show();
		});
	}
}

$(function() {
	$("input").each(function() {
		if ($(this).attr("id")!=null) {
			$(this).focusout(function() {
				check($(this));
			});
		}
	});
	
	   $('#established').datetimepicker({
		   timepicker:false,
		   format:'Y-m-d',
		   lang:'ch'
		 });
});

function check(t) {
	var checkin = $(t).data("checked");
	if (checkin) {
		if (checkin=="notnull") {
			if ($(t).val()=="") {
				wrong();
			} else {
				right();
			}
			return;
		} 
		if (checkin.indexOf("length")>-1) {
			var length = parseInt(checkin.substring(7));
			if ($(t).val().length<length) {
				wrong();
			} else {
				right();
			}
		}
		
		if (checkin=="mobile") {
			if (isMobile($(t).val())) {
				right();
			} else {
				wrong();
			}
		}
		
		if (checkin=="email") {
			if (isEmail($(t).val())) {
				right();
			} else {
				wrong();
			}
		}
	}
	
	function right() {
		$("em." + $(t).attr("id")).html("格式正确");
		$("em." + $(t).attr("id")).addClass("sucess").removeClass("fail");
	}
	function wrong() {
		$("em." + $(t).attr("id")).html($(t).data("inval"));
		$("em." + $(t).attr("id")).addClass("fail").removeClass("sucess");
	}
}

function checknNext() {
	check($("#company"));
	check($("#userid"));
	check($("#pwd"));
	check($("#contact"));
	check($("#mobile"));
	check($("#email"));
	
	if ($("em.fail").length>0) {
		return;
	} else {
		$(".step1").fadeOut('fast');
		$(".step2").fadeIn();
	}
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


</script>

</head>
<body class="bg">
<%@ include file="top.jsp"%> 


<div id="content">
    <!--内容开始 -->
    	<div class="regform step1">
        	<!-- -->
            <div class="title">
            	<span>企业用户注册</span>
            </div>
            
            <div class="form-content">
            	<!-- -->
                <table>
                	<!--循环行 -->
                	<tr>
                    	<td style="text-align:right">
                        	<b>*</b><span>公司全称（同营业执照）</span>
                        </td>
                        <td style="padding-left:20px;">
                        	<input id="company" type="text" value="" data-checked="notnull" data-inval="公司全称 不能为空">
                        </td>
                    </tr>
                    <!--错误提示信息 -->
                	<tr>
                    	<td>                       	
                        </td>
                        <td class="info" style="padding-left:20px;">
                        	<em class="company"></em>
                        </td>
                    </tr>
                    <!-- -->                    
                    <!--循环行 -->
                	<tr>
                    	<td style="text-align:right">
                        	<b>*</b><span>用户名</span>
                        </td>
                        <td class="info" style="padding-left:20px;">
                        	<input id="loginid" type="text" value="" data-checked="notnull"  data-inval="用户名不能为空">
                        </td>
                    </tr>
                   <!--错误提示信息 -->
                	<tr>
                    	<td>                       	
                        </td>
                        <td class="info" style="padding-left:20px;">
                        	<em class="loginid"></em>
                        </td>
                    </tr>
                    <!-- -->                      
                    <!--循环行 -->
                	<tr>
                    	<td style="text-align:right">
                        	<b>*</b><span>密码</span>
                        </td>
                        <td style="padding-left:20px;">
                        	<input id="pwd" type="password" value="" data-checked="length-6" data-inval="密码要大于6位">
                        </td>
                    </tr>
                   <!--错误提示信息 -->
                	<tr>
                    	<td>                       	
                        </td>
                        <td class="info" style="padding-left:20px;">
                        	<em class="pwd"></em>
                        </td>
                    </tr>
                    <!-- -->                      
                    <!--循环行 -->
                	<tr>
                    	<td style="text-align:right">
                        	<b>*</b><span>联系人</span>
                        </td>
                        <td style="padding-left:20px;">
                        	<input id="contact" type="text" data-checked="notnull" data-inval="请输入姓名">
                        </td>
                    </tr>
                   <!--错误提示信息 -->
                	<tr>
                    	<td>                       	
                        </td>
                        <td style="padding-left:20px;">
                        	<em class="contact"></em>
                        </td>
                    </tr>
                    <!-- -->                      
                    <!--循环行 -->
                	<tr>
                    	<td style="text-align:right">
                        	<b>*</b><span>联系人手机</span>
                        </td>
                        <td style="padding-left:20px;">
                        	<input id="mobile" type="text" data-checked="mobile" data-inval="请输入有效的手机号码">
                        </td>
                    </tr>
                   <!--错误提示信息 -->
                	<tr>
                    	<td>                       	
                        </td>
                        <td style="padding-left:20px;">
                        	<em class="mobile"></em>
                        </td>
                    </tr>
                    <!-- -->                      
                    <!--循环行 -->
                	<tr>
                    	<td style="text-align:right">
                        	<b>*</b><span>联系人邮箱</span>
                        </td>
                        <td style="padding-left:20px;">
                        	<input id="email" type="text" data-checked="email" data-inval="请输入有效的电子邮件地址">
                        </td>
                    </tr>
                   <!--错误提示信息 -->
                	<tr>
                    	<td>
                        </td>
                        <td style="padding-left:20px;">
                        	<em class="email"></em>
                        </td>
                    </tr>
                    <!-- -->     
                	<tr>
                    	<td>                       	
                        </td>
                        <td style="padding-left:20px;">
                        	<input class="active-btn" type="button" value="下一步" onclick="checknNext();">
                        </td>
                    </tr>
                    <!-- -->                                       
                    <!--循环行 -->                                                                                                    
                </table>
                <!-- -->
            </div>
            <!-- -->
            <div class="yinying">
        	</div>
        </div>


		<div class="regform step2">
        	<!-- -->
            <div class="title">
            	<span>企业用户注册</span>
            </div>
            
            <div class="form-content">
            	<!-- -->
                <table>
                	<!--循环行 -->
                	<tr>
                    	<td style="text-align:right">
                        	<b>*</b><span>注册地址</span>
                        </td>
                        <td style="padding-left:20px;">
                        	<input type="text" id="location" data-checked="notnull" data-inval="注册地址不能为空">
                        </td>
                    </tr>
                    <!--错误提示信息 -->
                	<tr>
                    	<td>                       	
                        </td>
                        <td style="padding-left:20px;">
                        	<em class="location"></em>
                        </td>
                    </tr>
                    <!-- -->                    
                    <!--循环行 -->
                	<tr>
                    	<td style="text-align:right">
                        	<b>*</b><span>成立时间</span>
                        </td>
                        <td style="padding-left:20px;">
                        	<input type="text" id="established" data-checked="notnull" data-inval="成立时间不能为空">
                        </td>
                    </tr>
                   <!--错误提示信息 -->
                	<tr>
                    	<td>                       	
                        </td>
                        <td style="padding-left:20px;">
                        	<em class="established"></em>
                        </td>
                    </tr>
                    <!-- -->                      
                    <!--循环行 -->
                	<tr>
                    	<td style="text-align:right">
                        	<b>*</b><span>企业法人</span>
                        </td>
                        <td style="padding-left:20px;">
                        	<input type="text" id="legalperson" data-checked="notnull" data-inval="企业法人姓名不能为空">
                        </td>
                    </tr>
                   <!--错误提示信息 -->
                	<tr>
                    	<td>                       	
                        </td>
                        <td style="padding-left:20px;">
                        	<em class="legalperson"></em>
                        </td>
                    </tr>
                    <!-- -->                      
                    <!--循环行 -->
                	<tr>
                    	<td style="text-align:right">
                        	<b>*</b><span>注册资本</span>
                        </td>
                        <td style="padding-left:20px;">
                        	<input type="text" id="capital" data-checked="notnull" data-inval="请输入注册资本">
                        </td>
                    </tr>
                   <!--错误提示信息 -->
                	<tr>
                    	<td>                       	
                        </td>
                        <td style="padding-left:20px;">
                        	<em class="capital"></em>
                        </td>
                    </tr>
                    <!-- -->                      
                    <!--循环行 -->
                    <tr>
                    	<td style="text-align:right">
                        	<b>*</b><span>营业执照号</span>
                        </td>
                        <td style="padding-left:20px;">
                        	<input id="bizlicence" type="text" data-checked="notnull" data-inval="营业执照号不能为空" >
                        </td>
                    </tr>
                   <!--错误提示信息 -->
                	<tr>
                    	<td>                       	
                        </td>
                        <td style="padding-left:20px;">
                        	<em class="bizlicence"></em>
                        </td>
                    </tr>
                    <!-- -->                       
                	<tr>
                    	<td style="text-align:right">
                        	<b>*</b><span>组织机构代码证号</span>
                        </td>
                        <td style="padding-left:20px;">
                        	<input id="groupcode" type="text" data-checked="notnull" data-inval="组织机构代码证号不能为空">
                        </td>
                    </tr>
                   <!--错误提示信息 -->
                	<tr>
                    	<td>                       	
                        </td>
                        <td style="padding-left:20px;">
                        	<em class="groupcode"></em>
                        </td>
                    </tr>
                    <!-- -->                      
                    <!--循环行 -->
                	<tr>
                    	<td style="text-align:right">
                        	<b>*</b><span>税务登记证号</span>
                        </td>
                        <td style="padding-left:20px;">
                        	<input type="text" id="taxcode" data-checked="notnull" data-inval="税务登记证号不能为空">
                        </td>
                    </tr>
                   <!--错误提示信息 -->
                	<tr>
                    	<td>                       	
                        </td>
                        <td style="padding-left:20px;">
                        	<em class="taxcode"></em>
                        </td>
                    </tr>
                    <!--循环行 -->
                	<tr>
                    	<td style="text-align:right">
                        	<b>*</b><span>基本开户行</span>
                        </td>
                        <td style="padding-left:20px;">
                        	<input type="text" id="bank" data-checked="notnull" data-inval="基本开户行不能为空">
                        </td>
                    </tr>
                   <!--错误提示信息 -->
                	<tr>
                    	<td>                       	
                        </td>
                        <td style="padding-left:20px;">
                        	<em class="bank"></em>
                        </td>
                    </tr>                    
                    <!-- -->     
                     <!--循环行 -->
                	<tr>
                    	<td style="text-align:right">
                        	<b>*</b><span>验证码</span>
                        </td>
                        <td style="padding-left:20px;">
                        	<input type="text" id="rndcode" data-checked="notnull" data-inval="验证不能为空">
                        	<img id="rnd-code-img" src="/rndimg"> <a onclick='$("#rnd-code-img").attr("src", "/rndimg?" + new Date().getTime());'>刷新验证码</a>
                        </td>
                    </tr>
                   <!--错误提示信息 -->
                	<tr>
                    	<td>                       	
                        </td>
                        <td style="padding-left:20px;">
                        	<em></em>
                        </td>
                    </tr>                    
                    <!-- --> 
                    
                	<tr>
                    	<td>                       	
                        </td>
                        <td style="padding-left:20px;">
                        	<input class="active-btn" onclick="reg();" type="button" value="去借款">
                        </td>
                    </tr>
                    <!-- -->                                       
                    <!--循环行 -->                                                                                                    
                </table>
                <!-- -->
            </div>
            <!-- -->
            <div class="yinying">
        	</div>
        </div>


    <!--内容结束 -->
    </div>



	

<%@ include file="foot.jsp"%> 
	
</body>
</html>