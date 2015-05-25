<%@page import="java.util.List"%>
<%@page import="java.util.Map"%>
<%@page import="org.springframework.web.context.ContextLoaderListener"%>
<%@page import="com.ever365.fin.FinanceService"%>
<%@page import="com.ever365.rest.AuthenticationUtil"%>
<%@page import="java.util.Date"%>
<%@ page contentType="text/html;charset=UTF-8"%>
<%
FinanceService finService = ((FinanceService) ContextLoaderListener
		.getCurrentWebApplicationContext().getBean("fin.service"));

Map<String, Object> pageInfo = finService.getIndexPageInfo();
%>
<!doctype html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge" />

<title>欢迎来到融信担保</title>

<link type="text/css" rel="stylesheet" href="css/base.css"><!--通用CSS样式 -->
<link type="text/css" rel="stylesheet" href="css/index_top.css">

<style type="text/css">

.hidden {
	display: none;
}
.clearfix:after {
	visibility: hidden;
	display: block;
	font-size: 0;
	content: " ";
	clear: both;
	height: 0;
}
.clearfix { display: inline-table; }
/* Hides from IE-mac \*/
* html .clearfix { height: 1%; }
.clearfix { display: block; }
/* End hide from IE-mac */


</style>
<%
AuthenticationUtil.setCurrentUser(null);
Object user = request.getSession().getAttribute(AuthenticationUtil.SESSION_CURRENT_USER);
if (user != null) {
	AuthenticationUtil.setCurrentUser((String) user);
} 
Date date = new Date();
%>

<script type="text/javascript" src="js/jquery-1.7.2.min.js"></script>
<script type="text/javascript">
function login() {
	var loginid = $("#loginid").val();
	var pwd = $("#pwd").val();
	if (loginid=="" || pwd=="" || pwd.length<6) {
		$("#login-error").show();
		$("#login-error").html("输入的用户名或密码格式错误");
		return;
	}
	
	var rnd = null;
	if ($("p.rndimg:visible").length==1) {
		rnd = $("#rndcode").val();
	}
	
	$.post("/service/fin/login", {
		'loginid': loginid,
		'pwd': pwd,
		'rndcode': rnd
	}, function(data) {
		location.href = location.href;
	}).fail(function(e) {
		if (e.status==412) {
			$("#login-error").show();
			$("#login-error").html("验证码错误");
			refreshImg();
		} else {
			$("#login-error").show();
			$("#login-error").html("用户名或密码错误");
			refreshImg();
		}
	});
	
}


function refreshImg() {
	$("p.rndimg").show();
	$("#rnd-img").attr("src", "/rndimg?" + new Date().getTime());
}
</script>

</head>
<body class="bg">
	<div id="index_top">
    	<div class="index_top-lv01">
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
	                    <span class="nohot"><a style="color:#666;" href="login.jsp">快速登录</a></span>
                    <%} else { %>            
	                    <span class="cls-mr"><a class="u-line" href="home.jsp"><%=AuthenticationUtil.getCurrentUser() %></a></span>
                    	<span>欢迎您来融信融资网</span>
                    	<span><a href="/service/fin/logout">【安全退出】</a></span>
                    <%} %>
                </div>
            </div>
        </div>
    	<!-- -->
    	<div class="index_top-lv02">
		<!-- -->
        	<div class="l">
            	<div class="l_01">
       				<img src="img/index_009.png">         	
                </div>	
                
                <div class="l_02">
                	<ul>
                    	<!-- -->
                    	<a href="#"><li>
                        	<div class="icon">
                            	<img src="img/index_010.png">
                            </div>
                            <div class="text">
                            	<span>超过万家金融机构</span>
                            </div>
                        </li></a>
                        <!-- -->
                    	<a href="#"><li>
                        	<div class="icon">
                            	<img src="img/index_011.png">
                            </div>
                            <div class="text">
                            	<span>免费申请</span>
                            </div>
                        </li></a>
                        <!-- -->  
                    	<a href="#"><li>
                        	<div class="icon">
                            	<img src="img/index_012.png">
                            </div>
                            <div class="text">
                            	<span>最快一天放款</span>
                            </div>
                        </li></a>
                        <!-- -->
                    	<a href="#"><li>
                        	<div class="icon">
                            	<img src="img/index_013.png">
                            </div>
                            <div class="text">
                            	<span>政府背书</span>
                            </div>
                        </li></a>
                        <!-- -->
                    	<a href="#"><li>
                        	<div class="icon">
                            	<img src="img/index_014.png">
                            </div>
                            <div class="text">
                            	<span>国有担保</span>
                            </div>
                        </li></a>
                        <!-- -->                        
                                                                      
                    </ul>
                	
                </div>
			</div>
            
            <div class="r">
            	<div class="r_01">
                	<img src="img/index_015.png">
                    <span>服务电话：4008-8888-8888</span>
                </div>
                <div class="r_02">
                	<img src="img/index_016.png">
                </div>
                <div class="r_03">
                	<input type="button" value="融资指南">
                	<input type="button" value="申请借款">
                	<input type="button" value="关于我们">                
                </div>
            </div>
		<!-- -->
        </div>
        <!-- -->
        <div class="index_top-lv03">
        	<ul>
            	<li><a href="#">首页</a></li>
            	<li><a href="#">融资入口</a></li>
            	<li><a href="#">账号管理</a></li>
            	<li><a href="#">投资资源</a></li>
            	<li><a href="#">理财产品</a></li>
            	<li><a href="#">金融超市</a></li>                                                                                
            </ul>
        </div>
        


   		<div class="index_top-lv04">
        	<!-- -->
            <div class="l">
            	<div class="box clearfix">
                    <div class="l_01">
                        <div class="title_l">
                            <span>最新受理项目</span>
                        </div>
                        <div class="title_r">
                            <span><a href="#">更多&gt;</a></span>
                        </div>
                    </div>
                    <div class="l_02">
                        <div class="list_l clearfix"><!--左 -->
                            <ul>
                                <li>
                                    <img src="img/index_024.png">
                                    <span><a href="#">淄博市融资金融有限公司企业经营贷款项目</a></span>
                                </li>
                                <li>
                                    <img src="img/index_024.png">
                                    <span><a href="#">淄博市融资金融有限公司企业经营贷款项目</a></span>
                                </li>
                                <li>
                                    <img src="img/index_024.png">
                                    <span><a href="#">淄博市融资金融有限公司企业经营贷款项目</a></span>
                                </li>
                                <li>
                                    <img src="img/index_024.png">
                                    <span><a href="#">淄博市融资金融有限公司企业经营贷款项目</a></span>
                                </li>
                                <li>
                                    <img src="img/index_024.png">
                                    <span><a href="#">淄博市融资金融有限公司企业经营贷款项目</a></span>
                                </li>
                                <li>
                                    <img src="img/index_024.png">
                                    <span><a href="#">淄博市融资金融有限公司企业经营贷款项目</a></span>
                                </li>                                                                                                                                            
                            </ul>
                        </div>
                        <!-- -->
                        <div class="list_r"><!--右 -->
                            <ul>
                                <li>
                                    <img src="img/index_024.png">
                                    <span><a href="#">淄博市融资金融有限公司企业经营贷款项目</a></span>
                                </li>
                                <li>
                                    <img src="img/index_024.png">
                                    <span><a href="#">淄博市融资金融有限公司企业经营贷款项目</a></span>
                                </li>
                                <li>
                                    <img src="img/index_024.png">
                                    <span><a href="#">淄博市融资金融有限公司企业经营贷款项目</a></span>
                                </li>
                                <li>
                                    <img src="img/index_024.png">
                                    <span><a href="#">淄博市融资金融有限公司企业经营贷款项目</a></span>
                                </li>
                                <li>
                                    <img src="img/index_024.png">
                                    <span><a href="#">淄博市融资金融有限公司企业经营贷款项目</a></span>
                                </li>
                                <li>
                                    <img src="img/index_024.png">
                                    <span><a href="#">淄博市融资金融有限公司企业经营贷款项目</a></span>
                                </li>                                                                                                                                            
                            </ul>                    
                        </div>
                        <!-- -->
                        <div class="list_c"><!--中 -->                    
                        </div>
                        
                    </div>
                </div>
                <!-- -->
                <div class="yinying">
                </div>                
            </div>

            
            <div class="r">
            	<div class="box"> 
           			<div class="r_01">
                    	<span>享受更多便利？欢迎成为我们的绿色通道企业</span>
                    </div>
                    <div class="r_02">
                    	<span class="tab_l current">请登录</span> <!--current为选中的样式 必须要和签满的tab_X样式并存 -->
                        <span class="tab_r ">后台登录</span>
                         
                    </div>
                    
                    <div class="r_03"><!--这里作为标签切换的页面 我估计可以复制两套，不同的post指向 -->
                    	<input class="form-text" value="" id="loginid">
                        <span class="usr">用户名</span>
                    	<input class="form-text"  type="password" id="pwd">
                        <span class="pwd">密&nbsp;&nbsp; 码</span>
                        <p id="login-error" class="error hidden"></p>
                        <p class="rndimg hidden"><input id="rndcode"> <img id="rnd-img"> <a href="javascript:refreshImg();">刷新</a></p>                        
                        <input type="button" class="form-button" value="登录" onclick="login();">
                    </div>
                    
                    <div class="r_04">
                    	<span class="r_04_l">
                        	<a href="#">个人注册</a>
                        </span>
                        <span class="r_04_l">
                        	<a href="#">企业注册</a>
                        </span>
                        <span class="r_04_r">
                        	<a href="#">找回密码？</a>
                        </span>
                    </div>
           		</div>
                <!-- -->
                <div class="yinying">
                </div>                     
            </div>
            <!-- -->
        </div>
        <!-- -->
        <div class="index_top-lv05">

              <div class="l">
                  <div class="box">
                    <div class="l_top">
                        <div class="l_top_l">
                            <span>
                            投资资源
                            </span>
                        </div>    
                        <div class="l_top_r">
                            <a href="#">更多&gt;</a>
                        </div>
                    </div>
                    
                    <div class="content">
                       <ul>
                       	<li><a href="#"><img src="img/index_020.png" alt=""></a></li>
                       	<li><a href="#"><img src="img/index_021.png" alt=""></a></li>
                       	<li><a href="#"><img src="img/index_022.png" alt=""></a></li>
                       	<li><a href="#"><img src="img/index_023.png" alt=""></a></li>
                       	<li><a href="#"><img src="img/index_020.png" alt=""></a></li>
                       	<li><a href="#"><img src="img/index_021.png" alt=""></a></li>
                       	<li><a href="#"><img src="img/index_022.png" alt=""></a></li>
                       	<li><a href="#"><img src="img/index_023.png" alt=""></a></li>
                       	<li><a href="#"><img src="img/index_020.png" alt=""></a></li>
                       	<li><a href="#"><img src="img/index_021.png" alt=""></a></li>
                       	<li><a href="#"><img src="img/index_022.png" alt=""></a></li>
                       	<li><a href="#"><img src="img/index_023.png" alt=""></a></li>                        
                       </ul>
                    </div>
                  </div>
                  <div class="yinying">
                  </div>
              </div>
          
          
              <div class="r">
                  <div class="box">
                    <div class="title">
                        <div class="title_l">新闻公告</div>
                        <div class="title_r"><a href="#">更多&gt;</a></div>
                    
                    </div>
                    
                    <div class="content">
                        <ul>
                        	<li><img src="img/index_024.png" alt=""><a href="">机构称央行今年信贷额度贷额度已部已...</a></img></li>
                        	<li><img src="img/index_024.png" alt=""><a href="">机构称央行今年信贷额度贷额度已部已...</a></img></li>
                        	<li><img src="img/index_024.png" alt=""><a href="">机构称央行今年信贷额度贷额度已部已...</a></img></li>
                        	<li><img src="img/index_024.png" alt=""><a href="">机构称央行今年信贷额度贷额度已部已...</a></img></li>
                        	<li><img src="img/index_024.png" alt=""><a href="">机构称央行今年信贷额度贷额度已部已...</a></img></li>
                        	<li><img src="img/index_024.png" alt=""><a href="">机构称央行今年信贷额度贷额度已部已...</a></img></li>
                        </ul>
                    </div>
                  

                  </div>
                    <div class="yinying">
                    </div>                  
              </div>
            
        </div>
        <!-- -->
        <div class="index_top-lv06">
            <div class="box">
                <div class="title">
                    <div class="l">
                        理财产品
                    </div>
                    <div class="r">
                        <a href="#">更多&gt;</a>
                    </div>
                </div>
                <div class="content">
                    <div class="box_1">
                    	<div class="box_1_top">
                        	<div class="l">
                            	网贷类投资
                            </div>
                            <div class="r">
                            	<a href="#"><%=((Map)pageInfo.get("wd")).get("size").toString() %>款&gt;</a>
                            </div>
                        </div>
                        <div class="box_1_foot">
                        	<%
                        		
                        		Object list = ((Map)pageInfo.get("wd")).get("list");
                        		System.out.println(list);
                        		List<Map> wdlist =  (List) list;
                        		for(Map wd :wdlist) {
                        	%>
	                        	<div class="item">
	                            	<div class="l">
	                                	<img src="<%=wd.get("logo").toString()%>">
	                                    <span><%=wd.get("platform").toString()%></span>
	                                </div>
	                                <div class="r">
	                                	<cite><%=wd.get("name").toString() %></cite>
	                                    <span>年化<b><%=wd.get("profit").toString()%>%</b></span>
	                                </div>
	                            </div>
                        	
                        	<%
                        		}
                        	%>
                        </div>
                    </div>
                    <!-- -->
                    <div class="box_1">
                    	<div class="box_1_top">
                        	<div class="l">
                            	银行理财
                            </div>
                            <div class="r">
                            	<a href="#"><%=((Map)pageInfo.get("yh")).get("size").toString() %>款&gt;</a>
                            </div>
                        </div>
                        <div class="box_1_foot">
                        
                        <%
                        		
                        	List<Map> yhlist = (List)  (((Map)pageInfo.get("yh")).get("list"));
                        		for(Map wd :yhlist) {
                        	%>
	                        	<div class="item">
	                            	<div class="l">
	                                	<img src="<%=wd.get("logo").toString()%>">
	                                    <span><%=wd.get("platform").toString()%></span>
	                                </div>
	                                <div class="r">
	                                	<cite><%=wd.get("name").toString() %></cite>
	                                    <span>年化<b><%=wd.get("profit").toString()%>%</b></span>
	                                </div>
	                            </div>
                        	<%
                        		}
                        	%>
                        	<div class="item cls_b">
                            	<div class="l">
                                	<img src="img/index_028.png">
                                    <span>齐商银行</span>
                                </div>
                                <div class="r">
                                	<cite>银龙理财</cite>
                                    <span>年化<b>10%</b></span>
                                </div>
                            </div>                            
                        </div>
                    </div>
                    <!-- -->
                    <div class="box_1 cl-mr">
                    	<div class="box_1_top">
                        	<div class="l">
                            	定向类理财
                            </div>
                            <div class="r">
                            <a href="#"><%=((Map)pageInfo.get("dx")).get("size").toString() %>款&gt;</a>
                            </div>
                        </div>
                        <div class="box_1_foot">
                        	<div class="item cls_b fix">
                            	<div class="l" style="height:25px; line-height:25px;">
                                    <span style="height:25px; line-height:25px;">余额宝</span>
                                </div>
                                <div class="r" style="height:25px; line-height:25px;">
                                    <span>年化<b>10%</b></span>
                                </div>
                            </div>
                        	<div class="item cls_b fix">
                            	<div class="l" style="height:25px; line-height:25px;">
                                    <span style="height:25px; line-height:25px;">网易理财</span>
                                </div>
                                <div class="r" style="height:25px; line-height:25px;">
                                    <span>年化<b>10%</b></span>
                                </div>
                            </div>        

                        	<div class="item cls_b fix">
                            	<div class="l" style="height:25px; line-height:25px;">
                                    <span style="height:25px; line-height:25px;">网易理财</span>
                                </div>
                                <div class="r" style="height:25px; line-height:25px;">
                                    <span>年化<b>10%</b></span>
                                </div>
                            </div>                            
                                                
                        </div>
                    </div>
                </div>
            </div>
            <div class="yinying">
            </div>
        </div>
        <!-- -->
        <div class="index_top-lv07">
        	<div class="box">
            	<div class="title">
                	<div class="l">
                    	金融超市
                    </div>
                    <div class="r">
                    	<a href="#">更多&gt;</a>
                    </div>
                </div>
                <div class="content">
                	<div class="l">
                    	<ul>
                        	<li class="current">  <!--current为选中的样式 -->
                            	<span>企业借款类</span>
                            </li>
                        	<li>
                            	<span>企业借款类</span>
                            </li>                            
                        </ul>
                    </div>
                    
                    <div class="r">
                    	<table>
                        	<tr style="background-color:#d8d8d8;">
                            	<td style="width:20%;">名称</td>
                                <td style="width:20%;">金额</td>
                                <td style="width:20%;">期限</td>
                                <td style="width:20%;">利率</td>
                                <td style="width:20%;">合作机构</td>
                            </tr>
							<!-- -->                            
                        	<tr>
                            	<td class="fix1">流动资金贷款</td>
                                <td class="fix2">￥100,000,000.00</td>
                                <td class="fix1">1-3个月</td>
                                <td class="fix1">12%</td>
                                <td class="fix1">e利友</td>
                            </tr>                            
                            <!-- -->
                        	<tr>
                            	<td class="fix1">流动资金贷款</td>
                                <td class="fix2">￥100,000,000.00</td>
                                <td class="fix1">1-3个月</td>
                                <td class="fix1">12%</td>
                                <td class="fix1">e利友</td>
                            </tr>                            
                            <!-- -->
                        	<tr>
                            	<td class="fix1">流动资金贷款</td>
                                <td class="fix2">￥100,000,000.00</td>
                                <td class="fix1">1-3个月</td>
                                <td class="fix1">12%</td>
                                <td class="fix1">e利友</td>
                            </tr>                            
                            <!-- -->
                        	<tr>
                            	<td class="fix1">流动资金贷款</td>
                                <td class="fix2">￥100,000,000.00</td>
                                <td class="fix1">1-3个月</td>
                                <td class="fix1">12%</td>
                                <td class="fix1">e利友</td>
                            </tr>                            
                            <!-- -->
                        	<tr>
                            	<td class="fix1">流动资金贷款</td>
                                <td class="fix2">￥100,000,000.00</td>
                                <td class="fix1">1-3个月</td>
                                <td class="fix1">12%</td>
                                <td class="fix1">e利友</td>
                            </tr>                            
                            <!-- -->                                                                                                                
                            
                        </table>
                    </div>
                </div>
            </div>
            
            <div class="yinying">
            </div>
        </div>
        <!-- -->
        <div class="index_top-lv08">
        	<div class="box">
            	<div class="title">
                	<div class="l">
                    	合作机构
                    </div>
                    <div class="r">
                    	<a href="#">更多&gt;</a>
                    </div>
                </div>
                <div class="content">
                	<a href="#"><img src="img/index_029.png"></a>
                	<a href="#" style="text-align:center;"><img src="img/index_030.png"></a>
                	<a href="#" style="text-align:center;"><img src="img/index_031.png"></a>
                	<a href="#" style="text-align:right;"><img src="img/index_034.png"></a>                                                            
                </div>            	
            </div>
            
            <div class="yinying">
            </div>
        </div>
        <!-- -->
        <!-- -->
        <!-- -->
   </div> 
    <%@ include file="foot.jsp"%> 
</body>
</html>