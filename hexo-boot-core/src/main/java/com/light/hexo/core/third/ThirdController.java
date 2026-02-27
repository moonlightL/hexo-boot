package com.light.hexo.core.third;

import cn.hutool.core.codec.Base64Encoder;
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
import sun.misc.BASE64Encoder;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
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

    private static final String[] RANDOM_PIC_URL = {
            "https://api.r10086.com/%E6%A8%B1%E9%81%93%E9%9A%8F%E6%9C%BA%E5%9B%BE%E7%89%87api%E6%8E%A5%E5%8F%A3.php?%E5%9B%BE%E7%89%87%E7%B3%BB%E5%88%97=%E9%A3%8E%E6%99%AF%E7%B3%BB%E5%88%978&%E5%8F%82%E6%95%B0=json",
            "https://api.r10086.com/%E6%A8%B1%E9%81%93%E9%9A%8F%E6%9C%BA%E5%9B%BE%E7%89%87api%E6%8E%A5%E5%8F%A3.php?%E5%9B%BE%E7%89%87%E7%B3%BB%E5%88%97=%E5%8A%A8%E6%BC%AB%E7%BB%BC%E5%90%8815&%E5%8F%82%E6%95%B0=json"
    };

    @RequestMapping(value = "/admin/file/randomPic.json", method = RequestMethod.POST)
    @ResponseBody
    public FileResult randomPic(Integer type) throws GlobalException, IOException {

        String result = HttpClientUtil.sendGet(RANDOM_PIC_URL[type] + "&timestamp=" + System.currentTimeMillis());
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
