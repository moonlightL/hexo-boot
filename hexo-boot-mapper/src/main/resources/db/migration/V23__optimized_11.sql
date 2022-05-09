CREATE TABLE `t_comment` (
	`id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
	`page` VARCHAR(255) NOT NULL DEFAULT '' COMMENT '评论页面',
	`nickname` VARCHAR(16) NOT NULL DEFAULT '' COMMENT '评论用户昵称',
	`avatar` VARCHAR(255) NOT NULL DEFAULT '' COMMENT '评论头像',
	`email` VARCHAR(64) NOT NULL DEFAULT '' COMMENT '邮箱地址',
	`home_page` VARCHAR(255) NOT NULL DEFAULT '' COMMENT '主页',
	`is_blogger` TINYINT(4) NOT NULL DEFAULT '0' COMMENT '是否为博主',
	`content` VARCHAR(500) NOT NULL DEFAULT '' COMMENT '评论内容',
	`p_id` INT(11) NOT NULL DEFAULT '0' COMMENT '父级评论 id',
	`banner_id` INT(11) NOT NULL DEFAULT '0' COMMENT '面板 id',
	`source_nickname` VARCHAR(16) NOT NULL DEFAULT '' COMMENT '被回复者昵称',
	`is_delete` TINYINT(4) NOT NULL DEFAULT '0' COMMENT '是否删除',
	`ip_address` VARCHAR(64) NOT NULL DEFAULT '' COMMENT 'ip 地址',
	`os_name` VARCHAR(32) NOT NULL DEFAULT '' COMMENT '系统名称',
	`browser` VARCHAR(32) NOT NULL DEFAULT '' COMMENT '浏览器',
	`praise_num` INT(11) NOT NULL DEFAULT '0' COMMENT '点赞数',
	`comment_type` TINYINT(4) NOT NULL DEFAULT '0' COMMENT '评论类型 1:文章 2:留言',
	`create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
	`update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
	PRIMARY KEY (`id`),
	INDEX `idx_p_id` (`p_id`),
	INDEX `idx_create_time` (`create_time`),
	INDEX `idx_nickname` (`nickname`),
	INDEX `idx_email` (`email`),
	INDEX `idx_banner_id` (`banner_id`)
)
COMMENT='评论表'
;