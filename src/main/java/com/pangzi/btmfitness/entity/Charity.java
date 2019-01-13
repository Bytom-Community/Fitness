package com.pangzi.btmfitness.entity;

import com.pangzi.btmfitness.common.DataEntity;

/**
 * @author zxw
 * @date 2018-11-10
 */
public class Charity extends DataEntity<Charity> {
    private String cover;
    private String title;
    private String summary;
    private String owner;
    private String content;
    /**
     * 状态 0 未开始；1 进行中；2 已结束
     */
    private Integer status;
    /**
     * 捐赠类型 1 能量捐；2 直接捐赠
     */
    private Integer charityType;
    /**
     * charityType为2时有效，直接捐赠的筹集方地址
     */
    private String ownerAddress;
    private Long startTime;
    private Long endTime;

    public Charity() {
    }

    public Charity(Long id) {
        this.setId(id);
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getCharityType() {
        return charityType;
    }

    public void setCharityType(Integer type) {
        this.charityType = type;
    }

    public String getOwnerAddress() {
        return ownerAddress;
    }

    public void setOwnerAddress(String ownerAddress) {
        this.ownerAddress = ownerAddress;
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
