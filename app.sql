/*
Navicat MySQL Data Transfer

Source Server         : local
Source Server Version : 50625
Source Host           : localhost:3306
Source Database       : app

Target Server Type    : MYSQL
Target Server Version : 50625
File Encoding         : 65001

Date: 2016-10-07 22:23:30
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for user_info
-- ----------------------------
DROP TABLE IF EXISTS `user_info`;
CREATE TABLE `user_info` (
  `id` varchar(32) NOT NULL,
  `user_name` varchar(32) DEFAULT NULL,
  `password` varchar(64) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of user_info
-- ----------------------------
INSERT INTO `user_info` VALUES ('1', 'admin', '123456');
