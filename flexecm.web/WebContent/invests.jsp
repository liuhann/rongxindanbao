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
<title>融信担保-理财产品</title>
<!--css -->
<link type="text/css" rel="stylesheet" href="css/base.css"><!--通用CSS样式 -->
<link type="text/css" rel="stylesheet" href="css/xinwen.css">
<!--jquery -->
</head>

<body class="bg">
<!-- -->
<%@ include file="top.jsp" %>

    <div id="content">
<!--内容开始 -->
    <div class="xinwen_001">
        <cite>理财产品</cite>
        <%
            FinanceService finService = ((FinanceService) ContextLoaderListener
                    .getCurrentWebApplicationContext().getBean("fin.service"));
            Map<String, Object> map = finService.filterCollection("investments", null, 0, 100);
            List<Map> investmentList = (List)map.get("list");
        %>

        <ul class="xinwen_list" style="height: 500px;">
            <%
                for(Map inv : investmentList) {
            %>
                <li>
                    <div class="l">
                        <span>
                        <b>·</b>[<%=inv.get("platform").toString()%>]<%=inv.get("name").toString() %>-年化<b><%=inv.get("profit").toString()%>%</b>
                        </span>
                    </div>

                    <div class="r">
                        <span><%=finService.INVESTMENT_TYPES.get(inv.get("type").toString())%></span>
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
