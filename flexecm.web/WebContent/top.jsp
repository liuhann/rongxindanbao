<%@page import="java.util.Map"%>
<%@page import="org.springframework.web.context.ContextLoaderListener"%>
<%@page import="com.ever365.fin.FinanceService"%>
<%@page import="java.util.Date"%>
<%@page import="com.ever365.rest.AuthenticationUtil"%>
<%@ page contentType="text/html;charset=UTF-8"%>

<%
AuthenticationUtil.setCurrentUser(null);
Object user = request.getSession().getAttribute(AuthenticationUtil.SESSION_CURRENT_USER);
if (user != null) {
	AuthenticationUtil.setCurrentUser((String) user);
} 
Date date = new Date();
%>
<link type="text/css" rel="stylesheet" href="css/top.css">
    <!--头部开始 -->
<div id="top">
	<div class="top-lv01">
		<div class="w1150">
			<div class="l">
				<span>今天是</span>
				<span><%=1900+date.getYear() %>年<%=date.getMonth()+1 %>月<%=date.getDate() %>日</span>
			</div>

			<div class="r">
				<%if(AuthenticationUtil.getCurrentUser()==null) { %>
					<span><a style="color:#666;" href="preg.jsp">个人注册</a></span>
					<img src="img/index_004.png" class="sep">
					<span><a style="color:#666;" href="orgreg.jsp">企业注册</a></span>
					<img src="img/index_004.png" class="sep">
					<span class="nohot"><a style="color:#666;" href="login.jsp">快速登录</a>
				</span>
				<%} else {
					FinanceService finService = ((FinanceService) ContextLoaderListener
							.getCurrentWebApplicationContext().getBean("fin.service"));
				%>
					<span class="cls-mr"><a class="u-line" href="#"><%=finService.getCurrentUser().get("rname") %></a></span>
					<span>欢迎您来融信融资网</span>
					<span><a href="/service/fin/logout">【安全退出】</a></span>
				<%} %>
			</div>
		</div>
	</div>
	<!-- -->
	<div class="top-lv02">
		<div class="l">
			<a href="index.jsp"><img src="img/index_009.png"></a>
		</div>

		<div class="r">
			<a href="home.jsp?request">快速融资</a>
		</div>
	</div>
	<!-- -->