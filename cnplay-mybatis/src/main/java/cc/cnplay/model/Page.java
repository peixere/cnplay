package cc.cnplay.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(Include.NON_NULL)
public class Page implements Serializable
{

	/**
	 * 分页类
	 */
	private static final long serialVersionUID = -6518359964486465431L;

	@ApiModelProperty(value = "当前页", example = "1")
	private int number = 1;

	@ApiModelProperty(value = "每页行数", example = "20")
	private int size = 20;

	@ApiModelProperty(value = "当前页起始行,从0开始", hidden = true, example = "0")
	private int start;

	@ApiModelProperty(value = "总页数", hidden = true, example = "0")
	private int count;

	@ApiModelProperty(value = "总记录数", hidden = true, example = "0")
	private int total;

	public int getNumber()
	{
		if (number < 1)
		{
			number = 1;
		}
		return number;
	}

	public int getSize()
	{
		if (size < 1)
		{
			size = 20;
		}
		return size;
	}

	public int getStart()
	{
		start = getSize() * (getNumber() - 1);
		return start;
	}

	public int getCount()
	{
		int pageSize = getSize();
		count = (total + pageSize - 1) / pageSize;
		return count;
	}

}
