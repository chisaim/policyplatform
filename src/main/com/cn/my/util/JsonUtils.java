package com.cn.my.util;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;

//用于前台传入多个平级对象做入参，@requestbody只能注解一个对象，用来反序列化
public class JsonUtils {
	public static String obj2json(Object obj) throws Exception {
		return JSON.toJSONString(obj);
	}

	public static <T> T json2obj(String jsonStr, Class<T> clazz) {
		try {
			return JSON.parseObject(jsonStr, clazz);
		} catch (Exception e) {
			System.err.println("json2obj错误 " + e.getMessage());
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> Map<String, Object> json2map(String jsonStr)
			throws Exception {
		return JSON.parseObject(jsonStr, Map.class);
	}

	public static <T> T map2obj(Map<?, ?> map, Class<T> clazz)
			throws Exception {
		return JSON.parseObject(JSON.toJSONString(map), clazz);
	}

	public static <T> List<T> data2list(List<?> list, Class<T> clazz)
			throws Exception {
		List<T> listT = new ArrayList<T>();
		for (Object t : list) {
			listT.add(json2obj(obj2json(t), clazz));
		}
		return listT;
	}

	// jsonArray转Array,不能定义list再转为array，会被类型擦除
	@SuppressWarnings("unchecked")
	public static <T> T[] JSONArray2Array(JSONArray jsonArray, Class<T> clazz)
			throws Exception {
		T[] array;
		array = (T[]) Array.newInstance(clazz, jsonArray.size());
		int i = 0;
		for (Object json : jsonArray) {
			array[i] = clazz.cast(JsonUtils.json2obj(json.toString(), clazz));
			i++;
		}
		return array;
	}

	public static void main(String[] args) throws Exception {

	}

	public static Object mapToObject(Map<String, Object> map,Class<?> beanClass) {
		if (map == null)
			return null;
		try {
			Object obj = beanClass.newInstance();
			Field[] fields = obj.getClass().getDeclaredFields();
			for (Field field : fields) {
				int mod = field.getModifiers();
				if (Modifier.isStatic(mod) || Modifier.isFinal(mod)) {
					continue;
				}
				field.setAccessible(true);
				field.set(obj, map.get(field.getName()));
			}
			return obj;
		} catch (InstantiationException e) {
			System.err.println(e.getMessage());
		} catch (IllegalAccessException e) {
			System.err.println(e.getMessage());
		}
		return beanClass;
	}

	public static Map<String, Object> objectToMap(Object obj) {
		try {
			if (obj == null) {
				return null;
			}
			Map<String, Object> map = new HashMap<String, Object>();
			Field[] declaredFields = obj.getClass().getDeclaredFields();
			for (Field field : declaredFields) {
				field.setAccessible(true);
				map.put(field.getName(), field.get(obj));
			}
			return map;
		} catch (IllegalArgumentException e) {
			System.err.println(e.getMessage());
		} catch (IllegalAccessException e) {
			System.err.println(e.getMessage());
		}
		return null;
	}

}
