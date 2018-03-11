function initEvaluateResult(){
	
	var he = $('#evaluateResult').height();
	$("#evaluateResult #evaluateResultGrid").css("height",he + "px");
	
	//初始化表格
	initEvaluateResultGrid();
	
	initEvaluateMessageGrid();
	
	//初始化加载数据
	pagenationEvaluateResultGrid(1,10);
}

function initEvaluateResultGrid(){
	$('#evaluateNorth #evaluateResultGrid').datagrid({
		fit:true,
		fitColumns:true,// 表宽度自适应
		rownumbers:true,// 显示行号
		pagination:true,
		nowrap:false,
		singleSelect:true,
		columns:[[{field:'ck',checkbox:"true",width:40,align:'left'},
		          {field:'keyword_id',hidden:'true'},
		          {field:'audited_ids',hidden:'true'},
		          {field:'keyword',title:'关键词',width:150,halign:'center',align:'left'},
		          {field:'recall_ratio',title:'查全率',width:50,align:'center'},
		          {field:'precision_ratio',title:'查准率',width:50,align:'center'},
		          {field:'target_num',title:'命中短信样本数',width:50,align:'center'},
		          {field:'total_num',title:'总短信样本数',width:50,align:'center'},
		          {field:'create_time',title:'入库时间',width:60,align:'center',formatter:function(value,row,index){
		        	  return formTimestampValue(new Date(value));}}
		]],
		onSelect:function(rowIndex, rowData){
			var audited_ids = rowData.audited_ids;
			if(""==audited_ids||null==audited_ids||undefined==audited_ids){
				msgShow("查询信息","未命中短信","info");
			}else{
				$.ajax({
				 	type: 'GET',
				 	url: '/policyplatform/evaluateResult/queryTargetMessage?audited_ids='+audited_ids,
				 	async: false,
				 	success: function (data) {
				 		if(data.code == 200){
				 			$('#evaluateCenter #evaluateMessageGrid').datagrid('loadData',data.data);
				 		}else {
							msgShow("查询",data.message,"info");
						}
				 	},
				 	error: function () {
				 		msgShow("连接错误","获取用户菜单失败！","error");
				 	}
				 });
			}
		}
	});
}

function initEvaluateMessageGrid(){
	$('#evaluateCenter #evaluateMessageGrid').datagrid({
		fit:true,
		fitColumns:true,// 表宽度自适应
		rownumbers:true,// 显示行号
		pagination:false,
		nowrap:false,
		singleSelect:true,
		columns:[[{field:'audited_id',hidden:'true'},
		          {field:'content',title:'短信内容',width:150,halign:'center',align:'left'},
		          {field:'auditedresult',title:'审核结果',width:50,align:'center'},
		          {field:'verifytime',title:'入库时间',width:60,align:'center',formatter:function(value,row,index){
		        	  return formTimestampValue(new Date(value));}}
		]],
	});
}

function pagenationEvaluateResultGrid(page,size){
	var resultData = queryEvaluateResult(page,size);
	if(null!=resultData){
		if(200==resultData.code){
			if(null==resultData.data||undefined ==resultData.data){
				msgShow("查询信息",resultData.message,"info");
			}else{
				var data = resultData.data;
				$('#evaluateNorth #evaluateResultGrid').datagrid('loadData',data.data);
				//重设pagenation
				//分页插件
				var pageplug = $('#evaluateNorth #evaluateResultGrid').datagrid('getPager');
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
						pagenationEvaluateResultGrid(pageNumber, pageSize);
					},
					onRefresh:function(pageNumber, pageSize){//刷新之后触发
						pagenationEvaluateResultGrid(pageNumber, pageSize);
					},
					onChangePageSize:function(pageSize){//当用户改变页面尺寸时触发
						pagenationEvaluateResultGrid(1, pageSize);
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

function queryEvaluateResult(page,size){
	var da;
	var queryString = "&size="+size+"&page="+page;
	$.ajax({
		type: 'GET',
		url: '/policyplatform/evaluateResult/queryEvaluateResult?'+queryString,
		async: false,
		error: function () {
			msgShow("连接错误","未连通服务器！","error");
		}
	}).done(function(data){
		da = data;
	});
	return da;
}