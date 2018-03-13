package com.gongsibao.panda.platform.basic;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.gongsibao.panda.platform.basic.reference.SetOfBooksReferenceTest;
import com.gongsibao.panda.platform.basic.reference.U8BankReferenceTest;
import com.gongsibao.panda.platform.basic.workspace.config.DictWorkspaceTest;
import com.gongsibao.panda.platform.basic.workspace.gardian.DeviceWorkspaceTest;
import com.gongsibao.panda.platform.basic.workspace.gardian.ProvidesWorkspaceTest;
import com.gongsibao.panda.platform.basic.workspace.u8.ManualVoucherOrderDTOWorkspaceTest;
import com.gongsibao.panda.platform.basic.workspace.u8.PayReceiptCheckDTOWorkspaceTest;
import com.gongsibao.panda.platform.basic.workspace.u8.SetOfBooksWorkspaceTest;
import com.gongsibao.panda.platform.basic.workspace.u8.U8BankWorkspaceTest;
import com.gongsibao.panda.platform.basic.workspace.u8.VoucherLogWorkspaceTest;


@RunWith(Suite.class)
@SuiteClasses({

	ResourceTest.class, 

	//参照
	SetOfBooksReferenceTest.class,
	U8BankReferenceTest.class,

	DictWorkspaceTest.class,
	
	//U8配置
	SetOfBooksWorkspaceTest.class,
	U8BankWorkspaceTest.class,
	VoucherLogWorkspaceTest.class,
	
	//回单核对
	PayReceiptCheckDTOWorkspaceTest.class,
	//手动凭证订单列表
	ManualVoucherOrderDTOWorkspaceTest.class,
	
	//收款审核
	//ReceivablesAuditDTOWorkspaceTest.class,	
	
	DeviceWorkspaceTest.class,
	ProvidesWorkspaceTest.class,
	NavigationTest.class
	
})

public class AllTest {

}