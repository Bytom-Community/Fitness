package com.pangzi.btmfitness.dao;

import com.pangzi.btmfitness.common.CrudDao;
import com.pangzi.btmfitness.entity.Charity;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author zxw
 * @date 2018-11-10
 */
@Mapper
public interface CharityDao extends CrudDao<Charity> {
}
