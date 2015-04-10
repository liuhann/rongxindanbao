<%@page import="java.util.Map"%>
<%@page import="com.ever365.comment.CommentFriendService"%>
<%@page import="org.springframework.web.context.support.WebApplicationContextUtils"%>
<%@page import="org.springframework.web.context.WebApplicationContext"%>
<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>

<%

WebApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(application);
CommentFriendService commentFriendService = (CommentFriendService) context.getBean("comment.service");

Map<String, Object> article = commentFriendService.getArticle(request.getParameter("id"));

%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	
	<link rel="StyleSheet" href="css/index.css" type="text/css">
	
	<title></title>
</head>
<body>


<div class="top">
	<div class="wrapper clearfix">
		<div class="logo"></div>
		<div class="search">
			<input type="text" id="searchContext" placeholder="搜索关键字或提交文章链接"> <a class="searchbtn" onclick="searchOrImport();">GO</a>
		</div>
		
		<div class="nav"></div>
		
		<div class="user"></div>
	</div>
</div>

<div class="wrapper">
	<div class="article-container clearfix">
		<div class="meta">
			<h2 class="title"><%=article.get("title") %></h2>
			<div class="info"><span class="date"><%=article.get("date") %></span></div>
		</div>
		
		<div class="content">
			<%=article.get("html") %>
		</div>
		
		
		<div class="postArea">
			<div class="user"></div>
			<div class="tie">
				<form>
					<textarea id="poster"></textarea>
				</form>
			</div>
			<div>
				<a class="btn">马上发表</a>
			</div>
		</div>
		
		<div class="comments"></div>
	</div>
	
	<div class="side">
		
		<div class="stastics">
	
		</div>
	</div>
</div>



</body>
</html>