package com.light.hexo.common.base;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.light.hexo.business.admin.model.Tag;
import com.light.hexo.common.exception.GlobalException;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.List;

/**
 * @Author MoonlightL
 * @ClassName: BaseServiceImpl
 * @ProjectName hexo-boot
 * @Description: Service 基类实现
 * @DateTime 2020/7/30 10:24
 */
public abstract class BaseServiceImpl<T> implements BaseService<T> {

    /**
     *  由子类实现
     * @return
     */
    public abstract BaseMapper<T> getBaseMapper();

    private Class<T> doClass;

    public BaseServiceImpl() {
        doClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    protected abstract Example getExample(BaseRequest request);

    @Override
    public int saveModel(T model) throws GlobalException {
        return this.getBaseMapper().insertSelective(model);
    }

    @Override
    public int removeModel(Serializable id) throws GlobalException {
        return this.getBaseMapper().deleteByPrimaryKey(id);
    }

    @Override
    public int removeBatch(List<? extends Serializable> idList) throws GlobalException {
        Example example = new Example(doClass);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("id", idList);
        return this.getBaseMapper().deleteByExample(example);
    }

    @Override
    public int updateModel(T model) throws GlobalException {
        return this.getBaseMapper().updateByPrimaryKeySelective(model);
    }

    @Override
    public T findById(Serializable id) throws GlobalException {
        return this.getBaseMapper().selectByPrimaryKey(id);
    }

    @Override
    public List<T> findAll() throws GlobalException {
        return this.getBaseMapper().selectAll();
    }

    @Override
    public List<T> findAll(BaseRequest<T> request) throws GlobalException {
        Example example = this.getExample(request);
        return this.getBaseMapper().selectByExample(example);
    }

    @Override
    public PageInfo<T> findPage(BaseRequest<T> request) throws GlobalException {
        PageHelper.startPage(request.getPageNum(), request.getPageSize());
        Example example = this.getExample(request);
        List<T> list;
        if (example != null) {
            list = this.getBaseMapper().selectByExample(example);
        } else {
            list = this.getBaseMapper().selectAll();
        }
        return new PageInfo<>(list);
    }
}
