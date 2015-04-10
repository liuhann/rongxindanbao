<%@ page contentType="text/html;charset=UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>融资需求发布</title>


<SCRIPT type=text/javascript src="jquery-2.1.0.min.js"></SCRIPT>


<!--css -->
<link type="text/css" rel="stylesheet" href="css/base.css"><!--通用CSS样式 -->
<link type="text/css" rel="stylesheet" href="css/top.css">
<link type="text/css" rel="stylesheet" href="css/foot.css">
<!--jquery -->

<link type="text/css" rel="stylesheet" href="css/rongzirukouP-1.css">


<script type="text/javascript">


$(function() {
	
	$.getJSON("/service/fa/org/current", {
		
	},function(data) {
		$("#cuname").html(data.cu);
	}).fail(function() {
		alert("用户已失效");
		location.href = "/fin/orglogin.html";
	});
});


function sendRequest() {
	
	$.post("/service/fin/org/request", {
		"total" : $("#total").val(),
		"dura" : $("#dura").val(),
		"contract" : $("#contract").val(),
		"name" : $("#name").val(),
		"phone" : $("#phone").val(),
		"nav": 1
	}, function() {
		alert("您的融资需求已提交");
		nav("1");
	});
}

function frmrequest() {
	$(".content.request").show();
	$(".content.list").hide();
}


function nav(id) {
	$(".nav li.current").removeClass("current");
	$(".nav li.nav" + id).addClass("current");
	
	$.getJSON("/service/fin/org/request/list", {
		"nav": id,
		"start": 0,
		"limit": 20
	}, function(data) {
		$(".content.request").hide();
		$(".content.list").show();
		
		$(".content.list tr.item").remove();
		for(var i=0 ; i<data.list.length; i++) {
			var item = data.list[i];
			$(".content.list table").append("<tr class='item'><td>" + item.total +" 万元</td><td>" +
					item.dura + "月</td><td>" + item.name + "</td><td>" + item.phone + "</td><td>" 
					+ (item.comment?item.comment:"暂无") + "</td></tr>");
		}
	});
}
</script>
</head>
<body>

<%@ include file="top.jsp"%> 


	
    <!--内容开始 -->
	<div id="content">
		<!--左 -->
		<div class="rongzirukouP-l">
        	<ul>
            	<li><cite>平台融资方式的介绍</cite></li>
                <li><img src="img/rongzirukouP-1_001.png"><span><a href="#">服务于谁？</a></span></li>
                <li><img src="img/rongzirukouP-1_001.png"><span><a href="#">背景是谁？</a></span></li>
                <li><img src="img/rongzirukouP-1_001.png"><span><a href="#">政府背书的优势？</a></span></li>
                <li><img src="img/rongzirukouP-1_001.png"><span><a href="#">提供哪些服务？</a></span></li>
                <li><img src="img/rongzirukouP-1_001.png"><span><a href="#">服务方式？</a></span></li>
                <li><img src="img/rongzirukouP-1_001.png"><span><a href="#">收费方式？</a></span></li>
				<li><img src="img/rongzirukouP-1_001.png"><span><a href="#">投资方有哪些资源？</a></span></li>
                <li><img src="img/rongzirukouP-1_001.png"><span><a href="#">贷款需要的资料？</a></span></li>                
                <li class="end"></li>                                                                                
            </ul>
            <div class="yinying">
            </div>
        </div>
        <!--中 -->
		<div class="rongzirukouP-c">
        		<div class="left-nav"> <!--主控菜单 -->
                	<ul>
                    	<li class="first"><a href="#">企业融资</a>
                        	<div style="display:block"></div><!--控制行级样式即可滑动绿色条 -->
                        </li>
                        <li><a href="#">购车贷款</a><div style="display:none"></div></li>
                        <li><a href="#">购房贷款</a><div style="display:none"></div></li>
                        <li><a href="#">个人经营</a><div style="display:none"></div></li>
                        <li><a href="#">个人消费</a><div style="display:none"></div></li>
                        <li><a href="#">其他贷款</a><div style="display:none"></div></li>
                        <li class="end"></li>
                    </ul>
                </div>
                <!-- -->
                <div class="right-content"> <!--主控菜单显示区域 -->
                	<div class="step-top"> <!--进行步骤显示区域 -->
                    	<ul>
                        	<li class="first select">基本资料</li><!--select 样式表示当前选中 也可以是当前进行时 -->
                            <li>您的借款历史</li>
                            <li>借款担保情况</li>
                            <li>进一步完善资料</li>
                            <li class="end">最后一些资料，加油</li>
                        </ul>
                    </div>
                    <!-- -->
                    <div class="step-content"> <!--具体的步骤操作-->
                    	<div class="form-x">
                        	<table>
                            	<tr>
                                	<td style="padding-top:5px;">贷款金额</td>
                                    <td>
                                    	<div class="form-item">
                                        	<input class="form-text" type="text" value="5">
                                            <span>万元</span>
                                        </div>
                                        <div class="err">
                                        </div>
                                    </td>
                                </tr>
                                <!-- -->
                            	<tr>
                                	<td>贷款期限</td>
                                    <td>
                                    	<div class="form-item">
											<select class="form-sele" name="">
											  <option value="12个月">12个月</option>
											</select>
                                        </div>
                                        <div class="err">
                                        </div>                                        
                                    </td>
                                </tr>
                                <!-- -->      
                            	<tr>
                                	<td>居住城市</td>
                                    <td>
                                    	<div class="form-item">
											<select class="form-sele1" name="">
											  <option value="新疆">新疆</option>
											</select>
                                            
											<select class="form-sele1" name="">
											  <option value="乌鲁木齐">乌鲁木齐</option>
											</select>                                            
                                        </div>
                                        <div class="err">
                                        </div>                                        
                                    </td>
                                </tr>
                            	<tr>
                                	<td>您的称谓</td>
                                    <td>
                                    	<div class="form-item">
										<input class="form-radio" name="" type="radio" value=""><b>女士</b>      
                                        		                                        										<input class="form-radio" name="" type="radio" value="">                                         <b>先生</b>
                                        </div>
                                        <div class="err">
                                        </div>                                        
                                    </td>
                                </tr>
                                <!-- -->
                            	<tr>
                                	<td style="padding-top:5px;">您的姓名</td>
                                    <td>
                                    	<div class="form-item">
                                        	<input class="form-text" type="text" value="白璐">
                                        </div>
                                        <div class="err">
                                        </div>
                                    </td>
                                </tr>
                                <!-- -->
                            	<tr>
                                	<td style="padding-top:5px;">手机号码</td>
                                    <td>
                                    	<div class="form-item">
                                        	<input class="form-text" type="text" value="15349277850">
                                        </div>
                                        <div class="err">
                                        </div>
                                    </td>
                                </tr>
                                <!-- -->
                            	<tr>
                                	<td style="padding-top:5px;">常用邮箱</td>
                                    <td>
                                    	<div class="form-item">
                                        	<input class="form-text" type="text" value="15344587846@189.com">
                                        </div>
                                        <div class="err">
                                        </div>
                                    </td>
                                </tr>
                                <!-- -->                                <tr>
									<td></td>
                                    <td>
                                    	<div class="form-button">
                                        	<input class="form-btn" type="button" value="保存">
                                            <input class="form-btn" type="button" value="下一步">
                                        </div>
                                    </td>
                                </tr>                                                               
                            </table>
                    	</div>
                    </div> 
                </div>
        		<!-- -->
                <div class="yinying">
            	</div>
        </div>
        <!--右 -->
        <div class="rongzirukouP-r">
        	<div class="news-list first">
            	<cite>理财产品</cite>
                <em><a href="#">更多&gt;</a></em>
            	<ul>
                	<li><img src="img/rongzirukouP-1_001.png"><a href="#">服务于谁？</a></li>
                	<li><img src="img/rongzirukouP-1_001.png"><a href="#">背景是谁？</a></li>
                	<li><img src="img/rongzirukouP-1_001.png"><a href="#">政府背书的优势？</a></li>
                	<li><img src="img/rongzirukouP-1_001.png"><a href="#">提供哪些服务？</a></li>
                	<li><img src="img/rongzirukouP-1_001.png"><a href="#">服务方式？</a></li>
                </ul>

            </div>
            <div class="yinying">
            </div>
            <!-- -->
        	<div class="news-list">
            	<cite>理财产品</cite>
                <em><a href="#">更多&gt;</a></em>
            	<ul>
                	<li><img src="img/rongzirukouP-1_001.png"><a href="#">服务于谁？</a></li>
                	<li><img src="img/rongzirukouP-1_001.png"><a href="#">背景是谁？</a></li>
                	<li><img src="img/rongzirukouP-1_001.png"><a href="#">政府背书的优势？</a></li>
                	<li><img src="img/rongzirukouP-1_001.png"><a href="#">提供哪些服务？</a></li>
                	<li><img src="img/rongzirukouP-1_001.png"><a href="#">服务方式？</a></li>
                </ul>

            </div>
            <div class="yinying">
            </div>
            <!-- -->            
        	<div class="news-list">
            	<cite>理财产品</cite>
                <em><a href="#">更多&gt;</a></em>
            	<ul>
                	<li><img src="img/rongzirukouP-1_001.png"><a href="#">服务于谁？</a></li>
                	<li><img src="img/rongzirukouP-1_001.png"><a href="#">背景是谁？</a></li>
                	<li><img src="img/rongzirukouP-1_001.png"><a href="#">政府背书的优势？</a></li>
                	<li><img src="img/rongzirukouP-1_001.png"><a href="#">提供哪些服务？</a></li>
                	<li><img src="img/rongzirukouP-1_001.png"><a href="#">服务方式？</a></li>
                </ul>

            </div>
            <div class="yinying">
            </div>            
        </div>

    </div>
	<!--内容结束 -->
	
<div class="wrapper">
	<div>您好 <span id="cuname"></span> <a href="/service/fa/org/logout">退出</a></div>
</div>

<div class="wrapper">
	<div class="nav">
		<UL>
			<LI class="bang" onclick="frmrequest();">发布融资需求</LI>
			<LI >资料修改</LI>
			<li class="title">借款需求</li>
			<LI class="nav0" onclick="nav(0)">草稿</LI>
			<LI class="nav1" onclick="nav(1)">审核中</LI>
			<LI class="nav2" onclick="nav(2)">复审中</LI>
			<LI class="nav10" onclick="nav(10)">审核通过</LI>
			<LI class="nav-1" onclick="nav(-1)">未通过</LI>
		</UL>
	</div>
	
	<div class="content list" style="display:none;">
		<table class="gridtable">
			<tr>
				<th>贷款金额</th>
				<th>贷款期限</th>
				<th>姓名</th>
				<th>手机号码</th>
				<th>审核情况</th>
			</tr>
			
		</table>
	</div>
	
	
	<div class="content request">
		<div class="tab clearfix">
			<ul>
				<li class="step1 current">基本资料</li>
				<li class="step2">您的借款历史</li>
				<li class="step3">借款担保情况</li>
			</ul>
		</div>
		<div class="step1 form">
			<div><label>贷款金额</label> <span><input id="total"></span>万元 </div>
			<div><label>贷款期限</label> <span><input id="dura"></span> </div>
			<div><label>居住城市</label> <span><input id="contract"></span> </div>
			<div><label>您的称谓</label> <span><input type="radio" name="endf">先生<input type="radio" name="endf">女士</span> </div>
			<div><label>您的姓名</label>  <span><input id="name"></span> </div>
			<div><label>手机号码</label>  <span><input id="phone"></span> </div>
			<div><label>常用邮箱</label>  <span><input id="email"></span> </div>
			
			<div class="ops">
				<a class="btn" onclick="sendRequest()">保存</a>  <a>下一步</a>
			</div>
		</div>
		
		<div class="step2 form hide">
			<div><label>借款主体</label> <span><input id="main">5</span> </div>
			<div><label>借款额度</label> <span><input id="mm"></span> </div>
			<div><label>借款方式</label> <span><input id="method"></span> </div>
			<div><label>合作银行</label>  <span><input id="bank"></span> </div>
			<div><label>补充说明</label>  <textarea id="extra"></textarea></div>
			<a class="btn">保存</a>  <a>下一步</a>
		</div>
		
		<div class="step3 form hide">
			<div><label>被担保主体</label> <span><input id="undermain"></span> </div>
			<div><label>担保金额</label> <span><input id="dura"></span> </div>
			<div><label>担保期限</label> <span><input id="contract"></span> </div>
			<div><label>担保方式</label> <span><input type="radio" name="endf">先生<input type="radio" name="endf">女士</span> </div>
			<div><label>合作银行</label>  <span><input id="name"></span> </div>
			<div><label>补充说明</label>  <textarea id="extra"></textarea> </div>
			<a class="btn">确认提交</a>
		</div>
	
	</div>

</div>

<%@ include file="foot.jsp"%> 



</body>
</html>