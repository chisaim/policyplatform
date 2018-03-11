function initMessageAudit(){
	//初始化表格高度
	var he = $('div.message_audit').height();
	$(".message_audit #messageJunkGrid").css("height",he + "px");
	//初始化表格
	initMessageJunkGrid();
	//初始化表头工具栏
	initMessageJunkToolbar();
	//获取审核数据按钮绑定事件
	$(".message_audit #getMessageJunk").bind('click',function(){initGetMessageJunk();});
	//批量设置按钮绑定事件
	$(".message_audit #batchMessageBtn").bind('click',function(){batchSetAuditedResult();});
	//提交审核结果按钮事件绑定
	$(".message_audit #submitMessageAuditedBtn").bind('click',function(){submitMessageAudited();});
	//查询该用户当天已经审核的数据量
	$(".message_audit #queryAuditedNumBtn").bind('click',function(){queryAuditedNum();});
	//查询系统当前剩余需审核的数据量
	$(".message_audit #queryJUNKNumBtn").bind('click',function(){queryJUNKNum();});
}

function initMessageJunkGrid(){
	$(".message_audit #messageJunkGrid").datagrid({
		fit:true,
		fitColumns:true,// 表宽度自适应
		rownumbers:true,// 显示行号
		toolbar:'.message_audit #tb',
		pagination:false,
		nowrap:false,
		onClickCell:function(index, field, value){$(this).datagrid('beginEdit', index);},
		onLoadSuccess:function(data){
			var rows = $(this).datagrid('getRows');
			for(var rowsindex=0;rowsindex<rows.length;rowsindex++){
				$(this).datagrid('updateRow',{
					index: rowsindex,
					row: {
						auditedresult: 0//默认给0
					}
				});
				$(this).datagrid('beginEdit', rowsindex);
			}
		},
		columns:[[
		          {field:'junk_id',hidden:'true'},
		          {field:'msg_id',hidden:'true'},
		          {field:'content',title:'短信内容',width:150,halign:'center',align:'left'},
		          {field:'auditedresult',title:'审核结果',width:30,align:'center',editor:{type:'combobox',
		        	  options:{panelHeight:'auto',valueField:'setValue',textField:'setText',data:[{setValue : '0',setText : '正常'},
		        	      {setValue:'1',setText:'垃圾'}]},
		        	  }
		          },
		          {field:'advertiser',title:'广告主',width:50,align:'center'},
		          {field:'callerid',title:'主叫号码',width:50,align:'center'},
		          {field:'calledid',title:'被叫号码',width:50,align:'center'},
		          {field:'createdtime',title:'入库时间',width:60,align:'center',formatter:function(value,row,index){
		        	  return formTimestampValue(new Date(value));}},
		          {field:'repeatcount',title:'重复次数',width:30,align:'center'},
			      {field:'content_origin',title:'原始短信内容',width:150,halign:'center',align:'left'},
		          {field:'inspect_level',title:'违规级别',width:30,align:'center',formatter:function(value,row,index){
		        	  return formatMessageAuditInspectLevel(value);}},
		        ]]
	});
}
//格式化显示违规级别    违规级别：1.监控2.拦截3.拉黑
function formatMessageAuditInspectLevel(value){
	switch (value){
		case 1:
			return "监控";
		case 2:
			return "拦截";
		case 3:
			return "拉黑";
	}
}
function initMessageJunkToolbar(){
	$(".message_audit #illegalLevel").combobox({
		valueField:'value',
		textField:'text',
		editable:false,
		width:60,
		height:22,
		panelHeight:"auto",
		data:[{value:"",text:'所有'},
		      {value:1,text:'监控'},
		      {value:2,text:'拦截'},
		      {value:3,text:'拉黑'}]
	});
	$(".message_audit #batchMessageSet").combobox({
		valueField:'value',
		textField:'text',
		editable:false,
		width:60,
		height:22,
		panelHeight:"auto",
		data:[{value:0,text:'正常'},
		      {value:1,text:'垃圾'}]
	});
}
function initGetMessageJunk(){
	var timeStart = $(".message_audit #timeStart").datetimebox('getValue');
	var timeEnd = $(".message_audit #timeEnd").datetimebox('getValue');
	var illegalLevel = $(".message_audit #illegalLevel").combobox('getValue');
	var messageNumber = $(".message_audit #messageNumber").numberspinner('getValue');
	var querystr = "from="+timeStart+"&to="+timeEnd+"&inspect_level="+illegalLevel+"&number="+messageNumber;
	$.ajax({
		type: 'GET',
		url: '/policyplatform/message/getMessageJunk?'+querystr,
		async: false,
		success: function(data){
			if(data.code==200){
				$(".message_audit #messageJunkGrid").datagrid('loadData',data.data);
			}else{
				msgShow("提示信息",data.message,"info");
			}
		},
		error: function () {
			msgShow("连接错误","未连通服务器！","error");
		}
	});
}
function batchSetAuditedResult(){
	var rows = $(".message_audit #messageJunkGrid").datagrid('getRows');
	var setResult = $(".message_audit #batchMessageSet").combobox('getValue');
	for(var rowsindex=0;rowsindex<rows.length;rowsindex++){
		$(".message_audit #messageJunkGrid").datagrid('updateRow',{
			index: rowsindex,
			row: {
				auditedresult: setResult//默认给0
			}
		});
		$(".message_audit #messageJunkGrid").datagrid('beginEdit', rowsindex);
	}
}
function submitMessageAudited(){
	var rows = $(".message_audit #messageJunkGrid").datagrid('getRows');
	var array = new Array();
	for(var rowsindex=0;rowsindex<rows.length;rowsindex++){
		//必须得停止编辑，不然获取不到值
		$(".message_audit #messageJunkGrid").datagrid('endEdit', rowsindex);
		var obj = {
			junk_id:rows[rowsindex].junk_id,
			msg_id:rows[rowsindex].msg_id,
			auditedresult:rows[rowsindex].auditedresult
		};
		//复原状态，不然太丑了
		$(".message_audit #messageJunkGrid").datagrid('beginEdit', rowsindex);
		array.push(obj);
	}
	//将数据post到后台
	$.ajax({
		type: 'POST',
		url: '/policyplatform/message/submitMessageAudited',
		async: false,
		contentType: "application/json; charset=utf-8",
	 	data:JSON.stringify(array),
		success: function(data){
			if(data.code==200){
				//伪装加载数据，其实是清理屏幕
				$(".message_audit #messageJunkGrid").datagrid('loadData',{total:0,rows:[]});
			}else{
				msgShow("提示信息",data.message,"info");
			}
		},
		error: function () {
			msgShow("连接错误","未连通服务器！","error");
		}
	});
}
function queryAuditedNum(){
	$.ajax({
		type: 'GET',
		url: '/policyplatform/message/queryAuditedNum',
		async: false,
		success: function(data){
			msgShow("提示信息",data.message,"info");
		},
		error: function () {
			msgShow("连接错误","未连通服务器！","error");
		}
	});
}
function queryJUNKNum(){
	$.ajax({
		type: 'GET',
		url: '/policyplatform/message/queryJUNKNum',
		async: false,
		success: function(data){
			msgShow("提示信息",data.message,"info");
		},
		error: function () {
			msgShow("连接错误","未连通服务器！","error");
		}
	});
}