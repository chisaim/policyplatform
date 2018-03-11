$(function(){
	$('#password').keyup(function (event) {
		if (event.keyCode == "13") {
			$("#login").trigger("click");
			return false;
		}
	});

	$("#login").on("click", function () {
		submitForm();
	});

	function submitForm() {
		if (navigator.appName == "Microsoft Internet Explorer" &&
			(navigator.appVersion.split(";")[1].replace(/[ ]/g, "") == "MSIE6.0" ||
				navigator.appVersion.split(";")[1].replace(/[ ]/g, "") == "MSIE7.0" ||
				navigator.appVersion.split(";")[1].replace(/[ ]/g, "") == "MSIE8.0")
			) {
			alert("您的浏览器版本过低，请使用360安全浏览器的极速模式或IE9.0以上版本的浏览器");
	} else {
		if($('#username').val()==""||$('#password').val()==""){
			alert("户名密码不能为空");
		}else{
			var formData = {
				userName: $('#username').val(),
				passWord: $('#password').val(),
			};
			var isLogining = false;//防止重复点击
			if(!isLogining){
				$.ajax({
					type: 'POST',
					url: '/policyplatform/user/userLogin',
					contentType: "application/json; charset=utf-8",
					data: JSON.stringify(formData),
					async: true,
					beforeSend: function() {
						isLogining = true
		            },
					success: function (data) {
						if (data.code == 200) {
							document.cookie="token="+data.data.token;
							location.href = '/policyplatform/index.html';
						} else {
							alert(data.message);
						}
					},
					error: function () {
						isLogining = false;
						alert("服务器连接错误！");
					}
				});
			}else{
				alert("请求数据中！");
			}
		}
	}
}

$("#reset").on("click", function () {
	$("#username").val("");
	$("#password").val("");
});
});