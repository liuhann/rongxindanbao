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

function pageUnConfirmed() {
	$("#ccontent").load("sub/accounts.html", function() {
		filterAccount({"ecfm":{"$ne":true}});
	});
}

function pageConfirmed() {
	$("#ccontent").load("sub/accounts.html", function() {
		filterAccount({"ecfm":true});
	});
}

function adminList() {
	$("#ccontent").load("sub/backAccounts.html", function() {
		
		filterAccount({"type":"admin"});
	});
}
function backAccountList() {
	$("#ccontent").load("sub/backAccounts.html", function() {
		filterAccount({"type":"backac"});
	});
}

function editAccount(u) {
	$("#ccontent").load("sub/backAccountEdit.html", function() {
		var c = formCheck(".adminEdit ");
		c.init(u);
		c.editable("edit");
	});
}


function editAdmin(u) {
	$("#ccontent").load("sub/adminEdit.html", function() {
		var c = formCheck(".adminEdit ");
		c.init(u);
		c.editable("edit");
	});
}

function filterAccount(filter) {
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
	$("#ccontent").load("sub/viewCompanies.html");
}

function addNews() {
	$("#ccontent").load("sub/addNews.html");
}
function rolesList() {
	$("#ccontent").load("sub/roles.html");
}

function editRole() {
	$("#ccontent").load("sub/roleEdit.html");
}

function addGreenOrg() {
	$("#ccontent").load("sub/companyEdit.html", function() {
		var c = formCheck(".companyEdit ");
		c.init({});
		c.editable("edit");
	});
}

function editOrg(company) {
	$("#ccontent").load("sub/companyEdit.html", function() {
		var c = formCheck(".companyEdit ");
		c.init(company);
		c.readOnly("read");
	});
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


function pageFirstAudit() {
	var filter = {
		"audit": 1
	};
	viewLoanTable(filter, "初审列表", function(t, data, field) {
		$("<a class='gbtn'>查看</a>").appendTo($(t)).click(function() {
			var data = $(this).parents(".row").data("entry");
			viewLoan(data);
		});
	});
}

function pageFinalAudit() {
	var filter = {
		"audit": 2
	};
	viewLoanTable(filter, "复审列表", function(t, data, field) {
		$("<a class='gbtn'>查看</a>").appendTo($(t)).click(function() {
			var data = $(this).parents(".row").data("entry");
			viewLoan(data);
		});
		$("<a class='gbtn'>通过</a>").appendTo($(t));
		$("<a class='gbtn'>驳回</a>").appendTo($(t));
	});
}

function pageKilledLoans() {
	var filter = {
		"audit": -1
	};
	viewLoanTable(filter, "未通过列表", function(t, data, field) {
		$("<a class='gbtn'>查看</a>").appendTo($(t)).click(function() {
			var data = $(this).parents(".row").data("entry");
			viewLoan(data);
		});
	});
}

function pagePassedLoans() {
	var filter = {
		"audit": 5
	};
	viewLoanTable(filter, "已通过项目列表", function(t, data, field) {
		$("<a class='gbtn'>查看</a>").appendTo($(t)).click(function() {
			var data = $(this).parents(".row").data("entry");
			viewLoan(data);
		});
	});
}

function pageFinishedLoans() {
	var filter = {
		"audit": 5,
		"finished": 1
	};
	viewLoanTable(filter, "还款完结项目列表", function(t, data, field) {
		$("<a class='gbtn'>查看</a>").appendTo($(t)).click(function() {
			var data = $(this).parents(".row").data("entry");
			viewLoan(data);
		});
	});
}

function viewLoanTable(filter, txt, cb) {
	$(".gridr>div").hide();
	$("#loans-list").show();
	
	$.post("/service/fin/loan/list", {
		"filter": JSON.stringify(filter)
	}, function(data) {
		var result = JSON.parse(data);
		$("#loans-list .panel .title").html(txt)
		initTable("#loans-table", result, function(t,data,field) {
			if (field=="audit") {
				cb(t, data, field);
			}
		});
	});
}

function viewLoan(loan) {
	$(".gridr>div").hide();
	$("#view-company-loan").show();
}


function listNews() {
	$("#ccontent").load("sub/newslist.html");
}

_uploaderInit = false;
function initUploaders() {
	if (!_uploaderInit) {
		_uploaderInit = true;
		loadJS(["js/plupload.full.min.js"], function() {
			initUploader("btn-media-upload", "/service/attachz/upload", function(up, s, r) {
				if (r) {
					listPics();
				}
			});
			initUploader("btn-news-title-img", "/service/attachz/upload", function(up, s, r) {
				$("#news-title-img").attr("src", "/service/attachz/preview?id=" + r.response);
				$("#new-splash").val(r.response);
			});
		});
		
	}
}
function showPics() {
	$("#ccontent").load("sub/pics.html");
}
