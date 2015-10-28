<%@page import="org.json.JSONObject" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>融信担保-用户首页</title>

    <SCRIPT type=text/javascript src="js/jquery-1.11.2.min.js"></SCRIPT>
    <script type="text/javascript" src="js/webcommon.js"></script>

    <script type="text/javascript" src="js/sitedata_bas.js"></script>

    <!--css -->
    <link type="text/css" rel="stylesheet" href="css/base.css">
    <!--通用CSS样式 -->
    <link type="text/css" rel="stylesheet" href="css/admin.css">

    <script type="text/javascript" src="datepicker/jquery.datetimepicker.js"></script>
    <link type="text/css" rel="stylesheet" href="datepicker/jquery.datetimepicker.css">

    <link type="text/css" rel="stylesheet" href="css/zhanghuguanli.css">
    <link type="text/css" rel="stylesheet" href="css/zhanghuguanli_jksq.css">
    <style>

        .zhanghuguanli_jksq_001 .r .lv02 ul li {
            width: 100px;
        }

        .zhanghuguanli_jksq_001 .r .lv02.csteps ul li {
            width: 170px;
        }

        .zhanghuguanli_jksq_001 .r .lv02 ul li.sele {
        }

        .zhanghuguanli_jksq_001 .r .lv02 ul li span {
            width: auto;
        }

        .active-btn {
            cursor: pointer;
        }

        a {
            cursor: pointer;
        }

        table {
            width: 100%;
        }

        li {
            cursor: pointer;
        }

        .zhanghuguanli_jksq_001 .r .lv03 table tr td {
            height: auto;
        }

        .zhanghuguanli_jksq_001 .r .lv03 table tr td a {
            float: none;
            display: inline-block;
        }

        .readonly {
            border: none;
        }

        .template, .hidden {
            display: none;
        }


        #form-content td {
            padding:10px;
        }
        #form-content td:FIRST-CHILD {
            width: 220px;
            text-align: right;
        }

        #form-content td:LAST-CHILD {
            width: 600px;
        }

        .menu_list {
            display: none;
        }
    </style>
</head>
<body class="bg">
<%@ include file="top.jsp" %>
<%
    FinanceService finService = ((FinanceService) ContextLoaderListener
            .getCurrentWebApplicationContext().getBean("fin.service"));
    Map<String, Object> currentUser = finService.getCurrentUser();

    if (currentUser.get("cu") == null) {
        response.sendRedirect("login.jsp");
    }
%>

<script type="text/javascript">
    var uinfo =<%=new JSONObject(currentUser)%>;
</script>

<script type="text/javascript" src="js/home.js"></script>

<div id="content">
    <!--内容开始 -->
    <div class="zhanghuguanli_001 zhanghuguanli_jksq_001">
        <div class="l">
            <div class="icon">
                <img src="img/zhanghuguanli_001.png">
                <span><a href="javascript:void(0)"><%=currentUser.get("rname") %>
                </a>您好!</span>
            </div>
            <div class="menu_list person company">
                <ul>
                    <li class="current" onclick="dashboard();">
                        账户总览
                    </li>
                    <% if ("商业承兑汇票融资".equals(currentUser.get("vip-type"))) { %>
                        <li id="fstartDraft" onclick="startDraft();">
                            商业承兑汇票 &gt;
                        </li>
                    <%} else { %>
                        <li id="fstartLoan" onclick="startLoanRequest();">
                            借款申请&gt;
                        </li>
                    <% }%>

                    <li onclick="loanProgress();">
                        借款进度&gt;
                    </li>
                    <li>
                        <a href="#">评估报告&gt;</a>
                    </li>
                    <li>
                        <a href="#">信用评级&gt;</a>
                    </li>
                    <li onclick="config();">
                        信息设置&gt;
                    </li>

                </ul>
            </div>
            <div class="menu_list creditmgr">
                <ul>
                    <li onclick="pushedLoans();">
                        推送的项目&gt;
                    </li>
                    <li onclick="intendedLoans();">
                        跟进的项目&gt;
                    </li>

                </ul>
            </div>
        </div>

        <div class="r dashboard">

            <%if (!Boolean.TRUE.equals(currentUser.get("ecfm"))) { %>
            <div class="warn">
                <p>您未验证邮箱，请输入8位验证码: <input id="ecfm"> <a class="btn" onclick="checkEmail();">验证</a>

                <p>

                <p>如果您的邮箱未收到验证码，请<a onclick="resendEmail();">点击重新发送</a>

                <p>
            </div>
            <%} %>
            <div class="r_block_1">
                <table>
                    <tr>
                        <td style="width:25%">
                            <div class="gd">
                                <cite>借款总金额</cite>
                                <span>￥0<b>元</b></span>
                            </div>
                        </td>
                        <td style="width:25%">
                            <div class="gd">
                                <cite>成功借款金额</cite>
                                <span>￥0<b>元</b></span>
                            </div>
                        </td>
                        <td style="width:25%">
                            <div class="gd">
                                <cite>成功借款笔数</cite>
                                <span>0<b>次</b></span>
                            </div>
                        </td>
                        <td style="width:25%">
                            <div class="gd" style="border-right:none">
                                <cite>借款通过率</cite>
                                <span>100<b>%</b></span>
                            </div>
                        </td>
                    </tr>
                </table>
            </div>

            <div class="r_block_2">
                <span>年度借款总结：</span>
            </div>

            <div class="r_block_3">
                借款分析
            </div>
        </div>

        <div class="r form personEdit hidden" id="person-edit">

        </div>

        <div class="r form hidden" id="ccontent">

        </div>

        <div class="r loanRequest hidden">
            <div class="lv01">
                <a href="javascript:continueLoanEdit();" class="sele-bg">继续填写</a>
                <a href="#">新填一个</a>
            </div>

            <div class="psteps lv02 steps">
                <ul>
                    <li class="sele">
                        <span class="first ureq-1">基本信息</span><!--sele即为选中的样式 -->
                    </li>
                    <li>
                        <span class="ureq-2">贷款需求</span>
                    </li>
                    <li>
                        <span class="ureq-3">资产情况</span>
                    </li>
                    <li>
                        <span class="ureq-4">借款历史</span>
                    </li>
                    <li>
                        <span class="ureq-5">其他金融资产</span>
                    </li>
                    <li style="display: none;">
                        <span class="ureq-6">配偶资产情况</span>
                    </li>
                    <li style="display: none;">
                        <span class="ureq-7">配偶借款历史</span>
                    </li>
                    <li style="width: 120px; display: none;">
                        <span class="ureq-8">配偶其他金融资产</span>
                    </li>
                    <li>
                        <span class="end ureq-9">家庭成员状况</span>
                    </li>
                </ul>
            </div>

            <div class="csteps lv02 steps">
                <ul>
                    <li class="sele">
                        <span class="first creq-1">基本资料</span><!--sele即为选中的样式 -->
                    </li>
                    <li>
                        <span class="creq-2">您的借款历史</span>
                    </li>
                    <li>
                        <span class="creq-3">借款担保情况</span>
                    </li>
                    <li>
                        <span class="creq-4">进一步完善资料</span>
                    </li>
                    <li>
                        <span class="creq-5">最后一些资料，加油</span>
                    </li>
                </ul>
            </div>

            <div class="lv03" id="form-content">
            </div>
        </div>

        <div class="yinying">
        </div>
    </div>


    <!--内容结束 -->
</div>


<%@ include file="foot.jsp" %>

</body>
</html>