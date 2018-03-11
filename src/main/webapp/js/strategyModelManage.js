function initStrategyModelManage(){
	var editKeywordId = -1;//用于传递数据给edit事件
	var he = $('div.strategyModelManage').height();
	$(".strategyModelManage #keywordGrid").css("height",he + "px");
	//初始化keyword表格
	$(".strategyModelManage #keywordGrid").datagrid({
		fit:true,
		fitColumns:true,// 表宽度自适应
		rownumbers:true,// 显示行号
		toolbar:'.strategyModelManage #tb',
		pagination:true,
		columns:[[
		          {field:'id',hidden:'true'},
		          {field:'ck',checkbox:"true",width:30,align:'left'},
		          {field:'keyword',title:'关键词内容',width:150,align:'left'},
		          {field:'servicetype',title:'业务类型',width:80,align:'left'},
		          {field:'createdtime',title:'创建时间',width:80,align:'left',formatter:function(value,row,index){
		        	  return formTimestampValue(new Date(value));}},
			      {field:'kwclass',title:'关键词分类',width:100,align:'left'},
			      {field:'optype',title:'操作类型',width:80,align:'left'},
		          {field:'operator',title:'操作人',width:100,align:'left'},
		          {field:'monthreshold',title:'监控阈值',width:40,align:'left'},
		          {field:'filterthreshold',title:'过滤阈值',width:40,align:'left'},
		          {field:'abthreshold',title:'加黑阈值',width:40,align:'left'},
		          {field:'unittime',title:'处置时间',width:40,align:'left'}
		        ]]
	});
	//dialog模板初始化
	$("#keywordDialog").dialog({
	    title: '关键词策略信息',
	    width: 400,
	    height: 260,
	    closed: true,
	    cache: false,
	    buttons:[{id:'savekeyword',iconCls:'icon-save',text:'Save'},
	             {id:'canclekeyword',iconCls:'icon-cancel',text:'Cancle'}],
	    content:'<div id="keywordContent"></div>'
	});
	
	//初始化面板
	//************************************************************************//
	$("#keywordDialog #keywordContent").append('<a style="margin-left:5px;margin-top:5px;color:black;">关键词信息：</a>');
	$("#keywordDialog #keywordContent").append('<input id="keyword" type="text" style="width:280px;margin-left:5px;margin-top:5px;"></br>');
	$("#keywordDialog #keywordContent").append('</br><a style="margin-left:5px;margin-top:5px;color:black;">业务类型：</a><input style="margin-left:5px;margin-top:5px;" id="servicetype" value="1">');
	$("#keywordContent #servicetype").combobox({
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
	$("#keywordDialog #keywordContent").append('&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a style="margin-left:5px;margin-top:5px;color:black;">关键词分类：</a>');
	$("#keywordDialog #keywordContent").append('<input style="margin-left:5px;margin-top:5px;" id="kwclass" value="1"></br>');
	$("#keywordContent #kwclass").combobox({
		valueField:'value',
		textField:'text',
		editable:false,
		width:70,
		height:22,
		panelHeight:"auto",
		data:[{value:1,text:'涉黄'},
		      {value:2,text:'政治类'},
		      {value:3,text:'商业广告'},
		      {value:4,text:'违法诈骗'},
		      {value:5,text:'其他'}]
	});
	$("#keywordDialog #keywordContent").append('</br><a style="margin-left:5px;margin-top:5px;color:black;">处置时间：</a>');
	$("#keywordDialog #keywordContent").append('<input style="margin-left:5px;margin-top:5px;" id="timespan" value="1">');
	$("#keywordDialog #keywordContent").append('&nbsp;&nbsp;<input style="margin-left:5px;margin-top:5px;" id="unit" value="4">');
	$("#keywordDialog #keywordContent").append('&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a style="margin-left:5px;margin-top:5px;color:black;">监控阈值：</a><input style="margin-left:5px;margin-top:5px;" id="monthreshold" value="0"></br>');
	$("#keywordContent #timespan").numberspinner({
	    min: 0,
	    max: 100,
	    width: 60,
	    height: 22,
	    editable: true
	});
	$("#keywordContent #unit").combobox({
		valueField:'value',
		textField:'text',
		editable:false,
		width:50,
		height:22,
		panelHeight:"auto",
		data:[{value:1,text:'秒'},
		      {value:2,text:'分'},
		      {value:3,text:'小时'},
		      {value:4,text:'天'},
		      {value:5,text:'月'}]
	});
	$("#keywordContent #monthreshold").numberspinner({
	    min: 0,
	    max: 100,
	    width: 60,
	    height: 22,
	    editable: true
	});
	$("#keywordDialog #keywordContent").append('</br><a style="margin-left:5px;margin-top:5px;color:black;">过滤阈值：</a><input style="margin-left:5px;margin-top:5px;" id="filterthreshold" value="0">');
	$("#keywordDialog #keywordContent").append('&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a style="margin-left:5px;margin-top:5px;color:black;">加黑阈值：</a><input style="margin-left:5px;margin-top:5px;" id="abthreshold" value="0"></br>');
	$("#keywordContent #filterthreshold").numberspinner({
	    min: 0,
	    max: 100,
	    width: 60,
	    height: 22,
	    editable: true
	});
	$("#keywordContent #abthreshold").numberspinner({
	    min: 0,
	    max: 100,
	    width: 60,
	    height: 22,
	    editable: true
	});
	$("#keywordDialog #keywordContent").append('</br><a style="margin-left:5px;margin-top:5px;color:black;">操作类型：</a><input style="margin-left:5px;margin-top:5px;" id="optype" value="1">');
	$("#keywordContent #optype").combobox({
		valueField:'value',
		textField:'text',
		editable:false,
		width:60,
		height:22,
		panelHeight:"auto",
		data:[{value:1,text:'制作'},
		      {value:0,text:'解除'}]
	});
	$.parser.parse($("#keywordDialog"));
	$("#savekeyword").bind('click',function(){
		if($("#keywordDialog #keyword").val()==""){
			msgShow("提示信息","请输入策略关键字信息","info");
		}else{
			var data = organizeKeywordData();
			if($("#keywordDialog").hasClass("addkeyword")){
				$.ajax({
					type: 'POST',
					url: '/policyplatform/keyword/addNewKeyword',
					async: false,
				 	contentType: "application/json; charset=utf-8",
				 	data:JSON.stringify(data),
					success: function (data) {
						if(data.code == 200){
							pagenationKeyword(1,10);
						}else{
							msgShow("错误提示信息",data.message,"error");
						}
					},
					error: function () {
						msgShow("连接错误","未连通服务器！","error");
					}
				});
			}else{
				data.id = editKeywordId;
				$.ajax({
					type: 'POST',
					url: '/policyplatform/keyword/editKeyword',
					async: false,
				 	contentType: "application/json; charset=utf-8",
				 	data:JSON.stringify(data),
					success: function (data) {
						if(data.code == 200){
							pagenationKeyword(1,10);
						}else{
							msgShow("错误提示信息",data.message,"error");
						}
					},
					error: function () {
						msgShow("连接错误","未连通服务器！","error");
					}
				});
			}
			$("#keywordDialog").panel('close',true);
		}	
	});
	$("#canclekeyword").bind('click',function(){
		$("#keywordDialog").panel('close',true);
	});
	//************************************************************************//
	
	$("#keywordForm").dialog({
	    title: '关键词策略文件上传',
	    width: 270,
	    height: 140,
	    closed: true,
	    cache: false,
	    content:'<form id="keywordFile" method="post" style="margin-left:30px;"></form>'
	});
	$("#keywordForm #keywordFile").append('</br>');
	$("#keywordForm #keywordFile").append('<input class="easyui-filebox" data-options="prompt:\'Choose a file...\'" name="file" style="width:200px;">');
	$("#keywordForm #keywordFile").append('</br></br>');
	$("#keywordForm #keywordFile").append('<a href="#" id="uploadKeywordFile" class="easyui-linkbutton" iconCls="icon-upload" style="margin-left:20px;">上传</a>');
	$("#keywordForm #keywordFile").append('<a href="#" id="canceluploadKeywordFile" class="easyui-linkbutton" iconCls="icon-clear" style="margin-left:30px;">重置</a>');
	$.parser.parse($("#keywordForm #keywordFile"));
	
	//增删改按钮事件
	$(".strategyModelManage #addKeyword").bind('click',function(){
		$("#keywordDialog").addClass("addkeyword");
		formatKeywordDialog();
		$("#keywordDialog").panel('open',true);
	});
	$(".strategyModelManage #delKeyword").bind('click',function(){
		var row = $(".strategyModelManage #keywordGrid").datagrid('getSelected');
		if(null==row){
			msgShow("提示信息","未选中数据！","info");
		}else{
			$.ajax({
				type: 'GET',
				url: '/policyplatform/keyword/delKeyword?keywordid='+row.id,
				async: false,
				success: function(data){
					if(data.code==200){
						pagenationKeyword(1,10);
					}else{
						msgShow("提示信息",data.message,"info");
					}
				},
				error: function () {
					msgShow("连接错误","未连通服务器！","error");
				}
			});
		}
	});
	$(".strategyModelManage #editKeyword").bind('click',function(){
		$("#keywordDialog").removeClass("addkeyword");
		formatKeywordDialog();
		//被选中数据回填
		var row = $(".strategyModelManage #keywordGrid").datagrid('getSelected');
		if(null==row){
			msgShow("提示信息","未选中数据！","info");
		}else{
			$.ajax({
				type: 'GET',
				url: '/policyplatform/keyword/querySingleKeyword?keywordid='+row.id,
				async: false,
				success: function(data){
					if(data.code==200){
						editKeywordId = -1;
						editKeywordId = data.data.id;
						//数据回填
						setbackKeywordDiaolog(data.data);
						$("#keywordDialog").panel('open',true);
					}else{
						msgShow("提示信息",data.message,"info");
					}
				},
				error: function () {
					msgShow("连接错误","未连通服务器！","error");
				}
			})
		}
	});
	//文件上传
	$(".strategyModelManage #uploadKeyword").bind('click',function(){
		$("#keywordForm #keywordFile").form('clear');
		$("#keywordForm").panel('open',true);
	});
	$("#keywordFile #uploadKeywordFile").bind('click',function(){
		//可以将form表单整个直接传递请求中
		var form = new FormData($('#keywordForm #keywordFile')[0]);//FormData用于封装数据
//		var file = $('#keywordFile #filebox_file_id_1').val();// js 获取文件对象
//		form.append("file", file);
		$.ajax({  
            url:"/policyplatform/keyword/uploaddata",  
            type: 'POST',  
            data: form,
            async: false,
            cache: false,  
            contentType: false,  
            processData: false,  
            success: function (data) {
            	if(data.code == 200){
            		pagenationKeyword(1,10);
            	}else{
            		msgShow("上传文件失败",data.message,"info");
            	}
             },  
             error: function (data) {
            	 msgShow("连接错误","未连通服务器！","error");
             },
             complete:function(){
            	 $("#keywordForm").panel('close',true);
             }
        }); 
	});
	$("#keywordFile #canceluploadKeywordFile").bind('click',function(){
		$("#keywordForm #keywordFile").form('clear');
	});
	//初始化数据
	pagenationKeyword(1,10);
}
//分页插件
function pagenationKeyword(pageNumber,pageSize){
	var resultData = queryKeyword(pageSize,pageNumber);
	if(null!=resultData){
		if(200==resultData.code){
			$(".strategyModelManage #keywordGrid").datagrid('loadData',resultData.data.data);
			//重设pagenation
			//分页插件
			var pageplug = $(".strategyModelManage #keywordGrid").datagrid('getPager');
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
					pagenationKeyword(pageNumber, pageSize);
				},
				onRefresh:function(pageNumber, pageSize){//刷新之后触发
					pagenationKeyword(pageNumber, pageSize);
				},
				onChangePageSize:function(pageSize){//当用户改变页面尺寸时触发
					pagenationKeyword(pageNumber, pageSize);
				}
			});
		}else{
			msgShow("查询错误",resultData.message,"error");
		}
	}else{
		msgShow("连接错误","未连通服务器！","error");
	}
	return undefined;
}
function queryKeyword(pageSize,pageNumber){
	var da;
	var queryString = "page_number="+pageNumber+"&page_size="+pageSize;
	$.ajax({
		type: 'GET',
		url: '/policyplatform/keyword/queryKeyword?'+queryString,
		async: false,
		error: function () {
			msgShow("连接错误","未连通服务器！","error");
		}
	}).done(function(data){
		da = data;
	});
	return da;
}
//组织数据
function organizeKeywordData(){
	var data = {
		'keyword':$("#keywordDialog #keyword").val(),
		'servicetype':$("#keywordDialog #servicetype").combobox("getValue"),
		'kwclass':$("#keywordDialog #kwclass").combobox("getValue"),
		'timespan':$("#keywordDialog #timespan").numberspinner("getValue"),
		'unit':$("#keywordDialog #unit").combobox("getValue"),
		'monthreshold':$("#keywordDialog #monthreshold").numberspinner("getValue"),
		'filterthreshold':$("#keywordDialog #filterthreshold").numberspinner("getValue"),
		'abthreshold':$("#keywordDialog #abthreshold").numberspinner("getValue"),
		'optype':$("#keywordDialog #optype").combobox("getValue")
	};
	return data;
}
//格式化dialog面板
function formatKeywordDialog(){
	$("#keywordDialog #keyword").val("");
	$("#keywordDialog #servicetype").combobox("setValue",1);
	$("#keywordDialog #kwclass").combobox("setValue",1);
	$("#keywordDialog #timespan").numberspinner("setValue",1);
	$("#keywordDialog #unit").combobox("setValue",4);
	$("#keywordDialog #monthreshold").numberspinner("setValue",0);
	$("#keywordDialog #filterthreshold").numberspinner("setValue",0);
	$("#keywordDialog #abthreshold").numberspinner("setValue",0);
	$("#keywordDialog #optype").combobox("setValue",1);
	return undefined;
}
//将查询到的单个数据信息回填到dialog中去
function setbackKeywordDiaolog(keyword){
	$("#keywordDialog #keyword").val(keyword.keyword);
	$("#keywordDialog #servicetype").combobox("setValue",keyword.servicetype);
	$("#keywordDialog #kwclass").combobox("setValue",keyword.kwclass);
	$("#keywordDialog #timespan").numberspinner("setValue",keyword.timespan);
	$("#keywordDialog #unit").combobox("setValue",keyword.unit);
	$("#keywordDialog #monthreshold").numberspinner("setValue",keyword.monthreshold);
	$("#keywordDialog #filterthreshold").numberspinner("setValue",keyword.filterthreshold);
	$("#keywordDialog #abthreshold").numberspinner("setValue",keyword.abthreshold);
	$("#keywordDialog #optype").combobox("setValue",keyword.optype);
	return undefined;
}