package com.pangzi.btmfitness.dao;

import com.pangzi.btmfitness.common.CrudDao;
import com.pangzi.btmfitness.entity.BtmAccount;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author zhangxuewen
 * @date 2018.10.20
 */
@Mapper
public interface BtmAccountDao extends CrudDao<BtmAccount> {
    /**
     * 查找用户别名是否存在
     *
     * @param alias
     * @return
     */
    BtmAccount getByAccountAlias(String alias);
}
