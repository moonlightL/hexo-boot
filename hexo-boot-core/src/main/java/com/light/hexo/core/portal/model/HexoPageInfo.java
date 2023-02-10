package com.light.hexo.core.portal.model;

import lombok.ToString;
import java.io.Serializable;

/**
 * @Author MoonlightL
 * @ClassName: HexoPageInfo
 * @ProjectName hexo-boot
 * @Description: 封装逻辑分页数据
 * @DateTime 2020/9/21 11:13
 */
@ToString
public class HexoPageInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    protected int pageNum;

    protected int pageSize;

    protected int total;

    protected int pages;

    protected Object data;

    protected int[] navigatepageNums;

    protected int navigatePages = 10;

    protected boolean hasPreviousPage;

    protected boolean hasNextPage;

    public HexoPageInfo() {}

    /**
     * 常规分页
     * @param pageNum
     * @param pageSize
     * @param total
     * @param data
     */
    public HexoPageInfo(int pageNum, int pageSize, int total, Object data) {
        this.data = data;
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.total = total;

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

    protected void setTotalPage() {
        if (this.total % this.pageSize == 0) {
            this.pages = this.total / this.pageSize;
        } else {
            this.pages = this.total / this.pageSize + 1;
        }
    }

    protected void setNavigatepageNums() {
        int startPage;
        int endPage;
        int[] pageBar;
        if (this.pages <= this.navigatePages) {
            pageBar = new int[this.pages];
            startPage = 1;
            endPage = pages;
        } else {
            pageBar = new int[this.navigatePages];
            startPage = this.pageNum - 4;
            endPage = this.pageNum + 5;

            if (startPage < 1) {
                startPage = 1;
                endPage = this.navigatePages;
            }

            if (endPage > this.pages) {
                endPage = this.pages;
                startPage = this.pages - (this.navigatePages - 1);
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
