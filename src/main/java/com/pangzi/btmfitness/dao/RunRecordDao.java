package com.pangzi.btmfitness.dao;

import com.pangzi.btmfitness.common.CrudDao;
import com.pangzi.btmfitness.entity.RunRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author zhangxuewen
 * @date 2018.11.05
 */
@Mapper
public interface RunRecordDao extends CrudDao<RunRecord> {
}
