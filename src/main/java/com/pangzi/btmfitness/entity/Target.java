package com.pangzi.btmfitness.entity;

import com.pangzi.btmfitness.common.DataEntity;

/**
 * @author zhangxuewen
 * @date 2018.10.23
 */
public class Target extends DataEntity<Target> {
    private String openId;
    private Long target;
    private String betAmount;
    private String payout;
    private String winAmount;
    private String isFinish;
    private Integer finishWay;
    private Long winRun;

    private Long startTime;
    private Long endTime;

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public Long getTarget() {
        return target;
    }

    public void setTarget(Long target) {
        this.target = target;
    }

    public String getBetAmount() {
        return betAmount;
    }

    public void setBetAmount(String betAmount) {
        this.betAmount = betAmount;
    }

    public String getPayout() {
        return payout;
    }

    public void setPayout(String payout) {
        this.payout = payout;
    }

    public String getWinAmount() {
        return winAmount;
    }

    public void setWinAmount(String winAmount) {
        this.winAmount = winAmount;
    }

    public String getIsFinish() {
        return isFinish;
    }

    public void setIsFinish(String isFinish) {
        this.isFinish = isFinish;
    }

    public Integer getFinishWay() {
        return finishWay;
    }

    public void setFinishWay(Integer finishWay) {
        this.finishWay = finishWay;
    }

    public Long getWinRun() {
        return winRun;
    }

    public void setWinRun(Long winRun) {
        this.winRun = winRun;
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
