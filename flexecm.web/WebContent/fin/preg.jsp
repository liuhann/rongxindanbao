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

<style type="text/css">

@charset "utf-8";
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
	var check = formCheck(".form-content ");
	var request = check.getRequest();
	request.type = "person";
	if (check.test()) {
		$.post("/service/fin/account/reg",  request , function() {
			location.href = "home.jsp";
		}).fail(function(error) {
			if (error.status==412) {
				alert("验证码输入错误");
			} else {
				alert("用户名已经被注册了，请更换");
			}
		});
	}
}

$(function() {
	var check = formCheck(".form-content ");
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
            	<span>个人用户注册</span>
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