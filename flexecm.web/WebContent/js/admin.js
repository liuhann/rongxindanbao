/**
 */
$(document).ready(function() {
	$(".gridr>div").hide();
	navTo($("ul.nav li.navuser"));
});


var editor;
KindEditor.ready(function(K) {
	editor = K.create('textarea[name="kind-editor"]', {
		resizeType : 1,
		allowPreviewEmoticons : false,
		allowImageUpload : false,
		items : [
		         'fontname', 'fontsize', '|', 'forecolor', 'hilitecolor', 'bold', 'italic', 'underline',
		         'removeformat', '|', 'justifyleft', 'justifycenter', 'justifyright', 'insertorderedlist',
		         'insertunorderedlist', '|', 'emoticons', 'image', 'link']
	});
});


function navTo(t) {
	//$(".top ul.nav li").removeClass("current");
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
	loadPage($("#ccontent"), "sub/addNews.html", function() {
		
	});
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
	var filter = {
	};
	viewLoanTable(filter, "所有项目", function(t, data, field) {
		if (field=="open") {
			$("<a class='gbtn'>查看</a>").data("loan", data).appendTo($(t)).click(function() {
				viewLoan($(this).data("loan"));
			});
		}
	});
}


function pageFirstAudit() {
	var filter = {
		"audit": 1
	};
	viewLoanTable(filter, "初审列表", function(t, data, field) {
		if (field=="open") {
			$("<a class='gbtn'>查看</a>").data("loan", data).appendTo($(t)).click(function() {
				viewLoan($(this).data("loan"));
			});
		}
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

function viewLoanTable(filter, txt, cellfunc) {
	$("#ccontent").show();
	$("#ccontent").load("sub/loanList.html", function() {
		$.post("/service/fin/loan/list", {
			"filter": JSON.stringify(filter)
		}, function(data) {
			var result = JSON.parse(data);
			$("#loans-list .panel .title").html(txt);
			initTable("#loans-table", result, function(t,data,cal) {
				if (cal=="open") {
					cellfunc(t, data, cal);
				}
			});
		});
	});
}

function viewLoan(loan) {
	if (loan.type==1) { //company
		$("#ccontent").html("");
		loadPages($("#ccontent"), 
				["sub/creq-1.html",
				 "sub/creq-2.html",
				 "sub/creq-3.html",
				 "sub/creq-4.html",
				 "sub/creq-5.html"],
				 ["基本信息","借款历史","担保情况","企业信息","经营情况"],
				function(url,div) {
					if (url==null) {
						var c = new formCheck("#ccontent ");
						c.init(loan);
						c.readOnly();
						$("img.field").css("width", "480").css("height", "360");
					}
				});
	} else if (loan.type==2) { //person
		$("#ccontent").html("");
		if (loan.mate3) {
			loadPages($("#ccontent"), 
					[
					 "sub/ureq-1.html",
					 "sub/ureq-2.html",
					 "sub/ureq-3.html",
					 "sub/ureq-4.html",
					 "sub/ureq-5.html",
					 "sub/ureq-3.html?mate=1",
					 "sub/ureq-4.html?mate=1",
					 "sub/ureq-5.html?mate=1",
					 "sub/ureq-6.html"
					],
					[
					 "基本信息",
					 "贷款需求",
					 "资产情况",
					 "借款历史",
					 "其他金融资产",
					 "配偶资产情况",
					 "配偶借款历史",
					 "配偶其他金融资产",
					 "家庭成员状况"
					],
					function(url,div) {
						if (url=="sub/ureq-3.html?mate=1") {
							var c = new formCheck(div);
							c.init(loan.mate3);
						} else if (url=="sub/ureq-4.html?mate=1") {
							var c = new formCheck(div);
							c.init(loan.mate4);
						} else if (url=="sub/ureq-5.html?mate=1") {
							var c = new formCheck(div);
							c.init(loan.mate5);
						} else {
							var c = new formCheck(div);
							c.init(loan);
						}
 						if (url==null) {
 							var c = new formCheck("#ccontent");
							c.readOnly();
						}
				});
		} else {
			loadPages($("#ccontent"), 
					[
					 "sub/ureq-1.html",
					 "sub/ureq-2.html",
					 "sub/ureq-3.html",
					 "sub/ureq-4.html",
					 "sub/ureq-5.html",
					 "sub/ureq-6.html"
					],
					[
					 "基本信息",
					 "贷款需求",
					 "资产情况",
					 "借款历史",
					 "其他金融资产",
					 "家庭成员状况"
					],
					function(url,div) {
						if (url==null) {
							var c = new formCheck("#ccontent");
							c.init(loan);
							c.readOnly();
						}
				});
		}
	}
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

function listNews() {
	loadPage($("#ccontent"), "sub/newslist.html");
}

function showPics() {
	loadPage($("#ccontent"), "sub/pics.html");
}
