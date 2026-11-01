-- 检查多语言数据是否存在

USE govservice;

-- 1. 查看所有语言的数量
SELECT LanguageType, COUNT(*) as count
FROM MultilingualInfo
GROUP BY LanguageType
ORDER BY count DESC;

-- 2. 查看ItemID=1（身份证办理）的所有语言版本
SELECT * FROM MultilingualInfo WHERE ItemID = 1 ORDER BY LanguageType;

-- 3. 检查特定语言是否存在（蒙古语）
SELECT * FROM MultilingualInfo WHERE ItemID = 1 AND LanguageType = 'mn';

-- 4. 检查特定语言是否存在（维语）
SELECT * FROM MultilingualInfo WHERE ItemID = 1 AND LanguageType = 'ug';

-- 5. 检查瑶语是否存在
SELECT * FROM MultilingualInfo WHERE ItemID = 1 AND LanguageType = 'yao';

-- 6. 查看总记录数
SELECT COUNT(*) as total_records FROM MultilingualInfo;

