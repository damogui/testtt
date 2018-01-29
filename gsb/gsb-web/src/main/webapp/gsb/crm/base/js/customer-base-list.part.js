System.Declare("com.gongsibao.crm.web");
//所有客户列表基类
com.gongsibao.crm.web.BaseCustomerListPart = org.netsharp.panda.commerce.ListPart.Extends({
	ctor : function() {
		this.base();
		this.addUrl = null;
		this.editUrl = null;
		this.addTaskUrl=null;
	},
	add:function(){
		
		window.open(this.addUrl);
	},
	detail:function(id){
		
		this.edit(id);
	},
	edit : function(id) {

		window.open(this.editUrl+"?id="+id);
	},
	doubleClickRow : function(index, row) {

		this.edit(row.id);
	},
	addTask:function(){

		var row = this.getSelectedItem();
		if(row == null){
			return;
		}
		var customerId = row.id;
    	var url=this.addTaskUrl+'?fk=customerId:'+customerId;
    	layer.open({
  		  type: 2,
  		  title: '新增任务',
  		  fixed: false,
  		  maxmin: true,
  		  shadeClose:true,
  		  area: ['90%','90%'],
  		  content: url,
  		  cancel: function(){ 

		  }
  	    });
	},
	openMember : function(customerId){

		var me = this;
		IMessageBox.confirm("您确定要开通会员吗？",function(r){
			
			if(r===true){

				me.invokeService("openMember", [customerId],function(data) {
					if(data){
						IMessageBox.toast('开通成功');
						me.reload();
						layer.closeAll();
					}else{
						IMessageBox.toast('开通失败,稍后再试');
					}
				});
			}
		});
	}
});
