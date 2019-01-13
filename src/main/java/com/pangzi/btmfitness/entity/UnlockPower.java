package com.pangzi.btmfitness.entity;

import com.pangzi.btmfitness.common.DataEntity;

/**
 * @author zhangxuewen
 * @date 2018.11.07
 */
public class UnlockPower extends DataEntity<UnlockPower> {
    private String openId;
    private Long targetId;
    private Long contractRecordId;
    private String unlockTxId;
    private Long power;
    private Long spentPower;
    private Long unspentPower;
    private int isOwner;
    private Long createDay;

    public UnlockPower() {
    }

    public UnlockPower(Long targetId) {
        this.targetId = targetId;
    }

    public UnlockPower(String openId) {
        this.openId = openId;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public Long getTargetId() {
        return targetId;
    }

    public void setTargetId(Long targetId) {
        this.targetId = targetId;
    }

    public Long getContractRecordId() {
        return contractRecordId;
    }

    public void setContractRecordId(Long contractRecordId) {
        this.contractRecordId = contractRecordId;
    }

    public String getUnlockTxId() {
        return unlockTxId;
    }

    public void setUnlockTxId(String unlockTxId) {
        this.unlockTxId = unlockTxId;
    }

    public Long getPower() {
        return power;
    }

    public void setPower(Long power) {
        this.power = power;
    }

    public Long getSpentPower() {
        return spentPower;
    }

    public void setSpentPower(Long spentPower) {
        this.spentPower = spentPower;
    }

    public Long getUnspentPower() {
        return unspentPower;
    }

    public void setUnspentPower(Long unspentPower) {
        this.unspentPower = unspentPower;
    }

    public int getIsOwner() {
        return isOwner;
    }

    public void setIsOwner(int isOwner) {
        this.isOwner = isOwner;
    }

    public Long getCreateDay() {
        return createDay;
    }

    public void setCreateDay(Long createDay) {
        this.createDay = createDay;
    }
}
