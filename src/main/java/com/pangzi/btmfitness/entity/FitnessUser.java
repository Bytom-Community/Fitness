package com.pangzi.btmfitness.entity;

import com.pangzi.btmfitness.common.DataEntity;

/**
 * @author zhangxuewen
 * @date 2018.10.11
 */
public class FitnessUser extends DataEntity<FitnessUser> {
    private String nickName;
    private String gender;
    private String country;
    private String province;
    private String city;
    private String language;
    private String avatarUrl;
    private String openId;
    private String btmAccount;
    private String privateKey;


    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String getBtmAccount() {
        return btmAccount;
    }

    public void setBtmAccount(String btmAccount) {
        this.btmAccount = btmAccount;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }
}
