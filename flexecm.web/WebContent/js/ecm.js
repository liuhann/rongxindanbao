var currentEntity = {};
var currentUser ;

$(document).ready(function(){
	$("*[onclick]").each(function() {
		$(this).data("event", $(this).attr("onclick"));
		$(this).removeAttr("onclick");
		attachEvent($(this), function(t) {
			eval($(t).data("event"));
		});
	});
	initUi();
	initUploader();
	pageMyRepo();
});


function initUi() {
	$("div.oper").hide();
	clear();
	attachEvent($("#upload-dialog a.min"), function() {
		$("#upload-dialog").css("top", $(window).height() -32);
		$("#upload-dialog a.min").hide();
		$("#upload-dialog a.max").show();
	});
	attachEvent($("#upload-dialog a.max"), function() {
		$("#upload-dialog").css("top", $(window).height() -32-295);
		$("#upload-dialog a.min").show();
		$("#upload-dialog a.max").hide();
	});
	attachEvent($("#upload-dialog a.close"), function() {
		$("#upload-dialog").hide();
	});
	$(".dialog .dialog-handle a.diag-close").click(function() {
		closeDialog();
	});
	
/*	
	$.getJSON("/service/person/current", null, function(user) {
		currentUser = user;
	});
	*/
}

function clear() {
	hideMessage();
	hideDialog();
	closeRight();
	$("#upload-dialog").hide();
	$(".base .center>div").hide();
	$("div.head div.oper").hide();
}

var ROLE_LIST=["Admin", "Coordinator", "Contributor", "Creator", "Reader"];

function showButtons(role, selected) {
	$("div.head div.oper").show();
	$("a.head-btn").addClass("disabled");
	$("a.head-btn").each(function() {
		if ($(this).attr("selectonly")=="true") { //on for file selected  like copy/move/delete etc.
		} else {
			if (ROLE_LIST.indexOf(role)<=ROLE_LIST.indexOf($(this).attr("role"))) {
				$(this).removeClass("disabled");
			}
		}
	});
}

function addFile(r) {
	showDialog("file-dialog");
	
	$("#yun-file-detail").hide();
	$("#url-yun-share").val("");
	$("#yun-file-url").val("");
	$("#yun-file-name").val("");
	$("#yun-file-size").val("0B");
	
	if (r=="yun") {
		$("#file-dialog div.direct").hide();
	} else {
		$("#file-dialog div.direct").show();
	}
}

function onFileChecked() {
	var files = getCheckedFiles();
	$("div.oper").show();
	
	$("a.head-btn").addClass("disabled");
	
	//首先根据select属性做基础判断
	$("a.head-btn").each(function() {
		if ($(this).attr("select")=="*") { //always show
			$(this).removeClass("disabled");
		}
		if ($(this).attr("select")==">0" && files.length>0) { //on for file selected  like copy/move/delete etc.
			$(this).removeClass("disabled");
		}
		if ($(this).attr("select")=="=1" && files.length==1) { //on for file selected  like copy/move/delete etc.
			$(this).removeClass("disabled");
		}
	});
	//判断文件或者文件夹的显隐情况
	$("a.head-btn:not(.disabled)").each(function() {
		if ($(this).attr("type")=="file") {
			for ( var i = 0; i < files.length; i++) {
				if (files[i].type!="s:file") {
					$(this).addClass("disabled");
					break;
				}
			}
		}
	});
	
	if (currentEntity!=null) { //进行进一步权限判断
		if (currentEntity.owner == currentUser) return;  
		
		//获取ACE中得到的权限
		var permissions = [];
		for ( var i = 0; i < currentEntity.role.length; i++) {
			var details = currentEntity.role[i].split(":");
			permissions.push(details[1]);
		}
		
		//判断是否是创建者
		if (currentEntity.creator == currentUser) {
			permissions.push("Creator");
		}
		if (files.length>0) {
			var creator = true;
			for ( var i = 0; i < files.length; i++) {
				if (files[i].creator!=currentUser) {
					creator = false;
					break;
				}
			}
			if (creator) {
				permissions.push("Creator");
			}
		}
		
		$("a.head-btn:not(.disabled)").each(function(){
			if ($(this).attr("permission")!=null) {
				var required = $(this).attr("permission").split(",");
				var hasPermission = false;
				
				for ( var i = 0; i < permissions.length; i++) {
					if (required.indexOf(permissions[i])>-1) {
						hasPermission = true;
						break;
					}
				}
				if (!hasPermission) {
					$(this).addClass("disabled");
				}
			}
		});
	}
}

function hideDialog(dialogname) {
	$("div.hide-bg").hide();
	if (dialogname==null) {
		$("div.dialog").hide();
	} else {
		$("#" + dialogname).hide();
	}
}

function showDialog(dialogname) {
	$("div.hide-bg").show();
	var left = ($(window).width() - parseInt($("#" + dialogname).css("width")))/2;
	var top = ($(window).height() - parseInt($("#" + dialogname).css("height")))/2;
	$("#" + dialogname).css("left", left);
	$("#" + dialogname).css("top", top);
	$("#" + dialogname).show();
}

function closeDialog() {
	$("#listing ul.files li.checked").removeClass("checked");
	$("div.hide-bg").hide();
	$(".dialog").fadeOut("fast");
}

function listClipboardFiles() {
	showClipboard();
}

function listUploading() {
	$("#upload-dialog").show();
	$("#upload-dialog").css("top", $(window).height() -32-295);
	$("#upload-dialog a.min").show();
	$("#upload-dialog a.max").hide();
}


function addToClipboard() {
	var files = getCheckedFiles();
	
	for ( var i = 0; i < files.length; i++) {
		var file = files[i];
		if ($("#clip_"+file._id).length>0) continue;
		var li = $("li.cbitemTemplate").clone();
		li.data("file", file);
		li.attr("id", "clip_" + file._id);
		li.removeClass("cbitemTemplate").removeClass("hidden").addClass("cbitem");
		li.find(".filename").html(file.name);
		
		if (file.type=="s:folder") {
			li.find("p.ico").addClass("fd");
		} else {
			var endfix = file.name.substring(file.name.lastIndexOf(".")+1);
			li.find("p.ico").addClass(endfix);
		}
		
		attachEvent(li.find("a.mv"), function (d) {
			var fileData = $(d).parents("li.cbitem").data("file");
			if (fileData.pid==currentEntity._id) {
				showMessage("文件就在此目录中");
				return;
			}
			$.post("/service/file/move", {
				"srcPath": [fileData._id],
				"targetPath": currentEntity._id
			}, function() {
				refreshFolder("文件已经移动");
				$("#clip_"+fileData._id).remove();
			}).fail(function(error) {
				if (error.status==409) {
					showMessage("目录已经有同名文件");
				}
				if (error.status==406) {
					showMessage("不能移动到自己的子目录");
				}
			});
		});

		attachEvent(li.find("a.cp"), function(d) {
			var fileData = $(d).parents("li.cbitem").data("file");
			
			$.post("/service/file/copy", {
				"srcs": [fileData._id],
				"target": currentEntity._id
			}, function() {
				refreshFolder();
				$("#clip_"+fileData._id).remove();
			}).fail(function(error) {
				if (error.status==409 || error.status==400) {
					showMessage("目录已经有同名文件");
				}
				if (error.status==406) {
					showMessage("不能复制到自己的子目录");
				}
				
			});
		});
		
		$("#cblist").append(li);
	}
	
	unCheckFiles();
	showClipboard();
}

function clearClipboard() {
	$("#cblist>li.cbitem").remove();
}

function hideDropDown() {
	$("div.oper .dropdown").hide();
}
function showDropDown() {
	$("div.oper .dropdown").slideDown('fast');
	$("div.oper .dropdown").mouseleave(function() {
		hideDropDown();
	});
}


function toggleDropDown() {
	if ($("div.oper .dropdown:visible").length>0) {
		hideDropDown();
		return;
	}
	
	if (getCheckedFiles().length>0) {
		if ($("div.oper .dropdown:visible").length==0) {
			showDropDown();
		}
	}
}

function copy(id) {
	hideDropDown();
	showDialog("copy-move-dialog");
	$("#copyToUL>li").remove();
	$("#copy-move-dialog").data("action", "copy");
	$("#copy-move-dialog").data("src", id);
	$("#copyToUL>li").remove();
	$("#copy-move-dialog div.title span").html("复制文件到");
	
	addDialogTreeNode(null, "_user_root", "我的文件", "home", true);
	//addDialogTreeNode(null, "_public_root", "公共目录", "public", true);
	//addDialogTreeNode(null, "_shared_root", "分享的目录", "shared", true);
}
function move(id) {
	hideDropDown();
	$("#copy-move-dialog").data("action", "move");
	$("#copy-move-dialog").data("src", id);
	showDialog("copy-move-dialog");
	$("#copyToUL>li").remove();
	$("#copy-move-dialog div.title span").html("移动文件到");
	
	addDialogTreeNode(null, "_user_root", "我的文件", "home", true);
	//addDialogTreeNode(null, "_public_root", "公共目录", "public", true);
	//addDialogTreeNode(null, "_shared_root", "分享的目录", "shared", true);
}

function addDialogTreeNode(parent, id, name,icon, hasChildren) {
	if ($("#target" + id).length>0) return;
	
	var li = $('<li><span><em></em><dfn></dfn><span class="name"></span></span><ul></ul></li>');
	var parentul;
	if(parent==null) {
		parentul = $("#copyToUL");
	} else {
		parentul = $("#target" + parent);	
	}
	
	li.find("ul").attr("id", "target" + id);
	li.find("span.name").html(name);
	if (hasChildren) {
		li.find("em").addClass("plus");
	} else {
		li.find("em").addClass("empty");
	}
	li.find("dfn").addClass(icon);
	li.find("em").data("pid", id);
	
	attachEvent(li.find("em"), function(em) {  // the open child events
		var parentId = $(em).data("pid");
		if (em.hasClass("plus")) {
			$.getJSON("/service/repository/tree/list", {
				"pid": parentId
			}, function(list) {
				handleList(list, parentId);
			});
			function handleList(list, ulId) {
				for ( var i = 0; i < list.length; i++) {
					if (list[i].fot>0) {
						addDialogTreeNode(ulId, list[i]._id,list[i].name, "closed",true);
					} else {
						addDialogTreeNode(ulId, list[i]._id,list[i].name, "closed",false);
					}
				}
				if (list.length==0) {
					em.removeClass("plus").addClass("empty");
				} else {
					em.removeClass("plus").addClass("minus");
				}
				if (em.next().hasClass("closed")) {
					em.next().removeClass("closed").addClass("open");
				}
			}
		} else if (em.hasClass("minus")) {
			$("#target" + parentId).find("li").remove();
			em.removeClass("minus").addClass("plus");
			if (em.next().hasClass("open")) {
				em.next().removeClass("open").addClass("closed");
			}
		}
	});
	
	attachEvent(li.find(">span"), function(span) {
		$('ul.tree li>span.checked').removeClass("checked");
		$(span).addClass("checked");
	});
	parentul.append(li);
}

function confirmCopyOrMove() {
	if ($('ul.tree li>span.checked').length==0) {
		return;
	}
	var targetId = $('ul.tree li>span.checked').find("em").data("pid");
	
	if (targetId.indexOf("_")==0) {
		if (targetId=="_user_root") {
			targetId=null;
		} else {
			return;
		}
	}
	var src = $("#copy-move-dialog").data("src");
	
	if ($("#copy-move-dialog").data("action")=="copy") {
		hideDialog();
		$.post("/service/file/copy", {
			"src" : src,
			"target": targetId
		}, function() {
			hideDialog();
			refreshFolder("文件已经拷贝完成");
		}).fail(function(error) {
			hideDialog();
			if (error.status==409) {
				showAlert("拷贝失败，原因：目的文件夹存在同名文件");
			} else if (error.status==406) {
				showAlert("拷贝失败，原因：无法将文件夹拷贝到自己的子目录下");
			}
		});
	} else if ($("#copy-move-dialog").data("action")=="move") {
		hideDialog();
		$.post("/service/file/move", {
			"src" : src,
			"target": targetId
		}, function() {
			hideDialog();
			refreshFolder("文件已经移动完成");
		}).fail(function(error) {
			hideDialog();
			if (error.status==409) {
				showAlert("移动失败，原因：目的文件夹存在同名文件");
			} else if (error.status==406) {
				showAlert("移动失败，原因：无法将文件夹移动到自己的子目录下");
			}
		});
	}
}


function copyListItem() {
	if ($("#cblist>li.tocopy").length==0) return;
	
	var data = $("#cblist>li.tocopy").data("file");
	
	$("#cblist>li.tocopy").find("span.btns").hide();
	$("#cblist>li.tocopy").find("span.infos").show();
	
	$("#cblist>li.tocopy").find("span.infos").html("正在拷贝..");
	
	$.post("/service/file/copy", 
			{
				"srcs": [data._id],
				"target": currentEntity._id
			}, function() {
				refreshFolder();
				$("#cblist>li.tocopy").find("span.btns").show();
				$("#cblist>li.tocopy").find("span.infos").html("拷贝完成");
				
				if ($("#cblist>li.tocopy").next().length==0) {
					$("#cblist>li.tocopy").removeClass("tocopy");
					
					
				} else {
					$("#cblist>li.tocopy").removeClass("tocopy").next().addClass("tocopy");
					copyListItem();
				}
			}
	);
	
}


function showPenddingMsg(text) {
	showDialog("pendding-dialog");
	$("#pendding-dialog div.content").html(text);
}

function showAlert(text, func) {
	showDialog("alert-dialog");
	$("#alert-dialog div.content").html(text);
}

function showConfirm() {
	
}

function closeRight(div) {
	$(".base .right").css("right", -400);
	$(".base .center").css("right", 0);
	
	if (div!=null) {
		$("#" + div).hide();
	}
}

function showClipboard() {
	$(".base .right").css("right", 0);
	$(".base .right").css("opacity", 1);
	$(".base .center").css("right", 320);
	$(".right>div").hide();
	$("#clipboard").show();
}

function showTrash() {
	$(".base .right").css("right", 0);
	$(".base .right").css("opacity", 1);
	$(".base .center").css("right", 320);

	$(".right>div").hide();
	$("#trash").show();
}

function addFolderDialog() {
	$("#name-dialog").data("file", null);
	$("#name-dialog input.name").val("");
	showDialog("name-dialog");
}

function listTrashFiles() {
	$.getJSON("/service/entity/trash/list", {
		"repo": encodeURI(currentEntity.repo) 
	}, function(data) {
		$("#trashlist").data("repo", currentEntity.repo);
		$("#trashlist li.trashItem").remove();
		for ( var i = 0; i < data.length; i++) {
			var file = data[i];
			if ($("#trash_"+file._id).length>0) continue;
			var li = $("li.trashitemTemplate").clone();
			
			li.data("file", file);
			li.attr("id", "trash" + file._id);
			li.removeClass("trashitemTemplate").removeClass("hidden").addClass("trashItem");
			
			li.find(".filename").html(file.oname);
			
			li.find("div.content p").html("<i>" + Utils.formatTime(file.modified) + "</i>" + "被<i>" + file.modifier +  "</i>删除");
			
			li.find("div.btns").hide();
			
			attachEvent(li.find("a.remove"), function(t) {
				removeTrashItem(t);
			});
			attachEvent(li.find("a.recover"), function(t) { 
				recoverTrashItem(t);
			});
			
			li.hover(function() {
				$(this).find("div.btns").show();
			}, function() {
				$(this).find("div.btns").hide();
			});
			
			if (file.type=="s:folder") {
				li.find("p.ico").addClass("fd");
			} else {
				var endfix = file.oname.substring(file.oname.lastIndexOf(".")+1);
				li.find("p.ico").addClass(endfix);
			}
			$("#trashlist").append(li);
		}
		showTrash();
	});
}

function removeTrashItem(entity) {
	$.post("/service/entity/remove", {
		"id": entity._id
	}, function() {
	});
}

function recoverTrashItem(entity) {
	$.post("/service/file/recover", {
		"ids": [entity._id]
	}, function() {
		showMessage("文件已恢复");
	}).fail(function() {
		showMessage("文件恢复失败：原来路径不存在或者文件冲突");
		pageTrash();
	});
}

function recoverAll() {
	$.post("/service/file/recoverAll", {
				"repo" : $("#trashlist").data("repo")
			}, 
			function(data) {
				listTrashFiles();
				refreshFolder("文件已经还原");
	});
}

function clearTrash() {
	$.post("/service/trash/clean", {
	}, 
	function(data) {
		showMessage("回收站已清空", 3000);
	});
}



function confirmName() {
	var id = $("#name-dialog").data("file");
	var newName = $("#name-dialog input.name").val();
	if (newName=="") {
		$("#name-dialog .msg").html("文件名称不能为空");
		return;
	}
	if (id==null) { //add Folder
		showPenddingMsg("正在创建文件夹");
		$("name-dialog .msg").html();
		$.post("/service/folder/create", {
			"id": currentEntity._id,
			"name": newName
		}, function() {
			listFolder(currentEntity._id);
			closeDialog();
		}).fail(function(jqXHR, textStatus, errorThrown) {
			if (jqXHR.status==409) {
				$("name-dialog .msg").html("同名文件已经存在");
			}
		});
	} else {
		showPenddingMsg("正在重命名");
		$.post("/service/file/rename", {
			"src": id,
			"newName": newName
		}, function() {
			if (currentEntity.type="s:file") {
				listFolder(currentEntity.pid);
			} else {
				listFolder(currentEntity._id);
			}
			closeDialog();
		}).fail(function(jqXHR, textStatus, errorThrown) {
			if (jqXHR.status==409) {
				$("name-dialog .msg").html("同名文件已经存在");
			}
		});
	}
}

function rename(id) {
	$("#name-dialog").data("file", id);
	$("#name-dialog input.name").val(files[0].name);
	showDialog("name-dialog");
}

function remove(id) {
	$.post("/service/file/moveToTrash",
			{
				"id":id
			},
			function() {
				$("#listing ul.files li.checked").slideUp('fast');
				if (currentEntity.type=="s:file") {
					parent();
				}
				showMessage("文件已经移除到回收站");
			}
	);
}

function premove() {
	
}


var lastRemoved = [];

function recoverLastRemoved() {
	
	while(lastRemoved.length>0) {
		var id = lastRemoved.pop();
		$.post("/service/entity/recover", {
					"id": id
				}, 
				function() {
					
				});
	}
	if (lastRemoved.length>0) {
		for ( var i = 0; i < lastRemoved.length; i++) {
			
		}
	}
}


function sort(list, sortby) {
	if (sortby==null) {
		list.sort(function(a, b) {
			if (a.type==b.type) return 0;
			if (a.type=="s:folder") {
				return -1; 
			} else {
				return 1;
			}
		});
	} 
}


function listStared() {
	$("#listing ul.files li.item").remove();
	$("#fpath li.crumb").remove();
	
	$.getJSON("/service/keep/list", {}, function(list) {
		for ( var i = 0; i < list.length; i++) {
			var entity = list[i];
			var cloned = $("li.file-template").clone();
			renderFileItem(cloned, entity);
			$("#listing ul.files").append(cloned);
		}
	});
}

var currentSortBy = null;

function listFolder(id,msg) {
	$("#listing").show();
	$("#listing ul.files").show();
	$("#file-detail").hide();
	showMessage("正在刷新列表");
	$.getJSON("/service/repository/entity/list", {
		"skip": 0, 
		"limit": 100,
		"id": id
	}, function(result) {
		if (msg) {
			showMessage(msg, 3000);
		} else {
			hideMessage();
		}
		currentEntity = result;
		if (result.currentUser!=null) {
			currentUser = result.currentUser; 
		}
		//路径区增加下级路径
		addCrumb(result.name, result._id, result.root);
		
		//当前目录为根时隐藏返回上层目录按钮
		if ($("#browse-location li.crumb").length<=1) {
			$("#upper-folder").hide();
		} else {
			$("#upper-folder").show();
		}
		var list = result.list;
		$("#listing ul.files li.item").remove();
		
		//sort(list,  currentSortBy);
		for ( var i = 0; i < list.length; i++) {
			var entity = list[i];
			var cloned = $("li.file-template").clone();
			renderFileItem(cloned, entity);
			if (entity.type=="s:folder") {
				$("#listing ul.files").prepend(cloned);
			} else {
				$("#listing ul.files").append(cloned);
			}
		}
	});
}

function renderFileItem(cloned, entity) {
	var detailWidth= $(window).width()-510;
	cloned.removeClass("file-template");
	cloned.addClass("item");
	cloned.find("div.detail").css("width",detailWidth);
	cloned.data("entity", entity);
	cloned.find(".name").html(entity.name);

	//修改者及时间
	if (entity.modified==entity.created) {
		cloned.find(".time").html(Utils.formatTime(entity.created));
		cloned.find(".author").html(entity.creator);
	} else {
		cloned.find(".time").html(Utils.formatTime(entity.modified));
		cloned.find(".author").html(entity.modifier);
	}
	
	//标签
	if (entity.faceted && entity.faceted.length>0) {
		var tg = $("<span>|标签：</span>");
		cloned.find(".time").after(tg);
		for ( var f = 0; f < entity.faceted.length; f++) {
			var span = $("<span>" +  entity.faceted[f] + "</span>");
			span.addClass("faceted");
			tg.after(span);
		}
	}
	//图标
	if (entity.type=="s:folder") {
		cloned.find(".ext").addClass("fd");
	} else {
		cloned.find(".ext").addClass(entity.ext);
	}
	if (entity.icon!=null) {
		cloned.find(".ext").css("background", "none");
		cloned.find(".ext img").attr("src", "/service/file/image?id=" + entity.icon);
	}
	
	cloned.find(".at a").hide();
	
	if (entity.oname!=null) { //回收站中列表
		cloned.find(".at a.tbin").show();
		cloned.find(".name").html(entity.oname);
		
		cloned.find("a.recover").click(function() {
			recoverTrashItem($(this).parentData("entity"));
			$(this).parentsUntil("ul.files", "li.item").addClass("checked").slideUp();
			//$(this).parentsUntil("ul.files", "li.item").slideUp();
		});
		
		cloned.find("a.clean").click(function() {
			removeTrashItem($(this).parentData("entity"));
			$(this).parentsUntil("ul.files", "li.item").addClass("checked").slideUp();
			//$(this).parentsUntil("ul.files", "li.item").slideUp();
		});
		return;
	} else {
		if (entity.owner==currentUser)  {
			cloned.find(".at a.list").addClass("toggled").slice(0,3).removeClass("toggled").show();
		} else {
			cloned.find(".at a.list").not(".mine").addClass("toggled").slice(0,3).removeClass("toggled").show();
		}
		
		cloned.hover(function() {
			$(this).find("a.toggled").show();
		}, function() {
			$(this).find("a.toggled").hide();
		});
	}
	
	//是否被共享
	if ((entity.reader && entity.reader.length>0) || (entity.editor && entity.editor.length>0) 
			|| (entity.coordinator && entity.coordinator.length>0) ) {
		var span = $("<span>共享</span>");
		span.addClass("pt");
		cloned.find(".name").after(span);
		
		//cloned.addClass("shared");
		//cloned.find("span.slot").addClass("shared");
	}
	
	//绑定不同的事件
	if (entity.type=="s:folder") {
		cloned.find(".static").html(entity.fit + '个子文件,' + entity.fot + '个子目录');
		attachEvent(cloned.find(".name"), function(t) {
			var data = $(t).parentData("entity");
			listFolder(data._id);
		});
	} else {
		cloned.find(".static").html(Utils.formatFileSize(entity.size));
		cloned.find("a.package").remove();
		cloned.find(".name").click(function() {
			var entity = $(this).parentData("entity");
			viewFileDetail(entity);
		});
	}
	
	if(entity.keep!=null && entity.keep.indexOf(currentUser)>-1) {
		cloned.find("a.keep em").removeClass("unstar").addClass("stared");
	}
	
	cloned.find("a.share").click(function(t) {
		markContainer($(this));
		share();
	});
	
	cloned.find("a.tag").click(function(t) {
		markContainer($(this));
		faceted();
	});
	
	cloned.find("a.move").click(function() {
		var entity = $(this).parentData("entity");
		move(entity._id);
	});
	cloned.find("a.copy").click(function(t) {
		var entity = $(this).parentData("entity");
		copy(entity._id);
	});
	cloned.find("a.rename").click(function(t) {
		var entity = $(this).parentData("entity");
		rename(entity._id);
	});
	cloned.find("a.trash").click(function() {
		var entity = $(this).parentData("entity");
		remove(entity._id);
	});
	
	cloned.find("a.keep").click(function() {
		toggleKeep($(this).parentsUntil("ul.files", "li.item"));
	});
	
	function markContainer(t) {
		$(t).parentsUntil("ul.files", "li.item").addClass("checked");
	}
}

function viewFileDetail(entity) {
	$("div.head div.oper").hide();
	
	if (currentEntity._id != entity._id) {
		addCrumb(entity.name, entity._id, false);
		currentEntity = entity;
	}
	$("#head .oper").hide();
	$("#listing ul.files").hide();
	$("#file-detail").show();
	
	$("#file-detail").data("entity", entity);
	$("#file-detail .name").html(entity.name);
	$("#file-detail .created").html(Utils.formatTime(entity.created));
	$("#file-detail .modified").html(Utils.formatTime(entity.modified));
	$("#file-detail .size").html(Utils.formatFileSize(entity.size));
	$("#file-detail .author").html(entity.creator);
	$("#file-detail .owner").html(entity.owner);
     cleanPreview();
     if (entity.tn) {
    	 $('#preview-img').attr("src", "/service/file/image?id=" + entity._id);
    	 $("#no-preview").hide();
     } else {
    	 $('#preview-img').attr("src", "../img/spacer.gif");
    	 $("#no-preview").show();
    	 $("#preview-progress").html("");
     }
     
	if (entity.faceted) {
		$("#file-detail .tags").html(entity.faceted.join(","));
	} else {
		$("#file-detail .tags").html("无");
	}
	
	$("#file-detail ul.links li").remove();
	if (entity.url) {
		var li = $("<li><a>本地下载</a></li>");
		li.find("a").attr("href", "/service/file/download?id=" + currentEntity._id);
		$("#file-detail ul.links").append(li);
	}
	
	if (entity.pans) {
		for ( var i = 0; i < entity.pans.length; i++) {
			var  v =  "云盘下载";
			if (entity.pans[i].indexOf("yunpan.cn")>-1) {
				v = "360云盘下载链接";
			} else if (entity.pans[i].indexOf("pan.baidu.com")>-1) {
				v = "百度网盘下载链接";
			}
			var li = $("<li><a target='_blank'>"  + v + "</a></li>");
			li.find("a").attr("href", entity.pans[i]);
			$("#file-detail ul.links").append(li);
		}
	}
}

function removePreview() {
	$.post("/service/file/thumbnail/remove", {
		id: currentEntity._id
	}, function() {
		 $('#preview-img').attr("src", "../img/spacer.gif");
    	 $("#no-preview").show();
    	 $("#preview-progress").html("");
	});
}

function cleanPreview() {
	var crop = $('#preview-img').data('cropbox');
     if (crop!=null) {
    	 crop.remove();
     }
     $("#file-detail article.thumb").removeAttr("style");
}

function translateFileItem(li, file) {
	li.data("entity", file);
	li.find("span.name").html(file.name);
	
	if (file.faceted!=null) {
		for (key in file.faceted) {
			li.find("span.name").after("<span class='face'>" + file.faceted[key] + "</span>");
		}
	}
	li.find("div.fs").html(file.creator + "<br>" + Utils.formatTime(file.created) + "创建");
	
	if (file.acl) {
		li.find("p.ico span.slot").addClass("shared");
	}
	
	if (file.type=="s:folder") {
		li.find("p.ico").addClass("fd");
		li.find("div.fm").html(Utils.formatFileSize(file.size) +  "<br>"
				+  ((file.fot!=null)?(file.fot + "子文件夹，" + file.fit + "子文件") : ""));
		
		attachEvent(li.find("span.name"), function(t) {
			var data = $(t).parent().parent().parent().data("entity");
			listFolder(data._id);
		});
	} else {
		li.find("div.fm").html(Utils.formatFileSize(file.size));
		li.find("p.ico").addClass(file.ext);
		attachEvent(li.find("span.name"), function(t) {
			var data = $(t).parent().parent().parent().data("entity");
			window.open("/service/file/download?id=" + data._id);
		});
	}
}
	
	
function refreshFolder(msg) {
	if (currentEntity.type=="s:folder") {
		listFolder(currentEntity._id, msg);
	} else {
		listFolder(currentEntity.pid, msg);
	}
}

function parent() {
	listFolder(currentEntity.pid);
}

function listFolderByPath(file) {
	$(".base .center>div").hide();
	$("#listing").show();
	$("#fpath li.crumb").remove();
	listFolder(file._id);
}

function selectAll(t) {
	if ($("#listing ul.files li.item.checked").length==$("#listing ul.files li.item").length) {
		$("#listing ul.files li.item").removeClass("checked");
		$("#listing ul.files li.title").removeClass("checked");
	} else {
		$("#listing ul.files li.item").addClass("checked");
		$("#listing ul.files li.title").addClass("checked");
	}
	onFileChecked();
}

function toggleFileChecked(c) {
	if ($(c).parent().hasClass("checked")) {
		$(c).parent().removeClass("checked");
	} else {
		$(c).parent().addClass("checked");
	}
	onFileChecked();
}

function getCheckedFiles() {
	var list = [];
	$("#listing ul.files li.checked").each(function() {
		if ($(this).data("entity")!=null) {
			list.push($(this).data("entity"));
		}
	});
	if (list.length==0 && currentEntity!=null) {
		return [currentEntity];
	}
	return list;
}

function getCheckedOneFile() {
	var files = getCheckedFiles();
	if (files.length==0) return null;
	 return files[0];
}

function unCheckFiles() {
	$("#listing ul.files li.checked").removeClass("checked");
}

function addCrumb(name, id, isRoot) {
	if ($("#fpath>li.crumb:last-child").data("eid")==id) return; //for refresh path
	if ($("#fpath>li.crumb:nth-last-child(2)").data("eid")==id) { // for nav to parent
		$("#fpath>li.crumb:last-child").remove();
		return;
	};
	
	var li =  $('<li class="crumb folder"><a href="javascript:void(0)" class=""><img src="../img/crumb.svg" ><span></span></a></li>');
	li.find("span").html(Utils.shortenString(name,10));
	li.data("eid", id);
	
	$("#fpath li").removeClass("current");
	li.addClass("current");
	
	attachEvent(li, function(currentLi) {
		listFolder($(currentLi).data("eid"));
		$(currentLi).nextAll().remove();
	});
	
	if (isRoot) {
		$("#fpath li.crumb").remove();
		li.addClass("home");
		li.find("img").attr("src", "../img/home.svg");
	}
	$("#fpath").append(li);
}

function openRepo(repoData) {
	$(".base .center>div").hide();
	$("#listing").show();
	if (repoData==null) {
		listFolder(null);
	} else {
		listFolder(repoData.root);
	}
}



function pagePublic() {
	clear();
	$("#repo-list").show();
	
	$.getJSON("/service/repository/public/list", null, function(repoList){
		$("#my-repo .cloned").remove();
		
		for ( var i = 0; i < repoList.length; i++) {
			$("#repo-list li.snapshot").remove();
			for ( var i = 0; i < repoList.length; i++) {
				var repo = repoList[i];
				$("#repo-list").append(fileSnapshot(repo.root));
			}
		}
	});
}
function pageMyRepo() {
	clear();
	listFolder(null);
	$("div.head .oper.file").show();
}

function pageStared() {
	clear();
	$("#listing").show();
	listFolder("keep");
}

function pageTrash() {
	$("#listing").show();
	listFolder("trash");
	$("div.head .oper.trash").show();
}

function pageShares() {
	clear();
	$("#listing").show();
	listFolder("shared");
	
	//$("#share-list").show();
	/*
	$.getJSON("/service/share/mine", null, function(list) {
		$("#my-share li.snapshot").remove();
		for ( var i = 0; i < list.length; i++) {
			var file = list[i];
			$("#my-share").append(fileSnapshot(file));
		}
	});
	*/
	/*
	$.getJSON("/service/share/received", null, function(list) {
		$("#share-me li.snapshot").remove();
		for ( var i = 0; i < list.length; i++) {
			var file = list[i];
			$("#share-me").append(fileSnapshot(file));
		}
	});
	*/
}


function fileSnapshot(file) {
	var cloned = $("li.share.template").clone();
	cloned.removeClass("template").addClass("snapshot");
	cloned.find(".header span").html(file.name);
	
	if (file.type=="s:folder") {
		cloned.find(".header img").addClass("folder");
		cloned.find(".enter").html("进入文件夹");
		for ( var j = 0; j < file.list.length&&j<4; j++) {
			var subfile = file.list[j];
			var cloned_child = $("li.template.sharesub").clone();
			cloned_child.removeClass("template");
			cloned_child.find("span").html(subfile.name);
			
			if (subfile.type=="s:folder") {
				cloned_child.find("img").addClass("folder");
			} else {
				cloned_child.find("img").addClass(subfile.ext);
			}
			cloned.find("div.list ul").append(cloned_child);
		}
		
		if (file.list.length>4) {
			//cloned.find("div.list ul").append("<li>还有" + (file.list.length-4) +"个文件</li>");
		}  else if (file.list.length==0) {
			cloned.find("div.list ul").append("<li>没有文件</li>");
		} 
	} else {
		cloned.find(".enter").html("下载文件");
		cloned.find(".header img").addClass(file.ext);
		cloned.find("div.list ul").append("<li></li>");
	}
	cloned.find("span.time").html(file.owner);
	/*
	if (file.owner==currentUser.name) {
		cloned.find(".owner").html(file.permissions.length);
	} else {
	}
	*/
	cloned.find(".enter").data("file", file);
	attachEvent(cloned.find(".enter"), function(e) {
		var file = $(e).data('file');
		listFolderByPath(file);
	}) ;
	return cloned;
}

function uploadThumbnails() {
	  var crop = $('#preview-img').data('cropbox');
	  if (crop!=null) {
		  $.post("/service/file/thumbnail/crop", {
			  "eid": currentEntity._id,
			  "id":   $("#preview-img").data("entity")._id,
			  "x" : crop.result.cropX,
			  "y": crop.result.cropY,
			  "w" : crop.result.cropW,
			  "h": crop.result.cropH,
			  "zw": 320,
			  "zh": 256
		  }, function(id) {
			  cleanPreview();
			  $('#preview-img').attr("src", "/service/file/image?id=" + id);
			  $("#no-preview").hide();
		  });
		  /*
		  $.post("/service/file/thumbnail", {
			  "id": currentEntity._id,
			  "width": 320,
			  "height": 260,
			  "url": crop.getDataURL()
		  }, function(data) {
			  cleanPreview();
			  $.get("/service/file/thumbnail", {
				 "thumb":  data 
			  }, function(base64) {
				  $('#preview-img').attr("src", base64);
			  });
		  });
		  */
	  }
}

function initUploader() {
	var imgUploader = new plupload.Uploader({
		runtimes : 'html5',
		browse_button : 'btn-preview-upload',
		max_file_size : '1mb',
		multi_selection: false,
		filters : [
		            {title : "图片文件", extensions : "jpg,png"},
		        ],
		url : '/service/file/thumbnail/attach'
	});
	
	imgUploader.init();
	imgUploader.bind("FilesAdded", function(up, files) {
		$("#preview-progress").html("准备上传...");
		var param = {};
		param.id = currentEntity._id;
		up.settings.multipart_params = param; 
		up.start();
	});
	
	imgUploader.bind('UploadProgress', function(up, file) {
		$("#preview-progress").html("已传输"  + file.percent + "%");
	 });
	
	imgUploader.bind('FileUploaded', function(up, file, tn) {
		 $('#preview-img').attr("src", "/service/file/image?id=" + tn.response);
	     $("#file-detail article.thumb").css("background", 'none');
		 $("#no-preview").hide();
	});
	
	var uploader = new plupload.Uploader({
		runtimes : 'html5,html4',
		browse_button : 'btn-file-upload',
		max_file_size : '5mb',
		url : '/service/file/upload'
	});
	uploader.init();
	
	uploader.bind('FilesAdded', function(up, files) {  // fire when file is added to queue
		listUploading();
		for ( var i = 0; i < files.length; i++) {
			var file = files[i];
			var newli = $("#upload-list li.item.template").clone();
			newli.removeClass("template");
			newli.attr("id", file.id);
			newli.show();
			
			newli.find(".name").html(file.name);
			newli.find(".icon").addClass(Utils.getFileExt(file.name));
			newli.find(".size").html(plupload.formatSize(file.size) );
			newli.find(".status").html("等待传输中");
			
			newli.find("a.filename").attr("title", file.name);
			newli.find("p.btns").html("大小:<i>" + plupload.formatSize(file.size)  + "</i>, 等待传输中...");
			
			var endfix = file.name.substring(file.name.lastIndexOf(".")+1);
			newli.find("p.ico").addClass(endfix);
			
			var param = {};
			param.id = currentEntity._id;
			param.name = file.name;
			newli.data("param", param);
			up.settings.multipart_params = param; 
			$("#upload-list").append(newli);
		}
		up.start();
		closeDialog();
	});

	uploader.bind('UploadFile', function(up, file) { //before file upload start
		var newli = $("#" + file.id);
		up.settings.multipart_params = newli.data("param"); 
	});
	 uploader.bind('UploadProgress', function(up, file) { // on file upload progress
		 var newli = $("#" + file.id);
		 newli.find(".status").html(file.percent + "%");
		 newli.find(".progress").css("width", file.percent + "%");
		 
		 //newli.find("p.btns").html("大小:<i>" + plupload.formatSize(file.size)  + "</i>,已传输<i>" +  file.percent + "%</i>");
		 /*
		 newli.find(".text").html(Utils.shortenString(file.name, 20) + "(" + plupload.formatSize(file.size)  + ")" + "<br><span>"
				+ plupload.formatSize(up.total.bytesPerSec) + "/s  </span>");
		 newli.find(".percent").css("width", file.percent + "%");
		 $("#progress-bar .message").html("正在上传文件" + $("#progress-bar div.detail ul li.finished").length + "/" + $("#progress-bar div.detail ul li.progress").length);
		 */
	 });
		
	 uploader.bind('Error', function(up, err) {  // on file upload error
		 if (err.status==409) {
			 showAlert("存在同名文件");
			 var newli = $("#" + err.file.id);
			 newli.find(".status").html("<font color='red'>文件名称冲突</font>");
		 }
	 });

	 uploader.bind('FileUploaded', function(up, file, info) {  //on file upload finished
		 var newli = $("#" + file.id);
		 newli.find(".status").html("已传输完成");
		 
		 //newli.find("p.btns").html("大小:<i>" + plupload.formatSize(file.size)  + "</i>,已传输完成");
		 
		 refreshFolder();
		 //newli.addClass("finished");
	 });
}

function goYunshare() {
	$.getJSON("/service/file/yun", {
		url: $("#url-yun-share").val()
	}, function(data) {
		$("#yun-file-detail").slideDown('fast');
		$("#yun-file-url").val($("#url-yun-share").val());
		
		if (data.title==null) {
			$("#yun-file-detail .filter-title").html("地址解析失败，请手动填写文件基本信息");
		} else {
			$("#yun-file-detail .filter-title").html("地址解析完成");
			$("#yun-file-url").val($("#url-yun-share").val());
			$("#yun-file-name").val(data.title);
			
			if (data.size) {
				$("#yun-file-size").val(Utils.formatFileSize(data.size));
				$("#yun-file-size").data("size", data.size);
			}
		}
	});
}

function confirmYunAdd() {
	var size = "0";
	if ($("#yun-file-size").data("size")!=null) {
		size = $("#yun-file-size").data("size");
	}
	if (currentEntity.type=="s:file") {
		$.post("/service/file/attach", {
			"id": currentEntity._id,
			"url": $("#yun-file-url").val(),
			"name": $("#yun-file-name").val(),
			"size": size
		}, function(data) {
			closeDialog();
			currentEntity = JSON.parse(data);
			viewFileDetail(currentEntity);
		});
	} else {
		if ($("#yun-file-url").val()=="" || $("#yun-file-name").val() == "") {
			showAlert("文件地址和文件名称不能为空");
			return;
		} 
		
		$.post("/service/file/yun", {
			"id": currentEntity._id,
			"url": $("#yun-file-url").val(),
			"name": $("#yun-file-name").val(),
			"size": size
		}, function() {
			closeDialog();
			refreshFolder();
		});
	}
}

function pageFaceted() {
	$(".base .center>div").hide();
	$("#faceted-view").show();
	
	$("dl.faceted-filter dd div").removeClass("current");
	$("dl.faceted-filter dd div.default").addClass("current");
	
	$.getJSON("/service/faceted/list", null, function(list) {
		$("#faceted-view dd div.added").remove();
		
		for ( var i = 0; i < list.length; i++) {
			var name = list[i];
			var tag = "文件标签:";
			var pos = name.indexOf(":"); 
			if (pos>-1) {
				tag = name.substring(0, pos+1);
				name = name.substring(pos+1);
			}
			
			if ($("#faceted-view dl.faceted-filter dt:contains('" + tag + "')").length==0) {
				var dl = $("#faceted-view dl:last").clone();
				dl.find("dt").html(tag);
				dl.find("dd div.added").remove();
				$("#faceted-view dl.faceted-filter:last").after(dl);
			}
			
			var div = $("<div>" + name + "</div>");
			div.attr("filter", "facet=" + name);
			div.addClass("added");
			$("#faceted-view dl.faceted-filter dt:contains('" + tag + "')").next().append(div);
		}
		
		attachEvent($("dl.faceted-filter dd div"), function(t) {
			var filters = [];
			$("dl.faceted-filter dd div.current").each(function() {
				if ($(this).attr("filter")!=null) {
					filters.push($(this).attr("filter"));
				}
			});
			
			$.getJSON("/service/file/filter", {
				"filter": filters.join(';')
			}, function(list) {
				$("#faceted-view .files li.item").remove();
				
				for ( var i = 0; i < list.length; i++) {
					var cloned = $("#faceted-view .files li.template").clone();
					cloned.removeClass("template").addClass("item");
					
					translateFileItem(cloned, list[i]);
					$("#faceted-view .files").append(cloned);
				}
			});
		});
	});
}


function faceted(entity) {
	if (entity==null) {
		entity = $("#file-detail").data("entity");
	}
	$("#faceted-dialog ul.faceted-list li").remove();
	$("#faceted-dialog").data("entity", entity);
	if (entity.faceted) {
		for ( var i = 0; i < entity.faceted.length; i++) {
			if (entity.faceted[i]=="") continue;
			addFacetedLi(entity.faceted[i]);
		}
	}
	
	$("#new-faceted-name").keydown(function(key) {
		if ($(this).val()=='') return;
		if (key.keyCode==13) {
			onAddFaceted();
		}
	});
	showDialog("faceted-dialog");
}

function addFacetedLi(title) {
	var li = $("<li>" + title +"<img src='../img/spacer.gif'></li>");
	li.data("title", title);
	li.hide();
	$("#faceted-dialog ul.faceted-list").append(li);
	li.slideDown('fast');
	$("#new-faceted-name").val('');

	li.click(function() {
		$(this).slideUp('fast', function() {
			$(this).remove();
		});
	});
}
function onAddFaceted() {
	addFacetedLi($("#new-faceted-name").val());
}

function newFaceted() {
	showDialog("new-faceted");
	$("#new-faceted input").val('');
}

function confirmFacetedSet() {
	var entity = $("#faceted-dialog").data("entity");
	var faceteds = [];
	
	$("#faceted-dialog ul.faceted-list li").each(function() {
		faceteds.push($(this).data("title"));
	}) ;
	
	$.post("/service/faceted/set", {
		"id": entity._id,
		"list": JSON.stringify(faceteds)
	}, function() {
		closeDialog();
		refreshFolder("已为" +files.length + "个文件设置标签" );
	});
}


function confirmAddFaceted() {
	var val =  $("#newFacetedField").val();
	
	if (faceted=="") {
		showAlert("请输入标签名称", newFaceted());
		return;
	}
	
	var parent = $("#facetedParentField").val();
	
	$.post("/service/faceted/add", {
		"parent": parent,
		"title": val
	}, function(data) {
		faceted();
	});
}

function share(files) {
	hideDropDown();
	if (files==null) {
		files = getCheckedFiles();
	}
	if (files.length==0) return;
	$("#share-dialog").data("files", files);
	$("#share-dialog").data("currentTab", null);
	$("#share-dialog").find(".title span").html("分享" + ((files[0].type=="s:folder")?"目录":"文件") + "<em>" +  $(files[0].name).short(25) + "</em>") ;
		
	$("#sharedTo a").remove();
	if (files.length==1) {
		var users = files[0].reader;
		if (users!=null) {
			for ( var i = 0; i < users.length; i++) {
				addUserToCurrentRole(users[i]);
			}
		}
	}
	
	$("#shareToFilter").keyup(function(key) {
		var v = $.trim($(this).val());
		if (v == '') return;
		if (key.keyCode==32||key.keyCode==44) {
			addUserToCurrentRole($.trim($(this).val()));
			$(this).val('');
		}
		if (key.keyCode==13) {
			addUserToCurrentRole($(this).val());
			$(this).val('');
		}
	});
	$("#shareToFilter").blur(function() {
		if ($(this).val()=='') return;
		addUserToCurrentRole($(this).val());
		$(this).val('');
	});
	showDialog("share-dialog");
}

function publish() {
	$.post("/service/publish", {
		"id": currentEntity._id,
	}, function() {
		showMessage("文件发布完成");
	}).fail(function() {
		showMessage("文件发布失败！");
	});
}

function switchInherit() {
	if ($("#share-inherit").hasClass("checked")) {
		$("#share-inherit").removeClass("checked");
		$("#shared-usr ul.inherited li.item").hide();
	} else {
		$("#share-inherit").addClass("checked");
		$("#shared-usr ul.inherited li.item").show();
	}
}

function tabShareRole(role) {
	$("#share-tab li").removeClass("current");
	$("#share-tab li." + role).addClass("current");
	
	//发生切换应该页面缓存已经选定的用户
	shareSaveRoleUsers();
	
	$("#sharedTo a").remove();
	$("#share-dialog").data("currentTab", role);
	
	//获取已经缓存的用户列表
	var users = $("#share-dialog").data(role);
	for ( var i = 0; i < users.length; i++) {
		addUserToCurrentRole(users[i]);
	}
}

function shareSaveRoleUsers() {
	if( $("#share-dialog").data("currentTab")!=null) { 
		var list = []; 
		//获取选定的用户列表
		$("#sharedTo a").each(function() {
			list.push($(this).find("span.name").html());
		});
		//储存
		$("#share-dialog").data($("#share-dialog").data("currentTab"), list);
	}
}

//增加一个用户到某个权限
function addUserToCurrentRole(user) {
	//首先判断是否已经添加
	var list = [];
	$("#sharedTo a").each(function() {
		list.push($(this).find("span.name").html());
	});
	if (list.indexOf(user)>-1) return;
	
	var a = $('<a class="user"><span class="name">' + user + '</span></a>');
	a.attachEvent(function(t) {
		$(t).remove();
	});
	$("#shareToFilter").before(a);
}

function confirmShare() {
	var list = []; 
	//获取选定的用户列表
	$("#sharedTo a").each(function() {
		list.push($(this).find("span.name").html());
	});
	var postData = {
			"Reader": JSON.stringify(list),
			"Editor": "[]",
			"Coordinator": "[]",
			"inherit": true,
			"id":[$("#share-dialog").data("files")[0]._id]
	};
	$.post("/service/share/update", postData, function() {
		closeDialog();
		refreshFolder("内容共享成功");
	});
}

function cancelShare() {
	if ($("#share-dialog").data("files")[0].owner!=currentUser) return;
		
	var postData = {
			"Reader": "[]",
			"Editor": "[]",
			"Coordinator": "[]",
			"inherit": true,
			"id":[$("#share-dialog").data("files")[0]._id]
	};
	$.post("/service/share/update", postData, function() {
		closeDialog();
		refreshFolder("取消共享成功");
	});
}

function toggleKeep(li) {
	var entity = li.data("entity");
	var  kp = true;
	if (li.find("a.keep em").hasClass("stared")) {
		kp = false;
	}
	$.post("/service/keep/set", {
		"id": entity._id,
		"set": kp
	}, function() {
		if (kp) {
			li.find("a.keep em").removeClass("unstar").addClass("stared");
		} else {
			li.find("a.keep em").removeClass("stared").addClass("unstar");
		}
	});
}

function showMessage(m, h) {
	if (typeof m === 'string') {
		$("#msg").html(m);
	} else {
		$("#msg").html("");
		$("#msg").append(m);
	}

	$("#msg").show();
	if (h) {
		setTimeout("hideMessage()", h);
	}
}

function hideMessage() {
	hideDialog();
	$("#msg").slideUp();
}

function newRepo() {
	clear();
	$("#repo-edit-view").show();
	
	$("#repo-edit-view .shadow-box").hide();
	$("#repo-edit-view .new").show();
}

function confirmRepoName() {
	$.post("/service/repo/add", {
		'title': $("repo-name").val(),
		'desc': $("repo-desc").val()
	}, function() {
		pagePublic();
	});
}
