package com.gongsibao.panda.franchisee.workspace.operation;

import org.junit.Before;
import org.junit.Test;
import org.netsharp.core.MtableManager;
import org.netsharp.meta.base.WorkspaceCreationBase;
import org.netsharp.organization.dic.OperationTypes;
import org.netsharp.panda.controls.ControlTypes;
import org.netsharp.panda.dic.DatagridAlign;
import org.netsharp.panda.dic.OpenMode;
import org.netsharp.panda.dic.PartType;
import org.netsharp.panda.entity.PDatagrid;
import org.netsharp.panda.entity.PDatagridColumn;
import org.netsharp.panda.entity.PQueryItem;
import org.netsharp.panda.entity.PQueryProject;
import org.netsharp.panda.plugin.entity.PToolbar;
import org.netsharp.resourcenode.entity.ResourceNode;

import com.gongsibao.entity.franchisee.FranchiseeReport;
import com.gongsibao.franchisee.web.FranchiseeYearReportController;

public class OperationYearReportWorkspaceTest  extends WorkspaceCreationBase{

	@Override
	@Before
	public void setup() {
		entity = FranchiseeReport.class;
		urlList = "/bd/operation/year/report";
		listPartName = formPartName = "年统计";
		meta = MtableManager.getMtable(entity);
		formPartName = listPartName = meta.getName();
		resourceNodeCode = "GSB_BD_OPERATION_Year_Report";
		listPartType = PartType.TREEGRID_PART.getId();
		formOpenMode = OpenMode.WINDOW;
		listFilter="type=1";
		// 扩展
		listPartServiceController = FranchiseeYearReportController.class.getName();
		listPartJsController = FranchiseeYearReportController.class.getName();
		listPartImportJs = "/gsb/bd/js/operation/year.report.part.js";
		listToolbarPath="/bd/crm/report/toolbar";
	}

	@Test
	public void createToolbar() {
		ResourceNode node = this.getResourceNode();
		PToolbar toolbar = new PToolbar();
		{
			toolbar.toNew();
			toolbar.setBasePath("panda/datagrid/edit");
			toolbar.setPath(listToolbarPath);
			toolbar.setName("运营年统计工具栏");
			toolbar.setResourceNode(node);
		}
		addToolbarItem(toolbar, "disabled", "生成年报", "fa-stop-circle-o",
				"operYearReports()", null, 5);
		toolbarService.save(toolbar);
	}

	@Override
	protected PDatagrid createDatagrid(ResourceNode node) {

		PDatagrid datagrid = super.createDatagrid(node);
		datagrid.setTreeField("organizationId");
		PDatagridColumn column = null;
		
		column = addColumn(datagrid, "organizationId", "部门", ControlTypes.TEXT_BOX,
				100, true);
		{
		}

		column = addColumn(datagrid, "year", "年份", ControlTypes.NUMBER_BOX,
				100, true);
		{
		}

		column = addColumn(datagrid, "totalCount", "总客户数",
				ControlTypes.TEXT_BOX, 90, true);
		{
		}
		column = addColumn(datagrid, "trackCount", "已跟进数",
				ControlTypes.TEXT_BOX, 90, true);
		{
		}
		column = addColumn(datagrid, "unTrackCount", "未跟进数",
				ControlTypes.TEXT_BOX, 90, true);
		{
			// column.setGroupName(groupName);
		}
		String groupName = "意向度";
		column = addColumn(datagrid, "intentionDegree1Count", "高",
				ControlTypes.NUMBER_BOX, 80);
		{
			column.setGroupName(groupName);
		}
		column = addColumn(datagrid, "intentionDegree2Count", "中",
				ControlTypes.NUMBER_BOX, 80);
		{
			column.setGroupName(groupName);
		}
		column = addColumn(datagrid, "intentionDegree3Count", "低",
				ControlTypes.NUMBER_BOX, 80);
		{
			column.setGroupName(groupName);
		}

		groupName = "进度";
		column = addColumn(datagrid, "trackProgress1Count", "未拜访",
				ControlTypes.NUMBER_BOX, 80);
		{
			column.setGroupName(groupName);
		}
		column = addColumn(datagrid, "trackProgress2Count", "电话拜访",
				ControlTypes.NUMBER_BOX, 100);
		{
			column.setGroupName(groupName);
		}
		column = addColumn(datagrid, "trackProgress3Count", "陌拜",
				ControlTypes.NUMBER_BOX, 80);
		{
			column.setAlign(DatagridAlign.CENTER);
			column.setGroupName(groupName);
		}
		column = addColumn(datagrid, "trackProgress4Count", "洽谈中",
				ControlTypes.NUMBER_BOX, 80);
		{
			column.setGroupName(groupName);
		}
		column = addColumn(datagrid, "trackProgress5Count", "已合作",
				ControlTypes.NUMBER_BOX, 100);
		{
			column.setGroupName(groupName);
		}
		column = addColumn(datagrid, "trackProgress6Count", "已中止",
				ControlTypes.NUMBER_BOX, 80);
		{
			column.setAlign(DatagridAlign.CENTER);
			column.setGroupName(groupName);
		}
		column = addColumn(datagrid, "trackProgress7Count", "已合作中止",
				ControlTypes.NUMBER_BOX, 80);
		{
			column.setGroupName(groupName);
		}

		groupName = "预计签单";
		column = addColumn(datagrid, "expectedSign1Count", "两天内",
				ControlTypes.NUMBER_BOX, 80);
		{
			column.setGroupName(groupName);
		}
		column = addColumn(datagrid, "expectedSign2Count", "一周内",
				ControlTypes.NUMBER_BOX, 80);
		{
			column.setGroupName(groupName);
		}
		column = addColumn(datagrid, "expectedSign3Count", "一月内",
				ControlTypes.NUMBER_BOX, 80);
		{
			column.setGroupName(groupName);
		}
		column = addColumn(datagrid, "expectedSign4Count", "三月内",
				ControlTypes.NUMBER_BOX, 100);
		{
			column.setGroupName(groupName);
		}
		column = addColumn(datagrid, "expectedSign5Count", "三月以上",
				ControlTypes.NUMBER_BOX, 100);
		{
			column.setAlign(DatagridAlign.CENTER);
			column.setGroupName(groupName);
		}
		column = addColumn(datagrid, "parentId", "parentId",
				ControlTypes.TEXT_BOX, 100);
		{
			column.setVisible(false);
			column.setSystem(true);
		}
		return datagrid;
	}

	
	  @Override 
	  protected PQueryProject createQueryProject(ResourceNode node) {
		  PQueryProject queryProject = super.createQueryProject(node);
		  queryProject.toNew(); 
		  PQueryItem item = addQueryItem(queryProject, "date", "日期",ControlTypes.YEAR_BOX);{
			  item.setInterzone(true);
			  item.setShortcut(true);
		  }
		  return queryProject; 
	  }
	 

	public void doOperation() {

		ResourceNode node = resourceService.byCode(resourceNodeCode);
		operationService.addOperation(node, OperationTypes.view);
	}
}
