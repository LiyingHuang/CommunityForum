$(function(){
	$("#publishBtn").click(publish);
});

// f8:execute all  f10:one step  f11:go into function
function publish() {
	$("#publishModal").modal("hide");
	$("#hintModal").modal("show");
	setTimeout(function(){
		$("#hintModal").modal("hide");
	}, 2000);
}