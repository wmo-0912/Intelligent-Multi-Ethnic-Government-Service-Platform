# FAQ数据清理和补充说明

## 当前问题
1. FAQ数据有重复（4组相同数据，不同时间戳）
2. 除中文外，其他语言只有2个问题，缺少第3个问题"可以代办吗？"

## 解决方案

### 方法1：在IDEA中通过Database工具执行（推荐）

1. 打开IDEA的Database工具
2. 连接到govservice数据库
3. 在Query Console中依次执行以下SQL

#### 步骤1：清理重复数据（保留最早的记录）
```sql
-- 删除重复的FAQ，只保留QuestionID最小的
DELETE f1 FROM FAQ f1
INNER JOIN FAQ f2 
WHERE f1.QuestionID > f2.QuestionID
  AND f1.ItemID = f2.ItemID
  AND f1.Question = f2.Question
  AND f1.LanguageType = f2.LanguageType;
```

#### 步骤2：添加缺失的第3个问题
```sql
-- 蒙古语
INSERT IGNORE INTO FAQ (ItemID, Question, Answer, LanguageType) VALUES
(1, 'Хуулийн үнэмлэхийг бусдаар хийлгэж болох уу?', 
 'Хуулийн үнэмлэх бүртгүүлэхдээ зарчмын хувьд өөрөө биечлэн очих шаардлагатай, гэхдээ онцгой тохиолдолд бусдаар хийлгэх боломжтой бөгөөд итгэмжлэл болон холбогдох нотлох баримт бичиг гаргах шаардлагатай.', 
 'mn');

-- 藏语
INSERT IGNORE INTO FAQ (ItemID, Question, Answer, LanguageType) VALUES
(1, 'ངོས་འདེབས་ཤོག་ལེབ་གཞན་གྱིས་ཚབ་བྱས་ཆོག་གམ།', 
 'ངོས་འདེབས་ཤོག་ལེབ་གཏན་འཇགས་ལ་རང་གིས་ཐད་ཀར་འགྲོ་དགོས། ཐུན་མོང་མིན་པའི་གནས་ཚུལ་ལ་གཞན་གྱིས་ཚབ་བྱེད་ཆོག གཞན་གྱིས་ཚབ་བྱེད་ཡིག་ཆ་དང་ཆ་འཕྲིན་བཀོད་དགོས།', 
 'bo');

-- 维吾尔语
INSERT IGNORE INTO FAQ (ItemID, Question, Answer, LanguageType) VALUES
(1, 'كىملىك ھۆججىتىنى باشقىلار ۋاكالەتەن تەييارلاشقا بولامدۇ؟', 
 'كىملىك ھۆججىتى تەييارلاش ئەسلىدە ئۆزى ھازىر بولۇشى كېرەك، لېكىن ئالاھىدە ئەھۋاللاردا باشقىلارنىڭ ۋاكالىتىدە تەييارلاشقا بولىدۇ، ۋاكالەتنامە ۋە مۇناسىۋەتلىك گۇۋاھنامە ماتېرىياللىرىنى تەمىنلىشى كېرەك.', 
 'ug');

-- 哈萨克语
INSERT IGNORE INTO FAQ (ItemID, Question, Answer, LanguageType) VALUES
(1, 'Жеке куәлікті басқа адам арқылы ресімдеуге бола ма?', 
 'Жеке куәлік ресімдеу қағидасы бойынша өзі келуі керек, бірақ ерекше жағдайларда басқа адамға сенімхат беріп ресімдеуге болады, сенімхат және қатысты құжаттар қажет.', 
 'kk');

-- 朝鲜语
INSERT IGNORE INTO FAQ (ItemID, Question, Answer, LanguageType) VALUES
(1, '신분증을 대리 발급받을 수 있나요?', 
 '신분증 발급은 원칙적으로 본인이 직접 방문해야 하지만, 특별한 경우 타인에게 위임할 수 있으며, 위임장과 관련 증명 서류를 제출해야 합니다.', 
 'ko');
```

#### 步骤3：验证结果
```sql
-- 查看每种语言的FAQ数量
SELECT LanguageType, COUNT(*) as FAQ数量 
FROM FAQ 
WHERE ItemID = 1 
GROUP BY LanguageType 
ORDER BY LanguageType;
```

**预期结果**：每种语言都应该有3条FAQ

### 方法2：使用命令行（如果MySQL命令行可用）

```bash
mysql -u root -pqaz123456 govservice < src/main/resources/sql/fix_faq_simple.sql
```

## 完成后的状态
- 每种语言：3个问题
- 总计：6种语言 × 3个问题 = 18条FAQ
- 无重复数据

## 测试验证
修复完成后，重新测试哈萨克语FAQ搜索，应该返回3个问题而不是2个。

