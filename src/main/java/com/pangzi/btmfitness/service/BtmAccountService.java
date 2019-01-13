package com.pangzi.btmfitness.service;

import com.pangzi.btmfitness.common.CrudService;
import com.pangzi.btmfitness.dao.BtmAccountDao;
import com.pangzi.btmfitness.entity.BtmAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author zhangxuewen
 * @date 2018.10.20
 */
@Service
public class BtmAccountService extends CrudService<BtmAccountDao, BtmAccount> {
    @Autowired
    private BtmAccountDao btmAccountDao;

    public BtmAccount getByAccountAlias(String alias) {
        return btmAccountDao.getByAccountAlias(alias);
    }
}
