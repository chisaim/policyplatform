package com.cn.my.model;

import java.util.Date;

public class PreMessage {
	private Integer id;
	private String content;
	private String callerid;
	private String calledid;
	private Integer servicetype;
	private Date createdate;
	private Integer fileid;
	private Integer auditedresult;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
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
	public Integer getServicetype() {
		return servicetype;
	}
	public void setServicetype(Integer servicetype) {
		this.servicetype = servicetype;
	}
	public Date getCreatedate() {
		return createdate;
	}
	public void setCreatedate(Date createdate) {
		this.createdate = createdate;
	}
	public Integer getFileid() {
		return fileid;
	}
	public void setFileid(Integer fileid) {
		this.fileid = fileid;
	}
	public Integer getAuditedresult() {
		return auditedresult;
	}
	public void setAuditedresult(Integer auditedresult) {
		this.auditedresult = auditedresult;
	}
	@Override
	public String toString() {
		return "PreMessage [id=" + id + ", content=" + content + ", callerid="
				+ callerid + ", calledid=" + calledid + ", servicetype="
				+ servicetype + ", createdate=" + createdate + ", fileid="
				+ fileid + ", auditedresult=" + auditedresult + "]";
	}
	
}
