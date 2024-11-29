package com.light.hexo.core.third;

import com.light.hexo.common.exception.GlobalException;
import com.light.hexo.common.request.FileResult;
import com.light.hexo.common.request.bing.WebPic;
import com.light.hexo.common.util.HttpClientUtil;
import com.light.hexo.common.util.JsonUtil;
import com.light.hexo.common.vo.Result;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.BOMInputStream;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author MoonlightL
 * @ClassName: ThirdController
 * @Description: 第三方库控制器
 * @DateTime 2024/11/28 18:19
 */
@Controller
public class ThirdController {

    /**
     * 鸡汤
     * @return
     */
    @GetMapping("chickenSoup.json")
    @ResponseBody
    public Result chickenSoup() {
        String result = HttpClientUtil.sendGet("https://api.likepoems.com/ana/dujitang/");
        Map<String, Object> map = new HashMap<>();
        map.put("data", result);
        return Result.success(map);
    }

    private static final String[] RANDOM_PIC_URL = {"https://api.likepoems.com/img/nature/?timestamp=%s&json", "https://api.likepoems.com/img/pc/?timestamp=%s&json"};

    @RequestMapping(value = "/admin/file/randomPic.json", method = RequestMethod.POST)
    @ResponseBody
    public FileResult randomPic(Integer type) throws GlobalException, IOException {

        String result = HttpClientUtil.sendPost(String.format(RANDOM_PIC_URL[type], System.currentTimeMillis()), "");
        if (StringUtils.isBlank(result)) {
            return FileResult.fail("第三方图库出现异常，获取图片失败，请稍后再试");
        }

        // 处理 json 串包含 UTF-8 的 bom 不可见字符
        InputStream bis = new BOMInputStream(new ByteArrayInputStream(result.getBytes()));
        String finalResult = IOUtils.toString(bis, "UTF-8");

        WebPic webPic = JsonUtil.string2Obj(finalResult, WebPic.class);
        if (webPic == null || !"200".equals(webPic.getCode())) {
            return FileResult.fail("第三方图库出现异常，获取图片失败，请联系博客作者修复");
        }

        return FileResult.success(webPic.getImgurl());
    }
}
