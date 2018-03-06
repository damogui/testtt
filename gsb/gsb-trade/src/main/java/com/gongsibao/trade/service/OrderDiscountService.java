package com.gongsibao.trade.service;

import java.sql.Types;
import java.util.List;

import org.netsharp.communication.Service;
import org.netsharp.core.Oql;
import org.netsharp.service.PersistableService;

import com.gongsibao.entity.trade.OrderDiscount;
import com.gongsibao.trade.base.IOrderDiscountService;

@Service
public class OrderDiscountService extends PersistableService<OrderDiscount> implements IOrderDiscountService {

	public OrderDiscountService() {
		super();
		this.type = OrderDiscount.class;
	}

	@Override
	public List<OrderDiscount> queryByOrderId(Integer orderId) {
		Oql oql = new Oql();
		{
			oql.setType(this.type);
			oql.setSelects("*");
			oql.setFilter("orderId=?");
			oql.getParameters().add("orderId", orderId, Types.INTEGER);
		}
		return this.queryList(oql);
	}
}