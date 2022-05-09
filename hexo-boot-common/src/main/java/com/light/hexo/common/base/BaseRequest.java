package com.light.hexo.common.base;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @Author MoonlightL
 * @ClassName: BaseRequest
 * @ProjectName hexo-boot
 * @Description: 请求基类
 * @DateTime 2020/8/11 17:10
 */
@Setter
@Getter
@Slf4j
public abstract class BaseRequest<T> {

    /**
     * 当前页
     */
    private Integer pageNum = 1;

    /**
     * 页面大小
     */
    private Integer pageSize = 10;

    private Class<T> doClass;

    public BaseRequest() {
        try {
            Type[] actualTypeArguments = ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments();
            if (actualTypeArguments.length > 0) {
                doClass = (Class<T>) actualTypeArguments[0];
            }
        } catch (Exception e) {
            log.warn("==========BaseRequest 获取泛型参数异常：getClass: {}, error: {}==========", getClass(), e.getMessage());
        }
    }

    /**
     * 转成 DO 模型
     * @return
     */
    public T toDoModel() {
        if (doClass == null) {
            return null;
        }

        try {
            T doModel = doClass.newInstance();
            BeanUtils.copyProperties(this, doModel);
            return doModel;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public interface Save {}

    public interface Remove {}

    public interface Update {}

    public interface Query {}
}
