package com.pangzi.btmfitness.service;

import com.pangzi.btmfitness.common.CrudService;
import com.pangzi.btmfitness.dao.FitnessUserDao;
import com.pangzi.btmfitness.entity.FitnessUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author zhangxuewen
 * @date 2018.10.11
 */
@Service
public class FitnessUserService extends CrudService<FitnessUserDao, FitnessUser> {
    @Autowired
    private FitnessUserDao fitnessUserDao;

    public FitnessUser getByOpenId(String id) {
        return fitnessUserDao.getByOpenId(id);
    }
}
