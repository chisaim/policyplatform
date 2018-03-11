//时间戳格式化
function formTimestampValue(date){
	var y = date.getFullYear();
	var m = date.getMonth() + 1;
	var d = date.getDate();
	var hh = date.getHours();
	var mm = date.getMinutes();
	var ss = date.getSeconds();
	return y + '-' +m + '-' + d + " " + hh + ':' + mm + ':' + ss;
}
//日期控件的数据格式化
function dataformatter(date){  
	var y = date.getFullYear();  
	var m = date.getMonth()+1;  
	var d = date.getDate();  
	return y+'-'+(m<10?('0'+m):m)+'-'+(d<10?('0'+d):d);  
}  
//日期数据格式化
function dataparser(s){  
	if (!s) return new Date();  
	var ss = (s.split('-'));  
	var y = parseInt(ss[0],10);  
	var m = parseInt(ss[1],10);  
	var d = parseInt(ss[2],10);  
	if (!isNaN(y) && !isNaN(m) && !isNaN(d)){  
		return new Date(y,m-1,d);  
	} else {  
		return new Date();  
	}  
}
function datatimeformatter(date){
	return formTimestampValue(date);
}
function datatimeparser(s){
	var t = Date.parse(s);
	if (!isNaN(t)){
		return new Date(t);
	} else {
		return new Date();
	}
}

//double类型数据格式化
function formatDouble(value){
	if(value==null||value==0||value==""||value==undefined){
		return 0;
	}
	//保留两位小数
	if(value!=null){
		return value.toFixed(2);
	}
}

//给jquery补充元素的resize事件
(function($, h, c) {  
    var a = $([]), e = $.resize = $.extend($.resize, {}), i, k = "setTimeout", j = "resize", d = j  
            + "-special-event", b = "delay", f = "throttleWindow";  
    e[b] = 350;  
    e[f] = true;  
    $.event.special[j] = {  
        setup : function() {  
            if (!e[f] && this[k]) {  
                return false  
            }  
            var l = $(this);  
            a = a.add(l);  
            $.data(this, d, {  
                w : l.width(),  
                h : l.height()  
            });  
            if (a.length === 1) {  
                g()  
            }  
        },  
        teardown : function() {  
            if (!e[f] && this[k]) {  
                return false  
            }  
            var l = $(this);  
            a = a.not(l);  
            l.removeData(d);  
            if (!a.length) {  
                clearTimeout(i)  
            }  
        },  
        add : function(l) {  
            if (!e[f] && this[k]) {  
                return false  
            }  
            var n;  
            function m(s, o, p) {  
                var q = $(this), r = $.data(this, d);  
                r.w = o !== c ? o : q.width();  
                r.h = p !== c ? p : q.height();  
                n.apply(this, arguments)  
            }  
            if ($.isFunction(l)) {  
                n = l;  
                return m  
            } else {  
                n = l.handler;  
                l.handler = m  
            }  
        }  
    };  
    function g() {  
        i = h[k](function() {  
            a.each(function() {  
                var n = $(this), m = n.width(), l = n.height(), o = $  
                        .data(this, d);  
                if (m !== o.w || l !== o.h) {  
                    n.trigger(j, [ o.w = m, o.h = l ])  
                }  
            });  
            g()  
        }, e[b])  
    }  
})(jQuery, this);