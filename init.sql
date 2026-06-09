-- ============================================================
-- 新时代中国传统优秀文化成就思政素材智能检索学习系统V1.0
-- 数据库初始化脚本
-- MySQL 8.0+
-- ============================================================

CREATE DATABASE IF NOT EXISTS szxx_system
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_general_ci;

USE szxx_system;

-- ============================================================
-- 一、用户表
-- ============================================================
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id`           BIGINT        NOT NULL COMMENT '主键，雪花算法生成',
  `username`     VARCHAR(32)   NOT NULL COMMENT '登录用户名，唯一',
  `password`     VARCHAR(128)  NOT NULL COMMENT 'BCrypt加密后的密码',
  `nickname`     VARCHAR(32)   NOT NULL COMMENT '显示昵称',
  `avatar`       VARCHAR(255)  DEFAULT NULL COMMENT '头像URL',
  `email`        VARCHAR(64)   DEFAULT NULL COMMENT '邮箱',
  `phone`        VARCHAR(16)   DEFAULT NULL COMMENT '手机号',
  `role`         VARCHAR(16)   NOT NULL DEFAULT 'student' COMMENT '角色: student/teacher/admin',
  `status`       VARCHAR(16)   NOT NULL DEFAULT 'active' COMMENT '账号状态: active/disabled',
  `created_at`   DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
  `updated_at`   DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  KEY `idx_role` (`role`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 初始管理员账号：szxx / szxx123
INSERT INTO `user` (`id`, `username`, `password`, `nickname`, `role`, `status`) VALUES
(1, 'szxx', '$2b$12$AVp7cZivklywVTLbURRw5OwCzKEtlnn7GG402Wi0vUgRdtvcu2J1u', '系统管理员', 'admin', 'active');


-- ============================================================
-- 二、素材表（核心表）
-- ============================================================
DROP TABLE IF EXISTS `material`;
CREATE TABLE `material` (
  `id`               BIGINT        NOT NULL COMMENT '主键，雪花算法',
  `title`            VARCHAR(200)  NOT NULL COMMENT '素材标题',
  `author`           VARCHAR(100)  DEFAULT NULL COMMENT '作者/来源出处',
  `dynasty`          VARCHAR(32)   NOT NULL COMMENT '朝代编码（pre_qin/qin_han/wei_jin/sui_tang/song/yuan/ming/qing/modern）',
  `category`         VARCHAR(32)   NOT NULL COMMENT '文化品类编码（zhuzi/feiyi/minsu/jiyi/hongse/diangu/shici/keji）',
  `education_level`  VARCHAR(32)   NOT NULL COMMENT '思政学段编码（primary_low/primary_high/junior/high/college）',
  `tags`             VARCHAR(500)  DEFAULT NULL COMMENT '标签名冗余存储（逗号分隔），用于列表页快速展示，精确检索走material_tag表',
  `cover_image`      VARCHAR(255)  DEFAULT NULL COMMENT '封面图URL',
  `summary`          VARCHAR(500)  DEFAULT NULL COMMENT '摘要，列表页展示用，截取正文前200字',
  `content`          LONGTEXT      DEFAULT NULL COMMENT '正文（富文本HTML），由wangEditor等编辑器生成',
  `video_url`        VARCHAR(500)  DEFAULT NULL COMMENT '关联视频链接',
  `uploader_id`      BIGINT        NOT NULL COMMENT '上传者ID，关联user.id',
  `status`           VARCHAR(16)   NOT NULL DEFAULT 'pending' COMMENT '审核状态: pending/approved/rejected',
  `review_comment`   VARCHAR(500)  DEFAULT NULL COMMENT '审核意见',
  `view_count`       INT           NOT NULL DEFAULT 0 COMMENT '浏览量（计数器）',
  `favorite_count`   INT           NOT NULL DEFAULT 0 COMMENT '收藏量（计数器，冗余避免COUNT查询）',
  `download_count`   INT           NOT NULL DEFAULT 0 COMMENT '下载量（计数器）',
  `created_at`       DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at`       DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_status` (`status`),
  KEY `idx_dynasty` (`dynasty`),
  KEY `idx_category` (`category`),
  KEY `idx_education` (`education_level`),
  KEY `idx_uploader` (`uploader_id`),
  KEY `idx_created_at` (`created_at`),
  FULLTEXT KEY `ft_title_content` (`title`, `content`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='素材表（核心表）';


-- ============================================================
-- 三、素材附件表（Word/PDF文件）
-- ============================================================
DROP TABLE IF EXISTS `material_attachment`;
CREATE TABLE `material_attachment` (
  `id`          BIGINT       NOT NULL COMMENT '主键',
  `material_id` BIGINT       NOT NULL COMMENT '所属素材ID',
  `filename`    VARCHAR(200) NOT NULL COMMENT '原始文件名（论语原文.docx）',
  `file_path`   VARCHAR(500) NOT NULL COMMENT '服务器存储路径',
  `file_type`   VARCHAR(16)  NOT NULL COMMENT '文件类型: docx/pdf',
  `file_size`   BIGINT       NOT NULL DEFAULT 0 COMMENT '文件大小（字节）',
  `created_at`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_material_id` (`material_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='素材附件表';


-- ============================================================
-- 四、标签表
-- ============================================================
DROP TABLE IF EXISTS `tag`;
CREATE TABLE `tag` (
  `id`   BIGINT      NOT NULL AUTO_INCREMENT COMMENT '主键，自增即可（标签量不大）',
  `name` VARCHAR(32) NOT NULL COMMENT '标签名，如"儒家""燕赵文化""非遗传承"',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='标签表';


-- ============================================================
-- 五、素材-标签关联表（多对多中间表）
-- ============================================================
DROP TABLE IF EXISTS `material_tag`;
CREATE TABLE `material_tag` (
  `id`          BIGINT   NOT NULL AUTO_INCREMENT,
  `material_id` BIGINT   NOT NULL COMMENT '素材ID',
  `tag_id`      BIGINT   NOT NULL COMMENT '标签ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_material_tag` (`material_id`, `tag_id`),
  KEY `idx_tag_id` (`tag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='素材-标签关联表';


-- ============================================================
-- 六、知识点表
-- ============================================================
DROP TABLE IF EXISTS `knowledge_point`;
CREATE TABLE `knowledge_point` (
  `id`          BIGINT       NOT NULL AUTO_INCREMENT,
  `material_id` BIGINT       NOT NULL COMMENT '所属素材ID',
  `title`       VARCHAR(200) NOT NULL COMMENT '知识点标题，如"仁的核心内涵"',
  `content`     TEXT         NOT NULL COMMENT '知识点解析内容',
  `sort_order`  INT          NOT NULL DEFAULT 0 COMMENT '排序号，值小的排前面',
  PRIMARY KEY (`id`),
  KEY `idx_material_id` (`material_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='知识点表';


-- ============================================================
-- 七、收藏表
-- ============================================================
DROP TABLE IF EXISTS `favorite`;
CREATE TABLE `favorite` (
  `id`          BIGINT   NOT NULL AUTO_INCREMENT,
  `user_id`     BIGINT   NOT NULL COMMENT '用户ID',
  `material_id` BIGINT   NOT NULL COMMENT '素材ID',
  `created_at`  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_material` (`user_id`, `material_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_material_id` (`material_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收藏表';


-- ============================================================
-- 八、学习记录表
-- ============================================================
DROP TABLE IF EXISTS `learning_record`;
CREATE TABLE `learning_record` (
  `id`          BIGINT   NOT NULL AUTO_INCREMENT,
  `user_id`     BIGINT   NOT NULL COMMENT '用户ID',
  `material_id` BIGINT   NOT NULL COMMENT '素材ID',
  `duration`    INT      NOT NULL DEFAULT 0 COMMENT '累计学习时长（秒）',
  `completed`   TINYINT  NOT NULL DEFAULT 0 COMMENT '是否读完: 0未完成/1已完成',
  `created_at`  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '首次学习时间',
  `updated_at`  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后学习时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_material` (`user_id`, `material_id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学习记录表';


-- ============================================================
-- 九、搜索日志表
-- ============================================================
DROP TABLE IF EXISTS `search_log`;
CREATE TABLE `search_log` (
  `id`           BIGINT       NOT NULL AUTO_INCREMENT,
  `user_id`      BIGINT       DEFAULT NULL COMMENT '用户ID（未登录用户为NULL）',
  `keyword`      VARCHAR(100) NOT NULL COMMENT '搜索关键词',
  `result_count` INT          NOT NULL DEFAULT 0 COMMENT '返回结果数',
  `created_at`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_keyword` (`keyword`),
  KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='搜索日志表';


-- ============================================================
-- 十、下载日志表
-- ============================================================
DROP TABLE IF EXISTS `download_log`;
CREATE TABLE `download_log` (
  `id`            BIGINT      NOT NULL AUTO_INCREMENT,
  `user_id`       BIGINT      NOT NULL COMMENT '下载用户ID',
  `material_id`   BIGINT      NOT NULL COMMENT '被下载的素材ID',
  `download_type` VARCHAR(16) NOT NULL DEFAULT 'attachment' COMMENT '下载类型: attachment(下载附件)/export(导出课件)',
  `created_at`    DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_material_id` (`material_id`),
  KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='下载日志表';


-- ============================================================
-- 十一、分类表（朝代/文化品类/思政学段统一存这里）
-- ============================================================
DROP TABLE IF EXISTS `category_dict`;
CREATE TABLE `category_dict` (
  `id`         BIGINT       NOT NULL AUTO_INCREMENT,
  `type`       VARCHAR(32)  NOT NULL COMMENT '分类类型: dynasty/category/education_level',
  `code`       VARCHAR(32)  NOT NULL COMMENT '编码（唯一标识，如 pre_qin / zhuzi / primary_low）',
  `name`       VARCHAR(64)  NOT NULL COMMENT '展示名称（如"先秦""诸子文化""小学(1-3年级)"）',
  `sort_order` INT          NOT NULL DEFAULT 0 COMMENT '排序号',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_type_code` (`type`, `code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='分类字典表';

-- 初始化分类数据
INSERT INTO `category_dict` (`type`, `code`, `name`, `sort_order`) VALUES
-- 朝代
('dynasty', 'pre_qin',  '先秦',       1),
('dynasty', 'qin_han',  '秦汉',       2),
('dynasty', 'wei_jin',  '魏晋南北朝', 3),
('dynasty', 'sui_tang', '隋唐',       4),
('dynasty', 'song',     '宋',         5),
('dynasty', 'yuan',     '元',         6),
('dynasty', 'ming',     '明',         7),
('dynasty', 'qing',     '清',         8),
('dynasty', 'modern',   '近现代',     9),
-- 文化品类
('category', 'zhuzi',   '诸子文化',       1),
('category', 'feiyi',   '传统非遗',       2),
('category', 'minsu',   '民俗文化',       3),
('category', 'jiyi',    '传统技艺',       4),
('category', 'hongse',  '红色传统文化',   5),
('category', 'diangu',  '人文典故',       6),
('category', 'shici',   '诗词歌赋',       7),
('category', 'keji',    '古代科技',       8),
-- 思政学段
('education_level', 'primary_low',  '小学(1-3年级)',  1),
('education_level', 'primary_high', '小学(4-6年级)',  2),
('education_level', 'junior',       '初中',           3),
('education_level', 'high',         '高中',           4),
('education_level', 'college',      '大学',           5);
