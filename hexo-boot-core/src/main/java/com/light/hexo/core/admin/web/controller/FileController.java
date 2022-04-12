package com.light.hexo.core.admin.web.controller;

import com.google.gson.Gson;
import com.light.hexo.common.component.file.FileRequest;
import com.light.hexo.common.component.file.FileResponse;
import com.light.hexo.common.component.log.ActionEnum;
import com.light.hexo.common.component.log.OperateLog;
import com.light.hexo.common.exception.GlobalException;
import com.light.hexo.common.exception.GlobalExceptionEnum;
import com.light.hexo.common.model.FileResult;
import com.light.hexo.common.model.bing.WebPic;
import com.light.hexo.common.util.ExceptionUtil;
import com.light.hexo.common.util.HttpClientUtil;
import com.light.hexo.common.vo.Result;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

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
            ".sql", ".xls", "xlsx", ".doc", "docx", ".txt", ".md", ".pdf"};

    @Autowired
    private com.light.hexo.component.DefaultFileService defaultFileService;

    /**
     * 文件上传（分类、文章页）
     * @param file
     * @return
     */
    @RequestMapping(value = "upload.json", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    @OperateLog(value = "文件（分类、文章页）上传", actionType = ActionEnum.ADMIN_ADD)
    public Result upload(@RequestParam(value = "file") MultipartFile file) throws TimeoutException {

        if (file == null) {
            ExceptionUtil.throwEx(GlobalExceptionEnum.ERROR_PARAM);
        }

        String originalName = file.getOriginalFilename();
        if (!StringUtils.endsWithAny(originalName, VALID_SUFFIX)) {
            ExceptionUtil.throwEx(GlobalExceptionEnum.ERROR_PARAM);
        }

        FileRequest fileRequest = FileRequest.createRequest(file);
        FileResponse fileResponse = this.defaultFileService.upload(fileRequest);
        return Result.success(fileResponse.getUrl());
    }

    /**
     * 附件文件批量上传
     * @param files
     * @return
     */
    @RequestMapping(value = "uploadBatch.json", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    @OperateLog(value = "附件批量上传", actionType = ActionEnum.ADMIN_ADD)
    public Result uploadBatch(@RequestParam(value = "file", required = false) MultipartFile[] files) {

        if (files == null || files.length == 0) {
            ExceptionUtil.throwEx(GlobalExceptionEnum.ERROR_PARAM);
        }

        Map<String, List<String>> result = new HashMap<>(2);
        List<String> urlList = new ArrayList<>(files.length);
        List<String> errorList = new ArrayList<>();
        for (MultipartFile file : files) {
            String originalName = file.getOriginalFilename();
            if (!StringUtils.endsWithAny(originalName, VALID_SUFFIX)) {
                errorList.add(originalName);
                continue;
            }

            FileRequest fileRequest = FileRequest.createRequest(file);
            FileResponse fileResponse = this.defaultFileService.upload(fileRequest);
            if (fileResponse.isSuccess()) {
                urlList.add(fileResponse.getUrl());
            } else {
                errorList.add(originalName);
            }
        }

        result.put("right", urlList);
        result.put("error", errorList);

        return Result.success(result);
    }

    /**
     * markdown 编辑器文件上传
     * @param file
     * @return
     * @throws GlobalException
     */
    @RequestMapping(value = "/mdUploadFile.json", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    @OperateLog(value = "编辑器文件上传", actionType = ActionEnum.ADMIN_ADD)
    public FileResult mdUploadFile(@RequestParam(value = "editormd-image-file", required = false) MultipartFile file) throws TimeoutException {
        if (file == null) {
            ExceptionUtil.throwEx(GlobalExceptionEnum.ERROR_PARAM);
        }

        String originalName = file.getOriginalFilename();
        if (!StringUtils.endsWithAny(originalName, VALID_SUFFIX)) {
            ExceptionUtil.throwEx(GlobalExceptionEnum.ERROR_PARAM);
        }

        FileRequest fileRequest = FileRequest.createRequest(file);
        FileResponse fileResponse = this.defaultFileService.upload(fileRequest);
        return FileResult.success(fileResponse.getUrl());
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
