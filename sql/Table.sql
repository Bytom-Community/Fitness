/*
Navicat MySQL Data Transfer

Date: 2019-01-08 20:09:18
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for ad_record
-- ----------------------------
DROP TABLE IF EXISTS `ad_record`;
CREATE TABLE `ad_record` (
  `id` bigint(16) unsigned NOT NULL AUTO_INCREMENT,
  `ad_id` bigint(16) DEFAULT NULL,
  `open_id` varchar(64) DEFAULT NULL,
  `target` bigint(16) DEFAULT NULL,
  `enter_power` bigint(16) DEFAULT NULL,
  `current_power` bigint(16) DEFAULT NULL,
  `finished` varchar(16) DEFAULT NULL,
  `win_token` decimal(19,8) DEFAULT NULL COMMENT '成功分配的数量',
  `create_time` bigint(16) DEFAULT NULL,
  `update_time` bigint(16) DEFAULT NULL,
  `remarks` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `ad_id` (`ad_id`,`open_id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for ad_theme
-- ----------------------------
DROP TABLE IF EXISTS `ad_theme`;
CREATE TABLE `ad_theme` (
  `id` bigint(16) unsigned NOT NULL AUTO_INCREMENT,
  `cover` varchar(255) DEFAULT NULL,
  `title` varchar(64) DEFAULT NULL,
  `content` longtext,
  `tx_id` varchar(255) DEFAULT NULL COMMENT '合约哈希',
  `power_target` bigint(16) DEFAULT NULL COMMENT '筹集能量数',
  `reward_token` decimal(19,8) DEFAULT NULL COMMENT '奖励token数',
  `contract_key` varchar(255) DEFAULT NULL COMMENT '合约密钥',
  `contract_key_hash` varchar(255) DEFAULT NULL COMMENT '合约密钥哈希',
  `lock_program` varchar(255) DEFAULT NULL COMMENT '合约程序',
  `unspent_id` varchar(255) DEFAULT NULL COMMENT '未花费输出id',
  `unlock_hash` varchar(255) DEFAULT NULL COMMENT '解锁合约哈希',
  `unlock_value` bigint(16) DEFAULT NULL COMMENT '解锁合约的值',
  `unlock_value_hex` varchar(255) DEFAULT NULL COMMENT '解锁合约值的16进制',
  `status` int(1) DEFAULT NULL COMMENT '状态 0 未开始；1 进行中；2 已结束',
  `enter_power` bigint(16) DEFAULT NULL COMMENT '报名条件 能量数',
  `start_time` bigint(16) DEFAULT NULL,
  `end_time` bigint(16) DEFAULT NULL,
  `create_time` bigint(16) DEFAULT NULL,
  `update_time` bigint(16) DEFAULT NULL,
  `remarks` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for btm_account
-- ----------------------------
DROP TABLE IF EXISTS `btm_account`;
CREATE TABLE `btm_account` (
  `id` bigint(16) unsigned NOT NULL AUTO_INCREMENT,
  `account_id` varchar(64) DEFAULT NULL,
  `open_id` varchar(64) DEFAULT NULL,
  `key_secret` varchar(256) DEFAULT NULL COMMENT '密钥密码',
  `key_alias` varchar(256) DEFAULT NULL COMMENT '密钥别名',
  `key_public` varchar(1024) DEFAULT NULL COMMENT '公钥',
  `account_alias` varchar(256) DEFAULT NULL COMMENT '账户名',
  `receiver_address` varchar(256) DEFAULT NULL COMMENT '地址',
  `control_program` varchar(256) DEFAULT NULL COMMENT '合约接收码',
  `create_time` bigint(20) DEFAULT NULL,
  `update_time` bigint(20) DEFAULT NULL,
  `remarks` varchar(1024) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `open_id` (`open_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for charity
-- ----------------------------
DROP TABLE IF EXISTS `charity`;
CREATE TABLE `charity` (
  `id` bigint(16) unsigned NOT NULL AUTO_INCREMENT,
  `cover` varchar(255) DEFAULT NULL COMMENT '封面',
  `title` varchar(64) DEFAULT NULL,
  `summary` varchar(64) DEFAULT NULL COMMENT '简介',
  `owner` varchar(64) DEFAULT NULL,
  `content` varchar(255) DEFAULT NULL,
  `status` int(1) DEFAULT NULL COMMENT '状态 0 未开始；1 进行中；2 已结束',
  `charity_type` int(1) DEFAULT NULL COMMENT '捐赠类型 1 能量捐；2 直接捐赠',
  `owner_address` varchar(64) DEFAULT NULL COMMENT '接收捐赠的地址，只有type为2时生效',
  `create_time` bigint(16) DEFAULT NULL,
  `update_time` bigint(16) DEFAULT NULL,
  `remarks` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for charity_record
-- ----------------------------
DROP TABLE IF EXISTS `charity_record`;
CREATE TABLE `charity_record` (
  `id` bigint(16) unsigned NOT NULL AUTO_INCREMENT,
  `charity_id` bigint(16) DEFAULT NULL,
  `open_id` varchar(64) DEFAULT NULL,
  `tx_id` varchar(255) DEFAULT NULL COMMENT '捐赠哈希',
  `power_number` bigint(16) DEFAULT NULL COMMENT '捐赠能量数',
  `memo` longtext,
  `charity_token` decimal(16,8) DEFAULT NULL COMMENT '兑换的token通行',
  `prove_hash` varchar(255) DEFAULT NULL,
  `donate_amount` decimal(16,8) DEFAULT NULL COMMENT '捐赠数目',
  `create_time` bigint(16) DEFAULT NULL,
  `update_time` bigint(16) DEFAULT NULL,
  `remarks` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `charity_id` (`charity_id`),
  KEY `open_id` (`open_id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for contract_record
-- ----------------------------
DROP TABLE IF EXISTS `contract_record`;
CREATE TABLE `contract_record` (
  `id` bigint(16) unsigned NOT NULL AUTO_INCREMENT,
  `open_id` varchar(64) DEFAULT NULL,
  `target_id` bigint(16) DEFAULT NULL COMMENT '打卡id',
  `program` varchar(255) DEFAULT NULL COMMENT '合约program',
  `tx_id` varchar(255) DEFAULT NULL COMMENT '交易hash',
  `unspent_id` varchar(255) DEFAULT NULL COMMENT '合约未花费id',
  `value` int(8) DEFAULT NULL COMMENT '部署合约时的参数',
  `amount` bigint(16) DEFAULT NULL COMMENT '合约中的金额，单位NEU',
  `is_unlock` int(1) DEFAULT NULL COMMENT '是否已解锁，0 未解锁；1 已解锁',
  `unlock_value` varchar(16) DEFAULT NULL COMMENT '解锁合约的值，16进制',
  `create_time` bigint(16) DEFAULT NULL,
  `update_time` bigint(16) DEFAULT NULL,
  `remarks` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `target_id` (`target_id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for fitness_user
-- ----------------------------
DROP TABLE IF EXISTS `fitness_user`;
CREATE TABLE `fitness_user` (
  `id` bigint(16) unsigned NOT NULL AUTO_INCREMENT,
  `nick_name` varchar(64) DEFAULT NULL,
  `gender` varchar(16) DEFAULT NULL,
  `country` varchar(64) DEFAULT NULL,
  `province` varchar(64) DEFAULT NULL,
  `city` varchar(64) DEFAULT NULL,
  `language` varchar(64) DEFAULT NULL,
  `avatar_url` varchar(1024) DEFAULT NULL,
  `open_id` varchar(64) DEFAULT NULL,
  `btm_account` varchar(64) DEFAULT NULL,
  `private_key` varchar(1024) DEFAULT NULL,
  `create_time` bigint(64) DEFAULT NULL,
  `update_time` bigint(64) DEFAULT NULL,
  `remarks` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `open_id` (`open_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for run_record
-- ----------------------------
DROP TABLE IF EXISTS `run_record`;
CREATE TABLE `run_record` (
  `id` bigint(16) unsigned NOT NULL AUTO_INCREMENT,
  `run_step` bigint(16) DEFAULT NULL COMMENT '步数',
  `open_id` varchar(64) DEFAULT NULL,
  `create_time` bigint(16) DEFAULT NULL,
  `update_time` bigint(16) DEFAULT NULL,
  `remarks` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for target
-- ----------------------------
DROP TABLE IF EXISTS `target`;
CREATE TABLE `target` (
  `id` bigint(16) unsigned NOT NULL AUTO_INCREMENT,
  `open_id` varchar(64) DEFAULT NULL,
  `target` bigint(16) DEFAULT NULL COMMENT '今天目标',
  `bet_amount` varchar(16) DEFAULT NULL COMMENT '约定金额',
  `payout` varchar(16) DEFAULT NULL COMMENT '兑现约定奖励率',
  `win_amount` varchar(16) DEFAULT NULL COMMENT '赢取的奖励金',
  `is_finish` varchar(8) DEFAULT NULL COMMENT '是否兑现目标,true/false',
  `finish_way` int(1) DEFAULT NULL COMMENT '结算方式，0 未结算；1 主动兑现；2 系统结算',
  `win_run` bigint(16) DEFAULT NULL COMMENT '实现目标时的步数',
  `create_time` bigint(16) DEFAULT NULL,
  `update_time` bigint(16) DEFAULT NULL,
  `remarks` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `open_id` (`open_id`,`create_time`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for unlock_power
-- ----------------------------
DROP TABLE IF EXISTS `unlock_power`;
CREATE TABLE `unlock_power` (
  `id` bigint(16) unsigned NOT NULL AUTO_INCREMENT,
  `open_id` varchar(128) DEFAULT NULL,
  `target_id` bigint(16) DEFAULT NULL,
  `contract_record_id` bigint(16) DEFAULT NULL,
  `unlock_tx_id` varchar(128) DEFAULT NULL COMMENT '解锁合约txid',
  `power` bigint(16) DEFAULT NULL COMMENT '获得的能量',
  `spent_power` bigint(16) DEFAULT '0' COMMENT '已花费能量',
  `unspent_power` bigint(16) DEFAULT '0' COMMENT '未花费能量',
  `is_owner` int(1) DEFAULT NULL COMMENT '是否拥有权，0 否；1 是',
  `create_time` bigint(16) DEFAULT NULL,
  `update_time` bigint(16) DEFAULT NULL,
  `create_day` bigint(16) DEFAULT NULL COMMENT '创建当天0点时间',
  `remarks` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `open_id` (`open_id`,`is_owner`,`target_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for vote_options
-- ----------------------------
DROP TABLE IF EXISTS `vote_options`;
CREATE TABLE `vote_options` (
  `id` bigint(16) unsigned NOT NULL AUTO_INCREMENT,
  `vote_id` bigint(16) DEFAULT NULL,
  `image` varchar(255) DEFAULT NULL,
  `content` varchar(255) DEFAULT NULL,
  `show_type` int(1) DEFAULT NULL COMMENT '0 纯文字；1 纯图片；2 文字+图片',
  `create_time` bigint(16) DEFAULT NULL,
  `update_time` bigint(16) DEFAULT NULL,
  `remarks` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `vote_id` (`vote_id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for vote_record
-- ----------------------------
DROP TABLE IF EXISTS `vote_record`;
CREATE TABLE `vote_record` (
  `id` bigint(16) unsigned NOT NULL AUTO_INCREMENT,
  `theme_id` bigint(16) DEFAULT NULL,
  `option_id` bigint(16) DEFAULT NULL,
  `open_id` varchar(64) DEFAULT NULL,
  `power_number` bigint(16) DEFAULT NULL COMMENT '能量点数',
  `power_day` int(9) DEFAULT NULL COMMENT '持有能量的天数',
  `power_total` bigint(16) DEFAULT NULL COMMENT '总能量',
  `power_create_day` bigint(16) DEFAULT NULL COMMENT '能量的创建日期',
  `create_time` bigint(16) DEFAULT NULL,
  `update_time` bigint(16) DEFAULT NULL,
  `remarks` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `theme_id` (`theme_id`,`open_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for vote_theme
-- ----------------------------
DROP TABLE IF EXISTS `vote_theme`;
CREATE TABLE `vote_theme` (
  `id` bigint(16) unsigned NOT NULL AUTO_INCREMENT,
  `open_id` varchar(64) DEFAULT NULL,
  `title` varchar(255) DEFAULT NULL COMMENT '标题',
  `show_cover` varchar(255) DEFAULT NULL COMMENT '封面',
  `summary` varchar(255) DEFAULT NULL COMMENT '简介',
  `push_name` varchar(128) DEFAULT NULL COMMENT '发布人昵称',
  `status` int(1) DEFAULT NULL COMMENT '状态 0未开启；1 进行中；2 已结束',
  `over_time` bigint(16) DEFAULT NULL COMMENT '结束时间',
  `vote_result` bigint(16) DEFAULT NULL COMMENT 'options id',
  `create_time` bigint(16) DEFAULT NULL,
  `update_time` bigint(16) DEFAULT NULL,
  `remarks` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `id` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4;
