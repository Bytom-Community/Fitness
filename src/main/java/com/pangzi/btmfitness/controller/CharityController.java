package com.pangzi.btmfitness.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.pangzi.btmfitness.bytom.BtmApiService;
import com.pangzi.btmfitness.bytom.HexUtil;
import com.pangzi.btmfitness.common.PageQuery;
import com.pangzi.btmfitness.entity.*;
import com.pangzi.btmfitness.service.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;

import static com.pangzi.btmfitness.controller.PowerController.formatNumber;

/**
 * @author zxw
 * @date 2018-11-10
 */
@RestController
@RequestMapping(value = "v1/mini/charity")
public class CharityController {
    @Autowired
    private CharityService charityService;
    @Autowired
    private CharityRecordService charityRecordService;
    @Autowired
    private BtmAccountService btmAccountService;
    @Autowired
    private BtmApiService btmApiService;
    @Autowired
    private FitnessUserService userService;
    @Autowired
    private UnlockPowerService unlockPowerService;

    private final String BTM = "BTM";
    Logger logger = LoggerFactory.getLogger(getClass());

    @RequestMapping(value = "page")
    public String getPage(PageQuery<Character> pageQuery, HttpServletRequest request) {
        JSONObject object = new JSONObject();
        Charity charity = new Charity();
        charity.setStatus(1);
        PageQuery<Charity> page = charityService.findPage(pageQuery, charity);
        List<Charity> list = page.getList();
        JSONArray array = new JSONArray();
        for (int i = 0; i < list.size(); i++) {
            JSONObject jsonObject = new JSONObject();
            Charity charity1 = list.get(i);
            jsonObject.put("id", charity1.getId());
            jsonObject.put("cover", charity1.getCover());
            jsonObject.put("title", charity1.getTitle());
            jsonObject.put("summary", charity1.getSummary());
            jsonObject.put("type", charity1.getCharityType());
            Long total = 0L;
            if (charity1.getCharityType() == 1) {
                total = charityRecordService.sumPowerNumber(new CharityRecord(charity1.getId()));
            } else {
                total = charityRecordService.countRecord(new CharityRecord(charity1.getId()));
            }
            jsonObject.put("total", formatNumber(total.toString()));
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

    @RequestMapping(value = "charityInfo")
    public String getCharityInfo(HttpServletRequest request) {
        JSONObject object = new JSONObject();
        String id = request.getParameter("charityId");
        String openId = request.getParameter("id");
        if (StringUtils.isBlank(id) || StringUtils.isBlank(openId)) {
            object.put("code", 500);
            object.put("msg", "参数缺失");
            return object.toJSONString();
        }
        if (!StringUtils.isNumeric(id)) {
            object.put("code", 500);
            object.put("msg", "参数格式错误");
            return object.toJSONString();
        }
        Charity charity = charityService.get(new Charity(Long.valueOf(id)));
        if (charity == null) {
            object.put("code", 500);
            object.put("msg", "项目不存在");
            return object.toJSONString();
        }
        if (charity.getStatus() != 1) {
            object.put("code", 500);
            object.put("msg", "项目未开始或已结束");
            return object.toJSONString();
        }
        object.put("code", 200);
        object.put("msg", "success");
        object.put("info", charity);
        Long total = charityRecordService.countRecord(new CharityRecord(Long.valueOf(id)));
        object.put("total", formatNumber(total.toString()));
        CharityRecord record = new CharityRecord(Long.valueOf(id));
        record.setOpenId(openId);
        List<CharityRecord> charityRecordList = charityRecordService.getByOpenIdAndId(record);
        JSONArray array = new JSONArray();
        for (CharityRecord charityRecord : charityRecordList) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("proveHash", charityRecord.getProveHash());
            if (charity.getCharityType() == 1) {
                jsonObject.put("donate", charityRecord.getPowerNumber());
            } else {
                jsonObject.put("donate", charityRecord.getTxId());
            }
            array.add(jsonObject);
        }
        object.put("myRecord", array);
        if (charity.getCharityType() == 1) {
            Long sumPower = charityRecordService.sumPowerNumber(new CharityRecord(charity.getId()));
            object.put("amount", formatNumber(sumPower.toString()));
            charity.setOwnerAddress("无");
        } else {
            List<CharityRecord> list = charityRecordService.findListForCharity(new CharityRecord(charity.getId()));
            BigDecimal amount = BigDecimal.ZERO;
            for (int i = 0; i < list.size(); i++) {
                amount = amount.add(list.get(i).getDonateAmount());
            }
            object.put("amount", formatNumber(amount.toString()));
        }

        return object.toJSONString();
    }

    @RequestMapping(value = "donateCharity")
    public String donateCharity(HttpServletRequest request) {
        JSONObject object = new JSONObject();
        String id = request.getParameter("id");
        String charityId = request.getParameter("charityId");
        String amount = request.getParameter("amount");
        String password = request.getParameter("password");
        if (StringUtils.isBlank(id) || StringUtils.isBlank(charityId)
                || StringUtils.isBlank(amount) || StringUtils.isBlank(password)) {
            object.put("code", 500);
            object.put("msg", "参数缺失");
            return object.toJSONString();
        }
        Charity charity = charityService.get(new Charity(Long.valueOf(charityId)));
        if (charity == null || charity.getStatus() != 1 || charity.getCharityType() != 2) {
            object.put("code", 500);
            object.put("msg", "项目异常");
            return object.toJSONString();
        }
        BigDecimal amounts = new BigDecimal(amount);
        BtmAccount account = btmAccountService.get(new BtmAccount(id));
        String str = btmApiService.getBalanceByAliasAndSymbol(account.getAccountAlias(), "BTM");
        BigDecimal symbolAmount = JSON.parseObject(str).getBigDecimal("BTM");
        if (symbolAmount.compareTo(amounts) < 0) {
            object.put("code", 500);
            object.put("msg", "余额不足");
            return object.toJSONString();
        }
        Boolean result = btmApiService.validatePassword(account.getKeyPublic(), password);
        if (!result) {
            object.put("code", 500);
            object.put("msg", "密码错误");
            return object.toJSONString();
        }
        Long amountLong = amounts.multiply(new BigDecimal(100000000L)).longValue();
        Integer gas = btmApiService.estimateTransactionGas(account.getAccountAlias(), BTM, charity.getOwnerAddress(), amountLong);
        if (gas == -1) {
            gas = 1500000;
        }
        String txId = btmApiService.makeTransaction(account.getAccountAlias(), BTM, charity.getOwnerAddress(),
                amountLong, gas.longValue(), password);
        if (txId == null) {
            object.put("code", 500);
            object.put("msg", "UTXOs锁定");
            return object.toJSONString();
        }
        FitnessUser user = userService.getByOpenId(id);
        String memo = user.getNickName() + " 参与'" + charity.getTitle() + "'的爱心捐赠，本次共捐赠" + amount + "BTM。";
        memo = memo + todayString();
        String memoHex = HexUtil.StringToHex(memo);
        Long retireAmount = amountLong / 10;
        String hash = btmApiService.charityRetire(retireAmount, memoHex);
        if (hash == null) {
            logger.error("{} donate prove error.And donate hash:{}", user.getNickName(), txId);
            hash = "签写区块捐赠证明失败";
        }
        CharityRecord record = new CharityRecord();
        record.setCharityId(charity.getId());
        record.setDonateAmount(amounts);
        record.setMemo(memo);
        record.setOpenId(id);
        record.setProveHash(hash);
        record.setTxId(txId);
        record.setCharityToken(new BigDecimal(retireAmount).divide(new BigDecimal(100000000L)
                .setScale(8, BigDecimal.ROUND_HALF_DOWN)));
        charityRecordService.save(record);
        object.put("code", 200);
        object.put("msg", "success");
        object.put("txId", txId);
        object.put("hash", hash);
        return object.toJSONString();
    }

    @RequestMapping(value = "donatePower")
    public String donatePower(HttpServletRequest request) {
        JSONObject object = new JSONObject();
        String id = request.getParameter("id");
        String charityId = request.getParameter("charityId");
        String amount = request.getParameter("amount");
        if (StringUtils.isBlank(id) || StringUtils.isBlank(charityId)
                || StringUtils.isBlank(amount)) {
            object.put("code", 500);
            object.put("msg", "参数缺失");
            return object.toJSONString();
        }
        Charity charity = charityService.get(new Charity(Long.valueOf(charityId)));
        if (charity == null || charity.getStatus() != 1 || charity.getCharityType() != 1) {
            object.put("code", 500);
            object.put("msg", "项目异常");
            return object.toJSONString();
        }
        Long power = Long.valueOf(amount);
        List<UnlockPower> list = unlockPowerService.findList(new UnlockPower(id));
        Long totalPower = 0L;
        for (UnlockPower unlockPower : list) {
            totalPower += unlockPower.getUnspentPower();
        }
        if (totalPower < power) {
            object.put("code", 500);
            object.put("msg", "能量不足");
            return object.toJSONString();
        }
        int len = list.size() - 1;
        Long powerCache = power;
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
        FitnessUser user = userService.getByOpenId(id);
        String memo = user.getNickName() + " 参与'" + charity.getTitle() + "'的能量捐赠，本次共捐赠" + amount + "点能量。";
        memo = memo + todayString();
        String memoHex = HexUtil.StringToHex(memo);
        Long retireAmount = new BigDecimal(power).divide(new BigDecimal(10000L))
                .multiply(new BigDecimal(100000000L)).longValue();
        String hash = btmApiService.charityRetire(retireAmount, memoHex);
        if (hash == null) {
            logger.error("{} donate prove error.And donate item:{},power:{}", user.getNickName(), charityId, power);
            hash = "签写区块捐赠证明失败";
        }
        CharityRecord record = new CharityRecord();
        record.setCharityId(charity.getId());
        record.setMemo(memo);
        record.setOpenId(id);
        record.setProveHash(hash);
        record.setPowerNumber(power);
        record.setCharityToken(new BigDecimal(retireAmount).divide(new BigDecimal(100000000L)
                .setScale(8, BigDecimal.ROUND_HALF_DOWN)));
        charityRecordService.save(record);
        object.put("code", 200);
        object.put("msg", "success");
        object.put("hash", hash);
        return object.toJSONString();
    }

    private String todayString() {
        Calendar now = Calendar.getInstance();
        int year = now.get(Calendar.YEAR);
        int month = now.get(Calendar.MONTH) + 1;
        int day = now.get(Calendar.DAY_OF_MONTH);
        int hour = now.get(Calendar.HOUR_OF_DAY);
        int minute = now.get(Calendar.MINUTE);
        int second = now.get(Calendar.SECOND);
        String memo = "公元" + year + "年" + month + "月" + day + "日" + hour + "时" + minute + "分" + second + "秒，特此捐赠记录。";
        return memo;
    }
}
