function initPreStrategyManage(){
	
	var editkey = -1;
	
	//初始化文件表格，即preStrategyFileGrid
	initPreStrategyFileGrid();
	
	//初始化文件上传按钮
	initPreEvaluatePolicyForm();
	
	$.parser.parse($("#preStrategyNorth #tb"))
	pagenationPreStrategyFile(Date.parse(new Date("2018-1-1 00:00:00")),Date.parse(new Date($('#preStrategyNorth #dateTo').datebox('getValue')+" 23:59:59")),'',1,10);
	
	//查询按钮事件绑定
	$("#preStrategyNorth #preStrategyFileQuery").bind('click',function(){
		pagenationPreStrategyFile(Date.parse(new Date($('#preStrategyNorth #dateFrom').datebox('getValue')+" 23:59:59")),Date.parse(new Date($('#preStrategyNorth #dateTo').datebox('getValue')+" 23:59:59")),$('#preStrategyNorth #filename').val(),1,10);
	});
	
	//初始化策略表格
	initPreStrategyGrid();
	
	//按条件查询数据
	$("#preStrategyCenter #preStrategyQuery").bind('click',function(){
		pagenationPreStrategy(1,10);
	});
	
	//策略信息面板
	initPreEvaluatePolicyDialog();
	
	//给某个文件增加短信信息
	$("#preStrategyCenter #preStrategyAdd").bind('click',function(){
		//1.标记为addKeyword2.清空面板3.弹出面板
		$("#preEvaluatePolicyDialog").addClass("addKeyword");
		$("#preEvaluatePolicyDialog #preEvaluateKeyword").val("");
		$("#preEvaluatePolicyDialog #servicetype").combobox("setValue",1);
		$("#preEvaluatePolicyDialog #kwclass").combobox("setValue",1);
		$("#preEvaluatePolicyDialog").panel('open',true);
	});
	
	//修改某个文件增加策略信息
	$("#preStrategyCenter #preStrategyEdit").bind('click',function(){
		editkey = -1;
		//1.移除标记为addMessage2.清空数据回填3.弹出面板
		$("#preEvaluatePolicyDialog").removeClass("addKeyword");
		var row = $("#preStrategyCenter #preStrategyGrid").datagrid('getSelected');
		if(row==null){
			msgShow("操作错误","请选中数据！","error");
		}else{
			editkey = row.id;
			$("#preEvaluatePolicyDialog #preEvaluateKeyword").val(row.keyword);
			$("#preEvaluatePolicyDialog #servicetype").combobox("setValue",row.servicetype);
			$("#preEvaluatePolicyDialog #kwclass").val(row.kwclass);
			$("#preEvaluatePolicyDialog").panel('open',true);
		}
	});
	$("#saveEvaluateKeyword").bind('click',function(){
		if($("#preEvaluatePolicyDialog #preEvaluateKeyword").val()==""){
			msgShow("操作错误","请选填入策略内容！","error");
		}else{
			var data = orgPreEvaluateKeywordDialog();
			if($("#preEvaluatePolicyDialog").hasClass("addKeyword")){
				var row = $("#preStrategyNorth #preStrategyFileGrid").datagrid('getSelected');
				if(row==null){
					msgShow("操作错误","请选中文件！","error");
				}else{
					//是增加短信数据
					data.fileid = row.fileid;
					addPreEvaluateKeywordDialog(data);
				}
			}else{
				//是修改短信数据
				data.id = editkey;
				editPreEvaluateKeywordDialog(data);
			}
			$("#preEvaluatePolicyDialog").panel('close',true);
		}
	});
	
	$("#cancleEvaluateKeyword").bind('click',function(){
		$("#preEvaluatePolicyDialog").panel('close',true);
	});
	
	//删除策略
	$("#preStrategyCenter #preStrategyDel").bind('click',function(){
		var row = $("#preStrategyCenter #preStrategyGrid").datagrid('getSelected');
		if(row==null){
			msgShow("操作错误","请选中数据！","error");
		}else{
			delPreEvaluateKeyword(row.id);
		}
	});
	
	//导出按条件查询的策略
	$('#preStrategyCenter #preStrategyExport').bind('click',function(){
		$("#preStrategyCenter #exportPreStrategy").attr("href","#");
		//更改真●下载链href属性，并点击真●下载链接来下载数据
		var file = $("#preStrategyNorth #preStrategyFileGrid").datagrid('getSelected');
		if(file==null){
			msgShow("操作错误","请选中一个短信文件！","warn");
		}else{
			var fileid = file.fileid;
			var servicetype = $("#preStrategyCenter #servicetype").combobox('getValue');
			var kwclass = $("#preStrategyCenter #kwclass").combobox('getValue');
			var content = $("#preStrategyCenter #keyword").val();
			if(content!=""){
				content = encodeURI(encodeURI(content));
			}
			var querystr = "?fileid="+fileid+"&servicetype="+servicetype+"&kwclass="+kwclass+"&keyword="+content;
			$("#preStrategyCenter #true_export-btn").attr("href","/policyplatform/evalute/exportKeyword"+querystr); 
			$("#preStrategyCenter #exportPreStrategy").trigger('click');
		}
	});
}
function initPreStrategyFileGrid(){
	$("#preStrategyNorth #preStrategyFileGrid").datagrid({
		fit:true,
		fitColumns:true,// 表宽度自适应
		rownumbers:true,// 显示行号
		toolbar:'#preStrategyNorth #tb',
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
function initPreEvaluatePolicyForm(){
	$("#preEvaluatePolicyForm").dialog({
	    title: '预评估策略文件上传',
	    width: 270,
	    height: 140,
	    closed: true,
	    cache: false,
	    content:'<form id="preEvaluatePolicyFile" method="post" style="margin-left:30px;"></form>'
	});
	$("#preEvaluatePolicyForm #preEvaluatePolicyFile").append('</br>');
	$("#preEvaluatePolicyForm #preEvaluatePolicyFile").append('<input class="easyui-filebox" data-options="prompt:\'Choose a file...\'" name="file" style="width:200px;">');
	$("#preEvaluatePolicyForm #preEvaluatePolicyFile").append('</br></br>');
	$("#preEvaluatePolicyForm #preEvaluatePolicyFile").append('<a href="#" id="uploadPreEvaluatePolicyFile" class="easyui-linkbutton" iconCls="icon-upload" style="margin-left:20px;">上传</a>');
	$("#preEvaluatePolicyForm #preEvaluatePolicyFile").append('<a href="#" id="canceluploadPreEvaluatePolicyFile" class="easyui-linkbutton" iconCls="icon-clear" style="margin-left:30px;">重置</a>');
	$.parser.parse($("#preEvaluatePolicyForm #preEvaluatePolicyFile"));
	
	//文件上传
	$("#preStrategyNorth #uploadPreStrategy").bind('click',function(){
		$("#preEvaluatePolicyForm #preEvaluatePolicyFile").form('clear');
		$("#preEvaluatePolicyForm").panel('open',true);
	});
	
	$("#preEvaluatePolicyForm #uploadPreEvaluatePolicyFile").bind('click',function(){
		//可以将form表单整个直接传递请求中
		var form = new FormData($('#preEvaluatePolicyForm #preEvaluatePolicyFile')[0]);//FormData用于封装数据
		$.ajax({  
            url:"/policyplatform/evalute/uploadPrePolicy",  
            type: 'POST',  
            data: form,
            async: false,
            cache: false,  
            contentType: false,  
            processData: false,  
            success: function (data) {
            	if(data.code == 200){
            		$("#preStrategyNorth #preStrategyFileQuery").trigger('click');
            	}else{
            		msgShow("上传文件失败",data.message,"info");
            	}
            	$("#preEvaluatePolicyForm").panel('close',true);
             },  
             error: function (data) {
            	 msgShow("连接错误","未连通服务器！","error");
             },
             complete:function(){
            	 $("#preEvaluatePolicyForm").panel('close',true);
             }
        }); 
	});
	$("#preEvaluatePolicyForm #canceluploadPreEvaluatePolicyFile").bind('click',function(){
		$("#preEvaluatePolicyForm #preEvaluatePolicyFile").form('clear');
	});
}
function pagenationPreStrategyFile(dateFrom,dateTo,filename,pageNumber, pageSize){
	var resultData = queryPreFileKeyword(dateFrom,dateTo,filename,pageNumber, pageSize);
	if(null!=resultData){
		if(200==resultData.code){
			if(null==resultData.data||undefined ==resultData.data){
				msgShow("查询信息",resultData.message,"info");
			}else{
				$("#preStrategyNorth #preStrategyFileGrid").datagrid('loadData',resultData.data.data);
				//重设pagenation
				//分页插件
				var pageplug = $("#preStrategyNorth #preStrategyFileGrid").datagrid('getPager');
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
						pagenationPreStrategyFile(Date.parse(new Date($('#preStrategyNorth #dateFrom').datebox('getValue')+" 00:00:00")),Date.parse(new Date($('#preStrategyNorth #dateTo').datebox('getValue')+" 23:59:59")),filename,pageNumber, pageSize);
					},
					onRefresh:function(pageNumber, pageSize){//刷新之后触发
						pagenationPreStrategyFile(Date.parse(new Date($('#preStrategyNorth #dateFrom').datebox('getValue')+" 00:00:00")),Date.parse(new Date($('#preStrategyNorth #dateTo').datebox('getValue')+" 23:59:59")),filename,pageNumber, pageSize);
					},
					onChangePageSize:function(pageSize){//当用户改变页面尺寸时触发
						pagenationPreStrategyFile(Date.parse(new Date($('#preStrategyNorth #dateFrom').datebox('getValue')+" 00:00:00")),Date.parse(new Date($('#preStrategyNorth #dateTo').datebox('getValue')+" 23:59:59")),filename,1, pageSize);
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
function queryPreFileKeyword(dateFrom,dateTo,filename,pageNumber, pageSize){
	var da;
	var queryString = "from="+dateFrom+"&to="+dateTo+"&filename="+filename+"&size="+pageSize+"&number="+pageNumber;
	$.ajax({
		type: 'GET',
		url: '/policyplatform/evalute/queryPreKeywordFileInfo?'+queryString,
		async: false,
		error: function () {
			msgShow("连接错误","未连通服务器！","error");
		}
	}).done(function(data){
		da = data;
	});
	return da;
}
function initPreStrategyGrid(){
	$("#preStrategyCenter #preStrategyGrid").datagrid({
		fit:true,
		fitColumns:true,// 表宽度自适应
		rownumbers:true,// 显示行号
		toolbar:'#preStrategyCenter #tb',
		pagination:true,
		nowrap:false,
		columns:[[{field:'ck',checkbox:"true",width:40,align:'left'},
		{field:'id',hidden:'true'},
		{field:'fileid',hidden:'true'},
		{field:'keyword',title:'关键字内容',width:150,halign:'center',align:'left'},
		{field:'kwclass_cn',title:'关键字分类',width:80,align:'center'},
		{field:'createdtime',title:'导入时间',width:80,align:'center',formatter:function(value,row,index){
      	  return formTimestampValue(new Date(value));}},
      	  {field:'servicetype_cn',title:'业务类型',width:80,align:'center'}]]
	});
	$("#preStrategyCenter #servicetype").combobox({
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
	$("#preStrategyCenter #kwclass").combobox({
		valueField:'value',
		textField:'text',
		editable:false,
		width:70,
		height:22,
		panelHeight:"auto",
		data:[{value:"",text:'全部'},
		      {value:1,text:'涉黄'},
		      {value:2,text:'政治类'},
		      {value:3,text:'商业广告'},
		      {value:4,text:'违法诈骗'},
		      {value:5,text:'其它'}]
	});
}
function pagenationPreStrategy(page,size){
	var file = $("#preStrategyNorth #preStrategyFileGrid").datagrid('getSelected');
	if(file==null){
		msgShow("操作错误","请选中一个短信文件！","warn");
	}else{
		var fileid = file.fileid;
		var servicetype = $("#preStrategyCenter #servicetype").combobox('getValue');
		var keyword = $("#preStrategyCenter #keyword").val();
		var kwclass = $("#preStrategyCenter #kwclass").combobox('getValue');
		if(keyword!=""){
			keyword = encodeURI(encodeURI(keyword));
		}
		var resultData = queryPreKeyword(fileid,servicetype,kwclass,keyword,page,size);
		if(null!=resultData){
			if(200==resultData.code){
				if(null==resultData.data||undefined ==resultData.data){
					msgShow("查询信息",resultData.message,"info");
				}else{
					$('#preStrategyCenter #preStrategyGrid').datagrid('loadData',resultData.data.data);
					//重设pagenation
					//分页插件
					var pageplug = $('#preStrategyCenter #preStrategyGrid').datagrid('getPager');
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
							pagenationPreStrategy(pageNumber, pageSize);
						},
						onRefresh:function(pageNumber, pageSize){//刷新之后触发
							pagenationPreStrategy(pageNumber, pageSize);						
						},
						onChangePageSize:function(pageSize){//当用户改变页面尺寸时触发
							pagenationPreStrategy(1, pageSize);						
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
function queryPreKeyword(fileid,servicetype,kwclass,keyword,page,size){
	var da;
	var queryString = "fileid="+fileid+"&servicetype="+servicetype+"&kwclass="+kwclass+"&keyword="+keyword+"&page="+page+"&size="+size;
	$.ajax({
		type: 'GET',
		url: '/policyplatform/evalute/queryPreKeyword?'+queryString,
		async: false,
		error: function () {
			msgShow("连接错误","未连通服务器！","error");
		}
	}).done(function(data){
		da = data;
	});
	return da;
}
function initPreEvaluatePolicyDialog(){
	//dialog模板初始化
	$("#preEvaluatePolicyDialog").dialog({
	    title: '预评估策略信息',
	    width: 400,
	    height: 220,
	    closed: true,
	    cache: false,
	    buttons:[{id:'saveEvaluateKeyword',iconCls:'icon-save',text:'Save'},
	             {id:'cancleEvaluateKeyword',iconCls:'icon-cancel',text:'Cancle'}],
	    content:'<div id="preEvaluateKeywordContent"></div>'
	});
	$("#preEvaluatePolicyDialog #preEvaluateKeywordContent").append('<a style="margin-left:5px;margin-top:5px;color:black;">策略信息：</a>');
	$("#preEvaluatePolicyDialog #preEvaluateKeywordContent").append('<input id="preEvaluateKeyword" type="text" style="width:308px;margin-left:5px;margin-top:5px;margin-bottom:10px;"/>');
	$("#preEvaluatePolicyDialog #preEvaluateKeywordContent").append('</br><a style="margin-left:5px;margin-top:5px;color:black;">业务类型：</a><input style="margin-left:5px;margin-top:5px;" id="servicetype" value="1">');
	$("#preEvaluateKeywordContent #servicetype").combobox({
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
	$("#preEvaluatePolicyDialog #preEvaluateKeywordContent").append('</br><a style="margin-left:5px;margin-top:5px;color:black;">关键字类型：</a><input style="margin-left:5px;margin-top:5px;" id="kwclass" value="1">');
	$("#preEvaluateKeywordContent #kwclass").combobox({
		valueField:'value',
		textField:'text',
		editable:false,
		width:60,
		height:22,
		panelHeight:"auto",
		data:[{value:1,text:'涉黄'},
		      {value:2,text:'政治类'},
		      {value:3,text:'商业广告'},
		      {value:4,text:'违法诈骗'},
		      {value:5,text:'其他'}]
	});
	$.parser.parse($("#preEvaluatePolicyDialog"));
}
function orgPreEvaluateKeywordDialog(){
	var data = {
			keyword:$("#preEvaluatePolicyDialog #preEvaluateKeyword").val(),
			servicetype:$("#preEvaluatePolicyDialog #servicetype").combobox("getValue"),
			kwclass:$("#preEvaluatePolicyDialog #kwclass").combobox("getValue"),
		};
	return data;
}
function addPreEvaluateKeywordDialog(data){
	$.ajax({
		type: 'POST',
		url: '/policyplatform/evalute/addPreEvaluateKeyword',
		async: false,
		contentType: "application/json; charset=utf-8",
	 	data:JSON.stringify(data),
		success: function(data){
			if(data.code==200){
				$("#preStrategyCenter #preStrategyQuery").trigger('click');
			}else{
				msgShow("提示信息",data.message,"info");
			}
		},
		error: function () {
			msgShow("连接错误","未连通服务器！","error");
		}
	});
}
function editPreEvaluateKeywordDialog(data){
	$.ajax({
		type: 'POST',
		url: '/policyplatform/evalute/editPreEvaluateKeyword',
		async: false,
		contentType: "application/json; charset=utf-8",
	 	data:JSON.stringify(data),
		success: function(data){
			if(data.code==200){
				$("#preStrategyCenter #preStrategyQuery").trigger('click');
			}else{
				msgShow("提示信息",data.message,"info");
			}
		},
		error: function () {
			msgShow("连接错误","未连通服务器！","error");
		}
	});
}
function delPreEvaluateKeyword(id){
	$.ajax({
		type: 'GET',
		url: '/policyplatform/evalute/delPreEvaluateKeyword/'+id,
		async: false,
		success: function(data){
			if(data.code==200){
				$("#preStrategyCenter #preStrategyQuery").trigger('click');
			}else{
				msgShow("提示信息",data.message,"info");
			}
		},
		error: function () {
			msgShow("连接错误","未连通服务器！","error");
		}
	});
}
