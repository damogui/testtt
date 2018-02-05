package com.gongsibao.crm.service.action.task.allocation.manual;

import java.util.Map;

import org.netsharp.action.ActionContext;
import org.netsharp.action.IAction;
import org.netsharp.communication.ServiceFactory;

import com.gongsibao.crm.base.INCustomerOperationLogService;
import com.gongsibao.crm.base.INCustomerTaskNotifyService;
import com.gongsibao.entity.crm.NCustomerOperationLog;
import com.gongsibao.entity.crm.NCustomerTask;
import com.gongsibao.entity.crm.NCustomerTaskNotify;
import com.gongsibao.entity.crm.dic.ChangeType;
import com.gongsibao.entity.crm.dic.NotifyType;

/**
 * @author hw 分配：保存日志
 */
public class ActionManualAllocationRecordLog implements IAction {

	@Override
	public void execute(ActionContext ctx) {

		NCustomerTask task = (NCustomerTask) ctx.getItem();
		Map<String, Object> getMap = ctx.getStatus();

		// 1.保存流转日志
		INCustomerOperationLogService changeService = ServiceFactory.create(INCustomerOperationLogService.class);
		NCustomerOperationLog changeLog = new NCustomerOperationLog();
		{
			changeLog.toNew();
			changeLog.setTaskId(task.getId());
			changeLog.setChangeType(ChangeType.ALLOCATION);
			changeLog.setCustomerId(task.getCustomerId());
			changeLog.setFormUserId((Integer) getMap.get("formUserId"));
			changeLog.setToUserId(task.getOwnerId());
			changeLog.setSupplierId(task.getSupplierId());
			changeLog.setDepartmentId(task.getDepartmentId());
			changeService.save(changeLog);
		}

		// 2.保存通知日志
		INCustomerTaskNotifyService notifyService = ServiceFactory.create(INCustomerTaskNotifyService.class);
		NCustomerTaskNotify notify = new NCustomerTaskNotify();
		{
			String content = String.format("一个新任务分配给您，请及时跟进。任务ID：%s", task.getId());
			notify.toNew();
			notify.setTaskId(task.getId());
			notify.setType(NotifyType.WEIXIN);
			notify.setCustomerId(task.getCustomerId());
			notify.setSupplierId(task.getSupplierId());
			notify.setDepartmentId(task.getDepartmentId());
			//*业务员为空，通知服务商管理员或部门主管,暂无实现
			notify.setReceivedId(task.getOwnerId());
			notify.setContent(content);
			notifyService.save(notify);
		}
	}

}
