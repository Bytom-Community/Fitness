package com.pangzi.btmfitness.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.pangzi.btmfitness.bytom.BtmApiService;
import com.pangzi.btmfitness.common.PageQuery;
import com.pangzi.btmfitness.entity.AdRecord;
import com.pangzi.btmfitness.entity.AdTheme;
import com.pangzi.btmfitness.entity.UnlockPower;
import com.pangzi.btmfitness.service.AdRecordService;
import com.pangzi.btmfitness.service.AdThemeService;
import com.pangzi.btmfitness.service.UnlockPowerService;
import com.pangzi.btmfitness.utils.Digests;
import com.pangzi.btmfitness.utils.TimerUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author zxw
 * @date 2018-11-12
 */
@RestController
@RequestMapping(value = "v1/mini/ad")
public class AdController {

    @Autowired
    private BtmApiService btmApiService;
    @Autowired
    private AdThemeService adThemeService;
    @Autowired
    private AdRecordService adRecordService;
    @Autowired
    private UnlockPowerService unlockPowerService;

    @RequestMapping(value = "create")
    public String adCreate(HttpServletRequest request) {
        JSONObject object = new JSONObject();
        String id = request.getParameter("id");
        String key = request.getParameter("key");
        String cover = request.getParameter("cover");
        String title = request.getParameter("title");
        String powerTarget = request.getParameter("powerTarget");
        String rewardToken = request.getParameter("rewardToken");
        String enterPower = request.getParameter("enterPower");
        if (StringUtils.isBlank(id) || StringUtils.isBlank(key) || StringUtils.isBlank(cover)
                || StringUtils.isBlank(title) || StringUtils.isBlank(powerTarget) || StringUtils.isBlank(enterPower)
                || StringUtils.isBlank(rewardToken)) {
            object.put("code", 500);
            object.put("msg", "参数缺失");
            return object.toJSONString();
        }
        if (!StringUtils.isNumeric(powerTarget) || !StringUtils.isNumeric(rewardToken) || !StringUtils.isNumeric(enterPower)) {
            object.put("code", 500);
            object.put("msg", "参数格式错误");
            return object.toJSONString();
        }
        try {
            Integer.parseInt(powerTarget);
        } catch (Exception e) {
            object.put("code", 500);
            object.put("msg", "能量数请不要大于" + Integer.MAX_VALUE);
            return object.toJSONString();
        }
        String keyHash = Digests.digestSha256(key);
        AdTheme adTheme = new AdTheme();
        adTheme.setStatus(1);
        adTheme.setCover(cover);
        adTheme.setContractKey(key);
        adTheme.setContractKeyHash(keyHash);
        adTheme.setTitle(title);
        adTheme.setPowerTarget(Long.valueOf(powerTarget));
        adTheme.setEnterPower(Long.valueOf(enterPower));
        adTheme.setRewardToken(new BigDecimal(rewardToken));
        String program = btmApiService.compileAdSmartContract(keyHash, Integer.valueOf(powerTarget));
        if (program == null) {
            object.put("code", 500);
            object.put("msg", "合约编译错误");
            return object.toJSONString();
        }
        adTheme.setLockProgram(program);
        Long rewards = new BigDecimal(rewardToken).multiply(new BigDecimal(100000000L)).longValue();
        String txId = btmApiService.pushAdContract(rewards, program);
        if (txId == null) {
            object.put("code", 500);
            object.put("msg", "合约发布错误");
            return object.toJSONString();
        }
        adTheme.setTxId(txId);
        String unspentId = btmApiService.getContractId(txId, program);
        if (txId == null) {
            object.put("code", 500);
            object.put("msg", "区块请求错误");
            return object.toJSONString();
        }
        adTheme.setUnspentId(unspentId);
        adThemeService.save(adTheme);
        object.put("code", 200);
        object.put("msg", "success");
        object.put("txId", txId);
        return object.toJSONString();
    }

    @RequestMapping(value = "enter")
    public String userEnterAd(HttpServletRequest request) {
        JSONObject object = new JSONObject();
        String id = request.getParameter("id");
        String adId = request.getParameter("adId");
        String target = request.getParameter("target");
        String power = request.getParameter("power");
        if (StringUtils.isBlank(id) || StringUtils.isBlank(adId)) {
            object.put("code", 500);
            object.put("msg", "参数缺失");
            return object.toJSONString();
        }
        if (!StringUtils.isNumeric(target) || !StringUtils.isNumeric(power) || !StringUtils.isNumeric(adId)) {
            object.put("code", 500);
            object.put("msg", "参数格式错误");
            return object.toJSONString();
        }
        if (Long.valueOf(target) == 0 && Long.valueOf(power) == 0) {
            object.put("code", 500);
            object.put("msg", "参数错误");
            return object.toJSONString();
        }
        List<UnlockPower> list = unlockPowerService.findList(new UnlockPower(id));
        AdRecord record = new AdRecord();
        record.setAdId(Long.valueOf(adId));
        record.setOpenId(id);
        AdTheme adTheme = adThemeService.get(new AdTheme(Long.valueOf(adId)));
        AdRecord adRecord = adRecordService.get(record);
        if (adTheme == null || adTheme.getStatus() != 1) {
            object.put("code", 500);
            object.put("msg", "广告不存在");
            return object.toJSONString();
        }
        if (adRecord == null) {
            adRecord = new AdRecord();
            Long totalPower = 0L;
            for (UnlockPower unlockPower : list) {
                totalPower += unlockPower.getUnspentPower();
            }
            if (totalPower < adTheme.getEnterPower()) {
                object.put("code", 500);
                object.put("msg", "报名能量不足");
                return object.toJSONString();
            }
            int len = list.size() - 1;
            Long powerCache = Long.valueOf(target);
            for (int i = len; i > 0; i--) {
                UnlockPower unlockPower = list.get(i);
                if (unlockPower.getUnspentPower() >= powerCache) {
                    unlockPower.setUnspentPower(unlockPower.getUnspentPower() - powerCache);
                    unlockPower.setSpentPower(unlockPower.getSpentPower() + powerCache);
                    unlockPowerService.updateWithId(unlockPower);
                    break;
                } else {
                    unlockPower.setSpentPower(unlockPower.getSpentPower() + unlockPower.getUnspentPower());
                    powerCache = powerCache - unlockPower.getUnspentPower();
                    unlockPower.setUnspentPower(0L);
                    unlockPowerService.updateWithId(unlockPower);
                }
            }
            // 创建报名
            adRecord.setOpenId(id);
            adRecord.setTarget(Long.valueOf(target));
            adRecord.setCurrentPower(0L);
            adRecord.setFinished("false");
            adRecord.setAdId(adTheme.getId());
            adRecord.setEnterPower(adTheme.getEnterPower());
            adRecordService.save(adRecord);
            object.put("msg", "成功报名参与");
        } else {
            if (adRecord.getFinished().equals("true")) {
                object.put("code", 500);
                object.put("msg", "已完成打卡");
                return object.toJSONString();
            }
            // 打卡能量
            UnlockPower today = list.get(0);
            if (!today.getCreateDay().equals(TimerUtils.getTimesDayZero())) {
                object.put("code", 500);
                object.put("msg", "能量不足");
                return object.toJSONString();
            }
            if (today.getUnspentPower() < Long.valueOf(power)) {
                object.put("code", 500);
                object.put("msg", "能量不足");
                return object.toJSONString();
            }
            today.setSpentPower(today.getSpentPower() + Long.valueOf(power));
            today.setUnspentPower(today.getUnspentPower() - Long.valueOf(power));
            unlockPowerService.updateWithId(today);
            adRecord.setCurrentPower(adRecord.getCurrentPower() + Long.valueOf(power));
            if (adRecord.getCurrentPower() >= adRecord.getTarget()) {
                adRecord.setFinished("true");
                object.put("msg", "恭喜您已完成打卡");
            } else {
                object.put("msg", "成功增加能量");
            }
            adRecordService.updateWithId(adRecord);
        }
        object.put("code", 200);
        return object.toJSONString();
    }

    @RequestMapping(value = "page")
    public String getPage(PageQuery<AdTheme> pageQuery, HttpServletRequest request) {
        JSONObject object = new JSONObject();
        AdTheme adTheme = new AdTheme();
        adTheme.setStatus(1);
        PageQuery<AdTheme> page = adThemeService.findPage(pageQuery, adTheme);
        List<AdTheme> list = page.getList();
        JSONArray array = new JSONArray();
        for (AdTheme theme : list) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", theme.getId());
            jsonObject.put("cover", theme.getCover());
            jsonObject.put("enterNum", adRecordService.countRecord(new AdRecord(theme.getId())));
            array.add(jsonObject);
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("list", array);
        jsonObject.put("total", page.getTotal());
        jsonObject.put("pageSize", page.getPageSize());
        jsonObject.put("pageNo", page.getPageNo());
        jsonObject.put("pages", page.getPages());
        object.put("code", 200);
        object.put("msg", "success");
        object.put("page", jsonObject);
        return object.toJSONString();
    }

    @RequestMapping(value = "info")
    public String getAdInfo(HttpServletRequest request) {
        JSONObject object = new JSONObject();
        String id = request.getParameter("id");
        String adId = request.getParameter("adId");
        if (!StringUtils.isNumeric(adId) || StringUtils.isBlank(adId) || StringUtils.isBlank(id)) {
            object.put("code", 500);
            object.put("msg", "参数错误");
            return object.toJSONString();
        }
        AdTheme adTheme = adThemeService.get(new AdTheme(Long.valueOf(adId)));
        if (adTheme == null || adTheme.getStatus() != 1) {
            object.put("code", 500);
            object.put("msg", "广告不存在");
            return object.toJSONString();
        }
        JSONObject ad = new JSONObject();
        ad.put("title", adTheme.getTitle());
        ad.put("cover", adTheme.getCover());
        ad.put("content", adTheme.getContent());
        ad.put("powerTarget", adTheme.getPowerTarget());
        ad.put("rewardToken", adTheme.getRewardToken());
        ad.put("enterPower", adTheme.getEnterPower());
        ad.put("txId", adTheme.getTxId());
        ad.put("id", adTheme.getId());
        object.put("currentStatus", parseRecord(adTheme.getId()));
        object.put("userStatus", isUserEnter(id, adTheme.getId()));
        object.put("code", 200);
        object.put("msg", "success");
        object.put("info", ad);
        return object.toJSONString();
    }

    private JSONObject parseRecord(Long id) {
        JSONObject object = new JSONObject();
        List<AdRecord> list = adRecordService.findList(new AdRecord(id));
        Long totalPower = 0L;
        for (AdRecord adRecord : list) {
            totalPower += adRecord.getCurrentPower();
        }
        object.put("enterNumber", list.size());
        object.put("totalPower", totalPower);
        return object;
    }

    private JSONObject isUserEnter(String openId, Long id) {
        JSONObject object = new JSONObject();
        AdRecord record = new AdRecord();
        record.setAdId(id);
        record.setOpenId(openId);
        AdRecord adRecord = adRecordService.get(record);
        if (adRecord == null) {
            object.put("isEnter", false);
        } else {
            object.put("isEnter", true);
            object.put("isFinish", adRecord.getFinished());
            object.put("target", adRecord.getTarget());
            object.put("current", adRecord.getCurrentPower());
        }
        return object;
    }
}
