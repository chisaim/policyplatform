$(function(){
	//点击注销按钮事件
	$("#loginout").bind('click',function(){
		$.ajax({
			type: 'POST',
			url: '/policyplatform/user/userLoginOut',
			async: false,
			success: function (data) {
				if(data.code == 200){
					var myDate=new Date();    
		            myDate.setTime(-1000);//设置时间    
		            document.cookie="token"+"=''; expires="+myDate.toGMTString();
					location.href = '/policyplatform/login.html';
				}else {
					msgShow("执行错误",data.message,"error");
				}
			},
			error: function () {
				msgShow("连接错误","未连通服务器！","error");
			}
		});
	});
	
	//dialog模板初始化
	$("#changePwdDialog").dialog({
	    title: '用户信息',
	    width: 240,
	    height: 200,
	    closed: true,
	    cache: false,
	    buttons:[{id:'changePwdSave',iconCls:'icon-save',text:'Save'},
	             {id:'cancelChange',iconCls:'icon-cancel',text:'Cancle'}],
	    content:'<div id="changePwdContent"></div>'
	});
	$("#changePwdDialog #changePwdContent").append("</br><a style='margin-left:5px;margin-top:5px;color:black;'>密保问题：</a><input type='text' name='security_question' style='width: 140px;border-color: red;'>");
	$("#changePwdDialog #changePwdContent").append("</br></br><a style='margin-left:5px;margin-top:5px;color:black;'>密保答案：</a><input type='text' name='security_answer' style='width: 140px;border-color: red;'>");
	$("#changePwdDialog #changePwdContent").append("</br></br><a style='margin-left:5px;margin-top:5px;color:black;'>新密码：</a><input type='text' name='new_pwd' style='width: 140px;border-color: red;'>");
	$.parser.parse($("#changePwdDialog #changePwdContent"));
	
	$("#changePwd").bind('click',function(){
		initChangePwdQ();
	});
	
	$("#cancelChange").bind('click',function(){
		$("#changePwdDialog input[name=security_answer]").val("");
		$("#changePwdDialog input[name=new_pwd]").val("");
		$("#changePwdDialog").panel('close',true);
	});
})

function initChangePwdQ(){
	//设置密保问题
	$.ajax({
		type: 'POST',
		url: '/policyplatform/user/userGetSecurityQ',
		async: false,
		success: function (data) {
			if(data.code == 200){
				$("#changePwdDialog input[name=security_question]").val(data.data);
				$('#changePwdDialog input[name=security_question]').attr("readonly","readonly");
				$('#changePwdSave').bind('click',function(){
					var security_answer = $("#changePwdDialog input[name=security_answer]").val();
					var pass_word = $("#changePwdDialog input[name=new_pwd]").val();
					if(pass_word==""){
						msgShow("执行错误","密码不能为空！","error");
					}else{
						var result = {
								"security_answer":security_answer,
								"pass_word":pass_word
							};
						$.ajax({
								type: 'POST',
								url: '/policyplatform/user/changePwd',
								contentType: "application/json; charset=utf-8",
								async: false,
								data:JSON.stringify(result),
								success: function (data) {
									if(data.code == 200){
										msgShow("执行信息",data.message,"info");
									}else {
										msgShow("执行错误",data.message,"error");
									}
									$("#changePwdDialog").panel('close',true);
								},
								error: function () {
									msgShow("连接错误","未连通服务器！","error");
									$("#changePwdDialog").panel('close',true);
								}
						});
					}
				});
				$("#changePwdDialog").panel('open',true);
			}else {
				msgShow("执行错误",data.message,"error");
			}
		},
		error: function () {
			msgShow("连接错误","未连通服务器！","error");
		}
	});
}