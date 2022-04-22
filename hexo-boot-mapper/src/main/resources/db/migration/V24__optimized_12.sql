ALTER TABLE `t_attachment`
	CHANGE COLUMN `content_type` `content_type` VARCHAR(255) NOT NULL DEFAULT '' COMMENT '内容类型' AFTER `file_key`;
