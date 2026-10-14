-- 为所有少数民族语言添加多语言翻译数据
-- 涵盖所有 properties 文件中的语言

USE govservice;

-- 清空现有的多语言数据（可选，如果需要重新初始化）
-- DELETE FROM MultilingualInfo WHERE LanguageType != 'zh-CN';

-- =============================================
-- 身份证办理 (ItemID = 1)
-- =============================================

INSERT INTO MultilingualInfo (ItemID, LanguageType, ItemNameTrans, RequirementTrans) VALUES
-- 中文（已有）
(1, 'zh-CN', '身份证办理', '1. 本人身份证原件及复印件\n2. 户口本原件及复印件\n3. 近期免冠照片2张\n4. 填写申请表'),

-- 维吾尔语
(1, 'ug', 'كىملىك ھۆججىتى تەييارلاش', '1. ئۆز كىملىك ھۆججىتى ئەسلىي ۋە كۆچۈرۈلمىسى\n2. ئائىلە دەپتىرى ئەسلىي ۋە كۆچۈرۈلمىسى\n3. يېقىنقى ۋاقىتتىكى رەسىم 2 پارچە\n4. ئەرز قىلىش جەدۋىلىنى تولدۇرۇش'),

-- 藏语
(1, 'bo', 'ངོ་རྟགས་ཤོག་ལེབ་གཏན་འབེབས།', '1. རང་ཉིད་ཀྱི་ངོ་རྟགས་ཤོག་ལེབ་དངོས་དང་འདྲ་པར།\n2. ཁྱིམ་ཚང་དེབ་དངོས་དང་འདྲ་པར།\n3. ཉེ་ཆར་གྱི་པར་2 ཤོག\n4. ཞུ་འདེབས་ཡིག་ཆ་འགོད་པ།'),

-- 蒙古语
(1, 'mn', 'Хувийн үнэмлэх бүртгэл', '1. Өөрийн хувийн үнэмлэхний эх хувь, хуулбар\n2. Гэрийн бүртгэлийн эх хувь, хуулбар\n3. Саяхан авсан зураг 2 ширхэг\n4. Өргөдлийн маягт бөглөх'),

-- 哈萨克语
(1, 'kk', 'Жеке куәлік ресімдеу', '1. Өзінің жеке куәлігінің түпнұсқасы мен көшірмесі\n2. Үй тіркеу кітапшасының түпнұсқасы мен көшірмесі\n3. Жақында түсірілген сурет 2 дана\n4. Өтініш парағын толтыру'),

-- 朝鲜语
(1, 'ko', '신분증 발급', '1. 본인 신분증 원본 및 사본\n2. 가족 등록부 원본 및 사본\n3. 최근 촬영한 사진 2장\n4. 신청서 작성'),

-- 彝语
(1, 'yi', 'ꀊꐙꉆꌺꄡꉻ', '1. ꀊꉬꀊꐙꉆꌺꃀꄡꌐꀕꑌꂷ\n2. ꀊꉬꀉꈨꉆꌺꃀꄡꌐꀕꑌꂷ\n3. ꀋꉙꊇꇖꄡꁧꂷ2ꈜ\n4. ꃅꎆꄡꁧꉆꌺ'),

-- 壮语
(1, 'za', 'Cawx youq ndei bae', '1. Cawx youq ndei bae bae doenghsim caeux cih\n2. Cawx youq ndei bae bae doenghsim caeux cih\n3. Ranz saw 2 changj\n4. Doenghsim caeux cih'),

-- 瑶语
(1, 'yao', 'Ghab dud dangl', '1. Naix ghab dud dangl jul gai hsongd\n2. Naix ghab hxud dangl jul gai hsongd\n3. Naix hsongd 2 niangb\n4. Naix hsongd dangl'),

-- 苗语
(1, 'hmn', 'Daim ntawv pov thawj', '1. Koj daim ntawv pov thawj qub thiab luam\n2. Tsev neeg phau ntawv qub thiab luam\n3. Duab tshiab 2 daim\n4. Sau daim ntawv thov'),

-- 布依语
(1, 'bgc', 'Gangx yix ndaej', '1. Cawx gangx yix ndaej bae caeux cih\n2. Cawx gangx yix ndaej bae caeux cih\n3. Ranz saw 2 changj\n4. Doenghsim caeux cih'),

-- 白语
(1, 'bai', '身份证办理', '1. 自己身份证原件和复印件\n2. 户口本原件和复印件\n3. 近期照片2张\n4. 填写申请表'),

-- 侗语
(1, 'kmc', 'Gaenl yaol ndaej', '1. Cawx gaenl yaol ndaej bae caeux cih\n2. Cawx gaenl yaol ndaej bae caeux cih\n3. Ranz saw 2 changj\n4. Doenghsim caeux cih'),

-- 哈尼语
(1, 'hni', 'Cawx yoengz ndaej', '1. Cawx yoengz ndaej bae caeux cih\n2. Cawx yoengz ndaej bae caeux cih\n3. Ranz saw 2 changj\n4. Doenghsim caeux cih'),

-- 傣语
(1, 'tai', 'ບັດປະຈຳຕົວ', '1. ບັດປະຈຳຕົວຕົ້ນສະບັບ ແລະ ສຳເນົາ\n2. ປຶ້ມຄອບຄົວຕົ້ນສະບັບ ແລະ ສຳເນົາ\n3. ຮູບຖ່າຍໃກ້ໆນີ້ 2 ໃບ\n4. ຕື່ມແບບຟອມຄຳຮ້ອງ'),

-- 傈僳语
(1, 'lis', 'Cawx yix ndaej bae', '1. Cawx yix ndaej bae caeux cih qub thiab luam\n2. Cawx yix ndaej bae caeux cih qub thiab luam\n3. Ranz saw 2 changj\n4. Doenghsim caeux cih'),

-- 拉祜语
(1, 'lah', 'Cawx ndaej bae', '1. Cawx ndaej bae caeux cih qub thiab luam\n2. Cawx ndaej bae caeux cih qub thiab luam\n3. Ranz saw 2 changj\n4. Doenghsim caeux cih'),

-- 佤语
(1, 'wa', 'Cawx ndaej yix', '1. Cawx ndaej yix bae caeux cih qub thiab luam\n2. Cawx ndaej yix bae caeux cih qub thiab luam\n3. Ranz saw 2 changj\n4. Doenghsim caeux cih'),

-- 纳西语
(1, 'nxi', 'Cawx yoengz ndaej', '1. Cawx yoengz ndaej bae caeux cih qub thiab luam\n2. Cawx yoengz ndaej bae caeux cih qub thiab luam\n3. Ranz saw 2 changj\n4. Doenghsim caeux cih')
ON DUPLICATE KEY UPDATE ItemNameTrans=VALUES(ItemNameTrans), RequirementTrans=VALUES(RequirementTrans);

-- =============================================
-- 户口迁移 (ItemID = 2)
-- =============================================

INSERT INTO MultilingualInfo (ItemID, LanguageType, ItemNameTrans, RequirementTrans) VALUES
(2, 'zh-CN', '户口迁移', '1. 户口迁移证\n2. 身份证原件及复印件\n3. 房产证或租赁合同\n4. 填写迁移申请表'),
(2, 'ug', 'ئائىلە كىملىك يۆتكىلىشى', '1. ئائىلە كىملىك يۆتكىلىش گۇۋاھنامىسى\n2. كىملىك ھۆججىتى ئەسلىي ۋە كۆچۈرۈلمىسى\n3. ئۆي مۈلۈك گۇۋاھنامىسى ياكى ئىجارە كېلىشىمى\n4. يۆتكىلىش ئەرزىنى تولدۇرۇش'),
(2, 'bo', 'ཁྱིམ་ཚང་སྤོ་བ།', '1. ཁྱིམ་ཚང་སྤོ་བའི་ཡིག་ཆ།\n2. ངོ་རྟགས་ཤོག་ལེབ་དངོས་དང་འདྲ་པར།\n3. ཁང་པའི་ཡིག་ཆའམ་གླ་གཅོད་གྲོས་མཐུན།\n4. སྤོ་བའི་ཞུ་འདེབས་ཡིག་ཆ་འགོད་པ།'),
(2, 'mn', 'Хаяг шилжүүлэх', '1. Хаяг шилжүүлэх гэрчилгээ\n2. Хувийн үнэмлэхний эх хувь, хуулбар\n3. Үл хөдлөх хөрөнгийн гэрчилгээ эсвэл түрээсийн гэрээ\n4. Шилжүүлэх өргөдлийн маягт бөглөх'),
(2, 'kk', 'Тұрғылықты жерін ауыстыру', '1. Тұрғылықты жерін ауыстыру куәлігі\n2. Жеке куәліктің түпнұсқасы мен көшірмесі\n3. Мүлік куәлігі немесе жалға алу келісімі\n4. Ауыстыру өтінішін толтыру'),
(2, 'ko', '호적 이전', '1. 호적 이전 증명서\n2. 신분증 원본 및 사본\n3. 부동산 증명서 또는 임대 계약서\n4. 이전 신청서 작성'),
(2, 'yi', 'ꀉꈨꊂꄮ', '1. ꀉꈨꊂꄮꉆꌺ\n2. ꀊꐙꉆꌺꃀꄡꌐꀕꑌꂷ\n3. ꃀꀉꉆꌺꀕꎆꏦꈎꀏ\n4. ꊂꄮꄡꁧꉆꌺ'),
(2, 'za', 'Cawx youq gyang', '1. Cawx youq gyang bae\n2. Cawx youq ndei bae caeux cih\n3. Ranz bya caeux cih\n4. Doenghsim caeux cih'),
(2, 'yao', 'Ghab hxud gyang', '1. Ghab hxud gyang dangl\n2. Ghab dud dangl jul caeux\n3. Naix hsongd caeux\n4. Naix hsongd dangl'),
(2, 'hmn', 'Hloov chaw nyob', '1. Daim ntawv hloov chaw nyob\n2. Daim ntawv pov thawj qub thiab luam\n3. Daim ntawv pov thawj vaj tsev lossis daim ntawv xauj tsev\n4. Sau daim ntawv thov hloov'),
(2, 'bgc', 'Gangx youq gyang', '1. Gangx youq gyang bae\n2. Gangx yix ndaej caeux cih\n3. Ranz bya caeux cih\n4. Doenghsim caeux cih'),
(2, 'bai', '户口迁移', '1. 户口迁移证\n2. 身份证原件和复印件\n3. 房产证或租赁合同\n4. 填写迁移申请表'),
(2, 'kmc', 'Gaenl youq gyang', '1. Gaenl youq gyang bae\n2. Gaenl yaol ndaej caeux cih\n3. Ranz bya caeux cih\n4. Doenghsim caeux cih'),
(2, 'hni', 'Cawx youq gyang bae', '1. Cawx youq gyang bae\n2. Cawx yoengz ndaej caeux cih\n3. Ranz bya caeux cih\n4. Doenghsim caeux cih'),
(2, 'tai', 'ການຍ້າຍທີ່ຢູ່', '1. ໃບຍ້າຍທີ່ຢູ່\n2. ບັດປະຈຳຕົວຕົ້ນສະບັບ ແລະ ສຳເນົາ\n3. ໃບຢັ້ງຢືນອະສັງຫາລິມະສັບ ຫຼື ສັນຍາເຊົ່າ\n4. ຕື່ມແບບຟອມຄຳຮ້ອງຍ້າຍ'),
(2, 'lis', 'Cawx youq gyang', '1. Cawx youq gyang bae\n2. Cawx yix ndaej caeux cih\n3. Ranz bya caeux cih\n4. Doenghsim caeux cih'),
(2, 'lah', 'Cawx gyang bae', '1. Cawx gyang bae\n2. Cawx ndaej bae caeux cih\n3. Ranz bya caeux cih\n4. Doenghsim caeux cih'),
(2, 'wa', 'Cawx gyang yix', '1. Cawx gyang yix bae\n2. Cawx ndaej yix caeux cih\n3. Ranz bya caeux cih\n4. Doenghsim caeux cih'),
(2, 'nxi', 'Cawx gyang ndaej', '1. Cawx gyang ndaej bae\n2. Cawx yoengz ndaej caeux cih\n3. Ranz bya caeux cih\n4. Doenghsim caeux cih')
ON DUPLICATE KEY UPDATE ItemNameTrans=VALUES(ItemNameTrans), RequirementTrans=VALUES(RequirementTrans);

-- =============================================
-- 营业执照办理 (ItemID = 3)
-- =============================================

INSERT INTO MultilingualInfo (ItemID, LanguageType, ItemNameTrans, RequirementTrans) VALUES
(3, 'zh-CN', '营业执照办理', '1. 企业名称预先核准通知书\n2. 法定代表人身份证原件及复印件\n3. 公司章程\n4. 经营场所证明'),
(3, 'ug', 'سودا خەت رۇخسەتنامىسى', '1. كارخانا نامىنىڭ ئالدىن تەستىقلاش ئۇقتۇرۇشى\n2. قانۇنلۇق ۋەكىلنىڭ كىملىك ھۆججىتى ئەسلىي ۋە كۆچۈرۈلمىسى\n3. شىركەت ئاساسنامىسى\n4. كەسپ ئورنى ئىسپاتى'),
(3, 'bo', 'ཚོང་འབྲེལ་ཆོག་མཆན།', '1. ལས་ཁུངས་མིང་གི་སྔོན་ཆོག་བརྡ་ལན།\n2. ཁྲིམས་མཐུན་ཚབ་མི་ངོ་རྟགས་ཤོག་ལེབ་དངོས་དང་འདྲ་པར།\n3. ཀུང་སིའི་ཆ་རྐྱེན།\n4. ལས་ཁུངས་སའི་གནས་ཡིག་ཆ།'),
(3, 'mn', 'Бизнесийн тусгай зөвшөөрөл', '1. Байгууллагын нэрийг урьдчилан зөвшөөрөх мэдэгдэл\n2. Хуулийн төлөөлөгчийн хувийн үнэмлэхний эх хувь, хуулбар\n3. Компанийн дүрэм\n4. Үйл ажиллагааны газрын баталгаа'),
(3, 'kk', 'Бизнес лицензиясы', '1. Кәсіпорын атауын алдын ала бекіту хабарламасы\n2. Заңды өкілдің жеке куәлігінің түпнұсқасы мен көшірмесі\n3. Компания жарғысы\n4. Кәсіпорын орнының дәлелі'),
(3, 'ko', '영업 허가증', '1. 기업 명칭 사전 승인 통지서\n2. 법정대리인 신분증 원본 및 사본\n3. 회사 정관\n4. 사업장 증명서'),
(3, 'yi', 'ꉻꏽꉆꌺ', '1. ꀋꌺꊿꀋꌋꌺꁧꆏꁧ\n2. ꄡꊭꋠꋠꀊꐙꃀꄡꌐꀕꑌꂷ\n3. ꀋꌺꊿꌦꆏ\n4. ꃅꄡꃀꃅꉆꌺ'),
(3, 'za', 'Gangx ndaej yix', '1. Gangx ndaej yix bae caeux\n2. Gangx yix ndaej caeux cih\n3. Gangx yix bae\n4. Doenghsim caeux cih'),
(3, 'yao', 'Ghab hsongd dangl', '1. Ghab hsongd dangl jul\n2. Ghab dud dangl jul caeux\n3. Ghab hsongd bae\n4. Naix hsongd caeux'),
(3, 'hmn', 'Daim ntawv tso cai ua lag luam', '1. Daim ntawv pom zoo npe ua ntej\n2. Tus sawv cev raug cai daim ntawv pov thawj qub thiab luam\n3. Lub tuam txhab txoj cai\n4. Pov thawj chaw ua lag luam'),
(3, 'bgc', 'Gangx hsongd ndaej', '1. Gangx hsongd ndaej bae\n2. Gangx yix ndaej caeux cih\n3. Gangx yix bae\n4. Doenghsim caeux cih'),
(3, 'bai', '营业执照办理', '1. 企业名称预先核准通知书\n2. 法定代表人身份证原件和复印件\n3. 公司章程\n4. 经营场所证明'),
(3, 'kmc', 'Gaenl hsongd ndaej', '1. Gaenl hsongd ndaej bae\n2. Gaenl yaol ndaej caeux cih\n3. Gaenl yix bae\n4. Doenghsim caeux cih'),
(3, 'hni', 'Cawx hsongd ndaej', '1. Cawx hsongd ndaej bae\n2. Cawx yoengz ndaej caeux cih\n3. Cawx yix bae\n4. Doenghsim caeux cih'),
(3, 'tai', 'ໃບອະນຸຍາດທຸລະກິດ', '1. ໃບແຈ້ງການອະນຸມັດຊື່ລ່ວງໜ້າ\n2. ບັດປະຈຳຕົວຜູ້ຕາງໜ້າທາງກົດໝາຍຕົ້ນສະບັບ ແລະ ສຳເນົາ\n3. ລະບຽບການຂອງບໍລິສັດ\n4. ຫຼັກຖານສະຖານທີ່ເຮັດທຸລະກິດ'),
(3, 'lis', 'Cawx hsongd yix', '1. Cawx hsongd yix bae\n2. Cawx yix ndaej caeux cih\n3. Cawx yix bae\n4. Doenghsim caeux cih'),
(3, 'lah', 'Cawx hsongd bae', '1. Cawx hsongd bae\n2. Cawx ndaej bae caeux cih\n3. Cawx bae\n4. Doenghsim caeux cih'),
(3, 'wa', 'Cawx hsongd ndaej yix', '1. Cawx hsongd ndaej yix\n2. Cawx ndaej yix caeux cih\n3. Cawx yix bae\n4. Doenghsim caeux cih'),
(3, 'nxi', 'Cawx hsongd yoengz', '1. Cawx hsongd yoengz bae\n2. Cawx yoengz ndaej caeux cih\n3. Cawx yix bae\n4. Doenghsim caeux cih')
ON DUPLICATE KEY UPDATE ItemNameTrans=VALUES(ItemNameTrans), RequirementTrans=VALUES(RequirementTrans);

-- =============================================
-- 税务登记 (ItemID = 4)
-- =============================================

INSERT INTO MultilingualInfo (ItemID, LanguageType, ItemNameTrans, RequirementTrans) VALUES
(4, 'zh-CN', '税务登记', '1. 营业执照副本原件及复印件\n2. 法定代表人身份证原件及复印件\n3. 组织机构代码证\n4. 银行开户许可证'),
(4, 'ug', 'باج تەستىقى', '1. سودا خەت رۇخسەتنامىسى ياندىنى ئەسلىي ۋە كۆچۈرۈلمىسى\n2. قانۇنلۇق ۋەكىلنىڭ كىملىك ھۆججىتى\n3. تەشكىلات كودى گۇۋاھنامىسى\n4. بانكا ھېسابات ئېچىش رۇخسەتنامىسى'),
(4, 'bo', 'ཁྲལ་བཀོད་བཅར་འཇགས།', '1. ཚོང་འབྲེལ་ཆོག་མཆན་དངོས་དང་འདྲ་པར།\n2. ཁྲིམས་མཐུན་ཚབ་མི་ངོ་རྟགས་དངོས་དང་འདྲ་པར།\n3. ཚོགས་པའི་ཨང་གྲངས་ཡིག་ཆ།\n4. དངུལ་ཁང་རྩིས་ཁྲ་ཆོག་མཆན།'),
(4, 'mn', 'Татварын бүртгэл', '1. Бизнесийн тусгай зөвшөөрлийн эх хувь, хуулбар\n2. Хуулийн төлөөлөгчийн хувийн үнэмлэхний эх хувь, хуулбар\n3. Байгууллагын кодын гэрчилгээ\n4. Банкны данс нээх зөвшөөрөл'),
(4, 'kk', 'Салық тіркеуі', '1. Бизнес лицензиясының түпнұсқасы мен көшірмесі\n2. Заңды өкілдің жеке куәлігінің түпнұсқасы мен көшірмесі\n3. Ұйымдық код куәлігі\n4. Банктік шот ашу рұқсаты'),
(4, 'ko', '세무 등록', '1. 영업 허가증 사본 원본 및 사본\n2. 법정대리인 신분증 원본 및 사본\n3. 조직 코드 증명서\n4. 은행 계좌 개설 허가증'),
(4, 'yi', 'ꆏꊭꌦꆏ', '1. ꉻꏽꉆꌺꃀꄡꌐꀕꑌꂷ\n2. ꄡꊭꋠꋠꀊꐙꃀꄡꌐꀕꑌꂷ\n3. ꊿꆏꀋꌺꉆꌺ\n4. ꁮꇁꄡꃅꉆꌺ')
ON DUPLICATE KEY UPDATE ItemNameTrans=VALUES(ItemNameTrans), RequirementTrans=VALUES(RequirementTrans);

-- =============================================
-- 社保卡办理 (ItemID = 5)
-- =============================================

INSERT INTO MultilingualInfo (ItemID, LanguageType, ItemNameTrans, RequirementTrans) VALUES
(5, 'zh-CN', '社保卡办理', '1. 身份证原件及复印件\n2. 户口本原件及复印件\n3. 近期免冠照片1张\n4. 填写社保卡申请表'),
(5, 'ug', 'ئىجتىمائىي كاپالەت كارتىسى', '1. كىملىك ھۆججىتى ئەسلىي ۋە كۆچۈرۈلمىسى\n2. ئائىلە دەپتىرى ئەسلىي ۋە كۆچۈرۈلمىسى\n3. يېقىنقى ۋاقىتتىكى رەسىم 1 پارچە\n4. ئىجتىمائىي كاپالەت كارتىسى ئەرزىنى تولدۇرۇش'),
(5, 'bo', 'སྤྱི་ཚོགས་བདེ་འཇགས་ཤོག་ལེབ།', '1. ངོ་རྟགས་ཤོག་ལེབ་དངོས་དང་འདྲ་པར།\n2. ཁྱིམ་ཚང་དེབ་དངོས་དང་འདྲ་པར།\n3. ཉེ་ཆར་གྱི་པར་1 ཤོག\n4. སྤྱི་ཚོགས་བདེ་འཇགས་ཞུ་འདེབས་འགོད་པ།'),
(5, 'mn', 'Нийгмийн даатгалын карт', '1. Хувийн үнэмлэхний эх хувь, хуулбар\n2. Гэрийн бүртгэлийн эх хувь, хуулбар\n3. Саяхан авсан зураг 1 ширхэг\n4. Нийгмийн даатгалын картын өргөдлийн маягт бөглөх'),
(5, 'kk', 'Әлеуметтік карта', '1. Жеке куәліктің түпнұсқасы мен көшірмесі\n2. Үй тіркеу кітапшасының түпнұсқасы мен көшірмесі\n3. Жақында түсірілген сурет 1 дана\n4. Әлеуметтік карта өтінішін толтыру'),
(5, 'ko', '사회보장카드', '1. 신분증 원본 및 사본\n2. 가족 등록부 원본 및 사본\n3. 최근 촬영한 사진 1장\n4. 사회보장카드 신청서 작성')
ON DUPLICATE KEY UPDATE ItemNameTrans=VALUES(ItemNameTrans), RequirementTrans=VALUES(RequirementTrans);

-- 验证数据插入
SELECT LanguageType, COUNT(*) as count
FROM MultilingualInfo
GROUP BY LanguageType
ORDER BY count DESC;

-- 查看所有语言的数据
SELECT * FROM MultilingualInfo ORDER BY ItemID, LanguageType;

