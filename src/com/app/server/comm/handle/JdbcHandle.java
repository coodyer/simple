package com.app.server.comm.handle;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.Resource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.app.server.comm.annotation.Table;
import com.app.server.comm.entity.BeanEntity;
import com.app.server.comm.entity.Record;
import com.app.server.comm.entity.SQLEntity;
import com.app.server.comm.entity.Where;
import com.app.server.comm.page.Pager;
import com.app.server.comm.util.AspectUtil;
import com.app.server.comm.util.PropertUtil;
import com.app.server.comm.util.SpringContextHelper;
import com.app.server.comm.util.StringUtil;

/**
 * 数据库操作
 * @author DH
 *
 */
@Repository
public class JdbcHandle {
	

	/**
	 * 主库数据源
	 */
	@Resource
	private JdbcTemplate jdbcTemplate;
	
	/**
	 * 执行SQL查询语句
	 * 
	 * @param sql
	 * @param paraMap
	 *            参数map容器
	 * @return 结果集
	 */
	private List<Record> baseQuery(String sql, Map<Integer, Object> paraMap) {
		Long threadId = Thread.currentThread().getId();
		try {
			Object[] paras = getParams(paraMap, threadId);
			JdbcTemplate currTemplate=getTemplate();
			List<Map<String, Object>> results = currTemplate.queryForList(sql, paras);
			List<Record> list = mapSetToList(results);
			if (StringUtil.isNullOrEmpty(list)) {
				return null;
			}
			return list;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 执行SQL更新语句
	 * 
	 * @param sql
	 *            语句
	 * @param paraMap
	 *            参数
	 * @return
	 */
	private Integer baseUpdate(final String sql,
			final Map<Integer, Object> paraMap) {
		final Long threadId = Thread.currentThread().getId();
		try {
			if (!sql.toLowerCase().trim().startsWith("insert")) {
				Object[] paras = getParams(paraMap, threadId);
				return jdbcTemplate.update(sql, paras);
			}
			KeyHolder keyHolder = new GeneratedKeyHolder();
			Integer code = jdbcTemplate.update(new PreparedStatementCreator() {
				@Override
				public PreparedStatement createPreparedStatement(Connection con)
						throws SQLException {
					PreparedStatement ps = con.prepareStatement(sql,
							PreparedStatement.RETURN_GENERATED_KEYS);
					setParams(ps, paraMap, threadId);
					return ps;
				}
			}, keyHolder);
			List<Map<String, Object>> list=keyHolder.getKeyList();
			if (StringUtil.isNullOrEmpty(list)||list.size()>1) {
				return code;
			}
			if (StringUtil.isNullOrEmpty(list.get(0))||list.get(0).size()>1) {
				return code;
			}
			try {
				Integer obj=keyHolder.getKey().intValue();
				return obj;
			} catch (Exception e) {
				e.printStackTrace();
				return code;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}

	
	/**
	 * 解析分页条件
	 * 
	 * @param pager
	 * @return
	 */
	private String parsPagerSQL(Pager pager) {
		// 封装分页条件
		if (StringUtil.isNullOrEmpty(pager.getCurrentPage())) {
			pager.setCurrentPage(1);
		}
		if (StringUtil.isNullOrEmpty(pager.getPageSize())) {
			pager.setPageSize(10);
		}
		Integer startRows = (pager.getCurrentPage() - 1) * pager.getPageSize();
		return MessageFormat.format(" limit {0},{1} ",
				String.valueOf(startRows), String.valueOf(pager.getPageSize()));
	}
	
	/**
	 * 解析对象条件、where条件、分页条件
	 * 
	 * @param obj
	 *            对象条件
	 * @param where
	 *            where条件
	 * @param pager
	 *            分页条件
	 * @return
	 */
	public SQLEntity parseSQL(Object obj, Where where, Pager pager,
			String orderField, Boolean isDesc) {
		if (obj == null) {
			return null;
		}
		// 获取表名
		String tableName = PropertUtil.unParsParaName(getModelName(obj));
		StringBuilder sb = new StringBuilder(MessageFormat.format(
				"select * from {0} where 1=1 ", tableName));
		Map<Integer, Object> paraMap = new TreeMap<Integer, Object>();
		// 封装对象内置条件,默认以等于
		if (!(obj instanceof java.lang.Class)) {
			List<BeanEntity> prpres = PropertUtil.getBeanFields(obj);
			if (prpres == null || prpres.isEmpty()) {
				return null;
			}
			for (BeanEntity entity : prpres) {
				if (entity.getFieldValue() == null) {
					continue;
				}
				sb.append(MessageFormat.format(" and {0}=? ",
						PropertUtil.getFieldName(entity)));
				paraMap.put(paraMap.size() + 1, entity.getFieldValue());
			}
		}
		// 封装where条件
		if (!StringUtil.isNullOrEmpty(where)
				&& !StringUtil.isNullOrEmpty(where.getWheres())) {
			List<Where.ThisWhere> wheres = where.getWheres();
			for (Where.ThisWhere childWhere : wheres) {
				sb.append(MessageFormat.format(" and {0} {1} ",
						childWhere.getFieldName(), childWhere.getSymbol()));
				if (StringUtil.isNullOrEmpty(childWhere.getFieldValues())) {
					continue;
				}
				String inParaSql = StringUtil.getInPara(childWhere
						.getFieldValues().size());
				sb.append(MessageFormat.format(" ({0})  ", inParaSql));
				for (Object value : childWhere.getFieldValues()) {
					paraMap.put(paraMap.size() + 1, value);
				}
			}
		}
		// 封装排序条件
		if (!StringUtil.isNullOrEmpty(orderField)) {
			sb.append(MessageFormat.format(" order by {0}", orderField));
			if (isDesc != null && isDesc) {
				sb.append(" desc ");
			}
		}
		// 封装分页条件
		if (!StringUtil.isNullOrEmpty(pager)) {
			sb.append(parsPagerSQL(pager));
		}
		return new SQLEntity(sb.toString(), paraMap);
	}
	
	/**
	 * 查询功能区 -start
	 */

	/**
	 * 执行SQL语句
	 * 
	 * @param sql
	 * @param paraMap
	 *            参数map容器
	 * @return 结果集
	 */
	public List<Record> doQuery(String sql, Object... paras) {
		Map<Integer, Object> paraMap = new TreeMap<Integer, Object>();
		if (!StringUtil.isNullOrEmpty(paras)) {
			for (Object obj : paras) {
				paraMap.put(paraMap.size() + 1, obj);
			}
		}
		return baseQuery(sql, paraMap);
	}

	/**
	 * 执行SQL语句
	 * 
	 * @param sql
	 * @return
	 */
	public List<Record> doQuery(String sql) {
		return baseQuery(sql, null);
	}

	/**
	 * 执行SQL语句,返回对象
	 * 
	 * @param sql
	 * @return
	 */
	public List<?> doQueryBean(Class<?> clazz,String sql) {
		return doQueryBean(clazz, sql,null);
	}
	/**
	 * 执行SQL语句,返回对象
	 * 
	 * @param sql
	 * @return
	 */
	public List<?> doQueryBean(Class<?> clazz,String sql,Object ... paras) {
		List<Record>  recs= doQuery(sql, paras);
		if(StringUtil.isNullOrEmpty(recs)){
			return null;
		}
		return PropertUtil.recordToObjects(clazz, recs);
	}
	/**
	 * 执行SQL语句,返回对象
	 * 
	 * @param sql
	 * @return
	 */
	public Object doQueryBeanFirst(Class<?> clazz,String sql) {
		List<Record>  recs= baseQuery(sql, null);
		if(StringUtil.isNullOrEmpty(recs)){
			return null;
		}
		return PropertUtil.recordToObject(clazz, recs.get(0));
	}
	/**
	 * 执行SQL语句,返回对象
	 * 
	 * @param sql
	 * @return
	 */
	public Object doQueryBeanFirst(Class<?> clazz,String sql,Object... paras) {
		Record  rec= doQueryFirst(sql, paras);
		if(StringUtil.isNullOrEmpty(rec)){
			return null;
		}
		return PropertUtil.recordToObject(clazz, rec);
	}
	/**
	 * 执行SQL语句
	 * 
	 * @param sql
	 * @return
	 */
	public Record doQueryFirst(String sql, Object... paras) {
		List<Record> list = doQuery(sql, paras);
		if (StringUtil.isNullOrEmpty(list)) {
			return null;
		}
		return list.get(0);
	}

	/**
	 * 执行SQL语句
	 * 
	 * @param sql
	 * @return
	 */
	public Record doQueryFirst(String sql) {
		return doQueryFirst(sql, new Object[] {});
	}

	/**
	 * 执行SQL语句获得Double结果
	 * 
	 * @param sql
	 *            语句
	 * @param paraMap
	 *            参数集
	 * @return 整数
	 */
	public Double doQueryDouble(String sql, Object... paras) {
		List<Record> records = doQuery(sql, paras);
		if (StringUtil.isNullOrEmpty(records)) {
			return -1d;
		}
		Record rec = records.get(0);
		for (String key : rec.keySet()) {
			try {
				if (StringUtil.isNullOrEmpty(rec.get(key))) {
					continue;
				}
				Double size = Double.valueOf(rec.get(key).toString());
				return size;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		sql = formatSql(sql);
		if (sql.contains("select count(") || sql.contains("select sum(")
				|| sql.contains("select avg(")) {
			return 0d;
		}
		return -1d;
	}

	/**
	 * 执行SQL语句获得Integer结果
	 * 
	 * @param sql
	 *            语句
	 * @param paraMap
	 *            参数集
	 * @return 整数
	 */
	public Integer doQueryInteger(String sql, Object... paras) {
		return doQueryDouble(sql, paras).intValue();
	}
	/**
	 * 执行SQL语句获得Integer结果
	 * 
	 * @param sql
	 *            语句
	 * @param paraMap
	 *            参数集
	 * @return 整数
	 */
	public Long doQueryLong(String sql, Object... paras) {
		return Long.valueOf(doQueryDouble(sql, paras).intValue());
	}
	/**
	 * 执行SQL语句获得Float结果
	 * 
	 * @param sql
	 *            语句
	 * @param paraMap
	 *            参数集
	 * @return 整数
	 */
	public Float doQueryFloat(String sql, Object... paras) {
		return Float.valueOf(doQueryDouble(sql, paras).toString());
	}
	/**
	 * 根据多个字段查询对象
	 * 
	 * @param cla
	 *            类
	 * @param paraMap
	 *            条件集合
	 * @return
	 */
	public List<?> findBean(Class<?> cla,
			Map<String, Object> paraMap) {
		List<Record> recs = findRecord(cla, paraMap, null, null);
		return PropertUtil.recordToObjects(cla, recs);
	}

	/**
	 * 根据多个字段查询对象
	 * 
	 * @param cla
	 *            类
	 * @param paraMap
	 *            条件集合
	 * @return
	 */
	public List<?> findBean(Class<?> cla,
			Map<String, Object> paraMap, String orderField, Boolean isDesc) {
		List<Record> recs = findRecord(cla, paraMap, orderField, isDesc);
		return PropertUtil.recordToObjects(cla, recs);
	}
	/**
	 * 根据字段查询对象
	 * 
	 * @param cla
	 *            类
	 * @param fieldName
	 *            字段名
	 * @param fieldValue
	 *            字段值,可支持集合与数组IN查询
	 * @return
	 */
	public List<?> findBean(Class<?> cla,
			String fieldName, Object fieldValue, String orderField,
			Boolean isDesc) {
		List<Record> recs = findRecord(cla, fieldName, fieldValue, orderField,
				isDesc);
		return PropertUtil.recordToObjects(cla, recs);
	}

	/**
	 * 根据字段查询对象
	 * 
	 * @param cla
	 * @param fieldName
	 * @param fieldValue
	 * @return
	 */
	public List<?> findBean(Class<?> cla, String fieldName,
			Object fieldValue) {
		return findBean(cla, fieldName, fieldValue, null, null);
	}

	
	/**
	 * 根据对象查询对象集合
	 * 
	 * @param obj
	 *            对象
	 * @param where
	 *            where条件
	 * @param pager
	 *            分页对象
	 * @return
	 */
	public List<?> findBean(Object obj, Where where, Pager pager) {
		List<Record> list = findRecord(obj, where, pager, null, null);
		return PropertUtil.recordToObjects(getObjectClass(obj), list);
	}

	/**
	 * 根据对象查询对象集合
	 * 
	 * @param obj
	 *            对象
	 * @param where
	 *            where条件
	 * @param pager
	 *            分页对象
	 * @return
	 */
	public List<?> findBean(Object obj, Where where, Pager pager,
			String orderField, Boolean isDesc) {
		List<Record> list = findRecord(obj, where, pager, orderField, isDesc);
		return PropertUtil.recordToObjects(getObjectClass(obj), list);
	}

	/**
	 * 根据对象查询对象集合
	 * 
	 * @param obj
	 *            对象
	 * @param where
	 *            where条件
	 * @param pager
	 *            分页对象
	 * @return
	 */
	public List<?> findBean(Object obj, Where where,
			String orderField, Boolean isDesc) {
		List<Record> list = findRecord(obj, where, null, orderField, isDesc);
		return PropertUtil.recordToObjects(getObjectClass(obj), list);
	}

	/**
	 * 根据对象查询对象集合
	 * 
	 * @param obj
	 *            对象
	 * @param where
	 *            where条件
	 * @param pager
	 *            分页对象
	 * @return
	 */
	public List<?> findBean(Object obj, Pager pager) {
		List<Record> list = findRecord(obj, null, pager, null, null);
		return PropertUtil.recordToObjects(getObjectClass(obj), list);
	}

	/**
	 * 根据对象查询对象
	 * 
	 * @param obj
	 *            对象条件
	 * @param where
	 *            where条件
	 * @return
	 */
	public List<?> findBean(Object obj, Where where) {
		List<Record> list = findRecord(obj, where, null, null, null);
		return PropertUtil.recordToObjects(getObjectClass(obj), list);
	}

	/**
	 * 根据obj内部字段名和值进行查询，默认条件为等于
	 * 
	 * @param obj
	 * @return
	 */
	public List<?> findBean(Object obj) {
		List<Record> list = findRecord(obj, null, null, null, null);
		return PropertUtil.recordToObjects(getObjectClass(obj), list);
	}
	
	
	/**
	 * 根据字段查询对象
	 * 
	 * @param cla
	 * @param fieldName
	 * @param fieldValue
	 * @param orderField
	 * @param isDesc
	 * @return
	 */
	public Object findBeanFirst(Class<?> cla, String fieldName,
			Object fieldValue, String orderField, Boolean isDesc) {
		@SuppressWarnings("unchecked")
		List<Object> list = (List<Object>) findBean(cla, fieldName,
				fieldValue, orderField, isDesc);
		if (StringUtil.isNullOrEmpty(list)) {
			return null;
		}
		return list.get(0);
	}

	/**
	 * 根据字段查询对象
	 * 
	 * @param cla
	 * @param fieldName
	 * @param fieldValue
	 * @return
	 */
	public Object findBeanFirst(Class<?> cla, String fieldName,
			Object fieldValue) {
		return findBeanFirst(cla, fieldName, fieldValue, null, null);
	}
	/**
	 * 根据对象条件进行查询
	 * 
	 * @param cla
	 * @param fieldName
	 * @param fieldValue
	 * @param orderField
	 * @param isDesc
	 * @return
	 */
	public Object findBeanFirst(Object obj, Where where,
			String orderField, Boolean isDesc) {
		List<Record> list = findRecord(obj, where, null, orderField, isDesc);
		if (StringUtil.isNullOrEmpty(list)) {
			return null;
		}
		return PropertUtil.recordToObject(getObjectClass(obj), list.get(0));
	}

	/**
	 * 根据对象条件进行查询
	 * 
	 * @param obj
	 * @param where
	 * @return
	 */
	public Object findBeanFirst(Object obj, Where where) {
		return findBeanFirst(obj, where, null, null);
	}

	/**
	 * 根据对象条件进行查询
	 * 
	 * @param obj
	 * @return
	 */
	public Object findBeanFirst(Object obj) {
		return findBeanFirst(obj, null, null, null);
	}
	/**
	 * 根据字段查询结果集
	 * 
	 * @param cla
	 * @param paraMap
	 * @return
	 */
	public Object findBeanFirst(Class<?> cla,
			Map<String, Object> paraMap) {
		Record record = findRecordFirst(cla, paraMap, null, null);
		return PropertUtil.mapToObject(cla, record.getMap());
	}
	/**
	 * 根据对象查询结果集
	 * 
	 * @param obj
	 * @return
	 */
	public List<Record> findRecord(Object obj) {
		SQLEntity sqlEntity = parseSQL(obj, null, null, null, null);
		return baseQuery(sqlEntity.getSql(), sqlEntity.getParaMap());
	}

	/**
	 * 根据对象查询结果集
	 * 
	 * @param obj
	 * @param pager
	 * @return
	 */
	public List<Record> findRecord(Object obj, Pager pager) {
		SQLEntity sqlEntity = parseSQL(obj, null, pager, null, null);
		return baseQuery(sqlEntity.getSql(), sqlEntity.getParaMap());
	}
	/**
	 * 根据对象查询结果集
	 * 
	 * @param obj
	 *            对象条件
	 * @param where
	 *            where条件
	 * @return
	 */
	public List<Record> findRecord(Object obj, Where where,
			String orderField, Boolean isDesc) {
		return findRecord(obj, where, null, orderField, isDesc);
	}

	/**
	 * 根据对象查询结果集
	 * 
	 * @param obj
	 *            对象条件
	 * @param where
	 *            where条件
	 * @return
	 */
	public List<Record> findRecord(Object obj, String orderField,
			Boolean isDesc) {
		return findRecord(obj, null, null, orderField, isDesc);
	}

	/**
	 * 根据对象查询结果集
	 * 
	 * @param obj
	 *            对象条件
	 * @param where
	 *            where条件
	 * @param pager
	 *            分页信息
	 * @return
	 */
	public List<Record> findRecord(Object obj, Where where, Pager pager,
			String orderField, Boolean isDesc) {
		SQLEntity sqlEntity = parseSQL(obj, where, pager, orderField,
				isDesc);
		return baseQuery(sqlEntity.getSql(), sqlEntity.getParaMap());
	}

	/**
	 * 根据对象查询结果集
	 * 
	 * @param obj
	 * @param where
	 * @param pager
	 * @return
	 */
	public List<Record> findRecord(Object obj, Where where, Pager pager) {
		SQLEntity sqlEntity = parseSQL(obj, where, pager, null, null);
		return baseQuery(sqlEntity.getSql(), sqlEntity.getParaMap());
	}

	/**
	 * 根据对象查询结果集
	 * 
	 * @param obj
	 * @param where
	 * @return
	 */
	public List<Record> findRecord(Object obj, Where where) {
		SQLEntity sqlEntity = parseSQL(obj, where, null, null, null);
		return baseQuery(sqlEntity.getSql(), sqlEntity.getParaMap());
	}

	


	/**
	 * 根据字段查询结果集
	 * 
	 * @param cla
	 *            类
	 * @param fieldName
	 *            字段名
	 * @param fieldValue
	 *            字段值
	 * @return
	 */
	public List<Record> findRecord(Class<?> cla,
			String fieldName, Object fieldValue, String orderField,
			Boolean isDesc) {
		if (StringUtil.findEmptyIndex(fieldName, fieldValue) > -1) {
			return null;
		}
		Map<String, Object> map = new TreeMap<String, Object>();
		map.put(fieldName, fieldValue);
		return findRecord(cla, map, orderField, isDesc);
	}

	/**
	 * 根据字段查询结果集
	 * 
	 * @param cla
	 * @param fieldName
	 * @param fieldValue
	 * @return
	 */
	public List<Record> findRecord(Class<?> cla,
			String fieldName, Object fieldValue) {
		if (StringUtil.findEmptyIndex(fieldName, fieldValue) > -1) {
			return null;
		}
		Map<String, Object> map = new TreeMap<String, Object>();
		map.put(fieldName, fieldValue);
		return findRecord(cla, map, null, null);
	}

	/**
	 * 根据字段查询结果集
	 * 
	 * @param cla
	 * @param fieldName
	 * @param fieldValue
	 * @param orderField
	 * @param isDesc
	 * @return
	 */
	public Record findRecordFirst(Class<?> cla, String fieldName,
			Object fieldValue, String orderField, Boolean isDesc) {
		List<Record> records = findRecord(cla, fieldName, fieldValue,
				orderField, isDesc);
		if (StringUtil.isNullOrEmpty(records)) {
			return null;
		}
		return records.get(0);
	}

	/**
	 * 根据字段查询结果集
	 * 
	 * @param cla
	 * @param fieldName
	 * @param fieldValue
	 * @return
	 */
	public Record findRecordFirst(Class<?> cla, String fieldName,
			Object fieldValue) {
		return findRecordFirst(cla, fieldName, fieldValue, null, null);
	}

	
	/**
	 * 根据字段查询结果集
	 * 
	 * @param cla
	 *            类
	 * @param paraMap
	 *            多个字段
	 * @return
	 */
	public List<Record> findRecord(Class<?> cla,
			Map<String, Object> paraMap, String orderField, Boolean isDesc) {
		Where where = new Where();
		if (!StringUtil.isNullOrEmpty(paraMap)) {
			for (String key : paraMap.keySet()) {
				if (StringUtil.isNullOrEmpty(paraMap.get(key))) {
					where.set(key, "is null", new Object[] { null });
					continue;
				}
				if (paraMap.get(key) instanceof Collection<?>) {
					if (paraMap.get(key) instanceof Collection<?>) {
						where.set(key, "in",
								((Collection<?>) paraMap.get(key)).toArray());
					}
					continue;
				}
				if (paraMap.get(key).getClass().isArray()) {
					if (paraMap.get(key) instanceof Object[]) {
						where.set(key, "in", (Object[]) paraMap.get(key));
					}
					continue;
				}
				where.set(key, paraMap.get(key));
			}
		}
		SQLEntity sqlEntity = parseSQL(cla, where, null, orderField,
				isDesc);
		return baseQuery(sqlEntity.getSql(), sqlEntity.getParaMap());
	}

	/**
	 * 根据字段查询结果集
	 * 
	 * @param cla
	 * @param paraMap
	 * @param orderField
	 * @param isDesc
	 * @return
	 */
	public Record findRecordFirst(Class<?> cla, Map<String, Object> paraMap,
			String orderField, Boolean isDesc) {
		List<Record> list = findRecord(cla, paraMap, orderField, isDesc);
		if (StringUtil.isNullOrEmpty(list)) {
			return null;
		}
		return list.get(0);
	}

	/**
	 * 根据字段查询结果集
	 * 
	 * @param cla
	 * @param paraMap
	 * @return
	 */
	public Record findRecordFirst(Class<?> cla, Map<String, Object> paraMap) {
		return findRecordFirst(cla, paraMap, null, null);
	}
	/**
	 * 分页查询
	 * 
	 * @param obj
	 *            对象条件
	 * @param pager
	 *            分页信息
	 * @return
	 */
	public Pager findPager(Object obj, Pager pager) {
		return findPager(obj, null, pager, null, null);
	}

	/**
	 * 分页查询
	 * 
	 * @param obj
	 *            对象条件
	 * @param pager
	 *            分页信息
	 * @return
	 */
	public Pager findPager(Object obj, Pager pager,
			String orderField, Boolean isDesc) {
		return findPager(obj, null, pager, orderField, isDesc);
	}

	/**
	 * 分页查询
	 * 
	 * @param obj
	 *            对象条件
	 * @param where
	 *            where条件
	 * @param pager
	 *            分页条件
	 * @return
	 */
	public Pager findPager(Object obj, Where where, Pager pager,
			String orderField, Boolean isDesc) {
		SQLEntity sqlEntity = parseSQL(obj, where, pager, orderField,
				isDesc);
		Integer totalRows = getCount(sqlEntity.getSql(), sqlEntity.getParaMap());
		pager.setTotalRows(totalRows);
		List<Record> list = baseQuery(sqlEntity.getSql(),
				sqlEntity.getParaMap());
		List<?> objList = PropertUtil
				.recordToObjects(getObjectClass(obj), list);
		pager.setPageData(objList);
		return pager;
	}

	/**
	 * 分页查询
	 * 
	 * @param obj
	 * @param where
	 * @return
	 */
	public Pager findPager(Object obj, Where where) {
		return findPager(obj, where, null, null, null);
	}

	/**
	 * 分页查询
	 * 
	 * @param obj
	 * @param orderField
	 * @param isDesc
	 * @return
	 */
	public Pager findPager(Object obj, String orderField,
			Boolean isDesc) {
		return findPager(obj, null, null, orderField, isDesc);
	}
	
	

	/**
	 * 根据语句和条件查询总记录数
	 * 
	 * @param sql
	 *            语句
	 * @param map
	 *            条件容器
	 * @return
	 */
	public Integer getCount(String sql, Map<Integer, Object> map) {
		sql = parsCountSql(sql);
		Integer count = doQueryInteger(sql, map);
		return count;
	}

	/**
	 * 根据sql语句查询总记录数
	 * 
	 * @param sql
	 * @return
	 */
	public Integer getCount(String sql) {
		return getCount(sql, null);
	}

	

	/**
	 * 查询功能区 -end
	 */

	/**
	 * 更新功能区 -start
	 */
	
	/**
	 * 更新操作
	 * 
	 * @param sql
	 * @param objs
	 * @return
	 */
	public Integer doUpdate(String sql, Object... objs) {
		Map<Integer, Object> map = new HashMap<Integer, Object>();
		for (Object obj : objs) {
			map.put(map.size() + 1, obj);
		}
		return baseUpdate(sql, map);
	}

	/**
	 * 更新操作
	 * 
	 * @param sql
	 * @return
	 */
	public Integer doUpdate(String sql) {
		return baseUpdate(sql, null);
	}



	/**
	 * 根据对象进行更新
	 * 
	 * @param obj
	 * @return
	 */
	public Integer update(Object obj) {
		return update(obj, "id");
	}

	/**
	 * 根据对象进行更新
	 * 
	 * @param obj
	 * @param priKeyNames
	 * @return
	 */
	public Integer update(Object obj, String... priKeyNames) {
		try {
			if (obj == null) {
				return -1;
			}
			// 获取表名
			String tableName = PropertUtil.unParsParaName(getModelName(obj));
			// 获取属性列表
			List<BeanEntity> prpres = PropertUtil.getBeanFields(obj);
			if (prpres == null || prpres.isEmpty()) {
				return -1;
			}
			List<String> priKeys = Arrays.<String> asList(priKeyNames);
			// 拼接SQL语句
			StringBuilder sql = new StringBuilder(MessageFormat.format(
					"update {0} set ", tableName));
			Map<Integer, Object> paraMap = new TreeMap<Integer, Object>();
			BeanEntity vo = null;
			String fieldName = null;
			for (int i = 0; i < prpres.size(); i++) {
				vo = prpres.get(i);
				if (vo != null) {
					fieldName = PropertUtil.getFieldName(vo);
					if (fieldName == null || "".equals(fieldName)) {
						continue;
					}
					if (priKeys.contains(fieldName)) {
						continue;
					}
					if (vo.getFieldValue() == null) {
						continue;
					}
					sql.append(fieldName).append("=?");
					// 封装参数
					paraMap.put(paraMap.size() + 1, vo.getFieldValue());
					if (i < prpres.size() - 1) {
						sql.append(",");
					}
				}
			}
			if (sql.toString().endsWith(",")) {
				sql = new StringBuilder(sql.toString().substring(0,
						sql.toString().length() - 1));
			}
			sql.append(" where ");
			for (int i = 0; i < priKeyNames.length; i++) {
				Object fieldValue = PropertUtil.getFieldValue(obj,
						priKeyNames[i]);
				if (StringUtil.isNullOrEmpty(fieldValue)) {
					sql.append(MessageFormat.format(" {0} is null  ",
							priKeyNames[i]));
				} else {
					sql.append(MessageFormat.format(" {0}=? ", priKeyNames[i]));
					paraMap.put(paraMap.size() + 1, fieldValue);
				}
				if (i != priKeyNames.length - 1) {
					sql.append(" and ");
				}
			}
			return baseUpdate(sql.toString(), paraMap);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}

	/**
	 * 更新功能区 -end
	 */

	/**
	 * 删除功能区 -start
	 */
	/**
	 * 根据对象条件进行删除
	 * 
	 * @param cla
	 * @param priKeyName
	 * @param priKeyValue
	 * @return
	 */
	public Integer delete(Class<?> cla, String priKeyName, Object priKeyValue) {
		if (StringUtil.findEmptyIndex(cla, priKeyName, priKeyValue) > -1) {
			return -1;
		}
		// 获取表名
		String tableName = PropertUtil.unParsParaName(getModelName(cla));
		StringBuilder sb = new StringBuilder();
		sb.append("delete ").append(" from ").append(tableName)
				.append(" where ").append(priKeyName).append("=? ");
		Map<Integer, Object> paraMap = new TreeMap<Integer, Object>();
		paraMap.put(1, priKeyValue);
		return baseUpdate(sb.toString(), paraMap);
	}

	/**
	 * 根据对象条件进行删除
	 * 
	 * @param cla
	 * @param priKeyValue
	 * @return
	 */
	public Integer delete(Class<?> cla, Object priKeyValue) {
		return delete(cla, "id", priKeyValue);
	}

	/**
	 * 更新功能区 -end
	 */

	/**
	 * 插入功能区 -start
	 */
	/**
	 * 根据对象条件进行插入
	 * 
	 * @param obj
	 * @return
	 */
	public Integer insert(Object obj) {
		try {
			if (obj == null) {
				return -1;
			}
			// 获取表名
			String tableName = PropertUtil.unParsParaName(getModelName(obj));
			// 获取属性列表
			List<BeanEntity> prpres = PropertUtil.getBeanFields(obj);
			if (prpres == null || prpres.isEmpty()) {
				return -1;
			}
			// 拼接SQL语句
			StringBuilder sql = new StringBuilder(MessageFormat.format(
					"insert into {0} set ", tableName));
			Map<Integer, Object> paraMap = new TreeMap<Integer, Object>();
			BeanEntity vo = null;
			String fieldName = null;
			for (int i = 0; i < prpres.size(); i++) {
				vo = prpres.get(i);
				if (vo != null) {
					fieldName = PropertUtil.getFieldName(vo);
					if (fieldName == null || "".equals(fieldName)) {
						continue;
					}
					if (vo.getFieldValue() == null) {
						continue;
					}
					sql.append(fieldName).append("=?");
					// 封装参数
					paraMap.put(paraMap.size() + 1, vo.getFieldValue());
					if (i < prpres.size() - 1) {
						sql.append(",");
					}
				}
			}
			if (sql.toString().endsWith(",")) {
				sql = new StringBuilder(sql.toString().substring(0,
						sql.toString().length() - 1));
			}
			return baseUpdate(sql.toString(), paraMap);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}

	/**
	 * 根据对象条件进行插入或更新
	 * 
	 * @param obj
	 * @param priKeyName
	 * @return
	 */
	public Integer saveOrUpdate(Object obj, String... priKeyName) {
		Object priKeyvalue = PropertUtil.getFieldValue(obj, priKeyName[0]);
		if (StringUtil.isNullOrEmpty(priKeyvalue)) {
			return insert(obj);
		}
		return update(obj, priKeyName);
	}

	/**
	 * 根据对象进行插入或更新
	 * 
	 * @param obj
	 * @param priKeyName
	 * @return
	 */
	public Integer saveOrUpdateAuto(Object obj) {
		if (obj == null) {
			return -1;
		}
		// 获取表名
		String tableName = PropertUtil.getTableName(obj);
		// 拼接SQL语句
		StringBuilder sql = new StringBuilder(MessageFormat.format(
				"insert into {0} set ", tableName));
		Map<Integer, Object> paraMap = new TreeMap<Integer, Object>();
		String diySql=parseFieldSql(obj,paraMap);
		if(StringUtil.isNullOrEmpty(diySql)){
			return -1;
		}
		sql.append(diySql);
		sql.append(" ON DUPLICATE KEY UPDATE ");
		diySql=parseFieldSql(obj,paraMap);
		sql.append(diySql);
		return baseUpdate(sql.toString(),paraMap);
	}

	public String parseFieldSql(Object obj,Map<Integer, Object> paraMap){
		List<BeanEntity> prpres = PropertUtil.getBeanFields(obj);
		StringBuilder sql = new StringBuilder();
		BeanEntity vo = null;
		String fieldName = null;
		for (int i = 0; i < prpres.size(); i++) {
			vo = prpres.get(i);
			if (vo != null) {
				fieldName = PropertUtil.getFieldName(vo);
				if (fieldName == null || "".equals(fieldName)) {
					continue;
				}
				if (vo.getFieldValue() == null) {
					continue;
				}
				sql.append(fieldName).append("=?");
				// 封装参数
				paraMap.put(paraMap.size() + 1, vo.getFieldValue());
				if (i < prpres.size() - 1) {
					sql.append(",");
				}
			}
		}
		if (sql.toString().endsWith(",")) {
			sql = new StringBuilder(sql.toString().substring(0,
					sql.toString().length() - 1));
		}
		return sql.toString();
	}
	/**
	 * 插入功能区 -end
	 */

	/**
	 * 内部方法 -start
	 */
	private Class<?> getObjectClass(Object obj) {
		if (obj instanceof java.lang.Class) {
			return (Class<?>) obj;
		}
		return obj.getClass();
	}

	private static String getModelName(Object obj) {
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

	private List<Record> mapSetToList(List<Map<String, Object>> list) {
		if (StringUtil.isNullOrEmpty(list)) {
			return null;
		}
		List<Record> recs = new ArrayList<Record>();
		for (Map<String, Object> map : list) {
			Record rec = new Record();
			rec.putAll(map);
			recs.add(rec);
		}
		return recs;
	}

	private String parsCountSql(String sql) {
		while (sql.indexOf("  ") > -1) {
			sql = sql.replace("  ", " ");
		}
		Integer formIndex = sql.toLowerCase().indexOf("from");
		if (formIndex > -1) {
			sql = sql.substring(formIndex, sql.length());
		}
		Integer orderIndex = sql.toLowerCase().indexOf("order by");
		if (orderIndex > -1) {
			sql = sql.substring(0, orderIndex);
		}
		Integer limitIndex = sql.toLowerCase().indexOf("limit");
		while (limitIndex > -1) {
			String firstSql = sql.substring(0, limitIndex);
			String lastSql = sql.substring(limitIndex);
			if (lastSql.indexOf(")") > -1) {
				lastSql = lastSql.substring(lastSql.indexOf(")"));
				firstSql = firstSql + lastSql;
			}
			sql = firstSql;
			limitIndex = sql.toLowerCase().indexOf("limit");
		}
		if (orderIndex > -1) {
			sql = sql.substring(0, orderIndex);
		}
		sql = "select count(*) " + sql;
		return sql;
	}

	private static String formatSql(String sql) {
		while (sql.contains("  ")) {
			sql = sql.replace("  ", " ");
		}
		return sql.toLowerCase();
	}

	/**
	 * 设定更新参数
	 * 
	 * @param statement
	 * @param paraMap
	 * @param threadId
	 */
	private void setParams(PreparedStatement statement,
			Map<Integer, Object> paraMap, Long threadId) {
		if (paraMap != null) {
			for (Integer key : paraMap.keySet()) {
				try {
					statement.setObject(key, paraMap.get(key));
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 获取更新参数
	 * 
	 * @param paraMap
	 * @param threadId
	 * @return
	 */
	private Object[] getParams(Map<Integer, Object> paraMap, Long threadId) {
		if (paraMap != null) {
			List<Object> paraList = new ArrayList<Object>();
			for (Integer key : paraMap.keySet()) {
				paraList.add(paraMap.get(key));
			}
			if (StringUtil.isNullOrEmpty(paraList)) {
				return null;
			}
			return paraList.toArray();
		}
		return null;
	}
	private JdbcTemplate getTemplate() {
		String template=AspectUtil.getCurrDBTemplate();
		List<BeanEntity> entitys=PropertUtil.getBeanFields(this);
		if(!StringUtil.isNullOrEmpty(entitys)){
			for (BeanEntity entity:entitys) {
				if(!JdbcTemplate.class.isAssignableFrom(entity.getFieldType())){
					continue;
				}
				if(StringUtil.isNullOrEmpty(entity.getFieldValue())){
					continue;
				}
				if(entity.getFieldName().equals(template)){
					return (JdbcTemplate) entity.getFieldValue();
				}
			}
		}
		if(StringUtil.isNullOrEmpty(template)){
			return jdbcTemplate;
		}
		return SpringContextHelper.getBean(template);
	}

	public static void main(String[] args) {
		String sql = "select * from admin ,";
		if (sql.endsWith(",")) {
			System.out.println(sql.substring(0, sql.length() - 1));
		}
	}
}
