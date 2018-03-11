package com.cn.my.service;

import java.util.List;
import java.util.Map;

import com.cn.my.model.MeanFirst;
import com.cn.my.model.Role;
import com.cn.my.model.User;

public interface IUserService {

	public String test();
	
	//用户登录，返回用户信息
	public User loginCheck(String userName, String passWord);

	//获取用户的菜单信息
	public List<MeanFirst> getUserTab(String token);

	//获取其所有子级用户
	public List<User> getUsersByCode(String user_code);

	//得到role数据
	public List<Role> getRoles(String role_level_code);

	//用户名唯一性校验
	public int userUniqueCheck(String user_name);

	//创建用户
	public Boolean createUser(User user);

	//删除用户
	public boolean deleteUser(int subUserId);

	//获取用户相关的信息
	public User getUserInfoById(int user_id);

	//根据角色名查询角色信息
	public String getRoleByRole_name(String role_name);

	//修改用户信息
	public boolean editUser(User user);

	//根据用户名查询用户id
	public int getUserIdByUserName(String user_name);

	//新建角色
	public boolean createRole(int id, String string, Role role, List<MeanFirst> meanFList);

	//角色名唯一性校验
	public int roleUniqueCheck(String role_name);

	//获取角色信息
	public Map<?, ?> getRoleInfo(String user_role_level, String role_name);

	//根据用户名验证创建用户
	public boolean checkIsCreated(int user_id, String name);

	//更新相关权限数据
	public boolean roleUpdate(List<MeanFirst> meanFList, String name);

	//检查从属关系
	public boolean checkIsBelong(String string, String role_name);

	//删除角色
	public boolean deleteRole(String role_name);

	public void changePwd(int i, String pass_word)throws Exception;
	
}
