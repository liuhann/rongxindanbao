<%@ page contentType="text/html;charset=UTF-8"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>个人注册</title>

<SCRIPT type=text/javascript src="js/jquery-1.11.2.min.js"></SCRIPT>
<SCRIPT type=text/javascript src="datepicker/jquery.datetimepicker.js"></SCRIPT>
<script type="text/javascript" src="js/webcommon.js"></script>

<!--css -->
<link type="text/css" rel="stylesheet" href="css/base.css"><!--通用CSS样式 -->
<link type="text/css" rel="stylesheet" href="css/top.css">

<link type="text/css" rel="stylesheet" href="datepicker/jquery.datetimepicker.css">
<!--jquery -->

<style>

.active-btn {
	cursor: pointer;
}
a {
	cursor: pointer;
}
</style>

<script type="text/javascript">


function reg() {
	var check = formCheck(".form-content ");

	if (check.test()) {
		$.post("/service/fin/account/reg",  check.getRequest() , function() {
			location.href = "confirmEmail.jsp";
		}).fail(function(error) {
			alert("用户名已经被注册了，请更换");
		});
	}
	
}

$(function() {
	
	$.getJSON("/service/fin/account/current", {}, function() {
				
	}).fail(function(error) {
		alert("请登陆");
				
	});
	
});


</script>

</head>
<body class="bg">
<%@ include file="top.jsp"%> 


<div id="content">
    <!--内容开始 -->
    	<div class="regform step1">
        	<!-- -->
            <div class="title">
            	<span>请验证您的邮箱地址</span>
            </div>

			<div class="content">
				<div>一封邮件已经发送到您的邮箱 <span id="email"></span> <a>不是这个邮箱？</a></div>
				<div>请输入您邮箱收到的8位数字 <input id="cfmcode" type="text"></div>
				
			</div>
            <div class="form-content">
            	<!-- -->
                <table>
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
                	<tr>
                    	<td style="text-align:right">
                        	<b>*</b><span>手机号码</span>
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
                        	<b>*</b><span>电子邮箱</span>
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
                    
                    <tr>
                    	<td>                       	
                        </td>
                        <td style="padding-left:20px;">
                        	<input class="active-btn" onclick="reg();" type="button" value="注册">
                        </td>
                    </tr>
                    
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