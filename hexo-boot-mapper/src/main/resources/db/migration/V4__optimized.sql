ALTER TABLE `t_post`
  ADD COLUMN `summary_html` TEXT NOT NULL COMMENT '摘要（html 格式）' AFTER `summary`;