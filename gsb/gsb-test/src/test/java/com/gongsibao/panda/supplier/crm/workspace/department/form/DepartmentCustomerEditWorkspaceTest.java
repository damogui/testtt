package com.gongsibao.panda.supplier.crm.workspace.department.form;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.netsharp.core.MtableManager;
import org.netsharp.util.StringManager;

import com.gongsibao.crm.web.NCustomerFormPart;
import com.gongsibao.entity.crm.NCustomer;
import com.gongsibao.panda.platform.operation.workspace.crm.form.CustomerEditWorkspaceTest;

public class DepartmentCustomerEditWorkspaceTest  extends CustomerEditWorkspaceTest{
	@Before
	public void setup() {
		
		super.setup();
		entity = NCustomer.class;
		urlForm = "/crm/department/customer/edit";
		listPartName = formPartName = "客户信息";
		meta = MtableManager.getMtable(entity);
		formPartName = listPartName = meta.getName();
		resourceNodeCode = "CRM_DEPARTMENT_CUSTOMER_Edit";
		
		List<String> ss = new ArrayList<String>();
		ss.add("/gsb/supplier/crm/base/js/customer-base-form.part.js");
		ss.add("/gsb/supplier/crm/department/js/customer-edit-form.part.js");
		ss.add("/gsb/panda-extend/gsb.customer.controls.js");
		formJsImport = StringManager.join("|", ss);

		
		formJsController = "com.gongsibao.crm.web.NCustomerDepartmentEditFormPart";
		formServiceController = NCustomerFormPart.class.getName();
		
		taskDetailResourceNodeCode = "CRM_DEPARTMENT_TASK_ALL";
		
		productsDetailResourceNodeCode = "CRM_DEPARTMENT_Products";
		foolowDetailResourceNodeCode = "CRM_DEPARTMENT_Foolow";
		notifyDetailResourceNodeCode = "CRM_DEPARTMENT_Notify";
		changeDetailResourceNodeCode = "CRM_DEPARTMENT_Change";
		companysResourceNodeCode = "CRM_DEPARTMENT_Companys";
		
		taskDetailJsController = "com.gongsibao.crm.web.DepartmentTaskDetailPart";
	}
	
	@Test
	public void createFormToolbar() {
		
	}
}