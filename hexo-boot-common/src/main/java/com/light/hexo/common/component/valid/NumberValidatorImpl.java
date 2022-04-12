package com.light.hexo.common.component.valid;

import org.apache.commons.lang3.StringUtils;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @Author MoonlightL
 * @ClassName: NumberValidatorImpl
 * @ProjectName hexo-boot
 * @Description: 数字检查
 * @DateTime 2020/8/14 14:26
 */
public class NumberValidatorImpl implements ConstraintValidator<NumberValidator, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return StringUtils.isNumeric(value);
    }
}
