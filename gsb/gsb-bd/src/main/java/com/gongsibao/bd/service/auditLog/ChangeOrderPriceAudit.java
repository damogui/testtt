package com.gongsibao.bd.service.auditLog;

import java.util.List;

import com.gongsibao.entity.bd.AuditLog;
import com.gongsibao.entity.bd.dic.AuditLogType;


/**
 * 订单改价申请审核
 * @author Administrator
 *
 */
public class ChangeOrderPriceAudit extends AbstractAuditLogService{

	@Override
	public List<AuditLog> getExtenAuditLogList(AuditLogType type,
			Integer formId, Integer addUserId) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
}