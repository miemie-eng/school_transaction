/*
 Navicat Premium Data Transfer

 Source Server         : MySQL
 Source Server Type    : MySQL
 Source Server Version : 80013
 Source Host           : localhost:3306
 Source Schema         : transaction

 Target Server Type    : MySQL
 Target Server Version : 80013
 File Encoding         : 65001

 Date: 10/06/2025 19:45:25
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for chat_message
-- ----------------------------
DROP TABLE IF EXISTS `chat_message`;
CREATE TABLE `chat_message`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `sender_id` int(11) NOT NULL,
  `receiver_id` int(11) NOT NULL,
  `content` text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `send_time` datetime(6) NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(6),
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `sender_id`(`sender_id`) USING BTREE,
  INDEX `receiver_id`(`receiver_id`) USING BTREE,
  CONSTRAINT `receiver_id` FOREIGN KEY (`receiver_id`) REFERENCES `user` (`uid`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `sender_id` FOREIGN KEY (`sender_id`) REFERENCES `user` (`uid`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 46 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of chat_message
-- ----------------------------
INSERT INTO `chat_message` VALUES (2, 20236770, 20236779, '你好', '2025-06-07 10:58:52.695119');
INSERT INTO `chat_message` VALUES (18, 20236779, 20236770, '你也好', '2025-06-07 10:59:01.235560');
INSERT INTO `chat_message` VALUES (19, 20236779, 20234999, '你好', '2025-06-05 23:44:10.413437');
INSERT INTO `chat_message` VALUES (21, 20236770, 20234999, '这商品怎么卖', '2025-06-06 16:59:50.000000');
INSERT INTO `chat_message` VALUES (22, 20236770, 20236779, '好', '2025-06-06 00:07:35.183409');
INSERT INTO `chat_message` VALUES (23, 20236770, 20236779, '真好', '2025-06-06 00:12:42.917422');
INSERT INTO `chat_message` VALUES (26, 20234999, 20236770, '面包便宜卖', '2025-06-06 17:01:26.000000');
INSERT INTO `chat_message` VALUES (44, 20236770, 20236779, '我想买水', '2025-06-08 16:06:52.037023');
INSERT INTO `chat_message` VALUES (45, 20236770, 20236779, '水', '2025-06-08 16:10:25.582557');
INSERT INTO `chat_message` VALUES (46, 20236779, 20236770, 'H', '2025-06-08 16:12:07.528996');

-- ----------------------------
-- Table structure for comments
-- ----------------------------
DROP TABLE IF EXISTS `comments`;
CREATE TABLE `comments`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `question_id` int(11) NULL DEFAULT NULL,
  `user_id` int(11) NULL DEFAULT NULL,
  `content` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `time` datetime NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `question_id`(`question_id`) USING BTREE,
  INDEX `user2_id`(`user_id`) USING BTREE,
  CONSTRAINT `question_id` FOREIGN KEY (`question_id`) REFERENCES `questions` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `user2_id` FOREIGN KEY (`user_id`) REFERENCES `user` (`uid`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 23 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of comments
-- ----------------------------
INSERT INTO `comments` VALUES (5, 2, 20234999, '确实，瓜很甜', '2025-06-02 22:10:22');
INSERT INTO `comments` VALUES (6, 3, 20234999, '教学楼在食堂旁边', '2025-06-02 22:14:03');
INSERT INTO `comments` VALUES (7, 2, 20234999, '我觉得不甜', '2025-06-02 22:14:29');
INSERT INTO `comments` VALUES (8, 4, 20234999, '延迟到6-8了', '2025-06-02 22:49:52');
INSERT INTO `comments` VALUES (9, 5, 20234999, '我觉得炸鸡他家好吃', '2025-06-02 22:59:54');
INSERT INTO `comments` VALUES (11, 5, 20236770, '我觉得中间那家好吃', '2025-06-02 23:02:33');
INSERT INTO `comments` VALUES (13, 7, 20236779, '我也是，想家呜呜呜', '2025-06-04 17:01:31');
INSERT INTO `comments` VALUES (14, 8, 20236770, '没人评论吗', '2025-06-07 11:11:25');
INSERT INTO `comments` VALUES (15, 9, 20236770, '哇苏', '2025-06-07 18:27:15');
INSERT INTO `comments` VALUES (16, 9, 20236779, '神奇', '2025-06-07 18:31:18');
INSERT INTO `comments` VALUES (17, 9, 20236770, '小青蛙', '2025-06-07 18:47:33');
INSERT INTO `comments` VALUES (18, 10, 20236779, '急急急', '2025-06-07 18:50:21');
INSERT INTO `comments` VALUES (19, 10, 20236770, '下午', '2025-06-07 20:47:52');
INSERT INTO `comments` VALUES (20, 11, 20236779, '晴空万里', '2025-06-07 21:00:07');
INSERT INTO `comments` VALUES (21, 11, 20234999, '乌云朗照', '2025-06-07 21:00:32');
INSERT INTO `comments` VALUES (23, 13, 20236770, '你好', '2025-06-08 16:10:10');

-- ----------------------------
-- Table structure for product
-- ----------------------------
DROP TABLE IF EXISTS `product`;
CREATE TABLE `product`  (
  `pname` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `pid` int(11) NOT NULL AUTO_INCREMENT,
  `pdescription` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `pvalue` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `ptype` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `pstate` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `pimage` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `ptime` date NULL DEFAULT NULL,
  `puid` int(11) NULL DEFAULT NULL,
  PRIMARY KEY (`pid`) USING BTREE,
  INDEX `puid`(`puid`) USING BTREE,
  CONSTRAINT `puid` FOREIGN KEY (`puid`) REFERENCES `user` (`uid`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 40 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of product
-- ----------------------------
INSERT INTO `product` VALUES ('小面包', 1, '好吃死', '120元', '食物', '在售', '16429c24-014e-48d0-87eb-bb353cc81a3a_bread2.jpg', '2025-06-08', 20236770);
INSERT INTO `product` VALUES ('茄子', 3, '香，四根便宜卖', '6元', '蔬菜', '未上架', 'eggplant.jpg', NULL, 20238888);
INSERT INTO `product` VALUES ('方便面', 4, '脆，两袋康师傅', '2元', '食物', '未上架', 'noodles.jpg', NULL, 20236770);
INSERT INTO `product` VALUES ('笔记本zi', 5, '好看，两本便宜卖', '6元', '文具', '未上架', 'notebook.jpg', NULL, 20236775);
INSERT INTO `product` VALUES ('小猪', 7, '肥硕', '5元', '动物', '在售', 'pig.jpg', '2025-06-06', 20236779);
INSERT INTO `product` VALUES ('水', 8, '纯净，三瓶', '5元', '饮品', '未上架', 'water.jpg', NULL, 20236770);
INSERT INTO `product` VALUES ('可乐', 9, '酸爽，两瓶', '5元', '饮品', '在售', 'coke.jpg', '2025-06-05', 20236770);
INSERT INTO `product` VALUES ('鲜花', 22, '好看的鲜花', '15元', '物品', '未上架', '19ce9e18-6354-42c7-b409-c94a38c2e361_flower.jpg', NULL, 20236779);
INSERT INTO `product` VALUES ('土豆', 24, '三块土豆', '5元', '蔬菜', '未上架', '5ebf1150-025a-499f-a9f5-0ecd62bab61a_potato.jpg', NULL, 20236779);
INSERT INTO `product` VALUES ('电脑', 25, '大大华为笔记本，货真价实便宜出', '50000元', '用品', '未上架', 'b1d14b87-3cef-4160-9a74-9b842ebd3ca4_computer.jpg', NULL, 20236770);
INSERT INTO `product` VALUES ('小熊', 27, '可爱的小熊', '10元', '玩具', '在售', '7e22acf1-ecb3-4dc9-95b6-78e7a6c90f78_toy.jpg', '2025-06-08', 20236775);
INSERT INTO `product` VALUES ('薯片', 29, '四包薯片便宜卖', '15元', '食物', '在售', '3e344ac5-cf16-4d13-8305-b1e97e1026a5_potato_chips.jpg', '2025-06-08', 20236775);
INSERT INTO `product` VALUES ('花生', 30, '三斤小花生便宜卖', '8元', '食物', '在售', 'ddd9ac5f-954d-4d28-8e9f-b887ad3bd820_peanut.jpg', '2025-06-08', 20236770);

-- ----------------------------
-- Table structure for purchase_history
-- ----------------------------
DROP TABLE IF EXISTS `purchase_history`;
CREATE TABLE `purchase_history`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `pname` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `seller_id` int(11) NULL DEFAULT NULL,
  `buyer_id` int(11) NULL DEFAULT NULL,
  `price` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `purchase_time` datetime NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `seller_id`(`seller_id`) USING BTREE,
  INDEX `buyer_id`(`buyer_id`) USING BTREE,
  CONSTRAINT `buyer_id` FOREIGN KEY (`buyer_id`) REFERENCES `user` (`uid`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `seller_id` FOREIGN KEY (`seller_id`) REFERENCES `user` (`uid`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 37 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of purchase_history
-- ----------------------------
INSERT INTO `purchase_history` VALUES (23, '笔记本', 20236779, 20236770, '6元', '2025-06-05 16:19:04');
INSERT INTO `purchase_history` VALUES (24, '笔', 20236779, 20236770, '7元', '2025-06-05 16:19:30');
INSERT INTO `purchase_history` VALUES (25, '面包', 20234999, 20236770, '20元', '2025-06-06 17:06:00');
INSERT INTO `purchase_history` VALUES (26, '鲜花', 20236770, 20236779, '15元', '2025-06-06 17:06:50');
INSERT INTO `purchase_history` VALUES (27, '土豆', 20236770, 20236779, '5元', '2025-06-06 17:10:33');
INSERT INTO `purchase_history` VALUES (28, '笔记本zi', 20236770, 20236775, '6元', '2025-06-07 10:54:41');
INSERT INTO `purchase_history` VALUES (37, '水', 20236779, 20236770, '5元', '2025-06-08 16:07:21');

-- ----------------------------
-- Table structure for questions
-- ----------------------------
DROP TABLE IF EXISTS `questions`;
CREATE TABLE `questions`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `title` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `content` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `user_id` int(11) NULL DEFAULT NULL,
  `time` datetime NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `user_id`(`user_id`) USING BTREE,
  CONSTRAINT `user_id` FOREIGN KEY (`user_id`) REFERENCES `user` (`uid`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 13 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of questions
-- ----------------------------
INSERT INTO `questions` VALUES (1, '起风了', '校园起风了', 20236779, '2025-06-01 15:44:54');
INSERT INTO `questions` VALUES (2, '瓜', '瓜甜', 20236775, '2025-06-02 12:41:34');
INSERT INTO `questions` VALUES (3, '教学楼怎么走', '如题，求问教学楼咋走', 20234999, '2025-06-02 22:13:03');
INSERT INTO `questions` VALUES (4, 'web作业', '求问web作业啥时候交', 20234999, '2025-06-02 22:48:45');
INSERT INTO `questions` VALUES (5, '小西门', '小西门哪家炸串好吃', 20234999, '2025-06-02 22:51:54');
INSERT INTO `questions` VALUES (6, '放假时间', '求问软院什么时候放假', 20236770, '2025-06-02 23:02:13');
INSERT INTO `questions` VALUES (7, '端午', '端午假期结束了好难过', 20236770, '2025-06-03 23:28:27');
INSERT INTO `questions` VALUES (8, '什么时候放假', '如题，快点放假吧，我真的很想放假', 20236770, '2025-06-06 11:37:17');
INSERT INTO `questions` VALUES (9, '小西门突遇青蛙', '今天在小西门的炸串店旁边看见三只青蛙', 20236770, '2025-06-07 11:41:51');
INSERT INTO `questions` VALUES (10, '求问什么时候吃饭最好', '什么时候吃饭啊啊啊啊啊啊啊', 20236770, '2025-06-07 18:47:59');
INSERT INTO `questions` VALUES (11, '天气咋样啊', '很好嘿嘿哈哈哈哈哈哈哈哈哈哈', 20236770, '2025-06-07 20:48:40');
INSERT INTO `questions` VALUES (13, 'hhhhhhhh', 'hhhhhhhhhhh', 20236770, '2025-06-08 16:10:04');

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `uid` int(255) NOT NULL,
  `uname` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `upwd` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `uphone` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`uid`) USING BTREE,
  INDEX `uname`(`uname`) USING BTREE,
  INDEX `uphone`(`uphone`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (20234999, 'Nancy', '777777', '17655555555');
INSERT INTO `user` VALUES (20236770, 'AAnna', '888888', '17611111112');
INSERT INTO `user` VALUES (20236775, 'nna', '1111112', '14488888888');
INSERT INTO `user` VALUES (20236779, 'ccC', '444444', '17622222222');
INSERT INTO `user` VALUES (20237777, 'hhhh', '111111', '17688888888');
INSERT INTO `user` VALUES (20238888, 'Bob', '222222', '17644444444');
INSERT INTO `user` VALUES (20239999, 'Lisa', '333333', '17633333333');

SET FOREIGN_KEY_CHECKS = 1;
