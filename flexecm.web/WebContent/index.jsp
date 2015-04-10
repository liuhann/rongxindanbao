<!DOCTYPE html>
<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Map"%>
<%@page import="org.springframework.web.context.support.WebApplicationContextUtils"%>
<%@page import="org.springframework.web.context.WebApplicationContext"%>
<%@page import="com.ever365.comment.CommentFriendService"%>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<meta charset="UTF-8">
	<title>评友</title>

<%
WebApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(application);
CommentFriendService commentFriendService = (CommentFriendService) context.getBean("comment.service");

Map<String, Object> fronts = commentFriendService.getFront();
%>

<SCRIPT type=text/javascript src="js/jquery.min.js"></SCRIPT>
<SCRIPT type=text/javascript src="js/index.js"></SCRIPT>
<link rel="StyleSheet" href="css/index.css" type="text/css">

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
	<div class="article-list clearfix">
			<%
			List<Map<String, Object>> articles = (List<Map<String, Object>>)fronts.get("all");
			for(Map<String, Object> article: articles) {
			%>
				<div class="article">
					<div class="title">
						<span class="head">
							<a><img src="<%=article.get("avtar")%>"></a>
						</span>
						<h2><a href="article.jsp?id=<%=article.get("id")%>"><%=article.get("title") %></a></h2>
					</div>
			
					<div class="abstract">
						<%=article.get("some") %> ......
					</div>
					<div class="intera">
						<a class="comment">
							<i></i>
							<span><%=article.get("comments") %>评论</span>
						</a>
						<a class="fr">作者： <%=article.get("author") %></a>
					</div>
				</div>
			<%
			}
			%>
	</div>
	
	<div class="side">
		<div class="stastics">
			<div class="title">评友</div>
			<div class="desc">讨论从互相尊重开始</div>
			<div class="topics"><%=fronts.get("article") %>份文章</div>
			<div class="comments"><%=fronts.get("comment") %>个讨论</div>
		</div>
	</div>
</div>

<!-- 
<div class="container show">
	<div class="wrapper">
		<div class="slide">
			<ul>
			<%
			List<Map<String, Object>> cards = (List<Map<String, Object>>)fronts.get("cards");
			for(Map<String, Object> card: cards) {
			%>
				<li class="card">
					<div class="preview" style="background-image: url('<%=card.get("cover")%>'); "></div>
					<h2 class="title"><%=card.get("title") %></h2>
					<div class="author"><%=card.get("author") %> <%=card.get("comments") %>评论</div>
				</li>
			<%	
			}
			%>
			</ul>
		</div>
		
	</div>
</div>


<div class="container">
	<div class="wrapper">
	
	
		
	</div>
</div>
 -->
</body>
</html>