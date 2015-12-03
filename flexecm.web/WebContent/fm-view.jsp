<%@ page contentType="text/html;charset=UTF-8"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Map"%>
<%@page import="org.springframework.web.context.ContextLoaderListener"%>
<%@page import="com.ever365.fin.FinanceService"%>
<%@page import="com.ever365.rest.AuthenticationUtil"%>
<%@page import="java.util.Date"%>
<%@ page import="com.ever365.utils.StringUtils" %>

<%! public static final String STATIC = "static";
%><%
    FinanceService finaService = ((FinanceService) ContextLoaderListener
            .getCurrentWebApplicationContext().getBean("fin.service"));
    if (request.getParameter("id")==null) {
        response.getWriter().println("Not Found");
        return;
    }
    Map news = finaService.getContent("finamarkets", request.getParameter("id"));

    if (news==null) {
        response.getWriter().println("Not Found");
    }

    Map<String, Object> map = finaService.filterCollection("finamarkets", null, 0, 100);
    List<Map> marketLists = (List)map.get("list");
%>


<!doctype html>
<html>
<head>

<meta charset="utf-8">
<title></title>
<!--css -->
<link type="text/css" rel="stylesheet" href="css/base.css"><!--通用CSS样式 -->
<link type="text/css" rel="stylesheet" href="css/xinwen_info.css">

    <script type="text/javascript" src="js/jquery-1.7.2.min.js"></script>
    <script>

        $(function() {
            $(".info_text").html($(".info_text").html().replace(/\n/g, "<br>"));

        });
    </script>
</head>

<body class="bg">
<!-- -->
<%@ include file="top.jsp" %>

<div class="top-lv03">
    <ul>
        <li><a href="index.jsp">首页</a></li>
        <li ><a href="elogin.jsp">融资入口</a></li>
        <li><a href="plogin.jsp">投资入口</a></li>
        <li ><a href="res.jsp">资金供应</a></li>
        <li class="current"><a href="markets.jsp" >金融超市</a></li>
        <li ><a href="/news-view.jsp?id=56556d060cf251a9d0946900">联系我们</a></li>
    </ul>
</div>
</div>

<div id="content">
    <!--内容开始 -->
    <div class="xinwen_info_001">
        <!--左边 -->
        <div class="l">
            <div class="title">
                <cite><%=news.get("name")%></cite>
            </div>
            <div class="content">
                <div class="info_text">
                    <%=news.get("desc")%>
                </div>
            </div>
        </div>
        <!--右边 -->
        <div class="r">
            <div class="title">
                <cite>更多产品</cite>
            </div>
            <div class="content">
                <ul>
                    <%
                        for(Map newsx : marketLists) {
                    %>
                    <li><b>•</b><a href="fm-view.jsp?id=<%=newsx.get("_id")%>"><%=newsx.get("name")%></a></li>
                    <%
                        }
                    %>
                </ul>
            </div>
        </div>


        <div class="yinying">
        </div>
    </div>

    <!--内容结束 -->
</div>


<!-- -->
    <%@ include file="foot.jsp" %>

    </body>
</html>
