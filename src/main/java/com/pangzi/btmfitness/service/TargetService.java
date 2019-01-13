package com.pangzi.btmfitness.service;

import com.pangzi.btmfitness.common.CrudService;
import com.pangzi.btmfitness.dao.TargetDao;
import com.pangzi.btmfitness.entity.Target;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author zhangxuewen
 * @date 2018.10.23
 */
@Service
public class TargetService extends CrudService<TargetDao, Target> {
    @Autowired
    private TargetDao targetDao;

    public List<Target> findListByMonth(Target entity) {
        return targetDao.findListByMonth(entity);
    }
}
