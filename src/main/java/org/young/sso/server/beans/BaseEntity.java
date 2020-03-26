package org.young.sso.server.beans;

import java.io.Serializable;
import java.util.Date;

import com.alibaba.fastjson.JSON;

public class BaseEntity implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/** 创建时间 **/
	private Date createTime;
	/** 更新时间 **/
	private Date updateTime;
	/** 排序字段 **/
	private transient String sort;
	/** 排序方式 **/
	private transient String order;
	/**检索开始时间**/
	private transient Date solrStartTime;
	/**检索结束时间**/
	private transient Date solrEndTime;

	public Date getCreateTime() {
		return createTime;
	}
	
	public long getCreateTimestamp() {
		return createTime==null?0:createTime.getTime();
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}
	
	public long getUpdateTimestamp() {
		return updateTime==null?0:updateTime.getTime();
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public String getSort() {
		return sort;
	}

	public void setSort(String sort) {
		this.sort = sort;
	}

	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}

	public Date getSolrStartTime() {
		return solrStartTime;
	}

	public void setSolrStartTime(Date solrStartTime) {
		this.solrStartTime = solrStartTime;
	}

	public Date getSolrEndTime() {
		return solrEndTime;
	}

	public void setSolrEndTime(Date solrEndTime) {
		this.solrEndTime = solrEndTime;
	}
	
	@Override
	public String toString() {
		return JSON.toJSONString(this);
	}
	
}
