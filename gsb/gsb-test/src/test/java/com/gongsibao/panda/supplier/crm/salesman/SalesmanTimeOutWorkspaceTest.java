package com.gongsibao.panda.supplier.crm.salesman;

import org.junit.Before;
import org.netsharp.panda.plugin.entity.PToolbar;

public class SalesmanTimeOutWorkspaceTest extends SalesmanAllTaskWorkspaceTest{

	@Override
	@Before
	public void setup() {
		
		super.setup();
		
		listPartName = "超时提醒";
		urlList = "/crm/salesman/task/timeout/list";
		resourceNodeCode = "CRM_SALESMAN_TIMEOUT";
		listFilter = "foolowStatus = 3 and ownerId = '{userId}' and NOW()>nextFoolowTime";
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