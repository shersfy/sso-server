-- 
-- MySQL Version:	5.7.17 MySQL Community Server (GPL) for Linux (x86_64)
-- Author: 		young
-- Release Date:	2020-03-01
-- SQL_MODE:		STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION
-- CMD: 			mysql -u -p -h --default-character-set=utf8 < ../*.sql

-- ----------------------------
-- Table structure for user_info
-- ----------------------------
DROP TABLE IF EXISTS `user_info`;
CREATE TABLE `user_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `username` varchar(255) NOT NULL COMMENT '用户名称',
  `real_name` varchar(255) NOT NULL COMMENT '真实姓名',
  `password` varchar(255) NOT NULL COMMENT '密码',
  `phone` varchar(255) DEFAULT NULL COMMENT '电话',
  `email` varchar(255) DEFAULT NULL COMMENT '邮箱',
  `note` varchar(255) DEFAULT NULL COMMENT '备注',
  `status` int(1) NOT NULL DEFAULT '-1' COMMENT '状态(-1：未激活，0:正常(默认)，1：已注销)',
  `update_time` timestamp NOT NULL DEFAULT '2020-03-01 00:00:00' ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_time` timestamp NOT NULL DEFAULT '2020-03-01 00:00:00' COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_stat_username` (`username`,`status`) USING BTREE COMMENT 'uk_stat_username',
  UNIQUE KEY `uk_stat_phone` (`phone`,`status`) USING BTREE COMMENT 'uk_stat_phone',
  UNIQUE KEY `uk_stat_email` (`email`,`status`) COMMENT 'uk_stat_email'
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8 COMMENT='用户信息';

-- ----------------------------
-- Records of user_info
-- ssoadmin/123456
-- ----------------------------
INSERT INTO `user_info` VALUES ('1', 'ssoadmin', '埃斯阿达米', 'e10adc3949ba59abbe56e057f20f883e', '13512345678', 'shersfy@163.com', null, '0', '2020-03-26 13:49:23', '2020-03-01 00:00:00');

