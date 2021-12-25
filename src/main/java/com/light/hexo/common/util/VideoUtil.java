package com.light.hexo.common.util;

import cn.hutool.core.util.RandomUtil;
import com.light.hexo.business.admin.config.BlogProperty;
import com.light.hexo.business.admin.constant.ConfigEnum;
import com.light.hexo.business.admin.service.ConfigService;
import org.apache.commons.lang3.StringUtils;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * @Author: MoonlightL
 * @ClassName: VideoUtil
 * @ProjectName: hexo-boot
 * @Description:
 * @DateTime: 2021-12-25 22:50
 */
@Component
public class VideoUtil {

    @Autowired
    private BlogProperty blogProperty;

    @Autowired
    private ConfigService configService;

    @Autowired
    private Environment environment;


    /**
     * 创建视频封面
     * @param videoName
     * @param videoUrl
     * @return 返回封面地址
     */
    public String createCover(String videoName, String videoUrl) {
        String coverUrl = "";
        FFmpegFrameGrabber grabber = null;
        try {
            grabber = FFmpegFrameGrabber.createDefault(videoUrl);
            grabber.start();
            //设置视频截取帧（默认取第一帧）
            Frame frame = grabber.grabImage();
            //视频旋转度
            Java2DFrameConverter converter = new Java2DFrameConverter();
            //绘制图片
            BufferedImage bi = converter.getBufferedImage(frame);
            //图片的类型
            String imageMat = "jpg";
            //图片的完整路径
            String filePath = this.configService.getConfigValue(ConfigEnum.LOCAL_FILE_PATH.getName());
            String localFilePath = StringUtils.isNotBlank(filePath) ? filePath  + File.separator : this.blogProperty.getAttachmentDir();
            String coverName = videoName + "_" + RandomUtil.randomNumbers(6) + "." + imageMat;
            String coverPath = localFilePath + "/cover/" + coverName;
            File target = new File(coverPath);
            if (!target.getAbsoluteFile().exists()) {
                target.getAbsoluteFile().mkdirs();
            }

            //创建文件
            ImageIO.write(bi, imageMat, target);
            String blogPage = this.configService.getConfigValue(ConfigEnum.HOME_PAGE.getName());
            coverUrl = (StringUtils.isNotBlank(blogPage) ? blogPage : "http://" + IpUtil.getHostIp() + ":" + this.environment.getProperty("server.port")) + "/cover/" + coverName;

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (grabber != null) {
                    grabber.stop();
                }
            } catch (FrameGrabber.Exception e) {
                e.printStackTrace();
            }
        }

        return coverUrl;
    }
}
