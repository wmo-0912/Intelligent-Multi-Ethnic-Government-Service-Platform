-- 添加文件信息表和通知消息表
-- 可以直接在 MySQL 客户端或 IDEA 数据库工具中执行此脚本

USE govservice;

-- 文件信息表
CREATE TABLE IF NOT EXISTS FileInfo (
    FileID INT PRIMARY KEY AUTO_INCREMENT COMMENT '文件ID',
    FileName VARCHAR(255) NOT NULL COMMENT '原始文件名',
    StoragePath VARCHAR(500) NOT NULL COMMENT '存储路径',
    FileType VARCHAR(100) NOT NULL COMMENT '文件类型(MIME)',
    FileSize BIGINT NOT NULL COMMENT '文件大小(字节)',
    UploadUserId INT NOT NULL COMMENT '上传用户ID',
    RelatedRecordId INT COMMENT '关联办理记录ID',
    FileCategory VARCHAR(50) COMMENT '文件分类(ID_CARD/PROOF/OTHER)',
    UploadTime DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间',
    Deleted BOOLEAN DEFAULT FALSE COMMENT '逻辑删除标记',
    FOREIGN KEY (UploadUserId) REFERENCES User(UserID) ON DELETE CASCADE,
    FOREIGN KEY (RelatedRecordId) REFERENCES HandlingRecord(RecordID) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文件信息表';

-- 通知消息表
CREATE TABLE IF NOT EXISTS Notification (
    NotificationID INT PRIMARY KEY AUTO_INCREMENT COMMENT '通知ID',
    UserId INT NOT NULL COMMENT '接收用户ID',
    Title VARCHAR(200) NOT NULL COMMENT '通知标题',
    Content VARCHAR(1000) NOT NULL COMMENT '通知内容',
    NotifyType VARCHAR(50) NOT NULL COMMENT '通知类型(APPLICATION_STATUS/SYSTEM/ANNOUNCEMENT)',
    RelatedRecordId INT COMMENT '关联办理记录ID',
    IsRead BOOLEAN DEFAULT FALSE COMMENT '是否已读',
    CreateTime DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    ReadTime DATETIME COMMENT '阅读时间',
    FOREIGN KEY (UserId) REFERENCES User(UserID) ON DELETE CASCADE,
    FOREIGN KEY (RelatedRecordId) REFERENCES HandlingRecord(RecordID) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='通知消息表';

-- 创建索引
-- FileInfo(UploadUserId)
SET @idx_exists := (
  SELECT COUNT(*) FROM information_schema.statistics
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'FileInfo' AND INDEX_NAME = 'idx_file_upload_user'
);
SET @sql := IF(@idx_exists=0, 'CREATE INDEX idx_file_upload_user ON FileInfo(UploadUserId)', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- FileInfo(RelatedRecordId)
SET @idx_exists := (
  SELECT COUNT(*) FROM information_schema.statistics
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'FileInfo' AND INDEX_NAME = 'idx_file_record'
);
SET @sql := IF(@idx_exists=0, 'CREATE INDEX idx_file_record ON FileInfo(RelatedRecordId)', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- Notification(UserId)
SET @idx_exists := (
  SELECT COUNT(*) FROM information_schema.statistics
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'Notification' AND INDEX_NAME = 'idx_notification_user'
);
SET @sql := IF(@idx_exists=0, 'CREATE INDEX idx_notification_user ON Notification(UserId)', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- Notification(IsRead)
SET @idx_exists := (
  SELECT COUNT(*) FROM information_schema.statistics
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'Notification' AND INDEX_NAME = 'idx_notification_read'
);
SET @sql := IF(@idx_exists=0, 'CREATE INDEX idx_notification_read ON Notification(IsRead)', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 验证表是否创建成功
SELECT 'FileInfo 表创建成功' AS Result, COUNT(*) AS RecordCount FROM FileInfo
UNION ALL
SELECT 'Notification 表创建成功' AS Result, COUNT(*) AS RecordCount FROM Notification;

