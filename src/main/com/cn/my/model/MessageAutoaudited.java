package com.cn.my.model;

import java.util.Date;

public class MessageAutoaudited {
    private Integer id;

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

    private Date createdtime;

    private Integer audited_id;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public Date getCreatedtime() {
        return createdtime;
    }

    public void setCreatedtime(Date createdtime) {
        this.createdtime = createdtime;
    }

    public Integer getAudited_id() {
        return audited_id;
    }

    public void setAudited_id(Integer audited_id) {
        this.audited_id = audited_id;
    }
}