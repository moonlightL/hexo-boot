package com.light.hexo.business.portal.model;

import com.light.hexo.common.util.DateUtil;
import lombok.Getter;
import lombok.Setter;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * @Author MoonlightL
 * @ClassName: SiteMap
 * @ProjectName hexo-boot
 * @Description: 站点地图
 * @DateTime 2021/1/9 22:09
 */
@Setter
@Getter
public class SiteMap implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * url https://www.xxx.com
     */
    private String loc;

    /**
     * 最后更新时间 yyyy-MM-dd
     */
    private LocalDate lastmod;

    /**
     * 更新速度 always hourly daily weekly monthly yearly never
     */
    private String changefreq;

    /**
     * 权重 1.0 0.9 0.8
     */
    private String priority;


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("<url>");
        sb.append("<loc>" + loc + "</loc>");
        sb.append("<lastmod>" + DateUtil.ldToStr(lastmod) + "</lastmod>");
        sb.append("<changefreq>" + changefreq + "</changefreq>");
        sb.append("<priority>" + priority + "</priority>");
        sb.append("</url>");
        return sb.toString();
    }

    public SiteMap() {
    }

    public SiteMap(String loc) {
        this.loc = loc;
        this.lastmod = LocalDate.now();
        this.changefreq = "daily";
        this.priority = "1.0";
    }

    public SiteMap(String loc, LocalDate lastmod, String changefreq, String priority) {
        this.loc = loc;
        this.lastmod = lastmod;
        this.changefreq = changefreq;
        this.priority = priority;
    }

}
