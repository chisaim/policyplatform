package com.cn.my.model;

import java.util.Date;

public class Keyword {
    private Integer id;

    private Integer level;

    private String keyword;

    private Integer unit;

    private Integer timespan;

    private Integer monthreshold;

    private Integer filterthreshold;

    private Integer abthreshold;

    private Integer servicetype;

    private Integer kwclass;

    private Integer optype;

    private Integer userid;

    private Date createdtime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public Integer getUnit() {
        return unit;
    }

    public void setUnit(Integer unit) {
        this.unit = unit;
    }

    public Integer getTimespan() {
        return timespan;
    }

    public void setTimespan(Integer timespan) {
        this.timespan = timespan;
    }

    public Integer getMonthreshold() {
        return monthreshold;
    }

    public void setMonthreshold(Integer monthreshold) {
        this.monthreshold = monthreshold;
    }

    public Integer getFilterthreshold() {
        return filterthreshold;
    }

    public void setFilterthreshold(Integer filterthreshold) {
        this.filterthreshold = filterthreshold;
    }

    public Integer getAbthreshold() {
        return abthreshold;
    }

    public void setAbthreshold(Integer abthreshold) {
        this.abthreshold = abthreshold;
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

    public Integer getOptype() {
        return optype;
    }

    public void setOptype(Integer optype) {
        this.optype = optype;
    }

    public Integer getUserid() {
        return userid;
    }

    public void setUserid(Integer userid) {
        this.userid = userid;
    }

    public Date getCreatedtime() {
        return createdtime;
    }

    public void setCreatedtime(Date createdtime) {
        this.createdtime = createdtime;
    }

	@Override
	public String toString() {
		return "Keyword [id=" + id + ", level=" + level + ", keyword=" + keyword
				+ ", unit=" + unit + ", timespan=" + timespan
				+ ", monthreshold=" + monthreshold + ", filterthreshold="
				+ filterthreshold + ", abthreshold=" + abthreshold
				+ ", servicetype=" + servicetype + ", kwclass=" + kwclass
				+ ", optype=" + optype + ", userid=" + userid + ", createdtime="
				+ createdtime + "]";
	}
    
}