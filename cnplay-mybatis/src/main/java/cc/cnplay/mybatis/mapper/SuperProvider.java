package cc.cnplay.mybatis.mapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cc.cnplay.SnowFlake;
import cc.cnplay.model.SuperIdEntity;

public class SuperProvider
{

	private Logger logger = LoggerFactory.getLogger(SuperProvider.class);

	// @SuppressWarnings("unchecked")
	// private Class<T> persistentClass = (Class<T>) GenericUtils.getFirstArgs(SuperProvider.class, this.getClass());
	//
	// protected String table;
	//
	// public SuperProvider()
	// {
	// this.table = Metadata.getMetadata(persistentClass).getTable();
	// }
	//
	// public String getTable()
	// {
	// return table;
	// }

	public String countAll(Class<?> clazz)
	{
		String sql = Metadata.sqlCountAll(clazz);
		logger.info(sql);
		return sql;
	}

	public String deleteAll(Class<?> clazz)
	{
		String sql = Metadata.sqlDeleteAll(clazz);
		logger.info(sql);
		return sql;
	}

	public String delete(SuperIdEntity e)
	{
		String sql = Metadata.sqlDeleteById(e);
		logger.info(sql);
		return sql;
	}

	public String find(SuperIdEntity e)
	{
		String sql = Metadata.createSelect(e);
		logger.info(sql);
		return sql;
	}

	public String findAll(Class<?> clazz)
	{
		String sql = Metadata.sqlSelectAll(clazz);
		logger.info(sql);
		return sql;
	}

	public String findOne(SuperIdEntity e)
	{
		String sql = Metadata.createSelectOne(e);
		logger.info(sql);
		return sql;
	}

	public String findById(SuperIdEntity e)
	{
		String sql = Metadata.sqlSelectById(e);
		logger.info(sql);
		return sql;
	}

	public String insert(SuperIdEntity e)
	{
		if (e.getId() != null && e.getId() <= 0)
		{
			e.setId(SnowFlake.next());
		}
		String sql = Metadata.sqlInsert(e);
		logger.info(sql);
		return sql;
	}

	public String update(SuperIdEntity e)
	{
		String sql = Metadata.createUpdate(e);
		logger.info(sql);
		return sql;
	}

	public String page(SuperIdEntity page)
	{
		String sql = Metadata.createSelectLimit(page);
		logger.info(sql);
		return sql;
	}

	public String pageCount(SuperIdEntity page)
	{
		String sql = Metadata.createSelectCount(page);
		logger.info(sql);
		return sql;
	}
}
