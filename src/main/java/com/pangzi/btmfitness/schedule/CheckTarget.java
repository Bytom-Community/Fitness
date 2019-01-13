package com.pangzi.btmfitness.schedule;

import com.pangzi.btmfitness.bytom.BtmApiService;
import com.pangzi.btmfitness.entity.*;
import com.pangzi.btmfitness.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.pangzi.btmfitness.utils.TimerUtils.getTimesDayEnd;
import static com.pangzi.btmfitness.utils.TimerUtils.getTimesDayZero;

/**
 * @author zxw
 * @date 2018-11-05
 */
@Component
public class CheckTarget {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private TargetService targetService;
    @Autowired
    private ContractRecordService contractRecordService;
    @Autowired
    private RunRecordService runRecordService;
    @Autowired
    private BtmApiService btmApiService;
    @Autowired
    private BtmAccountService btmAccountService;
    @Autowired
    private UnlockPowerService unlockPowerService;

    @Scheduled(cron = "00 55 23 * * ?")
    public void checkTodayTarget() {
        logger.info("FITNESS系统开始进行今日链上打卡结算...");
        Target target = new Target();
        target.setStartTime(getTimesDayZero());
        target.setEndTime(getTimesDayEnd());
        target.setFinishWay(0);
        List<Target> targetList = targetService.findList(target);
        int successTotal = 0, failTotal = 0;
        for (Target item : targetList) {
            RunRecord record = new RunRecord(item.getOpenId(), getTimesDayZero(), getTimesDayEnd());
            RunRecord runRecord = runRecordService.get(record);
            String stepHex = Long.toHexString(runRecord.getRunStep());
            if (stepHex.length() % 2 != 0) {
                stepHex = "0" + stepHex;
            }
            ContractRecord contractRecord = contractRecordService.get(new ContractRecord(item.getId()));
            BtmAccount account = btmAccountService.get(new BtmAccount(item.getOpenId()));
            if (runRecord.getRunStep() < item.getTarget()) {
                // 用户未达成目标，系统解锁合约
                systemUnlockContract(item, contractRecord, account);
                failTotal++;
            } else {
                String txId = btmApiService.unLockSmartContract(contractRecord.getAmount(), account.getControlProgram(),
                        contractRecord.getUnspentId(), stepHex);
                if (txId != null) {
                    contractRecord.setUnlockValue(stepHex);
                    contractRecord.setIsUnlock(1);
                    contractRecord.setRemarks("解锁哈希:" + txId);
                    updateContractRecord(contractRecord);
                    item.setWinRun(record.getRunStep());
                    updateTarget(item, 1);
                    UnlockPower power = new UnlockPower();
                    power.setUnlockTxId(txId);
                    power.setContractRecordId(contractRecord.getId());
                    power.setOpenId(account.getOpenId());
                    power.setTargetId(item.getId());
                    power.setUnspentPower(contractRecord.getValue().longValue());
                    power.setSpentPower(0L);
                    power.setIsOwner(1);
                    power.setPower(contractRecord.getValue().longValue());
                    power.setCreateDay(getTimesDayZero());
                    unlockPowerService.save(power);
                    successTotal++;
                } else {
                    //TODO 处理解锁失败
                }
            }
        }
        logger.info("FITNESS结算完成，共计结算 {} 条，达成目标人数 {}，失败人数 {}", targetList.size(), successTotal, failTotal);
    }

    private void systemUnlockContract(Target target, ContractRecord record, BtmAccount account) {
        String hex = Long.toHexString(record.getValue() + 10);
        if (hex.length() % 2 != 0) {
            hex = "0" + hex;
        }
        String id = btmApiService.unLockSmartContract(record.getAmount(), account.getControlProgram(),
                record.getUnspentId(), hex);
        if (id != null) {
            record.setUnlockValue(hex);
            record.setIsUnlock(1);
            record.setRemarks("解锁哈希:" + id);
            updateContractRecord(record);
            updateTarget(target, 0);
        }
        UnlockPower power = new UnlockPower();
        power.setUnlockTxId(id);
        power.setContractRecordId(record.getId());
        power.setOpenId(account.getOpenId());
        power.setTargetId(target.getId());
        power.setUnspentPower(record.getValue().longValue());
        power.setSpentPower(0L);
        power.setIsOwner(0);
        power.setPower(record.getValue().longValue());
        power.setCreateDay(getTimesDayZero());
        unlockPowerService.save(power);
    }

    private void updateTarget(Target target, int flag) {
        if (flag == 1) {
            target.setIsFinish("true");
            target.setFinishWay(2);
        } else {
            target.setIsFinish("false");
            target.setFinishWay(2);
        }
        targetService.updateWithId(target);
    }

    private void updateContractRecord(ContractRecord contractRecord) {
        contractRecordService.updateWithId(contractRecord);
    }


}
