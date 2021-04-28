package com.light.hexo.business.admin.web.controller;

import com.google.gson.Gson;
import com.light.hexo.common.component.file.*;
import com.light.hexo.common.exception.GlobalException;
import com.light.hexo.common.exception.GlobalExceptionEnum;
import com.light.hexo.common.model.FileResult;
import com.light.hexo.common.model.Result;
import com.light.hexo.common.model.bing.WebPic;
import com.light.hexo.common.util.ExceptionUtil;
import com.light.hexo.common.util.HttpClientUtil;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author MoonlightL
 * @ClassName: FileController
 * @ProjectName hexo-boot
 * @Description: 文件控制器
 * @DateTime 2020/8/13 20:03
 */
@RequestMapping("/admin/file")
@Controller
public class FileController {

    private static final String[] VALID_SUFFIX = {".jpg", ".jpeg", ".png", ".gif", ".webp",
            ".sql", ".xls", ".xlsx",".doc",".docx", ".txt", ".pdf"};

    @Autowired
    private DefaultFileService defaultFileService;


    @RequestMapping(value = "upload.json", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public Result upload(@RequestParam(value = "file") MultipartFile file) {

        if (file == null) {
            ExceptionUtil.throwEx(GlobalExceptionEnum.ERROR_PARAM);
        }

        String originalName = file.getOriginalFilename();
        if (!StringUtils.endsWithAny(originalName, VALID_SUFFIX)) {
            ExceptionUtil.throwEx(GlobalExceptionEnum.ERROR_PARAM);
        }

        try {
            FileRequest fileRequest = new FileRequest();
            String baseName = FilenameUtils.getBaseName(originalName);
            String extension = FilenameUtils.getExtension(originalName);
            String newFilename = baseName + "_" + System.currentTimeMillis() + "." + extension;
            fileRequest.setOriginalName(originalName)
                       .setFilename(newFilename)
                       .setData(file.getBytes())
                       .setFileSize(file.getSize())
                       .setContentType(file.getContentType())
                       .setExtension(extension);

            FileResponse fileResponse = this.defaultFileService.upload(fileRequest);
            return Result.success(fileResponse.getUrl());

        } catch (IOException e) {
            e.printStackTrace();
        }

        return Result.fail();
    }

    @RequestMapping(value = "uploadBatch.json", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public Result uploadBatch(@RequestParam(value = "file", required = false) MultipartFile[] files) {

        if (files == null || files.length == 0) {
            ExceptionUtil.throwEx(GlobalExceptionEnum.ERROR_PARAM);
        }

        List<String> urlList = new ArrayList<>(files.length);
        for (MultipartFile file : files) {
            try {
                String originalName = file.getOriginalFilename();
                if (!StringUtils.endsWithAny(originalName, VALID_SUFFIX)) {
                    continue;
                }

                FileRequest fileRequest = new FileRequest();
                String baseName = FilenameUtils.getBaseName(originalName);
                String extension = FilenameUtils.getExtension(originalName);
                String newFilename = baseName + "_" + System.currentTimeMillis() + "." + extension;
                fileRequest.setOriginalName(originalName)
                           .setFilename(newFilename)
                           .setData(file.getBytes())
                           .setFileSize(file.getSize())
                           .setContentType(file.getContentType())
                           .setExtension(extension);

                FileResponse fileResponse = this.defaultFileService.upload(fileRequest);
                urlList.add(fileResponse.getUrl());

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return Result.success(urlList);
    }

    @RequestMapping(value = "/mdUploadFile.json", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public FileResult mdUploadFile(@RequestParam(value = "editormd-image-file", required = false) MultipartFile file) throws GlobalException {
        if (file == null) {
            ExceptionUtil.throwEx(GlobalExceptionEnum.ERROR_PARAM);
        }

        String originalName = file.getOriginalFilename();
        if (!StringUtils.endsWithAny(originalName, VALID_SUFFIX)) {
            ExceptionUtil.throwEx(GlobalExceptionEnum.ERROR_PARAM);
        }

        try {
            FileRequest fileRequest = new FileRequest();
            String baseName = FilenameUtils.getBaseName(originalName);
            String extension = FilenameUtils.getExtension(originalName);
            String newFilename = baseName + "_" + System.currentTimeMillis() + "." + extension;
            fileRequest.setOriginalName(originalName)
                       .setFilename(newFilename)
                       .setData(file.getBytes())
                       .setFileSize(file.getSize())
                       .setContentType(file.getContentType())
                       .setExtension(extension);

            FileResponse fileResponse = this.defaultFileService.upload(fileRequest);
            return FileResult.success(fileResponse.getUrl());

        } catch (IOException e) {
            e.printStackTrace();
        }

        return FileResult.fail("上传失败");
    }

    @RequestMapping(value = "/randomPic.json", method = RequestMethod.POST)
    @ResponseBody
    public FileResult randomPic() throws GlobalException {
        String result = HttpClientUtil.sendPost("https://api.ixiaowai.cn/gqapi/gqapi.php?return=json", "");
        Gson gson = new Gson();
        WebPic webPic = gson.fromJson(result, WebPic.class);
        if (webPic == null || !"200".equals(webPic.getCode())) {
            return FileResult.fail("获取失败，请重新拉取");
        }

        return FileResult.success(webPic.getImgurl());
    }
}
