<%@ page contentType="text/html;charset=UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>企业融资</title>

<SCRIPT type=text/javascript src="js/jquery-2.1.0.min.js"></SCRIPT>

<!-- 文件上传组件 -->
<SCRIPT type=text/javascript src="js/plupload.full.min.js"></SCRIPT>

<!--css -->
<link type="text/css" rel="stylesheet" href="css/base.css"><!--通用CSS样式 -->
<link type="text/css" rel="stylesheet" href="css/top.css">
<link type="text/css" rel="stylesheet" href="css/foot.css">

<link type="text/css" rel="stylesheet" href="css/rongzirukouP-1.css">

<script type="text/javascript" src="js/sitedata_bas.js"></script>
<!--jquery -->

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
<script type="text/javascript">


$(function(){
	for(var i=0; i<arrCity.length; i++) {
		$("#provinces").append("<option>" + arrCity[i].name  + "</option>");
	}	
	
	$("#provinces").change(function() {
		for(var i=0; i<arrCity.length; i++) {
			if (arrCity[i].name==$(this).val()) {
				$("#cities option").remove();
				for (var j = 0; j < arrCity[i].sub.length; j++) {
					$("#cities").append("<option>" + arrCity[i].sub[j].name + "</option>");
				}
				return;
			}
		}
	});
	
	initUploader(".fileUploader1Container", "fileUploader1");
	initUploader(".fileUploader2Container", "fileUploader2");
	initUploader(".fileUploader3Container", "fileUploader3");
	initUploader(".fileUploader4Container", "fileUploader4");
	initUploader(".fileUploader5Container", "fileUploader5");
});



function save(n, cb) {
	cb();
}

function nextStep(n) {
	save(n, function() {
		$(".step-content .form-x:visible").hide();
		$(".step-content .form-x.step" + n).show();
		
		$(".step-top li.select").click(function() {
			nextStep(n-1);
		});
		
		$(".step-top li.select").removeClass("select");
		$(".step-top li.step" + n).addClass("select");
	});
}

function initUploader(container,btnid) {
	var uploader = new plupload.Uploader({
	 	runtimes : 'html5,flash,silverlight,html4',
	    browse_button : btnid, // you can pass in id...
	    url : "/service/attach/upload",
	    filters : {
	        max_file_size : '10mb',
	        mime_types: [
	            {title : "选择图形格式文件", extensions : "jpg,gif,png"}
	        ]
	    },

	    // Flash settings
	    flash_swf_url : '/plupload/js/Moxie.swf',
	 
	    // Silverlight settings
	    silverlight_xap_url : '/plupload/js/Moxie.xap',
	 
	    init: {
	        FilesAdded: function(up, files) {
	        	 uploader.start();
	        },
	 
	        UploadProgress: function(up, file) {
	        	//$(".fileUploader1Container span").html(file.percent + "%");
	        },
	 
	        Error: function(up, err) {
	        	$(container).find("span").html("Error");
	        },
	        FileUploaded: function(up,file,r) {
	        	$(container).append('<img style="height:40px;" src="/service/fa/preview?id=' + r.response + '">');
	        }
	    }
	});
	 
	uploader.init();	
}

</script>

</head>
<body class="bg">
<%@ include file="top.jsp"%> 

<!--内容开始 -->
	<div id="content" class="clearfix">
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
                        	<li class="first select step1">基本资料</li><!--select 样式表示当前选中 也可以是当前进行时 -->
                            <li class="step2">您的借款历史</li>
                            <li class="step3">借款担保情况</li>
                            <li class="step4">进一步完善资料</li>
                            <li class="end step5">最后一些资料，加油</li>
                        </ul>
                    </div>
                    <!-- -->
                    <div class="step-content"> <!--具体的步骤操作-->
                    	<div class="form-x step1">
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
											<select class="form-sele1" id="provinces">
											</select>
                                            
											<select class="form-sele1" id="cities">
											  <option >请选择</option>
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
                                        	<input class="form-btn" type="button" onclick="save(1);" value="保存">
                                            <input class="form-btn" onclick="nextStep(2);" type="button" value="下一步">
                                        </div>
                                    </td>
                                </tr>                                                               
                            </table>
                    	</div>
                    
                    <div class="form-x step2 hidden">
                        	<table>
                                <!-- -->
                            	<tbody><tr>
                                	<td style="padding-top:5px;">借款主体</td>
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
                                	<td style="padding-top:5px;">借款额度</td>
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
                                	<td>借款方式</td>
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
                                	<td>合作银行</td>
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
                               <tr>
                                	<td>合作银行</td>
                                    <td>
                                    	<div class="form-item">
						<textarea class="form-textarea" name="" cols="" rows=""></textarea>
                                        </div>
                                        <div class="err">
                                        </div>                                        
                                    </td>
                                </tr>                                <!-- -->                                 
                          
                                <!-- -->                                <tr>
									<td></td>
                                    <td>
                                    	<div class="form-button">
                                        	<input class="form-btn" type="button" onclick="save(2);" value="保存">
                                            <input class="form-btn" onclick="nextStep(3);" type="button" value="下一步">
                                        </div>
                                    </td>
                                </tr>                                                               
                            </tbody></table>
                    	</div>
                    
                    	<div class="form-x step3 hidden">
                        	<table>

                                <!-- -->
                            	<tbody><tr>
                                	<td style="padding-top:5px;text-align:right;">被担保主体</td>
                                    <td>
                                    	<div class="form-item">
                                        	<input class="form-text" type="text" value="">
                                        </div>
                                        <div class="err">
                                        </div>
                                    </td>
                                </tr>
                                <!-- -->
                            	<tr>
                                	<td style="padding-top:5px;text-align:right;">担保金额</td>
                                    <td>
                                    	<div class="form-item">
                                        	<input class="form-text" type="text" value="">
                                        </div>
                                        <div class="err">
                                        </div>
                                    </td>
                                </tr>
                                <!-- --> 
                            	<tr>
                                	<td style="padding-top:5px;text-align:right;">担保期限</td>
                                    <td>
                                    	<div class="form-item">
                                        	<input class="form-text" type="text" value="">
                                        </div>
                                        <div class="err">
                                        </div>
                                    </td>
                                </tr>
                                <!-- -->                                                                 
                               <tr>
                                	<td style="padding-top:5px;text-align:right;">担保方式</td>
                                    <td style="padding-top:5px;">
                                    	<div class="form-item">
											<select class="form-sele" name="">
											  <option value=""></option>
											</select>
                                        </div>
                                        <div class="err">
                                        </div>                                        
                                    </td>
                                </tr>
                                <!-- -->  
                             
                               <tr>
                                	<td style="padding-top:5px;text-align:right;">合作银行</td>
                                    <td style="padding-top:5px;">
                                    	<div class="form-item">
											<select class="form-sele" name="">
											  <option value=""></option>
											</select>
                                        </div>
                                        <div class="err">
                                        </div>                                        
                                    </td>
                                </tr>
                                <!-- -->
                               <tr>
                                	<td style="padding-top:5px;text-align:right;">补充说明</td>
                                    <td>
                                    	<div class="form-item">
						<textarea class="form-textarea" name="" cols="" rows="" style="height:100px"></textarea>
                                        </div>
                                        <div class="err">
                                        </div>                                        
                                    </td>
                                </tr>                                <!-- -->                                 
                               <tr>
                                	<td style="padding-top:5px;text-align:right;">公司简介</td>
                                    <td>
                                    	<div class="form-item">
						<textarea class="form-textarea" name="" cols="" rows="" style="height:100px"></textarea>
                                        </div>
                                        <div class="err">
                                        </div>                                        
                                    </td>
                                </tr>                                  
                          
                                <!-- -->                                <tr>
									<td></td>
                                    <td>
                                        <div class="notice">
                                          <span><b>*</b>此简介为重要参考依据，请认真填写</span>
                                        </div>
                                    	<div class="form-button" style="padding-top:20px;">
                                        	<input class="form-btn" type="button" onclick="save(3);" value="保存">
                                            <input class="form-btn" type="button" onclick="nextStep(4);" value="下一步">
                                        </div>
                                    </td>
                                </tr>                                                               
                            </tbody></table>
                    	</div>
                    	
                    	
                    	
                    	<div class="form-x hidden step4">
                        	<table>

                                <!-- -->
                            	<tbody><tr>
                                	<td style="padding-top:5px;text-align:right;"><b>*</b>企业性质</td>
                                    <td>
                                    	<div class="form-item">
                                        	<input class="form-text" type="text" value="">
                                        </div>
                                        <div class="err">
                                        </div>
                                    </td>
                                </tr>
                                <!-- -->
                                <tr>
                                	<td style="padding-top:5px;text-align:right;"><b>*</b>所属行业</td>
                                    <td>
                                    	<div class="form-item">
                                        	<input class="form-text" type="text" value="">
                                        </div>
                                        <div class="err">
                                        </div>
                                    </td>
                                </tr>
                                <!-- -->
                                <tr>
                                	<td style="padding-top:5px;text-align:right;"><b>*</b>实际控制人</td>
                                    <td>
                                    	<div class="form-item">
                                        	<input class="form-text" type="text" value="">
                                        </div>
                                        <div class="err">
                                        </div>
                                    </td>
                                </tr>
                                <!-- -->
                                <tr>
                                	<td style="padding-top:5px;text-align:right;"><b>*</b>实际经营地址</td>
                                    <td>
                                    	<div class="form-item">
                                        	<input class="form-text" type="text" value="">
                                        </div>
                                        <div class="err">
                                        </div>
                                    </td>
                                </tr>
                                <!-- -->
                                <tr>
                                	<td style="padding-top:5px;text-align:right;"><b>*</b>基本开户行</td>
                                    <td>
                                    	<div class="form-item">
                                        	<input class="form-text" type="text" value="">
                                        </div>
                                        <div class="err">
                                        </div>
                                    </td>
                                </tr>
                                <!-- -->
                                <tr>
                                	<td style="padding-top:5px;text-align:right;"><b>*</b>贷款卡号</td>
                                    <td>
                                    	<div class="form-item">
                                        	<input class="form-text" type="text" value="">
                                        </div>
                                        <div class="err">
                                        </div>
                                    </td>
                                </tr>
                                <!-- -->
                                <tr>
                                	<td style="padding-top:5px;text-align:right;"><b>*</b>法人证件上传</td>
                                    <td>
                                    	<div class="form-item">
                                            <div class="fileUploader1Container">
                                            	<span class="info"></span>
                                             	<a id="fileUploader1" href="javascript:;">[点击上传]</a>
                                            </div>
                                        </div>
                                        
                                        <div class="err">
                                        </div>
                                    </td>
                                </tr>
                                <!-- -->   
                                <tr>
                                	<td style="padding-top:5px;text-align:right;"><b>*</b>开户许可证上传</td>
                                    <td>
                                    	<div class="form-item">
                                            <div class="fileUploader2Container">
                                            	<span class="info"></span>
                                             	<a id="fileUploader2" href="javascript:;">[点击上传]</a>
                                            </div>
                                        </div>
                                        
                                        <div class="err">
                                        </div>
                                    </td>
                                </tr>
                                <!-- -->   
                                <tr>
                                	<td style="padding-top:5px;text-align:right;"><b>*</b>营业执照上传</td>
                                    <td>
                                    	<div class="form-item">
                                            <div class="fileUploader3Container">
                                            	<span class="info"></span>
                                             	<a id="fileUploader3" href="javascript:;">[点击上传]</a>
                                            </div>
                                        </div>
                                        
                                        <div class="err">
                                        </div>
                                    </td>
                                </tr>
                                <!-- -->   
                                <tr>
                                	<td style="padding-top:5px;text-align:right;"><b>*</b>组织机构代码证上传</td>
                                    <td>
                                    	<div class="form-item">
                                             <div class="fileUploader4Container">
                                            	<span class="info"></span>
                                             	<a id="fileUploader4" href="javascript:;">[点击上传]</a>
                                            </div>
                                        </div>
                                        
                                        <div class="err">
                                        </div>
                                    </td>
                                </tr>
                                <!-- -->   
                                <tr>
                                	<td style="padding-top:5px;text-align:right;"><b>*</b>税务登记证上传</td>
                                    <td>
                                    	<div class="form-item">
                                            <div class="fileUploader5Container">
                                            	<span class="info"></span>
                                             	<a id="fileUploader5" href="javascript:;">[点击上传]</a>
                                            </div>
                                        </div>
                                        
                                        <div class="err">
                                        </div>
                                    </td>
                                </tr>
                                <!-- -->                                                                                                   
                                                                
                                <tr>
									<td></td>
                                    <td>
                                    	<div class="form-button" style="padding-top:0px;">
                                        	<input class="form-btn" type="button" onclick="save(4);" value="保存">
                                            <input class="form-btn" type="button" onclick="nextStep(5);" value="下一步">
                                        </div>
                                    </td>
                                </tr>                                                               
                            </tbody></table>
                    	</div>
                    	
                    	
                    	
                    	<div class="form-x step5 hidden" style="padding-left:20px;">
                        	<table>

                                <!-- -->
                            	<tbody><tr>
                                	<td style="padding-top:5px;text-align:right;">个人资产</td>
                                    <td>
                                    	<div class="form-item">
                                        	<input class="form-text" type="text" value="">
                                        </div>
                                        <div class="err">
                                        </div>
                                    </td>
                                </tr>
                                <!-- -->
                            	<tr>
                                	<td style="padding-top:5px;text-align:right;">固定资产</td>
                                    <td>
                                    	<div class="form-item">
                                        	<input class="form-text" type="text" value="">
                                        </div>
                                        <div class="err">
                                        </div>
                                    </td>
                                </tr>
                                <!-- --> 
                            	<tr>
                                	<td style="padding-top:5px;text-align:right;">近两年的水电费</td>
                                    <td>
                                    	<div class="form-item">
                                        	<input class="form-text" type="text" value="">
                                        </div>
                                        <div class="err">
                                        </div>
                                    </td>
                                </tr>
                                <!-- -->                                        
                            	<tr>
                                	<td style="padding-top:5px;text-align:right;">近两年的工资额</td>
                                    <td>
                                    	<div class="form-item">
                                        	<input class="form-text" type="text" value="">
                                        </div>
                                        <div class="err">
                                        </div>
                                    </td>
                                </tr>
                                <!-- -->   
                            	<tr>
                                	<td style="padding-top:5px;text-align:right;">近两年的销售收入分析表</td>
                                    <td>
                                    	<div class="form-item">
                                        	<input class="form-text" type="text" value="">
                                        </div>
                                        <div class="err">
                                        </div>
                                    </td>
                                </tr>
                                <!-- -->   
                            	<tr>
                                	<td style="padding-top:5px;text-align:right;">五名以上的上下游客户明细表</td>
                                    <td>
                                    	<div class="form-item">
                                        	<input class="form-text" type="text" value="">
                                        </div>
                                        <div class="err">
                                        </div>
                                    </td>
                                </tr>
                                <!-- -->   
                            	<tr>
                                	<td style="padding-top:5px;text-align:right;">您所能提供的抵押物的价值</td>
                                    <td>
                                    	<div class="form-item">
                                        	<input class="form-text" type="text" value="">
                                        </div>
                                        <div class="err">
                                        </div>
                                    </td>
                                </tr>
                                <!-- -->                                                                                                                                                             
                                <tr>
									<td></td>
                                    <td>
                                        <div class="notice">
                                          <span><b>*</b>终于完成了，仔细检查一下吧！</span>
                                        </div>
                                    	<div class="form-button" style="padding-top:20px;">
                                        	<input class="form-btn" type="button" onclick="save(5)" value="保存">
                                            <input class="form-btn" type="button" value="提交">
                                        </div>
                                    </td>
                                </tr>                                
                                <!-- -->                               
                            </tbody></table>
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




<%@ include file="foot.jsp"%> 
	
</body>
</html>