package com.light.hexo.core.portal.component;

import cn.hutool.core.net.URLDecoder;
import com.light.hexo.common.util.CacheUtil;
import com.light.hexo.common.util.JsonUtil;
import com.light.hexo.common.util.RequestUtil;
import com.light.hexo.common.vo.Result;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @Author MoonlightL
 * @ClassName: RequestLimitAspect
 * @ProjectName hexo-boot
 * @Description: 请求限制切面
 * @DateTime 2020/9/21 16:26
 */
@Component
@Aspect
public class RequestLimitAspect {

    @Pointcut("@annotation(com.light.hexo.core.portal.component.RequestLimit)")
    public void limitPointCut() {}

    @Around("limitPointCut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {

        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes sra = (ServletRequestAttributes) requestAttributes;
        if (sra == null) {
            return null;
        }

        HttpServletRequest request = sra.getRequest();
        HttpServletResponse response = sra.getResponse();

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();

        Object proceed = joinPoint.proceed();

        Method method = signature.getMethod();
        RequestLimit commentLimit = method.getAnnotation(RequestLimit.class);
        if (commentLimit != null) {
            String cacheName = commentLimit.cacheName();
            int time = commentLimit.time();
            String msg = commentLimit.msg();
            String nickname = URLDecoder.decode(request.getParameter("nickname"), Charset.defaultCharset());
            String cacheKey = cacheName + ":" + nickname;
            Object cacheObj = CacheUtil.get(cacheKey);
            if (cacheObj != null) {
                this.print(response, JsonUtil.obj2String(Result.fail(401, msg)));
                return null;
            }
            CacheUtil.put(cacheKey, nickname, time * 1000);
        }

        return proceed;
    }

    private void print(HttpServletResponse response, String result) throws IOException {
        if (response != null) {
            response.setContentType("application/json;charset=UTF-8");
            response.getOutputStream().write(result.getBytes(StandardCharsets.UTF_8));
        }
    }
}
