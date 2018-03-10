System.Declare("com.gongsibao.crm.web");
com.gongsibao.crm.web.DepartmentAllTaskListPart = com.gongsibao.crm.web.BaseTaskListPart.Extends({
	ctor : function() {
		
		this.base();
		this.addUrl = "/panda/crm/department/task/add";
		this.editUrl = "/nav/gsb/crm/department/task";
		this.followUrl = '/panda/crm/department/task/follow';
		this.addCustomerUrl = '/panda/crm/department/customer/add';
	},
	dispose : function(id){
		var me = this;
		var content = '<p style="padding-left:50px;">&nbsp;处理记录：</p>'
				+ '<p style="padding-left:50px;">'
				+ '<textarea collected="true" controltype="TextArea" id="txtNote" style="width:445px;height:100px;" '
				+ ' class="easyui-validatebox validatebox-text" data-options="validateOnCreate:false,validateOnBlur:true">'
				+ '</textarea></p>';
		layer.open({
			type : 1,
			title : '处理记录',
			fixed : false,
			maxmin : false,
			shadeClose : false,
			area : [ '600px', '360px'],
			content : content,
			btn : [ '确定', '取消' ],// 可以无限个按钮
			btn1 : function(index, layero) {
				var getNote = $("#txtNote").val();
				if (System.isnull(getNote)) {
					IMessageBox.info('请输入处理记录');
					return false;
				}
				me.doDisposeService(id,getNote);
			},
			btn2 : function(index, layero) {
			}
		});
	},
	doDisposeService : function(id,getNote) {
		var state = 4;
		var type = 2;
		var me = this;
		this.invokeService("abnormal", [id,state,getNote,type],function(data) {
			me.reload();
			IMessageBox.toast('操作成功');
			layer.closeAll();
			return;
		});
	}
});