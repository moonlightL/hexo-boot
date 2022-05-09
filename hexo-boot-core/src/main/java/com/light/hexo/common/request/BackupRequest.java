package com.light.hexo.common.request;

import com.light.hexo.mapper.model.Backup;
import com.light.hexo.common.base.BaseRequest;
import lombok.Getter;
import lombok.Setter;

/**
 * @Author MoonlightL
 * @ClassName: BackupRequest
 * @ProjectName hexo-boot
 * @Description: 备份请求对象
 * @DateTime 2020/9/8 18:44
 */
@Setter
@Getter
public class BackupRequest extends BaseRequest<Backup> {

    /**
     * 主键
     */
    private Integer id;

    /**
     * 名称
     */
    private String name;
}
