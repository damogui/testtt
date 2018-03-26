/**
 * Created by win on 2018/3/22.
 */
System.Declare("com.gongsibao.trade.web");
//订单业绩审核
com.gongsibao.trade.web.AuditOrderPerformanceListPart = org.netsharp.panda.commerce.ListPart.Extends({
    ctor: function () {
        this.base();

        this.auditUrl = "/nav/gsb/platform/trade/auditPerformance";//审核的urljsp 页面


    },

    audit: function (id) {//审核




      


        var me = this;
        var row = this.getSelectedItem();
        var rows = this.getSelections();
       
        var contentUrl = this.auditUrl + "?id=" + row.nDepReceivable_order_id+"&auditId="+row.id;


        layer.open({
            type: 2,//1是字符串 2是内容
            title: '订单业绩审核',
            fixed: false,
            maxmin: true,
            shadeClose: false,
            area: ['70%', '95%'],
            zIndex: 100000,
            id: "addAuditPerIframe",
            content: contentUrl,
            btn: ['审核通过', '审核不通过'],// 可以无限个按钮
            btn1: function (index, layero) {
               
                document.getElementById('addAuditPerIframe').firstElementChild.contentWindow.auditPerformanceCtrl .approved();
                IMessageBox.toast("审核通过");
                layer.close(index);
            },
            btn2: function (index, layero){
                document.getElementById('addAuditPerIframe').firstElementChild.contentWindow.auditPerformanceCtrl .rejected();
               return false;
            }
        });


    }


});