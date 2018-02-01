package com.gongsibao.crm.web;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;

import org.netsharp.communication.ServiceFactory;
import org.netsharp.panda.commerce.AdvancedListPart;
import org.netsharp.panda.commerce.FilterParameter;
import org.netsharp.util.StringManager;

import com.gongsibao.crm.base.INCustomerService;
import com.gongsibao.crm.base.INCustomerTaskService;
import com.gongsibao.crm.service.action.task.follow.ActionFollowVerify;
import com.gongsibao.entity.crm.NCustomerProduct;

public class TaskBaseListPart extends AdvancedListPart {

	public String getFilterByParameter(FilterParameter parameter) {

		if(parameter.getKey().equals("keyword")){

			// 这里全匹配
			ArrayList<String> filters = new ArrayList<String>();
			String keyword = parameter.getValue1().toString();
			filters.add("id='" + keyword + "'");
			filters.add("name='" + keyword + "'");
			filters.add("customer.id='" + keyword + "'");
			filters.add("customer.real_name='" + keyword + "'");
			filters.add("customer.mobile='" + keyword + "'");
			filters.add("customer.telephone='" + keyword + "'");
			filters.add("customer.qq='" + keyword + "'");
			filters.add("customer.weixin='" + keyword + "'");
			return "(" + StringManager.join(" or ", filters) + ")";
			
		}else if(parameter.getKey().equals("unFollowDayCount")){
			
			//未跟进天数：当前时间-上次跟进时间
			return String.format("(datediff(now(),last_follow_time)>%s and (datediff(now(),last_follow_time) )<%s)", parameter.getValue1(),parameter.getValue2());
		}
		return parameter.getFilter();
	}

	
	INCustomerTaskService taskService = ServiceFactory.create(INCustomerTaskService.class);
	
	/**
	 * 任务分配
	 * @param taskId
	 * @param supplierId
	 * @param departmentId
	 * @param toUserId
	 * @return
	 */
	public boolean allocation(String taskIds, Integer supplierId, Integer departmentId, Integer toUserId){
		
		String[] taskIdArray = taskIds.split("_");
		return taskService.batchAllocation(taskIdArray, supplierId, departmentId, toUserId);
	}
	/**
	 * 任务收回
	 * @param taskId
	 * @param getNote
	 * @return
	 */
	public boolean regain(String taskIds, String getNote){
		
		String[] taskIdArray = taskIds.split("_");
		return taskService.batchRegain(taskIdArray, getNote);
	}
	/**
	 * 任务退回
	 * @param taskId
	 * @param getNote
	 * @return
	 */
	public boolean rollback(Integer taskId, String getNote){

		return taskService.rollback(taskId, getNote);
	}
	/**
	 * 任务转移
	 * @param taskId
	 * @param supplierId
	 * @param departmentId
	 * @param toUserId
	 * @return
	 */
	public boolean transfer(String taskIds, Integer supplierId, Integer departmentId, Integer toUserId){

		String[] taskIdArray = taskIds.split("_");
		return taskService.batchTransfer(taskIdArray, supplierId, departmentId, toUserId);
	}
	/**
	 * 开通会员功能
	 * @param customerId
	 * @return
	 */
	public boolean openMember(Integer customerId){
		
		INCustomerService customerService = ServiceFactory.create(INCustomerService.class);
		return customerService.openMember(customerId);
	}
	/**
	 * 抽查异常处理
	 * @param taskId  任务Id
	 * @param state  1-"未抽查",2-"抽查正常",3-"抽查异常",4-"异常已处理"
	 * @param content 处理内容
	 * @param type  1-"抽查",2-"处理"
	 * @return
	 */
	public boolean abnormal(Integer taskId, Integer state, String content, Integer type){
		INCustomerTaskService taskService = ServiceFactory.create(INCustomerTaskService.class);
		return taskService.abnormal(taskId, state, content, type);
	}
	/**
	 * 任务跟进
	 * @param taskId 任务Id
	 * @param getqualityId 客户质量Id
	 * @param nextTime 下次跟进时间
	 * @param amount 预计金额
	 * @param content 内容
	 * @return
	 */
	public boolean follow(Integer taskId, Integer getqualityId,String nextTime,BigDecimal amount,String content){
		INCustomerTaskService taskService = ServiceFactory.create(INCustomerTaskService.class);
		return taskService.follow(taskId, getqualityId, nextTime, amount,content);
	}
	/**
	 * 任务跟进，验证意向产品
	 * @param taskId
	 * @return
	 */
	public boolean hasProduct(Integer taskId){
		ActionFollowVerify verify = new ActionFollowVerify();
		Boolean isHas = verify.hasProduct(taskId);
		return isHas;
	}
}
