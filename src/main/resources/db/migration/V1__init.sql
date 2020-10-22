CREATE DATABASE IF NOT EXISTS `hexo-boot`;
USE `hexo-boot`;

CREATE TABLE IF NOT EXISTS `t_user` (
  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `username` VARCHAR(32) NOT NULL DEFAULT '' COMMENT '用户名',
  `password` VARCHAR(64) NOT NULL DEFAULT '' COMMENT '密码',
  `nickname` VARCHAR(16) NOT NULL DEFAULT '' COMMENT '昵称',
  `email` VARCHAR(64) NOT NULL DEFAULT '' COMMENT '邮箱地址',
  `avatar` VARCHAR(255) NOT NULL DEFAULT '' COMMENT '头像',
  `home_url` VARCHAR(255) NOT NULL DEFAULT '' COMMENT '主页地址',
  `state` TINYINT(4) NOT NULL DEFAULT '1' COMMENT '状态',
  `role` TINYINT(4) NOT NULL DEFAULT '2' COMMENT '角色 1：博主 2：用户',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`),
  UNIQUE INDEX `idx_email` (`email`),
  UNIQUE INDEX `idx_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

CREATE TABLE IF NOT EXISTS `t_user_extend` (
	`id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
	`uid` INT(11) NOT NULL DEFAULT '0' COMMENT '用户 id',
	`descr` TEXT NOT NULL COMMENT '自我介绍',
	`create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
	`update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
	PRIMARY KEY (`id`),
	INDEX `idx_uid` (`uid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户信息扩展表';

CREATE TABLE IF NOT EXISTS `t_category` (
  `id` INT(10) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(10) NOT NULL DEFAULT '' COMMENT '名称',
  `cover_url` varchar(255) NOT NULL DEFAULT '' COMMENT '图片地址',
  `state` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态 1：可用 0：禁用',
  `sort` tinyint(4) NOT NULL DEFAULT '0' COMMENT '排序',
  `remark` varchar(255) NOT NULL DEFAULT '' COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='分类表';

CREATE TABLE IF NOT EXISTS `t_tag` (
  `id` INT(10) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(16) NOT NULL DEFAULT '' COMMENT '名称',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='标签表';

CREATE TABLE IF NOT EXISTS `t_post` (
  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `title` VARCHAR(64) NOT NULL DEFAULT '' COMMENT '标题',
  `summary` VARCHAR(300) NOT NULL DEFAULT '' COMMENT '摘要',
  `content` TEXT NOT NULL COMMENT '内容（md 格式）',
  `author` VARCHAR(16) NOT NULL DEFAULT '' COMMENT '作者',
  `year` CHAR(4) NOT NULL DEFAULT '' COMMENT '发表年份',
  `month` CHAR(2) NOT NULL DEFAULT '' COMMENT '发表月份',
  `day` CHAR(2) NOT NULL DEFAULT '' COMMENT '发表日期',
  `cover_url` VARCHAR(255) NOT NULL DEFAULT '' COMMENT '封面图片地址',
  `publish_date` VARCHAR(10) NOT NULL DEFAULT '' COMMENT '发表时间（yyyy-MM-dd）',
  `is_publish` TINYINT(4) NOT NULL DEFAULT '0' COMMENT '是否发表',
  `is_comment` TINYINT(4) NOT NULL DEFAULT '0' COMMENT '是否允许评论',
  `is_top` TINYINT(4) NOT NULL DEFAULT '0' COMMENT '是否置顶',
  `is_delete` TINYINT(4) NOT NULL DEFAULT '0' COMMENT '是否删除',
  `is_reprint` TINYINT(4) NOT NULL DEFAULT '0' COMMENT '是否转载',
  `reprint_link` VARCHAR(255) NOT NULL DEFAULT '' COMMENT '转载链接',
  `category_id` INT(20) NOT NULL DEFAULT '0' COMMENT '分类 id',
  `link` VARCHAR(128) NOT NULL DEFAULT '' COMMENT '文章链接',
  `tags` VARCHAR(50) NOT NULL DEFAULT '' COMMENT '标签，多个标签使用逗号拼接',
  `read_num` INT(11) NOT NULL DEFAULT '0' COMMENT '阅读数',
  `praise_num` INT(11) NOT NULL DEFAULT '0' COMMENT '点赞数',
  `comment_num` INT(11) NOT NULL DEFAULT '0' COMMENT '评论数',
  `top_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '置顶时间',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`),
  INDEX `idx_category_id` (`category_id`),
  INDEX `idx_title` (`title`),
  INDEX `idx_link` (`link`),
  INDEX `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文章表';

CREATE TABLE IF NOT EXISTS `t_post_tag` (
  `id` INT(10) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `post_id` INT(10) NOT NULL DEFAULT '0' COMMENT '文章 id',
  `tag_id` INT(10) NOT NULL DEFAULT '0' COMMENT '标签 id',
  PRIMARY KEY (`id`),
  INDEX `idx_post_id` (`post_id`),
  INDEX `idx_tag_id` (`tag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文章标签关系表';

CREATE TABLE `t_post_comment` (
	`id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
	`post_id` INT(10) NOT NULL DEFAULT '0' COMMENT '文章 id',
	`title` VARCHAR(50) NOT NULL DEFAULT '' COMMENT '文章标题',
	`user_id` INT(10) NOT NULL DEFAULT '0' COMMENT '评论用户 id',
	`nickname` VARCHAR(16) NOT NULL DEFAULT '' COMMENT '评论用户昵称',
	`content` VARCHAR(255) NOT NULL DEFAULT '' COMMENT '评论内容',
	`p_id` INT(11) NOT NULL DEFAULT '0' COMMENT '父级评论 id',
	`banner_id` INT(11) NOT NULL DEFAULT '0' COMMENT '面板 id',
	`source_nickname` VARCHAR(16) NOT NULL DEFAULT '' COMMENT '被回复者昵称',
	`is_delete` TINYINT(4) NOT NULL DEFAULT '0' COMMENT '是否删除',
	`ip_address` VARCHAR(64) NOT NULL DEFAULT '' COMMENT 'ip 地址',
	`browser` VARCHAR(32) NOT NULL DEFAULT '' COMMENT '浏览器',
	`praise_num` INT(11) NOT NULL DEFAULT '0' COMMENT '点赞数',
	`create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
	`update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
	PRIMARY KEY (`id`),
	INDEX `idx_user_id` (`user_id`),
	INDEX `idx_p_id` (`p_id`),
	INDEX `idx_post_id` (`post_id`),
	INDEX `idx_create_time` (`create_time`),
	INDEX `idx_nickname` (`nickname`),
	INDEX `idx_title` (`title`),
	INDEX `idx_banner_id` (`banner_id`)
)  ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评论表';

CREATE TABLE IF NOT EXISTS `t_guest_book` (
  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` INT(10) NOT NULL DEFAULT '0' COMMENT '留言用户 id',
  `nickname` VARCHAR(16) NOT NULL DEFAULT '' COMMENT '留言昵称',
  `content` VARCHAR(255) NOT NULL DEFAULT '' COMMENT '留言内容',
  `p_id` INT(10) NOT NULL DEFAULT '0' COMMENT '父级留言 id',
  `banner_id` INT(10) NOT NULL DEFAULT '0' COMMENT '面板 id',
  `source_nickname` VARCHAR(16) NOT NULL DEFAULT '' COMMENT '被回复者 id',
  `is_delete` TINYINT(4) NOT NULL DEFAULT '0' COMMENT '是否删除',
  `ip_address` VARCHAR(64) NOT NULL DEFAULT '' COMMENT 'ip 地址',
  `browser` VARCHAR(32) NOT NULL DEFAULT '' COMMENT '浏览器',
  `praise_num` INT(11) NOT NULL DEFAULT '0' COMMENT '点赞数',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`),
  INDEX `idx_user_id` (`user_id`),
  INDEX `idx_nickname` (`nickname`),
  INDEX `idx_p_id` (`p_id`),
  INDEX `idx_create_time` (`create_time`),
  INDEX `idx_banner_id` (`banner_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='留言表';

CREATE TABLE IF NOT EXISTS `t_backup` (
  `id` INT(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` VARCHAR(50) NOT NULL DEFAULT '' COMMENT '名称',
  `file_path` VARCHAR(255) NOT NULL DEFAULT '' COMMENT '路径',
  `file_size` BIGINT(20) NOT NULL DEFAULT '0' COMMENT '文件大小',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='备份表';

CREATE TABLE IF NOT EXISTS `t_config` (
	`id` INT(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
	`config_key` VARCHAR(50) NOT NULL DEFAULT '' COMMENT '参数名',
	`config_value` VARCHAR(120) NOT NULL DEFAULT '' COMMENT '参数值',
	`remark` VARCHAR(50) NOT NULL DEFAULT '' COMMENT '备注',
	`create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
	`update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
	PRIMARY KEY (`id`),
	UNIQUE INDEX `idx_config_key` (`config_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='全局配置表';

CREATE TABLE IF NOT EXISTS `t_blacklist` (
  `id` INT(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `ip_address` VARCHAR(64) NOT NULL DEFAULT '' COMMENT 'ip 地址',
  `remark` VARCHAR(255) NOT NULL DEFAULT '' COMMENT '备注',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`),
  UNIQUE INDEX `idx_ip_address` (`ip_address`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='黑名单表';

CREATE TABLE IF NOT EXISTS `t_attachment` (
  `id` INT(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `filename` VARCHAR(128) NOT NULL DEFAULT '' COMMENT '文件名称',
  `original_name` VARCHAR(128) NOT NULL DEFAULT '' COMMENT '原始文件名称',
  `file_url` VARCHAR(255) NOT NULL DEFAULT '' COMMENT '文件路径',
  `thumbnail_url` VARCHAR(255) NOT NULL DEFAULT '' COMMENT '缩略图路径',
  `file_path` VARCHAR(255) NOT NULL DEFAULT '' COMMENT '本地路径',
  `file_key` VARCHAR(255) NOT NULL DEFAULT '' COMMENT '文件 key(七牛云返回)',
  `content_type` VARCHAR(50) NOT NULL DEFAULT '' COMMENT '内容类型',
  `file_type` TINYINT(4) NOT NULL DEFAULT '1' COMMENT '文件类型 1：普通 2：图片 3：视频',
  `file_size` BIGINT(20) NOT NULL DEFAULT '0' COMMENT '文件大小',
  `position` TINYINT(4) NOT NULL DEFAULT '1' COMMENT '位置 1：本地 2：七牛云 3：OSS',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`),
  INDEX `idx_filename` (`filename`),
  INDEX `idx_original_filename` (`original_name`),
  INDEX `idx_create_time` (`create_time`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4 COMMENT='附件表';

CREATE TABLE IF NOT EXISTS `t_friend_link` (
  `id` BIGINT(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `title` VARCHAR(16) NOT NULL DEFAULT '' COMMENT '标题',
  `author` VARCHAR(10) NOT NULL DEFAULT '' COMMENT '作者',
  `home_url` VARCHAR(50) NOT NULL DEFAULT '' COMMENT '主页地址',
  `email` VARCHAR(64) NOT NULL DEFAULT '' COMMENT '邮箱地址',
  `sort` TINYINT(4) NOT NULL DEFAULT '0' COMMENT '排序',
  `remark` VARCHAR(255) NOT NULL DEFAULT '' COMMENT '备注',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='友链表';

CREATE TABLE `t_visit` (
	`id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
	`ip_address` VARCHAR(64) NOT NULL DEFAULT '' COMMENT 'ip 地址',
	`browser` VARCHAR(32) NOT NULL DEFAULT '' COMMENT '浏览器',
	`visit_date` DATE NOT NULL COMMENT '访问日期',
	`create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
	`update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
	PRIMARY KEY (`id`),
	INDEX `idx_ip_address` (`ip_address`),
	INDEX `idx_visit_date` (`visit_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='网站访问表';

CREATE TABLE `t_message` (
	`id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
	`content` VARCHAR(50) NOT NULL DEFAULT '' COMMENT '消息内容',
	`msg_type` TINYINT(4) NOT NULL DEFAULT '0' COMMENT '消息类型 1：评论 2：留言',
	`is_read` TINYINT(4) NOT NULL DEFAULT '0' COMMENT '是否已读 0：否 1：是 ',
	`create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
	`update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
	PRIMARY KEY (`id`),
	INDEX `idx_is_read` (`is_read`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='消息表';

CREATE TABLE IF NOT EXISTS `t_theme` (
  `id` INT(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(20) NOT NULL DEFAULT '' COMMENT '名称',
  `cover_url` varchar(255) NOT NULL DEFAULT '' COMMENT '预览图',
  `state` TINYINT(4) NOT NULL DEFAULT '0' COMMENT '状态',
  `sort` TINYINT(4) NOT NULL DEFAULT '0' COMMENT '排序',
  `remark` varchar(120) NOT NULL DEFAULT '' COMMENT '描述',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='主题表';