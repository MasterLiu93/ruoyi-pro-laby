/*
 Laby Boot 数据库清理脚本
 
 说明：在导入 laby.sql 后执行此脚本，清理多余模块的数据
 
 清理内容：
 - 删除多余模块的字典类型和字典数据
 - 删除多余模块的菜单和权限
 - 删除多余模块的角色菜单关联
 
 已移除模块：
 - AI、BPM、Mall、IoT、MP、CRM、ERP、Member、Pay、Report、Product、Promotion、Brokerage
 
 执行时间：约 5-10 秒
 创建日期：2025-10-27
*/

-- 使用数据库
USE `laby`;

-- ====================================
-- 第一步：删除字典数据
-- ====================================
DELETE FROM system_dict_data WHERE dict_type LIKE 'iot_%';
DELETE FROM system_dict_data WHERE dict_type LIKE 'bpm_%';
DELETE FROM system_dict_data WHERE dict_type LIKE 'ai_%';
DELETE FROM system_dict_data WHERE dict_type LIKE 'pay_%';
DELETE FROM system_dict_data WHERE dict_type LIKE 'trade_%';
DELETE FROM system_dict_data WHERE dict_type LIKE 'product_%';
DELETE FROM system_dict_data WHERE dict_type LIKE 'promotion_%';
DELETE FROM system_dict_data WHERE dict_type LIKE 'member_%';
DELETE FROM system_dict_data WHERE dict_type LIKE 'crm_%';
DELETE FROM system_dict_data WHERE dict_type LIKE 'erp_%';
DELETE FROM system_dict_data WHERE dict_type LIKE 'mp_%';
DELETE FROM system_dict_data WHERE dict_type LIKE 'report_%';
DELETE FROM system_dict_data WHERE dict_type LIKE 'brokerage_%';

-- ====================================
-- 第二步：删除字典类型
-- ====================================
DELETE FROM system_dict_type WHERE type LIKE 'iot_%';
DELETE FROM system_dict_type WHERE type LIKE 'bpm_%';
DELETE FROM system_dict_type WHERE type LIKE 'ai_%';
DELETE FROM system_dict_type WHERE type LIKE 'pay_%';
DELETE FROM system_dict_type WHERE type LIKE 'trade_%';
DELETE FROM system_dict_type WHERE type LIKE 'product_%';
DELETE FROM system_dict_type WHERE type LIKE 'promotion_%';
DELETE FROM system_dict_type WHERE type LIKE 'member_%';
DELETE FROM system_dict_type WHERE type LIKE 'crm_%';
DELETE FROM system_dict_type WHERE type LIKE 'erp_%';
DELETE FROM system_dict_type WHERE type LIKE 'mp_%';
DELETE FROM system_dict_type WHERE type LIKE 'report_%';
DELETE FROM system_dict_type WHERE type LIKE 'brokerage_%';

-- ====================================
-- 第三步：删除多余模块的菜单
-- ====================================

-- 删除 BPM 流程引擎菜单（ID范围：1185-1223, 2714-2739）
-- 注意：需要保留租户管理相关菜单（1138-1143, 1224-1229）
DELETE FROM system_menu WHERE id >= 1185 AND id <= 1223 
  AND id NOT IN (1138, 1139, 1140, 1141, 1142, 1143, 1224, 1225, 1226, 1227, 1228, 1229);
DELETE FROM system_menu WHERE id >= 2714 AND id <= 2739;

-- 删除 AI 模块菜单（ID范围：2758-2800, 5000-5100）
DELETE FROM system_menu WHERE id >= 2758 AND id <= 2800;
DELETE FROM system_menu WHERE id >= 5000 AND id <= 5100;

-- 删除 IoT 物联网菜单（ID范围：4000-4100）
DELETE FROM system_menu WHERE id >= 4000 AND id <= 4100;

-- 删除 MP 微信公众号菜单（ID范围：2084-2130）
DELETE FROM system_menu WHERE id >= 2084 AND id <= 2130;

-- 删除 Mall 商城菜单（ID范围：2000-2500, 包括产品、促销、交易等）
DELETE FROM system_menu WHERE id >= 2000 AND id <= 2500;

-- 删除 Member 会员菜单（ID范围：2262, 2317-2350）
DELETE FROM system_menu WHERE id = 2262;
DELETE FROM system_menu WHERE id >= 2317 AND id <= 2350;

-- 删除 CRM 客户管理菜单（ID范围：2516-2562）
DELETE FROM system_menu WHERE id >= 2516 AND id <= 2562;

-- 删除 ERP 企业资源计划菜单（ID范围：2563-2714）
DELETE FROM system_menu WHERE id >= 2563 AND id <= 2714;

-- 删除 Pay 支付菜单（ID范围：1117-1127）
DELETE FROM system_menu WHERE id >= 1117 AND id <= 1127;

-- 删除 Report 报表菜单（ID范围：1281-1282, 2153-2155）
DELETE FROM system_menu WHERE id IN (1281, 1282);
DELETE FROM system_menu WHERE id >= 2153 AND id <= 2155;

-- 删除"作者动态"菜单（ID: 1254）
DELETE FROM system_menu WHERE id = 1254;

-- 删除"OA 示例"菜单（ID: 5，属于BPM模块）
DELETE FROM system_menu WHERE id = 5;

-- 删除"秘钥解析"菜单（ID: 1132, 1150，属于Pay支付模块）
DELETE FROM system_menu WHERE id IN (1132, 1150);

-- 删除"商品统计"相关菜单（ID: 2545, 2743, 2744，属于Mall模块）
DELETE FROM system_menu WHERE id IN (2545, 2743, 2744);

-- 根据路径和权限标识删除菜单（以防ID范围不准确）
DELETE FROM system_menu WHERE component LIKE 'bpm/%';
DELETE FROM system_menu WHERE component LIKE 'ai/%';
DELETE FROM system_menu WHERE component LIKE 'iot/%';
DELETE FROM system_menu WHERE component LIKE 'mp/%';
DELETE FROM system_menu WHERE component LIKE 'mall/%';
DELETE FROM system_menu WHERE component LIKE 'member/%';
DELETE FROM system_menu WHERE component LIKE 'crm/%';
DELETE FROM system_menu WHERE component LIKE 'erp/%';
DELETE FROM system_menu WHERE component LIKE 'pay/%';
DELETE FROM system_menu WHERE component LIKE 'report/%';
DELETE FROM system_menu WHERE component LIKE 'trade/%';
DELETE FROM system_menu WHERE component LIKE 'product/%';
DELETE FROM system_menu WHERE component LIKE 'promotion/%';

DELETE FROM system_menu WHERE permission LIKE 'bpm:%';
DELETE FROM system_menu WHERE permission LIKE 'ai:%';
DELETE FROM system_menu WHERE permission LIKE 'iot:%';
DELETE FROM system_menu WHERE permission LIKE 'mp:%';
DELETE FROM system_menu WHERE permission LIKE 'mall:%';
DELETE FROM system_menu WHERE permission LIKE 'member:%';
DELETE FROM system_menu WHERE permission LIKE 'crm:%';
DELETE FROM system_menu WHERE permission LIKE 'erp:%';
DELETE FROM system_menu WHERE permission LIKE 'pay:%';
DELETE FROM system_menu WHERE permission LIKE 'report:%';
DELETE FROM system_menu WHERE permission LIKE 'trade:%';
DELETE FROM system_menu WHERE permission LIKE 'product:%';
DELETE FROM system_menu WHERE permission LIKE 'promotion:%';
DELETE FROM system_menu WHERE permission LIKE 'statistics:%';

-- ====================================
-- 第四步：删除角色菜单关联
-- ====================================
-- 删除已不存在菜单的角色菜单关联（通过LEFT JOIN清理）
DELETE FROM system_role_menu 
WHERE menu_id NOT IN (SELECT id FROM system_menu);

-- ====================================
-- 第五步：清理和更新 OAuth2 客户端
-- ====================================
-- 只保留 default 客户端，删除其他测试客户端
DELETE FROM system_oauth2_client WHERE client_id != 'default';

-- 更新 default 客户端的信息（移除作者相关）
UPDATE system_oauth2_client 
SET name = 'Laby Boot',
    logo = '',
    description = 'Laby Boot 管理系统',
    redirect_uris = '["http://127.0.0.1:80","http://localhost:80"]'
WHERE client_id = 'default';

-- ====================================
-- 第六步：更新租户信息（移除作者相关）
-- ====================================
-- 更新租户名称和联系人信息
UPDATE system_tenant 
SET name = '默认租户',
    contact_name = '管理员',
    contact_mobile = '18800000000',
    websites = 'localhost,127.0.0.1'
WHERE id = 1;

-- 删除测试租户
DELETE FROM system_tenant WHERE id != 1;

-- ====================================
-- 第七步：更新用户信息（移除作者相关）
-- ====================================
-- 更新管理员用户信息
UPDATE system_users 
SET nickname = '系统管理员',
    remark = '系统管理员账号',
    email = 'admin@example.com',
    mobile = '18800000001'
WHERE id = 1 AND username = 'admin';

-- 删除除了admin之外的所有系统用户（id=1是admin）
-- DELETE FROM system_users WHERE id > 1 AND tenant_id = 1;

-- ====================================
-- 第八步：更新部门信息
-- ====================================
-- 更新公司部门名称
UPDATE system_dept 
SET name = '总公司',
    leader_user_id = NULL
WHERE id = 100 AND parent_id = 0;

UPDATE system_dept 
SET name = '研发部'
WHERE id = 101 AND parent_id = 100;

UPDATE system_dept 
SET name = '市场部'
WHERE id = 102 AND parent_id = 100;

-- 删除多余的部门
DELETE FROM system_dept WHERE id > 108;

-- ====================================
-- 第九步：更新岗位信息
-- ====================================
UPDATE system_post 
SET name = '董事长',
    code = 'ceo'
WHERE id = 1;

UPDATE system_post 
SET name = '项目经理',
    code = 'manager'
WHERE id = 2;

-- 删除多余的岗位
DELETE FROM system_post WHERE id > 4;

-- ====================================
-- 第十步：更新通知公告（移除示例内容）
-- ====================================
DELETE FROM system_notice WHERE id > 0;

-- ====================================
-- 第十一步：清理操作日志和登录日志（可选，保留则注释掉）
-- ====================================
-- TRUNCATE TABLE system_operate_log;
-- TRUNCATE TABLE system_login_log;

-- ====================================
-- 第十二步：清理短信和邮件配置中的敏感信息
-- ====================================
-- 清理短信渠道的敏感信息（保留配置结构）
UPDATE system_sms_channel 
SET api_key = '',
    api_secret = '',
    callback_url = ''
WHERE id > 0;

-- 清理邮箱账号的敏感信息（保留配置结构）
UPDATE system_mail_account 
SET username = 'example@example.com',
    password = ''
WHERE id > 0;

-- ====================================
-- 第十三步：清理文件配置中的敏感信息
-- ====================================
-- 注意：文件配置的敏感信息在 config JSON 字段中，建议手动修改
-- 这里不删除配置，保留配置结构供后续使用
-- DELETE FROM infra_file_config WHERE id > 0;

-- ====================================
-- 第十四步：更新参数配置（移除作者相关）
-- ====================================
UPDATE infra_config 
SET value = '123456'
WHERE config_key = 'system.user.init-password';

-- ====================================
-- 完成
-- ====================================
SELECT '============ 清理完成！============' AS message;
SELECT '字典类型数' AS 项目, COUNT(*) AS 数量 FROM system_dict_type
UNION ALL
SELECT '字典数据数', COUNT(*) FROM system_dict_data
UNION ALL
SELECT '菜单数', COUNT(*) FROM system_menu
UNION ALL
SELECT '角色菜单关联数', COUNT(*) FROM system_role_menu
UNION ALL
SELECT '租户数', COUNT(*) FROM system_tenant
UNION ALL
SELECT '用户数', COUNT(*) FROM system_users
UNION ALL
SELECT '部门数', COUNT(*) FROM system_dept
UNION ALL
SELECT '岗位数', COUNT(*) FROM system_post;

