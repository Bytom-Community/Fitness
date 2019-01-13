package com.pangzi.btmfitness.common;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Lists;
import com.pangzi.btmfitness.utils.SqlFixInjectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author 张学文
 * @date 2018-03-22
 */
@Transactional(readOnly = true, rollbackFor = Exception.class)
public class CrudService<D extends CrudDao<T>, T extends DataEntity<T>> {
    @Autowired
    protected D dao;

    /**
     * 获取单条数据
     *
     * @param id
     * @return
     */
    public T get(T entity) {
        return dao.get(entity);
    }

    /**
     * 查询列表数据
     *
     * @param entity
     * @return
     */
    public List<T> findList(T entity) {
        return dao.findList(entity);
    }

    /**
     * 分页查询
     *
     * @param pageQuery
     * @param entity
     * @return
     */
    public PageQuery<T> findPage(PageQuery pageQuery, T entity) {
        String orderBy = pageQuery.getOrderBy();
        PageHelper.orderBy(SqlFixInjectionUtils.fixOrderBy(orderBy));
        PageHelper.startPage(pageQuery.getPageNo(), pageQuery.getPageSize());
        Page<T> page = (Page<T>) dao.findList(entity);
        List<T> list = Lists.newArrayList(page);
        PageQuery pageResult = new PageQuery();
        pageResult.setList(list);
        pageResult.setOrderBy(orderBy);
        pageResult.setPageNo(pageQuery.getPageNo());
        pageResult.setPageSize(page.getPageSize());
        pageResult.setPages(page.getPages());
        pageResult.setTotal(page.getTotal());
        return pageResult;
    }

    /**
     * 保存数据（插入或更新）
     *
     * @param entity
     */
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public Long save(T entity) {
        entity.setCreateTime(System.currentTimeMillis());
        return dao.insert(entity);
    }

    /**
     * 保存数据（插入或更新）
     *
     * @param entity
     */
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public Long updateWithId(T entity) {
        entity.setUpdateTime(System.currentTimeMillis());
        return dao.update(entity);
    }

    /**
     * 删除数据
     *
     * @param id
     */
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public void delete(String id) {
        dao.delete(id);
    }

    /**
     * 查询数目
     *
     * @param entity
     */
    public Long count(T entity) {
        return dao.count(entity);
    }
}
