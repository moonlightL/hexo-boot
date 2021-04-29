ALTER TABLE `t_friend_link`
	CHANGE COLUMN `home_url` `home_url` VARCHAR(255) NOT NULL DEFAULT '' COMMENT '主页地址' AFTER `author`;
