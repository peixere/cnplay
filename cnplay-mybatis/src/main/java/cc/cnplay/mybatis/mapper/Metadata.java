package cc.cnplay.mybatis.mapper;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.ibatis.jdbc.SQL;

import cc.cnplay.model.SuperIdEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Setter(AccessLevel.PROTECTED)
@Getter(AccessLevel.PROTECTED)
public class Metadata
{
	public final static Map<Class<?>, Metadata> cache = new HashMap<Class<?>, Metadata>();

	private Class<?> clazz;
	private String name;
	private String table;
	private Property id;
	private Property version;
	private Map<String, Property> props;

	private String sqlInsert;
	private String sqlDeleteById;
	private String sqlDeleteAll;
	private String sqlSelectAll;
	private String sqlSelectById;
	private String sqlCountAll;

	Metadata(Class<?> clazz)
	{
		this.setClazz(clazz);
		this.setName(clazz.getSimpleName());
		Entity entity = getEntity(clazz);
		Table table = getTable(clazz);
		if (table != null && !table.name().isEmpty())
		{
			setTable(table.name());
		}
		else if (!entity.name().isEmpty())
		{
			setTable(entity.name());
		}
		else
		{
			setTable(clazz.getSimpleName());
		}
	}

	public Field getField(String name)
	{
		Property prop = props.get(name);
		return prop != null ? prop.getField() : null;
	}

	public static Metadata getMetadata(Class<?> klass)
	{
		final Metadata metadata;
		if (cache.containsKey(klass))
		{
			metadata = cache.get(klass);
			return metadata;
		}
		else
		{
			if (!isEntity(klass))
			{
				throw new RuntimeException(String.format("%s not found Annotation javax.persistence.Entity", klass.getName()));
			}
			metadata = new Metadata(klass);
		}

		properties(metadata);

		sqlInsert(metadata);

		sqlDeleteById(metadata);

		sqlDeleteAll(metadata);

		sqlSelectById(metadata);

		sqlSelectAll(metadata);

		sqlCountAll(metadata);

		cache.put(klass, metadata);
		return metadata;
	}

	private static void properties(final Metadata metadata)
	{
		Map<String, Property> props = new LinkedHashMap<String, Property>();
		Field[] fields = FieldUtils.getAllFields(metadata.getClazz());
		for (Field field : fields)
		{
			Property prop = new Property(field);

			if (prop.isColumn())
			{
				props.put(prop.getName(), prop);
				if (prop.isId())
				{
					metadata.setId(prop);
				}
				if (prop.isVersion())
				{
					metadata.setVersion(prop);
				}
			}
		}
		if (metadata.getId() == null)
		{
			throw new RuntimeException(String.format("%s not found id cloumn", metadata.getClazz().getName()));
		}
		metadata.setProps(props);
	}

	private static void sqlCountAll(final Metadata metadata)
	{
		String sql = new SQL()
		{
			{
				SELECT(String.format("count(%s)",metadata.getId().getColumn()));
				FROM(metadata.getTable());
			}
		}.toString();
		metadata.setSqlCountAll(sql);
	}

	public static String sqlCountAll(Class<?> clazz)
	{
		return getMetadata(clazz).getSqlCountAll();
	}

	private static void sqlDeleteById(final Metadata metadata)
	{
		metadata.setSqlDeleteById(new SQL()
		{
			{
				DELETE_FROM(metadata.getTable());
				WHERE(String.format("%s = #{%s}", metadata.getId().getColumn(), metadata.getId().getName()));
			}
		}.toString());
	}

	private static void sqlDeleteAll(final Metadata metadata)
	{
		metadata.setSqlDeleteAll(new SQL()
		{
			{
				DELETE_FROM(metadata.getTable());
			}
		}.toString());
	}

	public static String sqlDeleteAll(Class<?> clazz)
	{
		return getMetadata(clazz).getSqlDeleteAll();
	}

	public static String sqlDeleteById(Object orig)
	{
		return getMetadata(orig.getClass()).getSqlDeleteById();
	}

	private static void sqlSelectById(final Metadata metadata)
	{
		metadata.setSqlSelectById(new SQL()
		{
			{
				SELECT("*");
				FROM(metadata.getTable());
				WHERE(String.format("%s = #{%s}", metadata.getId().getColumn(), metadata.getId().getName()));
			}
		}.toString());
	}

	public static String sqlSelectById(Object orig)
	{
		return getMetadata(orig.getClass()).getSqlSelectById();
	}

	private static void sqlSelectAll(final Metadata metadata)
	{
		String sql = new SQL()
		{
			{
				SELECT("*");
				FROM(metadata.getTable());
			}
		}.toString();
		metadata.setSqlSelectAll(sql);
	}

	public static String sqlSelectAll(Class<?> clazz)
	{
		return getMetadata(clazz).getSqlSelectAll();
	}

	private static void sqlInsert(final Metadata metadata)
	{
		String sqlInsert = new SQL()
		{
			{
				INSERT_INTO(metadata.getTable());
				Collection<Property> props = metadata.getProps().values();
				for (Property property : props)
				{
					if (!property.isVersion())
					{
						VALUES(property.getColumn(), "#{" + property.getName() + "}");
					}
					if (property.isVersion())
					{
						VALUES(property.getColumn(), "0");
					}
				}
			}
		}.toString();
		metadata.setSqlInsert(sqlInsert);
	}

	public static String sqlInsert(Object orig)
	{
		return getMetadata(orig.getClass()).getSqlInsert();
	}

	public static String createUpdate(Object orig)
	{
		Metadata metadata = getMetadata(orig.getClass());
		String sqlUpdate = new SQL()
		{
			{
				UPDATE(metadata.table);
				Collection<Property> props = metadata.getProps().values();
				for (Property property : props)
				{
					if (property.isUpdatable())
					{
						if (property.isVersion())
						{
							SET(String.format("%s = (%s + 1)", property.getColumn(), property.getColumn()));
						}
						else if (!property.isId())
						{
							Temporal temporal = property.getAnnotation(Temporal.class);
							if (temporal != null && TemporalType.TIMESTAMP.equals(temporal.value()))
							{
								SET(String.format("%s = now()", property.getColumn()));
							}
							else
							{
								Object value = getValue(orig, property);
								if (value != null)
								{
									SET(String.format("%s = #{%s}", property.getColumn(), property.getName()));
								}
							}
						}
					}
				}
				WHERE(String.format("%s = #{%s}", metadata.getId().getColumn(), metadata.getId().getName()));
				if (metadata.getVersion() != null)
				{
					Object value = getValue(orig, metadata.getVersion());
					if (value != null)
					{
						WHERE(String.format("%s = #{%s}", metadata.getVersion().getColumn(), metadata.getVersion().getName()));
					}
				}
			}
		}.toString();
		return sqlUpdate;
	}

	public static String createSelectCount(SuperIdEntity orig)
	{
		SQL sql = createSelectFrom(orig);
		sql.SELECT("count(*)");
		return sql.toString();
	}

	public static String createSelectLimit(SuperIdEntity orig)
	{
		String sql = createSelect(orig);
		if (orig.getPage() != null)
		{
			sql += " LIMIT #{page.start},#{page.size}";
		}
		return sql;
	}

	public static String createSelect(Object orig)
	{
		SQL sql = createSelectFrom(orig);
		sql.SELECT("*");
		return sql.toString();
	}

	public static SQL createSelectFrom(Object orig)
	{
		Metadata metadata = getMetadata(orig.getClass());
		SQL sql = new SQL()
		{
			{
				FROM(metadata.getTable());
				Collection<Property> props = metadata.getProps().values();
				for (Property p : props)
				{
					Object value = getValue(orig, p);
					if (value != null)
					{
						WHERE(String.format("%s = #{%s}", p.getColumn(), p.getName()));
					}
				}
			}
		};
		return sql;
	}

	public static String createSelectById(Object orig)
	{
		String sql = createSelect(orig);
		sql += " LIMIT 1";
		return sql;
	}

	public static String createSelectOne(Object orig)
	{
		String sql = createSelect(orig);
		sql += " LIMIT 1";
		return sql;
	}

	private static Object getValue(Object orig, Property prop)
	{
		try
		{
			Field field = prop.getField();
			if (field != null)
			{
				if (!field.isAccessible())
				{
					field.setAccessible(true);
				}
				return field.get(orig);
			}
		}
		catch (Throwable e)
		{
		}
		return null;
	}

	public static Table getTable(Class<?> klass)
	{
		Class<?> clazz = klass;
		while (clazz != null)
		{
			Table entity = clazz.getAnnotation(Table.class);
			if (null != entity)
			{
				return entity;
			}
			clazz = clazz.getSuperclass();
		}
		return null;
	}

	public static Entity getEntity(Class<?> klass)
	{
		Class<?> clazz = klass;
		while (clazz != null)
		{
			Entity entity = clazz.getAnnotation(Entity.class);
			if (null != entity)
			{
				return entity;
			}
			clazz = clazz.getSuperclass();
		}
		return null;
	}

	public static boolean isEntity(Class<?> klass)
	{
		return getEntity(klass) != null;
	}

	public static boolean isDbType(Class<?> clazz)
	{
		if (clazz.isPrimitive())
		{
			return true;
		}
		if (clazz.equals(java.lang.Integer.class) || clazz.equals(java.lang.Byte.class) || clazz.equals(java.lang.Long.class) || clazz.equals(java.lang.Double.class) || clazz.equals(java.lang.Float.class) || clazz.equals(java.lang.Short.class)
				|| clazz.equals(java.lang.Boolean.class) || clazz.equals(java.lang.String.class) || clazz.equals(java.lang.Byte[].class) || clazz.equals(byte[].class) || clazz.equals(java.math.BigDecimal.class) || clazz.equals(java.math.BigInteger.class)
				|| clazz.equals(java.util.Date.class) || clazz.equals(java.sql.Date.class) || clazz.equals(java.sql.Time.class) || clazz.equals(java.sql.Timestamp.class) || clazz.equals(java.sql.Date.class))
		{
			return true;
		}
		return false;
	}

	public static boolean isPersistable(Class<?> klass)
	{
		return null != klass.getAnnotation(Entity.class) || null != klass.getAnnotation(Embeddable.class) || null != klass.getAnnotation(MappedSuperclass.class);
	}

	@Setter
	@Getter
	public static class Property
	{

		public Property(Field field)
		{
			name = field.getName();
			column = name;
			this.field = field;
			Column c = getAnnotation(Column.class);
			if (c != null)
			{
				if (!c.name().isEmpty())
				{
					column = c.name();
				}
				updatable = c.updatable();
			}
		}

		public boolean isColumn()
		{
			if (!isPersistable(field.getDeclaringClass()))
			{
				return false;
			}
			if (!isDbType(field.getType()))
			{
				return false;
			}
			int mod = field.getModifiers();
			if (hasAnnotation(Transient.class) || Modifier.isTransient(mod) || Modifier.isTransient(mod) || Modifier.isStatic(mod))
			{
				return false;
			}
			return true;
		}

		public boolean isId()
		{
			return hasAnnotation(Id.class) || hasAnnotation(EmbeddedId.class);
		}

		public boolean isVersion()
		{
			return hasAnnotation(Version.class);
		}

		public <T extends Annotation> boolean hasAnnotation(Class<T> annotationClass)
		{
			return getAnnotation(annotationClass) != null;
		}

		public <T extends Annotation> T getAnnotation(Class<T> annotationClass)
		{
			T val = field.getAnnotation(annotationClass);
			return val;
		}

		private String column;
		private String name;
		private boolean updatable = true;
		private Field field;
	}

}
