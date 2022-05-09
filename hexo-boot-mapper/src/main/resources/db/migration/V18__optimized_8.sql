ALTER TABLE `t_post`
	ADD COLUMN `auth_code` VARCHAR(20) NOT NULL DEFAULT '' COMMENT '访问密码' AFTER `comment_num`;
