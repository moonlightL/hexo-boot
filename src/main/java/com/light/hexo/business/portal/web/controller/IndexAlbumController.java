package com.light.hexo.business.portal.web.controller;

import com.light.hexo.business.admin.constant.HexoExceptionEnum;
import com.light.hexo.business.admin.model.Album;
import com.light.hexo.business.portal.common.CommonController;
import com.light.hexo.business.portal.model.HexoPageInfo;
import com.light.hexo.common.util.ExceptionUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;

/**
 * @Author MoonlightL
 * @ClassName: IndexAlbumController
 * @ProjectName hexo-boot
 * @Description: 专辑控制器（首页）
 * @DateTime 2021/12/23, 0023 16:29
 */
@Controller
public class IndexAlbumController extends CommonController {

    private static final Integer PAGE_SIZE = 12;

    private static final String ALBUM_AUTH_PAGE = "portal/album/auth.html";

    /**
     * 专辑列表
     * @param resultMap
     * @return
     */
    @GetMapping(value = {"albums", "/albums/", "/albums/index.html"})
    public String albums(Map<String, Object> resultMap) {
        List<Album> albumList = this.albumService.listAlbumsByIndex();
        resultMap.put("albumList", albumList);
        resultMap.put("albumNum", albumList.size());
        resultMap.put("currentNav", this.navService.findByLink("/albums/"));
        return render("albums", false, resultMap);
    }

    /**
     * 专辑详情
     * @param albumId
     * @param resultMap
     * @return
     */
    @RequestMapping(value = {"albumDetail/{albumId}/", "albumDetail/{albumId}/{pageNum}"})
    public String albumsDetail(
            @PathVariable Integer albumId,
            @PathVariable(value="pageNum", required = false) Integer pageNum,
            String visitCode,
            Map<String, Object> resultMap) {
        Album album = this.albumService.findById(albumId);
        if (album == null) {
            ExceptionUtil.throwExToPage(HexoExceptionEnum.ERROR_ALBUM_NOT_EXIST);
        }

        pageNum = pageNum == null ? 1 : pageNum;

        if (StringUtils.isNotBlank(album.getVisitCode())) {
            if (StringUtils.isBlank(visitCode)) {
                resultMap.put("name", album.getName());
                resultMap.put("link", "/albumDetail/" + albumId + "/" + pageNum);
                return ALBUM_AUTH_PAGE;
            }

            if (!album.getVisitCode().equals(visitCode)) {
                resultMap.put("name", album.getName());
                resultMap.put("link", "/albumDetail/" + albumId + "/" + pageNum);
                resultMap.put("errorMsg", "访问密码不正确");
                return ALBUM_AUTH_PAGE;
            }

        }

        resultMap.put("album", album);
        HexoPageInfo pageInfo = this.albumDetailService.pageAlbumDetailByIndex(albumId, pageNum, PAGE_SIZE);
        resultMap.put("pageInfo", pageInfo);
        resultMap.put("currentNav", this.navService.findByLink("/albums/"));
        return render("albumDetail", false, resultMap);
    }
}
