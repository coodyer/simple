package com.app.server.comm.util;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.security.InvalidParameterException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.Modifier;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.MethodInfo;

import org.springframework.beans.BeanUtils;

import com.app.server.comm.annotation.Column;
import com.app.server.comm.annotation.Table;
import com.app.server.comm.entity.BeanEntity;
import com.app.server.comm.entity.Record;

/**
 * @remark 一个神奇的工具。
 * @author WebSOS
 * @email 644556636@qq.com
 * @blog http://54sb.org
 */
public class PropertUtil {
	static ClassPool pool = ClassPool.getDefault();
	/**
	 * 初始化字节码操作池
	 */
	static {
		try {
			pool.insertClassPath(new ClassClassPath(PropertUtil.class));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 复制对象属性
	 * 
	 * @param sourceObj
	 * @param targetObj
	 * @return
	 * @throws Exception
	 */
	public static Object copyPropres(Object sourceObj, Object targetObj)
			throws Exception {
		Map<String, Object> map = objToMap(sourceObj);
		return mapToObject(targetObj.getClass(), map);
	}

	/**
	 * 获取模型对应的数据库表名
	 * 
	 * @param obj
	 * @return
	 */
	public static String getModelName(Object obj) {
		try {
			Class<?> cla = obj.getClass();
			if (obj instanceof Class) {
				cla = (Class<?>) obj;
			}
			Table table = (Table) cla.getAnnotation(Table.class);
			if (!StringUtil.isNullOrEmpty(table)) {
				if (!StringUtil.isNullOrEmpty(table.value())) {
					return table.value();
				}
			}
			return getModelNameByClass(cla);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * 获取对象多个字段的值
	 * @param obj
	 * @param fieldNames
	 * @return
	 */
	public static List<Object> getFieldValues(Object obj, String... fieldNames) {
		if (StringUtil.isNullOrEmpty(obj)) {
			return null;
		}
		List<Object> values = new ArrayList<Object>(fieldNames.length * 2);
		for (String fieldName : fieldNames) {
			values.add(getFieldValue(obj, fieldName));
		}
		if (StringUtil.isNullOrEmpty(values)) {
			return null;
		}
		return values;
	}
	/**
	 * 获取模型对应的数据库表名
	 * 
	 * @param obj
	 * @return
	 */
	private static String getModelNameByClass(Class<?> cla) {
		try {
			String classNmae = cla.getName();
			String packageName = cla.getPackage().getName();
			String modelName = classNmae.replace(packageName, "")
					.replace(".", "").replace("\\.", "");
			return modelName;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 获取模型对应的数据库表名
	 * 
	 * @param obj
	 * @return
	 */
	public static String getTableName(Object obj) {
		return unParsParaName(getModelName(obj));
	}

	/**
	 * 获取方法参数列表
	 * 
	 * @param method
	 * @return
	 */
	public static List<BeanEntity> getMethodParas(Method method) {
		try {
			Class<?>[] types = method.getParameterTypes();
			if (StringUtil.isNullOrEmpty(types)) {
				return null;
			}
			Annotation[][] annotations = method.getParameterAnnotations();
			CtMethod cm = parseCtMethod(method);
			MethodInfo methodInfo = cm.getMethodInfo();
			CodeAttribute codeAttribute = methodInfo.getCodeAttribute();
			LocalVariableAttribute attr = (LocalVariableAttribute) codeAttribute
					.getAttribute(LocalVariableAttribute.tag);
			CtClass[] cts = cm.getParameterTypes();
			if (StringUtil.isNullOrEmpty(cts)) {
				return null;
			}
			List<BeanEntity> entitys = new ArrayList<BeanEntity>();
			int pos = Modifier.isStatic(cm.getModifiers()) ? 0 : 1;
			for (int i = 0; i < cts.length; i++) {
				BeanEntity entity = new BeanEntity();
				entity.setFieldName(attr.variableName(i + pos));
				entity.setFieldAnnotations(annotations[i]);
				entity.setFieldType(types[i]);
				entitys.add(entity);
			}
			return entitys;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 结果集转为对象
	 * 
	 * @param cla
	 * @param rec
	 * @return
	 */
	public static Object recordToObject(Class<?> cla, Record rec) {
		if (StringUtil.findNull(cla, rec) > -1) {
			return null;
		}
		return mapToObject(cla, rec.getMap());
	}

	/**
	 * 结果集列表转为对象列表
	 * 
	 * @param cla
	 * @param recs
	 * @return
	 */
	public static List<?> recordToObjects(Class<?> cla, List<Record> recs) {
		if (StringUtil.isNullOrEmpty(recs)) {
			return null;
		}
		List<Object> list = new ArrayList<Object>();
		for (Record rec : recs) {
			Object o = mapToObject(cla, rec.getMap());
			list.add(o);
		}
		if (StringUtil.isNullOrEmpty(list)) {
			return null;
		}
		return list;
	}

	/**
	 * map转为对象
	 * 
	 * @param cla
	 * @param sourceMap
	 * @return
	 */
	public static Object mapToObject(Class<?> cla, Map<String, Object> sourceMap) {
		if (StringUtil.findNull(cla, sourceMap) > -1) {
			return null;
		}
		try {
			List<BeanEntity> entitys = getBeanFields(cla);
			Object obj = cla.newInstance();
			for (BeanEntity entity : entitys) {
				Object value = sourceMap.get(entity.getFieldName());
				setProperties(obj, entity.getFieldName(), value);
			}
			return obj;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * map转为对象
	 * 
	 * @param cla
	 * @param sourceMap
	 * @return
	 */
	public static Object mapToObject2(Class<?> cla,
			Map<Object, Object> sourceMap) {
		if (StringUtil.isNullOrEmpty(sourceMap)) {
			return null;
		}
		try {
			List<BeanEntity> entitys = getBeanFields(cla);
			Object obj = cla.newInstance();
			for (BeanEntity entity : entitys) {
				Object value = sourceMap.get(entity.getFieldName());
				setProperties(obj, entity.getFieldName(), value);
			}
			return obj;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 获取模型对于数据库字段名
	 * 
	 * @param field
	 * @return
	 */
	public static String getFieldName(BeanEntity field) {
		Annotation[] annots = field.getFieldAnnotations();
		if (StringUtil.isNullOrEmpty(annots)) {
			return field.getFieldName();
		}
		for (Annotation annot : annots) {
			if (annot instanceof Column) {
				String fieldName = ((Column) annot).value();
				if (!StringUtil.isNullOrEmpty(fieldName)) {
					return fieldName;
				}
			}
		}
		return field.getFieldName();
	}

	/**
	 * 对象转map
	 * 
	 * @param obj
	 * @return
	 */
	public static Map<String, Object> objToMap(Object obj) {
		try {
			List<BeanEntity> entitys = getBeanFields(obj);
			Map<String, Object> map = new HashMap<String, Object>(
					entitys.size() * 2);
			for (BeanEntity entity : entitys) {
				map.put(entity.getFieldName(), entity.getFieldValue());
			}
			return map;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 对象转为map
	 * 
	 * @param obj
	 * @return
	 */
	public static Map<String, Object> objToSqlParaMap(Object obj) {
		try {
			BeanInfo sourceBean = Introspector.getBeanInfo(obj.getClass(),
					java.lang.Object.class);
			PropertyDescriptor[] sourceProperty = sourceBean
					.getPropertyDescriptors();
			if (sourceProperty == null) {
				return null;
			}
			Map<String, Object> map = new HashMap<String, Object>(
					sourceProperty.length * 2);
			for (PropertyDescriptor tmp : sourceProperty) {
				map.put(parsParaName(tmp.getName()), tmp.getReadMethod()
						.invoke(obj));
			}
			return map;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 获取某个对象的class
	 * 
	 * @param obj
	 * @return
	 */
	public static Class<? extends Object> getObjClass(Object obj) {
		if (obj instanceof Class) {
			return (Class<?>) obj;
		}
		return obj.getClass();
	}

	/**
	 * 下划线命名转驼峰式
	 * 
	 * @param paraName
	 * @return
	 */
	public static String parsParaName(String paraName) {
		if (paraName == null) {
			return null;
		}
		if (paraName.indexOf("_") > -1) {
			String[] paraNames = paraName.split("_");
			if (paraNames.length > 1) {
				StringBuilder sb = new StringBuilder();
				sb.append(paraNames[0]);
				for (int i = 1; i < paraNames.length; i++) {
					sb.append(firstUpcase(paraNames[i]));
				}
				return sb.toString();
			}
		}
		return paraName;
	}

	/**
	 * 驼峰式命名转下划线
	 * 
	 * @param paraName
	 * @return
	 */
	public static String unParsParaName(String paraName) {
		char[] chrs = paraName.toCharArray();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < chrs.length; i++) {
			char chr = chrs[i];
			if (i != 0 && Character.isUpperCase(chr)) {
				sb.append("_");
			}
			sb.append(String.valueOf(chr).toLowerCase());
		}
		return sb.toString();
	}

	/**
	 * 设置字段值
	 * 
	 * @param obj
	 *            实例对象
	 * @param propertyName
	 *            属性名
	 * @param value
	 *            新的字段值
	 * @return
	 */
	public static void setProperties(Object object, String propertyName,
			Object value) throws Exception {
		Field field = getField(object.getClass(), propertyName);
		if (StringUtil.isNullOrEmpty(field)) {
			throw new Exception("字段未找到:" + propertyName);
		}
		field.setAccessible(true);
		try {
			Object obj = parseValue(value, field.getType());
			field.set(object, obj);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * value值转换为对应的类型
	 * 
	 * @param value
	 * @param clazz
	 * @return
	 * @throws ParseException
	 */
	public static Object parseValue(Object value, Class<?> clazz)
			throws ParseException {
		if (StringUtil.isNullOrEmpty(value)) {
			if (clazz.isPrimitive()) {
				if (boolean.class.isAssignableFrom(clazz)) {
					return false;
				}
				if (byte.class.isAssignableFrom(clazz)) {
					return 0;
				}
				if (char.class.isAssignableFrom(clazz)) {
					return 0;
				}
				if (short.class.isAssignableFrom(clazz)) {
					return 0;
				}
				if (int.class.isAssignableFrom(clazz)) {
					return 0;
				}
				if (float.class.isAssignableFrom(clazz)) {
					return 0f;
				}
				if (long.class.isAssignableFrom(clazz)) {
					return 0l;
				}
				if (double.class.isAssignableFrom(clazz)) {
					return 0d;
				}
			}
			return value;
		}
		if (Boolean.class.isAssignableFrom(clazz)) {
			value = ((String) value).equals("true") ? true : false;
			return value;
		}
		if (Integer.class.isAssignableFrom(clazz)) {
			value = Integer.valueOf(value.toString());
			return value;
		}
		if (Float.class.isAssignableFrom(clazz)) {
			value = Float.valueOf(value.toString());
			return value;
		}
		if (Long.class.isAssignableFrom(clazz)) {
			value = Long.valueOf(value.toString());
			return value;
		}
		if (Double.class.isAssignableFrom(clazz)) {
			value = Double.valueOf(value.toString());
			return value;
		}
		if (String.class.isAssignableFrom(clazz)) {
			value = value.toString();
			return value;
		}
		if (Date.class.isAssignableFrom(clazz)) {
			if (Date.class.isAssignableFrom(value.getClass())) {
				return value;
			}
			if (StringUtil.isMatcher(value.toString(),
					"[0-9]{4}-[0-9]{1,2}-[0-9]{1,2}")) {
				value = new SimpleDateFormat("yyyy-MM-dd").parse(value
						.toString());
			}
			if (StringUtil
					.isMatcher(value.toString(),
							"^\\d{4}\\D+\\d{1,2}\\D+\\d{1,2}\\D+\\d{1,2}\\D+\\d{1,2}\\D+\\d{1,2}\\D*")) {
				value = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(value
						.toString());
			}
			return value;
		}
		return value;
	}

	/**
	 * 设置集合对象某字段值
	 * 
	 * @param objs
	 * @param fieldName
	 * @param fieldsValue
	 * @return
	 */
	public static List<?> setFieldValues(List<?> objs, String fieldName,
			Object fieldsValue) {
		if (StringUtil.isNullOrEmpty(objs)) {
			return null;
		}
		try {
			for (Object obj : objs) {
				try {
					if (StringUtil.isNullOrEmpty(obj)) {
						continue;
					}
					setProperties(obj, fieldName, fieldsValue);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return objs;
	}

	/**
	 * 获取class的字段列表
	 * 
	 * @param clazz
	 * @return
	 */
	public static List<Field> loadFields(Class<?> clazz) {
		List<Field> fields = new ArrayList<Field>();
		Field[] fieldArgs = clazz.getDeclaredFields();
		for (Field f : fieldArgs) {
			fields.add(f);
		}
		Class<?> superClass = clazz.getSuperclass();
		if (superClass == null) {
			return fields;
		}
		fields.addAll(loadFields(superClass));
		return fields;
	}

	/**
	 * 获取class的字段对象
	 * 
	 * @param clazz
	 * @param fieldName
	 * @return
	 */
	public static Field getField(Class<?> clazz, String fieldName) {
		List<Field> fields = loadFields(clazz);
		if (StringUtil.isNullOrEmpty(fields)) {
			return null;
		}
		for (Field f : fields) {
			if (f.getName().equals(fieldName)) {
				return f;
			}
		}
		return null;
	}

	/**
	 * 首个字符串大写
	 * 
	 * @param s
	 * @return
	 */
	public static String firstUpcase(String s) {
		if (Character.isUpperCase(s.charAt(0))) {
			return s;
		}
		return (new StringBuilder()).append(Character.toUpperCase(s.charAt(0)))
				.append(s.substring(1)).toString();
	}

	/**
	 * 一个神奇的方法：获取对象字段集合
	 * 
	 * @param obj
	 * @return
	 */
	public static List<BeanEntity> getBeanFields(Object obj) {
		Class<? extends Object> cla = getObjClass(obj);
		List<BeanEntity> infos = getClassFields(cla);
		if (StringUtil.isNullOrEmpty(infos)) {
			return infos;
		}
		if (obj instanceof java.lang.Class) {
			return infos;
		}
		for (BeanEntity info : infos) {
			try {
				Field f = info.getSourceField();
				f.setAccessible(true);
				Object value = f.get(obj);
				info.setFieldValue(value);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return infos;
	}

	/**
	 * 一个神奇的方法：获取class字段集合
	 * 
	 * @param cla
	 * @return
	 */
	public static List<BeanEntity> getClassFields(Class<?> cla) {
		try {
			List<Field> fields = loadFields(cla);
			List<BeanEntity> infos = new ArrayList<BeanEntity>();
			for (Field f : fields) {
				if (f.getName().equalsIgnoreCase("serialVersionUID")) {
					continue;
				}
				BeanEntity tmp = new BeanEntity();
				tmp.setSourceField(f);
				tmp.setFieldAnnotations(f.getAnnotations());
				tmp.setFieldName(f.getName());
				tmp.setFieldType(f.getType());
				infos.add(tmp);
			}
			return infos;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * List转为Map。fieldName作为Key，对象作为Value
	 * 
	 * @param objs
	 * @param fieldName
	 * @return
	 */
	public static Map<?, ?> listToMap(List<?> objs, String fieldName) {
		if (StringUtil.isNullOrEmpty(objs)) {
			return null;
		}
		Map<Object, Object> map = new TreeMap<Object, Object>();
		for (Object obj : objs) {
			try {
				Object fieldValue = getFieldValue(obj, fieldName);
				map.put(fieldValue, obj);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (StringUtil.isNullOrEmpty(map)) {
			return null;
		}
		return map;
	}

	/**
	 * 一个神奇的方法：从一个List提取字段名统一的分组
	 * 
	 * @param objs
	 * @param fieldName
	 * @param fieldValue
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static List<?> getGroup(List<?> objs, String fieldName,
			Object fieldValue) {
		if (StringUtil.isNullOrEmpty(objs)) {
			return null;
		}
		Map<Object, List> map = PropertUtil.listToMaps(objs, fieldName);
		if (StringUtil.isNullOrEmpty(map)) {
			return null;
		}
		return map.get(fieldValue);
	}

	/**
	 * 从一个集合获取某指定字段值第一个对象
	 * 
	 * @param objs
	 * @param fieldName
	 * @param fieldValue
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static Object getObjectByList(List<?> objs, String fieldName,
			Object fieldValue) {
		if (StringUtil.isNullOrEmpty(objs)) {
			return null;
		}
		Map map = PropertUtil.listToMap(objs, fieldName);
		if (StringUtil.isNullOrEmpty(map)) {
			return null;
		}
		return map.get(fieldValue);
	}

	/**
	 * 一个神奇的方法：一个List根据某个字段排序
	 * 
	 * @param objs
	 * @param fieldName
	 * @return
	 */
	public static List<?> doSeq(List<?> objs, String fieldName) {
		return doSeq(objs, fieldName, null);
	}

	/**
	 * 一个神奇的方法：一个List根据某个字段排序
	 * 
	 * @param objs
	 * @param fieldName
	 * @param isDesc
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static List<?> doSeq(List<?> objs, String fieldName, Boolean isDesc) {
		if (StringUtil.isNullOrEmpty(objs)) {
			return null;
		}
		Map<Object, List> maps = listToMaps(objs, fieldName);
		List list = new ArrayList();
		for (Object key : maps.keySet()) {
			try {
				list.addAll(maps.get(key));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (StringUtil.isNullOrEmpty(isDesc)) {
			isDesc = false;
		}
		if (isDesc) {
			Collections.reverse(list);
		}
		return list;
	}

	/**
	 * 移除对象当某个字段值为Null
	 * 
	 * @param objs
	 * @param fieldName
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static List<?> removeListFieldIsNull(List<?> objs, String fieldName) {
		if (StringUtil.isNullOrEmpty(objs)) {
			return null;
		}
		Map<Object, List> map = listToMaps(objs, fieldName);
		List<Object> newList = new ArrayList<Object>();
		for (Object key : map.keySet()) {
			if (StringUtil.isNullOrEmpty(key)) {
				continue;
			}
			newList.addAll(map.get(key));
		}
		if (StringUtil.isNullOrEmpty(newList)) {
			return null;
		}
		return newList;

	}

	/**
	 * 一个List转为Map，fieldName作为Key，所有字段值相同的组成List作为value
	 * 
	 * @param objs
	 * @param fieldName
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Map<Object, List> listToMaps(List objs, String fieldName) {
		if (StringUtil.isNullOrEmpty(objs)) {
			return null;
		}
		Map<Object, List> map = new TreeMap<Object, List>();
		List<Object> list;
		for (Object obj : objs) {
			try {
				Object fieldValue = getFieldValue(obj, fieldName);
				if (map.containsKey(fieldValue)) {
					map.get(fieldValue).add(obj);
					continue;
				}
				list = new ArrayList<Object>();
				list.add(obj);
				map.put(fieldValue, list);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (StringUtil.isNullOrEmpty(map)) {
			return null;
		}
		return map;
	}

	/**
	 * 获取对象某个字段值
	 * 
	 * @param obj
	 * @param fieldName
	 * @return
	 */
	public static Object getFieldValue(Object obj, String fieldName) {
		if (StringUtil.isNullOrEmpty(obj)) {
			return null;
		}
		Field f = getField(obj.getClass(), fieldName);
		if (StringUtil.isNullOrEmpty(f)) {
			return null;
		}
		f.setAccessible(true);
		try {
			return f.get(obj);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 获取方法的类
	 * 
	 * @param method
	 * @return
	 */
	public static Class<?> getMethodClass(Method method) {
		Class<?> cla = (Class<?>) PropertUtil.getFieldValue(method, "clazz");
		return cla;
	}

	/**
	 * 获取List对象某个字段的值组成新List
	 * 
	 * @param objs
	 * @param fieldName
	 * @return
	 */
	public static List<?> getFieldValues(List<?> objs, String fieldName) {
		if (StringUtil.isNullOrEmpty(objs)) {
			return null;
		}
		List<Object> list = new ArrayList<Object>();
		Object value;
		for (Object obj : objs) {
			value = getFieldValue(obj, fieldName);
			list.add(value);
		}
		if (StringUtil.isNullOrEmpty(objs)) {
			return null;
		}
		return list;
	}

	/**
	 * 获取对象字段列表
	 * 
	 * @param cla
	 * @return
	 */
	public static List<String> getFieldNames(Class<?> cla) {
		Field[] fields = cla.getDeclaredFields();
		List<String> fieldNames = new ArrayList<String>();
		for (Field field : fields) {
			fieldNames.add(field.getName());
		}
		return fieldNames;
	}

	/**
	 * 把一个List<Record>按照指定字段排序
	 * 
	 * @param recordList
	 *            集合
	 * @param fieldName
	 *            字段名
	 * @param isDesc
	 *            是否倒序
	 * @return
	 */
	public static List<Record> doRecordSeq(List<Record> recordList,
			String fieldName, Boolean isDesc) {
		if (StringUtil.isNullOrEmpty(recordList)) {
			return null;
		}
		if (StringUtil.isNullOrEmpty(fieldName)) {
			return recordList;
		}
		if (StringUtil.isNullOrEmpty(isDesc)) {
			isDesc = false;
		}
		Map<String, List<Record>> recordMap = getAllRecordBySameField(
				recordList, fieldName);
		List<Record> list = new ArrayList<Record>();
		if (!isDesc) {
			for (String key : recordMap.keySet()) {
				List<Record> tmpList = recordMap.get(key);
				if (StringUtil.isNullOrEmpty(tmpList)) {
					continue;
				}
				list.addAll(tmpList);
			}
			return list;
		}
		List<String> keyList = new ArrayList<String>(recordMap.keySet());
		for (int i = keyList.size(); i > 0; i--) {
			String key = keyList.get(i - 1);
			if (StringUtil.isNullOrEmpty(key)) {
				continue;
			}
			List<Record> tmpList = recordMap.get(key);
			if (StringUtil.isNullOrEmpty(tmpList)) {
				continue;
			}
			list.addAll(tmpList);
		}
		return list;
	}

	/**
	 * 根据字段名和字段值统计每个字段出现得List集合
	 * 
	 * @param recordList
	 *            record列表
	 * @param fieldName
	 *            字段名
	 * @return
	 */
	public static Map<String, List<Record>> getAllRecordBySameField(
			List<Record> recordList, String fieldName) {
		if (recordList == null || recordList.isEmpty()
				|| StringUtil.isNullOrEmpty(fieldName)) {
			return null;
		}
		Map<String, Record> map = listRecordToMap(recordList, fieldName);
		Map<String, List<Record>> finalMap = new TreeMap<String, List<Record>>();
		for (String key : map.keySet()) {
			List<Record> records = getRecordBySameField(recordList, fieldName,
					key);
			if (!StringUtil.isNullOrEmpty(records)) {
				finalMap.put(key, records);
			}
		}
		if (StringUtil.isNullOrEmpty(finalMap)) {
			return null;
		}
		return finalMap;
	}

	/**
	 * 根据字段名和字段值取出Record里值相同的record对象
	 * 
	 * @param recordList
	 *            record列表
	 * @param fieldName
	 *            字段名
	 * @param fieldValue
	 *            字段值
	 * @return
	 */
	public static List<Record> getRecordBySameField(List<Record> recordList,
			String fieldName, String fieldValue) {
		if (StringUtil.findEmptyIndex(recordList, fieldName, fieldValue) > -1) {
			return null;
		}
		List<Record> finalList = new ArrayList<Record>();
		String tmpValue = null;
		for (Record tmp : recordList) {
			if (StringUtil.isNullOrEmpty(tmp)) {
				continue;
			}
			tmpValue = StringUtil.toString(tmp.get(fieldName));
			if (tmpValue != null && tmpValue.equals(fieldValue)) {
				finalList.add(tmp);
			}
		}
		return finalList;
	}

	/**
	 * 把一个List<Record>按照指定字段填充map集合
	 * 
	 * @param recordList
	 *            list集合
	 * @param keyField
	 *            字段名
	 * @return
	 */
	public static Map<String, Record> listRecordToMap(List<Record> recordList,
			String keyField) {
		if (recordList == null || recordList.isEmpty()) {
			return null;
		}
		String key = null;
		Map<String, Record> map = new TreeMap<String, Record>();
		for (Record rec : recordList) {
			if (StringUtil.isNullOrEmpty(rec)) {
				continue;
			}
			key = StringUtil.toString(rec.get(keyField));
			if (StringUtil.isNullOrEmpty(key)) {
				key = "-1";
			}
			map.put(key, rec);
		}
		return map;
	}
	/**
	 * 将对象某些字段置空
	 * @param obj
	 * @param fieldNames
	 */
	@SuppressWarnings("unchecked")
	public static void removeFields(Object obj, String... fieldNames) {
		if (StringUtil.isNullOrEmpty(obj)) {
			return;
		}
		List<BeanEntity> fields = PropertUtil.getBeanFields(obj);
		Map<String, BeanEntity> map = (Map<String, BeanEntity>) listToMap(
				fields, "fieldName");
		for (String tmp : fieldNames) {
			try {
				if (map.containsKey(tmp)) {
					BeanEntity entity = map.get(tmp);
					PropertUtil.setProperties(obj, entity.getFieldName(), null);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}
	/**
	 * 清理其余字段，仅保留对象某些字段
	 * @param obj
	 * @param fieldNames
	 */
	@SuppressWarnings("unchecked")
	public static void accepFields(Object obj, String... fieldNames) {
		if (StringUtil.isNullOrEmpty(obj)) {
			return;
		}
		List<BeanEntity> fields = PropertUtil.getBeanFields(obj);
		Map<String, BeanEntity> map = (Map<String, BeanEntity>) listToMap(
				fields, "fieldName");
		for (String tmp : fieldNames) {
			try {
				if (!map.containsKey(tmp)) {
					BeanEntity entity = map.get(tmp);
					PropertUtil.setProperties(obj, entity.getFieldName(), null);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}
	/**
	 * 对象相同字段组成新list
	 * @param list
	 * @param cla
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static List getNewList(List list, Class cla) {
		if (StringUtil.findNull(list, cla) > -1) {
			return null;
		}
		List ls = new ArrayList();
		for (Object obj : list) {
			try {
				Object newObj = cla.newInstance();
				BeanUtils.copyProperties(obj, newObj);
				ls.add(newObj);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return ls;
	}

	/**
	 * 从对象中获取目标方法
	 * 
	 * @param methods
	 *            方法数组
	 * @param methodName
	 *            方法名称
	 * @param paras
	 *            参数列表
	 * @return
	 */
	public static Method getTargeMethod(Method[] methods, String methodName,
			Object... paras) {
		for (Method m : methods) {
			if (isTargeMethod(m, methodName, paras)) {
				return m;
			}
		}
		return null;
	}

	/**
	 * 判断目标是否是当前方法
	 * 
	 * @param method
	 *            当前方法
	 * @param methodName
	 *            目标方法名
	 * @param paras
	 *            目标方法参数列表
	 * @return
	 */
	private static boolean isTargeMethod(Method method, String methodName,
			Object... paras) {
		System.out.println("当前方法:" + method.getName() + ",目标方法:" + methodName);
		if (!method.getName().equals(methodName)) {
			return false;
		}
		Class<?>[] clas = method.getParameterTypes();
		if (StringUtil.isNullOrEmpty(clas) && StringUtil.isNullOrEmpty(paras)) {
			return true;
		}
		if (StringUtil.isNullOrEmpty(clas) || StringUtil.isNullOrEmpty(paras)) {
			return false;
		}
		if (clas.length != paras.length) {
			return false;
		}
		for (int i = 0; i < clas.length; i++) {
			if (paras[i] == null) {
				continue;
			}
			System.out.println("方法参数检测:" + paras[i].getClass().getName() + ":"
					+ clas[i].getName());
			if (!clas[i].isAssignableFrom(paras[i].getClass())) {
				return false;
			}
		}
		return true;
	}
	public static List<Method> loadMethods(Class<?> clazz) {
		List<Method> methods = new ArrayList<Method>(Arrays.<Method> asList(clazz
				.getDeclaredMethods()));
		if (!StringUtil.isNullOrEmpty(clazz.getSuperclass())) {
			methods.addAll(loadMethods(clazz.getSuperclass()));
		}
		return methods;
	}
	
	public static CtMethod parseCtMethod(Method method) {
		try {
			Class<?> clazz = getMethodClass(method);
			CtClass cc = pool.get(clazz.getName());
			Class<?>[] types = method.getParameterTypes();
			List<CtClass> list = new ArrayList<CtClass>();
			if (!StringUtil.isNullOrEmpty(types)) {
				if (!StringUtil.isNullOrEmpty(types)) {
					for (int i = 0; i < types.length; i++) {
						list.add(pool.get(types[i].getName()));
					}
				}
			}
			CtClass[] paraTypes = list.toArray(new CtClass[] {});
			if (StringUtil.isNullOrEmpty(paraTypes)) {
				paraTypes = null;
			}
			CtMethod mthd = cc.getDeclaredMethod(method.getName(), paraTypes);
			return mthd;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	/**
	 * 加载枚举的信息
	 * @param clazz
	 * @return
	 */
	public static <T> Map<String,Record> loadEnumRecord(Class<T> clazz) {
        if (!clazz.isEnum()) {
            throw new InvalidParameterException();
        }
        try {
            T[] enumConstants = clazz.getEnumConstants();
            Field[] fields=clazz.getDeclaredFields();
            if(StringUtil.isNullOrEmpty(fields)){
            	return null;
            }
            List<Field> fieldList=new ArrayList<Field>();
            for(Field field:fields){
            	try {
            		if(!(clazz.isAssignableFrom(field.getType()))&&!(("[L"+clazz.getName()+";").equals(field.getType().getName()))){
            			fieldList.add(field);
            		}
				} catch (Exception e) {
				}
            }
            if(StringUtil.isNullOrEmpty(fieldList)){
            	return null;
            }
            Map<String,Record> records=new HashMap<String,Record>();
            for (T ec : enumConstants) {
            	Record record=new Record();
            	for(Field field:fieldList){
            		Object value =getFieldValue(ec, field.getName());
            		record.put(field.getName(), value);
            	}
            	records.put(ec.toString(),record);
            }
            return records;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
	
	/**
	 * 加载枚举的信息
	 * @param clazz
	 * @return
	 */
	public static <T> Object loadEnumByField(Class<T> clazz,String fieldName,Object value) {
        if (!clazz.isEnum()) {
            throw new InvalidParameterException();
        }
        try {
            T[] enumConstants = clazz.getEnumConstants();
            for (T ec : enumConstants) {
            		Object currValue =getFieldValue(ec, fieldName);
				if (value == currValue ||currValue.equals(value)){
            			return ec;
            	}
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
	public static void main(String[] args) {

	}
}