package com.gongsibao.panda.igirl;
import com.gongsibao.panda.igirl.reference.NCLOneReferenceTest;
import com.gongsibao.panda.igirl.reference.NCLTwoReferenceTest;
import com.gongsibao.panda.igirl.workspace.*;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
@RunWith(Suite.class)
@SuiteClasses({ 
		ResourceTest.class,
		NCLOneReferenceTest.class,
		NCLTwoReferenceTest.class,
		NclOneWorkspaceTest.class,
		NclTwoWorkspaceTest.class,
		NclBatchWorkspaceTest.class,
		TradeMarkCaseAllWorkspaceTest.class,
		MyAllTradeMarkCaseWorkspaceTest.class,
		NavigationTest.class
		})
		
public class AllTest {

}