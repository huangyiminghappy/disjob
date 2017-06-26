/*
Navicat MySQL Data Transfer

Source Server         : 10.40.6.187
Source Server Version : 50631
Source Host           : 10.40.6.187:3306
Source Database       : ejob

Target Server Type    : MYSQL
Target Server Version : 50631
File Encoding         : 65001

Date: 2016-07-07 15:33:02
此文件存储ejob所有表结构预计初始数据更改

*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `ejob_job_basicinfo`
-- ----------------------------
DROP TABLE IF EXISTS `ejob_job_basicinfo`;
CREATE TABLE `ejob_job_basicinfo` (
  `pk_uuid` char(32) NOT NULL COMMENT '任务唯一标识',
  `ix_group_name` varchar(32) NOT NULL COMMENT '任务组名称',
  `ix_job_name` varchar(50) NOT NULL COMMENT '任务名称',
  `ix_created_at` timestamp NULL DEFAULT NULL COMMENT '创建时间',
  `ix_updated_at` timestamp NULL DEFAULT NULL COMMENT '最后一次更新时间',
  `ix_schedule_sip` char(15) DEFAULT NULL COMMENT '调度服务器IP',
  `ix_business_sip` char(15) DEFAULT NULL COMMENT '业务执行服务器IP',
  `ix_schedule_start` timestamp NULL DEFAULT NULL COMMENT '调度开始时间',
  `ix_schedule_end` timestamp NULL DEFAULT NULL COMMENT '调度结束时间',
  `ix_execute_start` timestamp NULL DEFAULT NULL COMMENT '执行开始时间',
  `ix_execute_end` timestamp NULL DEFAULT NULL COMMENT '执行结束时间',
  `ix_time_consuming` int(11) NOT NULL DEFAULT '0' COMMENT '任务执行耗时',
  `ix_current_status` char(1) DEFAULT '1' COMMENT '当前状态,1表成功,0表失败',
  `ix_error_location` varchar(128) DEFAULT NULL COMMENT '失败时异常位置',
  `ix_error_type` char(6) DEFAULT NULL COMMENT '失败时异常类型:-1->调度失败，1->php执行文件不存在，2->php类不存在，3->job方法不存在',
  `ix_error_reason` text COMMENT '失败原因',
  `ix_is_timeout` int(1) NOT NULL DEFAULT '0' COMMENT '是否超时,0:否,1:是',
  `ix_recvtime` varchar(32) DEFAULT NULL COMMENT 'php接收请求的时间',
  `ix_killprocess` int(1) NOT NULL DEFAULT '0' COMMENT '是否是强制杀死任务,0:否,1:是',
  `ix_sharing_requestid` varchar(32) DEFAULT NULL,
  UNIQUE KEY `pk_uuid` (`pk_uuid`),
  KEY `i_ix_group_name` (`ix_group_name`) USING HASH,
  KEY `i_ix_job_name` (`ix_job_name`) USING HASH,
  KEY `i_ix_create_time` (`ix_created_at`) USING BTREE
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for `ejob_job_exeprogress`
-- ----------------------------
DROP TABLE IF EXISTS `ejob_job_exeprogress`;
CREATE TABLE `ejob_job_exeprogress` (
  `pk_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增的表记录标志ID',
  `ix_uuid` char(32) NOT NULL COMMENT '任务唯一标识',
  `ix_created_at` timestamp null COMMENT '创建时间',
  `ix_business_sip` char(15) DEFAULT NULL COMMENT '业务服务器IP',
  `ix_data_time` varchar(32) DEFAULT NULL COMMENT '数据生成时间',
  `ix_type` tinyint(2) NOT NULL DEFAULT '0' COMMENT '进度类型',
  `ix_content` varchar(128) DEFAULT NULL COMMENT '进度内容',
  UNIQUE KEY `pk_id` (`pk_id`)
) ENGINE=MyISAM AUTO_INCREMENT=15 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for `ejob_job_alarm_mapping`
-- ----------------------------
DROP TABLE IF EXISTS `ejob_job_alarm_mapping`;
CREATE TABLE `ejob_job_alarm_mapping` (
  `pk_group_name` varchar(32) NOT NULL COMMENT '任务组名称',
  `ix_created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `ix_updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `ix_is_alarm` int(1) NOT NULL DEFAULT '0' COMMENT '是否报警，1表示开启,0表示关闭',
  `ix_alarm_rtx` varchar(256) DEFAULT NULL COMMENT 'rtx报警列表，多用户逗号分隔',
  UNIQUE KEY `pk_group_name` (`pk_group_name`)
) ENGINE=MyISAM AUTO_INCREMENT=8395608 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for `ejob_job_user`
-- ----------------------------
DROP TABLE IF EXISTS `ejob_job_user`;
CREATE TABLE `ejob_job_user` (
  `pk_username` varchar(32) NOT NULL COMMENT '用户名',
  `ix_password` varchar(32) NOT NULL COMMENT '密码',
  `ix_rolename` varchar(32) DEFAULT '普通用户' COMMENT '角色名',
  `ix_created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `ix_updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  UNIQUE KEY `pk_username` (`pk_username`)
) ENGINE=MyISAM AUTO_INCREMENT=8395608 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of ejob_job_user
-- ----------------------------
INSERT INTO `ejob_job_user` VALUES ('admin', 'admin', '超级管理员', '2016-09-07 13:45:16', '2016-09-07 13:45:16');


-- 二期新加的表：
DROP TABLE IF EXISTS `ejob_user_action_record`;
CREATE TABLE `ejob_user_action_record` (
  `pk_id` varchar(32) COLLATE utf8_unicode_ci NOT NULL,
  `ix_username` varchar(16) COLLATE utf8_unicode_ci NOT NULL COMMENT '用户名',
  `ix_permititem` varchar(255) COLLATE utf8_unicode_ci NOT NULL COMMENT '操作',
  `ix_result` varchar(10) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '操作结果',
  `ix_actiondate` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' ON UPDATE CURRENT_TIMESTAMP,
  `ix_action_param` varchar(500) COLLATE utf8_unicode_ci DEFAULT NULL,
  `ix_host` varchar(32) COLLATE utf8_unicode_ci DEFAULT NULL,
  `ix_addr` varchar(32) COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`pk_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

DROP TABLE IF EXISTS `ejob_job_permititem`;
CREATE TABLE `ejob_job_permititem` (
  `ix_url` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `ix_description` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `pk_id` int(32) NOT NULL AUTO_INCREMENT,
  `ix_item_type` varchar(32) COLLATE utf8_unicode_ci NOT NULL,
  `ix_operate_type` varchar(32) COLLATE utf8_unicode_ci DEFAULT NULL,
  `ix_parentItem` varchar(32) COLLATE utf8_unicode_ci DEFAULT NULL,
  `ix_enabled` char(1) COLLATE utf8_unicode_ci NOT NULL DEFAULT '0',
  PRIMARY KEY (`pk_id`)
) ENGINE=InnoDB AUTO_INCREMENT=32 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- ----------------------------
-- Records of ejob_job_permititem
-- ----------------------------
INSERT INTO `ejob_job_permititem` VALUES ('/page/job/alarm', 'job报警管理', '1', 'PAGE', null, null, '1');
INSERT INTO `ejob_job_permititem` VALUES ('/page/job/group', 'job组管理', '2', 'PAGE', null, null, '1');
INSERT INTO `ejob_job_permititem` VALUES ('/page/job/info', 'job信息管理', '3', 'PAGE', null, null, '1');
INSERT INTO `ejob_job_permititem` VALUES ('/page/monitor/auth', 'job数据授权', '4', 'PAGE', null, null, '0');
INSERT INTO `ejob_job_permititem` VALUES ('/page/monitor/cronTransfer', 'cron表达式转换', '5', 'PAGE', null, null, '1');
INSERT INTO `ejob_job_permititem` VALUES ('/page/monitor/minotorInfo', '任务监控', '6', 'PAGE', null, null, '1');
INSERT INTO `ejob_job_permititem` VALUES ('/page/service/serverInfo', '服务信息', '7', 'PAGE', null, null, '1');
INSERT INTO `ejob_job_permititem` VALUES ('/service/job/group/update', '任务组信息编辑', '8', 'ACTION', 'UPDATE', '2', '1');
INSERT INTO `ejob_job_permititem` VALUES ('/service/job/group/delete', '任务组信息删除', '9', 'ACTION', 'DELETE', '2', '1');
INSERT INTO `ejob_job_permititem` VALUES ('/service/job/alarm/list', '报警信息查询', '11', 'ACTION', 'SELECT', '1', '0');
INSERT INTO `ejob_job_permititem` VALUES ('/service/job/alarm/update', '报警信息编辑', '12', 'ACTION', 'UPDATE', '1', '1');
INSERT INTO `ejob_job_permititem` VALUES ('/service/job/alarm/delete', '报警信息删除', '13', 'ACTION', 'DELETE', '1', '1');
INSERT INTO `ejob_job_permititem` VALUES ('/service/job/group/list', 'job信息管理-jobGroup查询', '14', 'ACTION', 'SELECT', '3', '0');
INSERT INTO `ejob_job_permititem` VALUES ('/service/job/info/search', 'job信息管理-job查询', '15', 'ACTION', 'SELECT', '3', '0');
INSERT INTO `ejob_job_permititem` VALUES ('/page/monitor/jobExeStatistic', 'job统计查询', '16', 'PAGE', null, null, '1');
INSERT INTO `ejob_job_permititem` VALUES ('/service/monitor/jobStatistics', 'job统计查询-数据查询', '17', 'ACTION', 'SELECT', '16', '0');
INSERT INTO `ejob_job_permititem` VALUES ('/service/job/info/batchHandle', 'job批量执行', '18', 'ACTION', 'UPDATE', '3', '1');
INSERT INTO `ejob_job_permititem` VALUES ('/page/monitor/jobExeDetai', 'job执行明细', '19', 'PAGE', null, null, '1');
INSERT INTO `ejob_job_permititem` VALUES ('/service/monitor/jobDetailQuery', 'job执行明细-查询', '20', 'ACTION', 'SELECT', '19', '0');
INSERT INTO `ejob_job_permititem` VALUES ('/service/job/info/update', 'job信息管理-job编辑', '21', 'ACTION', 'UPDATE', '3', '1');
INSERT INTO `ejob_job_permititem` VALUES ('/service/job/info/fireNow', 'job信息管理-立即执行', '22', 'ACTION', 'UPDATE', '3', '1');
INSERT INTO `ejob_job_permititem` VALUES ('/service/job/info/pause', 'job信息管理-job暂停', '23', 'ACTION', 'OPERATE', '3', '1');
INSERT INTO `ejob_job_permititem` VALUES ('/service/job/info/resume', 'job信息管理-job恢复', '24', 'ACTION', 'OPERATE', '3', '1');
INSERT INTO `ejob_job_permititem` VALUES ('/service/monitor/restartJob', 'job信息管理-job重新加载', '25', 'ACTION', 'OPERATE', '3', '1');
INSERT INTO `ejob_job_permititem` VALUES ('/service/auth/getAuthInfos', 'job数据授权-权限查询', '26', 'ACTION', 'SELECT', '4', '0');
INSERT INTO `ejob_job_permititem` VALUES ('/service/auth/getUserList', 'job数据授权-用户查询', '27', 'ACTION', 'SELECT', '4', '0');
INSERT INTO `ejob_job_permititem` VALUES ('/service/auth/getJobgroup', 'job数据授权-任务组查询', '28', 'ACTION', 'SELECT', '4', '0');
INSERT INTO `ejob_job_permititem` VALUES ('/service/auth/auth', 'job数据授权-授权', '29', 'ACTION', 'OPERATE', '4', '1');
INSERT INTO `ejob_job_permititem` VALUES ('/service/auth/unAuth', 'job数据授权-权限取消', '30', 'ACTION', 'OPERATE', '4', '1');
INSERT INTO `ejob_job_permititem` VALUES ('/service/job/info/jobDetailInfo', 'job编辑详情页', '31', 'PAGE', null, '3', '1');

DROP TABLE IF EXISTS `ejob_job_userpermit`;
CREATE TABLE `ejob_job_userpermit` (
  `pk_id` int(32) NOT NULL AUTO_INCREMENT,
  `ix_user` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `ix_permititem` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  PRIMARY KEY (`pk_id`)
) ENGINE=InnoDB AUTO_INCREMENT=29 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

DROP TABLE IF EXISTS `ejob_rms_project_info`;
CREATE TABLE `ejob_rms_project_info` (
  `pk_id` int(10) NOT NULL AUTO_INCREMENT,
  `ix_project_code` varchar(32) COLLATE utf8_unicode_ci NOT NULL,
  `ix_token` varchar(32) COLLATE utf8_unicode_ci NOT NULL,
  PRIMARY KEY (`pk_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- ----------------------------
-- Records of ejob_rms_project_info
-- ----------------------------
INSERT INTO `ejob_rms_project_info` VALUES ('1', 'Ejob_services', 'RqDcKC1BYNIORNZ6ndZ4');
INSERT INTO `ejob_rms_project_info` VALUES ('2', 'Financial_system', 'Vklq83ilqeyAEEov1su5');

DROP TABLE IF EXISTS `ejob_rms_point_info`;
CREATE TABLE `ejob_rms_point_info` (
  `pk_id` int(10) NOT NULL AUTO_INCREMENT,
  `ix_project_code` varchar(32) COLLATE utf8_unicode_ci NOT NULL,
  `ix_point_code` varchar(32) COLLATE utf8_unicode_ci NOT NULL,
  `ix_description` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`pk_id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- ----------------------------
-- Records of ejob_rms_point_info
-- ----------------------------
INSERT INTO `ejob_rms_point_info` VALUES ('1', 'Ejob_services', 'SJC93229', 'Ejob功能监控');
INSERT INTO `ejob_rms_point_info` VALUES ('2', 'Ejob_services', 'SJC51876', 'Ejob网络闪断');
INSERT INTO `ejob_rms_point_info` VALUES ('3', 'Ejob_services', 'SJC93005', 'ejob功能监控(严重)');
INSERT INTO `ejob_rms_point_info` VALUES ('4', 'Financial_system', 'SJC31808', '财务');
INSERT INTO `ejob_rms_point_info` VALUES ('5', 'Ejob_services', 'SJC31808', 'Ejob自检发送');

DROP TABLE IF EXISTS `ejob_rms_errorcode`;
CREATE TABLE `ejob_rms_errorcode` (
  `ix_description` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `pk_id` int(10) NOT NULL AUTO_INCREMENT,
  `ix_point_code` varchar(16) COLLATE utf8_unicode_ci NOT NULL COMMENT '监控点编码',
  `ix_error_code` varchar(16) COLLATE utf8_unicode_ci NOT NULL COMMENT '错误编码',
  `ix_is_test` char(1) COLLATE utf8_unicode_ci NOT NULL DEFAULT '0',
  `ix_test_project_code` varchar(32) COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`pk_id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- ----------------------------
-- Records of ejob_rms_errorcode
-- ----------------------------
INSERT INTO `ejob_rms_errorcode` VALUES ('Ejob功能监控_ejob发生异常', '1', 'SJC93229', '230101', '1', '1');
INSERT INTO `ejob_rms_errorcode` VALUES ('Ejob功能监控_连接等待超时', '2', 'SJC93229', '230102', '0', null);
INSERT INTO `ejob_rms_errorcode` VALUES ('Ejob网络闪断_网络出现闪断', '3', 'SJC51876', '230201', '1', '1');
INSERT INTO `ejob_rms_errorcode` VALUES ('Ejob业务监控_x系统连接不上', '4', 'SJC31808', '230301', '0', null);
INSERT INTO `ejob_rms_errorcode` VALUES ('Ejob业务监控_任务超时报警', '5', 'SJC31808', '230302', '0', null);
INSERT INTO `ejob_rms_errorcode` VALUES ('Ejob业务监控_系统调用后返回错误信息', '6', 'SJC31808', '230303', '0', null);
INSERT INTO `ejob_rms_errorcode` VALUES ('Ejob功能监控_服务仅一个可用(严重)', '7', 'SJC93005', '230401', '1', '1');
INSERT INTO `ejob_rms_errorcode` VALUES ('Ejob业务监控_channel无法连接达十次(严重)', '8', 'SJC93005', '230402', '0', null);
INSERT INTO `ejob_rms_errorcode` VALUES ('Ejob业务监控_自检', '9', 'SJC31808', '230301', '1', '1');

DROP TABLE IF EXISTS `ejob_rms_group_mapping`;
CREATE TABLE `ejob_rms_group_mapping` (
  `pk_id` int(10) NOT NULL AUTO_INCREMENT,
  `ix_project_code` varchar(32) COLLATE utf8_unicode_ci NOT NULL,
  `ix_job_group` varchar(32) COLLATE utf8_unicode_ci NOT NULL,
  PRIMARY KEY (`pk_id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

 
-- 添加fin 对应ejob这边具体组
insert into ejob_rms_group_mapping(ix_project_code, ix_job_group) values('Financial_system','fin');

--添加pms报警

delete from ejob_rms_point_info where ix_project_code = 'PMS_system';
insert into ejob_rms_point_info(ix_project_code, ix_point_code, ix_description) values('PMS_system','SJC31808','PMS供应商系统');
delete from ejob_rms_project_info where ix_project_code = 'PMS_system';
insert into ejob_rms_project_info(ix_project_code,ix_token) values('PMS_system','OOUTLtNmnvfV4eEiNvYz');

insert into ejob_rms_group_mapping(ix_project_code, ix_job_group) values('PMS_system','pms');

--rms报警是否启用的字段
-- ----------------------------
-- Table structure for `ejob_rms_errorcode`
-- ----------------------------
DROP TABLE IF EXISTS `ejob_rms_errorcode`;
CREATE TABLE `ejob_rms_errorcode` (
  `ix_description` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `pk_id` int(10) NOT NULL AUTO_INCREMENT,
  `ix_point_code` varchar(16) COLLATE utf8_unicode_ci NOT NULL COMMENT '监控点编码',
  `ix_error_code` varchar(16) COLLATE utf8_unicode_ci NOT NULL COMMENT '错误编码',
  `ix_is_test` char(1) COLLATE utf8_unicode_ci NOT NULL DEFAULT '0',
  `ix_test_project_code` varchar(32) COLLATE utf8_unicode_ci DEFAULT NULL,
  `ix_available` char(1) COLLATE utf8_unicode_ci NOT NULL DEFAULT '0',
  PRIMARY KEY (`pk_id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- ----------------------------
-- Records of ejob_rms_errorcode
-- ----------------------------
INSERT INTO `ejob_rms_errorcode` VALUES ('Ejob功能监控_ejob发生异常', '1', 'SJC93229', '230101', '1', '1', '1');
INSERT INTO `ejob_rms_errorcode` VALUES ('Ejob功能监控_连接等待超时', '2', 'SJC93229', '230102', '0', null, '1');
INSERT INTO `ejob_rms_errorcode` VALUES ('Ejob网络闪断_网络出现闪断', '3', 'SJC51876', '230201', '1', '1', '1');
INSERT INTO `ejob_rms_errorcode` VALUES ('Ejob业务监控_x系统连接不上', '4', 'SJC31808', '230301', '0', null, '1');
INSERT INTO `ejob_rms_errorcode` VALUES ('Ejob业务监控_任务超时报警', '5', 'SJC31808', '230302', '0', null, '1');
INSERT INTO `ejob_rms_errorcode` VALUES ('Ejob业务监控_系统调用后返回错误信息', '6', 'SJC31808', '230303', '0', null, '1');
INSERT INTO `ejob_rms_errorcode` VALUES ('Ejob功能监控_服务仅一个可用(严重)', '7', 'SJC93005', '230401', '1', '1', '1');
INSERT INTO `ejob_rms_errorcode` VALUES ('Ejob业务监控_channel无法连接达十次(严重)', '8', 'SJC93005', '230402', '0', null, '1');
INSERT INTO `ejob_rms_errorcode` VALUES ('Ejob业务监控_自检', '9', 'SJC31808', '230301', '1', '1', '1');