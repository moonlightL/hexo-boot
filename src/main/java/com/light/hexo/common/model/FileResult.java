package com.light.hexo.common.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * @Author MoonlightL
 * @ClassName: FileResult
 * @ProjectName hexo-boot
 * @Description: MD 上传文件返回对象
 * @DateTime 2020/9/10 17:17
 */
@Getter
@Setter
public class FileResult implements Serializable {

    public int success = 1;

    private String message;

    private Object url;

    public FileResult(int success, String message) {
        this.success = success;
        this.message = message;
    }

    private FileResult(Object url) {
        this.message = "success";
        this.url = url;
    }

    public static FileResult success(String url) {
        return new FileResult(url);
    }

    public static FileResult success(List<String> urlList) {
        return new FileResult(urlList);
    }

    public static FileResult fail(String message) {
        return new FileResult(0,message);
    }
}