$(function(){
	$("#publishBtn").click(publish);
});

// f8:execute all  f10:one step  f11:go into function
function publish() {
	$("#publishModal").modal("hide");

	// get the title and content
	var title = $("#recipient-name").val();
	var content = $("#message-text").val();
	// send asynchronous request POST
	$.post(
		CONTEXT_PATH + "/discuss/add",      // controller path url
		{"title":title,"content":content},  // content
		function(data){                     // return result -- data
			data = $.parseJSON(data);
			// show the response msg in prompt box
			$("hintBody").text(data.msg);

			// show the prompt box
			$("#hintModal").modal("show");
			// auto hide the prompt box after 2s
			setTimeout(function(){
				$("#hintModal").modal("hide");
				// refresh the page if success
				if(data.code == 0){
					window.location.reload();
				}
			}, 2000);
		}
	);
}