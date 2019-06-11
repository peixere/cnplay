package cc.cnplay.model;

import java.io.Serializable;

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

	@ApiModelProperty(value = "主键")
	@Id()
	@Column(name = "id", unique = true, nullable = false)
	private Long id;

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
