ALTER TABLE `t_friend_link`
	ADD COLUMN `link_type` TINYINT(4) NOT NULL DEFAULT '1' COMMENT '类型 1：博主主页 2：常用网址' AFTER `sort`;

ALTER TABLE `t_friend_link`
	ADD COLUMN `background_color` VARCHAR(10) NOT NULL DEFAULT '' COMMENT '背景颜色' AFTER `link_type`;

