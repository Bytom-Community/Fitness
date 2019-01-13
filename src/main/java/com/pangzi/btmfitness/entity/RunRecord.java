package com.pangzi.btmfitness.entity;

import com.pangzi.btmfitness.common.DataEntity;

/**
 * @author zhangxuewen
 * @date 2018.11.05
 */
public class RunRecord extends DataEntity<RunRecord> {
    private Long runStep;
    private String openId;

    private Long startTime;
    private Long endTime;

    public RunRecord() {
    }

    public RunRecord(String id, Long startTime, Long endTime) {
        this.openId = id;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Long getRunStep() {
        return runStep;
    }

    public void setRunStep(Long runStep) {
        this.runStep = runStep;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }
}
