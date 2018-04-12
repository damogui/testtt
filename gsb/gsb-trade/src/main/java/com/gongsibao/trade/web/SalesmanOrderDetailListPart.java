package com.gongsibao.trade.web;

import java.sql.Types;
import java.util.ArrayList;

import org.netsharp.communication.ServiceFactory;
import org.netsharp.core.Oql;
import org.netsharp.panda.commerce.AdvancedListPart;
import org.netsharp.panda.commerce.FilterParameter;
import org.netsharp.util.StringManager;

import com.gongsibao.entity.product.Product;
import com.gongsibao.entity.trade.SoOrder;
import com.gongsibao.product.base.IProductService;
import com.gongsibao.trade.base.IOrderProdService;
import com.gongsibao.u8.base.ISoOrderService;
import com.gongsibao.utils.NumberUtils;
/**
 * Created by yxb on 2018/4/10.
 */
public class SalesmanOrderDetailListPart extends AdvancedListPart {
	IProductService productService = ServiceFactory.create(IProductService.class);
	ISoOrderService orderService = ServiceFactory.create(ISoOrderService.class);
	IOrderProdService orderProdService = ServiceFactory.create(IOrderProdService.class);

    @Override
    public String getFilterByParameter(FilterParameter parameter) {
        ArrayList<String> filters = new ArrayList<String>();
        //当是关键字时(订单明细编号、订单编号、下单人、下单人电话、操作公司)
        String keyword = parameter.getValue1().toString();
        if (parameter.getKey().equals("keyword")) {

            filters.add("no like '%" + keyword + "%'");
            filters.add("order_id in(select pkid from so_order where(no like '%" + keyword + "%' or customer_name like '%" + keyword + "%' or account_mobile like '%" + keyword + "%'))");
            filters.add("company_id in( select pkid from crm_company_intention where (name like '%" + keyword + "%' or full_name like '%" + keyword + "%' or company_name like '%" + keyword + "%' )  )");
            return "(" + StringManager.join(" or ", filters) + ")";
        }
        return parameter.getFilter();
    }
    
    /**
     * 根据产品Id获取是否有办理名称 
     * @param productId
     * @return
     */
    public Boolean isHandle(Integer productId){
    	Oql oql = new Oql();
        {
            oql.setType(Product.class);
            oql.setSelects("*");
            oql.setFilter("id=?");
            oql.getParameters().add("id", productId, Types.INTEGER);
        }
        Product entity = productService.queryFirst(oql);
        return entity.getIsHandle() == null ? false : entity.getIsHandle();
    }
    /**
     * 根据订单获取关联公司 
     * @param productId
     * @return
     */
    public String getCompaniesName(Integer orderId){
    	Oql oql = new Oql();
        {
            oql.setType(SoOrder.class);
            oql.setSelects("SoOrder.companyId,SoOrder.companyIntention.name");
            oql.setFilter("id=?");
            oql.getParameters().add("id", orderId, Types.INTEGER);
        }
        SoOrder entity = orderService.queryFirst(oql);
        return entity.getCompanyIntention() == null ? "" : entity.getCompanyIntention().getName();
    }
    /**
     * 开始操作前置条件：
     * 1.全额：订单余额=订单金额
     * 2.分期：订单余额＞0 && 已付金额＋结转转入额≥一期款
     * 订单余额 = paidPrice+carryIntoAmount-refundPrice-carryAmount
     * @param orderId
     * @return -1：订单信息为空、0：不满足全款、1：不满足分期、2：都满足
     */
    public Integer meetBegOption(Integer orderId){
    	Integer retuValue = 2;
    	SoOrder orderEntity = orderService.getByOrderId(orderId);
    	if(orderEntity != null){
    		//获取订单余额
    		Integer balance = NumberUtils.toInt(orderEntity.getPaidPrice()) + NumberUtils.toInt(orderEntity.getCarryIntoAmount()) - NumberUtils.toInt(orderEntity.getRefundPrice()) - NumberUtils.toInt(orderEntity.getCarryAmount());        	
        	//false:全款、true:分期
    		if(orderEntity.getIsInstallment()){
    			//获取一期款
    			Integer firstPhase = Integer.valueOf(StringManager.isNullOrEmpty(orderEntity.getInstallmentMode()) ? "0" : orderEntity.getInstallmentMode().split("|")[0]);
    			//获取现有金额（已付金额＋结转转入额）
    			Integer existAmount = NumberUtils.toInt(orderEntity.getPaidPrice()) + NumberUtils.toInt(orderEntity.getCarryIntoAmount());
    			if(balance <= 0 || existAmount < firstPhase){
            		retuValue = 1;
            	}
        	}else{
        		if(!balance.equals(orderEntity.getPayablePrice())){
            		retuValue = 0;
            	}
        	}
    	}else{
    		retuValue = -1;
    	}
    	return retuValue;
    }
    /**
	 * 订单明细操作-修改服务商、办理名称、操作公司
	 * @param orderProdId 订单产品Id
	 * @param supplierId 服务商Id
	 * @param handleName 办理名称
	 * @param companyId 关联公司Id或者0
	 * @return
	 */
    public Boolean begOption(Integer orderProdId, Integer supplierId, String handleName,Integer companyId){
    	return orderProdService.updateOrderDetail(orderProdId, supplierId, handleName, companyId);
    }
    /**
     * 订单明细操作-修改服务商
     * @param orderProdId 订单产品Id
     * @param supplierId 服务商Id
     * @return
     */
    public Boolean operateGroup(Integer orderProdId, Integer supplierId){
    	return orderProdService.updateOrderDetail(orderProdId, supplierId);
    }
}