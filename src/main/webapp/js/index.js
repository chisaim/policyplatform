//页面元素加载完成后加载页面
window.onload = function(){
	$('#loading-mask').fadeOut();
}

//页面初始化
$(function(){
	//根据token查询相关权限
	 $.ajax({
	 	type: 'GET',
	 	url: '/policyplatform/user/getUserTab',
	 	async: false,
	 	success: function (data) {
	 		if(data.code == 200){
	 			InitLeftMenu(data.data);
	 			tabClose();
	 			tabCloseEven();
	 		}else {
				msgShow("查询",data.message,"info");
			}
	 	},
	 	error: function () {
	 		msgShow("连接错误","获取用户菜单失败！","error");
	 	}
	 });
})

//初始化左侧
function InitLeftMenu(datajson) {
	$("#nav").accordion({animate:false,fit:true,border:false});
	var selectedPanelname = '';
	$.each(datajson.menus, function(i, n) {
		var menulist ='';
		menulist +='<ul class="navlist">';
		$.each(n.menus, function(j, o) {
			var id = o.url.substring(7,o.url.length-5);
			menulist += '<li><div ><a id='+id+' ref="'+o.menuid+'" href="#" rel="' + o.url + '" ><span class="nav">' + o.menuname + '</span></a></div> ';
			menulist+='</li>';
		})
		menulist += '</ul>';

		$('#nav').accordion('add', {
			title: n.menuname,
			content: menulist,
			border:false,
			iconCls: 'icon ' + n.icon
		});

		if(i==0)
			selectedPanelname =n.menuname;

	});
	$('#nav').accordion('select',selectedPanelname);

	$('.navlist li a').click(function(){
		var tabTitle = $(this).children('.nav').text();
		var url = $(this).attr("rel");
		var icon = $(this).find('.icon').attr('class');	

		addTab(tabTitle,url,icon);
		$('.navlist li div').removeClass("selected");
		$(this).parent().addClass("selected");
	}).hover(function(){
		$(this).parent().addClass("hover");
	},function(){
		$(this).parent().removeClass("hover");
	});

}

//在center中打开导航面板对应的小页面
function addTab(subtitle,url,icon){
	if(!$('#tabs').tabs('exists',subtitle)){
		$('#tabs').tabs('add',{
			title:subtitle,
			href:url,
			closable:true,
			icon:icon
		});
	}else{
		$('#tabs').tabs('select',subtitle);
		$('#mm-tabupdate').click();
	}
	tabClose();
}

//选项卡事件绑定
function tabClose()
{
	/*双击关闭TAB选项卡*/
	$(".tabs-inner").dblclick(function(){
		var subtitle = $(this).children(".tabs-closable").text();
		$('#tabs').tabs('close',subtitle);
	})
	/*为选项卡绑定右键*/
	$(".tabs-inner").bind('contextmenu',function(e){
		$('#mm').menu('show', {
			left: e.pageX,
			top: e.pageY
		});

		var subtitle =$(this).children(".tabs-closable").text();

		$('#mm').data("currtab",subtitle);
		$('#tabs').tabs('select',subtitle);
		return false;
	});
}

//绑定右键菜单事件
function tabCloseEven() {

    $('#mm').menu({
        onClick: function (item) {
            closeTab(item.id);
        }
    });

    return false;
}

function closeTab(action)
{
	var onlyOpenTitle="欢迎使用";//不允许关闭的标签的标题
    var alltabs = $('#tabs').tabs('tabs');
    var currentTab =$('#tabs').tabs('getSelected');
	var allTabtitle = [];
	$.each(alltabs,function(i,n){
		allTabtitle.push($(n).panel('options').title);
	})

    switch (action) {
        case "refresh":
            var iframe = $(currentTab.panel('options').content);
            var src = iframe.attr('src');
            $('#tabs').tabs('update', {
                tab: currentTab,
                options: {
                    href: src
                }
            })
            break;
        case "close":
            var currtab_title = currentTab.panel('options').title;
            $('#tabs').tabs('close', currtab_title);
            break;
        case "closeall":
            $.each(allTabtitle, function (i, n) {
                if (n != onlyOpenTitle){
                    $('#tabs').tabs('close', n);
				}
            });
            break;
        case "closeother":
            var currtab_title = currentTab.panel('options').title;
            $.each(allTabtitle, function (i, n) {
                if (n != currtab_title && n != onlyOpenTitle)
				{
                    $('#tabs').tabs('close', n);
				}
            });
            break;
        case "closeright":
            var tabIndex = $('#tabs').tabs('getTabIndex', currentTab);

            if (tabIndex == alltabs.length - 1){
                msgShow("信息",'不好意思，后边没有啦!',"info");
                return false;
            }
            $.each(allTabtitle, function (i, n) {
                if (i > tabIndex) {
                    if (n != onlyOpenTitle){
                        $('#tabs').tabs('close', n);
					}
                }
            });

            break;
        case "closeleft":
            var tabIndex = $('#tabs').tabs('getTabIndex', currentTab);
            if (tabIndex == 1) {
            	msgShow("信息",'只有一个页面了，留着吧!',"info");
                return false;
            }
            $.each(allTabtitle, function (i, n) {
                if (i < tabIndex) {
                    if (n != onlyOpenTitle){
                        $('#tabs').tabs('close', n);
					}
                }
            });

            break;
        case "exit":
            $('#closeMenu').menu('hide');
            break;
    }
}

//弹出信息窗口 title:标题 msgString:提示信息 msgType:信息类型 [error,info,question,warning]
function msgShow(title, msgString, msgType) {
	$.messager.alert(title, msgString, msgType);
}