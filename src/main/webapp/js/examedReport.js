//初始化html文件
function initExamedReport(){
	
	var he = $('div.examedReport').height();
	$(".examedReport #examedReportGrid").css("height",he + "px");
	
	//初始化表格
	initExamedReportGrid();

	//初始化违规级别下拉框
	initIllegalLevel();

	//初始化审核结果下拉框
	initExamedResult();

	$(".examedReport #dateFrom").datebox("setValue", "2018-1-1");   
	
	pagenationExamedReport(Date.parse(new Date("2018-1-1 00:00:00")),Date.parse(new Date($('.examedReport #dateTo').datebox('getValue')+" 23:59:59")),'','',1,10);

	$('.examedReport #query-btn').bind('click',function(){
		$('.examedReport #examedReportGrid').datagrid('loadData',{ total: 0, rows: [] });
		var dateFrom = Date.parse(new Date($('.examedReport #dateFrom').datebox('getValue')+" 00:00:00"));
		var dataTo = Date.parse(new Date($('.examedReport #dateTo').datebox('getValue')+" 23:59:59"));
		var type = $('.examedReport #illegalLevel').combobox('getValue');
		var resultType = $('.examedReport #examedResult').combobox('getValue');
		var size = $('.examedReport #examedReportGrid').datagrid('getPager').pagination('options').pageSize;
		pagenationExamedReport(Date.parse(new Date($('.examedReport #dateFrom').datebox('getValue')+" 00:00:00")),Date.parse(new Date($('.examedReport #dateTo').datebox('getValue')+" 23:59:59")),type,resultType,1,size);
	});
	
	$('.examedReport #export-btn').bind('click',function(){
		$(".examedReport #true_export-btn").attr("href","#");
		//更改真●下载链href属性，并点击真●下载链接来下载数据
		var dateFrom = Date.parse(new Date($('.examedReport #dateFrom').datebox('getValue')+" 00:00:00"));
		var dataTo = Date.parse(new Date($('.examedReport #dateTo').datebox('getValue')+" 23:59:59"));
		var type = $('.examedReport #illegalLevel').combobox('getValue');
		var resultType = $('.examedReport #examedResult').combobox('getValue');
		var querystr = "?from="+dateFrom+"&to="+dataTo+"&inspect_level="+type+"&auditedresult="+resultType;
		$(".examedReport #true_export-btn").attr("href","/policyplatform/message/exportdata"+querystr); 
		$(".examedReport #exportMessageAuditedBtn").trigger('click');
	});
}

function pagenationExamedReport(dateFrom,dateTo,type,resulttype,pageNumber, pageSize){
	var resultData = queryExamedMessage(dateFrom,dateTo,type,resulttype,pageNumber, pageSize);
	if(null!=resultData){
		if(200==resultData.code){
			if(null==resultData.data||undefined ==resultData.data){
				msgShow("查询信息",resultData.message,"info");
			}else{
				debugger;
				$('.examedReport #examedReportGrid').datagrid('loadData',resultData.data.data);
				//重设pagenation
				//分页插件
				var pageplug = $('.examedReport #examedReportGrid').datagrid('getPager');
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
						pagenationExamedReport(Date.parse(new Date($('.examedReport #dateFrom').datebox('getValue')+" 00:00:00")),Date.parse(new Date($('.examedReport #dateTo').datebox('getValue')+" 23:59:59")),type,resulttype,pageNumber, pageSize);
					},
					onRefresh:function(pageNumber, pageSize){//刷新之后触发
						pagenationExamedReport(Date.parse(new Date($('.examedReport #dateFrom').datebox('getValue')+" 00:00:00")),Date.parse(new Date($('.examedReport #dateTo').datebox('getValue')+" 23:59:59")),type,resulttype,pageNumber, pageSize);
					},
					onChangePageSize:function(pageSize){//当用户改变页面尺寸时触发
						pagenationExamedReport(Date.parse(new Date($('.examedReport #dateFrom').datebox('getValue')+" 00:00:00")),Date.parse(new Date($('.examedReport #dateTo').datebox('getValue')+" 23:59:59")),type,resulttype,1, pageSize);
					}
				});
			}
		}else{
			msgShow("查询错误",resultData.message,"error");
		}
	}else{
		msgShow("连接错误","未连通服务器！","error");
	}
	return undefined;
}

function queryExamedMessage(dateFrom,dateTo,type,resulttype,pageNumber, pageSize){
	var da;
	var queryString = "from="+dateFrom+"&to="+dateTo+"&inspect_level="+type+"&auditedresult="+
			resulttype+"&size="+pageSize+"&number="+pageNumber;
	$.ajax({
		type: 'GET',
		url: '/policyplatform/message/queryMessageAudited?'+queryString,
		async: false,
		error: function () {
			msgShow("连接错误","未连通服务器！","error");
		}
	}).done(function(data){
		da = data;
	});
	return da;
}
//初始化已审核短信报表表格
function initExamedReportGrid(){
	$('.examedReport #examedReportGrid').datagrid({
		fit:true,
		fitColumns:true,// 表宽度自适应
		rownumbers:true,// 显示行号
		toolbar:'.examedReport #tb',
		pagination:true,
		nowrap:false,
		columns:[[{field:'audited_id',hidden:'true'},
		          {field:'msg_id',hidden:'true'},
		          {field:'content',title:'短信内容',width:150,halign:'center',align:'left'},
		          {field:'auditedresult',title:'审核结果',width:50,align:'center'},
		          {field:'advertiser',title:'广告主',width:50,align:'center'},
		          {field:'callerid',title:'主叫号码',width:50,align:'center'},
		          {field:'calledid',title:'被叫号码',width:50,align:'center'},
			      {field:'content_origin',title:'原始短信内容',width:150,halign:'center',align:'left'},
		          {field:'inspect_level',title:'违规级别',width:50,align:'center'},
		          {field:'operator',title:'审核人员',width:30,align:'center'},
		          {field:'repeatcount',title:'重复次数',width:30,align:'center'},
		          {field:'verifytime',title:'审核时间',width:60,align:'center',formatter:function(value,row,index){
		        	  return formTimestampValue(new Date(value));}}
		]]
	});
}
//初始化违规级别下拉框
function initIllegalLevel(){
	$('.examedReport #illegalLevel').combobox({
		panelHeight:'auto',
		valueField:'setValue',
		textField:'setText',
		data:[{setValue:'',setText:'全部'},
		      {setValue:1,setText:'监控'},
		      {setValue:2,setText:'拦截'},
		      {setValue:3,setText:'加黑'}]
	});
}
//初始化审核结果下拉框
function initExamedResult(){
	$('.examedReport #examedResult').combobox({
		panelHeight:'auto',
		valueField:'setValue',
		textField:'setText',
		data:[{setValue:'',setText:'全部'},
		      {setValue:0,setText:'正常'},
		      {setValue:1,setText:'垃圾'}]
	});
}