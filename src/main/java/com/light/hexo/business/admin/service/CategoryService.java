package com.light.hexo.business.admin.service;
import com.light.hexo.business.admin.model.Category;
import com.light.hexo.common.base.BaseService;
import com.light.hexo.common.component.event.EventService;
import com.light.hexo.common.exception.GlobalException;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @Author MoonlightL
 * @ClassName: CategoryService
 * @ProjectName hexo-boot
 * @Description: 分类 Service
 * @DateTime 2020/8/3 11:55
 */
public interface CategoryService extends BaseService<Category>, EventService {

    /**
     * 保存分类
     * @param category
     * @throws GlobalException
     */
    void saveCategory(Category category) throws GlobalException;

    /**
     * 修改分类
     * @param category
     * @throws GlobalException
     */
    void updateCategory(Category category) throws GlobalException;

    /**
     * 批量删除
     * @param idStrList
     * @throws GlobalException
     */
    void removeCategoryBatch(List<String> idStrList) throws GlobalException;

    /**
     * 查询分类数
     * @return
     * @throws GlobalException
     */
    int getCategoryNum() throws GlobalException;

    /**
     * 批量查询分类
     * @param idList
     * @return
     * @throws GlobalException
     */
    List<Category> listCategory(List<Integer> idList) throws GlobalException;

    /**
     * 通过名称获取分类
     * @param name
     * @return
     * @throws GlobalException
     */
    Category getCategoryByName(String name) throws GlobalException;

    /**
     * 获取分类列表
     * @param state
     * @return
     * @throws GlobalException
     */
    List<Category> findAll(Boolean state) throws GlobalException;

    // =========================== 以下为前端页面请求 ============================

    /**
     * 分类列表 分类列表（首页）
     * @return
     * @throws GlobalException
     */
    List<Category> listCategoriesByIndex() throws GlobalException;

    /**
     * 通过名称获取分类信息
     * @param categoryName
     * @return
     * @throws GlobalException
     */
    Category findByCategoryName(String categoryName) throws GlobalException;

}
