package com.light.hexo.common.model.bing;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @Author MoonlightL
 * @ClassName: BingPic
 * @ProjectName hexo-boot
 * @Description: bing 图片
 * @DateTime 2021/2/4 17:22
 */
@Setter
@Getter
@ToString
public class WebPic {

    private String code;

    private String imgurl;

    private String width;

    private String height;

}
