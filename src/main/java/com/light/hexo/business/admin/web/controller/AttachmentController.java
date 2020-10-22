package com.light.hexo.business.admin.web.controller;

import com.github.pagehelper.PageInfo;
import com.light.hexo.business.admin.constant.HexoExceptionEnum;
import com.light.hexo.business.admin.model.Attachment;
import com.light.hexo.business.admin.service.AttachmentService;
import com.light.hexo.common.base.BaseController;
import com.light.hexo.common.component.file.*;
import com.light.hexo.common.exception.GlobalException;
import com.light.hexo.common.exception.GlobalExceptionEnum;
import com.light.hexo.common.model.AttachmentRequest;
import com.light.hexo.common.model.Result;
import com.light.hexo.common.util.ExceptionUtil;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

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
            ".sql", ".xls", ".doc", ".txt", ".md"};

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

    @GetMapping("/detailUI.html")
    public String addUI(Integer id, Map<String,Object> resultMap) throws GlobalException {
        Attachment attachment = this.attachmentService.findById(id);
        if (attachment == null) {
            ExceptionUtil.throwEx(HexoExceptionEnum.ERROR_ATTACHMENT_NOT_EXIST);
        }

        resultMap.put("vo", attachment);
        return render("detailUI", resultMap);
    }

    @PostMapping("/uploadBatch.json")
    @ResponseBody
    public Result uploadBatch(MultipartFile[] files) throws GlobalException {

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
                if (fileResponse.getSuccess()) {
                    urlList.add(fileResponse.getUrl());
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return Result.success(urlList);
    }

    /**
     * 删除附件
     * @param idStr
     * @return
     */
    @RequestMapping("remove.json")
    @ResponseBody
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


}
