-- ===================================================================
-- FAQ数据修复脚本
-- 功能：
-- 1. 清理重复的FAQ数据（保留最早记录）
-- 2. 为所有语言添加第3个问题："可以代办吗？"
-- ===================================================================

-- 1. 清理重复的FAQ（保留questionId最小的）
DELETE f1 FROM FAQ f1
INNER JOIN FAQ f2 
WHERE f1.QuestionID > f2.QuestionID
  AND f1.ItemID = f2.ItemID
  AND f1.Question = f2.Question
  AND f1.LanguageType = f2.LanguageType;

-- 2. 为所有语言添加第3个FAQ："可以代办吗？"

-- 蒙古语 (mn)
INSERT IGNORE INTO FAQ (ItemID, Question, Answer, LanguageType) VALUES
(1, 'Хуулийн үнэмлэхийг бусдаар хийлгэж болох уу?', 
 'Хуулийн үнэмлэх бүртгүүлэхдээ зарчмын хувьд өөрөө биечлэн очих шаардлагатай, гэхдээ онцгой тохиолдолд бусдаар хийлгэх боломжтой бөгөөд итгэмжлэл болон холбогдох нотлох баримт бичиг гаргах шаардлагатай.', 
 'mn');

-- 藏语 (bo)
INSERT IGNORE INTO FAQ (ItemID, Question, Answer, LanguageType) VALUES
(1, 'ངོས་འདེབས་ཤོག་ལེབ་གཞན་གྱིས་ཚབ་བྱས་ཆོག་གམ།', 
 'ངོས་འདེབས་ཤོག་ལེབ་གཏན་འཇགས་ལ་རང་གིས་ཐད་ཀར་འགྲོ་དགོས། དེ་ལ་ཐུན་མོང་གི་གནས་ཚུལ་ལ་གཞན་གྱིས་ཚབ་བྱེད་ཆོག་གི་ཡོད། དེ་ལ་ཆ་ཚན་གཞན་པའི་ཆ་ཚན་དང་ཆ་ཚན་གཞན་པའི་ཆ་ཚན་གཏན་འཇགས་དགོས།', 
 'bo');

-- 维吾尔语 (ug)
INSERT IGNORE INTO FAQ (ItemID, Question, Answer, LanguageType) VALUES
(1, 'كىملىك ھۆججىتىنى باشقىلار ۋاكالەتەن تەييارلاشقا بولامدۇ؟', 
 'كىملىك ھۆججىتى تەييارلاش ئەسلىدە ئۆزى ھازىر بولۇشى كېرەك، لېكىن ئالاھىدە ئەھۋاللاردا باشقىلارنىڭ ۋاكالىتىدە تەييارلاشقا بولىدۇ، ۋاكالەتنامە ۋە مۇناسىۋەتلىك گۇۋاھنامە ماتېرىياللىرىنى تەمىنلىشى كېرەك.', 
 'ug');

-- 哈萨克语 (kk)
INSERT IGNORE INTO FAQ (ItemID, Question, Answer, LanguageType) VALUES
(1, 'Жеке куәлікті басқа адам арқылы ресімдеуге бола ма?', 
 'Жеке куәлік ресімдеу қағидасы бойынша өзі келуі керек, бірақ ерекше жағдайларда басқа адамға сенімхат беріп ресімдеуге болады, сенімхат және қатысты құжаттар қажет.', 
 'kk');

-- 朝鲜语 (ko)
INSERT IGNORE INTO FAQ (ItemID, Question, Answer, LanguageType) VALUES
(1, '신분증을 대리 발급받을 수 있나요?', 
 '신분증 발급은 원칙적으로 본인이 직접 방문해야 하지만, 특별한 경우 타인에게 위임할 수 있으며, 위임장과 관련 증명 서류를 제출해야 합니다.', 
 'ko');

-- 彝语 (yi)
INSERT IGNORE INTO FAQ (ItemID, Question, Answer, LanguageType) VALUES
(1, 'ꀊꌧꄓꐨꌠꇁꇬꌠꑌꐥ？', 
 'ꀊꌧꄓꐨꊿꊇꌠꄿꃀꌺꌠ，ꆏꑍꐛꄚꅉꑟꎷꆏꑌꌠꇁꇬꌠꑌꐥ，ꄿꃀꌺꇁꇬꄸꅉꑟꐛꄚꋍꊇꌧ。', 
 'yi');

-- 壮语 (za)
INSERT IGNORE INTO FAQ (ItemID, Question, Answer, LanguageType) VALUES
(1, 'Ginhcwnz daizbinj dwg daengz goq lajmbwn ma?', 
 'Ginhcwnz daizbinj gangjdaeuj youq bonj gij cix bae, dangh youq dinghcaz ging\'ej dwg daengz goq lajmbwn bae, dwg gungh ginhcwnz daih goq lajmbwn ginhliengz.', 
 'za');

-- 回族语（阿拉伯语变体）(ar)
INSERT IGNORE INTO FAQ (ItemID, Question, Answer, LanguageType) VALUES
(1, 'هل يمكن لشخص آخر التقدم بطلب للحصول على بطاقة الهوية نيابة عني؟', 
 'من حيث المبدأ، يجب على الشخص نفسه التقدم للحصول على بطاقة الهوية، ولكن في ظروف خاصة يمكن تفويض شخص آخر، ويجب تقديم توكيل والمستندات ذات الصلة.', 
 'ar');

-- 土家语（使用汉字拼音）(tujia)
INSERT IGNORE INTO FAQ (ItemID, Question, Answer, LanguageType) VALUES
(1, '身份证办理可以代办吗？', 
 '身份证办理原则上需要本人亲自办理，但在特殊情况下可以委托他人代办，需要提供委托书和相关证明材料。', 
 'tujia');

-- 苗语 (hmn)
INSERT IGNORE INTO FAQ (ItemID, Question, Answer, LanguageType) VALUES
(1, 'Puas yog lwm tus tuaj yeem ua daim npav ID rau kuv?', 
 'Raws li txoj cai, koj yuav tsum tuaj ua daim npav ID koj tus kheej, tab sis nyob rau hauv cov xwm txheej tshwj xeeb koj tuaj yeem muab lub hwj chim rau lwm tus, yuav tsum muab daim ntawv tso cai thiab cov ntaub ntawv pov thawj.', 
 'hmn');

-- 侗语 (kam)
INSERT IGNORE INTO FAQ (ItemID, Question, Answer, LanguageType) VALUES
(1, '身份证办理可以代办吗？', 
 '身份证办理原则上需要本人亲自办理，但在特殊情况下可以委托他人代办，需要提供委托书和相关证明材料。', 
 'kam');

-- 瑶语 (yao)
INSERT IGNORE INTO FAQ (ItemID, Question, Answer, LanguageType) VALUES
(1, '身份证办理可以代办吗？', 
 '身份证办理原则上需要本人亲自办理，但在特殊情况下可以委托他人代办，需要提供委托书和相关证明材料。', 
 'yao');

-- 白族语 (bai)
INSERT IGNORE INTO FAQ (ItemID, Question, Answer, LanguageType) VALUES
(1, '身份证办理可以代办吗？', 
 '身份证办理原则上需要本人亲自办理，但在特殊情况下可以委托他人代办，需要提供委托书和相关证明材料。', 
 'bai');

-- 哈尼语 (hni)
INSERT IGNORE INTO FAQ (ItemID, Question, Answer, LanguageType) VALUES
(1, '身份证办理可以代办吗？', 
 '身份证办理原则上需要本人亲自办理，但在特殊情况下可以委托他人代办，需要提供委托书和相关证明材料。', 
 'hni');

-- 傣语 (khb)
INSERT IGNORE INTO FAQ (ItemID, Question, Answer, LanguageType) VALUES
(1, 'ᦵᦈᧁᧈᦌᦱᧃᧈᦋᦲᧈᧈᦂᦲᧃᧈᦍᦱᧃᧉᦌᦸᧂᧈᦂᧁᧉᦀᦸᧂᧉᦙᦱᧉ?', 
 'ᦵᦈᧁᧈᦌᦱᧃᧈᦋᦲᧈᧈᦂᦲᧃᧈᦍᦱᧃᧉᦌᦸᧂᧈᦵᦡᧃᧈᧉᦀᦸᧉᦘᦹᧈᦂᦸᧂᧈᦙᦱᧃᧉ，ᧈᦎᧂᧈᦓᦱᧂᧉᦀᦲᧃᧉᦋᦲᧉᧈᦂᦸᧉᧈᦗᦱᧃᧉᦂᦸᧃᧉᦙᦱᧃᧉ，ᦕᦸᧂᧉᦂᦸᧃᧉᦎᧁᧉᦵᦋᧁᧉᧈᦂᦸᧈᦂᦱᧂᧉ。', 
 'khb');

-- 黎语 (lic)
INSERT IGNORE INTO FAQ (ItemID, Question, Answer, LanguageType) VALUES
(1, '身份证办理可以代办吗？', 
 '身份证办理原则上需要本人亲自办理，但在特殊情况下可以委托他人代办，需要提供委托书和相关证明材料。', 
 'lic');

-- 傈僳语 (lis)
INSERT IGNORE INTO FAQ (ItemID, Question, Answer, LanguageType) VALUES
(1, '身份证办理可以代办吗？', 
 '身份证办理原则上需要本人亲自办理，但在特殊情况下可以委托他人代办，需要提供委托书和相关证明材料。', 
 'lis');

-- 满语 (mnc)
INSERT IGNORE INTO FAQ (ItemID, Question, Answer, LanguageType) VALUES
(1, '身份证办理可以代办吗？', 
 '身份证办理原则上需要本人亲自办理，但在特殊情况下可以委托他人代办，需要提供委托书和相关证明材料。', 
 'mnc');

-- 羌语 (qxs)
INSERT IGNORE INTO FAQ (ItemID, Question, Answer, LanguageType) VALUES
(1, '身份证办理可以代办吗？', 
 '身份证办理原则上需要本人亲自办理，但在特殊情况下可以委托他人代办，需要提供委托书和相关证明材料。', 
 'qxs');

-- 景颇语 (kac)
INSERT IGNORE INTO FAQ (ItemID, Question, Answer, LanguageType) VALUES
(1, '身份证办理可以代办吗？', 
 '身份证办理原则上需要本人亲自办理，但在特殊情况下可以委托他人代办，需要提供委托书和相关证明材料。', 
 'kac');

-- 验证清理结果
SELECT LanguageType, COUNT(*) as FAQ数量 
FROM FAQ 
WHERE ItemID = 1 
GROUP BY LanguageType 
ORDER BY LanguageType;

-- 显示每种语言的FAQ问题
SELECT LanguageType, Question 
FROM FAQ 
WHERE ItemID = 1 
ORDER BY LanguageType, QuestionID;

