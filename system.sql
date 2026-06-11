/*
 Navicat Premium Dump SQL

 Source Server         : ikun
 Source Server Type    : MySQL
 Source Server Version : 80026 (8.0.26)
 Source Host           : localhost:3306
 Source Schema         : system

 Target Server Type    : MySQL
 Target Server Version : 80026 (8.0.26)
 File Encoding         : 65001

 Date: 11/06/2026 11:05:02
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for admin
-- ----------------------------
DROP TABLE IF EXISTS `admin`;
CREATE TABLE `admin`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `username` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '账号',
  `password` varchar(225) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '密码',
  `salt` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '盐值',
  `name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '姓名',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '头像',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '手机号码',
  `email` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '邮箱',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `username`(`username` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '管理员表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of admin
-- ----------------------------
INSERT INTO `admin` VALUES (1, '123', 'kcP8/OfsCNgBGJ+la8X4Z5p0mzFvvQsWotNEoCACml8=', 'a1b2c3d4e5', '王一', '', '124412452', '2144442@qq.com');
INSERT INTO `admin` VALUES (2, 'admin', 'kcP8/OfsCNgBGJ+la8X4Z5p0mzFvvQsWotNEoCACml8=', 'a1b2c3d4e5', '王二', '', '18118189513', '214828@qq.com');
INSERT INTO `admin` VALUES (3, '12345', 'jZae727K08KaOmKSgOaGzww', 'a1b2c3d4e5', '王三', '', '1244124', '214444@qq.com');
INSERT INTO `admin` VALUES (4, '1234', '+la8X4Z5p0mzFvvQsWotNEoCACml8=', 'p6q7r8s9t0', '王四', '', '18218312978', '2141232623@qq.com');
INSERT INTO `admin` VALUES (5, '1233', 'g6d5e4f3a2b1c0d9e8f7a6b5c4d3e2f1a0b9c8d7e6f5a4b3c2d1e0f9a8b7c6d5', 'u1v2w3x4y5', '王五', '', '', '');

-- ----------------------------
-- Table structure for course
-- ----------------------------
DROP TABLE IF EXISTS `course`;
CREATE TABLE `course`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `course_id` varchar(12) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '课程编号',
  `course_name` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '课程名',
  `hours` int NULL DEFAULT NULL COMMENT '学时',
  `credits` decimal(5, 1) NOT NULL COMMENT '学分',
  `course_type` int NULL DEFAULT NULL COMMENT '课程类型(0/1/2 理论课/实践课/理-实)',
  `dept_id` int NULL DEFAULT NULL COMMENT '学院编号',
  `status` int NULL DEFAULT NULL COMMENT '状态（0/1 禁用/启用）',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 30 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of course
-- ----------------------------
INSERT INTO `course` VALUES (1, '22', 'java编程', 32, 2.0, 0, 121, 0);
INSERT INTO `course` VALUES (2, '34', '形式与政策1', 8, 0.5, 0, 121, 0);
INSERT INTO `course` VALUES (8, '456', '机器学习', 32, 2.0, 1, 123, 0);
INSERT INTO `course` VALUES (9, '789', 'XML技术', 54, 3.0, 0, 121, 1);
INSERT INTO `course` VALUES (14, '12', '线性代数', 30, 2.0, 0, 121, 1);
INSERT INTO `course` VALUES (15, '23', '劳动教育', 16, 1.0, 0, 141, 1);
INSERT INTO `course` VALUES (16, '123', 'Web前端技术', 48, 3.0, 0, 121, 1);
INSERT INTO `course` VALUES (17, '2', '军事理论', 32, 2.0, 0, 141, 1);
INSERT INTO `course` VALUES (18, '23', '编程设计与开发', 200, 3.0, 2, 121, 1);
INSERT INTO `course` VALUES (19, '12', '大学英语', 12, 1.0, 0, 151, 1);
INSERT INTO `course` VALUES (21, '234', '数据结构与算法分析', 48, 3.0, 0, 121, 1);
INSERT INTO `course` VALUES (24, '1', '数据通信与网络', 64, 4.0, 0, 121, 1);
INSERT INTO `course` VALUES (28, '1123', '计算机网络技术', 123, 1.0, 0, 121, 1);
INSERT INTO `course` VALUES (29, '12345', '大学英语2', 36, 2.0, 0, 151, 1);

-- ----------------------------
-- Table structure for course_section
-- ----------------------------
DROP TABLE IF EXISTS `course_section`;
CREATE TABLE `course_section`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `course_id` int NULL DEFAULT NULL COMMENT '课程ID',
  `teacher_id` int NULL DEFAULT NULL COMMENT '授课教师ID',
  `credits_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '学分类型',
  `place` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '上课地点',
  `number` int NULL DEFAULT 50 COMMENT '最大选课人数',
  `current_students` int NULL DEFAULT 0 COMMENT '当前选课人数',
  `status` int NULL DEFAULT 0 COMMENT '状态(0未开始，1进行中， 2已结束，3特殊状态)',
  `semester` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '学期(如：2024-2025学年第一学期)',
  `course_time` bit(17) NULL DEFAULT NULL COMMENT '二进制：17位，前5位表星期，后12位表课次',
  `course_total` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '课程总数',
  `course_number` bit(20) NULL DEFAULT NULL COMMENT '二进制：20位表周次课程',
  `created_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 49 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of course_section
-- ----------------------------
INSERT INTO `course_section` VALUES (7, 1, NULL, 'PUBLIC', NULL, 58, 0, 3, NULL, NULL, NULL, NULL, '2025-11-19 22:28:41');
INSERT INTO `course_section` VALUES (8, 16, NULL, 'PUBLIC', NULL, 50, 0, 3, NULL, NULL, NULL, NULL, '2025-11-19 22:28:59');
INSERT INTO `course_section` VALUES (9, 19, NULL, 'FOREIGN_LANGUAGE', NULL, 50, 0, 3, NULL, NULL, NULL, NULL, '2025-11-19 22:30:28');
INSERT INTO `course_section` VALUES (10, 28, NULL, 'PUBLIC', NULL, 50, 0, 3, NULL, NULL, NULL, NULL, '2025-11-19 22:30:45');
INSERT INTO `course_section` VALUES (15, 8, NULL, 'PUBLIC', NULL, 50, 0, 3, NULL, NULL, NULL, NULL, '2025-12-03 17:11:57');
INSERT INTO `course_section` VALUES (22, 16, 1, 'PUBLIC', '线下', 50, 0, 2, '2025-2026学年第一学期', b'00000011000000100', '14', b'00000011111111111111', '2025-12-24 23:08:24');
INSERT INTO `course_section` VALUES (23, 19, 4, 'FOREIGN_LANGUAGE', '线上', 50, 0, 1, '2025-2026学年第二学期', b'00001100000010000', '12', b'00000000111111111111', '2026-01-07 16:45:33');
INSERT INTO `course_section` VALUES (24, 28, 1, 'ART', '线下', 50, 1, 2, '2025-2026学年第二学期', b'00000001000001000', '14', b'00000011111111111111', '2026-01-08 15:30:02');
INSERT INTO `course_section` VALUES (28, 24, NULL, 'PUBLIC', NULL, 50, 0, 3, NULL, NULL, NULL, NULL, '2026-01-08 15:36:23');
INSERT INTO `course_section` VALUES (29, 18, NULL, 'PUBLIC', NULL, 50, 0, 3, NULL, NULL, NULL, NULL, '2026-01-08 15:36:47');
INSERT INTO `course_section` VALUES (35, 16, 1, 'PUBLIC', NULL, 50, 1, 2, '2025-2026学年第二学期', b'00000000110000010', '12', b'00000000111111111111', '2026-01-12 18:18:02');
INSERT INTO `course_section` VALUES (36, 29, NULL, 'FOREIGN_LANGUAGE', NULL, 50, 0, 3, NULL, NULL, NULL, NULL, '2026-01-13 10:28:46');
INSERT INTO `course_section` VALUES (37, 29, 4, 'FOREIGN_LANGUAGE', '线上', 50, 0, 0, '2025-2026学年第二学期', b'00000011000000010', '14', b'00000011111111111111', '2026-01-13 10:29:25');
INSERT INTO `course_section` VALUES (38, 1, 1, 'PUBLIC', NULL, 58, 0, 2, '2025-2026学年第二学期', b'00110000000010000', '12', b'00000000111111111111', '2026-01-14 17:22:02');
INSERT INTO `course_section` VALUES (39, 19, 4, 'FOREIGN_LANGUAGE', '线下', 50, 50, 1, '2025-2026学年第二学期', b'00000000110000010', '12', b'00000000111111111111', '2026-02-06 17:12:07');
INSERT INTO `course_section` VALUES (41, 19, 4, 'FOREIGN_LANGUAGE', '线上', 50, 1, 0, '2025-2026学年第二学期', b'00000011000001000', '15', b'00000111111111111111', '2026-02-06 17:19:40');
INSERT INTO `course_section` VALUES (42, 19, 4, 'FOREIGN_LANGUAGE', '线下', 50, 0, 1, '2025-2026学年第二学期', b'00001100000000001', '8', b'00000000000001010101', '2026-02-25 15:30:54');
INSERT INTO `course_section` VALUES (43, 1, 3, 'PUBLIC', '线上', 58, 0, 1, '2025-2026学年第二学期', b'00000110000001000', '13', b'00000001111111111111', '2026-03-21 15:11:51');
INSERT INTO `course_section` VALUES (44, 18, 3, 'PUBLIC', '线上', 50, 0, 1, '2025-2026学年第二学期', b'00000000110001000', '13', b'00000001111111111111', '2026-03-21 15:20:14');
INSERT INTO `course_section` VALUES (45, 16, 3, 'PUBLIC', '线上', 50, 0, 1, '2025-2026学年第二学期', b'00000000110000010', '17', b'00011111111111111111', '2026-03-21 15:20:52');
INSERT INTO `course_section` VALUES (46, 18, 1, 'PUBLIC', '线上', 50, 0, 2, '2025-2026学年第二学期', b'00000011000000100', '8', b'00000000000001010101', '2026-03-21 23:07:41');
INSERT INTO `course_section` VALUES (47, 24, 1, 'PUBLIC', '线上', 50, 0, 0, '2025-2026学年第二学期', b'01100000000001000', '12', b'00000000111111111111', '2026-04-23 23:42:21');
INSERT INTO `course_section` VALUES (48, 1, 1, 'PUBLIC', NULL, 58, 0, 0, '2025-2026学年第二学期', b'00001100000001000', '12', b'00000000111111111111', '2026-04-24 12:51:01');

-- ----------------------------
-- Table structure for credit_line
-- ----------------------------
DROP TABLE IF EXISTS `credit_line`;
CREATE TABLE `credit_line`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `dept_id` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `grade` varchar(6) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '// 年级（入学时间，判断第几节）',
  `sort` int NULL DEFAULT NULL COMMENT '// 排序(1.大一 2.大二 3.大三 4.大四)',
  `public_credit` decimal(3, 1) NULL DEFAULT NULL COMMENT '// 公共选修课学分要求',
  `foreign_language_credit` decimal(3, 1) NULL DEFAULT NULL COMMENT '// 外语选修课学分要求',
  `sport_credit` decimal(3, 1) NULL DEFAULT NULL COMMENT '体育选修课学分要求',
  `art_credit` decimal(3, 1) NULL DEFAULT NULL COMMENT '// 艺术选修课学分要求',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 51 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of credit_line
-- ----------------------------
INSERT INTO `credit_line` VALUES (1, '121', '2022', 0, 9.0, 6.0, 6.0, 4.0);
INSERT INTO `credit_line` VALUES (2, '121', '2022', 1, 2.0, 2.0, 2.0, 0.0);
INSERT INTO `credit_line` VALUES (3, '121', '2022', 2, 4.0, 2.0, 2.0, 2.0);
INSERT INTO `credit_line` VALUES (4, '121', '2022', 3, 3.0, 2.0, 2.0, 2.0);
INSERT INTO `credit_line` VALUES (5, '121', '2022', 4, 2.0, 1.0, 1.0, 2.0);
INSERT INTO `credit_line` VALUES (6, '122', '2022', 0, 7.0, 6.0, 6.0, 4.0);
INSERT INTO `credit_line` VALUES (7, '122', '2022', 1, 1.0, 2.0, 2.0, 0.0);
INSERT INTO `credit_line` VALUES (8, '122', '2022', 2, 4.0, 2.0, 2.0, 2.0);
INSERT INTO `credit_line` VALUES (9, '122', '2022', 3, 2.0, 2.0, 2.0, 2.0);
INSERT INTO `credit_line` VALUES (10, '122', '2022', 4, 0.0, 0.0, 0.0, 0.0);
INSERT INTO `credit_line` VALUES (26, '121', '2024', 1, 0.0, 1.0, 0.0, 0.0);
INSERT INTO `credit_line` VALUES (27, '121', '2024', 2, 0.0, 0.0, 0.0, 0.0);
INSERT INTO `credit_line` VALUES (28, '121', '2024', 3, 0.0, 0.0, 0.0, 0.0);
INSERT INTO `credit_line` VALUES (29, '121', '2024', 4, 0.0, 0.0, 0.0, 0.0);
INSERT INTO `credit_line` VALUES (30, '121', '2024', 0, 0.0, 1.0, 0.0, 0.0);
INSERT INTO `credit_line` VALUES (46, '151', '2022', 1, 1.0, 0.0, 1.0, 0.0);
INSERT INTO `credit_line` VALUES (47, '151', '2022', 2, 1.5, 1.0, 1.0, 1.0);
INSERT INTO `credit_line` VALUES (48, '151', '2022', 3, 1.0, 1.5, 1.0, 1.0);
INSERT INTO `credit_line` VALUES (49, '151', '2022', 4, 4.0, 4.0, 2.0, 4.0);
INSERT INTO `credit_line` VALUES (50, '151', '2022', 0, 3.5, 2.5, 3.0, 2.0);

-- ----------------------------
-- Table structure for department
-- ----------------------------
DROP TABLE IF EXISTS `department`;
CREATE TABLE `department`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `dept_id` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '学院代码',
  `dept_name` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '学院名称',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `department_dept_id_uindex`(`dept_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 15 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of department
-- ----------------------------
INSERT INTO `department` VALUES (1, '121', '软件工程学院');
INSERT INTO `department` VALUES (2, '122', '智能制造学院');
INSERT INTO `department` VALUES (3, '124', '计算机与科学学院');
INSERT INTO `department` VALUES (7, '131', '数学科学院');
INSERT INTO `department` VALUES (8, '141', '马克思主义学院');
INSERT INTO `department` VALUES (10, '151', '外语学院');
INSERT INTO `department` VALUES (11, '123', '网络工程学院');

-- ----------------------------
-- Table structure for notice
-- ----------------------------
DROP TABLE IF EXISTS `notice`;
CREATE TABLE `notice`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '内容',
  `time` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '公告表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of notice
-- ----------------------------
INSERT INTO `notice` VALUES (1, '123', '123123', '2025-09-10 23:19:08');

-- ----------------------------
-- Table structure for notification
-- ----------------------------
DROP TABLE IF EXISTS `notification`;
CREATE TABLE `notification`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL COMMENT '接收通知的用户ID',
  `type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '通知类型',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '通知内容',
  `is_read` tinyint NULL DEFAULT 0 COMMENT '是否已读（0：未读，1：已读）',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2350 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of notification
-- ----------------------------

-- ----------------------------
-- Table structure for permission
-- ----------------------------
DROP TABLE IF EXISTS `permission`;
CREATE TABLE `permission`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `permission_code` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '权限编码',
  `permission_name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '权限名称',
  `module` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '所属模块(如:USER, COURSE, DEPARTMENT等)',
  `action` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '操作类型(VIEW, ADD, EDIT, DELETE)',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 14 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of permission
-- ----------------------------
INSERT INTO `permission` VALUES (1, 'ALL:VIEW', '查看', 'ALL', 'VIEW');
INSERT INTO `permission` VALUES (2, 'ALL:ADD', '添加', 'ALL', 'ADD');
INSERT INTO `permission` VALUES (3, 'ALL:UPDATE', '编辑', 'ALL', 'UPDATE');
INSERT INTO `permission` VALUES (4, 'ALL:DELETE', '删除', 'ALL', 'DELETE');
INSERT INTO `permission` VALUES (5, 'CRLINE:VIEW', '查看选修达标分数', 'CRLINE', 'VIEW');
INSERT INTO `permission` VALUES (6, 'TEACHER:VIEW', '查看教师信息', 'TEACHER', 'VIEW');
INSERT INTO `permission` VALUES (7, 'TEACHER:UPDATE', '编辑教师信息', 'TEACHER', 'UPDATE');
INSERT INTO `permission` VALUES (8, 'STUDENT:VIEW', '查看学生信息', 'STUDENT', 'VIEW');
INSERT INTO `permission` VALUES (9, 'STUDENT:UPDATE', '编辑学生信息', 'STUDENT', 'UPDATE');
INSERT INTO `permission` VALUES (10, 'COSELECTION:VIEW', '查看选修课程', 'COSELECTION', 'VIEW');
INSERT INTO `permission` VALUES (11, 'COSELECTION:ADD', '添加选修课程', 'COSELECTION', 'ADD');
INSERT INTO `permission` VALUES (12, 'COSELECTION:UPDATE', '编辑选修课程', 'COSELECTION', 'UPDATE');
INSERT INTO `permission` VALUES (13, 'COSELECTION:DELETE', '删除选修课程', 'COSELECTION', 'DELETE');

-- ----------------------------
-- Table structure for role
-- ----------------------------
DROP TABLE IF EXISTS `role`;
CREATE TABLE `role`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `role_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '角色编码',
  `role_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '角色名称',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of role
-- ----------------------------
INSERT INTO `role` VALUES (1, 'ADMIN', '系统管理员1');
INSERT INTO `role` VALUES (2, 'TEACHER', '教师');
INSERT INTO `role` VALUES (3, 'STUDENT', '学生');

-- ----------------------------
-- Table structure for role_permission
-- ----------------------------
DROP TABLE IF EXISTS `role_permission`;
CREATE TABLE `role_permission`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `role_id` int NOT NULL COMMENT '角色ID',
  `permission_id` int NOT NULL COMMENT '权限ID',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 33 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of role_permission
-- ----------------------------
INSERT INTO `role_permission` VALUES (12, 3, 5);
INSERT INTO `role_permission` VALUES (13, 3, 8);
INSERT INTO `role_permission` VALUES (14, 3, 9);
INSERT INTO `role_permission` VALUES (15, 3, 10);
INSERT INTO `role_permission` VALUES (16, 3, 12);
INSERT INTO `role_permission` VALUES (17, 1, 2);
INSERT INTO `role_permission` VALUES (18, 1, 4);
INSERT INTO `role_permission` VALUES (19, 1, 3);
INSERT INTO `role_permission` VALUES (20, 1, 1);
INSERT INTO `role_permission` VALUES (21, 1, 11);
INSERT INTO `role_permission` VALUES (22, 1, 13);
INSERT INTO `role_permission` VALUES (23, 1, 12);
INSERT INTO `role_permission` VALUES (24, 1, 10);
INSERT INTO `role_permission` VALUES (25, 2, 11);
INSERT INTO `role_permission` VALUES (26, 2, 13);
INSERT INTO `role_permission` VALUES (27, 2, 12);
INSERT INTO `role_permission` VALUES (28, 2, 10);
INSERT INTO `role_permission` VALUES (29, 2, 9);
INSERT INTO `role_permission` VALUES (30, 2, 8);
INSERT INTO `role_permission` VALUES (31, 2, 7);
INSERT INTO `role_permission` VALUES (32, 2, 6);

-- ----------------------------
-- Table structure for semester_time_range
-- ----------------------------
DROP TABLE IF EXISTS `semester_time_range`;
CREATE TABLE `semester_time_range`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `semester` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '学期',
  `course_create_start` datetime NOT NULL COMMENT '课程创建开始时间',
  `course_create_end` datetime NOT NULL COMMENT '课程创建结束时间',
  `course_select_start` datetime NOT NULL COMMENT '选课开始时间',
  `course_select_end` datetime NOT NULL COMMENT '选课结束时间',
  `created_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of semester_time_range
-- ----------------------------
INSERT INTO `semester_time_range` VALUES (1, '2025-2026学年第一学期', '2025-06-01 00:00:00', '2025-08-31 23:59:59', '2025-08-15 00:00:00', '2025-09-15 23:59:59', '2026-04-21 16:50:44', '2026-04-21 16:50:44');
INSERT INTO `semester_time_range` VALUES (2, '2025-2026学年第二学期', '2026-04-24 00:00:00', '2026-04-25 23:59:59', '2026-04-24 00:00:00', '2026-04-25 23:59:59', '2026-04-21 16:50:44', '2026-04-24 12:39:27');
INSERT INTO `semester_time_range` VALUES (3, '2026-2027学年第一学期', '2026-06-01 00:00:00', '2026-08-31 23:59:59', '2026-08-15 00:00:00', '2026-09-15 23:59:59', '2026-04-21 16:50:44', '2026-04-21 16:50:44');

-- ----------------------------
-- Table structure for student
-- ----------------------------
DROP TABLE IF EXISTS `student`;
CREATE TABLE `student`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键id\r\n',
  `username` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '用户名',
  `password` varchar(225) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '密码',
  `salt` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '盐值',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '头像\n',
  `name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '姓名\n',
  `gender` int NULL DEFAULT NULL COMMENT '性别',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '手机号',
  `email` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '邮箱',
  `dept_id` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '学院编号',
  `grade` varchar(6) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '// 年级（入学时间，判断第几节）',
  `sort` int NULL DEFAULT NULL COMMENT '// 排序(1.大一 2.大二 3.大三 4.大四)',
  `public_credits` decimal(3, 1) NULL DEFAULT NULL COMMENT '//公共类型学分',
  `foreign_language_credits` decimal(3, 1) NULL DEFAULT NULL COMMENT '// 外语选修课学分要求',
  `sport_credits` decimal(3, 1) NULL DEFAULT NULL COMMENT '体育选修课学分要求',
  `art_credits` decimal(3, 1) NULL DEFAULT NULL COMMENT '// 艺术选修课学分要求',
  `status` int NULL DEFAULT NULL COMMENT '//状态信息（0：在读，1：休学，2：离校）',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of student
-- ----------------------------
INSERT INTO `student` VALUES (1, '1234567', '+la8X4Z5p0mzFvvQsWotNEoCACml8=', 'a1b2c3d4e5', '', '张一', 1, '', '', '131', '2022', 4, 2.0, 3.0, 1.0, 4.0, 0);
INSERT INTO `student` VALUES (2, '2123', '+la8X4Z5p0mzFvvQsWotNEoCACml8=', 'a1b2c3d4e5', '', '张二', 2, '', '', '121', '2022', 4, 2.0, 2.0, 2.0, 2.0, 0);
INSERT INTO `student` VALUES (3, '12345', '+la8X4Z5p0mzFvvQsWotNEoCACml8=', 'a1b2c3d4e5', '', '张三', 2, '', '', '121', '2022', 4, 2.0, 2.0, 3.0, 0.0, 0);
INSERT INTO `student` VALUES (4, '1234', 'kcP8/OfsCNgBGJ+la8X4Z5p0mzFvvQsWotNEoCACml8=', 'a1b2c3d4e5', '', '张四', 2, '', '', '151', '2022', 4, 0.0, 0.0, 1.0, 0.0, 0);
INSERT INTO `student` VALUES (9, '1212', 'hYqLl7QfLaaZLh03bRmh/9ESlMFhmjwMHNnCSfT7aIo=', '4w3eiNdgYp', '', '王其', 1, '', '', '121', '2022', 4, 0.0, 0.0, 0.0, 0.0, 0);

-- ----------------------------
-- Table structure for student_section
-- ----------------------------
DROP TABLE IF EXISTS `student_section`;
CREATE TABLE `student_section`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `scourse_id` int NOT NULL COMMENT '选课课程编号',
  `student_id` int NOT NULL COMMENT '学生编号',
  `student_name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '学生姓名',
  `ordinary_credit` decimal(4, 1) NULL DEFAULT NULL COMMENT '平时成绩',
  `exam_credit` decimal(4, 1) NULL DEFAULT NULL COMMENT '考试成绩',
  `total_credit` decimal(4, 1) NULL DEFAULT NULL COMMENT '总成绩',
  `status` int NULL DEFAULT NULL COMMENT '状态',
  `update_time` datetime NULL DEFAULT NULL COMMENT '时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 16 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of student_section
-- ----------------------------
INSERT INTO `student_section` VALUES (1, 20, 4, '张四', 86.0, 91.5, 89.3, 1, NULL);
INSERT INTO `student_section` VALUES (3, 24, 4, '张四', 89.0, 15.0, 44.6, 0, '2026-04-24 12:50:32');
INSERT INTO `student_section` VALUES (4, 27, 3, '张三', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `student_section` VALUES (5, 20, 3, '张三', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `student_section` VALUES (6, 23, 3, '张三', 94.0, 87.0, 89.8, 1, NULL);
INSERT INTO `student_section` VALUES (7, 37, 2, '张二', 84.0, 77.0, 79.8, 1, '2026-03-12 20:00:03');
INSERT INTO `student_section` VALUES (10, 35, 4, '张四', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `student_section` VALUES (11, 34, 4, '张四', 78.0, 85.0, 82.2, 1, '2026-03-21 23:47:17');
INSERT INTO `student_section` VALUES (13, 23, 4, '张四', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `student_section` VALUES (15, 41, 9, '王其', NULL, NULL, NULL, NULL, NULL);

-- ----------------------------
-- Table structure for teacher
-- ----------------------------
DROP TABLE IF EXISTS `teacher`;
CREATE TABLE `teacher`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `username` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户名',
  `password` varchar(225) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '密码',
  `salt` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '盐值',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '头像\n',
  `name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '姓名\n',
  `gender` int NULL DEFAULT NULL COMMENT '性别',
  `dept_id` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '部门编号',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '手机号',
  `email` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '邮箱',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `teacher_username_uindex`(`username` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of teacher
-- ----------------------------
INSERT INTO `teacher` VALUES (1, '221123', 'p2RLdWUPN8/q1VgZqEQBuF/HbHKLUi3q9HHGeiyBKUg=', 'a1b1c3d4e6', '', '李一', 1, '121', '1821831795', '221123263@qq.com');
INSERT INTO `teacher` VALUES (2, '050608', 'm2l1k0j9i8h7g6f5e4d3c2b1a0f9e8d7c6b5a4f3e2d1c0b9a8f7e6d5c4b', 'f6g7h8i9j0', '', '李二', 1, '131', '18218317952', '2211232623@qq.com');
INSERT INTO `teacher` VALUES (3, '221122', 'kcP8/OfsCNgBGJ+la8X4Z5p0mzFvvQsWotNEoCACml8=', 'a1b2c3d4e5', '', '李三', 2, '121', '1821837952', '221123223@qq.com');
INSERT INTO `teacher` VALUES (4, '245217', 'kcP8/OfsCNgBGJ+la8X4Z5p0mzFvvQsWotNEoCACml8=', 'a1b2c3d4e5', '', '李四', 2, '151', '18218312952', '2141232623@qq.com');

-- ----------------------------
-- Table structure for user_role
-- ----------------------------
DROP TABLE IF EXISTS `user_role`;
CREATE TABLE `user_role`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` int NOT NULL COMMENT '用户ID',
  `user_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户类型(ADMIN, TEACHER, STUDENT)',
  `role_id` int NOT NULL COMMENT '角色ID',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of user_role
-- ----------------------------
INSERT INTO `user_role` VALUES (1, 1, 'TEACHER', 1);
INSERT INTO `user_role` VALUES (2, 4, 'TEACHER', 1);

SET FOREIGN_KEY_CHECKS = 1;
