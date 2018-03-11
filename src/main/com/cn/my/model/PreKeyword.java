package com.cn.my.model;

import java.util.Date;

public class PreKeyword {
	private Integer id;

    private String keyword;

    private Integer servicetype;

    private Integer kwclass;

    private Date createdtime;
    
    private Integer fileid;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public Integer getServicetype() {
		return servicetype;
	}

	public void setServicetype(Integer servicetype) {
		this.servicetype = servicetype;
	}

	public Integer getKwclass() {
		return kwclass;
	}

	public void setKwclass(Integer kwclass) {
		this.kwclass = kwclass;
	}

	public Date getCreatedtime() {
		return createdtime;
	}

	public void setCreatedtime(Date createdtime) {
		this.createdtime = createdtime;
	}

	public Integer getFileid() {
		return fileid;
	}

	public void setFileid(Integer fileid) {
		this.fileid = fileid;
	}

	@Override
	public String toString() {
		return "PreKeyword [id=" + id + ", keyword=" + keyword
				+ ", servicetype=" + servicetype + ", kwclass=" + kwclass
				+ ", createdtime=" + createdtime + ", fileid=" + fileid + "]";
	}
    
}
