CREATE TABLE `t_action_log` (
	`id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
	`ip_address` VARCHAR(64) NOT NULL DEFAULT '' COMMENT 'ip 地址',
	`browser` VARCHAR(32) NOT NULL DEFAULT '' COMMENT '浏览器',
	`remark` VARCHAR(255) NOT NULL DEFAULT '' COMMENT '操作描述',
	`action_type` TINYINT NOT NULL DEFAULT '0' COMMENT '操作类型',
	`create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
	`update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
	PRIMARY KEY (`id`),
	INDEX `idx_action_type` (`action_type`),
	INDEX `idx_create_time` (`create_time`)
)
COMMENT='操作日志'
;

CREATE TABLE `t_action_log_detail` (
	`id` INT NOT NULL AUTO_INCREMENT COMMENT '主键',
	`log_id` INT NOT NULL DEFAULT '0' COMMENT '日志 id',
	`method_name` VARCHAR(120) NOT NULL DEFAULT '' COMMENT '方法名',
	`method_param` TEXT NOT NULL COMMENT '方法参数',
	PRIMARY KEY (`id`),
	INDEX `log_id` (`log_id`)
)
COMMENT='操作日志详情'
;
