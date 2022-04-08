package com.light.hexo.business.admin.web.controller;

import com.github.pagehelper.PageInfo;
import com.light.hexo.business.admin.constant.HexoExceptionEnum;
import com.light.hexo.business.admin.model.Album;
import com.light.hexo.business.admin.service.AlbumDetailService;
import com.light.hexo.business.admin.service.AlbumService;
import com.light.hexo.common.base.BaseController;
import com.light.hexo.common.base.BaseRequest;
import com.light.hexo.common.component.file.DefaultFileService;
import com.light.hexo.common.component.file.FileRequest;
import com.light.hexo.common.component.file.FileResponse;
import com.light.hexo.common.component.log.ActionEnum;
import com.light.hexo.common.component.log.OperateLog;
import com.light.hexo.common.exception.GlobalException;
import com.light.hexo.common.exception.GlobalExceptionEnum;
import com.light.hexo.common.model.AlbumDetailRequest;
import com.light.hexo.common.model.AlbumRequest;
import com.light.hexo.common.model.Result;
import com.light.hexo.common.util.ExceptionUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.util.*;

/**
 * @Author MoonlightL
 * @ClassName: AlbumController
 * @ProjectName hexo-boot
 * @Description: 专辑控制器
 * @DateTime 2021/12/21, 0021 15:49
 */
@RequestMapping("/admin/album")
@Controller
public class AlbumController extends BaseController {

    private final Integer PAGE_SIZE = 8;

    private static final String[] PHOTO_VALID_SUFFIX = {".jpg", ".jpeg", ".png", ".gif", ".webp"};

    private static final String[] VIDEO_VALID_SUFFIX = {".mp4", ".avi", ".rmvb", ".rm", ".wmv", ".mov"};

    @Autowired
    private AlbumService albumService;

    @Autowired
    private AlbumDetailService albumDetailService;

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

    /**
     * 编辑页
     * @param resultMap
     * @return
     */
    @RequestMapping("editUI/{id}.html")
    public String editUI(@PathVariable Integer id, Map<String, Object> resultMap) {
        Album album = this.albumService.findById(id);
        if (album == null) {
            ExceptionUtil.throwEx(HexoExceptionEnum.ERROR_ALBUM_NOT_EXIST);
        }

        resultMap.put("vo", album);
        return this.render("editUI", resultMap);
    }

    /**
     * 新增专辑
     * @param request
     * @return
     */
    @RequestMapping("add.json")
    @ResponseBody
    @OperateLog(value = "新增专辑", actionType = ActionEnum.ADMIN_ADD)
    public Result add(@Validated(BaseRequest.Save.class) AlbumRequest request) {
        Album album = request.toDoModel();
        album.setName(album.getName().trim());
        this.albumService.saveAlbum(album);
        return Result.success();
    }

    /**
     * 编辑专辑
     * @param request
     * @return
     */
    @RequestMapping("edit.json")
    @ResponseBody
    @OperateLog(value = "编辑专辑", actionType = ActionEnum.ADMIN_EDIT)
    public Result edit(@Validated(BaseRequest.Update.class) AlbumRequest request) {
        Album album = request.toDoModel();
        album.setName(album.getName().trim());
        this.albumService.updateAlbum(album);
        return Result.success();
    }

    /**
     * 删除专辑
     * @param idStr
     * @return
     */
    @RequestMapping("remove.json")
    @ResponseBody
    @OperateLog(value = "删除专辑", actionType = ActionEnum.ADMIN_REMOVE)
    public Result remove(@RequestParam String idStr) {
        if (StringUtils.isBlank(idStr)) {
            ExceptionUtil.throwEx(GlobalExceptionEnum.ERROR_PARAM);
        }

        this.albumService.removeAlbum(Arrays.asList(idStr.split(",")));
        return Result.success();
    }

    /**
     * 上传专辑文件
     * @param albumId
     * @param files
     * @return
     * @throws GlobalException
     */
    @PostMapping("/uploadBatch.json")
    @ResponseBody
    @OperateLog(value = "上传专辑文件", actionType = ActionEnum.ADMIN_ADD)
    public Result uploadBatch(@RequestParam(value = "file", required = false) MultipartFile[] files, Integer albumId, String dataUrl) {

        if (files == null || files.length == 0) {
            ExceptionUtil.throwEx(GlobalExceptionEnum.ERROR_PARAM);
        }

        Album album = this.albumService.findById(albumId);
        if (album == null) {
            ExceptionUtil.throwEx(HexoExceptionEnum.ERROR_ALBUM_NOT_EXIST);
        }

        Map<String, Object> result = new HashMap<>(2);
        List<String> urlList = new ArrayList<>(files.length);
        List<String> errorList = new ArrayList<>();
        for (MultipartFile file : files) {
            String originalName = file.getOriginalFilename();
            if (!StringUtils.endsWithAny(originalName, album.getDetailType().equals(1) ? PHOTO_VALID_SUFFIX : VIDEO_VALID_SUFFIX)) {
                errorList.add(originalName);
                continue;
            }

            FileRequest fileRequest = FileRequest.createRequest(file);
            fileRequest.setCoverBase64(dataUrl);
            FileResponse fileResponse = this.defaultFileService.upload(fileRequest);
            if (fileResponse.isSuccess()) {
                this.albumDetailService.saveAlbumDetail(albumId, fileResponse.getOriginalName(), fileResponse.getUrl(), fileResponse.getCoverUrl());
                urlList.add(fileResponse.getUrl());
                result.put("extraMsg", fileResponse.getErrorMsg());
            } else {
                errorList.add(originalName);
            }
        }

        result.put("right", urlList);
        result.put("error", errorList);

        return Result.success(result);
    }

    /**
     * 分页查询
     * @param request
     * @return
     */
    @RequestMapping("list.json")
    @ResponseBody
    public Result list(AlbumRequest request) {
        request.setPageSize(8);
        PageInfo<Album> pageInfo = this.albumService.findPage(request);
        return Result.success(pageInfo);
    }

    // ==========================专辑详情========================

    /**
     * 详情页
     * @param id
     * @param resultMap
     * @return
     * @throws GlobalException
     */
    @GetMapping("/detailUI.html")
    public String detailUI(Integer id, Map<String,Object> resultMap) throws GlobalException {
        Album album = this.albumService.findById(id);
        if (album == null) {
            ExceptionUtil.throwEx(HexoExceptionEnum.ERROR_ALBUM_NOT_EXIST);
        }

        resultMap.put("vo", album);
        return render(album.getDetailType().equals(1) ? "photoUI" : "videoUI", resultMap);
    }

    /**
     * 详情列表
     * @param request
     * @return
     */
    @RequestMapping("detailList.json")
    @ResponseBody
    public Result detailList(AlbumRequest request) {
        Integer albumId = request.getId();
        Album album = this.albumService.findById(albumId);
        if (album == null) {
            ExceptionUtil.throwEx(HexoExceptionEnum.ERROR_ALBUM_NOT_EXIST);
        }

        return Result.success(new PageInfo<>(this.albumDetailService.findListByAlbumId(albumId, request.getPageNum(), PAGE_SIZE)));
    }

    /**
     * 获取详情信息
     * @param detailId
     * @return
     */
    @RequestMapping("getAlbumDetail.json")
    @ResponseBody
    public Result getAlbumDetail(Integer detailId) {
        return Result.success(this.albumDetailService.findById(detailId));
    }

    /**
     * 保存专辑详情信息
     * @param request
     * @return
     */
    @RequestMapping("saveAlbumDetail.json")
    @ResponseBody
    @OperateLog(value = "保存专辑详情", actionType = ActionEnum.ADMIN_ADD)
    public Result saveAlbumDetail(@Validated(BaseRequest.Save.class) AlbumDetailRequest request) {
        this.albumDetailService.saveAlbumDetail(request.toDoModel());
        return Result.success();
    }

    /**
     * 修改专辑详情信息
     * @param request
     * @return
     */
    @RequestMapping("editAlbumDetail.json")
    @ResponseBody
    @OperateLog(value = "编辑专辑详情", actionType = ActionEnum.ADMIN_EDIT)
    public Result editAlbumDetail(@Validated(BaseRequest.Update.class) AlbumDetailRequest request) {
        this.albumDetailService.updateAlbumDetail(request.toDoModel());
        return Result.success();
    }

    /**
     * 删除专辑文件
     * @param detailId
     * @return
     */
    @RequestMapping("removeDetail.json")
    @ResponseBody
    @OperateLog(value = "删除专辑文件", actionType = ActionEnum.ADMIN_REMOVE)
    public Result removeDetail(Integer detailId) {
        this.albumDetailService.removeDetail(detailId);
        return Result.success();
    }

    /**
     * 删除专辑全部文件
     * @param albumId
     * @return
     */
    @RequestMapping("removeAllDetail.json")
    @ResponseBody
    @OperateLog(value = "删除专辑全部文件", actionType = ActionEnum.ADMIN_REMOVE)
    public Result removeAllDetail(Integer albumId) {
        this.albumDetailService.removeAllDetail(albumId);
        return Result.success();
    }
}
