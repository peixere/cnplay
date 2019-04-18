package cc.cnplay.mybatis.model;

import java.io.Serializable;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@MappedSuperclass
public abstract class SuperIdEntity implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static String randomId()
	{
		return UUID.randomUUID().toString();
	}

	@Id()
	@Column(name = "id", unique = true, nullable = false, length = 36)
	private String id;

	@ApiModelProperty(value = "分页查询参数", hidden = true)
	@Transient
	private transient Page page;

	/**
	 * {@inheritDoc}
	 */
	public boolean equals(Object o)
	{
		if (this == o)
		{
			return true;
		}
		return (o != null && o.toString().equals(this.toString()));
	}

	/**
	 * {@inheritDoc}
	 */
	public int hashCode()
	{
		return (id != null ? id.hashCode() : 0);
	}

	/**
	 * {@inheritDoc}
	 */
	public String toString()
	{
		return getClass().getName() + "@id=" + this.id;
	}

}
