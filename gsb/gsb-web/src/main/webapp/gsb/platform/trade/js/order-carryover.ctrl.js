System.Declare("com.gongsibao.trade.web");
com.gongsibao.trade.web.OrderCarryoverCtrl = org.netsharp.panda.core.CustomCtrl.Extends({
    ctor: function () {
    	
    	this.base();
    	this.service = 'com.gongsibao.trade.web.OrderCarryoverController';
    },
    init:function(){
	
    	var orderId = this.queryString('id');
    	this.invokeService ("getSoOrder", [orderId], function(data){
    		
    		$("payablePrice_hidden").val(data.payablePrice);
      		$("#paidPrice_hidden").val(data.paidPrice);
      		$("#refundPrice_hidden").val(data.refundPrice);
      		$("#carryAmount_hidden").val(data.carryAmount);
      		$("#carryIntoAmount_hidden").val(data.carryIntoAmount);
      		
    		$('#formOrderNo').val(data.no);
    	});
    },
    amountChange:function(newValue,oldValue){
    	//1.判断结转转出额是否大于订单余额
    	var payAblePrice = $("payablePrice_hidden").val();
    	var paidPrice = $("#paidPrice_hidden").val();
    	var refundPrice = $("#refundPrice_hidden").val();
    	var carryAmount = $("#carryAmount_hidden").val();
    	var carryIntoAmount = $("#carryIntoAmount_hidden").val();
    	
    	var balance = parseInt(paidPrice) + parseInt(carryIntoAmount) - parseInt(refundPrice) - parseInt(carryAmount);    	
    	var getNewValue = parseFloat(newValue)*100;
    	
    	if((balance - getNewValue) < 0){
    		$('#amount').numberbox('clear');
    		layer.msg('结转金额大于订单余额，请核实');
    		return false;
    	}
    	
    	//2.判断结转转入额是否大于转入订单待付款金额(订单待付款金额 = 订单应付金额 - 订单余额)
    	var toPayAmount = $("#toPayAmount_hidden").val();
    	if(!isEmpty($("#toOrderNo").val()) && (parseInt(toPayAmount) - getNewValue) < 0){
    		$('#amount').numberbox('clear');
    		layer.msg('结转金额大于转入订单待付款金额，请核实');
    	}
    },
    toOrderNoBlur:function(){
    	var me = this;
    	var orderNo = $('#toOrderNo').val();
    	me.invokeService("getSoOrderByNo", [orderNo], function(data){
    		if(data == null){
    			$('#toOrderNo').val("");
    			layer.msg('去向订单号输入有误，请重新输入');
    		}else{
    			if(data.carryStatus != 0 && data.carryStatus != 1054){
    				$('#toOrderNo').val("");
    				layer.msg('去向订单待审核中，暂不能操作！');
    			}else{
    				$("#toOrderId_hidden").val(data.id);
    				
    				var toPayablePrice = data.payablePrice;
    	      		var toPaidPrice = data.paidPrice;
    	      		var toRefundPrice = data.refundPrice;
    	      		var toCarryAmount = data.carryAmount;
    	      		var toCarryIntoAmount = data.carryIntoAmount;
    	      		
    	      		var balance = parseInt(toPaidPrice) + parseInt(toCarryIntoAmount) - parseInt(toRefundPrice) - parseInt(toCarryAmount);
    	      		var toPayAmount = parseInt(toPayablePrice) - parseInt(balance);    	      		
    	      		$("#toPayAmount_hidden").val(toPayAmount);
    			}
    		}
    	});
    },
    save:function(){
    	
    	var formOrderId = this.queryString('id');
    	var formOrderNo = $('#formOrderNo').val();
    	var toOrderId = $("#toOrderId_hidden").val();
    	var toOrderNo = $('#toOrderNo').val();
    	var remark = $('#carryRemark').val();
    	
    	var amount = parseFloat($('#amount').numberbox('getValue'))*100;
    	//未校验
    	if(isEmpty(toOrderNo) || isNaN(amount) || isEmpty(remark)){
    		layer.msg('请输入必填项');
    		return false;
    	}
    	var orderCarryover = {
    			formOrderId:formOrderId,
    			toOrderId:toOrderId,
    			formOrderNo:formOrderNo,
    			toOrderNo:toOrderNo,
    			amount:amount,
    			remark:remark
    	};
    	
    	var me = this;
    	IMessageBox.confirm('确定提交申请吗？',function(r){
    		if(r){
    			me.invokeService("applyCarryover", [orderCarryover], function(data){
    	    		IMessageBox.info('申请成功，请等待审核!',function(s){
    	    			window.parent.layer.closeAll();
    	    		});
    	    	});
    		}
    	});
    }
});
//字符验证
function isEmpty(obj){
    if(typeof obj == "undefined" || obj == null || obj == "" ){
        return true;
    }else{
        return false;
    }
}