package com.light.hexo.core.portal.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @Author MoonlightL
 * @ClassName: BasicInfo
 * @ProjectName hexo-boot
 * @Description: 基础信息
 * @DateTime 2022/6/6, 0006 19:11
 */
@Setter
@Getter
public class BasicInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;

    private String avatar;

    public BasicInfo(String name, String avatar) {
        this.name = name;
        this.avatar = avatar;
    }
}
