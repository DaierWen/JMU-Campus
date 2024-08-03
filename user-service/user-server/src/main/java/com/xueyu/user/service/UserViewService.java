package com.xueyu.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xueyu.common.core.result.ListVO;
import com.xueyu.user.pojo.vo.UserGeneralVO;
import com.xueyu.user.pojo.vo.UserView;
import com.xueyu.user.request.UserQueryRequest;
import com.xueyu.user.sdk.pojo.vo.UserSimpleVO;

import java.util.List;
import java.util.Map;

/**
 * 用户数据业务层
 *
 * @author durance
 */
public interface UserViewService extends IService<UserView> {

	/**
	 * 获取用户信息列表
	 *
	 * @param userIdList 用户id集合
	 * @return 用户数据
	 */
	List<UserSimpleVO> getUserInfoList(List<Integer> userIdList);

	/**
	 * 按照用户id获取用户列表
	 *
	 * @param userIdList 用户id列表
	 * @return 用户 id | 用户信息
	 */
	Map<Integer, UserSimpleVO> getUserInfoListById(List<Integer> userIdList);

	/**
	 * 获取单个用户信息
	 *
	 * @param userId 用户id
	 * @return 用户数据
	 */
	UserView getUserInfo(Integer userId);

	/**
	 * 根据传入id进行分组查询用户信息
	 *
	 * @param userGroupIds 分组id，key为分组id | value为该key所以对应的需要查询的用户id列表
	 * @return 分组用户信息
	 */
	Map<Integer, List<UserSimpleVO>> getUserInfoListByGroup(Map<String, List<Integer>> userGroupIds);

	/**
	 * 获取用户统计数据 VO
	 *
	 * @param userId 用户id
	 * @return 用户统计数据
	 */
	UserGeneralVO getUserGeneralInfo(Integer userId);

	/**
	 * 根据用户名搜索用户列表
	 *
	 * @param username 用户名
	 * @return 用户列表
	 */
	List<UserView> getUserListBySearch(String username);

	/**
	 * 用户列表查询
	 *
	 * @param request request
	 * @return 用户列表
	 */
	ListVO<UserView> getUserListPage(UserQueryRequest request);

}
