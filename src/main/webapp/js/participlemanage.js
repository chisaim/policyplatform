$(function(){
	initParticiple();
});

function initParticiple(){
	//初始化表格
	$('.participle #participleGrid').datagrid({
		fit:true,
		fitColumns:true,// 表宽度自适应
		rownumbers:true,// 显示行号
		toolbar:".participle #head",
		pagination:true,
		columns:[[{field:'ck',checkbox:"true",width:40,align:'left'},
		          {field:'name',title:'词名称',width:180,align:'left'},
		          {field:'participle_type',title:'分词类型',width:180,align:'left'},
		          {field:'operator',title:'操作账号',width:180,align:'left'},
		          {field:'pcreate_time',title:'上传时间',width:180,align:'right',
		        	  formatter:function(value){
		        		  var time=new Date(value);
		        		  var y = time.getFullYear();
		        		  var m = time.getMonth() + 1;
		        		  var d = time.getDate();
		        		  var h =time.getHours();
		        		  var M	= time.getMinutes();
		        		  var s	=time.getSeconds();
		        		  return y + '-' +m + '-' + d+' '+h+':'+M+':'+s; 
		        	  }},
		        {field:'remark',title:'备注',width:180,align:'right'},
		        {field:'word_id',hidden:'true'}]]});
	//分页插件
	pagenationAction(1,10);	
	//初始化弹窗
	initWindow();
	//初始化短信检出时间
	$("#participleDialog #Pcreate_time").datetimebox({
		value: 'currentDate',
	    required: true,
	    showSeconds: true,
	    formatter: function(value){return formTimestampValue(value);},
	    timeSeparator:':'
	});
	//取消按钮
	$("#participleDialog #cancelMessage-btn").bind('click',function(){
		$("#participleDialog").panel('close',true);
	});
	//格式化弹出窗口的填充数据
	$("#head #Padd").bind('click',function(){
		$("#participleDialog #name").val("");
		$("#participleDialog #participle_type").val("");
		$("#participleDialog #Pcreate_time").datetimebox("setValue",'currentDate');
		$("#participleDialog #operator").val("");
		$("#participleDialog #remark").val("");
		
		$("#participleDialog").addClass("addMessage");
		$("#participleDialog").panel('open',true);
	});
	//根据选中词条的ID返回当前词条信息
	$(".participle #Pedit").bind('click',function(){
		var row=$(".participle #participleGrid").datagrid('getSelected');
		if(row==null){
			msgShow("操作错误","请选中数据！","error");
		}else{
			$.ajax({
				type:'GET',
				url:'/policyplatform/Participle/getParticiple?id='+row.id,
				async: false,
				success:function(data){
					if(data.code==200){
						$("#participleDialog #name").val(data.data.name);
						$("#participleDialog #participle_type").val(data.data.participle_type);
						$("#participleDialog #Pcreate_time").datetimebox("setValue",formTimestampValue(new Date(data.data.Pcreate_time)));
						$("#participleDialog #operator").val(data.data.operator);
						$("#participleDialog #remark").val(data.data.remark);
						
						$("#participleDialog").removeClass("addMessage");
						$("#participleDialog").panel('open',true);
					}else{
						msgShow("查询错误",data.message,"error");
					}
				},
				error:function(){
					msgShow("连接错误","未连通服务器！","error");
				}
			});
		}
	});
	//判断窗口是否包含addMessage，如有则为添加，没有为修改
	$("#participleDialog #saveMessage-btn").bind('click',function(){
		var data=GetMessageDate();
		if($("#participleDialog").hasClass("addMessage")){
			$.ajax({
				type:'POST',
				url:'/policyplatform/Participle/addParticiple',
				aysnc:false,
				contentType:"application/json; charset=utf-8",
				data:JSON.stringify(data),
				success:function(data){
					if(data.code==200){
						pagenationAction(1,10);
					}else{
						msgShow("查询错误",data.message,"error");
					}
				},
				error:function(){
					msgShow("连接错误","未连通服务器！","error");	
				}
			});
		}else{
			$.ajax({
				type:'POST',
				url:'/policyplatform/Participle/editParticiple',
				aysnc:false,
				contentType:"application/json; charset=utf-8",
				data:JSON.stringify(data),
				success:function(data){
					if(data.code == 200){
						pagenationAction(1,10);
					}else{
						msgShow("查询错误",data.message,"error");
					}
				},
				error:function(){
					msgShow("连接错误","未连通服务器！","error");	
				}
			});
		}
		$("#participleDialog").panel('close',true);
	});
	
	$(".participle #Premove").bind('click',function(){
		var row=$(".participle #participleGrid").datagrid('getSelected');
		if(row!=null){
			$.ajax({
				type:'POST',
				url:'/policyplatform/Participle/deleteParticiple?id='+row.id,
				async:false,
				success:function(data){
					if(data.code==200){
						pagenationAction(1,10);
					}else{
						msgShow("查询错误",data.message,"error");
					}
				},
				error:function(){
						msgShow("连接错误","未连通服务器！","error");	
				}
			});
		}else{
			msgShow("操作错误","请选中数据！","error");
		}
	});
	
	//文件上传
	$(".participle #Pupload").bind('click',function(){
		$("#participleUpload #keywordFile").form('clear');
		$("#participleUpload").panel('open',true);
	});
	
	$("#participleUpload").dialog({
	    title: '关键词策略文件上传',
	    width: 270,
	    height: 140,
	    closed: true,
	    cache: false,
	    content:'<form id="keywordFile" method="post" style="margin-left:30px;"></form>'
	});
	$("#participleUpload #keywordFile").append('</br>');
	$("#participleUpload #keywordFile").append('<input class="easyui-filebox" data-options="prompt:\'Choose a file...\'" name="file" style="width:200px;">');
	$("#participleUpload #keywordFile").append('</br></br>');
	$("#participleUpload #keywordFile").append('<a href="#" id="uploadKeywordFile" class="easyui-linkbutton" iconCls="icon-upload" style="margin-left:20px;">上传</a>');
	$("#participleUpload #keywordFile").append('<a href="#" id="canceluploadKeywordFile" class="easyui-linkbutton" iconCls="icon-clear" style="margin-left:30px;">重置</a>');
	$.parser.parse($("#participleUpload #keywordFile"));
	
	$("#keywordFile #uploadKeywordFile").bind('click',function(){
		//可以将form表单整个直接传递请求中
		var form = new FormData($('#participleUpload #keywordFile')[0]);//FormData用于封装数据
		$.ajax({  
            url:"/policyplatform/Participle/uploadParticiple",  
            type: 'POST',  
            data: form,
            async: false,
            cache: false,  
            contentType: false,  
            processData: false,  
            success: function (data) {
            	if(data.code == 200){
            		pagenationAction(1,10);
            	}else{
            		msgShow("上传文件失败",data.message,"info");
            	}
             },  
             error: function (data) {
            	 msgShow("连接错误","未连通服务器！","error");
             },
             complete:function(){
            	 $("#participleUpload").panel('close',true);
             }
        }); 
	});
	$("#keywordFile #canceluploadKeywordFile").bind('click',function(){
		$("#participleUpload #keywordFile").form('clear');
	});
}

function GetMessageDate(){
	var data = {			
		"name":$("#participleDialog #name").val(),
		"participle_type":$("#participleDialog #participle_type").val(),
		"Pcreate_time":Date.parse($("#participleDialog #Pcreate_time").datetimebox("getValue")),
		"operator":$("#participleDialog #operator").val(),
		"remark":$("#participleDialog #remark").val(),
		"id":$(".participle #participleGrid").datagrid('getSelected')==null?-1:$(".participle #participleGrid").datagrid('getSelected').id
	};
	return data;
}

function initWindow(){
	var str='<a style="margin-left:10px;">词名称</a><br/>'+
			'<input id="name" style="width:150px;margin-left:10px;margin-top:5px;"><br/>'+
			'<a style="margin-left:10px;">分词类型</a><br/>'+
			'<input id="participle_type" style="width:150px;margin-left:10px;margin-top:5px;"><br/>'+
			'<a style="margin-left:10px;">上传时间</a><br/>&nbsp;&nbsp;'+
			'<input id="Pcreate_time" style="width:150px;margin-left:10px;margin-top:5px;"><br/>'+
			'<a style="margin-left:10px;">备注</a><br/>'+
			'<textarea rows="5" cols="40" id="remark" style="margin-left:10px;margin-top:5px;"></textarea><br/>'+
			'<a href="#" class="easyui-linkbutton" id="saveMessage-btn" style="margin-right:20px;width:40px;margin-left:80px; position: absolute;">保存</a>'+
			'<a href="#" class="easyui-linkbutton" id="cancelMessage-btn" style="margin-left:140px;width:40px;">取消</a>';
	$('#participleDialog').dialog({		
	    title: '分词详细信息',
	    width: 300,
	    height: 450,
	    closed: true,
	    cache: false,
	    content:str,
	    modal: true,
	});	
}

function pageInfo(pageSize,pageNumber){
	var info;
	var queryString= "&pageSize="+pageSize+"&pageNumber="+pageNumber;
	$.ajax({
		type: 'GET',
		url: '/policyplatform/Participle/pageInfo?'+queryString,
		async: false,
		error:function(){
			msgShow("连接错误","未连通服务器！","error");
		}
	}).done(function(data){
		info = data;
	});
	return info;
}

function pagenationAction(pageNumber,pageSize){
	var resultData=pageInfo(pageSize, pageNumber);
	if(null!=resultData){
		if(200==resultData.code){
			$('.participle #participleGrid').datagrid('loadData',resultData.data.data);
			var pagePlug=$('.participle #participleGrid').datagrid('getPager');
			$(pagePlug).pagination({
				total:resultData.data.total,	//记录总数,需要查库才能得到
				pageSize:pageSize,				//页面尺寸
				pageNumber:pageNumber,			//创建分页（pagination）时显示的页码
				pageList:[10,20,30], 			//用户能改变页面尺寸。pageList 属性定义了能改成多大的尺寸
				showPageList:true,				//定义是否显示页面列表
				showRefresh:true,				//定义是否显示刷新按钮
				//当用户选择新的页面时触发
				onSelectPage:function(pageNumber, pageSize){
					pagenationAction(pageNumber,pageSize);
				},
				//刷新之后触发
				onRefresh:function(pageNumber, pageSize){
					pagenationAction(pageNumber,pageSize);
				},
				//当用户改变页面尺寸时触发
				onChangePageSize:function(pageSize){
					pagenationAction(1,pageSize);
				},
			});
		}else{
			msgShow("查询错误",data.message,"error");
		}
	}else{
		msgShow("连接错误","未连通服务器！","error");
	}
	return undefined;
}
