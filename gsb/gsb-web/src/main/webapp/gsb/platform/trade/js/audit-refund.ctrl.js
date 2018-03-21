System.Declare("com.gongsibao.trade.web");
com.gongsibao.trade.web.AuditRefundCtrl = com.gongsibao.trade.web.AuditBaseCtrl.Extends({
    ctor: function () {
    	
    	this.base();
    	this.initializeDetailList = new System.Dictionary();
    	this.service = 'com.gongsibao.trade.web.audit.AuditRefundController';
    },
    initData:function(){
    	var me = this;
    	var refundId = this.queryString('id');
    	me.refundInfor(refundId);
    	
    	//加载Tab项
    	$('#detail_tabs').tabs({ 
    		tabHeight:30,
		    onSelect:function(title){
		    	var detailCtrl = me.initializeDetailList.byKey(title);
		    	if(detailCtrl){		    		
		    		//已经初始化过的不再执行
		    		return;
		    	}
		    	if(title=='退款业绩分配'){
		    		me.resultsfundInfor(refundId);
		    	}else if(title=='审批进度'){
		    		me.auditLogInfor();
		    	}
		    }
    	});
    	//加载默认第一项‘退款产品’
    	me.initializeDetailList.add('退款产品',this.refundProductInfor(refundId));
    },
    refundInfor: function(id){
    	//退款信息
    	this.invokeService("getRefundById", [id], function(data){
    		//临时存储OrderId,为了获取审核进度
    		$("#tempOrderId").val(data.orderId);
    		$("#refund_info_grid tr").eq(0).find("td").eq(1).html(data.setOfBooks.name);
    		$("#refund_info_grid tr").eq(0).find("td").eq(3).html(data.refundType);
    		
    		$("#refund_info_grid tr").eq(1).find("td").eq(1).html(data.payerName);
    		$("#refund_info_grid tr").eq(1).find("td").eq(3).html(data.bankNo);
    		$("#refund_info_grid tr").eq(1).find("td").eq(5).html((data.amount/100).toFixed(2));
    		
    		$("#refund_info_grid tr").eq(2).find("td").eq(1).html(data.remark);
    	});
    },
    refundProductInfor: function(id){
    	//tab-退款产品
    	
    },    
    resultsfundInfor: function(id){
    	//tab-退款业绩信息
    	this.invokeService("getNDepRefund", [id], function(data){    		
    		$('#audit_refund_grid').datagrid({
    			idField:'id',
    			emptyMsg:'暂无记录',
    			striped:true,
    			pagination:false,
    			showFooter:true,
    			singleSelect:true,
    			height:'100%',
    			data:data,
    		    columns:[[
    		        {field:'supplierId',title:'服务商',width:80,align:'center',formatter: function(value,row,index){
    		        	return row.supplier.name;
    		        }},
    		        {field:'departmentId',title:'部门',width:80,align:'center',formatter: function(value,row,index){
    		        	return row.department.name;
    		        }},
    		        {field:'salesmanId',title:'业务员',width:300,align:'center',formatter: function(value,row,index){
    		        	return row.salesman.name;
    		        }},
    		        {field:'amount',title:'退款业绩分配金额',width:280,align:'right',formatter: function(value,row,index){
    		        	return (value/100).toFixed(2);
    		        }},
    		    ]]
    		});
    	});
    },
    auditLogInfor: function(){
    	//tab-审批进度
    	var orderId = $("#tempOrderId").val();    	
    	this.invokeService("getAuditLogList", [orderId,1046], function(data){    		
    		$('#audit_progress_grid').datagrid({
    			idField:'id',
    			emptyMsg:'暂无记录',
    			striped:true,
    			pagination:false,
    			showFooter:true,
    			singleSelect:true,
    			height:'100%',
    			data:data,
    		    columns:[[
    		        {field:'creator',title:'创建人名称',width:80,align:'center'},
    		        {field:'status',title:'审核状态',width:80,align:'center',formatter: function(value,row,index){
    		        	return getStatus(value);
    		        }},
    		        {field:'createTime',title:'创建时间',width:300,align:'center'},
    		        {field:'content',title:'审批内容',width:280,align:'right'},
    		        {field:'remark',title:'说明',width:300,align:'center'}
    		    ]]
    		});
    	});
    }
});

//判断审核状态
function getStatus(status){
	var statString = '待审核';
	switch (status){
	case 1051:
		statString = "待审核";
	  break;
	case 1052:
		statString = "审核中";
	  break;
	case 1053:
		statString = "驳回审核";
	  break;
	case 1054:
		statString = "审核通过";
	  break;
	case 1055:
		statString = "排队";
	  break;
	default:
		statString = "关闭";
	}
	return statString;
}