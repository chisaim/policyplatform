package com.cn.my.mapper;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.cn.my.model.Action;
import com.cn.my.model.MeanFirst;
import com.cn.my.model.Role;
import com.cn.my.model.User;

public interface UserMapper {

	//测试前台与后台数据库接口是否能互通
	String test();

	//用户登录
	User loginCheck(User user);
	
	//插入用户，成功则返回1，即受影响的行数
	int insertUser(User user);

	//根据角色id获取用户的角色信息
	Role getRoleByRoleId(int roleId);

	//插入某个角色
	int insertRole(Role role);

	//插入角色和用户间的映射关系
	int insertRoleUserMap(@Param("userId") int userId, @Param("roleId") int roleId, @Param("time") Timestamp time);

	//根据映射关系获取角色id
	int getRoleIdFromMap(int userId);

	//获取角色的信息权限
	List<Action> getActionsByRoleIdList(List<Integer> actionIdList);

	//根据映射关系获取权限id
	List<Integer> getActionIdFromMap(int roleId);

	//根据权限分栏id的值获取一级菜单
	List<MeanFirst> getMeansByColumnIds(Integer[] action_column_ids);

	//根据父级编码获取所有子级用户
	List<User> getUsersByCode(String user_code);

	//根据角色等级编码，获得所有相关的权限组
	List<Role> getRoles(String role_level_code);

	//根据用户名查询这个用户名的数量
	int userUniqueCheck(String user_name);

	//根据用户名查询用户id
	int getUserIdByUserName(String user_name);

	//根据角色名查询角色id
	int getRoleIdByRoleName(String role_name);

	//解除用户和角色之间的映射关系
	int releaseMapUserRole(int subUserId);
	
	void releaseMapsUserRole(List<Integer> user_id);

	//删除用户信息
	int deleteUserById(int subUserId);

	//查询单个用户所有信息
	User getUserInfoById(int user_id);

	//查询角色信息
	Role getRoleByRole_name(String role_name);

	//修改用户信息
	int editUser(User user);

	//修改用户与角色的映射关系
	int updateRoleUserMap(@Param("userId")int userIdByUserName,@Param("roleId") int id);

	//根据二级菜单获取分栏id
	List<Integer> getActionColIdBySecondCode(List<String> mean_secondcode);

	//创建角色
	int createRole(Role role);

	//根据分栏id获取所对应的权限
	List<Integer> getActionIdByActionColIds(List<Integer> action_col_ids);

	//插入权限和角色的映射关系
	void insertActionRoleMap(Map<?, ?> map);

	//角色名唯一性校验
	int roleUniqueCheck(String role_name);

	//根据二级菜单删除角色权限映射
	int editRoleRemove(Map<String, Object> paramsRemove) throws Exception;

	//根据二级菜单增加角色权限映射
	int editRoleAdd(Map<String, Object> paramsAdd) throws Exception;

	//根据角色名获取角色等级信息
	String getRoleLevelCodeByRoleName(String role_name);

	//根据角色id获取用户id
	List<Integer> getUserIByRoleId(int role_id);

	//更新用户信息
	void changeUserStatus(List<Integer> user_id);

	void deleteRoleById(int role_id);

	void releaseMapRoleAction(int role_id);
	
	//获取角色的创建人信息
	int getCreateByByRoleName(String name);

	void changePwd(@Param("userId")int id, @Param("pass_word")String pass_word)throws Exception;

}
