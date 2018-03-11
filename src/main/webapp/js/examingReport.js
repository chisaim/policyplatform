//初始化html文件
function initexamingReport(){
	
	var he = $('div.examingReport').height();
	$(".examingReport #examingReportGrid").css("height",he + "px");
	
	//初始化表格
	initexamingReportGrid();

	//初始化违规级别下拉框
	initIllegalLevel();
	
	$(".examingReport #dateFrom").datebox("setValue", "2018-1-1");
	
	pagenationExamingReport(Date.parse(new Date("2018-1-1 00:00:00")),Date.parse(new Date($('.examingReport #dateTo').datebox('getValue')+" 23:59:59")),'',1,10);
	
	$('.examingReport #query-btn').bind('click',function(){
		$(".examingReport #examingReportGrid").datagrid('loadData',{ total: 0, rows: [] });
		var dateFrom = Date.parse(new Date($('.examingReport #dateFrom').datebox('getValue')+" 00:00:00"));
		var dataTo = Date.parse(new Date($('.examingReport #dateTo').datebox('getValue')+" 23:59:59"));
		var type = $('.examingReport #illegalLevel').combobox('getValue');
		var size = $('.examingReport #examingReportGrid').datagrid('getPager').pagination('options').pageSize;
		pagenationExamingReport(Date.parse(new Date("2018-1-1 00:00:00")),Date.parse(new Date($('.examingReport #dateTo').datebox('getValue')+" 23:59:59")),type,1,size);
	});
	
	$('.examingReport #export-btn').bind('click',function(){
		$(".examingReport #true_export-btn").attr("href","#");
		//更改真●下载链href属性，并点击真●下载链接来下载数据
		var dateFrom = Date.parse(new Date($('.examingReport #dateFrom').datebox('getValue')+" 00:00:00"));
		var dataTo = Date.parse(new Date($('.examingReport #dateTo').datebox('getValue')+" 23:59:59"));
		var type = $('.examingReport #illegalLevel').combobox('getValue');
		var querystr = "?from="+dateFrom+"&to="+dataTo+"&inspect_level="+type;
		$(".examingReport #true_export-btn").attr("href","/policyplatform/message/exportJunkdata"+querystr); 
		$(".examingReport #exportMessageJunkBtn").trigger('click');
	});
}

//初始化未审核短信报表表格
function initexamingReportGrid(){
	$('.examingReport #examingReportGrid').datagrid({
		fit:true,
	fitColumns:true,// 表宽度自适应
	rownumbers:true,// 显示行号
	toolbar:'.examingReport #tb',
	pagination:true,
	nowrap:false,
	columns:[[{field:'junk_id',hidden:'true'},
			  {field:'msg_id',hidden:'true'},
			  {field:'content',title:'短信内容',width:150,halign:'center',align:'left'},
			  {field:'advertiser',title:'广告主',width:50,align:'center'},
			  {field:'callerid',title:'主叫号码',width:50,align:'center'},
			  {field:'calledid',title:'被叫号码',width:50,align:'center'},
			  {field:'createdtime',title:'入库时间',width:60,align:'center',formatter:function(value,row,index){
				  return formTimestampValue(new Date(value));}},
			  {field:'content_origin',title:'原始短信内容',width:150,halign:'center',align:'left'},
			  {field:'inspect_level',title:'违规级别',width:30,align:'center'}
			]]
	});
}
function initIllegalLevel(){
	$('.examingReport #illegalLevel').combobox({
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
}
function pagenationExamingReport(dateFrom,dateTo,type,pageNumber, pageSize){
	var resultData = queryExamingMessage(dateFrom,dateTo,type,pageNumber, pageSize);
	if(null!=resultData){
		if(200==resultData.code){
			if(null==resultData.data||undefined ==resultData.data){
				msgShow("查询信息",resultData.message,"info");
			}else{
				$(".examingReport #examingReportGrid").datagrid('loadData',resultData.data.data);
				//重设pagenation
				//分页插件
				var pageplug = $(".examingReport #examingReportGrid").datagrid('getPager');
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
						pagenationExamingReport(Date.parse(new Date($('.examingReport #dateFrom').datebox('getValue')+" 00:00:00")),Date.parse(new Date($('.examingReport #dateTo').datebox('getValue')+" 23:59:59")),type,pageNumber, pageSize);
					},
					onRefresh:function(pageNumber, pageSize){//刷新之后触发
						pagenationExamingReport(Date.parse(new Date($('.examingReport #dateFrom').datebox('getValue')+" 00:00:00")),Date.parse(new Date($('.examingReport #dateTo').datebox('getValue')+" 23:59:59")),type,pageNumber, pageSize);
					},
					onChangePageSize:function(pageSize){//当用户改变页面尺寸时触发
						pagenationExamingReport(Date.parse(new Date($('.examingReport #dateFrom').datebox('getValue')+" 00:00:00")),Date.parse(new Date($('.examingReport #dateTo').datebox('getValue')+" 23:59:59")),type,1, pageSize);
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
function queryExamingMessage(dateFrom,dateTo,type,pageNumber, pageSize){
	var da;
	var queryString = "from="+dateFrom+"&to="+dateTo+"&inspect_level="+type+"&size="+pageSize+"&number="+pageNumber;
	$.ajax({
		type: 'GET',
		url: '/policyplatform/message/queryMessageJunk?'+queryString,
		async: false,
		error: function () {
			msgShow("连接错误","未连通服务器！","error");
		}
	}).done(function(data){
		da = data;
	});
	return da;
}