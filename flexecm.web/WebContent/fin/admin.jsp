<%@page import="com.ever365.rest.AuthenticationUtil"%>
<%@ page contentType="text/html;charset=UTF-8"%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>融信担保-后台管理</title>

<link type="text/css" rel="stylesheet" href="css/admin.css">
<script type="text/javascript" src="js/jquery-2.1.0.min.js"></script>
<script type="text/javascript" src="js/webcommon.js"></script>

<%
	if(request.getSession().getAttribute(AuthenticationUtil.SESSION_ADMIN)==null) {
		response.getWriter().println("用户已失效，请点击<a href='login.jsp'>重新登陆</a>");
		return;
	}
%>
<script type="text/javascript" src="js/admin.js"></script>

</head>
<body>

<div class="top">
	<div class="wrapper">
		<span class="title">融信担保-后台管理</span>
		<ul class="nav">
			<li class="navuser">用户管理</li>
			<li class="navproject">项目管理</li>
			<li class="navcontent">内容管理</li>
			<li class="navmanage">后台管理</li>
		</ul>
		
		<a>退出系统</a>
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
			<li>项目库</li>
			<li>初审列表</li>
			<li>复审列表</li>
			<li>未通过项目</li>
			<li>已通过项目</li>
			<li>还款完结项目列表</li>
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
	</div>
</div>

<div class="gridr clearfix">

	<div class="form companyEdit hidden">
		<div class="title">
			<h3>增加/编辑企业信息</h3> 
			<div class="ops">
				<a class="btn read" onclick="toggleEditOrg();">编辑</a>
			</div>
		</div>
		<div class="fcg">
			<label>公司全称（同营业执照）</label> 
			<div class="fc">
				<input id="company" type="text" value="" data-checked="notnull" data-inval="公司全称 不能为空">
			</div>
		</div>
		<div class="fcg">
			<label>用户名</label> 
			<div class="fc">
				<input id="loginid" type="text" value="" data-checked="notnull"  data-inval="用户名不能为空">
			</div>
		</div>
		<div class="fcg">
			<label>密码</label> 
			<div class="fc">
				<input id="pwd" type="text" value="" data-checked="length-6" data-inval="密码要大于6位"> <a class="btn edit" href="javascript:$('#pwd').val(genPass());">自动生成</a>
			</div>
		</div>
		<div class="fcg">
			<label>联系人</label> 
			<div class="fc">
				<input id="contact" type="text" data-checked="notnull" data-inval="请输入姓名">	
			</div>
		</div>
		<div class="fcg">
			<label>联系人手机</label> 
			<div class="fc">
				<input id="mobile" type="text" data-checked="mobile" data-inval="请输入有效的手机号码">
			</div>
		</div>
		<div class="fcg">
			<label>联系人邮箱</label> 
			<div class="fc">
				<input id="email" type="text" data-checked="email" data-inval="请输入有效的电子邮件地址">
			</div>
		</div>
		<div class="fcg">
			<label>注册地址</label> 
			<div class="fc">
				<input type="text" id="location" data-checked="notnull" data-inval="注册地址不能为空">
			</div>
		</div>
		<div class="fcg">
			<label>成立时间</label> 
			<div class="fc">
				<input type="text" id="established" data-checked="notnull" data-inval="成立时间不能为空">
			</div>
		</div>
		<div class="fcg">
			<label>企业法人</label> 
			<div class="fc">
				<input type="text" id="legalperson" data-checked="notnull" data-inval="企业法人姓名不能为空">
			</div>
		</div>
		<div class="fcg">
			<label>注册资本</label> 
			<div class="fc">
				<input type="text" id="capital" data-checked="notnull" data-inval="请输入注册资本">
			</div>
		</div>
		<div class="fcg">
			<label>营业执照号</label>
			<div class="fc">
				<input id="bizlicence" type="text" data-checked="notnull" data-inval="营业执照号不能为空" >
			</div>
		</div>
		<div class="fcg">
			<label>组织机构代码证号</label> 
			<div class="fc">
				<input id="groupcode" type="text" data-checked="notnull" data-inval="组织机构代码证号不能为空">
			</div>
		</div>
		<div class="fcg">
			<label>税务登记证号</label> 
			<div class="fc">
				<input type="text" id="taxcode" data-checked="notnull" data-inval="税务登记证号不能为空">
			</div>
		</div>
		<div class="fcg">
			<label>基本开户行</label> 
			<div class="fc">
				<input type="text" id="bank" data-checked="notnull" data-inval="基本开户行不能为空">
			</div>
		</div>
		
		<div class="fcg">
			<label>信用评级</label> 
			<div class="fc">
				<input type="text" id="crating" data-checked="notnull" data-inval="信用评级不能为空">
			</div>
		</div>
		<div class="hidden">
			<input type="text" id="_id" value="">
		</div>
		
		<div class="fcg">
			<a class="btn edit" onclick="saveOrg();">保存</a>
		</div>
	</div>
	
	<div class="accounts">
		<div class="panel">
			<div class="fr">
				<input class="search" placeholder="输入用户ID"><a class="find">查找</a>
			</div>
		</div>
		
		<div class="table">
		    <div class="row header">
		      <div class="cell" style="width: 100px;">
		        	帐号
		      </div>
		      <div class="cell">
		       		姓名
		      </div>
		      <div class="cell">
		       		身份证
		      </div>
		      <div class="cell">
		       		 手机
		      </div>
		      <div class="cell">
		      		邮箱
		      </div>
		      <div class="cell">
		      		状态
		      </div>
		    </div>
	    <div class="template">
	      <div class="cell location">
	      </div>
	      <div class="cell company">
	      </div>
	      <div class="cell bizlicence">
	      </div>
	      <div class="cell loginid">
	      </div>
	      <div class="cell projects">
	      </div>
	      <div class="cell crating">
	      </div>
	    </div>
	</div>	
	
		 <div class="empty">
		    	列表内容为空
		</div>
	
	</div>

	<div class="org">
		<div class="panel">
			<a class="btn" onclick="addGreenOrg();">增加绿色通道企业</a>
			<div class="fr">
				<input class="search" placeholder="输入企业名称"><a class="find">查找</a>
			</div>
		</div>

		<div class="table">
		    <div class="row header">
		      <div class="cell" style="width: 100px;">
		        	所在区
		      </div>
		      <div class="cell">
		       		企业名称
		      </div>
		      <div class="cell">
		       		营业执照号
		      </div>
		      <div class="cell">
		       		 借款人平台账号
		      </div>
		      <div class="cell">
		      		借款次数
		      </div>
		      <div class="cell">
		      		信用等级
		      </div>
		    </div>
	    <div class="template">
	      <div class="cell location">
	      </div>
	      <div class="cell company">
	      </div>
	      <div class="cell bizlicence">
	      </div>
	      <div class="cell loginid">
	      </div>
	      <div class="cell projects">
	      </div>
	      <div class="cell crating">
	      </div>
	    </div>
	</div>
	
	 <div class="empty">
	    	列表内容为空
	</div>
	</div>    

</div>

</div> <!-- wrapper end -->
</body>
</html>
