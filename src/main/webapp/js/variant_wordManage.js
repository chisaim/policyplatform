$(function(){
	initvariant_wordManage()
});

function initvariant_wordManage(){
	//初始化表格
	$(".variant_word #variant_wordGrid").datagrid({
		fit:true,
		fitColumns:true,// 表宽度自适应
		rownumbers:true,// 显示行号
		toolbar:".variant_word #head",
		pagination:true,
		columns:[[{field:'check',checkbox:"true",width:40,align:'left'},
		          {field:'srcword',title:'正常词',width:100,align:'left'},
		          {field:'varword',title:'变异词',width:100,align:'left'},
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
	//分页
	pagenationAction(1,10);
	//删除
	deletevariant_words(".variant_word #remove");
	//详细信息弹窗
	initVariant_wordWindow();
	//初始化时间
	$("#variant_wordDidlog #createdtime").datetimebox({
		value: 'currentDate',
	    required: true,
	    showSeconds: true,
	    formatter: function(value){return formTimestampValue(value);},
	    timeSeparator:':'
	});
	//取消按钮
	$("#variant_wordDidlog #cancelMessage-btn").bind('click',function(){
		$("#variant_wordDidlog").panel('close',true);
	});	
	//点击修改按钮返回字符详细信息
	$(".variant_word #edit").bind('click',function(){
		var row=$(".variant_word #variant_wordGrid").datagrid('getSelected');
		if(row==null){
			msgShow("操作错误","请选中数据！","error");
		}else{
			$.ajax({
				type:'GET',
				url:'/policyplatform/Variant_word/getVariant_word?id='+row.id,
				async: false,
				success:function(data){
					if(data.code==200){
						$("#variant_wordDidlog #srcword").val(data.data.srcword);
						$("#variant_wordDidlog #varword").val(data.data.varword);
						$("#variant_wordDidlog #fromid").val(data.data.fromid);
						$("#variant_wordDidlog #createdtime").datetimebox("setValue",formTimestampValue(new Date(data.data.createdtime)));
						$("#variant_wordDidlog").removeClass("addMessage");
						$("#variant_wordDidlog").panel('open',true);
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
		$("#variant_wordDidlog #srcword").val("");
		$("#variant_wordDidlog #varword").val("");
		$("#variant_wordDidlog #fromid").val("");
		$("#variant_wordDidlog #createdtime").datetimebox("setValue",'currentDate');
		
		$("#variant_wordDidlog").addClass("addMessage");
		$("#variant_wordDidlog").panel('open',true);
	});
	//判断窗口是否包含addMessage，如有则为添加，没有为修改
	$("#variant_wordDidlog #saveMessage-btn").bind('click',function(){
		var data=GetMessageDate();
		if($("#variant_wordDidlog").hasClass("addMessage")){
			$.ajax({
				type:'POST',
				url:'/policyplatform/Variant_word/insertVariant_word',
				aysnc:true,
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
				url:'/policyplatform/Variant_word/updateVariant_word',
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
		$("#variant_wordDidlog").panel('close',true);
	});
	
	//文件上传
	$(".variant_word #uploadWord").bind('click',function(){
		$("#variant_wordUpload #keywordFile").form('clear');
		$("#variant_wordUpload").panel('open',true);
	});
	$("#variant_wordUpload").dialog({
	    title: '关键词策略文件上传',
	    width: 270,
	    height: 140,
	    closed: true,
	    cache: false,
	    content:'<form id="keywordFile" method="post" style="margin-left:30px;"></form>'
	});
	$("#variant_wordUpload #keywordFile").append('</br>');
	$("#variant_wordUpload #keywordFile").append('<input class="easyui-filebox" data-options="prompt:\'Choose a file...\'" name="file" style="width:200px;">');
	$("#variant_wordUpload #keywordFile").append('</br></br>');
	$("#variant_wordUpload #keywordFile").append('<a href="#" id="uploadKeywordFile" class="easyui-linkbutton" iconCls="icon-upload" style="margin-left:20px;">上传</a>');
	$("#variant_wordUpload #keywordFile").append('<a href="#" id="canceluploadKeywordFile" class="easyui-linkbutton" iconCls="icon-clear" style="margin-left:30px;">重置</a>');
	$.parser.parse($("#variant_wordUpload #keywordFile"));
	
	$("#keywordFile #uploadKeywordFile").bind('click',function(){
		//可以将form表单整个直接传递请求中
		var form = new FormData($('#variant_wordUpload #keywordFile')[0]);//FormData用于封装数据
		$.ajax({  
            url:"/policyplatform/Variant_word/uploadWord",  
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
            	 $("#variant_wordUpload").panel('close',true);
             }
        }); 
	});
	$("#keywordFile #canceluploadKeywordFile").bind('click',function(){
		$("#variant_wordUpload #keywordFile").form('clear');
	});
}

function getvariant_words(pageSize,pageNumber){
	var words;
	var queryString= "&pageSize="+pageSize+"&pageNumber="+pageNumber;
	$.ajax({
		type:'GET',
		url:'/policyplatform/Variant_word/getVariant_words?'+queryString,
		async: false,
		error:function(){
			msgShow("连接错误","未连通服务器！","error");
		}
		}).done(function(data){
			words=data;
		});
	return words;
}

function pagenationAction(pageNumber,pageSize){
	var result=getvariant_words(pageSize, pageNumber);
	if(result!=null){
		if(result){
			$('.variant_word #variant_wordGrid').datagrid('loadData',result.data.data);
			var pagePlug=$('.variant_word #variant_wordGrid').datagrid('getPager');
			$(pagePlug).pagination({
				total:result.data.total,	//记录总数,需要查库才能得到
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
};

function GetMessageDate(){
	var data = {			
		"srcword":$("#variant_wordDidlog #srcword").val(),
		"varword":$("#variant_wordDidlog #varword").val(),
		"fromid":$("#variant_wordDidlog #fromid").val(),
		"createdtime":Date.parse($("#variant_wordDidlog #createdtime").datetimebox("getValue")),
		"id":$(".variant_word #variant_wordGrid").datagrid('getSelected')==null?-1:$(".variant_word #variant_wordGrid").datagrid('getSelected').id
	};
	return data;
}

function deletevariant_words(deleteButton){
	$(deleteButton).bind("click",function(){
		var getSelect=$(".variant_word #variant_wordGrid").datagrid("getSelected");
		if(getSelect!=null){
			$.ajax({
				type:'POST',
				url:'/policyplatform/Variant_word/deleteVariant_word?id='+getSelect.id,
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



function initVariant_wordWindow(){
	var str='<a style="margin-left:10px;">正常词</a><br/>'+
			'<input id="srcword" style="width:150px;margin-left:10px;margin-top:5px;"><br/>'+
			'<a style="margin-left:10px;">变异词</a><br/>'+
			'<input id="varword" style="width:150px;margin-left:10px;margin-top:5px;"><br/>'+
			'<a style="margin-left:10px;">创建时间</a><br/>&nbsp;&nbsp;'+
			'<input id="createdtime" style="width:150px;margin-left:10px;margin-top:5px;"><br/>'+
			'<a href="#" class="easyui-linkbutton" id="saveMessage-btn" style="margin-right:20px;width:40px;margin-left:40px; position: absolute;">保存</a>'+
			'<a href="#" class="easyui-linkbutton" id="cancelMessage-btn" style="margin-left:100px;width:40px;">取消</a>';
	$('#variant_wordDidlog').dialog({		
		title: '特殊字符详细信息',
		width: 240,
		height: 300,
		closed: true,
		cache: false,
		content:str,
		modal: true,
	});	
}