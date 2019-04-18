package cc.cnplay.mybatis.mapper;

import java.util.List;

import org.apache.ibatis.annotations.DeleteProvider;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.UpdateProvider;

import cc.cnplay.mybatis.model.SuperIdEntity;

@Mapper
public interface SuperMapper<T extends SuperIdEntity>
{
	@SelectProvider(type = SuperProvider.class, method = "countAll")
	int countAll(T e);

	@DeleteProvider(type = SuperProvider.class, method = "delete")
	int delete(T e);

	@DeleteProvider(type = SuperProvider.class, method = "deleteAll")
	int deleteAll(Class<T> clazz);
	
	@SelectProvider(type = SuperProvider.class, method = "find")
	List<T> find(T e);

	@SelectProvider(type = SuperProvider.class, method = "findAll")
	List<T> findAll(Class<T> clazz);

	@SelectProvider(type = SuperProvider.class, method = "findById")
	T findById(T e);

	@SelectProvider(type = SuperProvider.class, method = "findOne")
	T findOne(T e);

	@InsertProvider(type = SuperProvider.class, method = "insert")
	void insert(T e);

	@UpdateProvider(type = SuperProvider.class, method = "update")
	public int update(T e);

	@SelectProvider(type = SuperProvider.class, method = "page")
	public List<T> page(T page);

	@SelectProvider(type = SuperProvider.class, method = "pageCount")
	public int pageCount(T page);
}
