$(function() {
	$(".menu_list li").click(function() {
		$(".menu_list li.current").removeClass("current");
		$(this).addClass("current");
	});
	if(location.hash) {
		$(location.hash).click();
	}

	$(".menu_list." + uinfo.type).show();
});

function dashboard() {
	$("#content .r").hide();
	$(".r.dashboard").show();
}

function loanProgress() {
	$("#content .r").hide();
	$(".menu_list li.current").removeClass("current");
	$(".menu_list li").eq(2).addClass("current");
	loadPage($("#ccontent"), "sub/loan-progress-user.html");
}

function pushedLoans() {
	$("#content .r").hide();
	loadPage($("#ccontent"), "sub/loan-received.html");
}

function intendedLoans() {
	$("#content .r").hide();
	loadPage($("#ccontent"), "sub/loan-intended.html");
}

function config() {
	$("#content .r").hide();
	$("#person-edit").show();
	
	if (uinfo.type=="company") {
		loadPage($("#person-edit"), "sub/cinfo.html", function() {
			var fc = new formCheck("#person-edit ");
			fc.init(uinfo);
			$("#loginid").replaceWith($("#loginid").val());
			
			fc.showBtn("edit");
		});
	} else {
		loadPage($("#person-edit"), "sub/uinfo.html", function() {
			var fc = new formCheck("#person-edit ");
			fc.init(uinfo);
		});
	}
}

function goRequest() {
	$("#content .r").hide();
	$(".r.personRequest").show();
	
	var fo = new formCheck(".r.personRequest ");
}


function checkEmail() {
	$.getJSON("/service/fin/account/email/confirm", {
		"ecfm": $("#ecfm").val()
	}, function(data) {
		if (data=="1") {
			alert("邮件验证成功");
			location.href = location.href; 
		} else {
			alert("邮件验证码错误");
		}
	})
}

function resendEmail() {
	$.get("/service/fin/account/email/resend", {}, function() {
		alert("验证邮件已经再次发送，请查收");
	});
}


function sendLoanRequest(t) {
	saveMerge();
	if (loanRequest.maritalStatus != "2") {
		delete loanRequest.mate3;
		delete loanRequest.mate4;
		delete loanRequest.mate5;
	}
	
	$.post("/service/fin/loan/request",{
		'map': JSON.stringify(loanRequest)
	}, function() {
		alert("融资申请已提交");
		loanProgress();
	});
}


function startDraft() {

	$("#ccontent").show();
	$(".r.dashboard").hide();

	$("#ccontent").load("sub/draftForm.html", function() {

	});
}

function startLoanRequest(t) {
	$(".menu_list li.current").removeClass("current");
	$(".menu_list li").eq(1).addClass("current");
	$("#content .r").hide();
	$(".loanRequest").show();
	$(".steps").hide();
	if (uinfo.type=="company") {
		companyNewRequest();
		$(".csteps").show();
	} else {
		$(".psteps").show();
		personNewRequest();
	}
}

function continueLoanEdit() {
	$.getJSON("/service/fin/loan/temp/get", {
		
	}, function(data){
		currentTab = 0;
		currentPage = 0;
		loanRequest = data;
		if (uinfo.type=="company") {
			creqPageNext();
			$(".csteps").show();
		} else {
			ureqPageNext();
			$(".psteps").show();
		}
	});
	
}

function companyNewRequest() {
	loanRequest = {};
	currentTab = 0;
	currentPage = 0;
	loanRequest.type = 1;
	loanRequest.mobile = uinfo.mobile;
	loanRequest.email = uinfo.email;
	loanRequest.name = uinfo.contact;
	loanRequest.mpart = uinfo.company;
	loanRequest.loanlimit = "100万";
	loanRequest.loanmethod = "担保贷款";
	loanRequest.loanbank = "中国工商银行";
	loanRequest.grtpart = uinfo.company;
	loanRequest.grttotal = "100万";
	loanRequest.grtmethod = "抵押";
	loanRequest.grtbank = "中国工商银行";
	loanRequest.grtextra = "担保合同的从属性，又称附随性、伴随性，是指担保合同的成立和存在必须以一定的合同关系的存在为前提。被担保的合同关系是一种主法律关系，为之而设立的担保关系是一种从法律关系。我国《担保法》第5条第1款规定：“担保合同是主合同的从合同。”";
	loanRequest["grtcompany-desc"] = "经过两年多的运营，得到了广大投资人的认可和支持。公司规模越来越大，业务量也稳步上升。在探索流程规范化和产品标准化的同时，我们陆续开设了包括深圳、广州、东莞、惠州、佛山、顺德、江门、中山、上海、武汉、南昌、合肥、成都、苏州、无锡、南通、柳州在内的共16家营业部。进一步完善了长三角、珠三角的战略布局，并向内地二三线城市下沉。";
	
	creqPageNext();
}

function personNewRequest() {
	currentTab = 0;
	currentPage = 0;
	$(".ureq-6,.ureq-7,.ureq-8").parent().hide();
	loanRequest = {};
	//自动填写的个人信息
	loanRequest.type = 2;
	loanRequest.rname = uinfo.rname;
	loanRequest.idtype = uinfo.idtype;
	loanRequest.idcode = uinfo.idcode;
	loanRequest.appellation = "0";
	loanRequest.maritalStatus = "1";

	loanRequest.mobile = uinfo.mobile;
	loanRequest.email = uinfo.email;
	
	loanRequest.loan = 28;
	loanRequest.duration = 4;
	
	loanRequest["duration-scope"] = "6";
	var step3 = {
		"hases": "1",
		"m-income" : "14000",
		"company": "北京龙腾世纪股份有限公司",
		"ctitle": "部门经理",
		"est-location": "新海家园",
		"est-size": "188",
		"hasveh": "1",
		"vehname": "特斯拉T101",
		"vehlicene": "京A NB1250",
		"vehbloan": "0"
	};
	var step4 = {
			"hasloan": "1",
			"grttotal": "100",
			"grtperson": "韩文",
			"grtuntil": "2016-05-26",
			"existingLoans" : [{loanhisaccount: "20",loanhisbank: "上海浦东发展银行",loanhisrepay: "15",loanhistend: "2015-05-26",loanhiststart: "2015-05-01",loanhistype: "车辆贷款"},
			                   {loanhisaccount: "20",loanhisbank: "上海浦东发展银行",loanhisrepay: "15",loanhistend: "2015-05-26",loanhiststart: "2015-05-01",loanhistype: "车辆贷款"}]
	};
	var step5 = {
			"hasotherfv": "1",
			"persongrt": "1",
			"bankdep": "240",
			"stock": "122",
			"fund": "22",
			"bond": "10",
			"hascompany": "0"
	};
	
	$.extend(loanRequest, step3);
	$.extend(loanRequest, step4);
	$.extend(loanRequest, step5);
	loanRequest.mate3 = step3;
	loanRequest.mate4 = step4;
	loanRequest.mate5 = step5;

	loanRequest.haschild = "1";
	loanRequest.hasotherfv = "1";
	loanRequest["childage"] = "23";
	loanRequest["childcoll"] = "北京科技大学";
	
	ureqPageNext();
}

var loanRequest = {};

var currentTab = 0;
var currentPage = 0;

function creqPageNext() {
	currentTab ++;
	currentPage ++;
	requestNext("creq-" + currentTab, "creq-" + currentPage);
}

function creqPagePrev() {
	currentTab --;
	currentPage --;
	requestNext("creq-" + currentTab, "creq-" + currentPage);
}

//翻到下一页方法。 用户选择了有配偶，则配偶也要编写 3、4、5三栏的信息
function ureqPageNext() {
	if (currentTab==5) {
		if (loanRequest.maritalStatus=="2") {
			currentTab = 6
			currentPage = 3;
		} else {
			currentTab = 9;
			currentPage = 6;
		}
	} else {
		currentTab ++;
		currentPage ++;
	}
	
	if (currentTab>=6 && currentTab<=8) {
		requestNext("ureq-" + currentTab, "ureq-" + currentPage, "mate" + currentPage);
	} else {
		requestNext("ureq-" + currentTab, "ureq-" + currentPage);
	}
}

//翻到下一页方法。 用户选择了有配偶，则配偶也要编写 3、4、5三栏的信息
function ureqPagePrev() {
	if (currentTab==9) {
		if (loanRequest.maritalStatus=="2") {
			currentTab = 8
			currentPage = 5;
		} else {
			currentTab = 5;
			currentPage = 5;
		}
	} else if (currentTab==6) {
		currentTab = 5;
		currentPage = 5;
	} else {
		currentTab --;
		currentPage --;
	}

	if (currentTab>=6 && currentTab<=8) {
		requestNext("ureq-" + currentTab, "ureq-" + currentPage, "mate" + currentPage);
	} else {
		requestNext("ureq-" + currentTab, "ureq-" + currentPage);
	}
}


function requestNext(tab, page, pre) {
	saveMerge();
	$(".steps .sele").removeClass("sele");
	$(".steps ." + tab).addClass("sele");
	
	$("#form-content").html('<div class="loading" style="line-height: 400px;text-align: center;font-size: 16px;">正在载入页面</div>');
	
	$("#form-content").load("sub/" + page + ".html?" + new Date().getTime(), function() {
		var fc = new formCheck("#form-content ");

		if (pre) {
			$("#form-content").data("field", pre);
		} else {
			$("#form-content").data("field", null);
		}

		$("#form-content .savemerge").click(function() {
			saveMerge();
			saveAsTemp();
		});
		$("#form-content .pagenext").click(ureqPageNext);
		$("#form-content .pageprev").click(ureqPagePrev);
		$("#form-content .cpagenext").click(creqPageNext);
		$("#form-content .cpagepre").click(creqPagePrev);
		$("#form-content .submitloan").click(sendLoanRequest);
		/*
		if ($("input.date").length>0) {
			$("input.date").datetimepicker({
				format:'Y-m-d',
				timepicker:false,
				lang:'ch'
			});
		}
		
		if ($("a.imgupload").length>0) {
			$("a.imgupload").each(function() {
				var btnid = $(this).attr("id");
				initUploader(btnid, "/service/fin/upload","jpg,gif,png" ,function(up, file, r) {
					var img = $('<img class="field" style="height:40px; width: 50px;" src="/service/fin/preview?id='
							+ r.response + '">');
					img.data("field", btnid);
					img.data("picdata", r.response);
					$("#" + btnid).next("img").remove();
					$("#" + btnid).after(img);
				});
			});
		}

		if ($("a.attachfile").length>0) {
			$("a.attachfile").each(function() {
				var btnid = $(this).attr("id");
				initUploader(btnid, "/service/fin/upload","*" ,function(up, file, r) {
					var a = $('<a class="field" href="/service/fin/preview?id='
						+ r.response + '"> ' + file.name + '</a>');
					a.data("field", btnid);
					a.data("picdata", r.response);
					$("#" + btnid).next("a.field").remove();
					$("#" + btnid).after(a);
				});
			});
		}
		*/
		if (pre) {
			fc.init(loanRequest[pre]);
		} else {
			fc.init(loanRequest);
		}
	});
}

function saveMerge(showal) {
	var fc = new formCheck("#form-content ");
	if ($("#form-content").data("field")) {
		loanRequest[$("#form-content").data("field")] = fc.getRequest();
	} else {
		$.extend(loanRequest, fc.getRequest());
	}
}

function saveAsTemp() {
	$.post("/service/fin/loan/temp/save", 
		{
			'map': JSON.stringify(loanRequest)
		},
		function() {
			alert("保存成功");
		}
	);
}

function saveOrg() {
	var c = formCheck("#person-edit ");
	if(c.test()==true) {
		var account = c.getRequest();
		$.post("/service/fin/account/update", encode(account), function() {
			alert("保存完成");
			location.href = location.href;
		}).fail(function(error) {
			
		});
	}
}

