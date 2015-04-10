/**
 * 
 */
$(document).ready(function() {
	
});

function pageAllAccount() {
	$(".gridr>div").hide();
	$(".gridr .account").show();
	
	$(".account .row.item").remove();
	
	$.getJSON("/service/fin/account/list", {}, function() {
		
	});
}

function pageUnConfirmed() {
	
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