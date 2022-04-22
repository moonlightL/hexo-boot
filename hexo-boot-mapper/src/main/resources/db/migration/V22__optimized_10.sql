ALTER TABLE `t_album_detail`
	ADD COLUMN `cover_url` VARCHAR(255) NOT NULL DEFAULT '' COMMENT '封面地址' AFTER `url`;
