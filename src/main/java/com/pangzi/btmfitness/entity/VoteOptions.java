package com.pangzi.btmfitness.entity;

import com.pangzi.btmfitness.common.DataEntity;

/**
 * @author zxw
 * @date 2018-11-10
 */
public class VoteOptions extends DataEntity<VoteOptions> {
    private Long voteId;
    private String image;
    private String content;
    private int showType;

    private Long startTime;
    private Long endTime;

    public VoteOptions() {
    }

    public VoteOptions(Long voteId) {
        this.voteId = voteId;
    }

    public Long getVoteId() {
        return voteId;
    }

    public void setVoteId(Long voteId) {
        this.voteId = voteId;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getShowType() {
        return showType;
    }

    public void setShowType(int showType) {
        this.showType = showType;
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
