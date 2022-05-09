ALTER TABLE `t_friend_link`
	ADD COLUMN `logo` VARCHAR(255) NOT NULL DEFAULT '' COMMENT 'logo' AFTER `title`;

INSERT INTO `hexo-boot`.`t_config` (`config_key`, `remark`) VALUES ('twikoo_env_id', 'twikoo环境 id');

ALTER TABLE `t_nav`
	ADD COLUMN `parent_id` INT NOT NULL DEFAULT '0' COMMENT '父级 id' AFTER `content`,
	ADD INDEX `idx_parent_id` (`parent_id`);

ALTER TABLE `t_nav`
	ADD COLUMN `cover` VARCHAR(255) NOT NULL DEFAULT '' COMMENT '封面' AFTER `parent_id`;

INSERT INTO `hexo-boot`.`t_nav` (`name`, `link`, `code`, `icon`, `nav_type`, `sort`, `parent_id`) VALUES ('标签', '/tags/', 'tags', 'fa fa-tags', '1', '2', '0');

