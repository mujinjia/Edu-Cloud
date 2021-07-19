package com.jlee.model;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * 带分页数据返回值model
 *
 * @author jlee
 */
public class PageResult<T> implements Serializable {
    private List<T> result;
    private Long total;

    private PageResult(List<T> result, Long total) {
        this.result = result;
        this.total = total;
    }

    public PageResult() {
    }

    public static <T> PageResult<T> of(List<T> result, Long total) {
        return new PageResult<>(result, total);
    }

    public List<T> getResult() {
        return this.result;
    }

    public void setResult(List<T> result) {
        this.result = result;
    }

    public Long getTotal() {
        return this.total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    protected boolean canEqual(Object other) {
        return other instanceof PageResult;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PageResult<?> that = (PageResult<?>) o;
        return Objects.equals(result, that.result) && Objects.equals(total, that.total);
    }

    @Override
    public int hashCode() {
        return Objects.hash(result, total);
    }

    @Override
    public String toString() {
        return "PageResult(result=" + this.getResult() + ", total=" + this.getTotal() + ")";
    }
}