package com.gongsibao.panda.iduty.workspace.task;

import org.junit.Test;
import org.netsharp.communication.ServiceFactory;
import org.netsharp.organization.base.IOperationService;
import org.netsharp.organization.base.IOperationTypeService;
import org.netsharp.organization.dic.OperationTypes;
import org.netsharp.organization.entity.OperationType;
import org.netsharp.panda.base.IPWorkspaceService;
import org.netsharp.panda.entity.PWorkspace;
import org.netsharp.resourcenode.IResourceNodeService;
import org.netsharp.resourcenode.entity.ResourceNode;

public class MyTaskWorkspaceTest {
	
	private String url = "/iduty/task/my/list";
	private String name = "我的任务";
	private String code = "GSB_IDuty_Task_My_Task";

	@Test
	public void execute() {

		IResourceNodeService nodeService = ServiceFactory.create(IResourceNodeService.class);
		ResourceNode node = nodeService.byCode(this.code);

		IOperationTypeService operationTypeService = ServiceFactory.create(IOperationTypeService.class);
		OperationType opt = operationTypeService.byCode(OperationTypes.view);

		PWorkspace workspace = new PWorkspace();
		{
			workspace.setUrl(this.url);
			workspace.setName(this.name);
			workspace.setCode(this.code);
			workspace.setResourceNode(node);
			workspace.setRedirectUrl("http://t2.gongsibao.net/erpct2/index.html#/app/tms/myTask/");
			workspace.setOperationTypeId(opt.getId());
			workspace.toNew();
		}

		IPWorkspaceService workspaceService = ServiceFactory.create(IPWorkspaceService.class);
		workspaceService.save(workspace);
	}

	@Test
	public void operation() {

		IResourceNodeService resourceService = ServiceFactory.create(IResourceNodeService.class);
		ResourceNode node = resourceService.byCode(this.code);
		IOperationService service = ServiceFactory.create(IOperationService.class);
		service.addOperation(node, OperationTypes.view);
	}
}
