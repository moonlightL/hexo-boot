package com.light.hexo.common.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import java.text.SimpleDateFormat;

/**
 * @Author MoonlightL
 * @ClassName: JsonUtil
 * @ProjectName hexo-boot
 * @Description: JSON 工具类
 * @DateTime 2020/9/7 17:43
 */
public class JsonUtil {

    private static ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 日期格式化
     */
    private static final String STANDARD_FORMAT = "yyyy-MM-dd HH:mm:ss";

    static {
        // 对象的所有字段全部列入
        objectMapper.setSerializationInclusion(JsonInclude.Include.ALWAYS);

        // 取消默认转换 timestamps 形式
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS,false);

        // 忽略空 Bean 转 json 的错误
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS,false);

        // 所有的日期格式都统一为以下的样式，即 yyyy-MM-dd HH:mm:ss
        objectMapper.setDateFormat(new SimpleDateFormat(STANDARD_FORMAT));

        // 忽略 在 json 字符串中存在，但是在 java 对象中不存在对应属性的情况。防止错误
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);

        // 允许使用未带引号的字段名
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);

        // 允许使用单引号
        objectMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
    }

    /**
     * 对象转Json格式字符串
     * @param obj 对象
     * @return Json格式字符串
     */
    public static <T> String obj2String(T obj) {
        if (obj == null) {
            return null;
        }
        try {
            return obj instanceof String ? (String) obj : objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 对象转Json格式字符串(格式化的Json字符串)
     * @param obj 对象
     * @return 美化的Json格式字符串
     */
    public static <T> String obj2StringPretty(T obj) {
        if (obj == null) {
            return null;
        }
        try {
            return obj instanceof String ? (String) obj : objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 字符串转对象
     *
     * @param json
     * @param clazz
     * @return
     */
    public static <T> T string2Obj(String json, Class<T> clazz) {
        if (json == null || "".equals(json) || clazz == null) {
            return null;
        }

        try {
            return clazz.equals(String.class) ? (T) json : objectMapper.readValue(json, clazz);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static <T> T string2Obj(String json, TypeReference<T> typeReference) {
        if (json == null || "".equals(json) || typeReference == null) {
            return null;
        }

        try {
            return typeReference.getType().equals(String.class) ? (T) json : objectMapper.readValue(json, typeReference);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static <T> T string2Obj(String json, Class<?> collectionClass, Class<?> ... elementClasses) {
        if (json == null || "".equals(json) || collectionClass == null) {
            return null;
        }

        JavaType javaType = objectMapper.getTypeFactory().constructParametricType(collectionClass, elementClasses);

        try {
            return objectMapper.readValue(json, javaType);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
