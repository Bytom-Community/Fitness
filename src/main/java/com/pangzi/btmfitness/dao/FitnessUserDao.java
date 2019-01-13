package com.pangzi.btmfitness.dao;

import com.pangzi.btmfitness.common.CrudDao;
import com.pangzi.btmfitness.entity.FitnessUser;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author zhangxuewen
 * @date 2018.10.11
 */
@Mapper
public interface FitnessUserDao extends CrudDao<FitnessUser> {
    /**
     * 根据openId查询
     *
     * @param id
     * @return
     */
    FitnessUser getByOpenId(String id);
}
