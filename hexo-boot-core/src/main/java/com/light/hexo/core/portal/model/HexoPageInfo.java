package com.light.hexo.core.portal.model;

import lombok.ToString;

import java.io.Serializable;

/**
 * @Author MoonlightL
 * @ClassName: HexoPageInfo
 * @ProjectName hexo-boot
 * @Description: 归档信息
 * @DateTime 2020/9/21 11:13
 */
@ToString
public class HexoPageInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    private int pageNum;

    private int pageSize;

    private int total;

    private int pages;

    private Object data;

    private int[] navigatepageNums;

    private int navigatePages = 10;

    private boolean hasPreviousPage;

    private boolean hasNextPage;

    public HexoPageInfo(int pageNum, int pageSize, int total, Object data) {
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.total = total;
        this.data = data;

        this.setTotalPage();
        this.setNavigatepageNums();
    }

    public int getPageNum() {
        return pageNum;
    }

    public int getPageSize() {
        return pageSize;
    }

    public int getTotal() {
        return total;
    }

    public int getPages() {
        return pages;
    }

    public Object getData() {
        return data;
    }

    private void setTotalPage() {
        if (this.total % this.pageSize == 0) {
            this.pages = this.total / this.pageSize;
        } else {
            this.pages = this.total / this.pageSize + 1;
        }
    }

    private void setNavigatepageNums() {
        int startPage;
        int endPage;
        int[] pageBar;
        if (this.pages <= navigatePages) {
            pageBar = new int[this.pages];
            startPage = 1;
            endPage = pages;
        } else {
            pageBar = new int[10];
            startPage = this.pageNum - 4;
            endPage = this.pageNum + 5;

            if (startPage < 1) {
                startPage = 1;
                endPage = 10;
            }

            if (endPage > this.pages) {
                endPage = this.pages;
                startPage = this.pages - 9;
            }
        }

        int index = 0;
        for (int i = startPage; i <= endPage; i++) {
            pageBar[index++] = i;
        }

        this.navigatepageNums = pageBar;
    }

    public int[] getNavigatepageNums() {
        return navigatepageNums;
    }

    public boolean isHasPreviousPage() {
        return this.pageNum > 1;
    }


    public boolean isHasNextPage() {
        return this.pageNum < this.pages;
    }
}
