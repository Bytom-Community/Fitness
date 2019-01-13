package com.pangzi.btmfitness.service;

import com.pangzi.btmfitness.common.CrudService;
import com.pangzi.btmfitness.dao.UnlockPowerDao;
import com.pangzi.btmfitness.entity.UnlockPower;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author zhangxuewen
 * @date 2018.11.07
 */
@Service
public class UnlockPowerService extends CrudService<UnlockPowerDao, UnlockPower> {
    @Autowired
    private UnlockPowerDao unlockPowerDao;

    public List<UnlockPower> findAllList() {
        return unlockPowerDao.findAllList();
    }

    public UnlockPower getUserTodayPower(UnlockPower power) {
        return unlockPowerDao.get(power);
    }
}
