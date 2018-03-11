package com.cn.my.service.impl;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cn.my.mapper.UserMapper;
import com.cn.my.model.Action;
import com.cn.my.model.MeanFirst;
import com.cn.my.model.Role;
import com.cn.my.model.Token;
import com.cn.my.model.User;
import com.cn.my.service.IUserService;
import com.cn.my.util.MD5Util;
import com.cn.my.util.TokenUtil;

@Service("userService")
@Transactional
public class UserServiceImpl implements IUserService{

	@Autowired
	private UserMapper userMapper;
	
	//开启日志
	private final static Log logger = LogFactory.getLog(UserServiceImpl.class);
	
	public String test() {
		return userMapper.test();
	}
	
	public User loginCheck(String userName, String passWord) {
		//md5密码加密
		passWord = MD5Util.MD5Encode(passWord, "UTF8", false);
		User user = userMapper.loginCheck(new User(userName, passWord));
		return user;
	}

	public List<MeanFirst> getUserTab(String token) {
		//暂时不会存在一个用户对应多个角色的情况，只存在一对一的情况
		Token userToken = TokenUtil.tokenMap.get(token);
		int userId = userToken.getUser().getId();
		int roleId = userMapper.getRoleIdFromMap(userId);
		Role role = userMapper.getRoleByRoleId(roleId);
		//存储用户角色相关信息
		TokenUtil.tokenMap.get(token).setRole(role);
		List<Integer> actionIdList = userMapper.getActionIdFromMap(roleId);
		List<Action> actionList = userMapper.getActionsByRoleIdList(actionIdList);
		//存储用户角色的权限信息
		TokenUtil.tokenMap.get(token).setActions(actionList);
		Integer[] action_column_ids = Token.getActionIdsFromActions(actionList);
		//根据分栏id获取菜单信息
		List<MeanFirst> menus = userMapper.getMeansByColumnIds(action_column_ids);
		return menus;
	}

	public List<User> getUsersByCode(String user_code) {
		return userMapper.getUsersByCode(user_code);
	}

	public List<Role> getRoles(String role_level_code) {
		return userMapper.getRoles(role_level_code);
	}

	public int userUniqueCheck(String user_name) {
		return userMapper.userUniqueCheck(user_name);
	}

	public Boolean createUser(User user) {
		boolean flag = false;
		//增加用户的功能：user表增加用户信息，用户角色映射表增加用户角色对应信息
		//用户信息：密码加密、user_code编码
		user.setPass_word(MD5Util.MD5Encode(user.getPass_word(), "UTF8", false));
		user.setCreate_time(new Timestamp(new Date().getTime()));
		if((1==userMapper.userUniqueCheck(user.getUser_name())?false:true)){
			int num = userMapper.insertUser(user);
			if(1==num){
				int use_id = userMapper.getUserIdByUserName(user.getUser_name());
				int role_id = Integer.valueOf(user.getRole_name());
				int number = userMapper.insertRoleUserMap(use_id, role_id, new Timestamp(new Date().getTime()));
				flag = (number==1?true:false);
			}
		}
		return flag;
	}

	public boolean deleteUser(int subUserId) {
		Boolean flag = false;
		//先解除用户角色之间的映射关系、再删除用户相关信息
		int n = userMapper.releaseMapUserRole(subUserId);
		int m = userMapper.deleteUserById(subUserId);
		if((m&n)%2!=0){
			flag = true;
		}
		return flag;
	}

	public User getUserInfoById(int user_id) {
		return userMapper.getUserInfoById(user_id);
	}

	public String getRoleByRole_name(String role_name){
		String str = "";
		Role role = userMapper.getRoleByRole_name(role_name);
		if(null!=role){
			str = role.getRole_level_code();
		}
		return str;
	}

	public boolean editUser(User user) {
		int m = userMapper.editUser(user);
		int n = userMapper.updateRoleUserMap(userMapper.getUserIdByUserName(user.getUser_name()),userMapper.getRoleByRole_name(user.getRole_name()).getId());
		return (m&n)%2!=0;
	}

	public int getUserIdByUserName(String user_name) {
		return userMapper.getUserIdByUserName(user_name);
	}

	public List<Integer> getActionColIdBySecondCode(List<String> mean_secondcode) {
		return userMapper.getActionColIdBySecondCode(mean_secondcode);
	}

	public boolean createRole(int id, String role_level_code,Role role,List<MeanFirst> meanFList) {
		try {
			//0.角色名检查
			if(roleUniqueCheck(role.getName())!=0){
				return false;
			}
			//1.创建角色的过程，先存储角色信息
			role.setCreate_time(new Timestamp(new Date().getTime()));//补充时间参数
			role.setCreate_by(id);
			role.setRole_level_code(role_level_code);
			userMapper.createRole(role);
			int new_role_id = userMapper.getRoleIdByRoleName(role.getName());
			//2.根据一级菜单下的二级菜单获取权限分栏id
			List<String> mean_second_codes = TokenUtil.getSecondMean(meanFList);
			List<Integer> action_col_ids = userMapper.getActionColIdBySecondCode(mean_second_codes);
			//3.根据权限分栏id在权限表中得到所有符合要求的权限id，
			List<Integer> action_ids = userMapper.getActionIdByActionColIds(action_col_ids); 
			//4.在权限角色映射表中建立权限角色映射关系,需要插入多条数据
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("role_id", new_role_id);
			params.put("create_time", new Timestamp(new Date().getTime()));
			params.put("action_id", action_ids);
			userMapper.insertActionRoleMap(params);
			return true;
		} catch (Exception e) {
			logger.error(e.getMessage());
			return false;
		}
	}

	public int roleUniqueCheck(String role_name) {
		return userMapper.roleUniqueCheck(role_name);
	}

	public Map<?, ?> getRoleInfo(String user_role_level, String role_name) {
		Map<String,Object> map = new HashMap<String, Object>();
		Role role = userMapper.getRoleByRole_name(role_name);
		if(role.getRole_level_code().contains(user_role_level)){
			map.put("role",role);
			List<Integer> actionIdList = userMapper.getActionIdFromMap(role.getId());
			List<Action> actionList = userMapper.getActionsByRoleIdList(actionIdList);
			Integer[] action_column_ids = Token.getActionIdsFromActions(actionList);
			//根据分栏id获取菜单信息
			List<MeanFirst> menus = userMapper.getMeansByColumnIds(action_column_ids);
			map.put("mean_first", menus);
		}
		return map;
	}

	public boolean checkIsCreated(int user_id, String name) {
		return user_id==userMapper.getCreateByByRoleName(name);
	}

	public boolean roleUpdate(List<MeanFirst> meanFList, String name){
		/**
		 * 以下做法与删除该角色权限信息再重新插入相比读操作太多，写操作较少，视情况采用优化方式
		 */
		try {
			//被修改的权限集A、数据库原有权限集B
			//新增A-B,移除B-A
			List<String> secondMeanEdit = TokenUtil.getSecondMean(meanFList);
			int role_id = userMapper.getRoleByRole_name(name).getId();
			List<Integer> actionIdList = userMapper.getActionIdFromMap(role_id);
			List<Action> actionList = userMapper.getActionsByRoleIdList(actionIdList);
			Integer[] action_column_ids = Token.getActionIdsFromActions(actionList);
			//根据分栏id获取菜单信息
			List<MeanFirst> menus = userMapper.getMeansByColumnIds(action_column_ids);
			List<String> secondMeanBefore = TokenUtil.getSecondMean(menus);
			List<String> temp = secondMeanBefore;
			secondMeanBefore.removeAll(secondMeanEdit);//B-A删除数据
			secondMeanEdit.removeAll(temp);//A-B新增数据
			if(secondMeanBefore.size()!=0){
				Map<String, Object> paramsRemove = new HashMap<String, Object>();
				paramsRemove.put("role_id", role_id);
				paramsRemove.put("action_id",userMapper.getActionIdByActionColIds(userMapper.getActionColIdBySecondCode(secondMeanBefore)));
				int remove = userMapper.editRoleRemove(paramsRemove);
				logger.info("修改"+name+"的信息，删除权限角色映射信息"+remove+"条！");
			}
			if(secondMeanEdit.size()!=0){
				Map<String, Object> paramsAdd = new HashMap<String, Object>();
				paramsAdd.put("role_id", role_id);
				paramsAdd.put("action_id", userMapper.getActionIdByActionColIds(userMapper.getActionColIdBySecondCode(secondMeanEdit)));
				paramsAdd.put("create_time", new Timestamp(new Date().getTime()));
				int add = userMapper.editRoleAdd(paramsAdd);
				logger.info("修改"+name+"的信息，增加权限角色映射信息"+add+"条！");
			}
			return true;
		} catch (Exception e) {
			logger.error(e.getMessage());
			return false;
		}
	}

	public boolean checkIsBelong(String string, String role_name) {
		String level_code = userMapper.getRoleLevelCodeByRoleName(role_name);
		return level_code.contains(string);
	}

	public boolean deleteRole(String role_name) {
		try {
			//删除角色：解除角色相关用户的有效配置，删除角色，删除映射关系
			int role_id = userMapper.getRoleIdByRoleName(role_name);
			List<Integer> user_id = userMapper.getUserIByRoleId(role_id);
			if(user_id.size()!=0){
				userMapper.changeUserStatus(user_id);//使用户失效,失效后用户查询不出来，后期看是否要修改？
				userMapper.releaseMapsUserRole(user_id);
			}
			userMapper.deleteRoleById(role_id);
			userMapper.releaseMapRoleAction(role_id);
			return true;
		} catch (Exception e) {
			logger.error(e.getMessage());
			return false;
		}
	}

	@Override
	public void changePwd(int id,String pass_word){
		try {
			//md5密码加密
			pass_word = MD5Util.MD5Encode(pass_word, "UTF8", false);
			userMapper.changePwd(id,pass_word);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}
}
