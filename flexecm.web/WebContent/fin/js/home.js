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
	var fo = new formCheck(".r.personRequest ");
	
	if (fo.test()) {
		var r = fo.getRequest();
		r.type = 2;
		$.post("/service/fin/loan/request",  r , function() {
			alert("融资申请已提交");
			
			navTo();
		});
	} else {
		
	}
}

function pRequestNext(p) {
	$(".lv03").hide();
	$(p).show();
}

function saveLoanRequst() {
	var fo = new formCheck(".r.personRequest ");
	var r = fo.getRequest();
	
	r.type = 2;
	
	$.post("/service/fin/loan/request/save",  r , function() {
		alert("保存成功");
	});
}

