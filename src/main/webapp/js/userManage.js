function initUser() {
	// 初始化表格
	initUserGrid('.userManage #userManageGrid');
	//初始化数据，即打开页面时刷新数据
	initUserGridData();
//*******************************************************************//
	//初始化按钮事件绑定
	initBtnQuery('.userManage #userQuery');
	initBtnAdd('.userManage #userAdd');
	initBtnEdit('.userManage #userEdit');
	initBtnDelete('.userManage #userDelete');
//*******************************************************************//
//-----------------------增加用户面板的初始化事件--------------------//
	initUserManageDialog();
//-------------------------------------------------------------------//
	initAddOrEditUser();
}

function initUserGrid(tableEleName){
	var strs= new Array(); //定义一数组
	strs=tableEleName.split(" ");
	var str = strs[0]+' #tb';
	$(tableEleName).datagrid({
		fit:true,
	fitColumns:true,// 表宽度自适应
	rownumbers:true,// 显示行号
	toolbar:str,
	pagination:false,
	columns:[[{field:'ck',checkbox:"true",width:40,align:'left'},
	{field:'user_name',title:'用户名',width:180,align:'left'},
	{field:'security_question',title:'密保问题',width:150,align:'left'},
	{field:'security_answer',title:'密保答案',width:100,align:'left'},
	{field:'email',title:'电子邮件',width:100,align:'left'},
	{field:'phone_number',title:'电话号码',width:100,align:'right'},
	{field:'status',title:'状态',width:100,align:'right',formatter : function(value){
		return value==true?"有效":"无效";
	}},
	{field:'role_name',title:'角色',width:100,align:'right'},
	{field:'organization',title:'组织',width:80,align:'left'},
	{field:'create_time',title:'创建时间',width:80,align:'left',formatter : function(value){
		return formTimestampValue(new Date(value));
    }}]]
	});
}

function initUserGridData(){
	$.ajax({
		type: 'GET',
		url: '/policyplatform/user/getUsers',
		async: false,
		success: function (data) {
			if(data.code == 200){
				$('.userManage #userManageGrid').datagrid('loadData',data.data);
			}else {
				msgShow("查询错误",data.message,"error");
			}
		},
		error: function () {
			msgShow("连接错误","未连通服务器！","error");
		}
	});
}

function initBtnQuery(btnElement){
	$(btnElement).bind('click',function(){
		var userName = $('.userManage #userName').val();
		if(""==userName){
			initUserGridData();
		}else{
			var rows = $('.userManage #userManageGrid').datagrid('getRows');
			for(var rowsindex=0;rowsindex<rows.length;rowsindex++){
				if(rows[rowsindex].user_name!=userName){
					$('.userManage #userManageGrid').datagrid('deleteRow',rowsindex);
				}
			}
		}
	});
}

function initBtnAdd(btnElement){
	$(btnElement).bind('click',function(){
		$('#userManageDialog input[name=user_name]').removeAttr("readonly");
		$('#userManageDialog').addClass("addUser");
		initUserPanel();
		$("#userManageDialog").panel('open',true);
	});
}

function initBtnEdit(btnElement){
	$(btnElement).bind('click',function(){
		initUserPanel();
		//判断是否有选中的数据
		var data = $('.userManage #userManageGrid').datagrid('getSelected');
		if(null!=data){
			$.ajax({
				type: 'GET',
				url: '/policyplatform/user/getUserInfo?user_id='+data.id,
				async: false,
				success: function (data) {
					if(data.code == 200){
						//数据写入面板
						for(var prop in data.data){
							$('#userManageDialog input[name='+prop+']').val(data.data[prop]);
						}
						//combobox的值无法用上面的方法设置，密码加密后是不返回的，显示密码不允许更改
						$('#userManageDialog input[name=pass_word]').val("此处不允许更改密码");
						$('#userManageDialog #genderSelect').combobox('setValue',data.data.sex);
						$('#userManageDialog #roleSelect').combobox('setValue',data.data.role_name);
						$('#userManageDialog #statusSelect').combobox('setValue',String(data.data.status));
						$('#userManageDialog #birthday').datebox('setValue',data.data.birthday);
						$('#userManageDialog input[name=user_name]').attr("readonly","readonly");
						$('#userManageDialog').removeClass("addUser");
						$("#userManageDialog").panel('open',true);
					}else {
						msgShow("查询错误",data.message,"error");
					}
				},
				error: function () {
					msgShow("连接错误","未连通服务器！","error");
				}
			});
		}
	});
}

function initBtnDelete(btnElement){
	$(btnElement).bind('click',function(){
		var row = $('.userManage #userManageGrid').datagrid('getSelected');
		if(null!=row){

			var user_id = row.id;
			user_id = {
				"user_id":user_id
			};
			$.ajax({
				type: 'POST',
				url: '/policyplatform/user/deleteUser',
				contentType: "application/json; charset=utf-8",
				async: false,
				data:JSON.stringify(user_id),
				success: function (data) {
					if(data.code == 200){
						$('.userManage #userName').val("");
						$('.userManage #userQuery').trigger("click");
					}else {
						msgShow("执行错误",data.message,"error");
					}
				},
				error: function () {
					msgShow("连接错误","未连通服务器！","error");
				}
			});
		}else{
			msgShow("操作错误","请选中数据！","error");
		}
	});
}

function userUniqueCheck(str){
	if(""!=str){
		$.ajax({
			type: 'GET',
			url: '/policyplatform/user/userUniqueCheck?user_name='+str,
			contentType: "text/plain; charset=utf-8",
			async: false,
			success: function (data) {
				if(data.code == 200){
					//用户名可用则什么都不做
				}else {
					msgShow("参数检查",data.message,"info");
				}
			},
		});
	}
}

function initUserPanel(){
	$('#userManageDialog input[name="user_name"]').val("");
	$('#userManageDialog input[name="pass_word"]').val("");
	$('#userManageDialog #statusSelect').combobox('setValue',"true");
	$('#userManageDialog input[name="email"]').val("");
	$('#userManageDialog input[name="security_question"]').val("");
	$('#userManageDialog input[name="security_answer"]').val("");
	$('#userManageDialog #organizationSelect').val("");
	$('#userManageDialog input[name="name"]').val("");
	$('#userManageDialog #genderSelect').combobox('setValue',"男");
	$('#userManageDialog input[name="nation"]').val("");
	$('#userManageDialog #birthday').datebox('setValue',"currentDate");
	$('#userManageDialog input[name="native_place"]').val("");
	$('#userManageDialog input[name="qq"]').val("");
	$('#userManageDialog input[name="phone_number"]').val("");
	$('#userManageDialog input[name="residence"]').val("");
	$('#userManageDialog input[name="province"]').val("");
	$('#userManageDialog input[name="region"]').val("");
	$('#userManageDialog #roleSelect').val("");
}

function initUserManageDialog(){
	//dialog模板初始化
	$("#userManageDialog").dialog({
	    title: '用户信息',
	    width: 470,
	    height: 320,
	    closed: true,
	    cache: false,
	    buttons:[{id:'saveUser',iconCls:'icon-save',text:'Save'},
	             {id:'cancleUser',iconCls:'icon-cancel',text:'Cancle'}],
	    content:'<div id="userManageContent"></div>'
	});
	$("#userManageDialog #userManageContent").append("</br><a style='margin-left:5px;margin-top:5px;color:black;'>用户名：</a><input type='text' name='user_name' style='width: 70px;border-color: red;' onblur='userUniqueCheck(this.value)'>");
	$("#userManageDialog #userManageContent").append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a style='margin-left:5px;margin-top:5px;color:black;'>密码：</a><input type='text' name='pass_word' style='width: 80px;border-color: red;'>");
	$("#userManageDialog #userManageContent").append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a style='margin-left:5px;margin-top:5px;color:black;'>性别：</a><input id='genderSelect' name='sex' style='width:40px;' value='男'>");
	$("#userManageDialog #userManageContent").append("</br></br><a style='margin-left:5px;margin-top:5px;color:black;'>昵称：</a><input type='text' name='name' style='width: 50px;border-color: red;'>");
	$("#userManageDialog #userManageContent").append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a style='margin-left:5px;margin-top:5px;color:black;'>密保问题：</a><input type='text' name='security_question' style='width: 70px;border-color: red;'>");
	$("#userManageDialog #userManageContent").append("&nbsp;&nbsp;&nbsp;<a style='margin-left:5px;margin-top:5px;color:black;'>密保答案：</a><input type='text' name='security_answer' style='width: 70px;border-color: red;'>");
	$("#userManageDialog #userManageContent").append("</br></br><a style='margin-left:5px;margin-top:5px;color:black;'>角色：</a><input id='roleSelect' name='role_name' style='width: 80px;border-color: red;'>");
	$("#userManageDialog #userManageContent").append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a style='margin-left:5px;margin-top:5px;color:black;'>组织：</a><input id='organizationSelect' name='organization' style='width:80px;'>");
	$("#userManageDialog #userManageContent").append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a style='margin-left:5px;margin-top:5px;color:black;'>状态：</a><input id='statusSelect' name='status' value='true' style='width: 60px;border-color: red;'>");
	$("#userManageDialog #userManageContent").append("</br></br><a style='margin-left:5px;margin-top:5px;color:black;'>电话：</a><input type='text' name='phone_number' style='width: 80px;'>");
	$("#userManageDialog #userManageContent").append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a style='margin-left:5px;margin-top:5px;color:black;'>email：</a><input type='text' name='email' style='width: 80px;'>");
	$("#userManageDialog #userManageContent").append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a style='margin-left:5px;margin-top:5px;color:black;'>QQ：</a><input type='text' name='qq' style='width: 80px;'>");
	$("#userManageDialog #userManageContent").append("</br></br><a style='margin-left:5px;margin-top:5px;color:black;'>生日：</a><input id='birthday' name='birthday' class='easyui-datebox' style='width:90px'  data-options='formatter:dataformatter,parser:dataparser' value='currentDate'>");
	$("#userManageDialog #userManageContent").append("&nbsp;&nbsp;&nbsp;&nbsp;<a style='margin-left:5px;margin-top:5px;color:black;'>居住地：</a><input type='text' name='residence' style='width: 80px;'>");
	$("#userManageDialog #userManageContent").append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a style='margin-left:5px;margin-top:5px;color:black;'>民族：</a><input type='text' name='nation' style='width: 40px;'>");
	$("#userManageDialog #userManageContent").append("</br></br><a style='margin-left:5px;margin-top:5px;color:black;'>籍贯：</a><input type='text' name='native_place' style='width: 80px;'>");
	$("#userManageDialog #userManageContent").append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a style='margin-left:5px;margin-top:5px;color:black;'>省份：</a><input type='text' name='province' style='width: 80px;'>");
	$("#userManageDialog #userManageContent").append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a style='margin-left:5px;margin-top:5px;color:black;'>区域：</a><input type='text' name='region' style='width: 80px;'>");
	//初始化性别复选框
	$('#userManageDialog #genderSelect').combobox({
		panelHeight:45,
		valueField:'sexValue',
		textField:'sexText',
		data:[{sexValue:'男',sexText:'男'},{sexValue:'女',sexText:'女'}]
	});
	//初始化状态复选框
	$('#userManageDialog #statusSelect').combobox({
		panelHeight:45,
		valueField:'Value',
		textField:'Text',
		data:[{Value:'true',Text:'有效'},{Value:'false',Text:'失效'}]
	});
	//初始化角色下拉框
	$.ajax({
		type: 'GET',
		url: '/policyplatform/user/getRoles',
		contentType: "application/json; charset=utf-8",
		async: true,
		success: function (data) {
			if (data.code == 200) {
				$('#userManageDialog #roleSelect').combobox({
					panelHeight:80,
					valueField:'id',
					textField:'name',
					data:data.data
				});
			}else{
				msgShow("执行错误",data.message,"error");
			}
		},
		error: function () {
			msgShow("连接错误","连接服务器失败","error");
		}
	});
	$.parser.parse($("#userManageDialog #userManageContent"));
}
function initAddOrEditUser(){
	$("#cancleUser").bind('click',function(){
		$("#userManageDialog").panel('close',true);
	});
	$("#saveUser").bind('click',function(){
		//按钮提交前的关键数据非空检查
		if(""==$('#userManageDialog input[name="user_name"]').val()||""==$('#userManageDialog input[name="pass_word"]').val()||
			""==$('#userManageDialog input[name="name"]').val()||""==$('#userManageDialog input[name="security_answer"]').val()||
			""==$('#userManageDialog input[name="security_question"]').val()||""==$('#userManageDialog #roleSelect').combobox('getValue')||
			""==$('#userManageDialog #statusSelect').combobox('getValue')){
			msgShow("信息不完整错误","缺少必填信息！请检查红色框及角色、状态等必填信息是否完整！","warning");
		}else{
			var userData = {
					"user_name":$('#userManageDialog input[name="user_name"]').val(),
					"pass_word":$('#userManageDialog input[name="pass_word"]').val(),
					"status":$('#userManageDialog #statusSelect').combobox('getValue'),
					"email":$('#userManageDialog input[name="email"]').val(),
					"security_question":$('#userManageDialog input[name="security_question"]').val(),
					"security_answer":$('#userManageDialog input[name="security_answer"]').val(),
					"organization":$('#userManageDialog #organizationSelect').val(),
					"name":$('#userManageDialog input[name="name"]').val(),
					"sex":$('#userManageDialog #genderSelect').combobox('getValue'),
					"nation":$('#userManageDialog input[name="nation"]').val(),
					"birthday":$('#userManageDialog #birthday').datebox('getValue'),
					"native_place":$('#userManageDialog input[name="native_place"]').val(),
					"qq":$('#userManageDialog input[name="qq"]').val(),
					"phone_number":$('#userManageDialog input[name="phone_number"]').val(),
					"residence":$('#userManageDialog input[name="residence"]').val(),
					"province":$('#userManageDialog input[name="province"]').val(),
					"region":$('#userManageDialog input[name="region"]').val(),
					"role_name":$('#userManageDialog #roleSelect').combobox('getValue')
					};
			if($("#userManageDialog").hasClass("addUser")){
				$.ajax({
					type: 'POST',
					url: '/policyplatform/user/createUser',
					contentType: "application/json; charset=utf-8",
					async: false,
					data:JSON.stringify(userData),
					success: function (data) {
						if(data.code == 200){
							$("#userManageDialog").panel('close',true);
							$('.userManage #userName').val("");
							$('.userManage #userQuery').trigger("click");
						}else {
							msgShow("执行错误",data.message,"error");
						}
					},
					error: function () {
						msgShow("连接错误","未连通服务器！","error");
					}
				});
			}else{
				$.ajax({
					type: 'POST',
					url: '/policyplatform/user/editUser',
					contentType: "application/json; charset=utf-8",
					async: true,
					data:JSON.stringify(userData),
					success: function (data) {
						if(data.code == 200){
							$("#userManageDialog").panel('close',true);
							$('.userManage #userName').val("");
							$('.userManage #userQuery').trigger("click");
						}else {
							msgShow("执行错误",data.message,"error");
						}
					},
					error: function () {
						msgShow("连接错误","未连通服务器！","error");
					}
				});
			}
		}
	});
}
