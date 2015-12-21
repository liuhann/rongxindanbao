<%@page import="java.util.HashMap"%>
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

AuthenticationUtil.setCurrentUser(null);
Object user = request.getSession().getAttribute(AuthenticationUtil.SESSION_CURRENT_USER);
if (user != null) {
	AuthenticationUtil.setCurrentUser((String) user);
}
Map<String, Object> currentUser = finService.getCurrentUser();

Map<String, Object> pageInfo = finService.getIndexPageInfo();

Date date = new Date();

%>
<!doctype html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge" />

<title>欢迎来到融信融资</title>

<link type="text/css" rel="stylesheet" href="css/base.css"><!--通用CSS样式 -->
<link type="text/css" rel="stylesheet" href="css/index_top.css">

<style type="text/css">

.uinfo i { 
  color: #e03121;
  font-style: normal;
  font-size: 20px; margin: 0 5px;
}

.uinfo p {
  line-height: 40px;
  color: #999;
  font-size: 14px;
 }
 
.udesc {
	width: 100%;text-align: center; border-top: 1px solid #ccc; border-bottom: 1px solid #ccc;
}
.udesc td {
	padding: 10px 0px;
}
 
.uinfo .button {
  display: block;
  text-align: center;
  border: 1px solid #20A2DF;
  margin-top: 25px;
  color: #20A2DF;
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
}

.gview .gitem {
	text-align: center;
	float: left;
	width: 140px;
	margin: 55px;
	border: 1px solid #ccc;
	border-radius: 10px;
}

.gview .gitem .title {
	font-size: 20px;
	padding: 10px;
	color: #53B3FD;
}
.gview .gitem .sub_title {
	font-size: 12px;
	color: #999;
}

.gview .gitem .num {
	font-size: 24px;
	color: #e58922;
	padding-top: 15px;
}

.gview .gitem .em {
	padding-bottom: 10px;
}


.slide-wrapper {
	width: 2340px;
	height: 250px;
	-webkit-transition: margin-left 300ms ease-in;
	-moz-transition: margin-left 300ms ease-in;
	-ms-transition: margin-left 300ms ease-in;
	-o-transition: margin-left 300ms ease-in;
	transition: margin-left 300ms ease-in;
}

.title_r ul li {
	float: left;
	padding: 20px;
	padding-bottom: 10px;
	cursor: pointer;
	font-size: 14px;
}

.title_r ul li.current {
	border-bottom: 2px solid #673231;
}

.ui-tab-content {
	width: 780px;
	height: 250px;
	float: left;
	background-repeat: no-repeat;
	overflow: hidden;
}

.ui-tab-content .slides {
	height: 250px;
	float: left;
	width: 780px;
}

.recent-projs ul {
	margin-left: 20px;
}

.recent-projs ul li {
	padding: 10px;
}
.recent-projs ul li a {

	color: #21A3E0;
}

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
		if ($("#type").find("option:selected").attr("dest")!=null) {
			location.href = "home.jsp#" + $("#type").find("option:selected").attr("dest");
		} else {
			location.href = location.href;
		}
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


	$(function() {
		$("#snap-title ul li").click(function() {
			$("#snap-title ul li.current").removeClass("current");
			$(this).addClass("current");
			$(".slide-wrapper").css("margin-left", "-" + $("#snap-title ul li").index(this)*780 + "px");
		})
	});
</script>

</head>
<body class="bg">
	<div id="index_top">
    	<div class="index_top-lv01">
        	<div class="w1150">
                <div class="l">
					<span style="color: #777;">共创共享共成长，让我们一起实现梦想</span>
					<!--
                   <span>今天是</span>
                   <span><%=1900+date.getYear() %>年<%=date.getMonth()+1 %>月<%=date.getDate() %>日</span>
                   -->
                </div>
                
                 <div class="r">
                	<%if(AuthenticationUtil.getCurrentUser()==null) { %>
                		<span><a style="color:#666;" href="preg.jsp">个人注册</a></span>
	                	<img src="img/index_004.png" class="sep">
	                	<span><a style="color:#666;" href="orgreg.jsp">企业注册</a></span>
	                	<img src="img/index_004.png" class="sep">
	                    <span class="nohot"><a style="color:#666;" href="login.jsp">快速登录</a></span>
                    <%} else { %>            
	                    <span class="cls-mr"><a class="u-line" href="home.jsp"><%=currentUser.get("rname") %></a></span>
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
                            	<span>国家基金受托单位</span>
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
                            	<span>快速放款</span>
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

                </div>
                <div class="r_02">
                	<img src="res/rxQRCode.jpg" width="120px">
                </div>
                <div class="r_03">
                	<input type="button" value="融资指南" onclick="location.href='/news-view.jsp?id=565663a20cf251a9d0946904'">
                	<input type="button" value="关于我们" onclick="location.href='/news-view.jsp?id=56566dd20cf220da793cd0f9'">
                </div>
            </div>
		<!-- -->
        </div>
        <!-- -->
        <div class="index_top-lv03">
        	<ul>
            	<li><a href="index.jsp">首页</a></li>
            	<li><a href="home.jsp?request">融资入口</a></li>
            	<li><a href="home.jsp?profile">投资入口</a></li>
            	<li><a href="res.jsp">资金供应</a></li>
            	<li><a href="markets.jsp">金融超市</a></li>
            	<li><a href="/news-view.jsp?id=56556d060cf251a9d0946900">联系我们</a></li>
            </ul>
        </div>

   		<div class="index_top-lv04">
        	<!-- -->
            <div class="l">
            	<div class="clearfix" style="background-color: #fff;">
                    <div class="l_01">
                        <div class="title_l" style="padding: 20px;">
                            <span>融资信息</span>
                        </div>
                        <div class="title_r" id="snap-title">
							<ul >
								<li class="current">项目总览</li>
								<li>最新受理项目</li>
								<li>已成功项目</li>
								<li>更多</li>
							</ul>
                        </div>
                    </div>
                    <div class="l_02" style="padding-top: 0;">
						<div class="ui-tab-content">
							<div class="slide-wrapper">
								<div class="gview slides">
									<div class="gitem">
										<p class="title">融资需求</p>
										<div class="sub_title">数量</div>
										<div class="num">409</div>
										<div class="em">个</div>
									</div>
									<div class="gitem">
										<p class="title">融资需求</p>
										<div class="sub_title">完结</div>
										<div class="num">185</div>
										<div class="em">个</div>
									</div>
									<div class="gitem">
										<p class="title">成功对接</p>
										<div class="sub_title">总额</div>
										<div class="num">14457</div>
										<div class="em">万元</div>
									</div>
								</div>

								<div class="recent-projs slides">
									<%
										Map<String, Object> recentProjects = (Map)pageInfo.get("rc");
										List<Map> projLists = (List<Map>)recentProjects.get("list");
										int i = 0;
									%>
									<ul>
										<%
											while(i<projLists.size()) {
												Map proj = projLists.get(i);

												String title = "";
												if ((Integer)proj.get("type")==1) {
													title = proj.get("mpart") + "企业经营贷款项目" + proj.get("loan") + "万元";
												} else {
													title = "[个人]" + proj.get("rname")
															+ proj.get("purpose") + "申请,贷款额"+ proj.get("loan") + "万元";
												}
												i++;
										%>
										<li>
											<img src="img/index_024.png">
											<span><a href="#"><%=title %> </a></span>
										</li>
										<%
											}
										%>
									</ul>
								</div>
							</div>
						</div>
                    </div>
                </div>
                <!-- -->
                <div class="yinying">
                </div>                
            </div>

            
            <div class="r">
            
            	<%if (AuthenticationUtil.getCurrentUser()==null) { %>
            		<div class="box">
					<!--
           			<div class="r_01">
                    	<span>高新区企业、青年创业梦工场项目、新材料产业企业等用户登录入口</span>
                    </div>
                    -->
                    <div class="r_02" style="border-bottom: 2px solid #1F99D3;margin-top: 10px;">
                    	<span class="tab_l current" style="height:28px;">绿色通道</span> <!--current为选中的样式 必须要和签满的tab_X样式并存 -->
                        <!--<span> class="tab_r "><a href="login.jsp">后台登录</a></span> -->
                         
                    </div>

					<style>
						.login-panel {

						}

						.login-panel p {
							position: relative;
							margin-top: 10px;
						}

						.login-panel p span {
							position: absolute;
							font-size: 14px;
							color: #666;
							background-image: url('img/index_019.png');
							background-repeat: no-repeat;
							display: block;
							height: 32px;
							width: 86px;
							line-height: 32px;
							text-align: center;
						}

						.login-panel p select {
							border-left: none;
							padding: 5px;
							width: 190px;
							height: 32px;
							border: 1px solid #cccccc;
							border-radius: 3px;
							margin-left: 84px;
						}
						.login-panel p input {
							border-left: none;
							padding: 5px;
							width: 180px;
							height: 20px;
							border: 1px solid #cccccc;
							border-radius: 3px;
							margin-left: 84px;
						}

						.login-panel input[type='button'] {
							background-image: url('img/big-btn-bg.png');
							background-repeat: repeat-x;
							border-radius: 6px;
							height: 40px;
							font-size: 16px;
							color: #fff;
							margin-top: 10px;
							width: 270px;
							border: none;
							line-height: 30px;
						}

					</style>
					<div class="login-panel"><!--这里作为标签切换的页面 我估计可以复制两套，不同的post指向 -->
						<p>
							<span class="usr">用户类型</span>
							<select id="type">
								<option dest="fstartLoan">高新区企业</option>
								<option dest="fstartLoan">青年创业梦工场</option>
								<option dest="fstartLoan">新材料产业企业</option>
								<option dest="fstartDraft">商业承兑汇票融资</option>
							</select>
						</p>

						<p>
							<span class="usr">用户名</span>
							<input class="form-text" value="" id="loginid">
						</p>

						<p>
							<span class="pwd">密&nbsp;&nbsp; 码</span>
							<input class="form-text"  type="password" id="pwd">
						</p>

                        <p id="login-error" class="error hidden"></p>

						<p class="rndimg hidden"><input id="rndcode"> <img id="rnd-img"> <a href="javascript:refreshImg();">刷新</a></p>
                        <input type="button" class="form-button" value="登录" onclick="login();">
                    </div>
                    
                    <div class="r_04" style="margin-top: 8px;">
                    	<span class="r_04_l">
                        	<a href="preg.jsp">个人注册</a>
                        </span>
                        <span class="r_04_l" style="margin-left: 8px;">
                        	<a href="orgreg.jsp">企业注册</a>
                        </span>
                        <span class="r_04_r">
                        	<a href="forgot.jsp">找回密码？</a>
                        </span>
                    </div>
           		</div>
           		<% } else { %>
	           		<div class="box uinfo"  style="height: 250px;">
						Hi,<%=currentUser.get("rname") %>
						<%
						Map<String, Object> filters = new HashMap<String, Object>();
						filters.put("uid", AuthenticationUtil.getCurrentUser());
						Map<String, Object> myloans = finService.getLoanRequestList(filters, 0, 2000);
						List<Map> loanlist = (List<Map>)myloans.get("list");
						Integer audit1 = 0;
						Integer audit2 = 0;
						Integer audit3 = 0;
						for(Map loan: loanlist) {
							Integer audit = (Integer)loan.get("audit");
							if (audit==1) {
								audit1++;
							}
							if (audit==2) {
								audit2++;
							}
							if (audit==3) {
								audit3++;
							}
						}
						%>
						<p>您有<i style=""><%=loanlist.size() %></i>个项目</p>
						<table class="udesc" style="">
							<tr>
								<td ><p>初审中</p><i><%=audit1 %></i></td>
								<td ><p>复审中</p><i><%=audit2 %></i></td>
								<td><p>通过</p><i><%=audit3 %></i></td>
							</tr>
						</table>	           			
						
						<p><a class="button flax" href="home.jsp">进入我的账户</a></p>     			
	           		</div>
           		<% } %>
                <!-- -->
                <div class="yinying">
                </div>                     
            </div>
            <!-- -->
        </div>
        <!-- -->
        <div class="index_top-lv05">

              <div class="l">
                  <div class="box" style="height: 236px;">
                    <div class="l_top">
                        <div class="l_top_l">
                            <span>
                            资金供应
                            </span>
                        </div>    
                        <div class="l_top_r">
                            <a href="res.jsp">更多&gt;</a>
                        </div>
                    </div>
                    
                    <div class="content">
                       <ul>
                       	<%
							List<Map> finres = (List)((Map)pageInfo.get("finres")).get("list");
							for(Map res : finres) {
						%>
							<li><a target="_blank" href="<%=res.get("link")%>"><img src="<%=res.get("logo")%>" alt=""></a></li>

						<%
							}
						%>
                       </ul>
                    </div>
                  </div>
                  <div class="yinying">
                  </div>
              </div>

              <div class="r">
                  <div class="box" style="height: 236px;">
                    <div class="title">
                        <div class="title_l">新闻公告</div>
                        <div class="title_r"><a href="news.jsp">更多&gt;</a></div>
                    
                    </div>
                    
                    <div class="content">
                        <ul>
                        	<%
								List<Map> newslist = (List)((Map)pageInfo.get("news")).get("list");
								for(Map news : newslist) {
							%>
                        		<li><img src="img/index_024.png" alt=""><a href="news-view.jsp?id=<%=news.get("_id")%>"><%=news.get("title")%></a></img></li>
							<%
								}
							%>
                        </ul>
                    </div>
                  </div>
                    <div class="yinying">
                    </div>                  
              </div>
        </div>

        <div class="index_top-lv07">
        	<div class="box">
            	<div class="title">
                	<div class="l">
                    	金融超市
                    </div>
                    <div class="r">
                    	<%--<a href="markets.jsp">更多&gt;</a>--%>
                    </div>
                </div>

				<style>
					.content .l {
						display: block;
					}
					.index_top-lv07 .box .content .r {
						width: 1090px;
					}
					.index_top-lv07 .box .content .r table {
						width: 1090px;
					}
					.index_top-lv07 .box .content .l ul li {
						margin-bottom: 0;
						margin-top: 10px;
					}
					.index_top-lv07 .box .content .r table tr td {
						text-align: left;
						padding: 10px;
					}

					.fix1 a {
						background-color: #21A3E0;
						color: #fff;
						padding: 5px 10px;
					}
				</style>
                <div class="content">
                	<div class="l">
                    	<ul>
                        	<li class="current">  <!--current为选中的样式 -->
                            	<span style="color:white;">企业贷款类</span>
                            </li>
                        </ul>
                    </div>
                    
                    <div class="r">
                    	<table>
                        	<tr style="background-color:#d8d8d8;">
                            	<td  style="width: 30%">产品名称</td>
                                <td style="width: 10%">最高额度</td>
                                <td style="width: 10%">最长期限</td>
                                <td style="width: 20%">利率</td>
                                <td style="width: 20%">合作机构</td>
                                <td style="width: 10%">操作</td>
                            </tr>
                    		<%
                        	List<Map> fmlist = (List)  (((Map)pageInfo.get("fm")).get("list"));
                        		for(Map fm :fmlist) {
									if ("1".equals(fm.get("type"))) {
                        	%>
	                        	<!-- -->                            
                        		<tr>
                            	<td class="fix1"><%=fm.get("name").toString() %></td>
                                <td class="fix2"><%=fm.get("quota").toString() %></td>
                                <td class="fix1"><%=fm.get("cycle").toString() %></td>
                                <td class="fix1"><%=fm.get("rates").toString() %>%</td>
                                <td class="fix1"><%=fm.get("corporg").toString() %></td>
                                <td class="fix1"><a href="fm-view.jsp?id=<%=fm.get("_id")%>">查看</a></td>
                            </tr>
                        	<%
									}
                        		}
                        	%>
                        </table>
                    </div>
                </div>


				<div class="content">
					<div class="l">
						<ul>
							<li class="current">  <!--current为选中的样式 -->
								<span style="color:white;">个人贷款类</span>
							</li>
						</ul>
					</div>

					<div class="r">
						<table>
							<tr style="background-color:#d8d8d8;">
								<td  style="width: 30%">产品名称</td>
								<td style="width: 10%">最高额度</td>
								<td style="width: 10%">最长期限</td>
								<td style="width: 20%">利率</td>
								<td style="width: 20%">合作机构</td>
								<td style="width: 10%">操作</td>
							</tr>
							<%
								for(Map fm :fmlist) {
									if ("2".equals(fm.get("type"))) {
							%>
							<!-- -->
							<tr>
								<td class="fix1"><%=fm.get("name").toString() %></td>
								<td class="fix2"><%=fm.get("quota").toString() %></td>
								<td class="fix1"><%=fm.get("cycle").toString() %></td>
								<td class="fix1"><%=fm.get("rates").toString() %>%</td>
								<td class="fix1"><%=fm.get("corporg").toString() %></td>
								<td class="fix1"><a href="fm-view.jsp?id=<%=fm.get("_id")%>">查看</a></td>
							</tr>
							<%
									}
								}
							%>
						</table>
					</div>
				</div>

            </div>
            
            <div class="yinying">
            </div>
        </div>
        <!-- -->
        <%--<div class="index_top-lv08">--%>
        	<%--<div class="box">--%>
            	<%--<div class="title">--%>
                	<%--<div class="l">--%>
                    	<%--合作机构--%>
                    <%--</div>--%>
                    <%--<div class="r">--%>
                    	<%--<a href="#">更多&gt;</a>--%>
                    <%--</div>--%>
                <%--</div>--%>
                <%--<div class="content">--%>
                	<%--<a href="#"><img src="img/index_029.png"></a>--%>
                	<%--<a href="#" style="text-align:center;"><img src="img/index_030.png"></a>--%>
                	<%--<a href="#" style="text-align:center;"><img src="img/index_031.png"></a>--%>
                	<%--<a href="#" style="text-align:right;"><img src="img/index_034.png"></a>                                                            --%>
                <%--</div>            	--%>
            <%--</div>--%>
            <%----%>
            <%--<div class="yinying">--%>
            <%--</div>--%>
        <%--</div>--%>
        <!-- -->
        <!-- -->
        <!-- -->
   </div> 
    <%@ include file="foot.jsp"%> 
</body>
</html>