package com.light.hexo.common.constant;

/**
 * @Author MoonlightL
 * @Title: HexoConstant
 * @ProjectName: hexo-boot
 * @Description: 公共常量
 * @DateTime: 2020/7/30 19:26
 */
public class HexoConstant {

	public static final Integer CHANGE_NUM = 6;

	/**
	 * 登录用户 KEY
	 */
	public static final String CURRENT_USER = "currentUser";

	/**
	 * 验证码
	 */
	public static final String CAPTCHA = "captcha";

	/**
	 * 默认图片目录
	 */
	public static final String DEFAULT_IMG_DIR = "/admin/assets/custom/images";

	/**
	 * 默认用户头像
	 */
	public static final String DEFAULT_AVATAR = DEFAULT_IMG_DIR + "/avatar/default_avatar.jpg";

	/**
	 * 默认分类图片
	 */
	public static final String DEFAULT_CATEGORY_COVER = DEFAULT_IMG_DIR + "/category/default_category.jpg";

	/**
	 * 默认文章封面
	 */
	public static final String DEFAULT_POST_COVER = DEFAULT_IMG_DIR + "/post/default_post.jpg";

	/**
	 * 评论插件用户头像目录
	 */
	public static final String COMMENT_USER_AVATAR_DIR = "/admin/assets/custom/hb-comment/image/avatar";

	/**
	 * 默认视频封面
	 */
	public static final String DEFAULT_VIDEO_COVER = DEFAULT_IMG_DIR + "/video.jpg";
}
