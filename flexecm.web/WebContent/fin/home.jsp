<%@page import="java.util.Map"%>
<%@page import="com.ever365.fin.FinanceService"%>
<%@page import="org.springframework.web.context.ContextLoaderListener"%>
<%@page import="com.ever365.rest.HttpServiceRegistry"%>
<%@ page contentType="text/html;charset=UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>融信担保-用户首页</title>

<SCRIPT type=text/javascript src="js/jquery-1.11.2.min.js"></SCRIPT>
<script type="text/javascript" src="js/webcommon.js"></script>


<script type="text/javascript" src="js/sitedata_bas.js"></script>

<!--css -->
<link type="text/css" rel="stylesheet" href="css/base.css"><!--通用CSS样式 -->
<link type="text/css" rel="stylesheet" href="css/top.css">
<link type="text/css" rel="stylesheet" href="css/admin.css">
<link type="text/css" rel="stylesheet" href="css/zhanghuguanli.css">
<link type="text/css" rel="stylesheet" href="css/zhanghuguanli_jksq.css">

<style>

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

.zhanghuguanli_jksq_001 .r .lv03 table tr td a {
	float: none;
	display: inline-block;
}
</style>
</head>
<body class="bg">
<%@ include file="top.jsp"%> 

<%
	FinanceService finService = ((FinanceService) ContextLoaderListener
			.getCurrentWebApplicationContext().getBean("fin.service"));
	
	Map<String, Object> currentUser = finService.getCurrentUser();
	
	if (currentUser.get("cu")==null) {
		response.sendRedirect("login.jsp");
	}
%>

<script type="text/javascript">
var currentUser = "<%=AuthenticationUtil.getCurrentUser()%>";
</script>

<script type="text/javascript" src="js/home.js"></script>

<div id="content">
    <!--内容开始 -->
    	<div class="zhanghuguanli_001 zhanghuguanli_jksq_001">
			<div class="l">
            	<div class="icon">
                	<img src="img/zhanghuguanli_001.png">
                    <span><a href="javascript:void(0)"><%=currentUser.get("rname") %></a>您好!</span>
                </div>
                <div class="menu_list">
                	<ul>
                    	<li class="current" onclick="dashboard(this);">
                        	账户总览
                        </li>
                    	<li onclick="goRequest(this);">
                        	借款申请&gt;
                        </li>
                    	<li onclick="loanProgress(this);">
                        	借款进度&gt;
                        </li>
                    	<li>
                        	<a href="#">评估报告&gt;</a>
                        </li>
                    	<li>
                        	<a href="#">信用评级&gt;</a>
                        </li>
                    	<li onclick="config(this);">
                        	信息设置&gt;
                        </li>                        
                                                                                                
                    </ul>
                </div>
            </div>

			<div class="r dashboard">
				
				<%if (!Boolean.TRUE.equals(currentUser.get("ecfm"))) { %>
				<div class="warn">
					<p>您未验证邮箱，请输入8位验证码: <input id="ecfm"> <a class="btn" onclick="checkEmail();">验证</a><p>
					<p>如果您的邮箱未收到验证码，请<a  onclick="resendEmail();">点击重新发送</a><p>
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
                
                <div class="r_block_3" >
                	借款分析
                </div>
            </div>
            
		<div class="r form personEdit hidden">
			<div class="title">
				<h3>编辑个人信息</h3> 
			</div>
			<div class="fcg">
				<label>登录帐号</label> 
				<div class="fc">
					<%=currentUser.get("loginid") %>
				</div>
			</div>
			
			<div class="fcg">
				<label>真实姓名</label> 
				<div class="fc">
					<input id="rname" type="text" data-checked="notnull" value="<%=currentUser.get("rname") %>" data-inval="请输入个人姓名">
				</div>
			</div>
			
			<div class="fcg">
				<label>身份证号码</label> 
				<div class="fc">
					<input id="pcode" type="text" data-checked="pcode" value="<%=currentUser.get("pcode") %>" data-inval="请输入有效的身份证号码">
				</div>
			</div>
			
			<div class="fcg">
				<label>重设密码</label> 
				<div class="fc">
					<input id="pwd" type="password" value="" data-checked="length-6" data-inval="密码要大于6位"> <a class="btn edit" href="javascript:resetPwd();">重设</a>
				</div>
			</div>
			<div class="fcg">
				<label>手机号码</label> 
				<div class="fc">
					<input id="mobile" type="text" data-checked="mobile" value="<%=currentUser.get("mobile") %>" data-inval="请输入有效的手机号码">
				</div>
			</div>
			<div class="fcg">
				<label>电子邮箱</label> 
				<div class="fc">
					<input id="email" type="text" data-checked="email" value="<%=currentUser.get("email") %>" data-inval="请输入有效的电子邮件地址">  
				</div>
			</div>
			
			<div class="fcg">
				<a class="btn edit" onclick="saveOrg();">保存</a>
			</div>
		</div>

        <div class="r  personRequest hidden">
           	<div class="lv01">
               	<a href="#" class="sele-bg">继续填写</a>
                   <a href="#">新填一个</a>
             </div>
                
			<div class="lv02">
               	  <ul>
                    	<li>
                       		<span class="first sele"><a href="#">基本信息</a></span><!--sele即为选中的样式 -->
                        </li>
                   	<li>
                       		<span><a href="#">借款历史</a></span>
                        </li>
                   	<li>
                       		<span><a href="#">身份信息</a></span>
                        </li>
                   	<li>
                       		<span class="end"><a href="#">补充材料</a></span>
                        </li>                                                                        
                  </ul>
                </div>
                
              <div class="lv03 p11">
                	<table>
                    	<!--表单项 -->
                    	<tr>
                        	<td style="text-align:right;">
                            	<span>贷款用途</span>
                            </td>
                            <td>
								<select  id="purpose" class="form-select">
									<option>购车贷款</option>
									<option>购房按揭</option>
									<option>个人消费</option>
                                </select>
                            </td>
                        </tr>
                        
                        <!--表单项 -->
                    	<tr>
                        	<td style="text-align:right;">
                            	<span>真实姓名</span>
                            </td>
                            <td>
                            	<%=currentUser.get("rname") %>
                            </td>
                        </tr>
                        
                        <!--表单项 -->
                    	<tr>
                        	<td style="text-align:right;">
                            	<span>您的称谓</span>
                            </td>
                            <td>
								<input name="appellation" type="radio" value=""><i>女士</i>
								<input name="appellation" type="radio" checked="checked"><i>先生</i>                                
                            </td>
                        </tr>

						<!--表单项 -->
                    	<tr>
                        	<td style="text-align:right;">
                            	<span>身份证件</span>
                            </td>
                            <td>
								<%=currentUser.get("idtype") %> 号码 <%=currentUser.get("idcode") %>                         
                            </td>
                        </tr>
						
						<!--表单项 -->
                    	<tr>
                        	<td style="text-align:right;">
                            	<span>手机号码</span>
                            </td>
                            <td>
                            	<span><%=currentUser.get("mobile") %></span>
                                <a href="#">不用这个手机？</a>
                            </td>
                        </tr>
                    	<!--表单项 -->
                    	<tr>
                        	<td style="text-align:right;">
                            	<span>常用邮箱</span>
                            </td>
                            <td>
                            	<%=currentUser.get("email") %>
                                <a href="#">不用这个邮箱？</a>
                            </td>
                        </tr>
                    	<!--表单项 -->
                    	<tr>
                        	<td style="text-align:right;">
							
                            </td>
                            <td>
								<input class="form-button" onclick="saveLoanRequest();" name="" type="button" value="保存">
                                <input class="form-button" onclick="pRequestNext('.p12');" type="button" value="下一步">
                            </td>
                        </tr>
                        <!-- -->                                                           
                    </table>
                </div>
                
                <div class="lv03 hidden p12">
                
                	<table>
                		<!--表单项 -->
                    	<tr>
                        	<td style="width:82px; text-align:right;">
                            	<span>贷款类型</span>
                            </td>
                            <td style="width:383px;" >
								<span class="loan-type"></span>	                            
                            </td>
                        </tr>
                        
                    	<!--表单项 -->
                    	<tr>
                        	<td style="width:82px; text-align:right;">
                            	<span>贷款金额</span>
                            </td>
                            <td style="width:383px;">
                            	<select  id="loan-scope" class="form-select">
									<option>5万及以下</option>
									<option>5-10万(含)</option>
									<option>10-20万(含)</option>
									<option>20万以上</option>
                                </select>
                            </td>
                        </tr>
                        
                        <!--表单项 -->
                    	<tr>
                        	<td style="width:82px; text-align:right;">
                            	<span>手工录入金额</span>
                            </td>
                            <td style="width:383px;">
                            	<input class="form-text" id="loan" type="text" value="" data-checked="number+"  data-inval="贷款金额必须是数字">
                                <b>万元</b>    <em class="loan"></em>
                            </td>
                        </tr>
                        <!-- -->                        
                    	<!--表单项 -->
                    	<tr>
                        	<td style="text-align:right;">
                            	<span>贷款期限</span>
                            </td>
                            <td>
                            	<select  id="duration-scope" class="form-select">
									<option value="1">1个月</option>
									<option value="3">3个月</option>
									<option value="6">6个月</option>
									<option value="12">12个月</option>
									<option value="24">24个月</option>
									<option value="36">36个月</option>
									<option value="0" >手工录入</option>
                                </select>
                            	<input class="form-text hidden" type="text" id="duration" value="0" data-checked="number+"  data-inval="贷款期限必须是数字">
                                <em class="duration"></em>
                            </td>
                        </tr>
                    	<!--表单项 -->
                    	<tr>
                        	<td style="text-align:right;">
                            	<span>还款方式</span>
                            </td>
                            <td>
                            	<select  id="repayment-type" class="form-select">
									<option value="1">按月等额本息</option>
									<option value="2">按月还息到期还本</option>
                                </select>
                            </td>
                        </tr>
                    	<!--表单项 -->
                    	<tr>
                        	<td style="text-align:right;">
                            	<span>婚姻状况</span>
                            </td>
                            <td>
                            	<input name="maritalStatus" type="radio" value="1" checked="checked"><i>未婚</i>
								<input name="maritalStatus" type="radio" value="2" ><i>已婚</i>    
								<input name="maritalStatus" type="radio" value="3"><i>离异</i>
								<input name="maritalStatus" type="radio" value="4"><i>丧偶</i>    
                            </td>
                        </tr>
                    	<!--表单项 -->
                    	<tr>
                        	<td style="text-align:right;">
                            </td>
                            <td>
								<input class="form-button" onclick="saveLoanRequest();" name="" type="button" value="保存">
                                <input class="form-button" onclick="pRequestNext('.p13');" type="button" value="下一步">
                            </td>
                        </tr>
                        <!-- -->    
                	</table>
                </div>
                
                <div class="lv03 hidden p13">
                	<table>
                		  <!--表单项 -->
                    	<tr>
                        	<td style="width:82px; text-align:right;">
                            	<span>月收入</span>
                            </td>
                            <td style="width:383px;">
                            	<input class="form-text" id="m-income" type="text" value="" data-checked="number+"  data-inval="月收入必须是数字">
                                <b>元</b><em class="m-income"></em>
                            </td>
                        </tr>
                          <!--表单项 -->
                    	<tr>
                        	<td style="width:82px; text-align:right;">
                            	<span>工作单位</span>
                            </td>
                            <td style="width:383px;">
                            	<input class="form-text" id="company" type="text" value="" >
                                <em class="company"></em>
                            </td>
                        </tr>
                        <!--表单项 -->
                    	<tr>
                        	<td style="width:82px; text-align:right;">
                            	<span>职务</span>
                            </td>
                            <td style="width:383px;">
                            	<input class="form-text" id="ctitle" type="text" value="" data-checked="notnull"  data-inval="职务不能为空">
                               <em class="ctitle"></em>
                            </td>
                        </tr>
                        <!--表单项 -->
                    	<tr>
                        	<td style="width:82px; text-align:right;">
                            	<span>是否有房</span>
                            </td>
                            <td style="width:383px;">
                            	<input name="hashp" type="radio" value="1" checked="checked"><i>是</i>
								<input name="hashp" type="radio" value="0"><i>否</i>    
                            </td>
                        </tr>
                        
                        <!--表单项 -->
                    	<tr>
                        	<td style="width:82px; text-align:right;">
                            	<span>房产性质</span>
                            </td>
                            <td style="width:383px;">
                            	<select  id="hp-type" class="form-select">
									<option value="1">大产权</option>
									<option value="2">小产权</option>
									<option value="3">经济适用房</option>
                                </select>
                            </td>
                        </tr>
                        
                            <!--表单项 -->
                    	<tr>
                        	<td style="width:82px; text-align:right;">
                            	<span>房产坐落地址</span>
                            </td>
                            <td style="width:383px;">
                            	<input class="form-text" id="hploc" type="text" value="" data-checked="notnull"  data-inval="地址不能为空"> <em class="hploc"></em>
                            </td>
                        </tr>
                             <!--表单项 -->
                    	<tr>
                        	<td style="width:82px; text-align:right;">
                            	<span>房产面积</span>
                            </td>
                            <td style="width:383px;">
                            	<input class="form-text" id="hpsize" type="text" value="" data-checked="number+"  data-inval="地址不能为空"> 平方米 <em class="hpsize"></em>
                            </td>
                        </tr>
          				 <!--表单项 -->
                    	<tr>
                        	<td style="width:82px; text-align:right;">
                            	<span>购房合同上房产价值</span>
                            </td>
                            <td style="width:383px;">
                            	<input class="form-text" id="hpprice" type="text" value="" data-checked="number+"  data-inval="价值不能为空">  <em class="hpprice"></em>
                            </td>
                        </tr>                    
                        
                	</table>
                </div>
            </div>

           
		<div class="r lprogress hidden">
		
		<div class="table" id="loan-prgress-table">
		    <div class="row header">
		      <div class="cell">
		        	申请时间
		      </div>
		      <div class="cell">
		       		金额
		      </div>
		      <div class="cell">
		       		期限
		      </div>
		       <div class="cell">
		       		联系方式
		      </div>
		      <div class="cell">
		      		状态
		      </div>
		    </div>
			    <div class="template">
			      <div class="cell" data-eval="var d=new Date(entry['rtime']); d.format('yyyy-MM-dd hh:mm')">
			      </div>
			      <div class="cell" data-eval="entry['loan'] + '万元'">
			      </div>
			      <div class="cell" data-eval="entry['until'] + '个月'">
			      </div>
			      <div class="cell" data-f="email">
			      </div>
			      <div class="cell" data-eval="(entry['audit']==1)?'初审中':'复审中'">
			      </div>
			    </div>
		</div>	
		<div class="empty">列表内容为空</div>

	</div>
           
    <div class="yinying">
    </div>
 </div>
        
        
    <!--内容结束 -->
</div>

	

<%@ include file="foot.jsp"%> 
	
</body>
</html>