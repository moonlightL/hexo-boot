package com.light.hexo.business.admin.web.controller;

import com.github.pagehelper.PageInfo;
import com.light.hexo.business.admin.model.Music;
import com.light.hexo.business.admin.service.MusicService;
import com.light.hexo.common.base.BaseController;
import com.light.hexo.common.base.BaseRequest;
import com.light.hexo.common.exception.GlobalExceptionEnum;
import com.light.hexo.common.model.MusicRequest;
import com.light.hexo.common.model.Result;
import com.light.hexo.common.util.ExceptionUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Arrays;
import java.util.Map;

/**
 * @Author MoonlightL
 * @ClassName: MusicController
 * @ProjectName hexo-boot
 * @Description: 音乐控制器
 * @DateTime 2021/2/3 15:51
 */
@RequestMapping("/admin/music")
@Controller
public class MusicController extends BaseController {

    @Autowired
    private MusicService musicService;

    /**
     * 新增页
     * @param resultMap
     * @return
     */
    @RequestMapping("addUI.html")
    public String addUI(Map<String, Object> resultMap) {
        return this.render("addUI", resultMap);
    }

    /**
     * 编辑页
     * @param resultMap
     * @return
     */
    @RequestMapping("editUI/{id}.html")
    public String editUI(@PathVariable Integer id, Map<String, Object> resultMap) {
        Music music = this.musicService.findById(id);
        resultMap.put("vo", music);
        return this.render("editUI", resultMap);
    }

    /**
     * 新增音乐
     * @param request
     * @return
     */
    @RequestMapping("add.json")
    @ResponseBody
    public Result add(@Validated(BaseRequest.Save.class) MusicRequest request) {
        Music music = request.toDoModel();
        music.setName(music.getName().trim());
        music.setArtist(music.getArtist().trim());
        music.setSort(Integer.valueOf(request.getSort()));
        this.musicService.saveModel(music);
        return Result.success();
    }

    /**
     * 编辑音乐
     * @param request
     * @return
     */
    @RequestMapping("edit.json")
    @ResponseBody
    public Result edit(@Validated(BaseRequest.Update.class) MusicRequest request) {
        Music music = request.toDoModel();
        music.setName(music.getName().trim());
        music.setArtist(music.getArtist().trim());
        music.setSort(Integer.valueOf(request.getSort()));
        this.musicService.updateModel(music);
        return Result.success();
    }

    /**
     * 修改状态
     * @param request
     * @return
     */
    @RequestMapping("updateState.json")
    @ResponseBody
    public Result updateState(MusicRequest request) {
        this.musicService.updateModel(request.toDoModel());
        return Result.success();
    }

    /**
     * 删除音乐
     * @param idStr
     * @return
     */
    @RequestMapping("remove.json")
    @ResponseBody
    public Result remove(@RequestParam String idStr) {
        if (StringUtils.isBlank(idStr)) {
            ExceptionUtil.throwEx(GlobalExceptionEnum.ERROR_PARAM);
        }

        this.musicService.removeMusicBatch(Arrays.asList(idStr.split(",")));
        return Result.success();
    }

    /**
     * 分页查询
     * @param request
     * @return
     */
    @RequestMapping("list.json")
    @ResponseBody
    public Result list(MusicRequest request) {
        PageInfo<Music> pageInfo = this.musicService.findPage(request);
        return Result.success(pageInfo);
    }
}
