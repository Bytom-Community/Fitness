package com.pangzi.btmfitness.common;

import java.util.List;

/**
 * @author 张学文
 * @date 2018-03-22
 */
public interface CrudDao<T> {
    /**
     * 获取单条数据
     *
     * @param entity
     * @return
     */
    T get(T entity);

    /**
     * 查询数据列表，如果需要分页，请设置分页对象，如：entity.setPage(new Page<T>());
     *
     * @param entity
     * @return
     */
    List<T> findList(T entity);

    /**
     * 插入数据
     *
     * @param entity
     * @return
     */
    Long insert(T entity);

    /**
     * 更新数据
     *
     * @param entity
     * @return
     */
    Long update(T entity);

    /**
     * 删除数据（一般为逻辑删除，更新del_flag字段为1）
     *
     * @param id
     * @return
     * @see public int delete(T entity)
     */
    Long delete(String id);

    /**
     * 查询数目
     *
     * @param entity
     * @return
     */
    Long count(T entity);
}
