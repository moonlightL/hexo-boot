package com.light.hexo.business.admin.service.impl;

import com.light.hexo.business.admin.constant.HexoExceptionEnum;
import com.light.hexo.business.admin.mapper.CategoryMapper;
import com.light.hexo.business.admin.model.Category;
import com.light.hexo.business.admin.service.CategoryService;
import com.light.hexo.business.admin.service.PostService;
import com.light.hexo.business.portal.constant.PageConstant;
import com.light.hexo.common.base.BaseMapper;
import com.light.hexo.common.base.BaseRequest;
import com.light.hexo.common.base.BaseServiceImpl;
import com.light.hexo.common.constant.CacheKey;
import com.light.hexo.common.constant.HexoConstant;
import com.light.hexo.common.exception.GlobalException;
import com.light.hexo.common.exception.GlobalExceptionEnum;
import com.light.hexo.common.model.CategoryRequest;
import com.light.hexo.common.util.CacheUtil;
import com.light.hexo.common.util.EhcacheUtil;
import com.light.hexo.common.util.ExceptionUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author MoonlightL
 * @ClassName: CategoryServiceImpl
 * @ProjectName hexo-boot
 * @Description: 分类 Service 实现
 * @DateTime 2020/8/3 11:56
 */
@CacheConfig(cacheNames = "categoryCache")
@Service
public class CategoryServiceImpl extends BaseServiceImpl<Category> implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private PostService postService;

    @Override
    public BaseMapper<Category> getBaseMapper() {
        return this.categoryMapper;
    }

    @Override
    protected Example getExample(BaseRequest request) {
        // 获取查询参数
        CategoryRequest categoryRequest = (CategoryRequest) request;
        Example example = new Example(Category.class);
        Example.Criteria criteria = example.createCriteria();

        String name = categoryRequest.getName();
        if (StringUtils.isNotBlank(name)) {
            criteria.andLike("name", name.trim() + "%");
        }

        Boolean state = categoryRequest.getState();
        if (state != null) {
            criteria.andEqualTo("state", state);
        }

        example.orderBy("sort").asc();
        return example;
    }

    @Override
    public int saveModel(Category model) throws GlobalException {
        int num = super.saveModel(model);
        if (num > 0) {
            EhcacheUtil.clearByCacheName("categoryCache");
            CacheUtil.remove(CacheKey.INDEX_COUNT_INFO);
        }
        return num;
    }

    @Override
    public int updateModel(Category model) throws GlobalException {
        int num = super.updateModel(model);
        if (num > 0) {
            EhcacheUtil.clearByCacheName("categoryCache");
        }
        return num;
    }

    @Override
    public void saveCategory(Category category) throws GlobalException {

        Category db = this.getCategoryByName(category.getName().trim());
        if (db != null) {
            ExceptionUtil.throwEx(HexoExceptionEnum.ERROR_CATEGORY_NAME_REPEAT);
        }

        if (StringUtils.isBlank(category.getCoverUrl())) {
            category.setCoverUrl(HexoConstant.DEFAULT_CATEGORY_COVER);
        }

        this.saveModel(category);
    }

    @Override
    public void updateCategory(Category category) throws GlobalException {

        if (StringUtils.isBlank(category.getCoverUrl())) {
            category.setCoverUrl(HexoConstant.DEFAULT_CATEGORY_COVER);
        }

        this.updateModel(category);
    }

    @Override
    public void removeCategoryBatch(List<String> idStrList) throws GlobalException {
        List<Integer> idList = idStrList.stream().map(Integer::valueOf).collect(Collectors.toList());

        if (idList.contains(1)) {
            ExceptionUtil.throwEx(GlobalExceptionEnum.ERROR_CAN_NOT_DELETE_RESOURCE);
        }

        Example example = new Example(Category.class);
        example.createCriteria().andIn("id", idList);

        List<Category> categoryList = this.getBaseMapper().selectByExample(example);
        if (CollectionUtils.isEmpty(categoryList)) {
            return;
        }

        for (Category category : categoryList) {
            // 分类下没有文章才可删除
            if (!this.postService.isExistByCategoryId(category.getId())) {
                this.getBaseMapper().deleteByPrimaryKey(category.getId());
            }
        }

        EhcacheUtil.clearByCacheName("categoryCache");
        CacheUtil.remove(CacheKey.INDEX_COUNT_INFO);
    }

    @Override
    public int getCategoryNum() throws GlobalException {
        Example example = new Example(Category.class);
        example.createCriteria().andEqualTo("state", true);
        return this.getBaseMapper().selectCountByExample(example);
    }

    @Override
    public List<Category> listCategory(List<Integer> idList) throws GlobalException {
        Example example = new Example(Category.class);
        example.createCriteria().andIn("id", idList);
        return this.getBaseMapper().selectByExample(example);
    }

    @Override
    public Category getCategoryByName(String name) throws GlobalException {
        Example example = new Example(Category.class);
        example.createCriteria().andEqualTo("name", name);
        return this.getBaseMapper().selectOneByExample(example);
    }

    @Override
    public List<Category> findAll(Boolean state) throws GlobalException {
        if (state == null) {
            return super.findAll();
        }

        Example example = new Example(Category.class);
        example.createCriteria().andEqualTo("state", state);
        return this.getBaseMapper().selectByExample(example);
    }

    @Cacheable(key = "'" + PageConstant.CATEGORY_LIST + "'")
    @Override
    public List<Category> listCategoriesByIndex() throws GlobalException {
        Example example = new Example(Category.class);
        example.orderBy("sort").asc();
        List<Category> list = this.getBaseMapper().selectByExample(example);
        for (Category category : list) {
            Integer num = this.postService.getPostNumByCategoryId(category.getId());
            category.setPostNum(num);
        }

        return list;
    }

    @Override
    public Category findByCategoryName(String categoryName) throws GlobalException {

        Example example = new Example(Category.class);
        example.createCriteria().andEqualTo("name", categoryName);

        return this.getBaseMapper().selectOneByExample(example);
    }
}
