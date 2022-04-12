package com.light.hexo.common.model;

import com.light.hexo.mapper.model.Album;
import com.light.hexo.common.base.BaseRequest;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @Author MoonlightL
 * @ClassName: AlbumRequest
 * @ProjectName hexo-boot
 * @Description: 专辑请求对象
 * @DateTime 2021/12/21, 0021 15:47
 */
@Setter
@Getter
public class AlbumRequest extends BaseRequest<Album> {

    /**
     * 主键
     */
    @NotNull(message = "主键不能为空", groups = {BaseRequest.Update.class})
    private Integer id;

    /**
     * 名称
     */
    @NotEmpty(message = "专辑名称不能为空", groups = {BaseRequest.Save.class, BaseRequest.Update.class})
    private String name;

    /**
     * 内容类型 1：图片 2：视频
     */
    private Integer detailType;

    /**
     * 访问类型 1: 公开 0: 私密
     */
    private Integer visitType;

    /**
     * 访问密码
     */
    private String visitCode;

    /**
     * 封面
     */
    private String coverUrl;

    /**
     * 备注
     */
    private String remark;
}
