package com.gongsibao.panda.report;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({

	ResourceTest.class, 

	//销售业绩：部门
//	DayWorkspaceTest.class,
//	MonthWorkspaceTest.class,
//	SeasonWorkspaceTest.class,
//	WeekWorkspaceTest.class,
//	YearWorkspaceTest.class,
	
	//销售业绩：业务员
	 com.gongsibao.panda.report.workspace.perfrmance.salesman.DayWorkspaceTest.class,
	 com.gongsibao.panda.report.workspace.perfrmance.salesman.MonthWorkspaceTest.class,
	 com.gongsibao.panda.report.workspace.perfrmance.salesman.SeasonWorkspaceTest.class,
	 com.gongsibao.panda.report.workspace.perfrmance.salesman.WeekWorkspaceTest.class,
	 com.gongsibao.panda.report.workspace.perfrmance.salesman.YearWorkspaceTest.class,
	
	NavigationTest.class
	
})

public class AllTest {

}
