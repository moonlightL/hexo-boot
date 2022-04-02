package com.light.hexo.business.admin.web.controller;

import com.github.pagehelper.PageInfo;
import com.light.hexo.business.admin.constant.HexoExceptionEnum;
import com.light.hexo.business.admin.model.Attachment;
import com.light.hexo.business.admin.service.AttachmentService;
import com.light.hexo.common.base.BaseController;
import com.light.hexo.common.component.file.DefaultFileService;
import com.light.hexo.common.component.file.FileRequest;
import com.light.hexo.common.component.file.FileResponse;
import com.light.hexo.common.component.log.ActionEnum;
import com.light.hexo.common.component.log.OperateLog;
import com.light.hexo.common.exception.GlobalException;
import com.light.hexo.common.exception.GlobalExceptionEnum;
import com.light.hexo.common.model.AttachmentRequest;
import com.light.hexo.common.model.Result;
import com.light.hexo.common.util.ExceptionUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * @Author MoonlightL
 * @ClassName: AttachmentController
 * @ProjectName hexo-boot
 * @Description: 附件控制器
 * @DateTime 2020/8/21 10:33
 */
@RequestMapping("/admin/attachment")
@Controller
public class AttachmentController extends BaseController {

    private static final String[] VALID_SUFFIX = {".jpg", ".jpeg", ".png", ".gif", ".webp",
            ".sql", ".xls", "xlsx", ".doc", "docx", ".txt", ".md", ".pdf"};

    @Autowired
    private AttachmentService attachmentService;

    @Autowired
    private DefaultFileService defaultFileService;

    /**
     * 新增页面
     * @param resultMap
     * @return
     * @throws GlobalException
     */
    @GetMapping("/addUI.html")
    public String addUI(Map<String,Object> resultMap) throws GlobalException {
        return render("addUI", resultMap);
    }

    @Deprecated
    @GetMapping("/detailUI.html")
    public String detailUI(Integer id, Map<String,Object> resultMap) throws GlobalException {
        Attachment attachment = this.attachmentService.findById(id);
        if (attachment == null) {
            ExceptionUtil.throwEx(HexoExceptionEnum.ERROR_ATTACHMENT_NOT_EXIST);
        }

        resultMap.put("vo", attachment);
        return render("detailUI", resultMap);
    }

    /**
     * 下载文件
     * @param id
     * @param request
     * @param response
     * @throws GlobalException
     */
    @RequestMapping("/download.html")
    @OperateLog(value = "下载文件", actionType = ActionEnum.ADMIN_DOWNLOAD)
    public void downloadFile(Long id, HttpServletRequest request, HttpServletResponse response) throws GlobalException {

        Attachment attachment = this.attachmentService.findById(id);
        if (attachment == null) {
            ExceptionUtil.throwEx(HexoExceptionEnum.ERROR_ATTACHMENT_NOT_EXIST);
        }

        FileRequest fileRequest = new FileRequest();
        fileRequest.setFilename(attachment.getFilename());
        fileRequest.setFileKey(attachment.getFileKey());
        fileRequest.setFileUrl(attachment.getFileUrl());
        FileResponse fileResponse = this.defaultFileService.download(fileRequest, attachment.getPosition());
        if (fileResponse.isSuccess()) {
            byte[] data = fileResponse.getData();
            InputStream inputStream = new ByteArrayInputStream(data);
            response.setHeader("Set-Cookie", "fileDownload=true; path=/");
            super.download(inputStream, attachment.getFilename(), request, response);
        }
    }

    /**
     * 上传附件
     * @param files
     * @return
     * @throws GlobalException
     */
    @PostMapping("/uploadBatch.json")
    @ResponseBody
    @OperateLog(value = "上传附件", actionType = ActionEnum.ADMIN_ADD)
    public Result uploadBatch(@RequestParam(value = "file", required = false) MultipartFile[] files) throws GlobalException {

        if (files == null || files.length == 0) {
            ExceptionUtil.throwEx(GlobalExceptionEnum.ERROR_PARAM);
        }

        Map<String, List<String>> result = new HashMap<>(2);
        List<String> urlList = new ArrayList<>(files.length);
        List<String> errorList = new ArrayList<>();
        for (MultipartFile file : files) {
            String originalName = file.getOriginalFilename();
            try {
                if (!StringUtils.endsWithAny(originalName, VALID_SUFFIX)) {
                    errorList.add(originalName);
                    continue;
                }

                FileResponse fileResponse = this.defaultFileService.upload(file);
                if (fileResponse.isSuccess()) {
                    urlList.add(fileResponse.getUrl());
                }

            } catch (IOException e) {
                errorList.add(originalName);
                e.printStackTrace();
            }
        }

        result.put("right", urlList);
        result.put("error", errorList);

        return Result.success(result);
    }

    /**
     * 删除附件
     * @param idStr
     * @return
     */
    @RequestMapping("remove.json")
    @ResponseBody
    @OperateLog(value = "删除附件", actionType = ActionEnum.ADMIN_REMOVE)
    public Result remove(@RequestParam String idStr) {
        if (StringUtils.isBlank(idStr)) {
            ExceptionUtil.throwEx(GlobalExceptionEnum.ERROR_PARAM);
        }

        this.attachmentService.removeAttachmentBatch(Arrays.asList(idStr.split(",")));
        return Result.success();
    }

    /**
     * 分页查询
     * @param request
     * @return
     */
    @RequestMapping("list.json")
    @ResponseBody
    public Result list(AttachmentRequest request) {
        request.setPageSize(8);
        PageInfo<Attachment> pageInfo = this.attachmentService.findPage(request);
        return Result.success(pageInfo);
    }

    /**
     * 获取附件详情
     * @param id
     * @return
     * @throws GlobalException
     */
    @RequestMapping("detail.json")
    @ResponseBody
    public Result detail(Integer id) throws GlobalException {
        Attachment attachment = this.attachmentService.findById(id);
        if (attachment == null) {
            ExceptionUtil.throwEx(HexoExceptionEnum.ERROR_ATTACHMENT_NOT_EXIST);
        }

        return Result.success(attachment);
    }
}
