package com.light.hexo.business.admin.constant;

import com.light.hexo.common.exception.GlobalExceptionMap;

/**
 * @Author MoonlightL
 * @ClassName: HexoExceptionEnum
 * @ProjectName hexo-boot
 * @Description: 异常枚举
 * @DateTime 2020/9/7 18:06
 */
public enum HexoExceptionEnum implements GlobalExceptionMap {

    // 1xxx --> 用户
    ERROR_USER_NOT_EXIST(1001, "该用户不存在"),
    ERROR_USER_PASSWORD_WRONG(1002, "用户名或密码不正确"),
    ERROR_LOGIN_EXPIRE(1003, "未登录或登陆过期"),
    ERROR_STATE_WRONG(1004, "该用户不可用"),
    ERROR_WRONG_PASSWORD_UPDATE(1005, "修改密码失败，旧密码不正确"),
    ERROR_NOT_PERMISSION_OPERATE(1006, "没有权限操作"),
    ERROR_CAN_NOT_REMOVE_SELF(1007, "不能删除自己的账户"),
    ERROR_CAN_NOT_CLOSE_ADMIN(1008, "不能关闭博主账户"),
    ERROR_HAVE_INSTALLED(1009, "用户已创建"),

    // 2xxx -->  文章
    ERROR_POST_NOT_EXIST(2001, "该文章不存在"),
    ERROR_IMPORT_PATH_NOT_DIR(2002, "该路径不是文件目录"),
    ERROR_IMPORT_FILE_PATH_NOT_EXIST(2003, "导入的JSON文件不存在"),
    ERROR_IMPORT_PATH_HAVE_NO_MD(2004, "没有可导入的 Markdown 文件"),
    ERROR_NOT_SUPPORT_COMMENT(2005, "该文章已关闭评论功能"),
    ERROR_POST_TITLE_REPEAT(2006, "文章标题重复"),

    // 3xxx --> 评论
    ERROR_POST_COMMENT_NOT_EXIST(3001, "该评论不存在"),

    // 4xxx --> 分类
    ERROR_CATEGORY_NOT_EXIST(4001, "该分类不存在"),
    ERROR_CATEGORY_NAME_REPEAT(4002, "分类名称重复"),

    // 5xxx --> 标签
    ERROR_TAG_NOT_EXIST(5001, "该标签不存在"),

    // 6xxx --> 留言
    ERROR_GUEST_BOOK_NOT_EXIST(6001, "该留言不存在"),

    // 7xxx --> 备份
    ERROR_BACKUP_NOT_EXIST(7001, "该备份不存在"),

    // 8xxx --> 黑名单
    ERROR_BLACKLIST_NOT_EXIST(8001, "该黑名单不存在"),
    ERROR_IP_ADDRESS_TO_BLACK_LIST(8002, "不能将当前网段IP加入黑名单"),

    // 9xxx --> 附件
    ERROR_ATTACHMENT_NOT_EXIST(9001, "该附件不存在"),

    // 10xxx --> 配置
    ERROR_QN_CONFIG_IS_EMPTY(10001, "七牛云配置不完全，请在【个性配置】中填补再进行操作"),
    ERROR_OSS_CONFIG_IS_EMPTY(10002, "OSS 配置不完全，请在【个性配置】中填补再进行操作"),
    ERROR_FILE_UPLOAD(10003, "文件上传失败"),
    ERROR_FILE_DOWNLOAD(10004, "文件下载失败"),

    // 11xxx --> 主题
    ERROR_THEME_NOT_EXIST(11001, "该主题不存在"),
    ERROR_THEME_URL_WRONG(11002, "主题地址格式不正确"),
    ERROR_ACTIVE_THEME_CANNOT_REMOVE(11003, "使用中的主题不能被删除"),
    ERROR_THEME_UNZIP_WRONG(11004, "主题解压失败"),
    ERROR_THEME_COG_WRONG(11005, "主题目录结构不正确"),

    // 12xxx --> 导航
    ERROR_NAV_NOT_EXIST(12001, "访问路径不存在"),
    ERROR_NAV_PAGE_NOT_EXIST(12002, "访问页面不存在"),

    // 13xxx --> 动态
    ERROR_DYNAMIC_NOT_EXIST(13001, "动态不存在"),

    // 50xxx --> 前端
    ERROR_REPEAT_PRAISE_POST(50001, "已点赞，请勿重复操作")


    ;

    HexoExceptionEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

    private int code;

    private String message;

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
