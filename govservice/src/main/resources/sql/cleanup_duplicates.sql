-- 清理重复数据（可选，不影响功能测试）
-- 仅保留每个事项的最早一条记录

USE govservice;

-- 1. 查看重复数据
SELECT ItemName, DepartmentName, COUNT(*) as count
FROM ServiceItem
GROUP BY ItemName, DepartmentName
HAVING count > 1;

-- 2. 删除重复数据，保留 ItemID 最小的（最早插入的）
DELETE t1 FROM ServiceItem t1
INNER JOIN ServiceItem t2 
WHERE t1.ItemID > t2.ItemID 
  AND t1.ItemName = t2.ItemName 
  AND t1.DepartmentName = t2.DepartmentName;

-- 3. 验证清理结果
SELECT ItemName, DepartmentName, COUNT(*) as count
FROM ServiceItem
GROUP BY ItemName, DepartmentName
HAVING count > 1;

-- 应该返回空结果集，表示没有重复数据了

