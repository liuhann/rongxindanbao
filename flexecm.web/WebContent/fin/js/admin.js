/**
 * 
 */
$(document).ready(function() {
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
	$(".gridr .account").show();
	
	$(".account .row.item").remove();
	
	$.getJSON("/service/fin/account/list", {}, function() {
		
	});
}

function pageUnConfirmed() {
	$.getJSON("/service/fin/account/unconfirmed", {}, function(result) {
		initTable("#uncfmAccount",result)
	});
}

function initTable(tbid, data, cellcb) {
	
	$(tbid + " .row.item").remove();
	$(tbid + " .empty").show();
	$(tbid + " .empty").html("正在读取列表");
	
	for(var i=0; i<data.list.length; i++) {
		$(tbid).next(".empty").hide();
		var cloned = $(tbid + " .template").clone().removeClass("template").addClass("item row");
		cloned.find("div.cell").each(function() {
			
			if ($(this).data("f")) {
				$(this).html(data.list[i][$(this).data("f")]);
			}
			$(tbid).append(cloned);
		});
	}
}


function pageCompanys() {
	$(".gridr>div").hide();
	
	$(".gridr .org").show();
	
	$(".org .row.item").remove();
	$(".org .empty").show();
	$(".org .empty").html("正在读取列表");
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
		$.post("/service/fin/account/save", account, function() {
			alert("保存完成");
			pageCompanys();
		}).fail(function(error) {
			alert("用户名称1");
		});
	}
}