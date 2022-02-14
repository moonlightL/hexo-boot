package com.light.hexo.common.base;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.light.hexo.common.exception.GlobalException;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import tk.mybatis.mapper.entity.Example;
import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.List;

/**
 * @Author MoonlightL
 * @ClassName: CommonServiceImpl
 * @ProjectName hexo-boot
 * @Description: 抽象的 CommonService 实现
 * @DateTime 2022/1/27, 0027 13:45
 */
public abstract class CommonServiceImpl<T, M extends BaseMapper> implements CommonService<T, M>, ApplicationContextAware {

    private Class<T> doClass;
    
    protected M mapper;

    protected ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @PostConstruct
    public void init() {
        doClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        Class<M> mapperClass = (Class<M>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[1];
        mapper = applicationContext.getBean(mapperClass);
    }

    protected abstract Example getExample(BaseRequest request);

    @Override
    public int saveModel(T model) throws GlobalException {
        return this.mapper.insertSelective(model);
    }

    @Override
    public int removeModel(Serializable id) throws GlobalException {
        return this.mapper.deleteByPrimaryKey(id);
    }

    @Override
    public int removeBatch(List<? extends Serializable> idList) throws GlobalException {
        Example example = new Example(doClass);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("id", idList);
        return this.mapper.deleteByExample(example);
    }

    @Override
    public int updateModel(T model) throws GlobalException {
        return this.mapper.updateByPrimaryKeySelective(model);
    }

    @Override
    public T findById(Serializable id) throws GlobalException {
        return (T) this.mapper.selectByPrimaryKey(id);
    }

    @Override
    public List<T> findAll() throws GlobalException {
        return this.mapper.selectAll();
    }

    @Override
    public List<T> findAll(BaseRequest<T> request) throws GlobalException {
        Example example = this.getExample(request);
        return this.mapper.selectByExample(example);
    }

    @Override
    public PageInfo<T> findPage(BaseRequest<T> request) throws GlobalException {
        PageHelper.startPage(request.getPageNum(), request.getPageSize());
        Example example = this.getExample(request);
        List<T> list;
        if (example != null) {
            list = this.mapper.selectByExample(example);
        } else {
            list = this.mapper.selectAll();
        }
        return new PageInfo<>(list);
    }
}
