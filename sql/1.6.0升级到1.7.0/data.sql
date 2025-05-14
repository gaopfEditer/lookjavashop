-- MySQL dump 10.13  Distrib 8.0.33, for macos12.6 (x86_64)
--
-- Host: office-pro1-bt.likeshop.cn    Database: java_b2c
-- ------------------------------------------------------
-- Server version	5.7.43-log

-- 1.7.0

INSERT INTO `la_crontab` (`name`, `types`, `command`, `rules`, `remark`, `error`, `status`, `strategy`, `concurrent`, `is_delete`, `start_time`, `end_time`, `task_time`, `create_time`, `update_time`, `delete_time`) VALUES
(	'关闭超时未停止的秒杀活动',	'default',	'goodsCloseIsIsActivityJob.handle()',	'0/5 * * * * ?',	'',	'',	1,	1,	0,	0,	1719113385,	1719113385,	0,	1719113024,	1719113279,	0);

ALTER TABLE `la_album`
ADD `ip` varchar(30) COLLATE 'utf8mb4_general_ci' NOT NULL DEFAULT '' COMMENT 'IP';