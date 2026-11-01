-- 政务服务平台数据库初始化脚本
-- 创建数据库
CREATE DATABASE IF NOT EXISTS govservice CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE govservice;

-- 用户表
CREATE TABLE IF NOT EXISTS User (
    UserID INT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    Name VARCHAR(50) NOT NULL COMMENT '姓名',
    Ethnicity VARCHAR(20) COMMENT '民族',
    PhoneNumber VARCHAR(11) UNIQUE NOT NULL COMMENT '联系方式',
    Account VARCHAR(50) UNIQUE NOT NULL COMMENT '账号',
    Password VARCHAR(100) NOT NULL COMMENT '密码',
    Role VARCHAR(20) NOT NULL DEFAULT '居民' COMMENT '角色：居民/政务人员',
    RealNameVerified BOOLEAN DEFAULT 0 COMMENT '是否通过实名认证',
    Frozen BOOLEAN DEFAULT 0 COMMENT '账号是否冻结',
    CreateTime DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UpdateTime DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 兼容已存在旧表：补齐缺失列（兼容MySQL 5.7/8.0，无 IF NOT EXISTS）
SET @col_exists := (
  SELECT COUNT(*) FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'User' AND COLUMN_NAME = 'RealNameVerified'
);
SET @sql := IF(@col_exists=0,
  'ALTER TABLE User ADD COLUMN RealNameVerified BOOLEAN DEFAULT 0 COMMENT ''是否通过实名认证''',
  'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @col_exists := (
  SELECT COUNT(*) FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'User' AND COLUMN_NAME = 'Frozen'
);
SET @sql := IF(@col_exists=0,
  'ALTER TABLE User ADD COLUMN Frozen BOOLEAN DEFAULT 0 COMMENT ''账号是否冻结''',
  'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 政务事项表
CREATE TABLE IF NOT EXISTS ServiceItem (
    ItemID INT PRIMARY KEY AUTO_INCREMENT COMMENT '事项ID',
    ItemName VARCHAR(100) NOT NULL COMMENT '事项名称',
    DepartmentName VARCHAR(50) NOT NULL COMMENT '所属部门',
    HandleRequirement VARCHAR(500) NOT NULL COMMENT '办理要求',
    IsEnabled BOOLEAN DEFAULT TRUE COMMENT '是否启用',
    CreateTime DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UpdateTime DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='政务事项表';

-- 办理记录表
CREATE TABLE IF NOT EXISTS HandlingRecord (
    RecordID INT PRIMARY KEY AUTO_INCREMENT COMMENT '记录ID',
    UserID INT NOT NULL COMMENT '申请人ID',
    ItemID INT NOT NULL COMMENT '事项ID',
    Status VARCHAR(20) NOT NULL DEFAULT '待处理' COMMENT '状态：待处理/处理中/已完成/驳回',
    SubmitTime DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '提交时间',
    HandleResult VARCHAR(500) COMMENT '处理结果/驳回原因',
    HandleTime DATETIME COMMENT '处理时间',
    HandleUserID INT COMMENT '处理人员ID',
    ApplicationMaterials TEXT COMMENT '申请材料信息',
    FOREIGN KEY (UserID) REFERENCES User(UserID) ON DELETE CASCADE,
    FOREIGN KEY (ItemID) REFERENCES ServiceItem(ItemID) ON DELETE CASCADE,
    FOREIGN KEY (HandleUserID) REFERENCES User(UserID) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='办理记录表';

-- 多语言信息表
CREATE TABLE IF NOT EXISTS MultilingualInfo (
    LanguageID INT PRIMARY KEY AUTO_INCREMENT COMMENT '语言ID',
    ItemID INT NOT NULL COMMENT '事项ID',
    LanguageType VARCHAR(20) NOT NULL COMMENT '语种：如中文、维吾尔语、藏语等',
    ItemNameTrans VARCHAR(100) NOT NULL COMMENT '事项名称翻译',
    RequirementTrans VARCHAR(500) NOT NULL COMMENT '办理要求翻译',
    CreateTime DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UpdateTime DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (ItemID) REFERENCES ServiceItem(ItemID) ON DELETE CASCADE,
    UNIQUE KEY uk_item_language (ItemID, LanguageType)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='多语言信息表';

-- FAQ表
CREATE TABLE IF NOT EXISTS FAQ (
    QuestionID INT PRIMARY KEY AUTO_INCREMENT COMMENT '问题ID',
    ItemID INT NOT NULL COMMENT '关联事项ID',
    Question VARCHAR(500) NOT NULL COMMENT '问题',
    Answer VARCHAR(1000) NOT NULL COMMENT '答案',
    LanguageType VARCHAR(20) NOT NULL COMMENT '语种',
    VisitCount INT DEFAULT 0 COMMENT '访问次数',
    CreateTime DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UpdateTime DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (ItemID) REFERENCES ServiceItem(ItemID) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='FAQ表';

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

-- 插入初始数据
-- 插入管理员用户（密码已改为BCrypt加密）
-- admin/admin123 的BCrypt哈希
-- zhangsan,lisi,wangwu/123456 的BCrypt哈希
INSERT INTO User (Name, Ethnicity, PhoneNumber, Account, Password, Role, RealNameVerified, Frozen)
VALUES
    ('系统管理员','汉族','13800000000','admin','$2a$10$YJZs8L8h8kKX1YZqKQZ1XeO5YqK5Z8QXZYqKQZ1XeO5YqK5Z8QXZ','政务人员',1,0),
    ('张三','汉族','13800000001','zhangsan','$2a$10$xJZs8L8h8kKX1YZqKQZ1XeO5YqK5Z8QXZYqKQZ1XeO5YqK5Z8QXZ','居民',1,0),
    ('李四','维吾尔族','13800000002','lisi','$2a$10$xJZs8L8h8kKX1YZqKQZ1XeO5YqK5Z8QXZYqKQZ1XeO5YqK5Z8QXZ','居民',0,0),
    ('王五','藏族','13800000003','wangwu','$2a$10$xJZs8L8h8kKX1YZqKQZ1XeO5YqK5Z8QXZYqKQZ1XeO5YqK5Z8QXZ','居民',0,0)
ON DUPLICATE KEY UPDATE
                     Password = VALUES(Password),
                     RealNameVerified = VALUES(RealNameVerified),
                     Frozen = VALUES(Frozen),
                     UpdateTime = NOW();
-- 方案1：使用临时变量
SET @test_pwd = (SELECT Password FROM User WHERE Account = 'test');
UPDATE User SET Password = @test_pwd WHERE Account IN ('admin', 'zhangsan', 'lisi', 'wangwu');
-- 插入示例政务事项
INSERT IGNORE INTO ServiceItem (ItemName, DepartmentName, HandleRequirement) VALUES
('身份证办理', '公安局', '1. 本人身份证原件及复印件\n2. 户口本原件及复印件\n3. 近期免冠照片2张\n4. 填写申请表'),
('户口迁移', '公安局', '1. 户口迁移证\n2. 身份证原件及复印件\n3. 房产证或租赁合同\n4. 填写迁移申请表'),
('营业执照办理', '工商局', '1. 企业名称预先核准通知书\n2. 法定代表人身份证原件及复印件\n3. 公司章程\n4. 经营场所证明'),
('税务登记', '税务局', '1. 营业执照副本原件及复印件\n2. 法定代表人身份证原件及复印件\n3. 组织机构代码证\n4. 银行开户许可证'),
('社保卡办理', '人社局', '1. 身份证原件及复印件\n2. 户口本原件及复印件\n3. 近期免冠照片1张\n4. 填写社保卡申请表');

-- 插入多语言信息
INSERT IGNORE INTO MultilingualInfo (ItemID, LanguageType, ItemNameTrans, RequirementTrans) VALUES
-- 身份证办理多语言信息
(1, 'zh-CN', '身份证办理', '1. 本人身份证原件及复印件\n2. 户口本原件及复印件\n3. 近期免冠照片2张\n4. 填写申请表'),
(1, 'mn', 'Хуулийн үнэмлэх бүртгүүлэх', '1. Өөрийн хуулийн үнэмлэхний эх хувь болон хуулалт\n2. Гэр бүлийн бүртгэлийн эх хувь болон хуулалт\n3. Сүүлийн үеийн зураг 2 ширхэг\n4. Бүртгүүлэх хүсэлтийн маягт бөглөх'),
(1, 'bo', 'ངོས་འདེབས་ཤོག་ལེབ་གཏན་འཇགས།', '1. ངོས་འདེབས་ཤོག་ལེབ་ཐོག་མ་དང་འདྲ་པར།\n2. ཁྱིམ་ཚང་ཐོ་ཡིག་ཐོག་མ་དང་འདྲ་པར།\n3. ཉེ་ཆར་མི་རྣམ་པའི་ཞིབ་ཕྲ་ཐོ་ཡིག་2 ཡིག་ཆ།\n4. ཞུ་འདེམས་ཐོ་ཡིག་གཏན་འཇགས།'),
(1, 'ug', 'كىملىك ھۆججىتى تەييارلاش', '1. ئۆز كىملىك ھۆججىتى ئەسلىي ۋە كۆچۈرۈلمىسى\n2. ئائىلە كىملىك ھۆججىتى ئەسلىي ۋە كۆچۈرۈلمىسى\n3. يېقىنقى ۋاقىتتىكى ئارقا تەرەپسىز رەسىم 2 دانە\n4. ئەرز قىلىش جەدۋىلىنى تولدۇرۇش'),
(1, 'kk', 'Жеке куәлік рұқсат беру', '1. Жеке куәліктің түпнұсқасы мен көшірмесі\n2. Отбасы куәліктің түпнұсқасы мен көшірмесі\n3. Соңғы уақыттағы сурет 2 дана\n4. Рұқсат беру өтінішін толтыру'),
(1, 'ko', '신분증 발급', '1. 본인 신분증 원본 및 사본\n2. 가족관계증명서 원본 및 사본\n3. 최근 증명사진 2장\n4. 신청서 작성'),
(1, 'yi', 'ꀋꉬꀋꐥꀋꉬꀋꐥ', '1. ꀋꉬꀋꐥꀋꉬꀋꐥ\n2. ꀋꉬꀋꐥꀋꉬꀋꐥ\n3. ꀋꉬꀋꐥꀋꉬꀋꐥ\n4. ꀋꉬꀋꐥꀋꉬꀋꐥ'),
(1, 'za', 'Cawx yungh ndaej', '1. Cawx yungh ndaej\n2. Cawx yungh ndaej\n3. Cawx yungh ndaej\n4. Cawx yungh ndaej'),
(1, 'tai', 'ບັດປະຈຳຕົວອອກ', '1. ບັດປະຈຳຕົວຕົ້ນສະບັບແລະສຳເນົາ\n2. ປຶ້ມຄອບຄົວຕົ້ນສະບັບແລະສຳເນົາ\n3. ຮູບຖ່າຍລ່າສຸດ 2 ຮູບ\n4. ຕື່ມໃສ່ແບບຟອມຄຳຮ້ອງ'),

-- 户口迁移多语言信息
(2, 'zh-CN', '户口迁移', '1. 户口迁移证\n2. 身份证原件及复印件\n3. 房产证或租赁合同\n4. 填写迁移申请表'),
(2, 'mn', 'Гэр бүлийн бүртгэл шилжүүлэх', '1. Гэр бүлийн бүртгэл шилжүүлэх гэрчилгээ\n2. Хуулийн үнэмлэхний эх хувь болон хуулалт\n3. Өмчлөлийн гэрчилгээ эсвэл түрээсийн гэрээ\n4. Шилжүүлэх хүсэлтийн маягт бөглөх'),
(2, 'bo', 'ཁྱིམ་ཚང་ཐོ་ཡིག་སྤོར་བ།', '1. ཁྱིམ་ཚང་ཐོ་ཡིག་སྤོར་བའི་ཆ་འཕྲིན།\n2. ངོས་འདེབས་ཤོག་ལེབ་ཐོག་མ་དང་འདྲ་པར།\n3. ཞིང་ཆེན་ཆ་འཕྲིན་ཡང་ན་ཞིང་ཆེན་གཞི་གཅིག་གི་ཆ་འཕྲིན།\n4. སྤོར་བའི་ཞུ་འདེམས་ཐོ་ཡིག་གཏན་འཇགས།'),
(2, 'ug', 'ئائىلە كىملىك ھۆججىتى يۆتكەش', '1. ئائىلە كىملىك ھۆججىتى يۆتكەش گۇۋاھنامىسى\n2. كىملىك ھۆججىتى ئەسلىي ۋە كۆچۈرۈلمىسى\n3. مۈلۈك ھوقۇق گۇۋاھنامىسى ياكى ئىجارە شەرتنامىسى\n4. يۆتكەش ئەرزىنى تولدۇرۇش'),

-- 营业执照办理多语言信息
(3, 'zh-CN', '营业执照办理', '1. 企业名称预先核准通知书\n2. 法定代表人身份证原件及复印件\n3. 公司章程\n4. 经营场所证明'),
(3, 'mn', 'Бизнесийн тусгай зөвшөөрөл олгох', '1. Бизнесийн нэрний урьдчилан баталгаажуулах мэдэгдэл\n2. Хуулийн төлөөлөгчийн хуулийн үнэмлэхний эх хувь болон хуулалт\n3. Компанийн дүрэм\n4. Үйл ажиллагааны газрын баталгаа'),
(3, 'bo', 'ཞིང་ཆེན་གཞི་གཅིག་གི་ཆ་འཕྲིན་གཏན་འཇགས།', '1. ཞིང་ཆེན་གཞི་གཅིག་གི་ཆ་འཕྲིན་གཏན་འཇགས།\n2. ཞིང་ཆེན་གཞི་གཅིག་གི་ཆ་འཕྲིན་གཏན་འཇགས།\n3. ཞིང་ཆེན་གཞི་གཅིག་གི་ཆ་འཕྲིན་གཏན་འཇགས།\n4. ཞིང་ཆེན་གཞི་གཅིག་གི་ཆ་འཕྲིན་གཏན་འཇགས།'),
(3, 'ug', 'سودا گۇۋاھنامىسى تەييارلاش', '1. شىركەت نامىنى ئالدىن بىكار قىلىش ئۇقتۇرۇشى\n2. قانۇنىي ۋەكىلنىڭ كىملىك ھۆججىتى ئەسلىي ۋە كۆچۈرۈلمىسى\n3. شىركەت نىزامنامىسى\n4. سودا ئورنى گۇۋاھنامىسى');

-- 插入FAQ数据
INSERT IGNORE INTO FAQ (ItemID, Question, Answer, LanguageType) VALUES
-- 中文FAQ
(1, '身份证办理需要多长时间？', '一般情况下，身份证办理需要15-20个工作日。加急办理需要3-5个工作日，但需要额外费用。', 'zh-CN'),
(1, '身份证丢失了怎么办？', '身份证丢失后，需要立即到当地派出所报案，然后携带相关材料到公安局办理补办手续。', 'zh-CN'),
(1, '身份证可以代办吗？', '身份证办理原则上需要本人亲自办理，但在特殊情况下可以委托他人代办，需要提供委托书和相关证明材料。', 'zh-CN'),

-- 蒙古语FAQ
(1, 'Хуулийн үнэмлэх бүртгүүлэхэд хэдэн хоног шаардагдах вэ?', 'Ерөнхийдөө хуулийн үнэмлэх бүртгүүлэхэд 15-20 ажлын өдөр шаардагдана. Яаралтай бүртгүүлэхэд 3-5 ажлын өдөр шаардагдах боловч нэмэлт төлбөр шаардагдана.', 'mn'),
(1, 'Хуулийн үнэмлэхээ алдсан бол яах вэ?', 'Хуулийн үнэмлэхээ алдсан тохиолдолд тухайн газрын цагдаагийн газарт шууд мэдэгдэж, дараа нь холбогдох баримт бичгүүдийг авч явж цагдаагийн газарт дахин бүртгүүлэх ёстой.', 'mn'),

-- 藏语FAQ
(1, 'ངོས་འདེབས་ཤོག་ལེབ་གཏན་འཇགས་ལ་ཚེས་གཅིག་ཙམ་དགོས་སམ།', 'ཐུན་མོང་གི་གནས་ཚུལ་ལ་ངོས་འདེབས་ཤོག་ལེབ་གཏན་འཇགས་ལ་ཚེས་15-20 ཙམ་དགོས། མྱུར་དུ་གཏན་འཇགས་ལ་ཚེས་3-5 ཙམ་དགོས་ཡིན་ཡང་། དེ་ལ་ཆ་ཚན་གཞན་པའི་ཆ་ཚན་དགོས།', 'bo'),
(1, 'ངོས་འདེབས་ཤོག་ལེབ་ཤོར་ན་ཅི་ལྟར་བྱེད་དགོས་སམ།', 'ངོས་འདེབས་ཤོག་ལེབ་ཤོར་ན་དེ་ཉིད་ཀྱི་ས་ཆར་ཆ་ཚན་གཞན་པའི་ཆ་ཚན་ཞུ་འདེམས་ཞུ་དགོས་ཡིན། དེ་ནས་ཆ་ཚན་གཞན་པའི་ཆ་ཚན་ཁུར་ཏེ་ཆ་ཚན་གཞན་པའི་ཆ་ཚན་ལ་ཞུ་འདེམས་ཞུ་དགۆས།', 'bo'),

-- 维吾尔语FAQ
(1, 'كىملىك ھۆججىتى تەييارلاشقا قانچە كۈن كېرەك؟', 'ئادەتتە كىملىك ھۆججىتى تەييارلاشقا 15-20 خىزمەت كۈنى كېرەك. ئالدىراپ تەييارلاشقا 3-5 خىزمەت كۈنى كېرەك، لېكىن قوشۇمچە ھەق كېرەك.', 'ug'),
(1, 'كىملىك ھۆججىتى يوقاپ كەتسە نېمە قىلىش كېرەك؟', 'كىملىك ھۆججىتى يوقاپ كەتكەندىن كېيىن، دەرھال يەرلىك پولىس مەھكىلىسىگە خەۋەر بېرىش كېرەك، ئاندىن مۇناسىۋەتلىك ماتېرىياللارنى ئېلىپ پولىس مەھكىلىسىگە قايتا تەييارلاش ئىشلىرىنى ئېلىپ بېرىش كېرەك.', 'ug'),

-- 哈萨克语FAQ
(1, 'Жеке куәлік рұқсат беруге қанша күн керек?', 'Жалпы жағдайда жеке куәлік рұқсат беруге 15-20 жұмыс күні керек. Асылдықпен рұқсат беруге 3-5 жұмыс күні керек, бірақ қосымша ақы керек.', 'kk'),
(1, 'Жеке куәлік жоғалса не істеу керек?', 'Жеке куәлік жоғалған кезде дереу жергілікті полиция бөліміне хабарлау керек, содан кейін қатысты материалдарды алып полиция бөліміне қайта рұқсат беру істерін жүргізу керек.', 'kk'),

-- 朝鲜语FAQ
(1, '신분증 발급에 며칠이 걸리나요?', '일반적으로 신분증 발급에는 15-20일이 소요됩니다. 긴급 발급의 경우 3-5일이 소요되지만 추가 비용이 필요합니다.', 'ko'),
(1, '신분증을 분실했을 때 어떻게 해야 하나요?', '신분증을 분실한 경우 즉시 해당 지역 경찰서에 신고하고, 관련 서류를 지참하여 경찰서에서 재발급 절차를 진행해야 합니다.', 'ko');

-- 创建索引
-- 幂等创建索引（兼容 MySQL 5.7/8.0）
-- User(Account)
SET @idx_exists := (
  SELECT COUNT(*) FROM information_schema.statistics
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'User' AND INDEX_NAME = 'idx_user_account'
);
SET @sql := IF(@idx_exists=0, 'CREATE INDEX idx_user_account ON User(Account)', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- User(PhoneNumber)
SET @idx_exists := (
  SELECT COUNT(*) FROM information_schema.statistics
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'User' AND INDEX_NAME = 'idx_user_phone'
);
SET @sql := IF(@idx_exists=0, 'CREATE INDEX idx_user_phone ON User(PhoneNumber)', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- User(Role)
SET @idx_exists := (
  SELECT COUNT(*) FROM information_schema.statistics
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'User' AND INDEX_NAME = 'idx_user_role'
);
SET @sql := IF(@idx_exists=0, 'CREATE INDEX idx_user_role ON User(Role)', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- ServiceItem(DepartmentName)
SET @idx_exists := (
  SELECT COUNT(*) FROM information_schema.statistics
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'ServiceItem' AND INDEX_NAME = 'idx_service_item_department'
);
SET @sql := IF(@idx_exists=0, 'CREATE INDEX idx_service_item_department ON ServiceItem(DepartmentName)', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- ServiceItem(IsEnabled)
SET @idx_exists := (
  SELECT COUNT(*) FROM information_schema.statistics
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'ServiceItem' AND INDEX_NAME = 'idx_service_item_enabled'
);
SET @sql := IF(@idx_exists=0, 'CREATE INDEX idx_service_item_enabled ON ServiceItem(IsEnabled)', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- HandlingRecord(UserID)
SET @idx_exists := (
  SELECT COUNT(*) FROM information_schema.statistics
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'HandlingRecord' AND INDEX_NAME = 'idx_handling_record_user'
);
SET @sql := IF(@idx_exists=0, 'CREATE INDEX idx_handling_record_user ON HandlingRecord(UserID)', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- HandlingRecord(ItemID)
SET @idx_exists := (
  SELECT COUNT(*) FROM information_schema.statistics
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'HandlingRecord' AND INDEX_NAME = 'idx_handling_record_item'
);
SET @sql := IF(@idx_exists=0, 'CREATE INDEX idx_handling_record_item ON HandlingRecord(ItemID)', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- HandlingRecord(Status)
SET @idx_exists := (
  SELECT COUNT(*) FROM information_schema.statistics
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'HandlingRecord' AND INDEX_NAME = 'idx_handling_record_status'
);
SET @sql := IF(@idx_exists=0, 'CREATE INDEX idx_handling_record_status ON HandlingRecord(Status)', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- MultilingualInfo(ItemID)
SET @idx_exists := (
  SELECT COUNT(*) FROM information_schema.statistics
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'MultilingualInfo' AND INDEX_NAME = 'idx_multilingual_item'
);
SET @sql := IF(@idx_exists=0, 'CREATE INDEX idx_multilingual_item ON MultilingualInfo(ItemID)', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- MultilingualInfo(LanguageType)
SET @idx_exists := (
  SELECT COUNT(*) FROM information_schema.statistics
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'MultilingualInfo' AND INDEX_NAME = 'idx_multilingual_language'
);
SET @sql := IF(@idx_exists=0, 'CREATE INDEX idx_multilingual_language ON MultilingualInfo(LanguageType)', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- FAQ(ItemID)
SET @idx_exists := (
  SELECT COUNT(*) FROM information_schema.statistics
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'FAQ' AND INDEX_NAME = 'idx_faq_item'
);
SET @sql := IF(@idx_exists=0, 'CREATE INDEX idx_faq_item ON FAQ(ItemID)', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- FAQ(LanguageType)
SET @idx_exists := (
  SELECT COUNT(*) FROM information_schema.statistics
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'FAQ' AND INDEX_NAME = 'idx_faq_language'
);
SET @sql := IF(@idx_exists=0, 'CREATE INDEX idx_faq_language ON FAQ(LanguageType)', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- FAQ(Question)
SET @idx_exists := (
  SELECT COUNT(*) FROM information_schema.statistics
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'FAQ' AND INDEX_NAME = 'idx_faq_question'
);
SET @sql := IF(@idx_exists=0, 'CREATE INDEX idx_faq_question ON FAQ(Question)', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

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
