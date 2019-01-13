package com.pangzi.btmfitness.service;

import com.pangzi.btmfitness.common.CrudService;
import com.pangzi.btmfitness.dao.CharityRecordDao;
import com.pangzi.btmfitness.entity.CharityRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author zxw
 * @date 2018-11-10
 */
@Service
public class CharityRecordService extends CrudService<CharityRecordDao, CharityRecord> {
    @Autowired
    private CharityRecordDao charityRecordDao;

    public List<CharityRecord> findListForCharity(CharityRecord record) {
        return charityRecordDao.findListForCharity(record);
    }

    public Long sumPowerNumber(CharityRecord record) {
        return charityRecordDao.sumPowerNumber(record);
    }

    public Long countRecord(CharityRecord record) {
        return charityRecordDao.countRecord(record);
    }

    public List<CharityRecord> getByOpenIdAndId(CharityRecord record) {
        return charityRecordDao.getByOpenIdAndId(record);
    }
}
