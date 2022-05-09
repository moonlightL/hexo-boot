package com.light.hexo.core.admin.service.impl;

import com.light.hexo.common.base.BaseServiceImpl;
import com.light.hexo.common.constant.HexoExceptionEnum;
import com.light.hexo.mapper.mapper.CategoryMapper;
import com.light.hexo.mapper.base.BaseMapper;
import com.light.hexo.mapper.model.Category;
import com.light.hexo.common.event.CategoryEvent;
import com.light.hexo.core.admin.service.CategoryService;
import com.light.hexo.core.admin.service.PostService;
import com.light.hexo.core.portal.constant.PageConstant;
import com.light.hexo.common.base.BaseRequest;
import com.light.hexo.common.component.event.BaseEvent;
import com.light.hexo.common.component.event.EventEnum;
import com.light.hexo.common.component.event.EventPublisher;
import com.light.hexo.common.constant.HexoConstant;
import com.light.hexo.common.exception.GlobalException;
import com.light.hexo.common.exception.GlobalExceptionEnum;
import com.light.hexo.common.request.CategoryRequest;
import com.light.hexo.common.util.EhcacheUtil;
import com.light.hexo.common.util.ExceptionUtil;
import com.light.hexo.common.util.SpringContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.context.WebApplicationContext;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.ServletContext;
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
@Slf4j
public class CategoryServiceImpl extends BaseServiceImpl<Category> implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private PostService postService;

    @Autowired
    @Lazy
    private EventPublisher eventPublisher;

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
            criteria.andLike("name", "%" + name.trim() + "%");
        }

        Boolean state = categoryRequest.getState();
        if (state != null) {
            criteria.andEqualTo("state", state);
        }

        example.orderBy("sort").asc();
        return example;
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
        // 清除缓存
        this.eventPublisher.emit(new CategoryEvent(this));
    }

    @Override
    public void updateCategory(Category category) throws GlobalException {

        if (StringUtils.isBlank(category.getCoverUrl())) {
            category.setCoverUrl(HexoConstant.DEFAULT_CATEGORY_COVER);
        }

        this.updateModel(category);
        // 清除缓存
        this.eventPublisher.emit(new CategoryEvent(this));
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

        // 清除缓存
        this.eventPublisher.emit(new CategoryEvent(this));
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
        example.createCriteria().andEqualTo("state", true);
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
        example.createCriteria().andEqualTo("name", categoryName)
                                .andEqualTo("state", true);

        return this.getBaseMapper().selectOneByExample(example);
    }

    @Override
    public String getCode() {
        return EventEnum.CATEGORY.getType();
    }

    @Override
    public void dealWithEvent(BaseEvent event) {

        EhcacheUtil.clearByCacheName("categoryCache");

        WebApplicationContext webApplicationContext = (WebApplicationContext) SpringContextUtil.applicationContext;
        ServletContext servletContext = webApplicationContext.getServletContext();
        if (servletContext == null) {
            log.info("===========CategoryService dealWithEvent 获取 servletContext 为空============");
            return;
        }

        List<Category> categoryList = this.listCategoriesByIndex();
        servletContext.setAttribute("categoryList", categoryList);
        servletContext.setAttribute("categoryNum", categoryList.size());
    }
}
