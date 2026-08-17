package com.nabgha.book.common.domain;

import java.util.List;

public record PageResult<T>(List<T> content, int pageNumber, int pageSize, long totalElements) {

    public int getTotalPages() {
        return pageSize == 0 ? 0 : (int) Math.ceil((double) totalElements / pageSize);
    }

    public boolean isFirst() {
        return pageNumber == 0;
    }

    public boolean isLast() {
        return pageNumber >= getTotalPages() - 1;
    }
}