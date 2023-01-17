ALTER TABLE `t_post`
	ADD COLUMN `layout` TINYINT NOT NULL DEFAULT '1' COMMENT '文章布局 1：混合 2：文字 3：图片 4：视频' AFTER `top_time`;
