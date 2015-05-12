$(function() {
	
});

function dashboard(t) {
	navTo(t);
	
	$("#content .r").hide();
	$(".r.dashboard").show();
	
}

function loanProgress(t) {
	navTo(t);	
	$("#content .r").hide();
	$(".r.lprogress").show();	
	
	var filter = {
		"uid": currentUser
	}
	$.post("/service/fin/loan/list", {
		"filter": JSON.stringify(filter)
	}, function(data) {
		var result = JSON.parse(data);
		
		initTable("#loan-prgress-table", result);
	});
}

function config(t) {
	navTo(t);
	$("#content .r").hide();
	$(".r.personEdit").show();
}


function goRequest(t) {
	navTo(t);
	$("#content .r").hide();
	$(".r.personRequest").show();
	
	var fo = new formCheck(".r.personRequest ");
}

function navTo(t) {
	$(".menu_list li.current").removeClass("current");
	$(t).addClass("current");
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

function sendLoanRequest() {
	$.post("/service/fin/loan/request",  loanRequest , function() {
		alert("融资申请已提交");
		navTo();
	});
}

function startLoanRequest() {
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

function companyNewRequest() {
	loanRequest = {};
	currentTab = 0;
	currentPage = 0;
	
	creqPageNext();
}

function personNewRequest() {
	currentTab = 0;
	currentPage = 0;
	
	loanRequest = {};
	//自动填写的个人信息
	loanRequest.rname = uinfo.rname;
	loanRequest.idtype = uinfo.idtype;
	loanRequest.idcode = uinfo.idcode;
	
	loanRequest.mobile = uinfo.mobile;
	loanRequest.email = uinfo.email;
	loanRequest.type = 2;
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

function requestNext(tab, page, pre) {
	saveMerge();
	
	$(".steps .sele").removeClass("sele")
	$(".steps ." + tab).addClass("sele");
	
	$("#form-content").html('<div class="loading" style="line-height: 400px;text-align: center;font-size: 16px;">正在载入页面</div>');
	
	$("#form-content").load("sub/" + page + ".html?" + new Date().getTime(), function() {
		var fc = new formCheck("#form-content ");
		$("#form-content").data("field", pre);
		
		$("#form-content .savemerge").click(saveMerge);
		$("#form-content .pagenext").click(ureqPageNext);
		$("#form-content .cpagenext").click(creqPageNext);
		$("#form-content .submitloan").click(sendLoanRequest);
		
		if (pre) {
			fc.init(loanRequest[pre]);
		} else {
			fc.init(loanRequest);
		}
	});
}

function saveMerge() {
	var fc = new formCheck("#form-content ");
	if ($("#form-content").data("field")) {
		loanRequest[$("#form-content").data("field")] = fc.getRequest();
	} else {
		$.extend(loanRequest, fc.getRequest());
	}
}

function saveLoanRequst() {
	saveMerge();
	$.post("/service/fin/loan/request/save",  loanRequest , function() {
		alert("保存成功");
	});
}

