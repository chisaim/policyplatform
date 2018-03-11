package com.cn.my.model;

import java.util.Date;

public class EvaluateResult {
    private Integer id;

    private Integer keyword_id;

    private String audited_ids;

    private Double recall_ratio;

    private Double precision_ratio;

    private Integer total_num;

    private Integer miss_num;

    private Integer target_num;

    private Date create_time;

    private Date updated_time;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getKeyword_id() {
        return keyword_id;
    }

    public void setKeyword_id(Integer keyword_id) {
        this.keyword_id = keyword_id;
    }

    public String getAudited_ids() {
        return audited_ids;
    }

    public void setAudited_ids(String audited_ids) {
        this.audited_ids = audited_ids;
    }

    public Double getRecall_ratio() {
        return recall_ratio;
    }

    public void setRecall_ratio(Double recall_ratio) {
        this.recall_ratio = recall_ratio;
    }

    public Double getPrecision_ratio() {
        return precision_ratio;
    }

    public void setPrecision_ratio(Double precision_ratio) {
        this.precision_ratio = precision_ratio;
    }

    public Integer getTotal_num() {
        return total_num;
    }

    public void setTotal_num(Integer total_num) {
        this.total_num = total_num;
    }

    public Integer getMiss_num() {
        return miss_num;
    }

    public void setMiss_num(Integer miss_num) {
        this.miss_num = miss_num;
    }

    public Integer getTarget_num() {
        return target_num;
    }

    public void setTarget_num(Integer target_num) {
        this.target_num = target_num;
    }

    public Date getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Date create_time) {
        this.create_time = create_time;
    }

    public Date getUpdated_time() {
        return updated_time;
    }

    public void setUpdated_time(Date updated_time) {
        this.updated_time = updated_time;
    }
}