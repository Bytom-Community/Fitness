package com.pangzi.btmfitness.entity;

import com.pangzi.btmfitness.common.DataEntity;

/**
 * @author zhangxuewen
 * @date 2018.10.20
 */
public class BtmAccount extends DataEntity<BtmAccount> {
    private String accountId;
    private String openId;
    private String keySecret;
    private String keyAlias;
    private String keyPublic;
    private String accountAlias;
    private String receiverAddress;
    private String controlProgram;

    public BtmAccount(){}

    public BtmAccount(String id){
        this.openId = id;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String getKeySecret() {
        return keySecret;
    }

    public void setKeySecret(String keySecret) {
        this.keySecret = keySecret;
    }

    public String getKeyAlias() {
        return keyAlias;
    }

    public void setKeyAlias(String keyAlias) {
        this.keyAlias = keyAlias;
    }

    public String getKeyPublic() {
        return keyPublic;
    }

    public void setKeyPublic(String keyPublic) {
        this.keyPublic = keyPublic;
    }

    public String getAccountAlias() {
        return accountAlias;
    }

    public void setAccountAlias(String accountAlias) {
        this.accountAlias = accountAlias;
    }

    public String getReceiverAddress() {
        return receiverAddress;
    }

    public void setReceiverAddress(String receiverAddress) {
        this.receiverAddress = receiverAddress;
    }

    public String getControlProgram() {
        return controlProgram;
    }

    public void setControlProgram(String controlProgram) {
        this.controlProgram = controlProgram;
    }
}
