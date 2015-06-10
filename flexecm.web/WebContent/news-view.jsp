<%@ page contentType="text/html;charset=UTF-8"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Map"%>
<%@page import="org.springframework.web.context.ContextLoaderListener"%>
<%@page import="com.ever365.fin.FinanceService"%>
<%@page import="com.ever365.rest.AuthenticationUtil"%>
<%@page import="java.util.Date"%>
<%@ page import="com.ever365.utils.StringUtils" %>

<%
    FinanceService finaService = ((FinanceService) ContextLoaderListener
            .getCurrentWebApplicationContext().getBean("fin.service"));
    if (request.getParameter("id")==null) {
        response.getWriter().println("Not Found");
        return;
    }
    Map news = finaService.getContent("news", request.getParameter("id"));

    if (news==null) {
        response.getWriter().println("Not Found");
    }

    Map<String, Object> map = finaService.filterCollection("news", null, 0, 10);
    List<Map> newslist = (List)map.get("list");

%>

<!doctype html>
<html>
<head>

<meta charset="utf-8">
<title></title>
<!--css -->
<link type="text/css" rel="stylesheet" href="css/base.css"><!--通用CSS样式 -->
<link type="text/css" rel="stylesheet" href="css/xinwen_info.css">

    <!--jquery -->
</head>

<body class="bg">
<!-- -->
<%@ include file="top.jsp" %>


<div id="content">
    <!--内容开始 -->
    <div class="xinwen_info_001">
        <!--左边 -->
        <div class="l">
            <div class="title">
                <cite><%=news.get("title")%></cite>
                <i>编辑：<%=news.get("editor")%>    来源：<%=news.get("from")%>    日期：<%=StringUtils.formateDate(new Date((Long)news.get("updated")))%></i>
            </div>
            <div class="content">
                <div class="info_text">
                    <%=news.get("html")%>
                </div>
            </div>
        </div>
        <!--右边 -->
        <div class="r">
            <div class="title">
                <cite>更多消息</cite>
            </div>
            <div class="content">
                <ul>
                    <%
                        for(Map newsx : newslist) {
                    %>
                    <li><b>•</b><a href="news-view.jsp?id=<%=newsx.get("_id")%>"><%=newsx.get("title")%></a></li>
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
