package com.light.hexo.core.admin.component;

import com.light.hexo.common.constant.ConfigEnum;
import com.light.hexo.core.admin.service.ConfigService;
import com.light.hexo.common.util.HttpClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * @Author MoonlightL
 * @ClassName: BaiDuPushService
 * @ProjectName hexo-boot
 * @Description: 百度推送
 * @DateTime 2020/9/17 17:51
 */
@Component
@Slf4j
public class BaiDuPushService {

    private static final String BAI_DU_PUSH_URL = "http://data.zz.baidu.com/urls?site=HOME_PAGE&token=TOKEN";

    @Autowired
    private ConfigService configService;

    /**
     * 百度推送
     * @param url
     */
    @Async
    public void push2BaiDu(String url) {

        String home_page = this.configService.getConfigValue(ConfigEnum.HOME_PAGE.getName());
        if (StringUtils.isBlank(home_page)) {
            log.info("===== push2BaiDu home_page 为空 =====");
            return;
        }

        String bai_du_push_token = this.configService.getConfigValue(ConfigEnum.BAI_DU_PUSH_TOKEN.getName());
        if (StringUtils.isBlank(bai_du_push_token)) {
            log.info("===== push2BaiDu bai_du_push_token 为空 =====");
            return;
        }

        String requestUrl = BAI_DU_PUSH_URL.replace("HOME_PAGE", home_page).replace("TOKEN", bai_du_push_token);
        String result = HttpClientUtil.sendPost(requestUrl, home_page + "/" + url);

        log.info("===== push2BaiDu result:{}=====", result);
    }
}
