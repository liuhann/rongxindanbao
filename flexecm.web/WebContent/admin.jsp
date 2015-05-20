<%@page import="com.ever365.rest.AuthenticationUtil"%>
<%@ page contentType="text/html;charset=UTF-8"%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>融信担保-后台管理</title>

<%
	if(request.getSession().getAttribute(AuthenticationUtil.SESSION_ADMIN)==null) {
		response.getWriter().println("用户已失效，请点击<a href='login.jsp'>重新登陆</a>");
		return;
	}
%>

<link type="text/css" rel="stylesheet" href="css/admin.css">
<script type="text/javascript" src="js/jquery-2.1.0.min.js"></script>
<script type="text/javascript" src="js/webcommon.js"></script>
<script type="text/javascript" src="js/json2.js"></script>

<script charset="utf-8" src="js/kindeditor/kindeditor-min.js"></script>
<script charset="utf-8" src="js/kindeditor/zh_CN.js"></script>

<script type="text/javascript" src="js/admin.js"></script>
</head>
<body>

<div class="top">
	<div class="wrapper">
		<span class="title">融信担保-后台管理</span>
		<ul class="nav">
			<li class="navuser" onclick="navTo(this);">用户管理</li>
			<li class="navproject" onclick="navTo(this);">项目管理</li>
			<li class="navcontent" onclick="navTo(this);">内容管理</li>
			<li class="navmanage" onclick="navTo(this);">后台管理</li>
		</ul>
		<a href="/service/fin/logout">退出系统</a>
	</div>
</div>

<div class="wrapper">
<div class="gridl clearfix">
	<div class="box navuser">
		<div class="title">
			<h2>用户管理</h2>
		</div>
		<ul>
			<li onclick="pageUnConfirmed();">待认证用户</li>
			<li onclick="pageConfirmed();">已认证用户</li>
			<li onclick="pageCompanys();">绿色通道企业用户</li>
			<li onclick="pageLoanOfficer();">信贷经理</li>
		</ul>
	</div>
	
	<div class="box navproject hidden">
		<div class="title">
			<h2>项目管理</h2>
		</div>
		<ul>
			<li onclick="pageAllLoans();">项目库</li>
			<li onclick="pageFirstAudit();">初审列表</li>
			<li onclick="pageFinalAudit();">复审列表</li>
			<li onclick="pageKilledLoans();">未通过项目</li>
			<li onclick="pagePassedLoans();">已通过项目</li>
			<li onclick="pageFinishedLoans();">还款完结项目列表</li>
		</ul>
	</div>
	
	<div class="box navcontent hidden">
		<div class="title">
			<h2>理财产品来源管理</h2>
		</div>
		<ul>
			<li>P2P</li>
			<li>银行理财</li>
			<li>货币基金</li>
		</ul>
		
		<div class="title">
			<h2>新闻公告</h2>
		</div>
		<ul>
			<li onclick="addNews();">增加新闻公告</li>
			<li onclick="listNews();">新闻列表</li>
		</ul>
	</div>
	<div class="box navmanage hidden">
		<div class="title">
			<h2>理财产品来源管理</h2>
		</div>
		<ul>
			<li onclick="editAdmin();">增加管理员</li>
			<li onclick="adminList();">管理员列表</li>
			<li onclick="backAccountList();">用户管理</li>
			<li onclick="rolesList();">角色管理</li>
			<li>用户授权</li>
		</ul>
	</div>
</div>

<div class="gridr clearfix" id="ccontent">
	<!-- 
	<div id="view-company-loan" class="form">
		<div class="title">
			<h3>申请项目信息</h3> 
			<div class="ops">
				<a class="btn read" onclick="toggleEditOrg();">编辑</a>
				<a class="btn read" onclick="toggleEditOrg();">编辑</a>
			</div>
		</div>
		<div class="subtitle">
			<h3>基本资料</h3>
		</div>
		<div class="fcg">
			<label>贷款金额 </label> 
			<div class="fc">
				<input style="width:4rem;" id="loan" type="text" value="" data-checked="number+" data-inval="金额必须为正整数"> 万元
			</div>
		</div>
		<div class="fcg">
			<label>贷款期限</label> 
			<div class="fc">
				<input style="width:4rem;" id="duration" type="text" value="" data-checked="number+" data-inval="贷款时间为正整数"> 个月
			</div>
		</div>
		<div class="fcg">
			<label>居住城市</label> 
			<div class="fc">
				<select class="form-sele1" id="provinces"></select>
				<select class="form-sele1" id="cities">
				  <option >请选择</option>
				</select>                 
			</div>
		</div>
		<div class="fcg">
			<label>您的称谓</label> 
			<div class="fc">
				<input name="appellation" type="radio" value=""><span>女士</span>
				<input name="appellation" type="radio" checked="checked"><span>先生</span>         
			</div>
		</div>
		<div class="fcg">
			<label>您的姓名</label> 
			<div class="fc">
				<input class="form-text" type="text" value="" id="name" data-checked="notnull" data-inval="姓名不能为空">
			</div>
		</div>
		<div class="fcg">
			<label>手机号码</label> 
			<div class="fc">
				<input class="form-text" type="text" value="" id="mobile" data-checked="mobile" data-inval="请输入正确的手机号码">
			</div>
		</div>
		<div class="fcg">
			<label>	</label> 
			<div class="fc">
				<input class="form-text" type="text" value="" id="email" data-checked="email" data-inval="请输入正确的电子邮件">
			</div>
		</div>
		
		<div class="subtitle">
			<h3>借款主体</h3>
		</div>
		
		<div class="fcg">
			<label>借款主体</label> 
			<div class="fc">
				<input class="form-text" type="text" value="" id="email" data-checked="email" data-inval="请输入正确的电子邮件">
			</div>
		</div>
	</div>
		 -->
</div>
</div> <!-- wrapper end -->

<div class="img-preview">
	<div class="bimg">
		<img>
	</div>
</div>
</body>
</html>
