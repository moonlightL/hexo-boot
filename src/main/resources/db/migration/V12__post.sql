ALTER TABLE `t_post`
	ADD COLUMN `cover_type` TINYINT NOT NULL DEFAULT '4' COMMENT '封面布局 1：全封面 2：无封面 3：左侧 4：顶部' AFTER `cover_url`;

UPDATE t_post SET cover_type = 1 WHERE is_top = 1;

ALTER TABLE `t_nav`
	ADD COLUMN `read_num` INT NOT NULL DEFAULT '1' COMMENT '浏览数' AFTER `cover`;

