package com.xueyu.user.sdk.pojo.vo;

import lombok.Data;

/**
 * 缩减用户信息实体类
 * 应用与简单展示用户信息的场景、如点赞，评论等
 *
 * @author durance
 */
@Data
public class UserSimpleVO {

	/**
	 * 自增id
	 */
	Integer id;

	/**
	 * 名称
	 */
	String nickname;

	/**
	 * 头像路径
	 */
	String avatarUrl;

	/**
	 * 性别 0 为匿 | 1为boy | 2为girl
	 */
	Integer sex;

	/**
	 * 互相关注  0 否 1 是
	 */
	Integer isMutual;

}
