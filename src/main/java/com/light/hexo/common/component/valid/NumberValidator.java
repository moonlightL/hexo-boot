package com.light.hexo.common.component.valid;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author MoonlightL
 * @ClassName: NumberVlidator
 * @ProjectName hexo-boot
 * @Description: 数字检查
 * @DateTime 2020/8/14 14:24
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {NumberValidatorImpl.class})
public @interface NumberValidator {

    String message() default "该字段值必须为数字";

    /**
     * 约束注解在验证时所属的组别
     */
    Class<?>[] groups() default { };

    /**
     * 约束注解的有效负载
     */
    Class<? extends Payload>[] payload() default { };
}
