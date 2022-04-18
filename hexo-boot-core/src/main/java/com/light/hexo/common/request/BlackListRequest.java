package com.light.hexo.common.request;

import com.light.hexo.mapper.model.Blacklist;
import com.light.hexo.common.base.BaseRequest;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * @Author MoonlightL
 * @ClassName: BlackListRequest
 * @ProjectName hexo-boot
 * @Description: 黑名单请求对象
 * @DateTime 2020/9/9 15:33
 */
@Setter
@Getter
public class BlackListRequest extends BaseRequest<Blacklist> {

    /**
     * 主键
     */
    @NotNull(message = "主键不能为空", groups = {BaseRequest.Update.class})
    private Integer id;

    /**
     * ip 地址
     */
    @NotEmpty(message = "ip 地址不能为空", groups = {BaseRequest.Save.class, BaseRequest.Update.class})
    private String ipAddress;


    /**
     * 备注
     */
    private String remark;

    /**
     * 状态 1:永久 2：临时
     */
    private Integer state;

    /**
     * 解除时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime expireTime;
}
