CREATE TABLE `t_nav` (
	`id` INT(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
	`name` VARCHAR(10) NOT NULL DEFAULT '' COMMENT '名称',
	`link` VARCHAR(50) NOT NULL DEFAULT '' COMMENT '链接',
	`code` VARCHAR(50) NOT NULL DEFAULT '' COMMENT '编码',
	`icon` VARCHAR(50) NOT NULL DEFAULT '' COMMENT '图标',
	`nav_type` TINYINT(4) NOT NULL DEFAULT '2' COMMENT '类型 1：默认 2：自定义',
	`state` TINYINT(4) NOT NULL DEFAULT '1' COMMENT '可用状态',
	`sort` TINYINT(4) NOT NULL DEFAULT '0' COMMENT '排序',
	`content` TEXT NULL COMMENT '内容',
	`create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
	`update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
	PRIMARY KEY (`id`),
	UNIQUE INDEX `idx_link` (`link`)
)
ENGINE=InnoDB
;


INSERT INTO `hexo-boot`.`t_nav` (`name`, `link`, `code`, `icon`, `nav_type`, `sort`) VALUES ('首页', '/', 'index', 'fa fa-home','1', 1);
INSERT INTO `hexo-boot`.`t_nav` (`name`, `link`, `code`, `icon`, `nav_type`, `sort`) VALUES ('归档', '/archives/', 'archives', 'fa fa-archive', '1', 2);
INSERT INTO `hexo-boot`.`t_nav` (`name`, `link`, `code`, `icon`, `nav_type`, `sort`) VALUES ('分类', '/categories/', 'categories', 'fa fa-folder', '1', 3);
INSERT INTO `hexo-boot`.`t_nav` (`name`, `link`, `code`, `icon`, `nav_type`, `sort`) VALUES ('友链', '/friendLinks/', 'friendLinks', 'fa fa-anchor', '1', 4);
INSERT INTO `hexo-boot`.`t_nav` (`name`, `link`, `code`, `icon`, `nav_type`, `sort`) VALUES ('关于', '/about/', 'about', 'fa fa-user', '1', 5);
