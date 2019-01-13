package com.pangzi.btmfitness.entity;

import com.pangzi.btmfitness.common.DataEntity;

/**
 * @author zhangxuewen
 * @date 2018-10-28
 */
public class ContractRecord extends DataEntity<ContractRecord> {
    private String openId;
    private Long targetId;
    private String program;
    private String txId;
    private String unspentId;
    /**
     * 部署合约时的参数
     */
    private Integer value;
    /**
     * 合约中的金额，单位NEU
     */
    private Long amount;
    /**
     * 是否已解锁，0 未解锁；1 已解锁
     */
    private Integer isUnlock;
    /**
     * 解锁合约的值，16进制
     */
    private String unlockValue;

    public ContractRecord() {
    }

    public ContractRecord(Long targetId) {
        this.targetId = targetId;
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

    public String getProgram() {
        return program;
    }

    public void setProgram(String program) {
        this.program = program;
    }

    public String getTxId() {
        return txId;
    }

    public void setTxId(String txId) {
        this.txId = txId;
    }

    public String getUnspentId() {
        return unspentId;
    }

    public void setUnspentId(String unspentId) {
        this.unspentId = unspentId;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public Integer getIsUnlock() {
        return isUnlock;
    }

    public void setIsUnlock(Integer isUnlock) {
        this.isUnlock = isUnlock;
    }

    public String getUnlockValue() {
        return unlockValue;
    }

    public void setUnlockValue(String unlockValue) {
        this.unlockValue = unlockValue;
    }
}
