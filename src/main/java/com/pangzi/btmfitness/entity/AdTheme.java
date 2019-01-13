package com.pangzi.btmfitness.entity;

import com.pangzi.btmfitness.common.DataEntity;

import java.math.BigDecimal;

/**
 * @author zxw
 * @date 2018-11-12
 */
public class AdTheme extends DataEntity<AdTheme> {
    private String cover;
    private String title;
    private String content;
    private String txId;
    private Long powerTarget;
    private BigDecimal rewardToken;
    private String contractKey;
    private String contractKeyHash;
    private String lockProgram;
    private String unspentId;
    private String unlockHash;
    private Long unlockValue;
    private String unlockValueHex;
    private int status;
    private Long enterPower;
    private Long startTime;
    private Long endTime;

    public AdTheme() {
    }

    public AdTheme(Long id) {
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTxId() {
        return txId;
    }

    public void setTxId(String txId) {
        this.txId = txId;
    }

    public Long getPowerTarget() {
        return powerTarget;
    }

    public void setPowerTarget(Long powerTarget) {
        this.powerTarget = powerTarget;
    }

    public BigDecimal getRewardToken() {
        return rewardToken;
    }

    public void setRewardToken(BigDecimal rewardToken) {
        this.rewardToken = rewardToken;
    }

    public String getContractKey() {
        return contractKey;
    }

    public void setContractKey(String contractKey) {
        this.contractKey = contractKey;
    }

    public String getContractKeyHash() {
        return contractKeyHash;
    }

    public void setContractKeyHash(String contractKeyHash) {
        this.contractKeyHash = contractKeyHash;
    }

    public String getLockProgram() {
        return lockProgram;
    }

    public void setLockProgram(String lockProgram) {
        this.lockProgram = lockProgram;
    }

    public String getUnspentId() {
        return unspentId;
    }

    public void setUnspentId(String unspentId) {
        this.unspentId = unspentId;
    }

    public String getUnlockHash() {
        return unlockHash;
    }

    public void setUnlockHash(String unlockHash) {
        this.unlockHash = unlockHash;
    }

    public Long getUnlockValue() {
        return unlockValue;
    }

    public void setUnlockValue(Long unlockValue) {
        this.unlockValue = unlockValue;
    }

    public String getUnlockValueHex() {
        return unlockValueHex;
    }

    public void setUnlockValueHex(String unlockValueHex) {
        this.unlockValueHex = unlockValueHex;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Long getEnterPower() {
        return enterPower;
    }

    public void setEnterPower(Long enterPower) {
        this.enterPower = enterPower;
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
