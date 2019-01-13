package com.pangzi.btmfitness.dao;

import com.pangzi.btmfitness.common.CrudDao;
import com.pangzi.btmfitness.entity.ContractRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author zhangxuewen
 * @date 2018-10-28
 */
@Mapper
public interface ContractRecordDao extends CrudDao<ContractRecord> {
}
