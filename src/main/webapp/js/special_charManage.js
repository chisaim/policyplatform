$(function(){
	initspecial_charManage()
});

function initspecial_charManage(){
	//初始化表格
	$(".special_char #special_charGrid").datagrid({
		fit:true,
		fitColumns:true,// 表宽度自适应
		rownumbers:true,// 显示行号
		toolbar:".special_char #head",
		pagination:true,
		columns:[[{field:'check',checkbox:"true",width:40,align:'left'},
		          {field:'spchar',title:'特殊字符',width:100,align:'left'},
		          {field:'fromid',title:'来源',width:100,align:'left'},
		          {field:'createdtime',title:'创建时间',width:150,align:'right',
		        	  formatter:function(value){
		        		  var time=new Date(value);
		        		  var y = time.getFullYear();
		        		  var m = time.getMonth() + 1;
		        		  var d = time.getDate();
		        		  var h = time.getHours();
		        		  var M	= time.getMinutes();
		        		  var s	= time.getSeconds();
		        		  return y + '-' +m + '-' + d+' '+h+':'+M+':'+s; 
		        	  }},
		        {field:'id',hidden:'true'}]]
	
	});
	//分页插件
	pagenationAction(1,10);
	//删除
	deleteSpecial_char(".special_char #remove");
	//初始化弹窗
	initSpecial_charWindow();
	//取消按钮
	$("#special_charDidlog #cancelMessage-btn").bind('click',function(){
		$("#special_charDidlog").panel('close',true);
	});
	//点击修改按钮返回字符详细信息
	$(".special_char #edit").bind('click',function(){
		var row=$(".special_char #special_charGrid").datagrid('getSelected');
		if(row==null){
			msgShow("操作错误","请选中数据！","error");
		}else{
			$.ajax({
				type:'GET',
				url:'/policyplatform/Special_char/getSpecial_char?id='+row.id,
				async: false,
				success:function(data){
					if(data.code==200){
						$("#special_charDidlog #spchar").val(data.data.spchar);
						$("#special_charDidlog #fromid").val(data.data.fromid);
						$("#special_charDidlog #createdtime").datetimebox("setValue",formTimestampValue(new Date(data.data.createdtime)));
						
						$("#special_charDidlog").removeClass("addMessage");
						$("#special_charDidlog").panel('open',true);
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
	//格式化添加窗口的填充数据
	$("#head #add").bind('click',function(){
		$("#special_charDidlog #spchar").val("");
		$("#special_charDidlog #fromid").val("");
		$("#special_charDidlog #createdtime").datetimebox("setValue",'currentDate');
		
		$("#special_charDidlog").addClass("addMessage");
		$("#special_charDidlog").panel('open',true);
	});
	//初始化时间
	$("#special_charDidlog #createdtime").datetimebox({
		value: 'currentDate',
	    required: true,
	    showSeconds: true,
	    formatter: function(value){return formTimestampValue(value);},
	    timeSeparator:':'
	});
	//判断窗口是否包含addMessage，如有则为添加，没有为修改
	$("#special_charDidlog #saveMessage-btn").bind('click',function(){
		var data=GetMessageDate();
		if($("#special_charDidlog").hasClass("addMessage")){
			$.ajax({
				type:'POST',
				url:'/policyplatform/Special_char/insertSpecial_char',
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
				url:'/policyplatform/Special_char/updateSpecial_char',
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
		$("#special_charDidlog").panel('close',true);
	});
	
	//文件上传
	$(".special_char #upload").bind('click',function(){
		$("#special_charUpload #keywordFile").form('clear');
		$("#special_charUpload").panel('open',true);
	});
	
	$("#special_charUpload").dialog({
	    title: '关键词策略文件上传',
	    width: 270,
	    height: 140,
	    closed: true,
	    cache: false,
	    content:'<form id="keywordFile" method="post" style="margin-left:30px;"></form>'
	});
	$("#special_charUpload #keywordFile").append('</br>');
	$("#special_charUpload #keywordFile").append('<input class="easyui-filebox" data-options="prompt:\'Choose a file...\'" name="file" style="width:200px;">');
	$("#special_charUpload #keywordFile").append('</br></br>');
	$("#special_charUpload #keywordFile").append('<a href="#" id="uploadKeywordFile" class="easyui-linkbutton" iconCls="icon-upload" style="margin-left:20px;">上传</a>');
	$("#special_charUpload #keywordFile").append('<a href="#" id="canceluploadKeywordFile" class="easyui-linkbutton" iconCls="icon-clear" style="margin-left:30px;">重置</a>');
	$.parser.parse($("#special_charUpload #keywordFile"));
	
	$("#keywordFile #uploadKeywordFile").bind('click',function(){
		//可以将form表单整个直接传递请求中
		var form = new FormData($('#special_charUpload #keywordFile')[0]);//FormData用于封装数据
		$.ajax({  
            url:"/policyplatform/Special_char/uploadSp",  
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
            	 $("#special_charUpload").panel('close',true);
             }
        }); 
	});
	$("#keywordFile #canceluploadKeywordFile").bind('click',function(){
		$("#special_charUpload #keywordFile").form('clear');
	});
}

//初始化数据
function getSpecial_chars(pageSize,pageNumber){
	var char;
	var queryString= "&pageSize="+pageSize+"&pageNumber="+pageNumber;
	$.ajax({
		type:'GET',
		url:'/policyplatform/Special_char/getSpecial_chars?'+queryString,
		async: false,
		error:function(){
			msgShow("连接错误","未连通服务器！","error");
		}
		}).done(function(data){
			char=data;
		});
	return char;
}
	
function pagenationAction(pageNumber,pageSize){
	var resultData=getSpecial_chars(pageSize,pageNumber);
	if(resultData!=null){
		if(resultData){				
			$('.special_char #special_charGrid').datagrid('loadData',resultData.data.data);
			var pagePlug=$('.special_char #special_charGrid').datagrid('getPager');
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

//删除
function deleteSpecial_char(deleteData){
	$(deleteData).bind('click',function(){
		var del=$(".special_char #special_charGrid").datagrid('getSelected');
		if(del!=null){
			$.ajax({
				type:'POST',
				url:'/policyplatform/Special_char/deleteSpecial_char?id='+del.id,
				async:true,
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
}

function initSpecial_charWindow(){
	var str='<a style="margin-left:10px;">特殊字符</a><br/>'+
			'<input id="spchar" style="width:150px;margin-left:10px;margin-top:5px;"><br/>'+
			'<a style="margin-left:10px;">上传时间</a><br/>&nbsp;&nbsp;'+
			'<input id="createdtime" style="width:150px;margin-left:10px;margin-top:5px;"><br/>'+
			'<a href="#" class="easyui-linkbutton" id="saveMessage-btn" style="margin-right:20px;width:40px;margin-left:40px; position: absolute;">保存</a>'+
			'<a href="#" class="easyui-linkbutton" id="cancelMessage-btn" style="margin-left:100px;width:40px;">取消</a>';
	$('#special_charDidlog').dialog({		
	    title: '特殊字符详细信息',
	    width: 240,
	    height: 240,
	    closed: true,
	    cache: false,
	    content:str,
	    modal: true,
	});	
}
function GetMessageDate(){
	var data = {			
		"spchar":$("#special_charDidlog #spchar").val(),
		"fromid":$("#special_charDidlog #fromid").val(),
		"createdtime":Date.parse($("#special_charDidlog #createdtime").datetimebox("getValue")),
		"id":$(".special_char #special_charGrid").datagrid('getSelected')==null?-1:$(".special_char #special_charGrid").datagrid('getSelected').id
	};
	return data;
}