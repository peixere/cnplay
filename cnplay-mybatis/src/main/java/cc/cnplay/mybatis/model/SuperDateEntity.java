package cc.cnplay.mybatis.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 
 * 实体表基类
 * 
 * @author peixere@qq.com
 * 
 * @version 2012-12-03
 * 
 * @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")。控制入参
 * 
 * @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")。
 * 
 */
@Getter
@Setter
@MappedSuperclass
public abstract class SuperDateEntity extends SuperIdEntity
{
	private static final long serialVersionUID = 1L;

	public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
	public static final String DATE_FORMAT_YMD = "yyyy-MM-dd";

	@ApiModelProperty(notes = "创建时间")
	@Column(name = "date_create", updatable = false)
	private Date dateCreate;

	@ApiModelProperty(notes = "更新时间")
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "date_update")
	private Date dateUpdate;

	@ApiModelProperty(notes = "数据备注")
	@Column(name = "memo", length = 512, nullable = true)
	private String memo;

}
