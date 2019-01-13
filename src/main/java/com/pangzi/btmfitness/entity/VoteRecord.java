package com.pangzi.btmfitness.entity;

import com.pangzi.btmfitness.common.DataEntity;

/**
 * @author zxw
 * @date 2018-11-14
 */
public class VoteRecord extends DataEntity<VoteRecord> {
    private Long themeId;
    private Long optionId;
    private String openId;
    private Long powerNumber;
    private Integer powerDay;
    private Long powerTotal;
    private Long powerCreateDay;

    public Long getThemeId() {
        return themeId;
    }

    public void setThemeId(Long themeId) {
        this.themeId = themeId;
    }

    public Long getOptionId() {
        return optionId;
    }

    public void setOptionId(Long optionId) {
        this.optionId = optionId;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public Long getPowerNumber() {
        return powerNumber;
    }

    public void setPowerNumber(Long powerNumber) {
        this.powerNumber = powerNumber;
    }

    public Integer getPowerDay() {
        return powerDay;
    }

    public void setPowerDay(Integer powerDay) {
        this.powerDay = powerDay;
    }

    public Long getPowerTotal() {
        return powerTotal;
    }

    public void setPowerTotal(Long powerTotal) {
        this.powerTotal = powerTotal;
    }

    public Long getPowerCreateDay() {
        return powerCreateDay;
    }

    public void setPowerCreateDay(Long powerCreateDay) {
        this.powerCreateDay = powerCreateDay;
    }
}
