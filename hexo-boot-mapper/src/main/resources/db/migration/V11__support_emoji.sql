ALTER TABLE `t_nav`
	CHANGE COLUMN `name` `name` VARCHAR(10) NOT NULL DEFAULT '' COMMENT '名称' COLLATE 'utf8mb4_general_ci' AFTER `id`;