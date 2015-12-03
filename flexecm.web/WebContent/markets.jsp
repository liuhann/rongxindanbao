<%@ page contentType="text/html;charset=UTF-8"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Map"%>
<%@page import="org.springframework.web.context.ContextLoaderListener"%>
<%@page import="com.ever365.fin.FinanceService"%>
<%@page import="com.ever365.rest.AuthenticationUtil"%>
<%@page import="java.util.Date"%>
<%@ page import="com.ever365.utils.StringUtils" %>

<!doctype html>
<html>
<head>

<meta charset="utf-8">
<title>融信担保-金融超市</title>
<!--css -->
<link type="text/css" rel="stylesheet" href="css/base.css"><!--通用CSS样式 -->
<link type="text/css" rel="stylesheet" href="css/xinwen.css">
<!--jquery -->
</head>

<body class="bg">
<!-- -->
<%@ include file="top.jsp"%>
<div class="top-lv03">
    <ul>
        <li><a href="index.jsp">首页</a></li>
        <li ><a href="elogin.jsp">融资入口</a></li>
        <li><a href="plogin.jsp">投资入口</a></li>
        <li><a href="res.jsp">资金供应</a></li>
        <li class="current"><a href="markets.jsp" >金融超市</a></li>
        <li><a href="/news-view.jsp?id=56556d060cf251a9d0946900">联系我们</a></li>
    </ul>
</div>
</div>


    <div id="content">
<!--内容开始 -->
    <div class="xinwen_001">
        <cite>金融超市</cite>
        <%
            FinanceService finService = ((FinanceService) ContextLoaderListener
                    .getCurrentWebApplicationContext().getBean("fin.service"));
            Map<String, Object> map = finService.filterCollection("finamarkets", null, 0, 100);
            List<Map> marketLists = (List)map.get("list");
        %>

        <ul class="xinwen_list" style="height: 500px;">
            <%
                for(Map market : marketLists) {
            %>
                <li>
                    <div class="l">
                        <span>
                        <b>·</b><a href="fm-view.jsp?id=<%=market.get("_id")%>"><%=market.get("name")%></a>
                        </span>
                    </div>

                    <div class="r">
                        <span><%=market.get("corporg").toString()%></span>
                    </div>
                </li>
            <%
                }
            %>
        </ul>

<div class="photo_news">
<div class="photo-item">
<img src="img/xinwen_001.png">
</div>

<div class="photo-block">
</div>
</div>


</div>
<div class="yinying">
</div>
<!--内容结束 -->
</div>
<!-- -->
    <%@ include file="foot.jsp" %>

    </body>
</html>
