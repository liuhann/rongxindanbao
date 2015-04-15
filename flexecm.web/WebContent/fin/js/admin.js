
/**
 */
$(document).ready(function() {
	$(".gridr>div").hide();
	navTo($("ul.nav li.navuser"));
});


function navTo(t) {
	$(".top ul.nav li").removeClass("current");
	$(".gridl .box").hide();
	
	$(".gridl .box." + $(t).attr("class")).show();
	
	eval($(".gridl .box." + $(t).attr("class")).find("li").first().attr("onclick"));
	$(t).addClass("current");
	
}

function pageAllAccount() {
	$(".gridr>div").hide();
	$(".gridr .accounts").show();
	
	$(".account .row.item").remove();
	
	$.getJSON("/service/fin/account/list", {}, function() {
		
	});
}

function pageUnConfirmed() {
	filterAccount({"ecfm":{"$ne":true}});
}
function pageConfirmed() {
	filterAccount({"ecfm":true});
}

function filterAccount(filter) {
	$(".gridr>div").hide();
	$(".gridr .accounts").show();
	
	$.post("/service/fin/account/filter", {
		"filter": JSON.stringify(filter)
	}, function(data) {
		var result = JSON.parse(data);
		initTable("#uncfmAccount",result, function(cell, data) {
			var cal = cell.data("cal");
			
			if (cal=="type") {
				if (data.type=="company") {
					cell.html("企业");
				}
				if (data.type=="person") {
					cell.html("个人");
				}
			}
		});
	});
}

function pageCompanys() {

	$(".gridr>div").hide();
	$("#vip-companies").show();
	$("#vip-companies .empty").show();
	$("#vip-companies .empty").html("正在读取列表");
	
	var filter = {
		"vip": true,
		"type": "company"
	};
	$.post("/service/fin/account/filter", {
		"filter": JSON.stringify(filter)
	}, function(data) {
		var result = JSON.parse(data);
		$("#vip-companies .empty").html("无绿色通道企业");
		initTable("#vip-companies",result);
	});
	
	/*
	$(".org .row.item").remove();

	$.getJSON("/service/fin/org/list", {}, function(data) {
		$(".org .empty").html("列表内容为空");	
		for(var i=0; i<data.list.length; i++) {
			var cloned = $(".org .template").clone().removeClass("template").addClass("item row");
			cloned.data("company-info", data.list[i]);
			cloned.find(".location").html(data.list[i].location);
			cloned.find(".company").html(data.list[i].company);
			cloned.find(".bizlicence").html(data.list[i].bizlicence);
			cloned.find(".loginid").html(data.list[i].loginid);
			cloned.find(".projects").html(data.list[i].projects?data.list[i].projects:0);
			cloned.find(".crating").html(data.list[i].crating?data.list[i].crating:"E");
			
			cloned.click(function() {
				editOrg($(this).data("company-info"));
			});
			$(".org .empty").hide();
			$(".org .table").append(cloned);
		}
	});
	
	*/
}

function addGreenOrg() {
	$(".gridr>div").hide();
	$(".companyEdit").show();
	var c = formCheck(".companyEdit ");
	c.init({});
	c.editable("edit");
}

function editOrg(company) {
	$(".gridr>div").hide();
	$(".companyEdit").show();
	var c = formCheck(".companyEdit ");
	c.init(company);
	c.readOnly("read");
}

function toggleEditOrg() {
	var c = formCheck(".companyEdit ");
	c.editable("edit");
	$(".companyEdit #loginid").attr("disabled", true);
	$(".companyEdit #loginid").css("border", "none");
}

function saveOrg() {
	var c = formCheck(".companyEdit ");
	if(c.test()==true) {
		var account = c.getRequest();
		account.type = "company";
		account.vip = true;
		$.post("/service/fin/account/save", account, function() {
			alert("保存完成");
			pageCompanys();
		}).fail(function(error) {
			alert("用户名称1");
		});
	}
}

function pageAllLoans() {
	$(".gridr>div").hide();
	$("#loans-list").show();
	
	$.post("/service/fin/loan/list", {
	}, function(data) {
		var result = JSON.parse(data);
		initTable("#loans-table", result);
	});
}


function addNews() {
	$(".gridr>div").hide();
	$(".gridr .add-news").show();
	loadCss("ueditor/themes/default/css/umeditor.css");
	loadJS([
	        "ueditor/lang/zh-cn/zh-cn.js",
	        "ueditor/umeditor.config.js",
	        "ueditor/umeditor.js"
	        ], function() {
			$(".add-news .empty").hide();
		  	var ue = UM.getEditor('umcontainer');
	});
}