package com.xueyu.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xueyu.user.mapper.UserGeneralMapper;
import com.xueyu.user.mapper.UserMapper;
import com.xueyu.user.pojo.domain.User;
import com.xueyu.user.pojo.domain.UserGeneral;
import com.xueyu.user.service.UserService;
import com.xueyu.user.util.JwtUtil;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.sql.Timestamp;

/**
 * @author durance
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

	@Resource
	UserMapper userMapper;

	@Resource
	UserGeneralMapper userGeneralMapper;

	@Override
	public Boolean registerUser(User user) {
		LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
		wrapper.eq(User::getPhone, user.getPhone()).or().eq(User::getUsername, user.getUsername());
		User check = userMapper.selectOne(wrapper);
		// 不为null说明已经创建改用户
		if (check != null) {
			return false;
		}
		Timestamp time = new Timestamp(System.currentTimeMillis());
		user.setCreateTime(time);
		String hashpw = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());
		user.setPassword(hashpw);
		userMapper.insert(user);
		// 创建数据统计表行数据
		UserGeneral userGeneral = new UserGeneral();
		userGeneral.setUserId(user.getId());
		userGeneralMapper.insert(userGeneral);
		return true;
	}

	@Override
	public String loginUser(User user) {
		LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
		wrapper.eq(User::getUsername, user.getUsername());
		User check = userMapper.selectOne(wrapper);
		boolean checkpw = BCrypt.checkpw(user.getPassword(), check.getPassword());
		if (!checkpw) {
			return null;
		}
		// 签发jwt，设置用户id
		return JwtUtil.createJwt("userId", check.getId());
	}

}
