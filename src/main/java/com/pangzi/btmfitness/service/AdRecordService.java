package com.pangzi.btmfitness.service;

import com.pangzi.btmfitness.common.CrudService;
import com.pangzi.btmfitness.dao.AdRecordDao;
import com.pangzi.btmfitness.entity.AdRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author zxw
 * @date 2018-11-12
 */
@Service
public class AdRecordService extends CrudService<AdRecordDao, AdRecord> {
    @Autowired
    private AdRecordDao adRecordDao;

    public void endingUpdate(AdRecord record) {
        adRecordDao.endingUpdate(record);
    }

    public Long countRecord(AdRecord record) {
        return adRecordDao.countRecord(record);
    }
}
