function initSampleRepair(){
//	var he = $('div.examedReport').height();
//	$(".examedReport #examedReportGrid").css("height",he + "px");
	var editkey = -1;
	//初始化文件表格，即sampleRepairFileGrid
	initSampleRepairFileGrid();
	
	//初始化文件上传按钮
	initPreEvaluateSmsForm();
	
	$.parser.parse($("#sampleRepairNorth #tb"))
	$("#sampleRepairNorth #dateFrom").datebox("setValue", "2018-1-1");   
	
	pagenationSampleRepairFile(Date.parse(new Date("2018-1-1 00:00:00")),Date.parse(new Date($('#sampleRepair #dateTo').datebox('getValue')+" 23:59:59")),'',1,10);
	
	//查询按钮事件绑定
	$("#sampleRepairNorth #sampleRepairFileQuery").bind('click',function(){
		pagenationSampleRepairFile(Date.parse(new Date($('#sampleRepair #dateFrom').datebox('getValue')+" 23:59:59")),Date.parse(new Date($('#sampleRepair #dateTo').datebox('getValue')+" 23:59:59")),$('#sampleRepairNorth #filename').val(),1,10);
	});
	
	//初始化短信表格
	initSampleRepairMessageGrid();
	
	//短信信息面板
	initPreEvaluateSmsDialog();

	//按条件查询数据
	$("#sampleRepairCenter #sampleRepairMessageQuery").bind('click',function(){
		pagenationPreMessage(1,10);
	});
	//给某个文件增加短信信息
	$("#sampleRepairCenter #sampleRepairMessageAdd").bind('click',function(){
		//1.标记为addMessage2.清空面板3.弹出面板
		$("#preEvaluateSmsDialog").addClass("addMessage");
		$("#preEvaluateSmsDialog #preEvaluateSms").val("");
		$("#preEvaluateSmsDialog #servicetype").combobox("setValue",1);
		$("#preEvaluateSmsDialog #callerid").val("");
		$("#preEvaluateSmsDialog #calledid").val("");
		$("#preEvaluateSmsDialog").panel('open',true);
	});
	//修改某个文件增加短信信息
	$("#sampleRepairCenter #sampleRepairMessageEdit").bind('click',function(){
		editkey = -1;
		//1.移除标记为addMessage2.清空数据回填3.弹出面板
		$("#preEvaluateSmsDialog").removeClass("addMessage");
		var row = $("#sampleRepairCenter #sampleRepairMessageGrid").datagrid('getSelected');
		if(row==null){
			msgShow("操作错误","请选中数据！","error");
		}else{
			editkey = row.id;
			$("#preEvaluateSmsDialog #preEvaluateSms").val(row.content);
			$("#preEvaluateSmsDialog #servicetype").combobox("setValue",row.servicetype);
			$("#preEvaluateSmsDialog #callerid").val(row.callerid);
			$("#preEvaluateSmsDialog #calledid").val(row.calledid);
			$("#preEvaluateSmsDialog").panel('open',true);
		}
	});
	$("#saveEvaluateSms").bind('click',function(){
		if($("#preEvaluateSmsDialog #preEvaluateSms").val()==""){
			msgShow("操作错误","请选填入短信内容！","error");
		}else{
			var data = orgPreEvaluateSmsDialog();
			if($("#preEvaluateSmsDialog").hasClass("addMessage")){
				var row = $("#sampleRepairNorth #sampleRepairFileGrid").datagrid('getSelected');
				if(row==null){
					msgShow("操作错误","请选中文件！","error");
				}else{
					//是增加短信数据
					data.fileid = row.fileid;
					addPreEvaluateSmsDialog(data);
				}
			}else{
				//是修改短信数据
				data.id = editkey;
				editPreEvaluateSmsDialog(data);
			}
			$("#preEvaluateSmsDialog").panel('close',true);
		}
	});
	
	$("#cancleEvaluateSms").bind('click',function(){
		$("#preEvaluateSmsDialog").panel('close',true);
	});
	
	//删除短信
	$("#sampleRepairCenter #sampleRepairMessageDel").bind('click',function(){
		var row = $("#sampleRepairCenter #sampleRepairMessageGrid").datagrid('getSelected');
		if(row==null){
			msgShow("操作错误","请选中数据！","error");
		}else{
			delPreEvaluateSmsDialog(row.id);
		}
	});
	
	//导出数据
	$('#sampleRepairCenter #sampleRepairMessageExport').bind('click',function(){
		$("#sampleRepairCenter #exportPreMessage").attr("href","#");
		//更改真●下载链href属性，并点击真●下载链接来下载数据
		var file = $("#sampleRepairNorth #sampleRepairFileGrid").datagrid('getSelected');
		if(file==null){
			msgShow("操作错误","请选中一个短信文件！","warn");
		}else{
			var fileid = file.fileid;
			var servicetype = $("#sampleRepairCenter #business_type").combobox('getValue');
			var content = $("#sampleRepairCenter #message_content").val();
			if(content!=""){
				content = encodeURI(encodeURI(content));
			}
			var querystr = "?fileid="+fileid+"&servicetype="+servicetype+"&content="+content;
			$("#sampleRepairCenter #true_export-btn").attr("href","/policyplatform/evalute/exportdata"+querystr); 
			$("#sampleRepairCenter #exportPreMessage").trigger('click');
		}
	});
}

function initSampleRepairFileGrid(){
	$("#sampleRepairNorth #sampleRepairFileGrid").datagrid({
		fit:true,
		fitColumns:true,// 表宽度自适应
		rownumbers:true,// 显示行号
		toolbar:'#sampleRepairNorth #tb',
		pagination:true,
		nowrap:false,
		singleSelect:true,
		columns:[[{field:'ck',checkbox:"true",width:40,align:'left'},
		{field:'fileid',hidden:'true'},
		{field:'filename',title:'文件名',width:150,halign:'center',align:'center'},
		{field:'datanumber',title:'样本总数',width:80,align:'center'},
		{field:'createtime',title:'导入时间',width:80,align:'center',formatter:function(value,row,index){
      	  return formTimestampValue(new Date(value));}},
		{field:'operator',title:'操作人',width:100,align:'center'}]]
	});
}
function initPreEvaluateSmsDialog(){
	//dialog模板初始化
	$("#preEvaluateSmsDialog").dialog({
	    title: '预评估短信信息',
	    width: 400,
	    height: 220,
	    closed: true,
	    cache: false,
	    buttons:[{id:'saveEvaluateSms',iconCls:'icon-save',text:'Save'},
	             {id:'cancleEvaluateSms',iconCls:'icon-cancel',text:'Cancle'}],
	    content:'<div id="preEvaluateSmsContent"></div>'
	});
	$("#preEvaluateSmsDialog #preEvaluateSmsContent").append('<div style="float:left;margin-top:20px;"><a style="margin-left:5px;margin-top:5px;color:black;">短信信息：</a></div>');
	$("#preEvaluateSmsDialog #preEvaluateSmsContent").append('<div style="float:left;"><textarea rows="3" cols="22" id="preEvaluateSms" type="text" style="width:308px;margin-left:5px;margin-top:5px;margin-bottom:10px;"/><div>');
	$("#preEvaluateSmsDialog #preEvaluateSmsContent").append('</br><a style="margin-left:5px;margin-top:5px;color:black;">业务类型：</a><input style="margin-left:5px;margin-top:5px;" id="servicetype" value="1">');
	$("#preEvaluateSmsContent #servicetype").combobox({
		valueField:'value',
		textField:'text',
		editable:false,
		width:100,
		height:22,
		panelHeight:"auto",
		data:[{value:1,text:'点到点'},
		      {value:2,text:'网间'},
		      {value:3,text:'端口类自有业务短信'},
		      {value:4,text:'端口类集团客户/行业应用短信'},
		      {value:5,text:'端口类梦网SP短信'},
		      {value:6,text:'其它'}]
	});
	$("#preEvaluateSmsDialog #preEvaluateSmsContent").append('</br>&nbsp;&nbsp;<a style="margin-left:5px;margin-top:5px;color:black;">主叫号码：</a>');
	$("#preEvaluateSmsDialog #preEvaluateSmsContent").append('<input type="text" style="margin-left:5px;margin-top:5px;" id="callerid"></br>');
	$("#preEvaluateSmsDialog #preEvaluateSmsContent").append('&nbsp;&nbsp;<a style="margin-left:5px;margin-top:5px;color:black;">被叫号码：</a>');
	$("#preEvaluateSmsDialog #preEvaluateSmsContent").append('<input type="text" style="margin-left:5px;margin-top:5px;" id="calledid"></br>');
	$.parser.parse($("#preEvaluateSmsDialog"));
}
function initPreEvaluateSmsForm(){
	$("#preEvaluateSmsForm").dialog({
	    title: '预评估短信文件上传',
	    width: 270,
	    height: 140,
	    closed: true,
	    cache: false,
	    content:'<form id="preEvaluateSmsFile" method="post" style="margin-left:30px;"></form>'
	});
	$("#preEvaluateSmsForm #preEvaluateSmsFile").append('</br>');
	$("#preEvaluateSmsForm #preEvaluateSmsFile").append('<input class="easyui-filebox" data-options="prompt:\'Choose a file...\'" name="file" style="width:200px;">');
	$("#preEvaluateSmsForm #preEvaluateSmsFile").append('</br></br>');
	$("#preEvaluateSmsForm #preEvaluateSmsFile").append('<a href="#" id="uploadPreEvaluateSmsFile" class="easyui-linkbutton" iconCls="icon-upload" style="margin-left:20px;">上传</a>');
	$("#preEvaluateSmsForm #preEvaluateSmsFile").append('<a href="#" id="canceluploadPreEvaluateSmsFile" class="easyui-linkbutton" iconCls="icon-clear" style="margin-left:30px;">重置</a>');
	$.parser.parse($("#preEvaluateSmsForm #preEvaluateSmsFile"));
	
	//文件上传
	$("#sampleRepairNorth #uploadMessage4PreEvaluate").bind('click',function(){
		$("#preEvaluateSmsForm #preEvaluateSmsFile").form('clear');
		$("#preEvaluateSmsForm").panel('open',true);
	});
	
	$("#preEvaluateSmsForm #uploadPreEvaluateSmsFile").bind('click',function(){
		//可以将form表单整个直接传递请求中
		var form = new FormData($('#preEvaluateSmsForm #preEvaluateSmsFile')[0]);//FormData用于封装数据
		$.ajax({  
            url:"/policyplatform/evalute/uploadPreSms",  
            type: 'POST',  
            data: form,
            async: false,
            cache: false,  
            contentType: false,  
            processData: false,  
            success: function (data) {
            	if(data.code == 200){
            		$("#sampleRepairNorth #sampleRepairFileQuery").trigger('click');
            	}else{
            		msgShow("上传文件失败",data.message,"info");
            	}
            	$("#preEvaluateSmsForm").panel('close',true);
             },  
             error: function (data) {
            	 msgShow("连接错误","未连通服务器！","error");
             },
             complete:function(){
            	 $("#preEvaluateSmsForm").panel('close',true);
             }
        }); 
	});
	$("#preEvaluateSmsForm #canceluploadPreEvaluateSmsFile").bind('click',function(){
		$("#preEvaluateSmsForm #preEvaluateSmsFile").form('clear');
	});
}
function pagenationSampleRepairFile(dateFrom,dateTo,filename,pageNumber, pageSize){
	var resultData = queryPreFileSms(dateFrom,dateTo,filename,pageNumber, pageSize);
	if(null!=resultData){
		if(200==resultData.code){
			if(null==resultData.data||undefined ==resultData.data){
				msgShow("查询信息",resultData.message,"info");
			}else{
				$('#sampleRepairNorth #sampleRepairFileGrid').datagrid('loadData',resultData.data.data);
				//重设pagenation
				//分页插件
				var pageplug = $('#sampleRepairNorth #sampleRepairFileGrid').datagrid('getPager');
				$(pageplug).pagination({
					total:resultData.data.total,					//记录总数,需要查库才能得到
					pageSize:pageSize,				//页面尺寸
					pageNumber:pageNumber,			//创建分页（pagination）时显示的页码
					pageList:[10,20,30], 		//用户能改变页面尺寸。pageList 属性定义了能改成多大的尺寸
					//	loading:false					//定义数据是否正在加载
					//	buttons:						//自定义按钮
					showPageList:true,				//定义是否显示页面列表
					showRefresh:true,				//定义是否显示刷新按钮
					onSelectPage:function(pageNumber, pageSize){//当用户选择新的页面时触发
						pagenationSampleRepairFile(Date.parse(new Date($('#sampleRepairNorth #dateFrom').datebox('getValue')+" 00:00:00")),Date.parse(new Date($('#sampleRepairNorth #dateTo').datebox('getValue')+" 23:59:59")),filename,pageNumber, pageSize);
					},
					onRefresh:function(pageNumber, pageSize){//刷新之后触发
						pagenationSampleRepairFile(Date.parse(new Date($('#sampleRepairNorth #dateFrom').datebox('getValue')+" 00:00:00")),Date.parse(new Date($('#sampleRepairNorth #dateTo').datebox('getValue')+" 23:59:59")),filename,pageNumber, pageSize);
					},
					onChangePageSize:function(pageSize){//当用户改变页面尺寸时触发
						pagenationSampleRepairFile(Date.parse(new Date($('#sampleRepairNorth #dateFrom').datebox('getValue')+" 00:00:00")),Date.parse(new Date($('#sampleRepairNorth #dateTo').datebox('getValue')+" 23:59:59")),filename,1, pageSize);
					}
				});
			}
		}else{
			msgShow("查询错误",resultData.message,"error");
		}
	}else{
		msgShow("连接错误","未连通服务器！","error");
	}
}
function queryPreFileSms(dateFrom,dateTo,filename,pageNumber, pageSize){
	var da;
	var queryString = "from="+dateFrom+"&to="+dateTo+"&filename="+filename+"&size="+pageSize+"&number="+pageNumber;
	$.ajax({
		type: 'GET',
		url: '/policyplatform/evalute/queryPreMessageFileInfo?'+queryString,
		async: false,
		error: function () {
			msgShow("连接错误","未连通服务器！","error");
		}
	}).done(function(data){
		da = data;
	});
	return da;
}
function initSampleRepairMessageGrid(){
	$("#sampleRepairCenter #sampleRepairMessageGrid").datagrid({
		fit:true,
		fitColumns:true,// 表宽度自适应
		rownumbers:true,// 显示行号
		toolbar:'#sampleRepairCenter #tb',
		pagination:true,
		nowrap:false,
		columns:[[{field:'ck',checkbox:"true",width:40,align:'left'},
		{field:'id',hidden:'true'},
		{field:'fileid',hidden:'true'},
		{field:'content',title:'短信内容',width:150,halign:'center',align:'left'},
		{field:'callerid',title:'主叫号码',width:80,align:'center'},
		{field:'calledid',title:'被叫号码',width:80,align:'center'},
		{field:'createdate',title:'导入时间',width:80,align:'center',formatter:function(value,row,index){
      	  return formTimestampValue(new Date(value));}},
//      	{field:'filename',title:'所属文件',width:80,align:'center'},
//		{field:'operator',title:'操作人',width:100,align:'center'},
      	  {field:'servicetype_cn',title:'业务类型',width:80,align:'center'}]]
	});
	$("#sampleRepairCenter #business_type").combobox({
		valueField:'value',
		textField:'text',
		editable:false,
		width:100,
		height:22,
		panelHeight:"auto",
		data:[{value:"",text:'全部'},
		      {value:1,text:'点到点'},
		      {value:2,text:'网间'},
		      {value:3,text:'端口类自有业务短信'},
		      {value:4,text:'端口类集团客户/行业应用短信'},
		      {value:5,text:'端口类梦网SP短信'},
		      {value:6,text:'其它'}]
	});
}
function pagenationPreMessage(page,size){
	var file = $("#sampleRepairNorth #sampleRepairFileGrid").datagrid('getSelected');
	if(file==null){
		msgShow("操作错误","请选中一个短信文件！","warn");
	}else{
		var fileid = file.fileid;
		var servicetype = $("#sampleRepairCenter #business_type").combobox('getValue');
		var content = $("#sampleRepairCenter #message_content").val();
		if(content!=""){
			content = encodeURI(encodeURI(content));
		}
		var resultData = queryPreMessage(fileid,servicetype,content,page,size);
		if(null!=resultData){
			if(200==resultData.code){
				if(null==resultData.data||undefined ==resultData.data){
					msgShow("查询信息",resultData.message,"info");
				}else{
					$('#sampleRepairCenter #sampleRepairMessageGrid').datagrid('loadData',resultData.data.data);
					//重设pagenation
					//分页插件
					var pageplug = $('#sampleRepairCenter #sampleRepairMessageGrid').datagrid('getPager');
					$(pageplug).pagination({
						total:resultData.data.total,					//记录总数,需要查库才能得到
						pageSize:size,				//页面尺寸
						pageNumber:page,			//创建分页（pagination）时显示的页码
						pageList:[10,20,30], 		//用户能改变页面尺寸。pageList 属性定义了能改成多大的尺寸
						//	loading:false					//定义数据是否正在加载
						//	buttons:						//自定义按钮
						showPageList:true,				//定义是否显示页面列表
						showRefresh:true,				//定义是否显示刷新按钮
						onSelectPage:function(pageNumber, pageSize){//当用户选择新的页面时触发
							pagenationPreMessage(pageNumber, pageSize);
						},
						onRefresh:function(pageNumber, pageSize){//刷新之后触发
							pagenationPreMessage(pageNumber, pageSize);						
						},
						onChangePageSize:function(pageSize){//当用户改变页面尺寸时触发
							pagenationPreMessage(1, pageSize);						
						}
					});
				}
			}else{
				msgShow("查询错误",resultData.message,"error");
			}
		}else{
			msgShow("连接错误","未连通服务器！","error");
		}
	}
}
function queryPreMessage(fileid,servicetype,content,page,size){
	var da;
	var queryString = "fileid="+fileid+"&servicetype="+servicetype+"&content="+content+"&page="+page+"&size="+size;
	$.ajax({
		type: 'GET',
		url: '/policyplatform/evalute/queryPreMessage?'+queryString,
		async: false,
		error: function () {
			msgShow("连接错误","未连通服务器！","error");
		}
	}).done(function(data){
		da = data;
	});
	return da;
}
function orgPreEvaluateSmsDialog(){
	var data = {
		content:$("#preEvaluateSmsDialog #preEvaluateSms").val(),
		servicetype:$("#preEvaluateSmsDialog #servicetype").combobox("getValue"),
		callerid:$("#preEvaluateSmsDialog #callerid").val(),
		calledid:$("#preEvaluateSmsDialog #calledid").val(),
	};
	return data;
}
function addPreEvaluateSmsDialog(data){
	$.ajax({
		type: 'POST',
		url: '/policyplatform/evalute/addPreEvaluateSms',
		async: false,
		contentType: "application/json; charset=utf-8",
	 	data:JSON.stringify(data),
		success: function(data){
			if(data.code==200){
				$("#sampleRepairCenter #sampleRepairMessageQuery").trigger('click');
			}else{
				msgShow("提示信息",data.message,"info");
			}
		},
		error: function () {
			msgShow("连接错误","未连通服务器！","error");
		}
	});
}
function editPreEvaluateSmsDialog(data){
	$.ajax({
		type: 'POST',
		url: '/policyplatform/evalute/editPreEvaluateSms',
		async: false,
		contentType: "application/json; charset=utf-8",
	 	data:JSON.stringify(data),
		success: function(data){
			if(data.code==200){
				$("#sampleRepairCenter #sampleRepairMessageQuery").trigger('click');
			}else{
				msgShow("提示信息",data.message,"info");
			}
		},
		error: function () {
			msgShow("连接错误","未连通服务器！","error");
		}
	});
}
function delPreEvaluateSmsDialog(id){
	$.ajax({
		type: 'GET',
		url: '/policyplatform/evalute/delPreEvaluateSms/'+id,
		async: false,
		success: function(data){
			if(data.code==200){
				$("#sampleRepairCenter #sampleRepairMessageQuery").trigger('click');
			}else{
				msgShow("提示信息",data.message,"info");
			}
		},
		error: function () {
			msgShow("连接错误","未连通服务器！","error");
		}
	});
}