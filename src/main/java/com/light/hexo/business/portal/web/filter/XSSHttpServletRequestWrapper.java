package com.light.hexo.business.portal.web.filter;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.util.Enumeration;
import java.util.Map;
import java.util.Vector;

/**
 * @Author: MoonlightL
 * @ClassName: XSSHttpServletRequestWrapper
 * @ProjectName: hexo-boot
 * @Description: 重写 HttpServletRequestWrapper
 * @DateTime: 2020/10/4 09:53
 */
public class XSSHttpServletRequestWrapper extends HttpServletRequestWrapper {

    private Map<String, String[]> parameterMap;

    public XSSHttpServletRequestWrapper(HttpServletRequest request) {
        super(request);
        this.parameterMap = request.getParameterMap();
    }

    @Override
    public String getParameter(String name) {

        String value = super.getParameter(name);

        if (StringUtils.isNoneBlank(value)) {
            value = StringEscapeUtils.escapeHtml4(value);
        }

        return value;
    }

    @Override
    public String[] getParameterValues(String name) {

        String[] data = this.parameterMap.get(name);
        if (data != null && data.length > 0) {
            for (int i = 0; i < data.length; i++) {
                data[i] = StringEscapeUtils.escapeHtml4(data[i]);
            }
        }

        return data;
    }

    @Override
    public Enumeration<String> getParameterNames() {
        Vector<String> vector = new Vector<>(parameterMap.keySet());
        return vector.elements();
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        return parameterMap;
    }
}


