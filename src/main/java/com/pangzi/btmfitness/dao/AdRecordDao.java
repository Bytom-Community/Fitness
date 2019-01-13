package com.pangzi.btmfitness.dao;

import com.pangzi.btmfitness.common.CrudDao;
import com.pangzi.btmfitness.entity.AdRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author zxw
 * @date 2018-11-12
 */
@Mapper
public interface AdRecordDao extends CrudDao<AdRecord> {
    /**
     * 广告结束或用户完成时更新
     *
     * @param record
     */
    void endingUpdate(AdRecord record);

    /**
     * 统计指定项目条数
     *
     * @param record
     * @return
     */
    Long countRecord(AdRecord record);
}
