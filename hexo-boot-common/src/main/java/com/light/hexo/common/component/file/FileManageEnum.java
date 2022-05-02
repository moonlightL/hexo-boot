package com.light.hexo.common.component.file;

import lombok.Getter;

/**
 * @Author MoonlightL
 * @ClassName: FileManageEnum
 * @ProjectName hexo-boot
 * @Description: 文件管理模式枚举
 * @DateTime 2020/9/10 16:52
 */
@Getter
public enum FileManageEnum {

    LOCAL("1", "本地"),
    QI_NIU("2", "七牛云"),
    OSS("3", "OSS"),
    COS("4", "腾讯云")
    ;

    private String code;

    private String message;

    FileManageEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public static String getMessageByCode(String code) {
        for (FileManageEnum modeEnum : FileManageEnum.values()) {
            if (modeEnum.getCode() == code) {
                return modeEnum.getMessage();
            }
        }
        return "";
    }
}
