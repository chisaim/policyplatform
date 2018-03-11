package com.cn.my.controler;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.cn.my.model.MeanFirst;
import com.cn.my.model.Result;
import com.cn.my.model.Role;
import com.cn.my.model.Token;
import com.cn.my.model.User;
import com.cn.my.service.IUserService;
import com.cn.my.util.Constant;
import com.cn.my.util.JsonUtils;
import com.cn.my.util.TimeUtil;
import com.cn.my.util.TokenUtil;
import com.cn.my.util.UuidUtil;

/**
 * @ClassName: UserControler
 * @Description: user相关的控制器
 * @author zengyejun
 * @date 2017年10月10日
 */
@Controller
@RequestMapping("/user")
public class UserControler {

	@Autowired
	@Qualifier("userService")
	private IUserService userService;

	// 开启日志
	private final static Log logger = LogFactory.getLog(UserControler.class);

	// 就是一个测试方法，测试前后台及数据库访问
	@RequestMapping(value = "/test", produces = "text/html;charset=UTF-8", method = RequestMethod.GET)
	@ResponseBody
	public String test() {
		String s = userService.test();
		return s;
	}

	/**
	 * 登陆打算分成两步走，1登陆验证是否是合法用户，是则分发token，否则告诉前端用户非法，
	 * 2.前端得到token后，页面跳转到index.html，页面加载windows.load方法来获取需要加载的数据，即控制前端手风琴选项下的显示
	 */
	@RequestMapping(value = "/userLogin", method = RequestMethod.POST)
	public @ResponseBody Result<?> userLogin(
			@RequestBody JSONObject requestJson, HttpServletRequest request,
			HttpServletResponse response) {
		String userName = requestJson.getString("userName");
		String passWord = requestJson.getString("passWord");
		User user = userService.loginCheck(userName, passWord);
		Result<Map<String, String>> re = new Result<Map<String, String>>();
		Map<String, String> map = new HashMap<String, String>();
		// 验证user用户是否存在，不存在直接返回前台提示用户名密码错误
		if (null == user) {
			re.setCode(Constant.CODE_FOR_FAILURER);
			re.setMessage("用户登录失败！");
			map.put("info", "username or password was unrecognized!");
			re.setData(map);
			return re;
		}
		if (!user.isStatus()) {
			re.setCode(Constant.CODE_FOR_FAILURER);
			re.setMessage("用户登录失败！");
			map.put("info", "用户失效，请联系上级用户或超级管理员解封账号!");
			re.setData(map);
			return re;
		}
		// token生成并存储
		String token = UuidUtil.generateToken();
		TokenUtil.tokenMap.put(token, new Token(user,
				Timestamp.valueOf(String.valueOf(TimeUtil.getCurrentTime()))));
		map.put("token", token);
		re.setCode(Constant.CODE_FOR_SUCCESSR);
		re.setMessage("用户登录成功！");
		re.setData(map);
		return re;
	}

	// 根据用户token，获取用户菜单信息并存储在TokenUtil中
	@RequestMapping(value = "/getUserTab", method = RequestMethod.GET)
	public @ResponseBody Result<?> getUserTab(HttpServletRequest request) {
		// 过滤器中已验证过，token不会为null
		String token = TokenUtil.getCookieByName(request, "token");
		List<MeanFirst> menus = userService.getUserTab(token);
		Result<Map<String, List<MeanFirst>>> re = new Result<Map<String, List<MeanFirst>>>();
		re.setCode(Constant.CODE_FOR_SUCCESSR);
		re.setMessage("用户获取菜单信息！");
		Map<String, List<MeanFirst>> map = new HashMap<String, List<MeanFirst>>();
		map.put("menus", menus);
		re.setData(map);
		return re;
	}

	// 用户登出实现
	@RequestMapping(value = "/userLoginOut", method = RequestMethod.POST)
	public @ResponseBody Result<?> loginOut(HttpServletRequest request) {
		String token = TokenUtil.getCookieByName(request, "token");
		TokenUtil.tokenMap.remove(token);
		Result<?> re = new Result<String>(Constant.CODE_FOR_SUCCESSR, "登出成功！");
		return re;
	}

	// 获取用户及其所有子用户数据
	@RequestMapping(value = "/getUsers", method = RequestMethod.GET)
	@ResponseBody
	public Result<?> getUsers(HttpServletRequest request) {
		// 用户编码信息：user_code列存储的是超级管理员的id号/次一级的id号/再次一级../父一级id，即采用的是用path存储相关的关系
		Token token = TokenUtil.tokenMap.get(TokenUtil.getCookieByName(request, "token"));
		User user = token.getUser();
		user.setRole_name(token.getRole().getName());
		String user_code = user.getUser_code() + "/" + user.getId();
		List<User> userList = userService.getUsersByCode(user_code);
		userList.add(user);
		Result<List<User>> re = new Result<List<User>>();
		re.setCode(Constant.CODE_FOR_SUCCESSR);
		re.setMessage("获取相关用户信息成功！");
		re.setData(userList);
		return re;
	}

	// 获取用户本身角色等级及其建立的下级权限组
	@RequestMapping(value = "/getRoles", method = RequestMethod.GET)
	@ResponseBody
	public Result<?> getRoles(HttpServletRequest request) {
		Role role = TokenUtil.tokenMap.get(TokenUtil.getCookieByName(request, "token")).getRole();
		List<Role> roleList = userService.getRoles(role.getRole_level_code());
		Result<List<Role>> re = new Result<List<Role>>();
		re.setCode(Constant.CODE_FOR_SUCCESSR);
		re.setMessage("获取相关角色信息成功！");
		re.setData(roleList);
		return re;
	}

	// 用户名唯一性检验
	@RequestMapping(value = "/userUniqueCheck", method = RequestMethod.GET)
	@ResponseBody
	public Result<?> userUniqueCheck(@RequestParam("user_name") String user_name) {
		int num = userService.userUniqueCheck(user_name);
		Result<Boolean> re = new Result<Boolean>();
		if (num != 0) {
			re.setCode(Constant.CODE_FOR_FAILURER);
			re.setMessage("用户名已存在！");
			re.setData(false);
		} else {
			re.setCode(Constant.CODE_FOR_SUCCESSR);
			re.setMessage("用户名可用！");
			re.setData(true);
		}
		return re;
	}

	// 用户名唯一性检验
	@RequestMapping(value = "/roleUniqueCheck", method = RequestMethod.GET)
	@ResponseBody
	public Result<?> roleUniqueCheck(@RequestParam("role_name") String role_name) {
		int num = userService.roleUniqueCheck(role_name);
		Result<Boolean> re = new Result<Boolean>();
		if (num != 0) {
			re.setCode(Constant.CODE_FOR_FAILURER);
			re.setMessage("角色名已存在！");
			re.setData(false);
		} else {
			re.setCode(Constant.CODE_FOR_SUCCESSR);
			re.setMessage("角色名可用！");
			re.setData(true);
		}
		return re;
	}

	// 创建用户
	@RequestMapping(value = "/createUser", method = RequestMethod.POST)
	@ResponseBody
	public Result<?> createUser(@RequestBody User user,HttpServletRequest request) {
		User userOwn = TokenUtil.tokenMap.get(TokenUtil.getCookieByName(request, "token")).getUser();
		user.setUser_code(userOwn.getUser_code() + "/" + userOwn.getId());
		Boolean flag = userService.createUser(user);
		if (flag) {
			return new Result<String>(Constant.CODE_FOR_SUCCESSR, "新建用户成功！");
		}
		return new Result<String>(Constant.CODE_FOR_SUCCESSR, "创建用户失败！");
	}

	// 删除用户
	@RequestMapping(value = "/deleteUser", method = RequestMethod.POST)
	@ResponseBody
	public Result<?> deleteUser(@RequestBody JSONObject requestJson,HttpServletRequest request) {
		int subUserId = requestJson.getInteger("user_id");
		// 查看执行用户是否有删除该用户的权限
		boolean flag = judgeIslegal(request, subUserId);
		if (flag) {
			boolean re = userService.deleteUser(subUserId);
			if (re) {
				return new Result<String>(Constant.CODE_FOR_SUCCESSR,
						"删除用户成功！");
			} else {
				return new Result<String>(Constant.CODE_FOR_FAILURER,
						"删除用户失败！有未知bug！");
			}
		}
		return new Result<String>(Constant.CODE_FOR_FAILURER, "权限不足，别搞事！");
	}

	// 用户对其子用户的操作权限判断
	private boolean judgeIslegal(HttpServletRequest request, int subUserId) {
		User user = TokenUtil.tokenMap.get(TokenUtil.getCookieByName(request, "token")).getUser();
		List<User> userList = userService.getUsersByCode(user.getUser_code() + "/" + user.getId());
		// 查看执行用户是否有操作该用户的权限
		boolean flag = false;
		for (User userSub : userList) {
			if (subUserId == userSub.getId()) {
				flag = true;
				break;
			}
		}
		return flag;
	}

	// 获取单个用户所有信息
	@RequestMapping(value = "/getUserInfo", method = RequestMethod.GET)
	@ResponseBody
	public Result<?> getUserInfo(@RequestParam("user_id") int user_id,HttpServletRequest request) {
		boolean flag = TokenUtil.tokenMap.get(TokenUtil.getCookieByName(request, "token")).getUser().getId() == user_id;
		if (judgeIslegal(request, user_id) || flag) {
			User user = userService.getUserInfoById(user_id);
			Result<User> re = new Result<User>(Constant.CODE_FOR_SUCCESSR,"得到用户相关信息！");
			re.setData(user);
			return re;
		}
		return new Result<String>(Constant.CODE_FOR_FAILURER, "权限不足！");
	}

	// 保存用户修改的信息，用户名和密码不可更改，修改密码的功能接口另行开发
	@RequestMapping(value = "/editUser", method = RequestMethod.POST)
	@ResponseBody
	public Result<?> editUser(@RequestBody User user,HttpServletRequest request) {
		// 是否有修改该数据的权限
		if (judgeIslegal(request,userService.getUserIdByUserName(user.getUser_name()))) {
			// 检查更改的数据是否有效：即角色信息是否是在修改范围内的数据
			if (userService.getRoleByRole_name(user.getRole_name()).contains(TokenUtil.tokenMap.get(TokenUtil.getCookieByName(request,"token")).getRole().getRole_level_code())) {
				if (userService.editUser(user)) {
					return new Result<String>(Constant.CODE_FOR_SUCCESSR,"修改相关信息成功！");
				}
				return new Result<String>(Constant.CODE_FOR_FAILURER,"数据更新失败！");
			}
			return new Result<String>(Constant.CODE_FOR_FAILURER, "修改角色信息失败！");
		}
		return new Result<String>(Constant.CODE_FOR_FAILURER, "修改相关信息失败！");
	}

	@RequestMapping(value = "/roleAdd", method = RequestMethod.POST)
	@ResponseBody
	public Result<?> roleAdd(@RequestBody Map<String, Object> data,HttpServletRequest request) {
		try {
			// 解析权限数据
			@SuppressWarnings({"unchecked"})
			Role role = JsonUtils.map2obj((Map<String, Object>) data.get("role"), Role.class);
			List<MeanFirst> meanFList = JsonUtils.data2list((List<?>) data.get("mean_first"), MeanFirst.class);

			Token token = TokenUtil.tokenMap.get(TokenUtil.getCookieByName(request, "token"));
			// 验证权限数据是否有效，防止httpclient模拟请求越权
			if (TokenUtil.isRoleLegal(meanFList,TokenUtil.getCookieByName(request, "token"))) {
				// 增加新角色，需要记录新角色的创建人、角色的编码等级（一般用的是创建角色用户的编码等级+"/"+用户角色的id）
				boolean flag = userService.createRole(token.getUser().getId(),token.getRole().getRole_level_code() + "/" + token.getRole().getId(),role, meanFList);
				if (flag) {
					return new Result<String>(Constant.CODE_FOR_SUCCESSR,"创建角色成功！");
				}
				return new Result<String>(Constant.CODE_FOR_FAILURER,"创建角色由于未知原因失败，请查看相关日志！");
			}
			return new Result<String>(Constant.CODE_FOR_FAILURER,"权限不足，低权限用户试图创建高权限用户！");
		} catch (Exception e) {
			logger.error(e.getMessage());
			return new Result<String>(Constant.CODE_FOR_FAILURER,e.getMessage());
		}
	}

	// 获取单个角色所有信息
	@RequestMapping(value = "/getRoleInfo", method = RequestMethod.GET)
	@ResponseBody
	public Result<?> getRoleInfo(@RequestParam("role_name") String role_name,HttpServletRequest request) {
		String user_role_level = TokenUtil.tokenMap.get(TokenUtil.getCookieByName(request, "token")).getRole().getRole_level_code();
		Map<?, ?> map = userService.getRoleInfo(user_role_level, role_name);
		if (null != map.get("role")) {
			Result<Map<?, ?>> re = new Result<Map<?, ?>>();
			re.setCode(Constant.CODE_FOR_SUCCESSR);
			re.setMessage("获取角色信息成功！");
			re.setData(map);
			return re;
		}
		return new Result<String>(Constant.CODE_FOR_FAILURER, "权限不足！");
	}

	// 更新角色信息
	@RequestMapping(value = "/roleUpdate", method = RequestMethod.POST)
	@ResponseBody
	public Result<?> roleUpdate(@RequestBody Map<String, Object> data,HttpServletRequest request) {
		try {
			// 解析权限数据
			@SuppressWarnings({"unchecked"})
			Role role = JsonUtils.map2obj((Map<String, Object>) data.get("role"),Role.class);
			List<MeanFirst> meanFList = JsonUtils.data2list((List<?>) data.get("mean_first"), MeanFirst.class);
			int user_id = TokenUtil.tokenMap.get(TokenUtil.getCookieByName(request, "token")).getUser().getId();
			//验证角色是该用户创建的，否则不予修改，验证权限是在范围内的，没有越权（即比对meanFlist），防止权限错赋，即使是用户上级也不能修改用户创建的角色
			boolean isCreated = userService.checkIsCreated(user_id,role.getName());
			boolean isLeagal = TokenUtil.isRoleLegal(meanFList, TokenUtil.getCookieByName(request, "token"));
			//修改角色表，角色权限映射表删除所有数据后重新增加
			if(isCreated&&isLeagal){
				//被修改的权限集A、数据库原有权限集B、用户权限集C
				//新增A-B,移除B-A
				boolean isSucess = userService.roleUpdate(meanFList,role.getName());
				if(isSucess){
					return new Result<String>(Constant.CODE_FOR_SUCCESSR,"修改角色信息成功！");
				}
				return new Result<String>(Constant.CODE_FOR_FAILURER,"修改角色信息失败，可能是数据库事物问题，请查看日志！");
			}else {
				logger.warn("用户"+user_id+"试图进行越权操作，请关注！");
				return new Result<String>(Constant.CODE_FOR_FAILURER,"权限不足，不允许修改！");
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			return new Result<String>(Constant.CODE_FOR_FAILURER,e.getMessage());
		}
	}
	
	//删除角色信息
	@RequestMapping(value = "/deleteRole", method = RequestMethod.POST)
	public @ResponseBody Result<?> deleteRole(@RequestBody JSONObject requestJson,HttpServletRequest request) {
		String role_name = requestJson.getString("role_name");
		Token token = TokenUtil.tokenMap.get(TokenUtil.getCookieByName(request, "token"));
		int user_id = token.getUser().getId();
		//验证角色是该用户创建的，是则可以删除，或者角色等级关系属于下发等级，也可以删除
		boolean isCreated = userService.checkIsCreated(user_id,role_name);
		boolean isBelong = userService.checkIsBelong(token.getRole().getRole_level_code()+"/"+token.getRole().getId(),role_name);
		if(isCreated||isBelong){
			//删除角色：解除角色相关用户的有效配置，删除角色，删除映射关系
			boolean isSuccess = userService.deleteRole(role_name);
			if(isSuccess){
				return new Result<String>(Constant.CODE_FOR_SUCCESSR,"删除角色成功！");
			}
			return new Result<String>(Constant.CODE_FOR_FAILURER,"数据库在删除角色出现问题！");
		}
		return new Result<String>(Constant.CODE_FOR_FAILURER,"权限不足！");
	}
	
	@RequestMapping(value = "/userGetSecurityQ", method = RequestMethod.POST)
	public @ResponseBody Result<?> userGetSecurityQ(HttpServletRequest request){
		try {
			User user = TokenUtil.tokenMap.get(TokenUtil.getCookieByName(request, "token")).getUser();
			Result<String> re = new Result<String>("200", "获取密保问题成功！", user.getSecurity_question());
			return re;
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return new Result<>("400","获取密保问题失败！");
	}
	
	@RequestMapping(value = "/changePwd", method = RequestMethod.POST)
	public @ResponseBody Result<?> changePwd(@RequestBody JSONObject json,HttpServletRequest request){
		try {
			String security_answer = json.getString("security_answer");
			User user = TokenUtil.tokenMap.get(TokenUtil.getCookieByName(request, "token")).getUser();
			if(security_answer.equals(user.getSecurity_answer())){
				String pass_word = json.getString("pass_word");
				userService.changePwd(user.getId(),pass_word);
				return new Result<>("200","修改密码成功！");
			}
			return new Result<>("400","密保答案有误,修改密码失败！");
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return new Result<>("400","修改密码失败！");
	}
}
