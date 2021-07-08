package com.light.hexo.business.admin.model;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * @Author MoonlightL
 * @ClassName: ActionLogDetail
 * @ProjectName hexo-boot
 * @Description: 操作日志详情
 * @DateTime 2021/7/8 10:22
 */
@Data
@Accessors(chain = true)
@ToString
@Table(name = "t_action_log_detail")
public class ActionLogDetail implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @Id
    @GeneratedValue(generator = "JDBC")
    private Integer id;

    /**
     * 日志 id
     */
    private Integer logId;

    /**
     * 方法名
     */
    private String methodName;

    /**
     * 方法参数
     */
    private String methodParam;
}
