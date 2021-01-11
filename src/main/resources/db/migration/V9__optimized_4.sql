ALTER TABLE `t_config`
	CHANGE COLUMN `config_value` `config_value` TEXT NULL COMMENT '参数值' AFTER `config_key`;

INSERT INTO `hexo-boot`.`t_config` (`config_key`, `config_value`, `remark`) VALUES ('we_chat_official_account', "", '微信公众号');
