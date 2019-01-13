package com.pangzi.btmfitness.dao;

import com.pangzi.btmfitness.common.CrudDao;
import com.pangzi.btmfitness.entity.VoteRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author zxw
 * @date 2018-11-14
 */
@Mapper
public interface VoteRecordDao extends CrudDao<VoteRecord> {
}
