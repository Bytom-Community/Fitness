package com.pangzi.btmfitness.entity;

import com.pangzi.btmfitness.common.DataEntity;

import java.math.BigDecimal;

/**
 * @author zxw
 * @date 2018-11-10
 */
public class CharityRecord extends DataEntity<CharityRecord> {
    private Long charityId;
    private String openId;
    private String txId;
    private Long powerNumber;
    private String memo;
    private BigDecimal charityToken;
    private String proveHash;
    private BigDecimal donateAmount;

    public CharityRecord() {
    }

    public CharityRecord(Long charityId) {
        this.charityId = charityId;
    }

    public Long getCharityId() {
        return charityId;
    }

    public void setCharityId(Long charityId) {
        this.charityId = charityId;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String getTxId() {
        return txId;
    }

    public void setTxId(String txId) {
        this.txId = txId;
    }

    public Long getPowerNumber() {
        return powerNumber;
    }

    public void setPowerNumber(Long powerNumber) {
        this.powerNumber = powerNumber;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public BigDecimal getCharityToken() {
        return charityToken;
    }

    public void setCharityToken(BigDecimal charityToken) {
        this.charityToken = charityToken;
    }

    public String getProveHash() {
        return proveHash;
    }

    public void setProveHash(String proveHash) {
        this.proveHash = proveHash;
    }

    public BigDecimal getDonateAmount() {
        return donateAmount;
    }

    public void setDonateAmount(BigDecimal donateAmount) {
        this.donateAmount = donateAmount;
    }
}
