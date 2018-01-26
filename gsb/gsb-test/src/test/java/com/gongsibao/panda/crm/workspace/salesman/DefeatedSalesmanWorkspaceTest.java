package com.gongsibao.panda.crm.workspace.salesman;

import org.junit.Before;
import org.netsharp.panda.plugin.entity.PToolbar;

public class DefeatedSalesmanWorkspaceTest extends AllSalesmanTaskWorkspaceTest{

	@Override
	@Before
	public void setup() {
		super.setup();
		urlList = "/crm/salesman/task/defeated/list";
		resourceNodeCode = "CRM_SALESMAN_TASK_DEFEATED";
		listFilter = "foolowStatus = 4 and ownerId = '{userId}'";
	}
	
	
	@Override
	public PToolbar createListToolbar() {
		
		return null;
	}
	
	@Override
	public PToolbar createRowToolbar() {
		
		return null;
	}
}
