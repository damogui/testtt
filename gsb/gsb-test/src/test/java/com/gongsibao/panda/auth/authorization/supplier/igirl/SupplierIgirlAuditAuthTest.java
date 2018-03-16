package com.gongsibao.panda.auth.authorization.supplier.igirl;

import org.junit.Before;

import com.gongsibao.panda.auth.authorization.AuthBaseTest;

public class SupplierIgirlAuditAuthTest extends AuthBaseTest{
	@Before
	public void setup() {

		roleCode = "IGIRL_Audit";
		super.setup();
	}
	
	protected void getResourceCodeList() {

		this.resourceNodeCodeList.add("IGIRL_Audit");
	}
}
