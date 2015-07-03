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
<title>融信担保-密码找回</title>
<SCRIPT type=text/javascript src="js/jquery-1.11.2.min.js"></SCRIPT>
    <script type="text/javascript" src="js/webcommon.js"></script>

    <!--css -->
<link type="text/css" rel="stylesheet" href="css/base.css"><!--通用CSS样式 -->
<link type="text/css" rel="stylesheet" href="css/xinwen.css">

    <script type="application/javascript">
        function findPassword() {
            var check = formCheck(".send-code ");
            if (check.test(true)) {
                var request = check.getRequest();
                $.post("/service/fin/account/findback", request, function() {
                    alert("邮箱验证码已发出");
                    $("#rnd-code-img").attr("src", "/rndimg?" + new Date().getTime());
                }).fail(function(error) {
                    if (error.status==400) {
                        alert("用户名和电子邮件不匹配");
                    } else if (error.status==412) {
                        $(".rndcode").html("图片验证码错误");
                        $(".rndcode").css("color","red");
                    }
                });
            }
        }

        function changePwd() {
            var check = formCheck(".reset-content");
            if (check.test()) {
                var request = check.getRequest();
                $.post("/service/fin/account/resetpwd", request, function() {
                    alert("密码重设完成");
                    location.href = "/index.jsp";
                }).fail(function(error) {
                    if (error.status==400) {
                        alert("用户名和电子邮件不匹配");
                    } else if (error.status==412) {
                        $(".rndcode").html("图片验证码错误");
                    }
                });
            }
        }
    </script>
<!--jquery -->
</head>

<body class="bg">
<!-- -->
<%@ include file="top.jsp" %>

    <div id="content">
        <!--内容开始 -->
        <div class="reset-content step1 regform">
            <!-- -->
            <div class="title">
                <span>密码找回</span>
            </div>

            <div class="form-content">
                <div class="send-code">
                <!-- -->
                <table>
                    <!--循环行 -->
                    <tr>
                        <td style="text-align:right">
                            <b>*</b><span>用户名</span>
                        </td>
                        <td class="info" style="padding-left:20px;">
                            <input id="loginid" type="text" value="" data-checked="notnull"  data-inval="用户名不能为空">
                        </td>
                    </tr>
                    <!--错误提示信息 -->
                    <tr>
                        <td>
                        </td>
                        <td class="info" style="padding-left:20px;">
                            <em class="loginid"></em>
                        </td>
                    </tr>

                    <!--循环行 -->
                    <tr>
                        <td style="text-align:right">
                            <b>*</b><span>电子邮箱</span>
                        </td>
                        <td style="padding-left:20px;">
                            <input id="email" type="text" data-checked="email" data-inval="请输入有效的电子邮件地址">
                        </td>
                    </tr>
                    <!--错误提示信息 -->
                    <tr>
                        <td>
                        </td>
                        <td style="padding-left:20px;">
                            <em class="email"></em>
                        </td>
                    </tr>

                    <!--循环行 -->
                    <tr>
                        <td style="text-align:right">
                            <b>*</b><span>图片验证码</span>
                        </td>
                        <td style="padding-left:20px;">
                            <input type="text" id="rndcode" data-checked="notnull" data-inval="验证不能为空">
                            <img id="rnd-code-img" src="/rndimg"> <a onclick='$("#rnd-code-img").attr("src", "/rndimg?" + new Date().getTime());'>刷新验证码</a>
                        </td>
                    </tr>

                    <!--错误提示信息 -->
                    <tr>
                        <td>
                        </td>
                        <td style="padding-left:20px;">
                            <em class="rndcode"></em>
                        </td>
                    </tr>
            </table>
            </div>
                <table>
                    <!--循环行 -->
                    <tr>
                        <td style="text-align:right">
                            <b>*</b><span>邮箱验证码</span>
                        </td>
                        <td style="padding-left:20px;">
                            <input id="emailcode" type="text"> <a href="javascript:findPassword();">点击获取邮箱验证码</a>
                        </td>
                    </tr>
                    <!--错误提示信息 -->
                    <tr>
                        <td>
                        </td>
                        <td style="padding-left:20px;">
                            <em class="emailcode"></em>
                        </td>
                    </tr>

                    <!--循环行 -->
                    <tr>
                        <td style="text-align:right">
                            <b>*</b><span>新密码</span>
                        </td>
                        <td style="padding-left:20px;">
                            <input id="pwd" type="password" value="" data-checked="length-6" data-inval="密码要大于6位">
                        </td>
                    </tr>
                    <!--错误提示信息 -->
                    <tr>
                        <td>
                        </td>
                        <td class="info" style="padding-left:20px;">
                            <em class="pwd"></em>
                        </td>
                    </tr>
                    <tr>
                        <td>
                        </td>
                        <td style="padding-left:20px;">
                            <input class="active-btn" onclick="changePwd();" type="button" value="提交">
                        </td>
                    </tr>
                </table>
            </div>
                <!-- -->
            </div>

    <div class="yinying">
    </div>
<!--内容结束 -->
</div>
<!-- -->
    <%@ include file="foot.jsp" %>

    </body>
</html>
