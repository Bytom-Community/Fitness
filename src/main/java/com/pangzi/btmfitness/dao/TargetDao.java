package com.pangzi.btmfitness.dao;

import com.pangzi.btmfitness.common.CrudDao;
import com.pangzi.btmfitness.entity.Target;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author zhangxuewen
 * @date 2018.10.23
 */
@Mapper
public interface TargetDao extends CrudDao<Target> {
    /**
     * 查询用户指定一个月的记录
     *
     * @param entity
     * @return
     */
    List<Target> findListByMonth(Target entity);
}
