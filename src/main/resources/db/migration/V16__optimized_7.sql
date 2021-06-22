ALTER TABLE `t_post`
	ADD COLUMN `custom_link` VARCHAR(128) NOT NULL DEFAULT '' COMMENT '自定义链接' AFTER `link`,
	ADD INDEX `idx_custom_link` (`custom_link`);