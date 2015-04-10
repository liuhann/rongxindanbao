<%@page import="java.util.Map"%>
<%@page import="com.ever365.fin.FinanceService"%>
<%@page import="org.springframework.web.context.ContextLoaderListener"%>
<%@page import="com.ever365.rest.HttpServiceRegistry"%>
<%@ page contentType="text/html;charset=UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>融信担保-用户首页</title>

<SCRIPT type=text/javascript src="js/jquery-1.11.2.min.js"></SCRIPT>
<script type="text/javascript" src="js/webcommon.js"></script>


<script type="text/javascript" src="js/sitedata_bas.js"></script>

<!--css -->
<link type="text/css" rel="stylesheet" href="css/base.css"><!--通用CSS样式 -->
<link type="text/css" rel="stylesheet" href="css/top.css">
<link type="text/css" rel="stylesheet" href="css/admin.css">
<link type="text/css" rel="stylesheet" href="css/zhanghuguanli.css">
<link type="text/css" rel="stylesheet" href="css/zhanghuguanli_jksq.css">

<style>

.active-btn {
	cursor: pointer;
}
a {
	cursor: pointer;
}
table {
	width: 100%;
}
li {
	cursor: pointer;
}

.zhanghuguanli_jksq_001 .r .lv03 table tr td a {
	float: none;
	display: inline-block;
}
</style>

<script type="text/javascript">


$(function() {
	
});

function dashboard(t) {
	navTo(t);
	
	$("#content .r").hide();
	$(".r.dashboard").show();
	
}

function config(t) {
	navTo(t);
	$("#content .r").hide();
	$(".r.personEdit").show();
}


function goRequest(t) {
	navTo(t);
	$("#content .r").hide();
	$(".r.personRequest").show();
	
	var fo = new formCheck(".r.personRequest ");
	
}

function navTo(t) {
	$(".menu_list li.current").removeClass("current");
	$(t).addClass("current");
}

function checkEmail() {
	$.getJSON("/service/fin/account/email/confirm", {
		"ecfm": $("#ecfm").val()
	}, function(data) {
		if (data=="1") {
			alert("邮件验证成功");
			location.href = location.href; 
		} else {
			alert("邮件验证码错误");
		}
	})
}

function resendEmail() {
	$.get("/service/fin/account/email/resend", {}, function() {
		alert("验证邮件已经再次发送，请查收");
	});
}

function sendLoanRequest() {
	var fo = new formCheck(".r.personRequest ");
	
	if (fo.test()) {
		
	}
}

</script>

</head>
<body class="bg">
<%@ include file="top.jsp"%> 

<%
	FinanceService finService = ((FinanceService) ContextLoaderListener
			.getCurrentWebApplicationContext().getBean("fin.service"));
	
	Map<String, Object> currentUser = finService.getCurrentUser();
	
	if (currentUser.get("cu")==null) {
		response.sendRedirect("login.jsp");
	}

%>


<div id="content">
    <!--内容开始 -->
    	<div class="zhanghuguanli_001 zhanghuguanli_jksq_001">
			<div class="l">
            	<div class="icon">
                	<img src="img/zhanghuguanli_001.png">
                    <span><a href="javascript:void(0)"><%=currentUser.get("loginid") %></a>您好!</span>
                </div>
                <div class="menu_list">
                	<ul>
                    	<li class="current" onclick="dashboard(this);">
                        	账户总览
                        </li>
                    	<li onclick="goRequest(this);">
                        	借款申请&gt;
                        </li>
                    	<li>
                        	<a href="#">借款进度&gt;</a>
                        </li>
                    	<li>
                        	<a href="#">评估报告&gt;</a>
                        </li>
                    	<li>
                        	<a href="#">信用评级&gt;</a>
                        </li>
                    	<li onclick="config(this);">
                        	信息设置&gt;
                        </li>                        
                                                                                                
                    </ul>
                </div>
            </div>

			<div class="r dashboard">
				
				<%if (!Boolean.TRUE.equals(currentUser.get("ecfm"))) { %>
				<div class="warn">
					<p>您未验证邮箱，请输入8位验证码: <input id="ecfm"> <a class="btn" onclick="checkEmail();">验证</a><p>
					<p>如果您的邮箱未收到验证码，请<a  onclick="resendEmail();">点击重新发送</a><p>
				</div>
				<%} %>
            	<div class="r_block_1">
                	<table>
                    	<tr>
                        	<td style="width:25%">
                            	<div class="gd">
                                	<cite>借款总金额</cite>
                                    <span>￥0<b>元</b></span>
                                </div>
                            </td>
                        	<td style="width:25%">
                            	<div class="gd">
                                	<cite>成功借款金额</cite>
                                    <span>￥0<b>元</b></span>
                                </div>
                            </td>
                        	<td style="width:25%">
                            	<div class="gd">
                                	<cite>成功借款笔数</cite>
                                    <span>0<b>次</b></span>
                                </div>
                            </td>
                        	<td style="width:25%">
                            	<div class="gd" style="border-right:none">
                                	<cite>借款通过率</cite>
                                    <span>100<b>%</b></span>
                                </div>
                            </td>
                        </tr>
                    </table>
                </div>
                
                <div class="r_block_2">
               		<span>年度借款总结：</span>
                </div>
                
                <div class="r_block_3" >
                	借款分析
                </div>
            </div>
            
			<div class="r form personEdit hidden">
			<div class="title">
				<h3>编辑个人信息</h3> 
			</div>
			<div class="fcg">
				<label>用户名</label> 
				<div class="fc">
					<%=currentUser.get("loginid") %>
				</div>
			</div>
			<div class="fcg">
				<label>重设密码</label> 
				<div class="fc">
					<input id="pwd" type="password" value="" data-checked="length-6" data-inval="密码要大于6位"> <a class="btn edit" href="javascript:resetPwd();">重设</a>
				</div>
			</div>
			<div class="fcg">
				<label>手机号码</label> 
				<div class="fc">
					<input id="mobile" type="text" data-checked="mobile" value="<%=currentUser.get("mobile") %>" data-inval="请输入有效的手机号码">
				</div>
			</div>
			<div class="fcg">
				<label>电子邮箱</label> 
				<div class="fc">
					<input id="email" type="text" data-checked="email" value="<%=currentUser.get("email") %>" data-inval="请输入有效的电子邮件地址">  
				</div>
			</div>
			<div class="fcg">
				<a class="btn edit" onclick="saveOrg();">保存</a>
			</div>
		</div>

            <div class="r  personRequest hidden">
            	<div class="lv01">
                	<a href="#" class="sele-bg">继续填写</a>
                    <a href="#">新填一个</a>
                </div>
                
                <div class="lv02">
               	  <ul>
                    	<li>
                       		<span class="first sele"><a href="#">基本信息</a></span><!--sele即为选中的样式 -->
                        </li>
                   	<li>
                       		<span><a href="#">借款历史</a></span>
                        </li>
                   	<li>
                       		<span><a href="#">身份信息</a></span>
                        </li>
                   	<li>
                       		<span class="end"><a href="#">补充材料</a></span>
                        </li>                                                                        
                  </ul>
                </div>
                
              <div class="lv03">
                	<table>
                    	<!--表单项 -->
                    	<tr>
                        	<td style="width:82px; text-align:right;">
                            	<span>贷款金额</span>
                            </td>
                            <td style="width:383px;">
                            	<input class="form-text" id="loan" type="text" value="" data-checked="number+"  data-inval="贷款金额必须是数字">
                                <b>万元</b>
                            </td>
                        </tr>
                        <!--错误提示信息 -->
                    	<tr>
                        	<td>
                            	
                            </td>
                            <td>
                           		<em class="loan"></em>
                            </td>
                        </tr>
                        <!-- -->                        
                    	<!--表单项 -->
                    	<tr>
                        	<td style="text-align:right;">
                            	<span>贷款期限</span>
                            </td>
                            <td>
                            	<input class="form-text" type="text" id="until" value="" data-checked="number+"  data-inval="贷款期限必须是数字">
                                <b>个月</b>
                            </td>
                        </tr>
                        <!--错误提示信息 -->
                    	<tr>
                        	<td>
                            	
                            </td>
                            <td>
                           		<em class="until"></em>
                            </td>
                        </tr>
                        <!-- -->                         
                  	<!--表单项 -->
                    	<tr>
                        	<td style="text-align:right;">
                            	<span>居住城市</span>
                            </td>
                            <td>
								<select name="" id="provinces" class="form-select">
                                </select>
                                
								<select id="cities" name="" class="form-select">
                                </select>                                
                            </td>
                        </tr>
                        <!--错误提示信息 -->
                    	<tr>
                        	<td>
                            	
                            </td>
                            <td>
                           		<em></em>
                            </td>
                        </tr>
                        <!-- -->                        
                  	<!--表单项 -->
                    	<tr>
                        	<td style="text-align:right;">
                            	<span>您的称谓</span>
                            </td>
                            <td>
								<input name="appellation" type="radio" value=""><i>女士</i>
								<input name="appellation" type="radio" checked="checked"><i>先生</i>                                
                            </td>
                        </tr>
                        <!--错误提示信息 -->
                    	<tr>
                        	<td>
                            	
                            </td>
                            <td>
                           		<em></em>
                            </td>
                        </tr>
                        <!-- -->                         
                    	<!--表单项 -->
                    	<tr>
                        	<td style="text-align:right;">
                            	<span>您的姓名</span>
                            </td>
                            <td>
                            	<input id="name" class="form-text" type="text" value="" data-checked="notnull"  data-inval="请输入您的姓名">
                            </td>
                        </tr>
                        <!--错误提示信息 -->
                    	<tr>
                        	<td>
                            	
                            </td>
                            <td>
                           		<em class="name"></em>
                            </td>
                        </tr>
                        <!-- -->                         
                    	<!--表单项 -->
                    	<tr>
                        	<td style="text-align:right;">
                            	<span>手机号码</span>
                            </td>
                            <td>
                            	<span><%=currentUser.get("mobile") %></span>
                                <a href="#">不用这个手机？</a>
                            </td>
                        </tr>
                        <!--错误提示信息 -->
                    	<tr>
                        	<td>
                            	
                            </td>
                            <td>
                           		<em></em>
                            </td>
                        </tr>
                        <!-- -->  
                    	<!--表单项 -->
                    	<tr>
                        	<td style="text-align:right;">
                            	<span>常用邮箱</span>
                            </td>
                            <td>
                            	<%=currentUser.get("email") %>
                                <a href="#">不用这个邮箱？</a>
                            </td>
                        </tr>
                        <!--错误提示信息 -->
                    	<tr>
                        	<td>
                            	
                            </td>
                            <td>
                           		<em></em>
                            </td>
                        </tr>
                        <!-- -->
                    	<!--表单项 -->
                    	<tr>
                        	<td style="text-align:right;">

                            </td>
                            <td>
								<input class="form-button" name="" type="button" value="保存">
                                <input class="form-button" onclick="sendLoanRequest();" type="button" value="提交请求">
                            </td>
                        </tr>
                        <!-- -->                                                           
                                      
                        
                    </table>
                </div>
            </div>
            <div class="yinying">
            </div>
        </div>
        
        
    <!--内容结束 -->
</div>

	

<%@ include file="foot.jsp"%> 
	
</body>
</html>