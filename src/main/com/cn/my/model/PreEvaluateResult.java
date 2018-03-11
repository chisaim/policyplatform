package com.cn.my.model;

import java.util.Date;

public class PreEvaluateResult {
    private Integer taskid;

    private Integer prepolicy_fileid;

    private Integer premessage_fileid;

    private Integer prepolicy_number;

    private Integer premessage_number;

    private Integer target_message;

    private Integer target_policy;

    private Double recall_ratio;

    private Double precision_ratio;

    private Date task_starttime;

    private Date task_endtime;

    private Integer task_status;

    private Integer userid;

    public Integer getTaskid() {
        return taskid;
    }

    public void setTaskid(Integer taskid) {
        this.taskid = taskid;
    }

    public Integer getPrepolicy_fileid() {
        return prepolicy_fileid;
    }

    public void setPrepolicy_fileid(Integer prepolicy_fileid) {
        this.prepolicy_fileid = prepolicy_fileid;
    }

    public Integer getPremessage_fileid() {
        return premessage_fileid;
    }

    public void setPremessage_fileid(Integer premessage_fileid) {
        this.premessage_fileid = premessage_fileid;
    }

    public Integer getPrepolicy_number() {
        return prepolicy_number;
    }

    public void setPrepolicy_number(Integer prepolicy_number) {
        this.prepolicy_number = prepolicy_number;
    }

    public Integer getPremessage_number() {
        return premessage_number;
    }

    public void setPremessage_number(Integer premessage_number) {
        this.premessage_number = premessage_number;
    }

    public Integer getTarget_message() {
        return target_message;
    }

    public void setTarget_message(Integer target_message) {
        this.target_message = target_message;
    }

    public Integer getTarget_policy() {
        return target_policy;
    }

    public void setTarget_policy(Integer target_policy) {
        this.target_policy = target_policy;
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

    public Date getTask_starttime() {
        return task_starttime;
    }

    public void setTask_starttime(Date task_starttime) {
        this.task_starttime = task_starttime;
    }

    public Date getTask_endtime() {
        return task_endtime;
    }

    public void setTask_endtime(Date task_endtime) {
        this.task_endtime = task_endtime;
    }

    public Integer getTask_status() {
        return task_status;
    }

    public void setTask_status(Integer task_status) {
        this.task_status = task_status;
    }

    public Integer getUserid() {
        return userid;
    }

    public void setUserid(Integer userid) {
        this.userid = userid;
    }
}