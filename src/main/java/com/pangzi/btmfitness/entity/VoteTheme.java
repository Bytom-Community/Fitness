package com.pangzi.btmfitness.entity;

import com.pangzi.btmfitness.common.DataEntity;

/**
 * @author zxw
 * @date 2018-11-10
 */
public class VoteTheme extends DataEntity<VoteTheme> {
    private String openId;
    private String title;
    private String showCover;
    private String summary;
    private String pushName;
    private Integer status;
    private Long overTime;
    private Long voteResult;

    private Long startTime;
    private Long endTime;

    public VoteTheme() {
    }

    public VoteTheme(Long id) {
        this.setId(id);
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getShowCover() {
        return showCover;
    }

    public void setShowCover(String showCover) {
        this.showCover = showCover;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getPushName() {
        return pushName;
    }

    public void setPushName(String pushName) {
        this.pushName = pushName;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Long getOverTime() {
        return overTime;
    }

    public void setOverTime(Long overTime) {
        this.overTime = overTime;
    }

    public Long getVoteResult() {
        return voteResult;
    }

    public void setVoteResult(Long voteResult) {
        this.voteResult = voteResult;
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
