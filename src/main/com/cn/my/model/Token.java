package com.cn.my.model;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.cn.my.util.TimeUtil;

public class Token {
	private static final long EXPIRE_TIME = 1440L;//过期时间，单位是分钟
	private User user;
	private Role role;
	private List<Action> actions;
	//登陆时间
	private Timestamp loginTime;
	//上次操作时间
	private Timestamp latestAction;
	public Token(){
		
	}
	public Token(User user, Timestamp timestamp) {
		super();
		this.user=user;
		this.loginTime=timestamp;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public Role getRole() {
		return role;
	}
	public void setRole(Role role) {
		this.role = role;
	}
	public List<Action> getActions() {
		return actions;
	}
	public void setActions(List<Action> actions) {
		this.actions = actions;
	}
	public Timestamp getLoginTime() {
		return loginTime;
	}
	public void setLoginTime(Timestamp loginTime) {
		this.loginTime = loginTime;
	}
	public Timestamp getLatestAction() {
		return latestAction;
	}
	public void setLatestAction(Timestamp latestAction) {
		this.latestAction = latestAction;
	}
	public boolean ifExpire(Token token){
		return subtractionTimStamp(Long.valueOf(Timestamp.valueOf(String.valueOf(TimeUtil.getCurrentTime())).getTime()), token.getLoginTime().getTime())>EXPIRE_TIME?true:false;
	}
	//返回分钟数
	private long subtractionTimStamp(long last,long first){
		return (last -first) / (1000 * 60);
	}
	
	//筛选分栏信息id
	public static Integer[] getActionIdsFromActions(List<Action> actions){
		Set<Integer> set = new HashSet<Integer>();
		for(Action action:actions){
			set.add(action.getAction_column_id());
		}
		return set.toArray(new Integer[]{});
	}
}
