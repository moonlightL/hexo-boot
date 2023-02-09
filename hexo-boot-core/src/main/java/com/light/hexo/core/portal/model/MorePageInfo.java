package com.light.hexo.core.portal.model;

import com.github.pagehelper.Page;
import lombok.ToString;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author MoonlightL
 * @ClassName: MorePageInfo
 * @ProjectName hexo-boot
 * @Description: 扩展版本分页
 * @DateTime 2023/2/2, 0002 14:46
 */
@ToString
public class MorePageInfo extends HexoPageInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 带省略号的分页
     * @param data
     * @param navigatePages  页码数量
     */
    public MorePageInfo(Object data, int navigatePages) {
        this.navigatePages = navigatePages;
        this.data = data;

        if (data instanceof Page) {
            Page page = (Page) data;
            this.pageNum = page.getPageNum();
            this.pageSize = page.getPageSize();
            this.total = (int) page.getTotal();
            this.pages = page.getPages();
            this.setNavigatepageNums();
        }
    }

    /**
     * 带省略号的分页
     * @param pageInfo
     * @param navigatePages 页码数量
     */
    public MorePageInfo(HexoPageInfo pageInfo, int navigatePages) {
        this.navigatePages = navigatePages;
        this.data = pageInfo.getData();
        this.pageNum = pageInfo.getPageNum();
        this.pageSize = pageInfo.getPageSize();
        this.total = pageInfo.getTotal();
        this.setTotalPage();
        this.setNavigatepageNums();
    }

    public void setNavigatepageNums() {
        List<Integer> pageBar = new ArrayList<>();
        if (this.pages <= this.navigatePages) {
            for (int i = 1; i <= this.pages; i++) {
                pageBar.add(i);
            }
        } else if (this.pageNum <= 8) {
            for (int i = 1; i <= 9; i++) {
                pageBar.add(i);
            }
            pageBar.add(-1);
            pageBar.add(this.pages);
        } else if (this.pageNum > this.pages - 7) {
            pageBar.add(1);
            pageBar.add(-1);
            for (int i = this.pages - 8; i <= this.pages; i++) {
                pageBar.add(i);
            }
        } else {
            pageBar.add(1);
            pageBar.add(-1);
            for (int i = this.pageNum - 3; i <= this.pageNum + 3; i++) {
                pageBar.add(i);
            }
            pageBar.add(-1);
            pageBar.add(this.pages);
        }

        int[] tmp = new int[pageBar.size()];
        for (int i = 0; i < pageBar.size(); i++) {
            tmp[i] = pageBar.get(i);
        }

        this.navigatepageNums = tmp;
    }
}
