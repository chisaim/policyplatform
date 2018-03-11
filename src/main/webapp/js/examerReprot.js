//初始化html文件
$(function(){
	
	var he = $('div.examerReport').height();
	$(".examerReport #examerReportGrid").css("height",he + "px");
	
	//初始化表格
	initExamerReportGrid('.examerReport #examerReportGrid');

	initGridToolExamer();

	$('.examerReport #query-btn').bind('click',function(){
		var from = Date.parse(new Date($('.examerReport #dateFrom').datebox('getValue')+" 00:00:00"));
		var to = Date.parse(new Date($('.examerReport #dateTo').datebox('getValue')+" 00:00:00"));
		var userid = $('.examerReport #examer').combobox('getValue');
		pagenationQueryExamerData(from,to,userid,1,10);
	});
	
	$('.examerReport #export-btn').bind('click',function(){
		$(".examerReport #true_export-btn").attr("href","#");
		//更改真●下载链href属性，并点击真●下载链接来下载数据
		var from = Date.parse(new Date($('.examerReport #dateFrom').datebox('getValue')+" 00:00:00"));
		var to = Date.parse(new Date($('.examerReport #dateTo').datebox('getValue')+" 00:00:00"));
		var userid = $('.examerReport #examer').combobox('getValue');
		var querystr = "?from="+from+"&to="+to+"&userid="+userid;
		$(".examerReport #true_export-btn").attr("href","/policyplatform/message/exportExamerReport"+querystr); 
		$(".examerReport #exportExamerReport").trigger('click');
	});
	
	$('.examerReport #query-btn').trigger('click');
})

//初始化审核人员报表表格
function initExamerReportGrid(eleName){
	var strs= new Array(); //定义一数组
	strs=eleName.split(" ");
	var str = strs[0]+' #tb';
	$(eleName).datagrid({
		fit:true,
	fitColumns:true,// 表宽度自适应
	rownumbers:true,// 显示行号
	toolbar:str,
	pagination:true,
	columns:[[{field:'id',hidden:'true'},
	          {field:'userid',hidden:'true'},
	          {field:'username',title:'审核人',width:80,align:'center'},
	{field:'number',title:'审核数据量',width:60,align:'center'},
	{field:'date',title:'审核日期',width:80,align:'center',formatter:function(value){
  	  return dataformatter(new Date(value));}},
	{field:'createtime',title:'入库时间',width:100,align:'center',formatter:function(value){
  	  return formTimestampValue(new Date(value));}}]]});
}

function initGridToolExamer(){
	//初始化审核人
	//利用ajax获取审核人的相关数据，再加载进data里面
	var from = Date.parse(new Date($('.examerReport #dateFrom').datebox('getValue')+" 00:00:00"));
	var to = Date.parse(new Date($('.examerReport #dateTo').datebox('getValue')+" 00:00:00"));
	var queryString = "from="+from+"&to="+to;
	$.ajax({
	 	type: 'GET',
	 	url: '/policyplatform/message/getExamUserInfo?'+queryString,
	 	async: false,
	 	success: function (data) {
	 		if(data.code == 200){
	 			var arr = data.data;
//	 			var object = new Object();
//	 			object.id="";
//	 			object.username="";
//	 			arr.push(object);
	 			$('.examerReport #examer').combobox({
	 				panelHeight:'auto',
	 				valueField:'id',
	 				textField:'username',
	 				data:arr
	 			});
	 			$('.examerReport #examer').combobox('setValue',arr[0].id);
	 		}else {
				msgShow("查询",data.message,"info");
			}
	 	},
	 	error: function () {
	 		msgShow("连接错误","获取用户菜单失败！","error");
	 	}
	 });
}

function pagenationQueryExamerData(from,to,userid,page,size){
	var resultData = queryExamerData(from,to,userid,page, size);
	if(null!=resultData){
		if(200==resultData.code){
			if(null==resultData.data||undefined ==resultData.data){
				msgShow("查询信息",resultData.message,"info");
			}else{
				$('.examerReport #examerReportGrid').datagrid('loadData',resultData.data.data);
				//重设pagenation
				//分页插件
				var pageplug = $('.examerReport #examerReportGrid').datagrid('getPager');
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
						pagenationQueryExamerData(Date.parse(new Date($('.examerReport #dateFrom').datebox('getValue')+" 00:00:00")),Date.parse(new Date($('.examerReport #dateTo').datebox('getValue')+" 00:00:00")),$('.examerReport #examer').combobox('getValue'),pageNumber, pageSize);
					},
					onRefresh:function(pageNumber, pageSize){//刷新之后触发
						pagenationQueryExamerData(Date.parse(new Date($('.examerReport #dateFrom').datebox('getValue')+" 00:00:00")),Date.parse(new Date($('.examerReport #dateTo').datebox('getValue')+" 00:00:00")),$('.examerReport #examer').combobox('getValue'),pageNumber, pageSize);
					},
					onChangePageSize:function(pageSize){//当用户改变页面尺寸时触发
						pagenationQueryExamerData(Date.parse(new Date($('.examerReport #dateFrom').datebox('getValue')+" 00:00:00")),Date.parse(new Date($('.examerReport #dateTo').datebox('getValue')+" 00:00:00")),$('.examerReport #examer').combobox('getValue'),1, pageSize);
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

function queryExamerData(from,to,userid,page,size){
	var da;
	var queryString = "from="+from+"&to="+to+"&userid="+userid+"&size="+size+"&page="+page;
	$.ajax({
		type: 'GET',
		url: '/policyplatform/message/queryExamerData?'+queryString,
		async: false,
		error: function () {
			msgShow("连接错误","未连通服务器！","error");
		}
	}).done(function(data){
		da = data;
	});
	return da;
}