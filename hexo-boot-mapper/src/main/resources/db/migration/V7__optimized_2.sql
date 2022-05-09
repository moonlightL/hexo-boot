ALTER TABLE `t_guest_book`
	ADD COLUMN `avatar` VARCHAR(255) NOT NULL DEFAULT '' COMMENT '留言头像' AFTER `nickname`,
	ADD COLUMN `is_blogger` TINYINT NOT NULL DEFAULT '0' COMMENT '是否为博主' AFTER `avatar`;

UPDATE t_guest_book b INNER JOIN (SELECT id,avatar FROM t_user) u ON b.user_id = u.id SET b.avatar = u.avatar;
UPDATE t_guest_book b INNER JOIN (SELECT id FROM t_user WHERE role = 1) u ON b.user_id = u.id SET b.is_blogger = 1;

ALTER TABLE `t_post_comment`
	ADD COLUMN `avatar` VARCHAR(255) NOT NULL DEFAULT '' COMMENT '评论头像' AFTER `nickname`,
	ADD COLUMN `is_blogger` TINYINT NOT NULL DEFAULT '0' COMMENT '是否为博主' AFTER `avatar`;

UPDATE t_post_comment c INNER JOIN (SELECT id,avatar FROM t_user) u ON c.user_id = u.id SET c.avatar = u.avatar;
UPDATE t_post_comment c INNER JOIN (SELECT id FROM t_user WHERE role = 1) u ON c.user_id = u.id SET c.is_blogger = 1;