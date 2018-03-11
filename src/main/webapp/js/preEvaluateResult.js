function initPreEvaluateResult(){
	//页面框架加载开始
	var he = $('div#preEvaluateCenterSub').width();
	$("#preEvaluateCenterSub").layout('add',{
		id:"preEvaluateCWest",
	    region: 'west',
	    width: he/2,
	    split: true
	});
	$("#preEvaluateCenterSub").layout('add',{
		id:"preEvaluateCCenter",
	    region: 'center',
	    width: he/2,
	    split: true
	});
	$("#preEvaluateCWest").panel({
		content:'<table id="preEvaluateKeywordGrid" class="easyui-datagrid" data-options="collapsible:true"/>'
	});
	$("#preEvaluateCCenter").panel({
		content:'<table id="preEvaluateMessageGrid" class="easyui-datagrid" data-options="collapsible:true"/>'
	});
	
	$('#preEvaluateCCenter #preEvaluateMessageGrid').datagrid({
		fit:true,
		fitColumns:true,// 表宽度自适应
		rownumbers:true,// 显示行号
		pagination:false,
		nowrap:false,
		singleSelect:true,
		columns:[[{field:'audited_id',hidden:'true'},
		          {field:'content',title:'短信内容',width:150,halign:'center',align:'left'},
		          {field:'auditedresult',title:'审核结果',width:50,align:'center',formatter:function(value){
		        	  return value==0?"正常":"垃圾";
		          }},
		          {field:'createdate',title:'入库时间',width:60,align:'center',formatter:function(value,row,index){
		        	  return formTimestampValue(new Date(value));}}
		]],
	});
	$('#preEvaluateCWest #preEvaluateKeywordGrid').datagrid({
		fit:true,
		fitColumns:true,// 表宽度自适应
		rownumbers:true,// 显示行号
		pagination:true,
		nowrap:false,
		singleSelect:true,
		columns:[[{field:'ck',checkbox:"true",width:40,align:'left'},
		          {field:'keyword_id',hidden:'true'},
		          {field:'pre_message_ids',hidden:'true'},
		          {field:'keyword',title:'关键词',width:150,halign:'center',align:'left'},
		          {field:'recall_ratio',title:'查准率',width:50,align:'center',formatter:function(value){
		        	 return formatDouble(value);
		          }},
		          {field:'precision_ratio',title:'查全率',width:50,align:'center',formatter:function(value){
		        	 return formatDouble(value);
		          }},
		          {field:'target_num',title:'命中短信样本数',width:50,align:'center'},
		          {field:'total_num',title:'总短信样本数',width:50,align:'center'},
		          {field:'create_time',title:'入库时间',width:60,align:'center',formatter:function(value,row,index){
		        	  return formTimestampValue(new Date(value));}}
		]],
		onSelect:function(rowIndex, rowData){
			if(rowData.pre_message_ids==null){
				//无数据不做操作
			}else{
				var url = '/policyplatform/evaluateResult/queryPreMessages?message_ids='+rowData.pre_message_ids;
				var data = queryPreData(url);
				if(data.code!=200){
					msgShow("提示信息",data.message,"info");
				}else{
					$('#preEvaluateCCenter #preEvaluateMessageGrid').datagrid('loadData',{ total: 0, rows: [] });
					$('#preEvaluateCCenter #preEvaluateMessageGrid').datagrid('loadData',data.data);
				}
			}
		}
	});
	initPreEvaluateResultGrid();
	initPreTaskDialog();
	//页面框架加载结束
	
	$("#preEvaluateNorth #addPreEvaluateTask").bind('click',function(){
		$("#preTaskDialog").panel('open',true);
	});
	
	$("#canclePreTask").bind('click',function(){
		$("#preTaskDialog").panel('close',true);
	});
	
	$("#savePreTask").bind('click',function(){
		var preMessageFileid = $('#preTaskDialog #premessageSelect').combobox('getValue');
		var preKeywordFileid = $('#preTaskDialog #prepolicySelect').combobox('getValue');
		if(preKeywordFileid==""||""==preMessageFileid){
			msgShow("参数错误","入参不能为空！","error");
		}else{
			$("#preTaskDialog").panel('close',true);
			msgShow("提示信息","执行预评估需要一定时间，请稍后再来查看结果！","info");
			$.ajax({
				type: 'GET',
				url: '/policyplatform/evaluateResult/executePreEvaluate?preMFid='+preMessageFileid+"&preKFid="+preKeywordFileid,
				async: false,
				success: function (data) {
					console.log("调用接口执行程序成功！");
				},
				error: function () {
					console.log("调用接口执行程序失败！");
				}
			});
		}
	});
	
	initPreEvaluateResultData(1,10);
	
	$("#preEvaluateNorth #refreshPreEvaluateTask").bind('click',function(){
		initPreEvaluateResultData(1,10);
	});
}

function initPreEvaluateResultGrid(){
	$('#preEvaluateNorth #preEvaluateResultGrid').datagrid({
		fit:true,
		fitColumns:true,// 表宽度自适应
		rownumbers:true,// 显示行号
		pagination:true,
		nowrap:false,
		toolbar:"#preEvaluateNorth #tb",
		singleSelect:true,
		columns:[[{field:'ck',checkbox:"true",width:40,align:'left'},
		          {field:'taskid',hidden:'true'},
		          {field:'prepolicy_fileid',hidden:'true'},
		          {field:'prepolicy_filename',title:'策略文件名',width:100,align:'center'},
		          {field:'prepolicy_number',title:'策略数据量',width:30,align:'center'},
		          {field:'premessage_fileid',hidden:'true'},
		          {field:'premessage_filename',title:'短信文件名',width:100,align:'center'},
		          {field:'premessage_number',title:'短信数据量',width:30,align:'center'},
		          {field:'target_message',title:'命中短信样本数',width:30,align:'center'},
		          {field:'target_policy',title:'命中短信策略数',width:30,align:'center'},
		          {field:'recall_ratio',title:'查全率',width:30,align:'center',formatter:function(value){
		        	 return formatDouble(value);
		          }},
		          {field:'precision_ratio',title:'查准率',width:30,align:'center',formatter:function(value){
		        	 return formatDouble(value);
		          }},
		          {field:'task_starttime',title:'任务开始时间',width:80,align:'center',formatter:function(value,row,index){
		        	  return formTimestampValue(new Date(value));}},
		          {field:'task_endtime',title:'任务结束时间',width:80,align:'center',formatter:function(value,row,index){
		        	  return formTimestampValue(new Date(value));}},
		          {field:'task_status',title:'任务状态',width:50,align:'center'},
		          {field:'username',title:'用户名',width:50,align:'center'},
		          
		]],
		onSelect:function(rowIndex, rowData){
			//根据taskid查询关键字
			pagenationQueryDataByTaskid(rowData.taskid,1,10);
		}
	});
}

function initPreTaskDialog(){
	$("#preTaskDialog").dialog({
	    title: '预评估任务信息',
	    width: 470,
	    height: 180,
	    closed: true,
	    cache: false,
	    buttons:[{id:'savePreTask',iconCls:'icon-save',text:'Save'},
	             {id:'canclePreTask',iconCls:'icon-cancel',text:'Cancle'}],
	    content:'<div id="preTaskContent"></div>'
	});
	$("#preTaskDialog #preTaskContent").append("</br>&nbsp;&nbsp;&nbsp;&nbsp;<a style='margin-left:5px;margin-top:5px;color:black;'>请选择预评估策略文件：</a><input id='prepolicySelect' style='width:280px;'>");
	$("#preTaskDialog #preTaskContent").append("</br></br>&nbsp;&nbsp;&nbsp;&nbsp;<a style='margin-left:5px;margin-top:5px;color:black;'>请选择预评估短信文件：</a><input name='premessageSelect' id='premessageSelect' style='width:280px;'>");
	
	var url = '/policyplatform/evalute/queryPreMessageFileInfo?from='+Date.parse(new Date("2018-1-1 00:00:00"))+"&to="+Date.parse(new Date())+"&filename=&size=1000&number=1";
	var dataPreSms = queryPreData(url);
	if(dataPreSms.code!=200){
		msgShow("提示信息",dataPreSms.message,"info");
	}else{
		$('#preTaskDialog #premessageSelect').combobox({
			panelHeight:'auto',
			editable:false,
			valueField:'fileid',
			textField:'filename',
			data:dataPreSms.data.data
		});
	}
	
	url = '/policyplatform/evalute/queryPreKeywordFileInfo?from='+Date.parse(new Date("2018-1-1 00:00:00"))+"&to="+Date.parse(new Date())+"&filename=&size=1000&number=1";
	var dataPreKeyword = queryPreData(url);
	if(dataPreKeyword.code!=200){
		msgShow("提示信息",dataPreKeyword.message,"info");
	}else{
		$('#preTaskDialog #prepolicySelect').combobox({
			panelHeight:'auto',
			editable:false,
			valueField:'fileid',
			textField:'filename',
			data:dataPreKeyword.data.data
		});
	}
	$.parser.parse($("#preTaskDialog #preTaskContent"));
}

function queryPreData(url){
	var da;
	$.ajax({
		type: 'GET',
		url: url,
		async: false,
		error: function () {
			msgShow("连接错误","未连通服务器！","error");
		}
	}).done(function(data){
		da = data;
	});
	return da;
}

function initPreEvaluateResultData(page,size){
	var resultData = queryPreData('/policyplatform/evaluateResult/queryPreResult?page='+page+'&size='+size);
	if(null!=resultData){
		if(200==resultData.code){
			if(null==resultData.data||undefined ==resultData.data){
				msgShow("查询信息",resultData.message,"info");
			}else{
				$('#preEvaluateNorth #preEvaluateResultGrid').datagrid('loadData',resultData.data.data);
				//重设pagenation
				//分页插件
				var pageplug = $('#preEvaluateNorth #preEvaluateResultGrid').datagrid('getPager');
				$(pageplug).pagination({
					total:resultData.data.total,					//记录总数,需要查库才能得到
					pageSize:size,				//页面尺寸
					pageNumber:page,			//创建分页（pagination）时显示的页码
					pageList:[10,20,30], 		//用户能改变页面尺寸。pageList 属性定义了能改成多大的尺寸
					//	loading:false					//定义数据是否正在加载
					//	buttons:						//自定义按钮
					showPageList:true,				//定义是否显示页面列表
					showRefresh:true,				//定义是否显示刷新按钮
					onSelectPage:function(page, size){//当用户选择新的页面时触发
						initPreEvaluateResultData(page, size);			
					},
					onRefresh:function(page, size){//刷新之后触发
						initPreEvaluateResultData(page, size);
					},
					onChangePageSize:function(size){//当用户改变页面尺寸时触发
						initPreEvaluateResultData(1, size);
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

function pagenationQueryDataByTaskid(taskid,page,size){
	var resultData = queryPreData('/policyplatform/evaluateResult/queryDataByTaskid?taskid='+taskid+'&page='+page+'&size='+size);
	if(null!=resultData){
		if(200==resultData.code){
			if(null==resultData.data||undefined ==resultData.data){
				msgShow("查询信息",resultData.message,"info");
			}else{
				$('#preEvaluateCWest #preEvaluateKeywordGrid').datagrid('loadData',resultData.data.data);
				//重设pagenation
				//分页插件
				var pageplug = $('#preEvaluateCWest #preEvaluateKeywordGrid').datagrid('getPager');
				$(pageplug).pagination({
					total:resultData.data.total,					//记录总数,需要查库才能得到
					pageSize:size,				//页面尺寸
					pageNumber:page,			//创建分页（pagination）时显示的页码
					pageList:[10,20,30], 		//用户能改变页面尺寸。pageList 属性定义了能改成多大的尺寸
					//	loading:false					//定义数据是否正在加载
					//	buttons:						//自定义按钮
					showPageList:true,				//定义是否显示页面列表
					showRefresh:true,				//定义是否显示刷新按钮
					onSelectPage:function(page, size){//当用户选择新的页面时触发
						pagenationQueryDataByTaskid(taskid,page, size);			
					},
					onRefresh:function(page, size){//刷新之后触发
						pagenationQueryDataByTaskid(taskid,page, size);
					},
					onChangePageSize:function(size){//当用户改变页面尺寸时触发
						pagenationQueryDataByTaskid(taskid,1, size);
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