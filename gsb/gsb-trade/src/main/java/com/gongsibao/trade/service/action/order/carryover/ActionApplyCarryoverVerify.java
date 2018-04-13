package com.gongsibao.trade.service.action.order.carryover;

import org.netsharp.action.ActionContext;
import org.netsharp.action.IAction;
import org.netsharp.communication.ServiceFactory;
import org.netsharp.core.BusinessException;
import org.netsharp.util.StringManager;

import com.gongsibao.entity.trade.NOrderCarryover;
import com.gongsibao.entity.trade.SoOrder;
import com.gongsibao.entity.trade.dic.AuditStatusType;
import com.gongsibao.u8.base.ISoOrderService;

public class ActionApplyCarryoverVerify  implements IAction{

	@Override
	public void execute(ActionContext ctx) {
		NOrderCarryover carryOver = (NOrderCarryover) ctx.getItem();
		
		//根据订单Id获取订单实体
		ISoOrderService orderService = ServiceFactory.create(ISoOrderService.class);
		SoOrder formOrder = orderService.getByOrderId(carryOver.getFormOrderId());
		SoOrder toOrder = orderService.getByOrderId(carryOver.getToOrderId());
		//来源金额
		Integer paidPrice = formOrder.getPaidPrice() == null ? 0 : formOrder.getPaidPrice().intValue();
		Integer carryIntoAmount = formOrder.getCarryIntoAmount() == null ? 0 : formOrder.getCarryIntoAmount().intValue();
		Integer refundPrice = formOrder.getRefundPrice() == null ? 0 : formOrder.getRefundPrice().intValue();
		Integer carryAmount = formOrder.getCarryAmount() == null ? 0 : formOrder.getCarryAmount().intValue();
		//去向金额
		Integer toPaidPrice = toOrder.getPaidPrice() == null ? 0 : toOrder.getPaidPrice().intValue();
		Integer toCarryIntoAmount = toOrder.getCarryIntoAmount() == null ? 0 : toOrder.getCarryIntoAmount().intValue();
		Integer toRefundPrice = toOrder.getRefundPrice() == null ? 0 : toOrder.getRefundPrice().intValue();
		Integer toCarryAmount = toOrder.getCarryAmount() == null ? 0 : toOrder.getCarryAmount().intValue();
		
		//订单余额
        Integer balance = paidPrice + carryIntoAmount - refundPrice - carryAmount;
        Integer toBalance = toPaidPrice + toCarryIntoAmount - toRefundPrice - toCarryAmount;
		//=====来源订单验证======
        //1.验证结转总金额是否超出
		if(balance.intValue() < 0){
			throw new BusinessException("该订单无可结转金额，请知悉");
		}
		//2.验证状态
		if(!formOrder.getCarryStatus().getValue().equals(AuditStatusType.wu.getValue()) && !formOrder.getCarryStatus().getValue().equals(AuditStatusType.Shtg.getValue())){			
			throw new BusinessException("有笔退款或结转目前审核中，请审核通过后，再创建");
		}
		//3.验证结转金额大于订单余额
		if((balance - carryOver.getAmount()) < 0){
    		throw new BusinessException("结转金额大于订单余额，请核实");
    	}
    	//4.验证结转转入额是否大于待付款金额(订单待付款金额 = 订单应付金额 - 订单余额)
		Integer toPayAmount = toOrder.getPayablePrice() - toBalance;
    	if((toPayAmount - carryOver.getAmount()) < 0){
    		throw new BusinessException("结转金额大于转入订单待付款金额，请核实");
    	}
    	//=====去向订单验证======
		//5.去向订单号是否存在
		if(toOrder == null || StringManager.isNullOrEmpty(toOrder.getNo())){
			throw new BusinessException("去向订单号输入有误，请重新输入");
		}
		if(!toOrder.getCarryStatus().getValue().equals(AuditStatusType.wu.getValue()) && !toOrder.getCarryStatus().getValue().equals(AuditStatusType.Shtg.getValue())){			
			throw new BusinessException("有笔退款或结转目前审核中，请审核通过后，再创建");
		}
	}
}
