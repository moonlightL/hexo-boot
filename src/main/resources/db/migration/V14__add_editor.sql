INSERT INTO `hexo-boot`.`t_config` (`config_key`, `config_value`, `remark`) VALUES ('editor_type', 'markdown', '编辑器类型');

ALTER TABLE `t_post`
	ADD COLUMN `content_html` MEDIUMTEXT NOT NULL COMMENT '内容（html 格式）' AFTER `content`;

ALTER TABLE `t_post`
	CHANGE COLUMN `content` `content` MEDIUMTEXT NOT NULL COMMENT '内容（原内容）' AFTER `summary_html`;


