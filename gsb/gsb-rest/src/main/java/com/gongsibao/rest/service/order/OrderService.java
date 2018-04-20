package com.gongsibao.rest.service.order;

import com.gongsibao.bd.base.IDictService;
import com.gongsibao.entity.Result;
import com.gongsibao.entity.acount.Account;
import com.gongsibao.entity.bd.Dict;
import com.gongsibao.entity.bd.Preferential;
import com.gongsibao.entity.bd.PreferentialCode;
import com.gongsibao.entity.bd.dic.DictType;
import com.gongsibao.entity.crm.Customer;
import com.gongsibao.entity.dict.ResponseStatus;
import com.gongsibao.entity.product.Price;
import com.gongsibao.entity.product.Product;
import com.gongsibao.entity.product.WorkflowNode;
import com.gongsibao.entity.trade.*;
import com.gongsibao.entity.trade.dic.*;
import com.gongsibao.product.base.IWorkflowNodeService;
import com.gongsibao.rest.base.product.IProductService;
import com.gongsibao.rest.dto.coupon.CouponValidateDTO;
import com.gongsibao.rest.dto.order.OrderProdAddDto;
import com.gongsibao.rest.dto.product.ProductPriceDTO;
import com.gongsibao.rest.base.bd.ICouponService;
import com.gongsibao.rest.base.customer.ICustomerService;
import com.gongsibao.rest.base.order.IInvoiceService;
import com.gongsibao.rest.base.order.IOrderService;
import com.gongsibao.rest.web.common.security.SecurityUtils;
import com.gongsibao.rest.web.common.util.AmountUtils;
import com.gongsibao.rest.web.common.util.NumberUtils;
import com.gongsibao.rest.web.common.util.StringUtils;
import com.gongsibao.rest.web.common.web.Pager;
import com.gongsibao.rest.web.dto.order.OrderAddDTO;
import com.gongsibao.rest.web.dto.order.OrderDTO;
import com.gongsibao.rest.web.dto.order.OrderProductDTO;
import com.gongsibao.utils.DateUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.netsharp.communication.ServiceFactory;
import org.netsharp.core.annotations.Transaction;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * ClassName: $
 *
 * @author wangkun <wangkun@gongsibao.com>
 * @Description: TODO
 * @date $ $
 */
@Service
public class OrderService implements IOrderService {
    private static AtomicLong atomicLong = new AtomicLong();

    // 订单服务
    com.gongsibao.trade.base.IOrderService tradeOrderService = ServiceFactory.create(com.gongsibao.trade.base.IOrderService.class);

    // 字典服务
    IDictService dictService = ServiceFactory.create(IDictService.class);
    IWorkflowNodeService workflowNodeService = ServiceFactory.create(IWorkflowNodeService.class);

    // 产品服务
    @Autowired
    IProductService productService;

    // CRM客户服务
    @Autowired
    ICustomerService customerService;

    // 优惠券服务
    @Autowired
    ICouponService couponService;

    @Autowired
    IInvoiceService invoiceService;

    @Override
    @Transaction
    public Result<SoOrder> saveOrder(OrderAddDTO orderAddDTO) {
        // 1. 处理订单信息
        Result<SoOrder> result = doOrder(orderAddDTO);
        if (Result.isFailed(result)) {
            return result;
        }
        SoOrder order = result.getObj();

        // 2. 处理优惠券信息
        Result<Preferential> couponResult = doCoupon(order, orderAddDTO);
        if (Result.isFailed(couponResult)) {
            result.setObj(null);
            result.setMsg(couponResult.getMsg());
            result.setStatus(ResponseStatus.FAILED);
            return result;
        }

        // 3. 处理发票信息
        Invoice invoice = doInvoice(order, orderAddDTO);

        // 4. 保存订单
        order = tradeOrderService.saveWebOrder(order, invoice, StringUtils.stringToList(orderAddDTO.getOrderDiscount()));

        result.setObj(order);
        return result;
    }

    @Override
    public Pager<OrderDTO> pageMyOrder(Integer accountId, Integer status, int currentPage, int pageSize) {
        int total = tradeOrderService.countOrderAccountIdStatus(accountId, status);
        if (total <= 0) {
            return new Pager<>(total, currentPage, pageSize);
        }
        Pager<OrderDTO> pager = new Pager<OrderDTO>(total, currentPage, pageSize);
        List<SoOrder> soOrders = tradeOrderService.pageOrderListByAccountIdStatus(accountId, status, currentPage,
                pageSize);
        if(soOrders!=null){
            List<OrderDTO> orderDtoList = soOrders.stream().map(soOrder -> {
                OrderDTO orderDTO = new OrderDTO();
                {
                    orderDTO.setPkid(soOrder.getId());
                    orderDTO.setNo(soOrder.getNo());
                    orderDTO.setAddTime(soOrder.getCreateTime());
                    orderDTO.setAdd_time(soOrder.getCreateTime());
                    orderDTO.setProcessStatusId(soOrder.getProcessStatus().getValue());
                    orderDTO.setPayStatusId(soOrder.getPayStatus().getValue());
                    orderDTO.setPayablePrice(soOrder.getPayablePrice());
                    orderDTO.setPaidPrice(soOrder.getPaidPrice());
                    orderDTO.setIsChangePrice(BooleanUtils.toInteger(soOrder.getIsChangePrice(),1,0));
                    orderDTO.setChangePriceAuditStatusId(soOrder.getChangePriceAuditStatus().getValue());
                    orderDTO.setType(soOrder.getType().getValue());
                    orderDTO.setIsInstallment(BooleanUtils.toInteger(soOrder.getIsInstallment(),1,0));
                    orderDTO.setInstallmentAuditStatusId(soOrder.getInstallmentAuditStatusId().getValue());
                    List<OrderProd> products = soOrder.getProducts().stream().sorted((o1, o2) -> {
                        Long first = o1.getCreateTime() == null ? 0 : o1.getCreateTime().getTime();
                        Long next = o2.getCreateTime() == null ? 0 : o2.getCreateTime().getTime();
                        return first.compareTo(next);
                    }).collect(Collectors.toList());
                    orderDTO.setOrderProdListWebs(products.stream().map(orderProd -> {
                        return convertTo(soOrder,orderProd);
                    }).collect(Collectors.toList()));

                }
                return orderDTO;
            }).collect(Collectors.toList());
            pager.setList(orderDtoList);
        }
        return pager;
    }

    private OrderProductDTO convertTo(SoOrder soOrder,OrderProd orderProd){
        OrderProductDTO orderProductDTO = new OrderProductDTO();
        orderProductDTO.setAccountMobile(soOrder.getAccountMobile());
        orderProductDTO.setAccountName(soOrder.getAccountName());
        orderProductDTO.setAddTime(soOrder.getCreateTime());
        orderProductDTO.setAuditStatusId(orderProd.getAuditStatus().getValue());
        orderProductDTO.setAuditStatusName(orderProd.getAuditStatus().getText());
        orderProductDTO.setCityName(dictService.byId(orderProd.getCityId()).getName());
        orderProductDTO.setIsComplaint(BooleanUtils.toInteger(orderProd.getIsComplaint(),1,0));
        orderProductDTO.setIsPackage(BooleanUtils.toInteger(soOrder.getIsPackage(),1,0));
        orderProductDTO.setIsRefund(BooleanUtils.toInteger(orderProd.getIsRefund(),1,0));
        orderProductDTO.setIsinstallment(BooleanUtils.toInteger(soOrder.getIsInstallment(),1,0));
        orderProductDTO.setNeedDays(orderProd.getNeedDays());
        orderProductDTO.setNo(orderProd.getNo());
        orderProductDTO.setOrderId(orderProd.getOrderId());
        orderProductDTO.setOrderNo(soOrder.getNo());
        orderProductDTO.setOrderProdIdStr(SecurityUtils.rc4Encrypt(soOrder.getId()));
        orderProductDTO.setPkid(0);
        orderProductDTO.setPrice(orderProd.getPrice());
        orderProductDTO.setPriceOriginal(orderProd.getPriceOriginal());
        orderProductDTO.setProcessedDays(orderProd.getProcessedDays());
        orderProductDTO.setProductIdStr(SecurityUtils.rc4Encrypt(orderProd.getProductId()));
        orderProductDTO.setProductName(orderProd.getProductName());
        orderProductDTO.setShowProcess(0);//TODO 不显示
        orderProductDTO.setSoOrderAddtime(soOrder.getCreateTime());
        orderProductDTO.setSoOrderPaidPrice(soOrder.getPaidPrice());
        orderProductDTO.setSoOrderPayStatusId(soOrder.getPayStatus().getValue());
        orderProductDTO.setSoOrderPrice(soOrder.getPayablePrice());
        orderProductDTO.setSoOrderPriceOriginal(soOrder.getTotalPrice());
        orderProductDTO.setTimeoutDays(orderProd.getTimeoutDays());
        orderProductDTO.setType(soOrder.getType().getValue());
        if (orderProductDTO.getSoOrderPaidPrice().compareTo(orderProductDTO.getSoOrderPriceOriginal()) == 0 || orderProd.getProcessStatusId() > 0) {
            orderProductDTO.setShowProcess(1);
        }
        if(soOrder.getPayStatus().getValue().equals(3011)){
            if(soOrder.getPayStatus().getValue().equals(3023)){
                orderProductDTO.setSoOrderProcessStatusName("已取消");
            }else{
                orderProductDTO.setSoOrderProcessStatusName("待付款");
            }
        }else if(soOrder.getPayStatus().getValue().equals(3012)){
            if (orderProductDTO.getIsinstallment() == 1) {
                WorkflowNode workflowNode = workflowNodeService.byId(orderProd.getProcessStatusId());
                if(workflowNode==null){
                    orderProductDTO.setSoOrderProcessStatusName("待办理");
                }else{
                    orderProductDTO.setSoOrderProcessStatusName(workflowNode.getName());
                }
            } else {
                orderProductDTO.setSoOrderProcessStatusName("已付部分款");
            }
        }else {
            WorkflowNode workflowNode = workflowNodeService.byId(orderProd.getProcessStatusId());
            if (null == workflowNode) {
                orderProductDTO.setSoOrderProcessStatusName("待办理");
            } else {
                orderProductDTO.setSoOrderProcessStatusName(workflowNode.getName());
            }
        }
        orderProductDTO.setOrderIdStr(SecurityUtils.rc4Encrypt(orderProd.getOrderId()));
        orderProductDTO.setCityId(0);
        orderProductDTO.setOrderId(0);
        orderProductDTO.setSoOrderProcessStatusId(0);
        orderProductDTO.setProcessStatusId(0);
        orderProductDTO.setOrderProdIdStr(SecurityUtils.rc4Encrypt(orderProd.getId()));
        orderProductDTO.setPkid(0);
        orderProductDTO.setAuditStatusId(0);
        orderProductDTO.setIsAssign(0);
        return orderProductDTO;
    }

    private Invoice doInvoice(SoOrder order, OrderAddDTO orderAddDTO) {
        // 发票信息处理
        Integer invoiceId = orderAddDTO.getInvoiceId();
        if (null != invoiceId && invoiceId > 0) {
            Invoice invoice = invoiceService.byId(invoiceId);
            if (null != invoice) {
                // 订单状态
                order.setIsInvoice(true);
                // 发票更新
                {
                    invoice.toPersist();
                    invoice.setAmount(order.getPayablePrice());
                    invoice.setContent(orderAddDTO.getPlatformType().getText() + "下单发票");
                }

                // 订单关联发票
                OrderInvoiceMap orderInvoiceMap = new OrderInvoiceMap();
                {
                    orderInvoiceMap.toNew();
                    orderInvoiceMap.setInvoiceId(invoice.getId());
                }
                invoice.setOrderInvoiceMaps(Arrays.asList(orderInvoiceMap));
                return invoice;
            }
        }
        return null;
    }

    private Result doCoupon(SoOrder order, OrderAddDTO orderAddDTO) {
        Result result = new Result();
        // 订单原价
        double payablePrice = order.getPayablePrice();

        List<String> couponNoList = StringUtils.stringToList(orderAddDTO.getOrderDiscount());
        if (CollectionUtils.isNotEmpty(couponNoList)) {
            // 查询历史订单数量
            Integer orderCount = tradeOrderService.countByAccountId(orderAddDTO.getAccount().getId(), true);

            // 优惠券查询
            Map<String, PreferentialCode> couponMap = couponService.mapByNos(couponNoList);

            // 优惠券使用平台查询
            Map<Integer, Dict> platformMap = dictService.mapByType(DictType.Yhqsypt.getValue());

            List<OrderDiscount> orderDiscountList = new ArrayList<>();
            for (String no : couponNoList) {
                PreferentialCode code = couponMap.get(no);
                if (null == code || null == code.getPreferential()) {
                    return new Result(ResponseStatus.FAILED, "优惠券[" + no + "]不存在");
                }
                Preferential coupon = code.getPreferential();
                // 封装优惠券验证对象
                CouponValidateDTO validateDTO = new CouponValidateDTO();
                {
                    validateDTO.setOrder(order);
                    validateDTO.setCouponNo(no);
                    validateDTO.setOrderCount(orderCount);
                    validateDTO.setPlatformType(orderAddDTO.getPlatformType());
                    validateDTO.setPreferentialCode(code);
                    validateDTO.setPlatformMap(platformMap);
                }

                // 验证优惠券是否可用
                result = couponService.validOrderCoupon(validateDTO);
                if (Result.isFailed(result)) {
                    return result;
                }

                // 计算订单价格
                Double couponPrice = couponService.couponPrice(payablePrice, coupon);
                payablePrice = AmountUtils.sub(payablePrice, couponPrice);

                // 设置订单关联优惠券
                OrderDiscount orderDiscount = new OrderDiscount();
                {
                    orderDiscount.toNew();
                    orderDiscount.setAmount(NumberUtils.doubleRoundInt(couponPrice));
                    orderDiscount.setPreferentialId(coupon.getId());
                    orderDiscount.setNo(no);
                    orderDiscount.setSqlid("");
                    orderDiscount.setRemark(orderAddDTO.getPlatformType().getText() + "下单使用");
                }
                orderDiscountList.add(orderDiscount);
            }

            // 特么的不会优惠到负值吧！
            if (payablePrice < 0) {
                payablePrice = 0d;
            }

            order.setPayablePrice(NumberUtils.doubleRoundInt(payablePrice));
            order.setDiscounts(orderDiscountList);
        }

        return result;
    }


    private Result<SoOrder> doOrder(OrderAddDTO orderAddDTO) {
        Account account = orderAddDTO.getAccount();
        // 组建order对象
        List<OrderProdAddDto> prodDtoList = orderAddDTO.getProductList();
        if (CollectionUtils.isEmpty(prodDtoList)) {
            return new Result<>(ResponseStatus.FAILED, "商品不能为空");
        }

        int customerId = 0;
        Customer customer = customerService.byAccountId(account.getId());
        if (null != customer) {
            customerId = customer.getId();
        }

        // 产品id
        List<Integer> productIds = new ArrayList<>();
        // 字段id
        List<Integer> dictIds = new ArrayList<>();
        // 定价id
        List<Integer> priceIds = new ArrayList<>();
        for (OrderProdAddDto orderProdAddDto : prodDtoList) {
            List<ProductPriceDTO> priceList = orderProdAddDto.getPriceList();
            if (CollectionUtils.isEmpty(priceList)) {
                return new Result<>(ResponseStatus.FAILED, "定价不能为空");
            }

            productIds.add(orderProdAddDto.getProductId());
            dictIds.add(orderProdAddDto.getCityId());

            for (ProductPriceDTO productPriceDTO : priceList) {
                priceIds.add(productPriceDTO.getPriceId());
            }
        }

        // 获取产品
        Map<Integer, Product> productMap = productService.mapByIds(productIds);
        // 字典
        Map<Integer, Dict> dictMap = dictService.findMapByIds(dictIds);
        // 定价
        Map<Integer, Price> priceMap = productService.mapPriceByIds(priceIds);

        // 封装orderProd 对象
        List<OrderProd> orderProdList = new ArrayList<>();
        Map<String, Integer> productNumMap = new HashMap<>();
        for (OrderProdAddDto orderProdAddDto : prodDtoList) {
            Integer productId = orderProdAddDto.getProductId();
            Product product = productMap.get(productId);
            if (null == product) {
                return new Result<>(ResponseStatus.FAILED, "商品不能为空");
            }
            Dict city = dictMap.get(orderProdAddDto.getCityId());
            if (null == city) {
                return new Result<>(ResponseStatus.FAILED, "请选择城市");
            }
            int quantity = 0;
            if (StringUtils.isNotBlank(orderAddDTO.getCategoryIds())) {
                quantity = StringUtils.idsToList(orderAddDTO.getCategoryIds()).size();
            } else {
                quantity = orderProdAddDto.getQuantity();
            }

            if (quantity == 0) {
                quantity = 1;
            }

            for (int i = 0; i < quantity; i++) {
                // 现价
                int opPrice = 0;
                // 原价
                int opOriginalPrice = 0;

                List<OrderProdItem> itemList = new ArrayList<>();
                for (ProductPriceDTO productPriceDTO : orderProdAddDto.getPriceList()) {
                    Price price = priceMap.get(productPriceDTO.getPriceId());
                    if (null == price) {
                        return new Result<>(ResponseStatus.FAILED, "定价不存在");
                    }
                    opPrice = opPrice + price.getPrice();
                    opOriginalPrice = opOriginalPrice + price.getOriginalPrice();

                    OrderProdItem item = new OrderProdItem();
                    {
                        item.toNew();
                        item.setPrice(price.getPrice());
                        item.setPriceOriginal(price.getOriginalPrice());
                        item.setServiceName(StringUtils.trimToEmpty(productPriceDTO.getTypeName()));
                        item.setPriceId(productPriceDTO.getPriceId());
                        item.setUnitName(StringUtils.trimToEmpty(productPriceDTO.getUnitName()));
                        item.setPriceRefund(0);
                        item.setIsBbk("");
                        item.setCreatorId(0);
                    }
                    itemList.add(item);
                }

                OrderProd orderProd = new OrderProd();
                {
                    orderProd.toNew();
                    orderProd.setNo("");
                    orderProd.setOrderId(0);
                    orderProd.setProductId(productId);
                    orderProd.setProductName(product.getName());
                    orderProd.setCityId(orderProdAddDto.getCityId());
                    orderProd.setCity(city);
                    orderProd.setCityName(city.getName());
                    orderProd.setQuantity(1);
                    orderProd.setCompanyId(orderAddDTO.getCompanyId());
                    orderProd.setTrademarkId(0);
                    orderProd.setPrice(opPrice);
                    orderProd.setPriceOriginal(opOriginalPrice);
                    orderProd.setCostStatus(CostStatus.NOENTRY);
                    orderProd.setOwnerId(orderAddDTO.getOwnerId());
                    orderProd.setCustomerId(customerId);
                    orderProd.setSettleStatus(SettleStatus.NO_SETTLEMENT);
                    orderProd.setItems(itemList);
                    orderProd.setCompanyId(0);
                    orderProd.setCreatorId(0);
                }
                productNumMap.put(product.getName(), 1 + NumberUtils.toInt(productNumMap.get(product.getName())));
                orderProdList.add(orderProd);
            }
        }

        int orderPrice = 0;
        int orderPayablePrice = 0;
        for (OrderProd orderProd : orderProdList) {
            orderPrice = orderPrice + orderProd.getPriceOriginal();
            orderPayablePrice = orderPayablePrice + orderProd.getPrice();
        }

        StringBuilder prodName = new StringBuilder();
        for (Map.Entry<String, Integer> entry : productNumMap.entrySet()) {
            String k = entry.getKey();
            Integer v = entry.getValue();
            if (v == 1) {
                prodName.append(k).append(",");
            } else {
                prodName.append(k).append("*").append(v).append(",");
            }
        }

        if (prodName.length() > 0) {
            prodName = prodName.delete(prodName.length() - 1, prodName.length());
        }

        SoOrder order = new SoOrder();
        {
            order.toNew();
            order.setNo(System.currentTimeMillis() + "-" + String.valueOf(atomicLong.getAndIncrement() % 1024));
            order.setProducts(orderProdList);
            order.setAccountId(account.getId());
            order.setAccountMobile(account.getMobilePhone());
            order.setAccountName(StringUtils.isBlank(account.getRealName()) ? account.getMobilePhone() : account.getRealName());
            order.setOwnerId(orderAddDTO.getOwnerId());
            order.setTaskId(0);
            order.setTotalPrice(orderPrice);
            order.setPayablePrice(orderPayablePrice);
            order.setPaidPrice(0);
            order.setChannelOrderNo("");
            order.setSourceType(OrderSourceType.IGIRL_TM);
            order.setIsInstallment(false);
            order.setInstallmentAuditStatusId(AuditStatusType.wu);
            order.setCompanyId(orderAddDTO.getCompanyId());
            order.setPlatformSource(OrderPlatformSourceType.Gsb);
            order.setCustomerId(customer.getId());
            order.setCustomerName(customer.getRealName());
            order.setDeliverId(orderAddDTO.getDeliverId());
            order.setCompanyId(orderAddDTO.getCompanyId());
            order.setCreatorId(0);
            order.setProdName(prodName.toString());

        }
        Result<SoOrder> result = new Result<>();
        result.setObj(order);
        return result;
    }
}
