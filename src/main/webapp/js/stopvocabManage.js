/**
 * @ClassName: StpvocabMangge
 * @Description: 停用词库管理JS，对应StopvocabMangge.html;
 * @author caixiaoyu
 * @date 2017年11月15日
 */
$(function(){
	initstopvocabManage();
});
function initstopvocabManage() {
	$('#stopdg').datagrid({
	fit:true,
	fitColumns:true,// 表宽度自适应
	rownumbers:true,// 显示行号
	singleSelect:true,
	idField:'itemid',
	pagination:true,
	toolbar:"#stoptb",
	columns:[[
        {field:'ck',title:'',id:'',checkbox:true},     
		{field:'id',title:'词ID',width:60},
	    {field:'stopword',title:'词名称',width:150,editor:'text'},
		{field:'wordtype',title:'词类型',width:80,align:'right',editor:{type:'numberbox',options:[0,1]}},
		{field:'createdtime',title:'操作时间',width:80,align:'right',formatter:function FormatDate (strTime) {
		    var date = new Date(strTime);
		    return date.getFullYear()+"-"+(date.getMonth()+1)+"-"+date.getDate()+" "+date.getHours()+":"+date.getMinutes()+":"+date.getSeconds();
		}},
	    {field:'fromid',title:'来源ID',width:100,editor:'admin'},
		{field:'status',title:'状态',width:50,align:'center',formatter: function(value){
			return value==true?"有效":"无效";
		}},
		{field:'action',title:'动作',width:120,align:'center',
			formatter:function(value,row,index){
				if (row.editing){
					var s = '<a href="#" onclick="saverow(this)">保存</a> ';
					return s;
				} else {
					var e = '<a href="#	" onclick="editrow(this)">修改</a> ';
					var s = '<a href="#" onclick="saverow(this)">保存</a> ';
					return e+s;
				}
			}
		}
	]],
	onBeforeEdit:function(index,row){
		row.editing = true;
		updateActions(index);
	},
	//修改操作事件
	onAfterEdit:function(index,row,changes){
		row.editing = false;
		updateActions(index);
		console.info(changes);
		var updated = $('#stopdg').datagrid('getChanges','updated')[0];  //我出错的地方
    	console.info(updated);
		var data = {
				    "stopword":updated.stopword,
				    "status":updated.status,
					"wordtype":updated.wordtype,
					"fromid":updated.fromid,
				    "action":updated.action,
				    "id":updated.id,  //我出错的地方：updated里有id，但是没在data里封装id,导致后台找不到id，没法查找
		};
		if(updated.length > 0){
			url = '/policyplatform/stopvocab/editStopvocab'
		}
		async: false, //表示同步执行
		$.ajax({
			type: 'POST',
			url: '/policyplatform/stopvocab/editStopvocab',
			contentType: "application/json; charset=utf-8",
			async: false,
			data:JSON.stringify(data),
			success: function (data) {
				if(data.code == 200){
					msgShow("修改成功",data.message);
					pagenationAction(1,10);
				}
				else {
					msgShow("修改错误",data.message,"error");
				}
			},
			error: function () {
				msgShow("连接错误","未连通服务器！","error");
			}
		});
	},
	onCancelEdit:function(index,row){
		row.editing = false;
		updateActions(index);
	}
	});
	 //停用词文件上传的dialog
	 $("#stopvocabForm").dialog({
		    title: '停用词文件上传',
		    width: 270,
		    height: 140,
		    closed: true,
		    cache: false,
		    content:'<form id="stopvocabFile" method="post" style="margin-left:30px;"></form>'
		});
		$("#stopvocabForm #stopvocabFile").append('</br>');
		$("#stopvocabForm #stopvocabFile").append('<input class="easyui-filebox" data-options="prompt:\'Choose a file...\'" name="file" style="width:200px;">');
		$("#stopvocabForm #stopvocabFile").append('</br></br>');
		$("#stopvocabForm #stopvocabFile").append('<a href="#" id="stopuploadFile" class="easyui-linkbutton" iconCls="icon-upload" style="margin-left:20px;">上传</a>');
		$("#stopvocabForm #stopvocabFile").append('<a href="#" id="cancelstopuploadFile" class="easyui-linkbutton" iconCls="icon-clear" style="margin-left:30px;">重置</a>');
		$.parser.parse($("#stopvocabForm #stopvocabFile"));
		
	//停用词上传按钮绑定
	 $(".stopvocabManage #stopupload").bind('click',function(){
			$("#stopvocabForm #stopvocabFile").form('clear');
			$("#stopvocabForm").panel('open',true);
		});
	 //停用词的上传事件
		$("#stopvocabFile #stopuploadFile").bind('click',function(){
			//可以将form表单整个直接传递请求中
			var form = new FormData($('#stopvocabForm #stopvocabFile')[0]);//FormData用于封装数据
			$.ajax({  
	            url:"/policyplatform/stopvocab/upload",  
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
	            	 $("#stopvocabForm").panel('close',true);
	             }
	        }); 
		});
		$("#stopvocabFile #cancelstopuploadFile").bind('click',function(){
			$("#stopvocabForm #stopvocabFile").form('clear');
		});

  }
		function getRowIndex(target){
			var tr = $(target).closest('tr.datagrid-row');
			return parseInt(tr.attr('datagrid-row-index'));
		}
		function editrow(target){
			$('#stopdg').datagrid('beginEdit', getRowIndex(target));
		}
		function saverow(target){
			$('#stopdg').datagrid('endEdit', getRowIndex(target));
		}
	//分页插件初始化
	pagenationAction(1,10);

  function updateActions(index){
		$('#stopdg').datagrid('updateRow',{
			index: index,
			row:{}
		});
	}
	//分页查找即数据初始化
function getstopvocabSearch(pageSize,pageNumber){
	var da;
	var queryString = "&pageSize="+pageSize+"&pageNumber="+pageNumber;
	$.ajax({
		type: 'GET',
		url: '/policyplatform/stopvocab/getStopvocab?'+queryString,
		async: false,
		error: function () {
			msgShow("连接错误","未连通服务器！","error");
		}
	}).done(function(data){
		   da = data;
		});
    return da;	
}
//分页插件：
function pagenationAction(pageNumber,pageSize){
		var resultData=getstopvocabSearch(pageSize, pageNumber);
		if(null!=resultData){
			if(200==resultData.code){
				$('.stopvocabManage #stopdg').datagrid('loadData',resultData.data.data);
				var pagePlug=$('.stopvocabManage #stopdg').datagrid('getPager');
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



/**
 * 添加事件
 * @date 2017年11月15日
 */
  //绑定添加弹出框
$("#stopvocabAdd-btn").click(function () {

	$("#stopadd").panel('open',true);
  });
$("#dialogsave").click(function () {
	var Data = {
				"stopword":$("#dialogname").val(),
				"status":$("#dialogstatus").val()=="0"?false:true, //三元运算符
				"wordtype":$("#dialogtype").val(),
	//		"fromid":$("#dialoguser").val(),
	//	"createdtime":$("#dialogtime").val() //前后台的时间戳不对应
	};
	
	$.ajax({
		type: 'POST',
		url: '/policyplatform/stopvocab/addStopvocab',
		contentType: "application/json; charset=utf-8",
		async: false,
		data:JSON.stringify(Data),
		success: function (data) {
		
			if(data.code == 200){
				$("#stopadd").panel('close',true);
				pagenationAction(3,10);
				
			}
			else {
				msgShow("执行错误",data.message,"error");
			}
		},
		error: function () {
			msgShow("连接错误","未连通服务器！","error");
		}
	});
    
	$("#dialogclose").click(function () {  
		
		$("#stopadd").panel('close',true);
		pagenationAction(1,10);
		
	})
});
	/**
	 * 删除事件
	 * @date 2017年11月20日
	 */
	//绑定删除按钮
	/*$(function() {
		//按钮事件绑定
		initstopvocabRemove('.stopvocabManage #stopvocabRemove-btn');
		})*/
	//function initstopvocabRemove(btnElement){
			$('.stopvocabManage #stopvocabRemove-btn').bind('click',function(){
				//判断是否有选中的数据
				var data = $('.stopvocabManage #stopdg').datagrid('getSelected');
				
				$.ajax({
				 	type: 'GET',
				 	url: '/policyplatform/stopvocab/removeStopvocab?stopvocab_id='+data.id,
				 	async: false, //表示同步执行
				 	success: function (data) {
				 		
				 		if(data.code == 200){
				 			pagenationAction(1,10);//删除成功后就初始化一次数据
				 		}else {
							alert(data.message);
						}
				 	},
				 	error: function () {
				 		msgShow("连接错误","获取用户数据失败！","error");
				 	}
				 });
			});
	
	/*function cancelrow(target){
		$('#stopdg').datagrid('cancelEdit', getRowIndex(target));
	}*/
		/**
		 * 根据停用词的名称来查询
		 * @date 2017年12月6日
		 */
		//根据要停用词名称进行查询
	    
    	 function doSearch(){
			var Name = $('#stopwordname').val();
			console.info(Name);
		    $.ajax({
			 	type: 'GET',
			 	url: '/policyplatform/stopvocab/foundStopvocab?stopvocab_stopword='+encodeURI(encodeURI(Name)),
			 	async: false, //表示同步执行
			 	success: function (Name) {
		
			 		if(Name.code == 200){
			 			$('.stopvocabManage #stopdg').datagrid('loadData',Name.data);
			 		
			 		}else {
						alert(Name.message);
					}
			 	},
			 	error: function () {
			 		msgShow("连接错误","获取用户数据失败！","error"	);
			 	}
			 });
		}
    