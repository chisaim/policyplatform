package com.cn.my.model;

import java.util.Date;
//待审核短信数据表实体类
public class MessageJunk {
    private Integer junk_id;

    private String msg_id;

    private Date monitoredtime;

    private String callerid;

    private String calledid;

    private String content;

    private String inspect_info;

    private Integer servicetype;

    private Integer inspect_level;

    private String contentmd5;

    private String contentsimhash;

    private Integer repeatcount;

    private Date createdtime;

    private String advertiser;

    private Integer status;

    private String content_origin;

    public Integer getJunk_id() {
        return junk_id;
    }

    public void setJunk_id(Integer junk_id) {
        this.junk_id = junk_id;
    }

    public String getMsg_id() {
        return msg_id;
    }

    public void setMsg_id(String msg_id) {
        this.msg_id = msg_id;
    }

    public Date getMonitoredtime() {
        return monitoredtime;
    }

    public void setMonitoredtime(Date monitoredtime) {
        this.monitoredtime = monitoredtime;
    }

    public String getCallerid() {
        return callerid;
    }

    public void setCallerid(String callerid) {
        this.callerid = callerid;
    }

    public String getCalledid() {
        return calledid;
    }

    public void setCalledid(String calledid) {
        this.calledid = calledid;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getInspect_info() {
        return inspect_info;
    }

    public void setInspect_info(String inspect_info) {
        this.inspect_info = inspect_info;
    }

    public Integer getServicetype() {
        return servicetype;
    }

    public void setServicetype(Integer servicetype) {
        this.servicetype = servicetype;
    }

    public Integer getInspect_level() {
        return inspect_level;
    }

    public void setInspect_level(Integer inspect_level) {
        this.inspect_level = inspect_level;
    }

    public String getContentmd5() {
        return contentmd5;
    }

    public void setContentmd5(String contentmd5) {
        this.contentmd5 = contentmd5;
    }

    public String getContentsimhash() {
        return contentsimhash;
    }

    public void setContentsimhash(String contentsimhash) {
        this.contentsimhash = contentsimhash;
    }

    public Integer getRepeatcount() {
        return repeatcount;
    }

    public void setRepeatcount(Integer repeatcount) {
        this.repeatcount = repeatcount;
    }

    public Date getCreatedtime() {
        return createdtime;
    }

    public void setCreatedtime(Date createdtime) {
        this.createdtime = createdtime;
    }

    public String getAdvertiser() {
        return advertiser;
    }

    public void setAdvertiser(String advertiser) {
        this.advertiser = advertiser;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getContent_origin() {
        return content_origin;
    }

    public void setContent_origin(String content_origin) {
        this.content_origin = content_origin;
    }

	@Override
	public String toString() {
		return "MessageJunk [junk_id=" + junk_id + ", msg_id=" + msg_id
				+ ", monitoredtime=" + monitoredtime + ", callerid=" + callerid
				+ ", calledid=" + calledid + ", content=" + content
				+ ", inspect_info=" + inspect_info + ", servicetype="
				+ servicetype + ", inspect_level=" + inspect_level
				+ ", contentmd5=" + contentmd5 + ", contentsimhash="
				+ contentsimhash + ", repeatcount=" + repeatcount
				+ ", createdtime=" + createdtime + ", advertiser=" + advertiser
				+ ", status=" + status + ", content_origin=" + content_origin
				+ "]";
	}
    
}