package org.young.sso.server.config;

import org.apache.commons.lang.StringUtils;

/** I18n Message codes **/
public interface I18nCodes {
	
	/** 服务异常: 服务响应异常'%s' %s秒, 请稍后重试 **/
	String MSGC000001 = "sso.server.MSGC000001";
	
	/** 参数校验: '%s' 不能是null **/
	String MSGC000002 = "sso.server.MSGC000002";
	
	/** 参数校验: '%s' 不能为空白 **/
	String MSGC000003 = "sso.server.MSGC000003";
	
	/** 参数校验: '%s' 最小值%s **/
	String MSGC000004 = "sso.server.MSGC000004";
	
	/** 参数校验: '%s' 最大值%s **/
	String MSGC000005 = "sso.server.MSGC000005";
	
	/** 参数校验: '%s' 最小长度%s, 最大长度%s **/
	String MSGC000006 = "sso.server.MSGC000006";
	
	/** 参数校验: '%s' 格式错误'%s' **/
	String MSGC000007 = "sso.server.MSGC000007";
	
	/** 参数校验: '%s' 仅支持'GET|POST|PUT|DELETE' **/
	String MSGC000008 = "sso.server.MSGC000008";
	
	/** 参数校验: '%s' 记录不存在, id=%s **/
	String MSGC000009 = "sso.server.MSGC000009";
	
	/** 参数校验: '%s' 取值范围'%s' **/
	String MSGC000010 = "sso.server.MSGC000010";
	
	/** 参数校验: '%s' 不能大于 '%s' **/
	String MSGC000011 = "sso.server.MSGC000011";
	
	/** 参数校验: '%s' 不能小于 '%s' **/
	String MSGC000012 = "sso.server.MSGC000012";
	
	/** 参数校验: '%s' 不能等于 '%s' **/
	String MSGC000013 = "sso.server.MSGC000013";
	
	/** 参数校验: '%s' 不等于 '%s' **/
	String MSGC000014 = "sso.server.MSGC000014";
	
	/** 参数校验: 应用服务不存在 '%s' **/
	String MSGC000015 = "sso.server.MSGC000015";
	
	/** 参数校验: 访问应用服务 '%s'错误 **/
	String MSGC000016 = "sso.server.MSGC000016";
	
	/** 参数校验: 记录已存在'%s' **/
	String MSGC000017 = "sso.server.MSGC000017";
	
	/** 参数校验: 记录不存在'%s' **/
	String MSGC000018 = "sso.server.MSGC000018";
	
	/** 参数校验: '%s'只能由字母数字下户线组成'%s' **/
	String MSGC000019 = "sso.server.MSGC000019";
	
	/** 参数校验: URL格式错误'%s', 例'https://example.com:8080' **/
	String MSGC000020 = "sso.server.MSGC000020";
	
	/** 参数校验: URL格式错误'%s', 服务地址以端口号终结, 例'https://example.com:8080' **/
	String MSGC000021 = "sso.server.MSGC000021";
	
	/** 参数校验: 密码不能全部是'*' **/
	String MSGC000022 = "sso.server.MSGC000022";
	
	/** 登录错误: 用户'%s'不存在 **/
	String MSGE000001 = "sso.server.MSGE000001";
	
	/** 登录错误: 密码错误 **/
	String MSGE000002 = "sso.server.MSGE000002";
	
	/** 登录错误: 操作已过期 **/
	String MSGE000003 = "sso.server.MSGE000003";
	
	/** 登录错误: 疑似伪造他人操作 **/
	String MSGE000004 = "sso.server.MSGE000004";
	
	/** 登录错误: 用户名或邮箱或手机号不能为空 **/
	String MSGE000005 = "sso.server.MSGE000005";
	
	/** 登录错误: 权限不足, 没有权限访问'%s' **/
	String MSGE000006 = "sso.server.MSGE000006";
	
	/** 登录错误: 权限校验失败 **/
	String MSGE000007 = "sso.server.MSGE000007";
	
	/** 登录错误: 请联系管理员查看日志 **/
	String MSGE000008 = "sso.server.MSGE000008";
	
	/** 操作错误: 操作过于频繁，请稍后重试 **/
	String MSGE000010 = "sso.server.MSGE000010";
	
	/** 验证错误: 授权续租失败，授权已过期或不存在 **/
	String MSGE000011 = "sso.server.MSGE000011";
	
	/** 验证错误: 数据解密错误 **/
	String MSGE000012 = "sso.server.MSGE000012";
	
	/** 验证错误: 用户未登录或已过期 **/
	String MSGE000013 = "sso.server.MSGE000013";
	
	/** 验证错误: 客户端应用加密异常 **/
	String MSGE000014 = "sso.server.MSGE000014";
	
	/** 上传错误: 上传文件异常'%s' **/
	String MSGE000015 = "sso.server.MSGE000015";
	
	/** 权限拒绝: 权限不足'%s' **/
	String MSGE000020 = "sso.server.MSGE000020";
	
	/** 权限拒绝: 权限不足, 该功能已被禁用 **/
	String MSGE000021 = "sso.server.MSGE000021";
	
	/** 权限拒绝: 系统内置账户不能修改 **/
	String MSGE000022 = "sso.server.MSGE000022";
	
	/** 权限拒绝: 权限不足，操作无效 **/
	String MSGE000023 = "sso.server.MSGE000023";
	
	/** 权限拒绝: 系统内置数据不能修改 **/
	String MSGE000024 = "sso.server.MSGE000024";
	
	/** 权限拒绝: 权限不足，数据不存在或已被禁用 **/
	String MSGE000025 = "sso.server.MSGE000025";
	
	/** 权限不足，操作无效，不能操作当前用户(自己)数据 **/
	String MSGE000026 = "sso.server.MSGE000026";
	
	/** 验证错误: 验证码不正确或已过期 **/
	String MSGE000030 = "sso.server.MSGE000030";
	
	/** 验证错误: 验证码发送失败 **/
	String MSGE000031 = "sso.server.MSGE000031";
	
	/** 验证错误: 手机号或邮箱错误'%s' **/
	String MSGE000032 = "sso.server.MSGE000032";
	
	/** 验证错误: '%s'已被使用 **/
	String MSGE000033 = "sso.server.MSGE000033";
	
	/** 验证错误: 所属租户不存在或已失效 **/
	String MSGE000034 = "sso.server.MSGE000034";
	
	/** 导入错误: 导入数据为空 **/
	String MSGE000040 = "sso.server.MSGE000040";
	
	/** 导入错误: 标题行字段个数不正确，期望值'%s'，实际值'%s' **/
	String MSGE000041 = "sso.server.MSGE000041";
	
	/** 导入错误: 数据行字段个数不正确，期望值'%s'，实际值'%s' **/
	String MSGE000042 = "sso.server.MSGE000042";
	
	/** 导入错误: 读取文件异常'%s' **/
	String MSGE000043 = "sso.server.MSGE000043";
	
	/**
	 * 获取整型code
	 * @param i18nCode
	 * @return
	 */
	static int getCode(String i18nCode) {
		if (StringUtils.isBlank(i18nCode)) {
			return 0;
		}
		
		int code = 0;
		String[] arr = i18nCode.split("\\.");
		if (arr[arr.length-1].startsWith("MSGC")) {
			code = Integer.parseInt(arr[arr.length-1].replace("MSGC", "1"));
		}
		else if (arr[arr.length-1].startsWith("MSGE")) {
			code = Integer.parseInt(arr[arr.length-1].replace("MSGE", "1"));
		}
		return code;
	}
	
	/**
	 * 整型code转换为i18nCode
	 */
	static String getI18nCode(int code) {
		if (code==0) {
			return "";
		}
		
		String i18nCode = String.valueOf(code);
		// MSGC
		if (i18nCode.startsWith("1")) {
			i18nCode = "sso.server.MSGC"+i18nCode.substring(1);
		}
		// MSGE
		else if (i18nCode.startsWith("2")){
			i18nCode = "sso.server.MSGE"+i18nCode.substring(1);
		}
		return i18nCode;
	}
	
}
