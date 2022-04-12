package com.light.hexo.common.component.log;

import com.light.hexo.common.base.BaseRequest;
import com.light.hexo.common.component.event.EventPublisher;
import com.light.hexo.common.util.BrowserUtil;
import com.light.hexo.common.util.IpUtil;
import com.light.hexo.common.util.JsonUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.jsoup.internal.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author MoonlightL
 * @ClassName: ActionLogAspect
 * @ProjectName hexo-boot
 * @Description: 操作日志切面
 * @DateTime 2021/7/7 17:41
 */
@Component
@Aspect
public class ActionLogAspect {

    @Autowired
    @Lazy
    private EventPublisher eventPublisher;

    @Pointcut("@annotation(com.light.hexo.common.component.log.OperateLog)")
    public void logPointCut() {}

    @Around("logPointCut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        Object result = point.proceed();
        this.saveActionLog(point);
        return result;
    }

    /**
     * 保存日志
     * @param joinPoint
     * @throws Exception
     */
    private void saveActionLog(ProceedingJoinPoint joinPoint) {

        try {
            RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
            ServletRequestAttributes sra = (ServletRequestAttributes) requestAttributes;
            if (sra == null) {
                return;
            }

            HttpServletRequest request = sra.getRequest();

            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            // 执行方法
            Method method = signature.getMethod();
            // 方法参数名
            String[] parameterNames = signature.getParameterNames();
            // 方法参数值
            Object[] args = joinPoint.getArgs();

            OperateLog operateLog = method.getAnnotation(OperateLog.class);
            if (operateLog != null) {
                ActionLogEvent event = new ActionLogEvent();
                event.setMethodName(method.getDeclaringClass().getName() + "." + method.getName())
                     .setMethodParam(this.getParameter(parameterNames, args))
                     .setIpAddress(IpUtil.getIpAddr(request))
                     .setBrowser(BrowserUtil.getBrowserName(request))
                     .setRemark(operateLog.value())
                     .setActionType(operateLog.actionType().getCode())
                     .setCreateTime(LocalDateTime.now());
                this.eventPublisher.emit(event);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getParameter(String[] parameterNames, Object[] args) {

        if (args == null || args.length == 0) {
            return "";
        }

        Object arg = args[0];
        Map<String, Object> map = new HashMap<>(10);

        if (arg instanceof BaseRequest) {
            Class<?> clazz = arg.getClass();
            Field[] fs = clazz.getDeclaredFields();

            try {
                for (Field f : fs) {
                    f.setAccessible(true);
                    if ("content".equals(f.getName())) {
                        continue;
                    }

                    Object val = f.get(arg);
                    if (val != null) {
                        if ("password".equals(f.getName())) {
                            map.put(f.getName(), DigestUtils.md5DigestAsHex(val.toString().trim().getBytes()));
                        } else {
                            map.put(f.getName(), val);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else if (arg instanceof HttpServletRequest) {
            HttpServletRequest request = (HttpServletRequest) arg;
            Map<String, String[]> parameterMap = request.getParameterMap();
            parameterMap.forEach((key, value) -> map.put(key, StringUtil.join(value, ",")));

        } else if (arg instanceof MultipartFile[]) {
            MultipartFile[] files = (MultipartFile[]) arg;
            int index = 1;
            for (MultipartFile file : files) {
                map.put("fileName_" + (index++), file.getOriginalFilename());
            }

        } else if (arg instanceof MultipartFile) {
            MultipartFile file = (MultipartFile) arg;
            map.put("fileName", file.getOriginalFilename());

        } else {
            for (int i = 0; i < parameterNames.length; i++) {
                if (!(args[i] instanceof HttpServletRequest ) && !(args[i] instanceof HttpServletResponse)) {
                    map.put(parameterNames[i], args[i]);
                }
            }
        }

        return JsonUtil.obj2StringPretty(map);
    }

}
