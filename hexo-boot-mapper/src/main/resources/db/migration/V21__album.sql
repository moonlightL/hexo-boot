CREATE TABLE `t_album` (
	`id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
	`name` VARCHAR(50) NOT NULL DEFAULT '' COMMENT '名称',
	`cover_url` VARCHAR(255) NOT NULL DEFAULT '' COMMENT '封面',
	`visit_type` TINYINT(4) NOT NULL DEFAULT '1' COMMENT '访问类型 1：公开 0：私密',
	`visit_code` VARCHAR(64) NOT NULL DEFAULT '' COMMENT '访问密码',
	`detail_type` TINYINT(4) NOT NULL DEFAULT '1' COMMENT '详情内容 1：图片 2：视频',
	`remark` VARCHAR(255) NOT NULL DEFAULT '' COMMENT '备注',
	`create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
	`update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
	PRIMARY KEY (`id`),
	INDEX `idx_create_time` (`create_time`)
)
COMMENT='专辑'
ENGINE=InnoDB
;

CREATE TABLE `t_album_detail` (
	`id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
	`name` VARCHAR(50) NOT NULL DEFAULT '' COMMENT '名称',
	`url` VARCHAR(255) NOT NULL DEFAULT '' COMMENT '地址',
	`sort` INT(11) NOT NULL DEFAULT '0' COMMENT '排序',
	`remark` VARCHAR(255) NOT NULL DEFAULT '' COMMENT '备注',
	`album_id` INT(11) NOT NULL DEFAULT '0' COMMENT '专辑 id',
	`create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
	`update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
	PRIMARY KEY (`id`),
	INDEX `idx_album_id` (`album_id`)
)
COMMENT='专辑详情'
ENGINE=InnoDB
;

INSERT INTO `hexo-boot`.`t_nav` (`name`, `link`, `code`, `icon`, `nav_type`, `sort`) VALUES ('专辑', '/albums/', 'albums', 'fa fa-diamond', '1', '7');