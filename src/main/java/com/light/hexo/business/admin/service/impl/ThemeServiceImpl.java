package com.light.hexo.business.admin.service.impl;

import com.light.hexo.business.admin.constant.ConfigEnum;
import com.light.hexo.business.admin.constant.HexoExceptionEnum;
import com.light.hexo.business.admin.mapper.ThemeMapper;
import com.light.hexo.business.admin.model.Theme;
import com.light.hexo.business.admin.service.ConfigService;
import com.light.hexo.business.admin.service.ThemeService;
import com.light.hexo.common.base.BaseMapper;
import com.light.hexo.common.base.BaseRequest;
import com.light.hexo.common.base.BaseServiceImpl;
import com.light.hexo.common.constant.CacheKey;
import com.light.hexo.common.exception.GlobalException;
import com.light.hexo.common.model.ThemeRequest;
import com.light.hexo.common.util.CacheUtil;
import com.light.hexo.common.util.ExceptionUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @Author MoonlightL
 * @ClassName: ThemeServiceImpl
 * @ProjectName hexo-boot
 * @Description: 主题 Service 实现
 * @DateTime 2020/9/24 14:38
 */
@Service
public class ThemeServiceImpl extends BaseServiceImpl<Theme> implements ThemeService {

    @Autowired
    private ThemeMapper themeMapper;

    @Autowired
    private ConfigService configService;

    @Override
    public BaseMapper<Theme> getBaseMapper() {
        return this.themeMapper;
    }

    @Override
    protected Example getExample(BaseRequest request) {
        ThemeRequest themeRequest = (ThemeRequest) request;
        Example example = new Example(Theme.class);
        Example.Criteria criteria = example.createCriteria();

        String name = themeRequest.getName();
        if (StringUtils.isNotBlank(name)) {
            criteria.andLike("name", name.trim() + "%");
        }

        example.orderBy("id").asc();

        return example;
    }


    @Override
    public Theme getCurrentTheme() throws GlobalException {

        String key = CacheKey.CURRENT_THEME;
        Theme theme = CacheUtil.get(key);
        if (theme == null) {
            Example example = new Example(Theme.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("state", 1);
            theme = this.themeMapper.selectOneByExample(example);
            CacheUtil.put(key, theme);
        }

        return theme;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void useTheme(Theme theme) throws GlobalException {
        Theme target = this.findById(theme.getId());
        if (target == null) {
            ExceptionUtil.throwEx(HexoExceptionEnum.ERROR_THEME_NOT_EXIST);
        }

        Theme currentTheme = this.getCurrentTheme();
        if (currentTheme != null) {
            currentTheme.setState(false);
            this.updateModel(currentTheme);
        }

        target.setState(true);
        this.updateModel(target);

        CacheUtil.remove(CacheKey.CURRENT_THEME);
    }

    @Override
    public Theme checkTheme(String themeName) throws GlobalException {
        Example example = new Example(Theme.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("name", themeName);
        return this.themeMapper.selectOneByExample(example);
    }

    @Override
    public void saveTheme(String name, String coverUrl, boolean state, String remark) throws GlobalException {

        Theme dbTheme = this.checkTheme(name);
        if (dbTheme != null) {
          return;
        }

        Theme theme = new Theme();
        theme.setName(name)
                .setCoverUrl(coverUrl)
                .setState(state)
                .setSort(1)
                .setRemark(remark);
        this.saveModel(theme);
    }

}
