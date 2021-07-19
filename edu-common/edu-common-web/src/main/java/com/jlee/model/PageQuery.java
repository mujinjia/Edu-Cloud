package com.jlee.model;

import java.io.Serializable;

/**
 * 分页参数接收model
 *
 * @author jlee
 */
public class PageQuery implements Serializable {
    public static final Integer DEFAULT_PAGE_INDEX = 1;
    public static final Integer DEFAULT_PAGE_SIZE = 10;
    private Integer pageIndex;
    private Integer pageSize;

    public PageQuery() {
    }

    public Integer getPageIndex() {
        return this.pageIndex == null ? DEFAULT_PAGE_INDEX : this.pageIndex;
    }

    public void setPageIndex(Integer pageIndex) {
        this.pageIndex = pageIndex;
    }

    public Integer getPageSize() {
        return this.pageSize == null ? DEFAULT_PAGE_SIZE : this.pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    @Override
    public String toString() {
        return "PageQuery(pageIndex=" + this.getPageIndex() + ", pageSize=" + this.getPageSize() + ")";
    }
}