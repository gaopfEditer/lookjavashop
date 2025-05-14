SET FOREIGN_KEY_CHECKS=0;

CREATE TABLE `la_distribution`  (
  `id` int(11) UNSIGNED NOT NULL AUTO_INCREMENT,
  `user_id` int(11) UNSIGNED NOT NULL COMMENT '用户id',
  `level_id` int(11) UNSIGNED NOT NULL DEFAULT 0 COMMENT '分销会员等级id',
  `is_distribution` tinyint(1) UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否分销会员 0-否 1-是',
  `is_freeze` tinyint(1) UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否冻结 0-否 1-是',
  `remark` varchar(255) NOT NULL DEFAULT '' COMMENT '备注',
  `distribution_time` int(10) NOT NULL DEFAULT 0 COMMENT '成为分销会员时间',
  `is_delete` tinyint(1) UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否删除: 0=否, 1=是',
  `create_time` int(10) NOT NULL DEFAULT 0 COMMENT '创建时间',
  `update_time` int(10) NOT NULL DEFAULT 0 COMMENT '更新时间',
  `delete_time` int(10) NOT NULL DEFAULT 0 COMMENT '删除时间',
  PRIMARY KEY (`id`) USING BTREE
);

CREATE TABLE `la_distribution_apply`  (
  `id` int(11) UNSIGNED NOT NULL AUTO_INCREMENT,
  `user_id` int(11) UNSIGNED NOT NULL COMMENT '用户id',
  `real_name` varchar(12) NOT NULL DEFAULT '' COMMENT '真实姓名',
  `mobile` varchar(30) NOT NULL COMMENT '手机号',
  `province` int(11) UNSIGNED NOT NULL DEFAULT 0 COMMENT '省份',
  `city` int(11) UNSIGNED NOT NULL DEFAULT 0 COMMENT '城市',
  `district` int(11) UNSIGNED NOT NULL DEFAULT 0 COMMENT '县区',
  `reason` varchar(255) NOT NULL DEFAULT '' COMMENT '申请原因',
  `audit_remark` varchar(255) NOT NULL DEFAULT '' COMMENT '审核原因',
  `audit_time` int(10) UNSIGNED NOT NULL DEFAULT 0 COMMENT '审核时间',
  `status` tinyint(1) UNSIGNED NOT NULL DEFAULT 1 COMMENT '状态：1-待审核；2-审核通过；3-审核不通过',
  `is_delete` tinyint(1) UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否删除: 0=否, 1=是',
  `create_time` int(10) UNSIGNED NOT NULL DEFAULT 0 COMMENT '创建时间',
  `update_time` int(10) UNSIGNED NOT NULL COMMENT '更新时间',
  `delete_time` int(10) UNSIGNED NOT NULL DEFAULT 0 COMMENT '删除时间',
  PRIMARY KEY (`id`) USING BTREE
);

CREATE TABLE `la_distribution_config`  (
  `id` int(11) UNSIGNED NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL DEFAULT '' COMMENT '配置项名称',
  `value` text NULL COMMENT '配置项值',
  `remarks` varchar(200) NOT NULL DEFAULT '' COMMENT '分销配置',
  `is_delete` tinyint(1) UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否删除: 0=否, 1=是',
  `create_time` int(10) UNSIGNED NOT NULL DEFAULT 0 COMMENT '创建时间',
  `update_time` int(10) UNSIGNED NOT NULL DEFAULT 0 COMMENT '更新时间',
  `delete_time` int(10) UNSIGNED NOT NULL DEFAULT 0 COMMENT '删除时间',
  PRIMARY KEY (`id`) USING BTREE
);

CREATE TABLE `la_distribution_goods`  (
  `id` int(11) UNSIGNED NOT NULL AUTO_INCREMENT,
  `goods_id` int(11) UNSIGNED NOT NULL COMMENT '商品id',
  `sku_id` int(11) UNSIGNED NOT NULL DEFAULT 0 COMMENT '商品规格id',
  `level_id` int(11) UNSIGNED NOT NULL DEFAULT 0 COMMENT '分销会员等级id',
  `self_ratio` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '自购佣金比例',
  `first_ratio` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '一级佣金比例',
  `second_ratio` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '二级佣金比例',
  `is_distribution` tinyint(1) UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否参与分销 0-不参与 1-参与',
  `rule` tinyint(1) UNSIGNED NOT NULL DEFAULT 1 COMMENT '佣金规则 1-按分销等级比例分佣 2-单独设置分佣比例',
  `is_delete` tinyint(1) UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否删除: 0=否, 1=是',
  `create_time` int(10) UNSIGNED NOT NULL DEFAULT 0 COMMENT '创建时间',
  `update_time` int(10) UNSIGNED NOT NULL DEFAULT 0 COMMENT '更新时间',
  `delete_time` int(10) UNSIGNED NOT NULL DEFAULT 0 COMMENT '删除时间',
  PRIMARY KEY (`id`) USING BTREE
);

CREATE TABLE `la_distribution_level`  (
  `id` int(11) UNSIGNED NOT NULL AUTO_INCREMENT,
  `name` varchar(60) NOT NULL COMMENT '等级名称',
  `weights` tinyint(3) UNSIGNED NOT NULL COMMENT '等级权重',
  `self_ratio` float UNSIGNED NOT NULL DEFAULT 0 COMMENT '自购佣金比例',
  `first_ratio` float UNSIGNED NOT NULL DEFAULT 0 COMMENT '一级佣金比例',
  `second_ratio` float UNSIGNED NOT NULL DEFAULT 0 COMMENT '二级佣金比例',
  `icon` varchar(255) NOT NULL DEFAULT '' COMMENT '图标',
  `image` varchar(255) NOT NULL DEFAULT '' COMMENT '背景图',
  `update_type` tinyint(1) UNSIGNED NOT NULL DEFAULT 1 COMMENT '升级关系，1-OR关系 2-AND关系',
  `is_default` tinyint(1) UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否默认等级 0-否 1-是',
  `remark` varchar(255) NOT NULL DEFAULT '' COMMENT '等级描述',
  `is_delete` tinyint(1) UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否删除: 0=否, 1=是',
  `create_time` int(10) UNSIGNED NOT NULL DEFAULT 0 COMMENT '创建时间',
  `update_time` int(10) UNSIGNED NOT NULL DEFAULT 0 COMMENT '更新时间',
  `delete_time` int(10) UNSIGNED NOT NULL DEFAULT 0 COMMENT '删除时间',
  PRIMARY KEY (`id`) USING BTREE
);

CREATE TABLE `la_distribution_level_update`  (
  `id` int(11) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT ' ',
  `level_id` int(11) UNSIGNED NOT NULL COMMENT '分销会员等级id',
  `scene` int(10) UNSIGNED NOT NULL DEFAULT 0 COMMENT '场景',
  `value` decimal(10, 2) UNSIGNED NOT NULL COMMENT '条件值',
  `is_delete` tinyint(1) UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否删除: 0=否, 1=是',
  `create_time` int(11) UNSIGNED NOT NULL DEFAULT 0 COMMENT '创建时间',
  `update_time` int(11) UNSIGNED NOT NULL DEFAULT 0 COMMENT '更新时间',
  `delete_time` int(11) UNSIGNED NOT NULL DEFAULT 0 COMMENT '删除时间',
  PRIMARY KEY (`id`) USING BTREE
);

CREATE TABLE `la_distribution_order`  (
  `id` int(11) UNSIGNED NOT NULL AUTO_INCREMENT,
  `sn` varchar(255) NOT NULL DEFAULT '' COMMENT '订单号',
  `user_id` int(11) UNSIGNED NOT NULL COMMENT '用户id',
  `order_id` int(11) UNSIGNED NOT NULL COMMENT '子订单id',
  `order_goods_id` int(11) UNSIGNED NOT NULL COMMENT '子订单id',
  `goods_id` int(11) UNSIGNED NOT NULL DEFAULT 0 COMMENT '商品id',
  `sku_id` int(10) UNSIGNED NOT NULL DEFAULT 0 COMMENT '商品SKU',
  `earnings` decimal(10, 2) UNSIGNED NOT NULL DEFAULT 0.00 COMMENT '预估收入',
  `level_id` int(11) UNSIGNED NOT NULL DEFAULT 0 COMMENT '分销会员等级',
  `level` int(11) UNSIGNED NOT NULL DEFAULT 0 COMMENT '分佣层级 1=自购佣金 2=一级分佣 3=二级分佣',
  `ratio` decimal(10, 2) UNSIGNED NOT NULL DEFAULT 0.00 COMMENT '分佣比例',
  `status` tinyint(1) UNSIGNED NOT NULL DEFAULT 1 COMMENT '状态：1-待结算 2-已结算 3-已失效',
  `settle_time` int(10) UNSIGNED NOT NULL DEFAULT 0 COMMENT '结算时间',
  `is_delete` tinyint(1) UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否删除: 0=否, 1=是',
  `create_time` int(10) UNSIGNED NOT NULL DEFAULT 0 COMMENT '创建时间',
  `update_time` int(10) UNSIGNED NOT NULL DEFAULT 0 COMMENT '更新时间',
  `delete_time` int(10) UNSIGNED NOT NULL DEFAULT 0 COMMENT '删除时间',
  PRIMARY KEY (`id`) USING BTREE
);

CREATE TABLE `la_log_earnings`  (
  `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `sn` varchar(32) NOT NULL DEFAULT '' COMMENT '流水号',
  `user_id` int(10) UNSIGNED NOT NULL DEFAULT 0 COMMENT '用户ID',
  `source_id` int(10) UNSIGNED NOT NULL DEFAULT 0 COMMENT '关联ID',
  `source_sn` varchar(100) NOT NULL DEFAULT '' COMMENT '关联单号',
  `change_type` smallint(5) UNSIGNED NOT NULL COMMENT '变动类型',
  `change_amount` decimal(10, 2) UNSIGNED NOT NULL DEFAULT 0.00 COMMENT '变动的数量',
  `left_amount` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '变动后数量',
  `action` tinyint(1) UNSIGNED NOT NULL DEFAULT 0 COMMENT '变动的动作: 1=增加, 2=减少',
  `remark` varchar(255) NOT NULL DEFAULT '' COMMENT '备注信息',
  `extra` text NULL COMMENT '预留字段',
  `create_time` int(10) UNSIGNED NOT NULL DEFAULT 0 COMMENT '创建时间',
  `update_time` int(10) UNSIGNED NOT NULL DEFAULT 0 COMMENT '更新时间',
  `delete_time` int(10) UNSIGNED NOT NULL DEFAULT 0 COMMENT '删除时间',
  PRIMARY KEY (`id`) USING BTREE
);

ALTER TABLE `la_user` ADD COLUMN `earnings` decimal(10, 2) UNSIGNED NOT NULL DEFAULT 0.00 COMMENT '用户佣金' AFTER `money`;

ALTER TABLE `la_user` ADD COLUMN `first_leader` int(11) UNSIGNED NOT NULL DEFAULT 0 COMMENT '第一个上级' AFTER `last_login_time`;

ALTER TABLE `la_user` ADD COLUMN `second_leader` int(11) UNSIGNED NOT NULL DEFAULT 0 COMMENT '第二个上级' AFTER `first_leader`;

ALTER TABLE `la_user` ADD COLUMN `third_leader` int(11) UNSIGNED NOT NULL DEFAULT 0 COMMENT '第三个上级' AFTER `second_leader`;

ALTER TABLE `la_user` ADD COLUMN `ancestor_relation` text NOT NULL COMMENT '关系链' AFTER `third_leader`;

ALTER TABLE `la_user` ADD COLUMN `code` varchar(30) NOT NULL COMMENT '邀请码' AFTER `ancestor_relation`;

ALTER TABLE `la_user` ADD COLUMN `inviter_id` int(11) UNSIGNED NOT NULL DEFAULT 0 COMMENT '邀请人id' AFTER `code`;

CREATE TABLE `la_withdraw_apply`  (
  `id` int(11) UNSIGNED NOT NULL AUTO_INCREMENT,
  `sn` varchar(25) NOT NULL COMMENT '提现单号',
  `batch_no` varchar(25) NOT NULL COMMENT '商家批次单号',
  `user_id` int(11) NOT NULL COMMENT '用户id',
  `real_name` varchar(255) NOT NULL DEFAULT '' COMMENT '真实姓名',
  `account` varchar(32) NOT NULL DEFAULT '' COMMENT '账号',
  `type` tinyint(1) NOT NULL COMMENT '类型：1-钱包余额；2-微信零钱；3-银行卡;4-微信收款码;5-支付宝收款码',
  `money` decimal(10, 2) UNSIGNED NOT NULL COMMENT '提现金额',
  `left_money` decimal(10, 2) NOT NULL COMMENT '用户可得的金额(扣除手续费后)',
  `money_qr_code` varchar(128) NOT NULL DEFAULT '' COMMENT '收款二维码',
  `handling_fee` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '手续费',
  `apply_remark` varchar(255) NOT NULL DEFAULT '' COMMENT '申请备注',
  `status` tinyint(1) NOT NULL COMMENT '状态：1-待提现;2-提现中;3-提现成功;4-提现失败;',
  `pay_desc` text NOT NULL COMMENT '微信零钱支付信息',
  `pay_search_result` text NOT NULL COMMENT '微信零钱支付查询结果',
  `payment_no` varchar(255) NOT NULL DEFAULT '' COMMENT '支付单号',
  `payment_time` int(11) UNSIGNED NOT NULL DEFAULT 0 COMMENT '支付时间',
  `audit_remark` varchar(255) NOT NULL DEFAULT '' COMMENT '审核备注',
  `transfer_voucher` varchar(255) NOT NULL DEFAULT '' COMMENT '转账凭证',
  `transfer_time` int(11) UNSIGNED NOT NULL DEFAULT 0 COMMENT '转账时间',
  `transfer_remark` varchar(255) NOT NULL DEFAULT '' COMMENT '转账备注',
  `bank` varchar(255) NOT NULL DEFAULT '' COMMENT '提现银行',
  `sub_bank` varchar(255) NOT NULL DEFAULT '' COMMENT '提现银行支行',
  `is_delete` int(10) UNSIGNED NOT NULL DEFAULT 0 COMMENT '删除时间',
  `create_time` int(10) UNSIGNED NOT NULL DEFAULT 0 COMMENT '创建时间',
  `update_time` int(10) UNSIGNED NOT NULL DEFAULT 0 COMMENT '更新时间',
  `delete_time` int(10) UNSIGNED NOT NULL DEFAULT 0 COMMENT '删除时间',
  PRIMARY KEY (`id`) USING BTREE
);

INSERT INTO `la_distribution_level`(`id`, `name`, `weights`, `self_ratio`, `first_ratio`, `second_ratio`, `icon`, `image`, `update_type`, `is_default`, `remark`, `is_delete`, `create_time`, `update_time`, `delete_time`) VALUES (1, '默认等级', 1, 0, 0, 0, '', '', 1, 1, '默认等级', 0, 1686910352, 1686910352, 0);

INSERT INTO `la_distribution_config`(`id`, `name`, `value`, `remarks`, `is_delete`, `create_time`, `update_time`, `delete_time`) VALUES (1, 'open', '1', '分销功能: 0=关闭, 1=开启', 0, 0, 0, 0);

INSERT INTO `la_distribution_config`(`id`, `name`, `value`, `remarks`, `is_delete`, `create_time`, `update_time`, `delete_time`) VALUES (2, 'level', '1', '分销层级: 1=一级分销, 2=二级分销', 0, 0, 0, 0);

INSERT INTO `la_distribution_config`(`id`, `name`, `value`, `remarks`, `is_delete`, `create_time`, `update_time`, `delete_time`) VALUES (3, 'isSelfRebate', '1', '自购返佣: 0=关闭, 1=开启', 0, 0, 0, 0);

INSERT INTO `la_distribution_config`(`id`, `name`, `value`, `remarks`, `is_delete`, `create_time`, `update_time`, `delete_time`) VALUES (4, 'isEarningsShow', '0', '商品详情显示佣金: 0=隐藏, 1=显示', 0, 0, 0, 0);

INSERT INTO `la_distribution_config`(`id`, `name`, `value`, `remarks`, `is_delete`, `create_time`, `update_time`, `delete_time`) VALUES (5, 'isEarningsScope', '2', '详情页佣金可见用：1=全部用户, 2=分销商户', 0, 0, 0, 0);

INSERT INTO `la_distribution_config`(`id`, `name`, `value`, `remarks`, `is_delete`, `create_time`, `update_time`, `delete_time`) VALUES (6, 'openCondition', '2', '开通分销商的条件: 1=无条件, 2=申请分销, 3=指定分销', 0, 0, 0, 0);

INSERT INTO `la_distribution_config`(`id`, `name`, `value`, `remarks`, `is_delete`, `create_time`, `update_time`, `delete_time`) VALUES (7, 'protocolShow', '1', '是否显示申请协议: 0=隐藏, 1=显示', 0, 0, 0, 0);

INSERT INTO `la_distribution_config`(`id`, `name`, `value`, `remarks`, `is_delete`, `create_time`, `update_time`, `delete_time`) VALUES (8, 'protocolContent', '11', '申请协议显示内容', 0, 0, 0, 0);

INSERT INTO `la_distribution_config`(`id`, `name`, `value`, `remarks`, `is_delete`, `create_time`, `update_time`, `delete_time`) VALUES (9, 'applyImage', '/api/static/distribution_bg.png', '申请页顶部宣传图', 0, 0, 0, 0);

INSERT INTO `la_distribution_config`(`id`, `name`, `value`, `remarks`, `is_delete`, `create_time`, `update_time`, `delete_time`) VALUES (10, 'settlementTiming', '1', '结算时机类型: 1=订单完成后', 0, 0, 0, 0);

INSERT INTO `la_distribution_config`(`id`, `name`, `value`, `remarks`, `is_delete`, `create_time`, `update_time`, `delete_time`) VALUES (11, 'settlementTime', '7', '结算时机时长: (天数)', 0, 0, 0, 0);

SET FOREIGN_KEY_CHECKS=1;