package com.pangzi.btmfitness.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.pangzi.btmfitness.bytom.BtmApiService;
import com.pangzi.btmfitness.entity.*;
import com.pangzi.btmfitness.service.*;
import com.pangzi.btmfitness.utils.WeChatUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.math.RoundingMode;

import static com.pangzi.btmfitness.utils.TimerUtils.getTimesDayEnd;
import static com.pangzi.btmfitness.utils.TimerUtils.getTimesDayZero;

/**
 * @author zhangxuewen
 * @date 2018-10-22
 */
@RestController
@RequestMapping(value = "v1/mini/target")
public class TargetController {
    @Autowired
    private TargetService targetService;
    @Autowired
    private WeChatUtil weChatUtil;
    @Autowired
    private BtmApiService btmApiService;
    @Autowired
    private ContractRecordService contractRecordService;
    @Autowired
    private BtmAccountService btmAccountService;
    @Autowired
    private RunRecordService runRecordService;
    @Autowired
    private UnlockPowerService unlockPowerService;
    @Value("${btm.receiver.address}")
    private String receiverAddress;

    @RequestMapping(value = "run", method = RequestMethod.POST)
    public String getRun(HttpServletRequest request) {
        JSONObject object = new JSONObject();
        String id = request.getParameter("id");
        String encryptedData = request.getParameter("encryptedData");
        String code = request.getParameter("code");
        String iv = request.getParameter("iv");
        String flag = request.getParameter("flag");
        if (StringUtils.isBlank(id) || StringUtils.isBlank(encryptedData)
                || StringUtils.isBlank(code) || StringUtils.isBlank(iv)) {
            object.put("code", 500);
            object.put("msg", "参数缺失");
            return object.toJSONString();
        }
        String str = weChatUtil.decodeEncryptedData(code, encryptedData, iv);
        if (StringUtils.isBlank(str)) {
            object.put("code", 500);
            object.put("msg", "步数数据解密失败");
            return object.toJSONString();
        }
        JSONObject data = JSON.parseObject(str);
        JSONArray list = data.getJSONArray("stepInfoList");
        Long step = list.getJSONObject(list.size() - 1).getLong("step");
        object.put("code", 200);
        object.put("msg", "success");
        object.put("step", step);
        RunRecord runRecord = new RunRecord(id, getTimesDayZero(), getTimesDayEnd());
        RunRecord record = runRecordService.get(runRecord);
        if (record == null) {
            runRecord.setRunStep(step);
            runRecordService.save(runRecord);
        } else {
            record.setRunStep(step);
            runRecordService.updateWithId(record);
        }
        if ("true".equals(flag)) {
            Target target = new Target();
            target.setOpenId(id);
            target.setStartTime(getTimesDayZero());
            target.setEndTime(getTimesDayEnd());
            Target targetResult = targetService.get(target);
            object.put("target", targetResult.getTarget());
            ContractRecord contractRecord = contractRecordService.get(new ContractRecord(targetResult.getId()));
            object.put("hash", contractRecord.getTxId());
            if (step >= targetResult.getTarget()) {
                object.put("finish", true);
            } else {
                object.put("finish", false);
            }

            if (targetResult.getFinishWay() != 0) {
                object.put("isFinished", true);
                object.put("hash", contractRecord.getRemarks().split(":")[1]);
            } else {
                object.put("isFinished", false);
                object.put("hash", contractRecord.getTxId());
            }
        }
        return object.toJSONString();
    }

    @RequestMapping(value = "check")
    public String checkTodayTarget(HttpServletRequest request) {
        JSONObject object = new JSONObject();
        String id = request.getParameter("id");
        if (StringUtils.isBlank(id)) {
            object.put("code", 500);
            object.put("msg", "参数缺失");
            return object.toJSONString();
        }
        Target target = new Target();
        target.setOpenId(id);
        target.setStartTime(getTimesDayZero());
        target.setEndTime(getTimesDayEnd());
        Target targetResult = targetService.get(target);
        if (targetResult == null) {
            object.put("isCreated", false);
        } else {
            object.put("isCreated", true);
        }
        object.put("code", 200);
        object.put("msg", "success");
        return object.toJSONString();
    }

    @RequestMapping(value = "create")
    public String create(HttpServletRequest request) {
        JSONObject object = new JSONObject();
        String id = request.getParameter("id");
        String betAmount = request.getParameter("amount");
        String step = request.getParameter("step");
        String password = request.getParameter("password");
        if (StringUtils.isBlank(id) || StringUtils.isBlank(betAmount) ||
                StringUtils.isBlank(step) || StringUtils.isBlank(password)) {
            object.put("code", 500);
            object.put("msg", "参数缺失");
            return object.toJSONString();
        }
        Target target = new Target();
        target.setOpenId(id);
        target.setStartTime(getTimesDayZero());
        target.setEndTime(getTimesDayEnd());
        Target targetResult = targetService.get(target);
        if (targetResult != null) {
            object.put("code", 500);
            object.put("msg", "今日已打卡");
            return object.toJSONString();
        }
        BtmAccount account = btmAccountService.get(new BtmAccount(id));
        JSONObject jsonObject = btmApiService.getBalanceByAccount(account.getAccountAlias());
        if (new BigDecimal(betAmount).compareTo(new BigDecimal(jsonObject.getString("FITNESS"))) > 0) {
            object.put("code", 500);
            object.put("msg", "余额不足");
            return object.toJSONString();
        }

        //TODO 将用户抵押的资产转移到FITNESS账号
        Long amounts = new BigDecimal(betAmount).multiply(new BigDecimal(100000000L)).longValue();
        int fee = btmApiService.estimateTransactionGas(account.getAccountAlias(), "FITNESS", receiverAddress, amounts);
        if (fee == -1) {
            fee = 1000000;
        }
        if (new BigDecimal(fee).compareTo(
                new BigDecimal(jsonObject.getString("BTM"))
                        .multiply(new BigDecimal(100000000L))) > 0) {
            object.put("code", 500);
            object.put("msg", "BTM余额不足够区块手续费");
            return object.toJSONString();
        }
        if (!btmApiService.validatePassword(account.getKeyPublic(), password)) {
            object.put("code", 500);
            object.put("msg", "密码错误");
            return object.toJSONString();
        }
        btmApiService.makeTransaction(account.getAccountAlias(), "FITNESS", receiverAddress,
                amounts, Long.valueOf(fee), password);

        String payout = getRate(Long.valueOf(step));
        String wimAmount = new BigDecimal(betAmount).multiply(new BigDecimal(payout))
                .setScale(2, RoundingMode.DOWN).toString();
        //编译、发布合约
        String program = btmApiService.compileSmartContract(Integer.valueOf(step));
        if (StringUtils.isBlank(program)) {
            object.put("code", 500);
            object.put("msg", "编译合约失败");
            return object.toJSONString();
        }
        Long contractAmount = new BigDecimal(wimAmount).multiply(new BigDecimal(100000000)).longValue();
        String txId = btmApiService.pushSmartContract(contractAmount, program);
        if (StringUtils.isBlank(txId)) {
            object.put("code", 500);
            object.put("msg", "发布合约失败");
            return object.toJSONString();
        }

        // 保存打卡记录
        target.setUpdateTime(System.currentTimeMillis());
        target.setBetAmount(betAmount);
        target.setTarget(Long.valueOf(step));
        target.setPayout(payout);
        target.setWinAmount(wimAmount);
        target.setIsFinish("false");
        target.setFinishWay(0);
        targetService.save(target);

        // 保存合约记录
        ContractRecord contractRecord = new ContractRecord();
        contractRecord.setProgram(program);
        contractRecord.setAmount(contractAmount);
        contractRecord.setOpenId(id);
        contractRecord.setTargetId(target.getId());
        contractRecord.setTxId(txId);
        contractRecord.setValue(Integer.valueOf(step));
        contractRecord.setIsUnlock(0);
        String unspentId = btmApiService.getContractId(txId, program);
        contractRecord.setUnspentId(unspentId);
        contractRecordService.save(contractRecord);


        object.put("code", 200);
        object.put("msg", "success");
        object.put("txId", txId);
        return object.toJSONString();
    }

    @RequestMapping(value = "unlock")
    public String unlockTarget(HttpServletRequest request) {
        JSONObject object = new JSONObject();
        String id = request.getParameter("id");
        String step = request.getParameter("step");
        if (StringUtils.isBlank(id) || StringUtils.isBlank(step)) {
            object.put("code", 500);
            object.put("msg", "参数缺失");
            return object.toJSONString();
        }
        Target target = new Target();
        target.setOpenId(id);
        target.setStartTime(getTimesDayZero());
        target.setEndTime(getTimesDayEnd());
        Target targetResult = targetService.get(target);
        ContractRecord contractRecord = contractRecordService.get(new ContractRecord(targetResult.getId()));
        if (contractRecord.getIsUnlock() == 1) {
            object.put("code", 500);
            object.put("msg", "目标已达成");
            return object.toJSONString();
        }
        if (targetResult.getTarget() > Long.valueOf(step)) {
            object.put("code", 500);
            object.put("msg", "目标未达成");
            return object.toJSONString();
        }
        String hex = Long.toHexString(Long.valueOf(step));
        if (hex.length() % 2 != 0) {
            hex = "0" + hex;
        }
        String accountProgram = btmAccountService.get(new BtmAccount(id)).getControlProgram();
        String txId = btmApiService.unLockSmartContract(contractRecord.getAmount(), accountProgram, contractRecord.getUnspentId(),
                hex);
        if (txId == null) {
            object.put("code", 500);
            object.put("msg", "请求区块错误");
            return object.toJSONString();
        }
        // 更新target
        targetResult.setWinRun(Long.valueOf(step));
        targetResult.setIsFinish("true");
        targetResult.setFinishWay(1);
        targetService.updateWithId(targetResult);
        // 更新 contractRecord
        contractRecord.setIsUnlock(1);
        contractRecord.setUnlockValue(hex);
        contractRecord.setRemarks("解锁哈希:" + txId);
        contractRecordService.updateWithId(contractRecord);

        UnlockPower power = new UnlockPower();
        power.setUnlockTxId(txId);
        power.setContractRecordId(contractRecord.getId());
        power.setOpenId(id);
        power.setTargetId(targetResult.getId());
        power.setUnspentPower(contractRecord.getValue().longValue());
        power.setSpentPower(0L);
        power.setPower(contractRecord.getValue().longValue());
        power.setIsOwner(1);
        power.setCreateDay(getTimesDayZero());
        unlockPowerService.save(power);

        object.put("code", 200);
        object.put("msg", "success");
        object.put("txId", txId);
        return object.toJSONString();
    }

    private String getRate(Long step) {
        BigDecimal total = new BigDecimal(30000L);
        BigDecimal stepB = new BigDecimal(step);
        BigDecimal rate = stepB.divide(total, 2, BigDecimal.ROUND_HALF_DOWN);

        if (step <= 8000) {
            rate = new BigDecimal(1).add(rate).setScale(2, BigDecimal.ROUND_HALF_DOWN);
        } else if (step <= 12000) {

            rate = new BigDecimal(1.1).add(rate).setScale(2, BigDecimal.ROUND_HALF_DOWN);
        } else if (step <= 20000) {
            rate = new BigDecimal(1.1).add(rate).setScale(2, BigDecimal.ROUND_HALF_DOWN);
        } else if (step <= 25000) {
            rate = new BigDecimal(1.5).add(rate).setScale(2, BigDecimal.ROUND_HALF_DOWN);
        } else {
            rate = new BigDecimal(1.8).add(rate).setScale(2, BigDecimal.ROUND_HALF_DOWN);
        }
        return rate.toString();
    }
}
