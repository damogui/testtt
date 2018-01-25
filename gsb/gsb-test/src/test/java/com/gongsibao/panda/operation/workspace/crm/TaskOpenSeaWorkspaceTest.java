package com.gongsibao.panda.operation.workspace.crm;

import org.junit.Before;
import org.junit.Test;
import org.netsharp.core.MtableManager;
import org.netsharp.meta.base.WorkspaceCreationBase;
import org.netsharp.organization.dic.OperationTypes;
import org.netsharp.panda.controls.ControlTypes;
import org.netsharp.panda.entity.PDatagrid;
import org.netsharp.panda.entity.PDatagridColumn;
import org.netsharp.panda.entity.PQueryItem;
import org.netsharp.panda.entity.PQueryProject;
import org.netsharp.panda.plugin.dic.ToolbarType;
import org.netsharp.panda.plugin.entity.PToolbar;
import org.netsharp.panda.plugin.entity.PToolbarItem;
import org.netsharp.resourcenode.entity.ResourceNode;

import com.gongsibao.crm.web.TaskOpenSeaListPart;
import com.gongsibao.entity.crm.NCustomerTask;
import com.gongsibao.entity.crm.NCustomerTaskFoolow;

public class TaskOpenSeaWorkspaceTest extends WorkspaceCreationBase{

	@Override
	@Before
	public void setup() {
			
		entity = NCustomerTask.class;
		urlList = "/operation/task/opensea/list";

		listPartName = formPartName = "公海";
		meta = MtableManager.getMtable(entity);
		formPartName = listPartName = meta.getName();
		resourceNodeCode = "GSB_CRM_Customer_Manager_OpenSea";

		listFilter = "(owner_id is null or owner_id =0)";
		listToolbarPath = "task/batch/allocation";
		listPartJsController = TaskOpenSeaListPart.class.getName();
		listPartServiceController = TaskOpenSeaListPart.class.getName();
		listPartImportJs = "/gsb/crm/base/js/task-base-list.part.js|/gsb/crm/platform/js/task-opensea-list.part.js";
	}
	
	@Test
	public void createListToolbar() {
		ResourceNode node = this.resourceService.byCode(NCustomerTaskFoolow.class.getSimpleName());
		//OperationType ot1 = operationTypeService.byCode(OperationTypes.add);
		PToolbar toolbar = new PToolbar();
		{
			toolbar.toNew();
			toolbar.setPath(listToolbarPath);
			toolbar.setName("批量分配");
			toolbar.setResourceNode(node);
			toolbar.setToolbarType(ToolbarType.BASE);
		}
		PToolbarItem item = new PToolbarItem();
		{
			item.toNew();
			item.setCode("add");
			item.setIcon("fa fa-check");
			item.setName("批量分配");
			item.setCommand(null);
			item.setSeq(1);
			item.setCommand("{controller}.batchAllocation();");
			toolbar.getItems().add(item);
		}
		toolbarService.save(toolbar);
	}
	
	@Override
	protected PDatagrid createDatagrid(ResourceNode node) {
		
		PDatagrid datagrid = super.createDatagrid(node);
		datagrid.setShowCheckbox(true);
		datagrid.setSingleSelect(false);
		datagrid.setToolbar("panda/datagrid/row/edit");
		PDatagridColumn column = null;

		column = addColumn(datagrid, "ownerId", "操作", ControlTypes.OPERATION_COLUMN, 100, true);
		column = addColumn(datagrid, "id", "任务ID", ControlTypes.TEXT_BOX, 60, false);
		column = addColumn(datagrid, "name", "任务名称", ControlTypes.TEXT_BOX, 100, false);
		column = addColumn(datagrid, "customerId", "客户ID", ControlTypes.TEXT_BOX, 60, false);
		column = addColumn(datagrid, "customer.realName", "客户名称", ControlTypes.TEXT_BOX, 100, false);

		//column = addColumn(datagrid, "customer.realName", "公司名称", ControlTypes.TEXT_BOX, 100, true);
		column = addColumn(datagrid, "customer.isMember", "是否会员", ControlTypes.BOOLCOMBO_BOX, 100, false);
		column = addColumn(datagrid, "customer.mobile", "手机号", ControlTypes.TEXT_BOX, 100, false);
		column = addColumn(datagrid, "customer.telephone", "座机", ControlTypes.TEXT_BOX, 100, false);
		column = addColumn(datagrid, "customer.qq", "QQ", ControlTypes.TEXT_BOX, 100, false);
		column = addColumn(datagrid, "customer.weixin", "微信", ControlTypes.TEXT_BOX, 100, false);
		
		//column = addColumn(datagrid, "customer.realName", "其他联系方式", ControlTypes.TEXT_BOX, 100, true);

		column = addColumn(datagrid, "intentionCategory", "质量分类", ControlTypes.ENUM_BOX, 100, false);
		column = addColumn(datagrid, "quality.name", "客户质量", ControlTypes.TEXT_BOX, 100, false);
		column = addColumn(datagrid, "nextFoolowTime", "下次跟进时间", ControlTypes.DATE_BOX, 100, false);
		column = addColumn(datagrid, "lastFoolowUser.name", "最后跟进人", ControlTypes.TEXT_BOX, 100, false);
		
		//column = addColumn(datagrid, "customer.realName", "意向产品", ControlTypes.TEXT_BOX, 100, true);
		column = addColumn(datagrid, "source.name", "任务来源", ControlTypes.TEXT_BOX, 100, false);
		column = addColumn(datagrid, "lastContent", "最后跟进内容", ControlTypes.TEXT_BOX, 300, false);
		column = addColumn(datagrid, "lastFollowTime", "最后跟进时间", ControlTypes.DATETIME_BOX, 130, false);
		column = addColumn(datagrid, "lastFoolowUser.name", "原业务员", ControlTypes.TEXT_BOX, 100, false);
		column = addColumn(datagrid, "creator", "创建人", ControlTypes.TEXT_BOX, 100, false);
		column = addColumn(datagrid, "createTime", "创建时间", ControlTypes.DATETIME_BOX, 130, false);
		
		return datagrid;
	}
	
	//配置查询条件
	@Override
	protected PQueryProject createQueryProject(ResourceNode node) {
		PQueryProject queryProject = super.createQueryProject(node);
		queryProject.toNew();
		PQueryItem item = null;
		item = addQueryItem(queryProject, "keyword", "关键字", ControlTypes.TEXT_BOX);{
			item.setTooltip("输入任务ID、客户ID、任务名称、客户名称、联系方式等");
			item.setWidth(350);
		}
		addQueryItem(queryProject, "createTime", "创建时间", ControlTypes.DATE_BOX);
		return queryProject;
	}
	
	public void doOperation() {
		ResourceNode node = resourceService.byCode(resourceNodeCode);
		operationService.addOperation(node, OperationTypes.view);
	}
}
