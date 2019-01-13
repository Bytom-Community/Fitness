package com.pangzi.btmfitness.common;

import com.pangzi.btmfitness.utils.SqlFixInjectionUtils;

import java.io.Serializable;
import java.util.List;

/**
 * @param <T>
 * @author zhangxuewen
 */
public class PageQuery<T> implements Serializable {
    private String orderBy;
    /**
     * 每页多少个元素
     */
    private Integer pageSize = 10;
    /**
     * 当前页码
     */
    private Integer pageNo = 1;
    /**
     * 元素总个数
     */
    private Long total;
    /**
     * 总页码
     */
    private Integer pages;
    /**
     * 元素列表
     */
    private List<T> list;

    public PageQuery() {

    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public String getOrderBy() {
        return SqlFixInjectionUtils.fixOrderBy(orderBy);
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public Integer getPageNo() {
        return pageNo;
    }

    public void setPageNo(Integer pageNo) {
        this.pageNo = pageNo;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Integer getPages() {
        return pages;
    }

    public void setPages(Integer pages) {
        this.pages = pages;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }
}
