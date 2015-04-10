<%@page import="java.util.Date"%>
<%@page import="com.ever365.rest.AuthenticationUtil"%>
<%@ page contentType="text/html;charset=UTF-8"%>

<%
AuthenticationUtil.setCurrentUser(null);
Object user = request.getSession().getAttribute(AuthenticationUtil.SESSION_CURRENT_USER);
if (user != null) {
	AuthenticationUtil.setCurrentUser((String) user);
} 
Date date = new Date();
%>
    <!--头部开始 -->
	<div id="top">
		<script type="text/javascript">
		
			$(document).ready(function() {
				$(".nohot").hover(function() {
					$(".pop_login").removeClass("hide");
				}, function() {
					$(".pop_login").addClass("hide");
				});
			});
		</script>
    	<div class="top-lv01">
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
	                    <span class="nohot"><a style="color:#666;" href="login.jsp">快速登录</a>
                    </span>
                    <%} else { %>            
	                    <span class="cls-mr"><a class="u-line" href="#"><%=AuthenticationUtil.getCurrentUser() %></a></span>
                    	<span>欢迎您来融信融资网</span>
                    	<span><a href="/service/fin/logout">【安全退出】</a></span>
                    <%} %>
                </div>
            </div>
        </div>
    	<!-- -->
    	<div class="top-lv02">
			<div class="l">
            	<a href="index.jsp"><img src="img/top_004.png"></a>
            </div>
			
            <div class="r">
            	<a href="">快速融资</a>
            </div>
        </div>
        <!-- -->
        <div class="top-lv03">
        	<ul>
            	<li><a href="index.jsp">首页</a></li>
            	<li><a href="#">融资入口</a></li>
            	<li><a href="me.jsp">账号管理</a></li>
            	<li><a href="#">投资资源</a></li>
            	<li><a href="#">理财产品</a></li>
            	<li><a href="#">金融超市</a></li>                                                                                
            </ul>
        </div>
    </div>

