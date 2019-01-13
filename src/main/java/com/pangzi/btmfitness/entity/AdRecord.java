package com.pangzi.btmfitness.entity;

import com.pangzi.btmfitness.common.DataEntity;

import java.math.BigDecimal;

/**
 * @author zxw
 * @date 2018-11-12
 */
public class AdRecord extends DataEntity<AdRecord> {
    private Long adId;
    private String openId;
    private Long target;
    private Long enterPower;
    private Long currentPower;
    private String finished;
    private BigDecimal winToken;

    public AdRecord() {
    }

    public AdRecord(Long adId) {
        this.adId = adId;
    }

    public Long getAdId() {
        return adId;
    }

    public void setAdId(Long adId) {
        this.adId = adId;
    }

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

    public Long getEnterPower() {
        return enterPower;
    }

    public void setEnterPower(Long enterPower) {
        this.enterPower = enterPower;
    }

    public Long getCurrentPower() {
        return currentPower;
    }

    public void setCurrentPower(Long currentPower) {
        this.currentPower = currentPower;
    }

    public String getFinished() {
        return finished;
    }

    public void setFinished(String finished) {
        this.finished = finished;
    }

    public BigDecimal getWinToken() {
        return winToken;
    }

    public void setWinToken(BigDecimal winToken) {
        this.winToken = winToken;
    }
}
