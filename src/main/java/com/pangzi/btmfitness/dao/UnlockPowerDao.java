package com.pangzi.btmfitness.dao;

import com.pangzi.btmfitness.common.CrudDao;
import com.pangzi.btmfitness.entity.UnlockPower;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author zhangxuewen
 * @date 2018.11.07
 */
@Mapper
public interface UnlockPowerDao extends CrudDao<UnlockPower> {
    /**
     * 查询全部能量数据
     *
     * @return
     */
    List<UnlockPower> findAllList();

    /**
     * 获取用户当天能量
     *
     * @param power
     * @return
     */
    UnlockPower getUserTodayPower(UnlockPower power);
}
