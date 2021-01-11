package com.light.hexo.business.admin.constant;

import lombok.Getter;

/**
 * @Author MoonlightL
 * @ClassName: ConfigEnum
 * @ProjectName hexo-boot
 * @Description: 配置枚举
 * @DateTime 2020/9/9 11:18
 */
@Getter
public enum ConfigEnum {

    /* 1:已安装 0：未安装 */
    IS_INSTALLED("is_installed", "", "是否已安装"),
    INSTALL_TIME("install_time", "","安装时间"),
    BLOG_NAME("blog_name", "","博客名称"),
    HOME_PAGE("home_page", "","博客首页"),
    DESCRIPTION("description", "月光中的污点,MoonlightL","博客描述"),
    KEYWORDS("keywords", "技术博客","博客关键字"),
    LOGO_URL("logo_url", "", "Logo 地址"),
    FAVICON_URL("favicon_url", "", "Favicon 地址"),
    RECORD("record", "浙ICP备00000000号","网站备案号"),
    POWER_BY("power_by", "Copyright©2020. Design by MoonlightL","版权信息"),

    BLOG_AUTHOR("blog_author", "","博主名称"),
    EMAIL("email", "","邮箱地址"),
    GIT_HUB_ACCOUNT("git_hub_account", "","GitHub 地址"),
    WX_ACCOUNT("wx_account", "","微信号"),
    QQ_ACCOUNT("qq_account", "","QQ 号"),
    WEI_BO_ACCOUNT("wei_bo_account", "","微博号"),
    // 暂无用处
    WE_CHAT_OFFICIAL_ACCOUNT("we_chat_official_account", "", "微信公众号"),

    /* 参考 FileManageEnum */
    MANAGE_MODE("manage_mode", "1","文件管理配置"),

    LOCAL_FILE_PATH("local_file_path", "", "本地文件路径"),

    QN_DOMAIN("qn_domain", "","七牛云域名"),
    QN_ACCESS_KEY("qn_access_key", "","七牛云 KEY"),
    QN_SECRET_KEY("qn_secret_key", "","七牛云密钥"),
    QN_BUCKET("qn_bucket", "","七牛云桶"),
    QN_REGION("qn_region", "", "机房"),

    OSS_ENDPOINT("oss_endpoint", "","OSS域名"),
    OSS_ACCESS_KEY("oss_access_key", "","OSS KEY"),
    OSS_SECRET_KEY("oss_secret_key", "","OSS 密钥"),
    OSS_BUCKET("oss_bucket", "","OSS 桶"),

    /* 1:自动 0：非自动 */
    BACKUP_AUTO("backup_auto", "1","是否自动备份"),
    BACKUP_DIR("backup_dir", "","备份目录"),

    POST_PAGE_SIZE("post_page_size", "9", "文章列表页面大小"),

    REWARD_COMMENT("reward_comment", "坚持原创技术分享，您的支持将鼓励我继续创作！","打赏话语"),
    REWARD_BY_ALI_PAY("reward_by_ali_pay", "","支付宝收款码地址"),
    REWARD_BY_WE_CHAT_PAY("reward_by_we_chat_pay", "","微信收款码地址"),

    LICENSE("license", "CC BY-NC-SA 4.0","文章版权协议"),
    LICENSE_URL("license_url", "https://creativecommons.org/licenses/by-nc-sa/4.0/","文章版权说明地址"),

    WE_CHAT_GZH("we_chat_gzh", "", "微信公众号"),
    WE_CHAT_GZH_COMMENT("we_chat_gzh_comment", "", "关注微信公众号话语"),

    META_CODE("meta_code", "", "文章详情页 meta 代码"),
    SCRIPT_CODE("script_code", "", "文章详情页 script 代码"),

    BAI_DU_PUSH_TOKEN("bai_du_push_token", "", "百度推送"),

    EMAIL_HOST("email_host", "smtp.163.com", "发送邮件服务器"),
    EMAIL_USERNAME("email_username", "", "发送邮件用户名"),
    EMAIL_PASSWORD("email_password", "", "发送邮件密码"),

    TWIKOO_ENV_ID("twikoo_env_id", "", "twikoo环境 id")
    ;

    private String name;

    private String value;

    private String remark;

    ConfigEnum(String name, String value, String remark) {
        this.name = name;
        this.value = value;
        this.remark = remark;
    }

}
