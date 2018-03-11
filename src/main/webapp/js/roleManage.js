//初始化函数
function initRole(){
	var he = $('div.roleManage').height();
	$(".roleManage #roleManageGrid").css("height",he + "px");
	// 初始化表格
	initRoleGrid('.roleManage #roleManageGrid');
	//初始化数据，即打开页面时刷新数据
	initRoleGridData();

//*******************************************************************//
	//初始化按钮事件绑定
	initRoleBtnQuery('.roleManage #roleQuery');
	initRoleBtnAdd('.roleManage #roleAdd');
	initRoleBtnEdit('.roleManage #roleEdit');
	initRoleBtnDelete('.roleManage #roleDelete');
//*******************************************************************//
	initRoleManageDialog();
	initRoleCombobox();
	$.parser.parse($("#roleManageDialog #roleManageContent"));
	//初始化下拉列表
//+++++++++++++++++初始化权限可选数据++++++++++++++++++++++++++++++++++++//
	initRoleActionData();
	initActionCheckbox();
	initAddOrEditRole();
}

function initRoleGrid(tableEleName){
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
	{field:'name',title:'角色名',width:180,align:'left'},
	{field:'status',title:'状态',width:100,align:'right',formatter:function(value){
		return value?true:false;
	}},
	{field:'description',title:'描述',width:100,align:'right'},
	{field:'create_by',title:'创建人',width:80,align:'left'},
	{field:'create_time',title:'创建时间',width:80,align:'left',formatter : function(value){
		var date = new Date(value);
		var y = date.getFullYear();
		var m = date.getMonth() + 1;
		var d = date.getDate();
		return y + '-' +m + '-' + d;}},]]
	});
}

function initRoleGridData(){
	$.ajax({
		type: 'GET',
		url: '/policyplatform/user/getRoles',
		async: false,
		success: function (data) {
			if(data.code == 200){
				$('.roleManage #roleManageGrid').datagrid('loadData',data.data);
			}else{
				msgShow("查询错误",data.message,"error");
			}
		},
		error: function () {
			msgShow("连接错误","未连通服务器！","error");
		}
	});
}

function initRoleBtnQuery(btnElement){
	$(btnElement).bind('click',function(){
		var roleName = $('.roleManage #roleName').val();
		if(""==roleName){
			initRoleGridData();
		}else{
			var rows = $('.roleManage #roleManageGrid').datagrid('getRows');
			for(var rowsindex=0;rowsindex<rows.length;rowsindex++){
				if(rows[rowsindex].name!=roleName){
					$('.roleManage #roleManageGrid').datagrid('deleteRow',rowsindex);
				}
			}
		}
	});
}

function initRoleBtnAdd(btnElement){
	$(btnElement).bind('click',function(){
		$('#roleManageDialog input[id=role_name]').removeAttr("readonly");
		$('#roleManageDialog').addClass("addRole");
		initRolePanel();
		$('#roleManageDialog').panel('open',true);
	});
}

function initRoleBtnEdit(btnElement){
	$(btnElement).bind('click',function(){
		initRolePanel();
		//判断是否有选中的数据
		var data = $('.roleManage #roleManageGrid').datagrid('getSelected');
		if(null!=data){
			$.ajax({
				type: 'GET',
				url: '/policyplatform/user/getRoleInfo?role_name='+data.name,
				async: false,
				success: function (data) {
					if(data.code == 200){
						//数据写入面板
						//checkbox数据
						$.each(data.data.mean_first,function(key,val){
							$('#roleManageDialog input[id='+val.menuid+']').attr("checked", true);
							$(".meanSecond input[id^="+val.menuid+"]").each(function(){
									$(this).attr("checked", false);
							});
							$.each(val.menus,function(key2,val2){
								$('#roleManageDialog #'+val2.menuid).attr("checked", true);
							});
						});
						//role数据，combobox的值无法用上面的方法设置，角色名不支持更改
						$('#roleManageDialog #status').combobox('setValue',String(data.data.role.status));
						$('#roleManageDialog input[name=role_name]').val(data.data.role.name);
						$('#roleManageDialog input[name=role_name]').attr("readonly","readonly");
						$('#roleManageDialog textarea[name=description]').val(data.data.role.description);
						$('#userManageDialog').removeClass("addRole");
						$('#roleManageDialog').panel('open',true);
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

function initRoleBtnDelete(btnElement){
	$(btnElement).bind('click',function(){
		var role_name = $('.roleManage #roleManageGrid').datagrid('getSelected').name;
		var data_role = {
			"role_name":role_name
		};
		$.ajax({
			type: 'POST',
			url: '/policyplatform/user/deleteRole',
			contentType: "application/json; charset=utf-8",
			async: false,
			data:JSON.stringify(data_role),
			success: function (data) {
				if(data.code == 200){
					$('.roleManage #roleName').val("");
					$('.roleManage #roleQuery').trigger("click");
				}else {
					msgShow("执行错误",data.message,"error");
				}
			},
			error: function () {
				msgShow("连接错误","未连通服务器！","error");
			}
		});
	});
}

function initRoleActionData(){
	 $.ajax({
		 	type: 'GET',
		 	url: '/policyplatform/user/getUserTab',
		 	async: false,
		 	success: function (data) {
		 		if(data.code == 200){
		 			var formdata = data.data;
		 			var eleUlStart = "<ul style='list-style:none;'>";
		 			for(var i=0;i<data.data.menus.length;i++){
		 				var li = "<li>";
		 				var divF = "<div style='font-size:16px;font-weight:bold;'>"+"<input class='meanfirst' type='checkbox' style='zoom:130%;' id='"+
		 					data.data.menus[i].menuid+"'>"+data.data.menus[i].menuname+"</div>";
		 				var divS = "<div style='word-wrap:break-word;word-break: break-all;'>";
		 				$.each(data.data.menus[i].menus,function(key,val){
		 					divS+="<div><input class='meanSecond' type='checkbox' style='margin-left:30px;' id='"+val.menuid+"'>"+val.menuname+"</div>";
		 				});
		 				divS+="</div>";
		 				eleUlStart+= li + divF + divS + "</li>";
		 			}
		 			eleUlStart+="</ul>";
		 			$("#roleManageDialog #roleManageContent").append(eleUlStart);
		 		}else {
					msgShow("信息",data.message,"info");
				}
		 	},
		 	error: function () {
		 		msgShow("连接错误","连接服务器失败","error");
		 	}
		 });
}

function initActionCheckbox(){
	//设置一级菜单的checkbox选中/取消选中事件
	$("#roleManageDialog .meanfirst").each(function(){
		$(this).change(function(){
			var idFirst = $(this).attr("id");
			if($(this).attr("checked")){
				$("#roleManageDialog .meanSecond").each(function(){
					if(idFirst==$(this).attr("id").substring(0,idFirst.length)){
						$(this).attr("checked", true);
					}
				});
			}else{
				$("#roleManageDialog .meanSecond").each(function(){
					if(idFirst==$(this).attr("id").substring(0,idFirst.length)){
						$(this).attr("checked", false);
					}
				});
			}
		});
	});
}
function initRoleCombobox(){
	//初始化状态信息
	$("#roleManageDialog input[name='status']").combobox({
		panelHeight:45,
		valueField:'Value',
		textField:'Text',
		editable:false,
		data:[{Value:'true',Text:'有效'},{Value:'false',Text:'失效'}]
	});
}
function roleUniqueCheck(str){
	if(""!=str){
		$.ajax({
			type: 'GET',
			url: '/policyplatform/user/roleUniqueCheck?role_name='+str,
			contentType: "text/plain; charset=utf-8",
			async: false,
			success: function (data) {
				if(data.code == 200){
					//角色名可用则什么都不做
				}else {
					msgShow("参数检查",data.message,"info");
				}
			},
		});
	}
}

function initRolePanel(){
	$.parser.parse($("#roleManageDialog #roleManageContent"));
	$('#roleManageDialog input[name="role_name"]').val("");
	$('#roleManageDialog textarea[name="description"]').val("");
	$('#roleManageDialog #status').combobox('setValue',"true");
	$('#roleManageDialog input[type="checkbox"]').each(function(){
		$(this).attr("checked", false);
	});
}

//组织数据
function formatData(){
	var actionData = [];
	$("#roleManageDialog .meanfirst").each(function(key,value){
		var s = $(value).is(":checked");//一级菜单是否被选中
		var idF = $(value).attr("id");
		//如果一级菜单被选中或二级菜单被选中的数量大于0,则数据需要传递给后台
		if($("input[id^="+$(value).attr("id")+"]:checked").length>0){
			var meanfirst = new Object();
			meanfirst.menuid = $(value).attr("id");
			var meanSecond = [];
			$("input[id^="+$(value).attr("id")+"]:checked").each(function(key2,value2){
				if($(value2).attr("id").length==1){
					return  true;
				}
				var meansecond = new Object();
				meansecond.menuid = $(value2).attr("id");
				meanSecond.push(meansecond);
			});
			meanfirst.menus = meanSecond;
			actionData.push(meanfirst);
		}
	});
	var roleData = {
			role:{
				"name":$("#roleManageDialog input[name='role_name']").val(),
				"status":$("#roleManageDialog #status").combobox('getValue'),
				"description":$("#roleManageDialog textarea[name='description']").val()
			},
			mean_first:actionData
	};
	return roleData;
}
function initRoleManageDialog(){
	//dialog模板初始化
	$("#roleManageDialog").dialog({
	    title: '角色信息',
	    width: 420,
	    height: 300,
	    closed: true,
	    cache: false,
	    buttons:[{id:'saveRole',iconCls:'icon-save',text:'Save'},
	             {id:'cancleRole',iconCls:'icon-cancel',text:'Cancle'}],
	    content:'<div id="roleManageContent"></div>'
	});
	$("#roleManageDialog #roleManageContent").append("</br><a style='margin-left:5px;margin-top:5px;color:black;'>角色名：</a><input type='text' name='role_name' id='role_name' style='width: 70px;border-color: red;' onblur='roleUniqueCheck(this.value)'>");
	$("#roleManageDialog #roleManageContent").append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a style='margin-left:5px;margin-top:5px;color:black;'>状态：</a><input type='text' id='status' name='status' value='true' style='width: 60px;border-color: red;'>");
	$("#roleManageDialog #roleManageContent").append("</br><div><div style='float:left;margin-top: 25px;'><a style='margin-left:5px;margin-top:5px;color:black;'>描述说明：</a></div><div><textarea  type='text' name='description' style='width: 300px;height: 50px;margin-top:10px;'/></div></div>");
}
function initAddOrEditRole(){
	$("#cancleRole").bind('click',function(){
		$("#roleManageDialog").panel('close',true);
	});
	$("#saveRole").bind('click',function(){
		if(""==$("#roleManageDialog input[name='role_name']").val()||""==$("#roleManageDialog input[name='status']").val()){
			msgShow("信息不完整错误","缺少必填信息！请检查红色框的必填信息是否完整！","warning");
		}else{
			if($("#roleManageDialog").hasClass("addRole")){
				$.ajax({
				 	type: 'POST',
				 	url: '/policyplatform/user/roleAdd',
				 	async: false,
				 	contentType: "application/json; charset=utf-8",
				 	data:JSON.stringify(formatData()),
				 	success: function (data) {
				 		if(data.code == 200){
				 			$("#roleManageDialog").panel('close',true);
							$('.roleManage #roleName').val("");
							$('.roleManage #roleQuery').trigger("click");
				 		}else {
				 			msgShow("执行错误",data.message,"error");
						}
				 	},
				 	error: function () {
				 		msgShow("连接错误","连接服务器失败","error");
				 	}
				 });
			}else{
				$.ajax({
				 	type: 'POST',
				 	url: '/policyplatform/user/roleUpdate',
				 	async: false,
				 	contentType: "application/json; charset=utf-8",
				 	data:JSON.stringify(formatData()),
				 	success: function (data) {
				 		if(data.code == 200){
				 			$("#roleManageDialog").panel('close',true);
							$('.roleManage #roleName').val("");
							$('.roleManage #roleQuery').trigger("click");
				 		}else {
				 			msgShow("执行错误",data.message,"error");
						}
				 	},
				 	error: function () {
				 		msgShow("连接错误","连接服务器失败","error");
				 	}
				 });
			}
		}
	});
}