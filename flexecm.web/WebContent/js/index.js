

function searchOrImport() {
	$.post("/service/url/contr", {
		"url": $("#searchContext").val()
	}, function(data) {
		alert("OK");
	});
}