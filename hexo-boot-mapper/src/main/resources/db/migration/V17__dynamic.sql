CREATE TABLE `t_dynamic` (
	`id` INT(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
	`content` VARCHAR(1000) NOT NULL DEFAULT '' COMMENT '内容',
	`color` VARCHAR(10) NOT NULL DEFAULT '' COMMENT '颜色',
	`praise_num` INT(11) NOT NULL DEFAULT '0' COMMENT '点赞数',
	`create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
	`update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
	PRIMARY KEY (`id`),
	INDEX `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='动态';

INSERT INTO `hexo-boot`.`t_dynamic` (`content`, `color`) VALUES ('美好的一天', '#1ABC9C');

INSERT INTO `hexo-boot`.`t_nav` (`name`, `link`, `code`, `icon`, `nav_type`, `sort`) VALUES ('动态', '/dynamics/', 'dynamics', 'fa fa-heart-o', '1', '6');

