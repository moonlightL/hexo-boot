ALTER TABLE `t_theme_extend`
	ADD COLUMN `tab` INT NOT NULL DEFAULT '1' COMMENT '归类 1：基础信息 2：页面配置 3：布局设置 4：评论设置 5：资源设置' AFTER `config_option`;
