package cc.cnplay.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import cc.cnplay.Note;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@JsonInclude(Include.NON_NULL)
public class PageResult<T>
{
	@Note("请求者应当继续提出请求")
	public final static int StatusIng = 100;

	@Note("请求响应正确")
	public final static int StatusOK = 200;

	@Note("（未登录） 请求要求身份验证")
	public final static int StatusNotLogin = 401;

	@Note("需要授权")
	public final static int StatusNotAuth = 407;

	@Note("服务器内部错误")
	public final static int StatusError = 500;

	private int status = StatusOK;

	private String msg = "成功";

	private T data;

	private Page page;

	public PageResult()
	{
		this(null);
	}

	public PageResult(T data)
	{
		this.setData(data);
	}

	public PageResult(T data, String msg)
	{
		this(data, msg, StatusOK);
	}

	public PageResult(T data, String msg, int status)
	{
		this.setStatus(status);
		this.setMsg(msg);
		this.setData(data);
	}

	public PageResult<T> ok(T data)
	{
		return ok(data, null);
	}

	public PageResult<T> ok(T data, Page page)
	{
		this.setData(data);
		this.setPage(page);
		this.setStatus(StatusOK);
		return this;
	}

	public PageResult<T> er(String msg)
	{
		this.setMsg(msg);
		this.setStatus(StatusError);
		return this;
	}
}
