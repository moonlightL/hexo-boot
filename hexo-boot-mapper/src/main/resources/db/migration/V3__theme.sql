CREATE TABLE `t_theme_extend` (
	`id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
	`theme_id` INT(10) UNSIGNED NOT NULL DEFAULT '0' COMMENT '主题 id',
	`config_name` VARCHAR(50) NOT NULL DEFAULT '' COMMENT '配置名称',
	`config_value` VARCHAR(255) NOT NULL DEFAULT '' COMMENT '配置值',
	`config_label` VARCHAR(10) NOT NULL DEFAULT '' COMMENT '配置标签',
	`config_type` VARCHAR(10) NOT NULL DEFAULT '' COMMENT '配置类型（如：input，select）',
	`config_option` VARCHAR(255) NOT NULL DEFAULT '' COMMENT '配置选项',
	`create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
	`update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
	PRIMARY KEY (`id`),
	UNIQUE INDEX `idx_theme_id` (`theme_id`, `config_name`)
)
COMMENT='主题配置扩展表'
ENGINE=InnoDB
;
