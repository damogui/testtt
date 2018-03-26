package com.gongsibao.trade.web;

import com.gongsibao.bd.base.IAuditLogService;
import com.gongsibao.entity.bd.AuditLog;
import com.gongsibao.entity.trade.OrderPayMap;
import com.gongsibao.entity.trade.Pay;
import com.gongsibao.entity.trade.SoOrder;
import com.gongsibao.trade.base.IOrderService;
import org.netsharp.communication.ServiceFactory;
import org.netsharp.core.Oql;
import org.netsharp.panda.commerce.AdvancedListPart;
import org.netsharp.panda.commerce.EasyuiDatagridResult;
import org.netsharp.panda.json.DatagridResultJson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by win on 2018/3/20.
 */
/*回款审核*/
public class AuditPayListPart extends AdvancedListPart {



    @Override
    public List<?> doQuery(Oql oql) {
        StringBuilder selects = new StringBuilder ();
        selects.append ("AuditLog.*,");
        selects.append ("AuditLog.pay.*,");
        selects.append ("AuditLog.pay.order.*");

        IAuditLogService auditLogService = ServiceFactory.create (IAuditLogService.class);
        oql.setSelects (selects.toString ());
        List<AuditLog> auditLogs = auditLogService.queryList (oql);
        return auditLogs;
    }



    //@Override
    public List<?> doQuery11(Oql oql) {
        oql.setSelects ("id,payForOrderCount,payWayType,amount,offlineAuditStatus,createTime,creator,orderPayMaps.*");
        List<Pay> resList = (List<Pay>) super.doQuery (oql);

        return resList;
    }


//    @Override
    protected Object serialize11(List<?> list, Oql oql) {

        HashMap<String, Object> json = (HashMap<String, Object>) super.serialize (list, oql);
        ArrayList<HashMap<String, Object>> ob2 = (ArrayList<HashMap<String, Object>>) json.get ("rows");

        for (int i = 0; i < ob2.size (); i++) {
            ob2.get (i).put ("orderIds", getOrderIds (list.get (i)));


        }


        return json;
    }

    /*获取订单号*/
    private Object getOrderIds(Object o) {
        Pay pay = (Pay) o;
        IOrderService orderService = ServiceFactory.create (IOrderService.class);//订
        /*根据订单id获取订单编号*/
        StringBuilder sb = new StringBuilder ();
        if (pay.getOrderPayMaps ().size () > 0) {


            for (OrderPayMap item : pay.getOrderPayMaps ()
                    ) {


                if (!item.equals (pay.getOrderPayMaps ().get (pay.getOrderPayMaps ().size () - 1))) {
                    sb.append ("<p>");

                }
                SoOrder order = orderService.getByOrderId (item.getOrderId ());
                if (order != null) {

                    sb.append (order.getNo ());
                }


                if (!item.equals (pay.getOrderPayMaps ().get (pay.getOrderPayMaps ().size () - 1))) {
                    sb.append ("</p>");

                }


            }


        }
        return sb.toString ();
    }


}
