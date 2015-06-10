/**
 */
$(document).ready(function() {
	$(".gridr>div").hide();
	navTo($("ul.nav li.navuser"));
	
	$(".gridl li").click(function() {
		$(".gridl li.selected").removeClass("selected");
		$(this).addClass("selected");
	});
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
		         'insertunorderedlist', '|', 'image', 'link']
	});
});


function navTo(t) {
	$(".top ul.nav li").removeClass("current");
	$(".gridl .box").hide();
	$(".gridl .box." + $(t).attr("class")).show();
	eval($(".gridl .box." + $(t).attr("class")).find("li").first().attr("onclick"));
	$(".gridl .box." + $(t).attr("class")).find("li").first().addClass("selected");
	$(t).addClass("current");
}

function pageUnConfirmed() {
	$("#ccontent").load("sub/accounts.html", function() {
		filterAccount({"ecfm":{"$ne":true},"registered": true});
	});
}

function pageConfirmed() {
	$("#ccontent").load("sub/accounts.html", function() {
		filterAccount({"ecfm":true,"registered": true});
	});
}

function backAccountList() {
	$("#ccontent").load("sub/admin-list.html", function() {
		filterAccount({"type":{"$in":["admin","bkuser"]}});
	});
}

function editAccount(u) {
	$("#ccontent").load("sub/account-edit.html", function() {
		var c = formCheck(".adminEdit ");
		c.init(u);
		c.editable("edit");
	});
}

function editAdmin(u) {
	$("#ccontent").load("sub/admin-edit.html", function() {
		var c = formCheck(".adminEdit ");
		c.init(u);
		c.editable("edit");
	});
}

function filterAccount(filter, skip, limit) {
	if (skip==null) skip = 0;
	if (limit==null) limit = 15;
	$.post("/service/fin/account/filter", {
		"filter": JSON.stringify(filter),
		'skip': skip,
		'limit': limit
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
		}, function(page) {
			filterAccount(filter, (page-1)*15, 15);
		});
	});
}

function pageCompanys() {
	loadPage($("#ccontent"), "sub/viewCompanies.html")
}

function pageLoanOfficer() {
	loadPage($("#ccontent"), "sub/credit-mgr-list.html");
}

function rolesList() {
	$("#ccontent").load("sub/role-list.html");
}

function editRole() {
	$("#ccontent").load("sub/role-edit.html");
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
			$("<a class='gbtn'>初审</a>").data("loan", data).appendTo($(t)).click(function() {
				viewLoan($(this).data("loan"), function(loan) {
					//if (loan.audit==1) { //初审中
						loadPages($("#ccontent"), ["sub/loan-approve.html"], ["项目初审"], function(url) {
							if (url==null) {
								$("#approve-content").data("loan", loan);
							}
						});
					//}
				});
			});
		}
	});
}

function pageFinalAudit() {
	var filter = {
		"audit": 2
	};
	viewLoanTable(filter, "复审列表", function(t, data, field) {
		if (field=="open") {
			$("<a class='gbtn'>复审</a>").data("loan", data).appendTo($(t)).click(function() {
				viewLoan($(this).data("loan"), function(loan) {
					loadPages($("#ccontent"), ["sub/loan-approve.html"], ["项目复审"], function(url) {
						if (url==null) {
							$("#approve-content").data("loan", loan);
							$("#risks").show();
						}
					});
				});
			});
		}
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
		"audit": 3
	};
	viewLoanTable(filter, "已通过项目列表", function(t, data, field) {
		$("<a class='gbtn'>查看</a>").data("loan",data).appendTo($(t)).click(function() {
			var data = $(this).data("loan");
			viewLoan(data, 
				function(loan) {
					//已通过则需要推送给项目经理
					loadPages($("#ccontent"), ["sub/loan-push.html"], ["推送给信贷经理"], function(url) {
						if (url==null) {
							$("#credit-push-content").data("loan", loan);
						}
					});
			});
		});

		$("<a class='gbtn'>设置进展</a>").data("loan",data).appendTo($(t)).click(function() {
			var data = $(this).data("loan");
			$("#ccontent").data("loan", data);
			$("#ccontent").html("");
			loadPages($("#ccontent"), ["sub/loan-finish.html"], ["编写项目进度情况"]);
		});
	});
}

function pageFinishedLoans() {
	var filter = {
		"audit": 10
	};
	viewLoanTable(filter, "还款完结项目列表", function(t, data, field) {
		$("<a class='gbtn'>查看</a>").appendTo($(t)).click(function() {
			var data = $(this).parents(".row").data("entry");
			viewLoan(data, function(loan) {
				$("#ccontent").append('<h2>后续进度</h2>');
				$("#ccontent").append("<div class='page'>" + data.progress + "</div>");
			});
		});
	});
}

function viewLoanTable(filter, txt, cellfunc) {
	$("#ccontent").show();
	loadPage($("#ccontent"), "sub/loanList.html", function() {
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

function pageNews() {
	loadPage($("#ccontent"), "sub/news-list.html");
}

function pageLicai() {
	loadPage($("#ccontent"), "sub/fin-product-list.html");
}

function pageFinaMarket() {
	loadPage($("#ccontent"), "sub/fin-markets-list.html");
}

function pageFinResource() {
	loadPage($("#ccontent"), "sub/fin-res-list.html");
}
