package com.cn.my.model;

import java.util.Date;

public class EvaluateFileInfo {
	private Integer fileid;
	private String filename;
	private Integer datanumber;
	private Date createtime;
	private Integer userid;
	private Integer filetype;
	public Integer getFileid() {
		return fileid;
	}
	public void setFileid(Integer fileid) {
		this.fileid = fileid;
	}
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public Integer getDatanumber() {
		return datanumber;
	}
	public void setDatanumber(Integer datanumber) {
		this.datanumber = datanumber;
	}
	public Date getCreatetime() {
		return createtime;
	}
	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}
	public Integer getUserid() {
		return userid;
	}
	public void setUserid(Integer userid) {
		this.userid = userid;
	}
	public Integer getFiletype() {
		return filetype;
	}
	public void setFiletype(Integer filetype) {
		this.filetype = filetype;
	}
	@Override
	public String toString() {
		return "EvaluateFileInfo [fileid=" + fileid + ", filename=" + filename
				+ ", datanumber=" + datanumber + ", createtime=" + createtime
				+ ", userid=" + userid + ", filetype=" + filetype + "]";
	}
}
