package com.light.hexo.core.admin.web.controller;

import com.github.pagehelper.PageInfo;
import com.light.hexo.common.vo.Result;
import com.light.hexo.core.admin.component.DumpService;
import com.light.hexo.common.constant.HexoExceptionEnum;
import com.light.hexo.mapper.model.Backup;
import com.light.hexo.core.admin.service.BackupService;
import com.light.hexo.core.admin.service.ConfigService;
import com.light.hexo.common.base.BaseController;
import com.light.hexo.common.component.log.ActionEnum;
import com.light.hexo.common.component.log.OperateLog;
import com.light.hexo.common.exception.GlobalException;
import com.light.hexo.common.exception.GlobalExceptionEnum;
import com.light.hexo.common.request.BackupRequest;
import com.light.hexo.common.util.ExceptionUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;
import java.util.Map;

/**
 * @Author MoonlightL
 * @ClassName: BackupController
 * @ProjectName hexo-boot
 * @Description: 备份控制器
 * @DateTime 2020/9/8 18:35
 */
@Controller
@RequestMapping("/admin/backup")
public class BackupController extends BaseController {

    @Autowired
    private ConfigService configService;

    @Autowired
    private DumpService dumpService;

    @Autowired
    private BackupService backupService;

    @RequestMapping("/configUI.html")
    public String configUI(Map<String, Object> resultMap) {
        Map<String, String> configMap = this.configService.getConfigMap();
        resultMap.put("configMap", configMap);
        return this.render("configUI", resultMap);
    }

    /**
     * 立即备份
     */
    @RequestMapping("/backupData.json")
    @ResponseBody
    @OperateLog(value = "立即备份", actionType = ActionEnum.ADMIN_ADD)
    public Result backupData() {
        String backupName = this.dumpService.dumpData();
        if (StringUtils.isBlank(backupName)) {
            return Result.fail();
        }

        this.backupService.saveBackup(backupName);
        return Result.success();
    }

    /**
     * 下载备份文件
     * @param id
     * @param request
     * @param response
     * @throws GlobalException
     */
    @RequestMapping("/download.html")
    @OperateLog(value = "下载备份文件", actionType = ActionEnum.ADMIN_DOWNLOAD)
    public void downloadFile(Long id, HttpServletRequest request, HttpServletResponse response) {
        Backup backup = this.backupService.findById(id);
        if (backup == null) {
            ExceptionUtil.throwExToPage(HexoExceptionEnum.ERROR_BACKUP_NOT_EXIST);
        }

        try {
            response.setHeader("Set-Cookie", "fileDownload=true; path=/");
            FileInputStream inputStream = new FileInputStream(new File(backup.getFilePath()));
            super.download(inputStream, backup.getName(), request, response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 保存备份配置
     * @param configMap
     * @return
     */
    @PostMapping("/saveConfig.json")
    @ResponseBody
    @OperateLog(value = "保存备份配置", actionType = ActionEnum.ADMIN_ADD)
    public Result saveConfig(@RequestParam Map<String, String> configMap) {
        this.backupService.saveConfig(configMap);
        return Result.success();
    }

    /**
     * 删除备份
     * @param idStr
     * @return
     * @throws GlobalException
     */
    @PostMapping("/remove.json")
    @ResponseBody
    @OperateLog(value = "删除备份", actionType = ActionEnum.ADMIN_REMOVE)
    public Result remove(@RequestParam String idStr) {
        if (StringUtils.isBlank(idStr)) {
            ExceptionUtil.throwEx(GlobalExceptionEnum.ERROR_PARAM);
        }

        this.backupService.removeBackupBatch(Arrays.asList(idStr.split(",")));
        return Result.success();
    }

    /**
     * 分页查询
     * @param request
     * @return
     */
    @RequestMapping("list.json")
    @ResponseBody
    public Result list(BackupRequest request) {
        PageInfo<Backup> pageInfo = this.backupService.findPage(request);
        return Result.success(pageInfo);
    }
}
