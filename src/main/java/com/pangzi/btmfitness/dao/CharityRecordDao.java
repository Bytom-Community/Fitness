package com.pangzi.btmfitness.dao;

import com.pangzi.btmfitness.common.CrudDao;
import com.pangzi.btmfitness.entity.CharityRecord;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author zxw
 * @date 2018-11-10
 */
@Mapper
public interface CharityRecordDao extends CrudDao<CharityRecord> {
    /**
     * 查找指定捐赠项目的捐赠记录
     *
     * @param record
     * @return
     */
    List<CharityRecord> findListForCharity(CharityRecord record);

    /**
     * 统计能量总数
     *
     * @param record
     * @return
     */
    Long sumPowerNumber(CharityRecord record);

    /**
     * 统计指定项目条数
     *
     * @param record
     * @return
     */
    Long countRecord(CharityRecord record);

    /**
     * 查找指定用户和id的记录
     *
     * @param record
     * @return
     */
    List<CharityRecord> getByOpenIdAndId(CharityRecord record);
}
