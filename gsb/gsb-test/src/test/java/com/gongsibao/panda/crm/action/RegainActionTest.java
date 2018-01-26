package com.gongsibao.panda.crm.action;

import org.junit.Before;
import org.junit.Test;
import org.netsharp.plugin.bean.BeanPath;

import com.gongsibao.crm.service.action.regain.ActionRegainSave;
import com.gongsibao.crm.service.action.regain.ActionRegainSaveLog;
import com.gongsibao.crm.service.action.regain.ActionRegainSendMessage;
import com.gongsibao.crm.service.action.regain.ActionRegainVerify;
import com.gongsibao.crm.service.action.regain.ActionRegainWriteBack;

public class RegainActionTest extends BaseActionTest{

	@Before
	public void setup() {

		resourceNodeCode = "GSB_CRM_MY_CUSTOMER";
		super.setup();
	}

	@Test
	public void save() {

		String pathName = "gsb/crm/task/regain";
		BeanPath beanPath = new BeanPath();
		{
			beanPath.toNew();
			beanPath.setPath(pathName);
			beanPath.setResourceNode(resourceNode);
			beanPath.setName("任务收回");
		}

		createBean(beanPath, "验证", ActionRegainVerify.class.getName(), resourceNode, 100);
		createBean(beanPath, "保存", ActionRegainSave.class.getName(), resourceNode, 200);
		createBean(beanPath, "回写", ActionRegainWriteBack.class.getName(), resourceNode, 300);
		createBean(beanPath, "日志", ActionRegainSaveLog.class.getName(), resourceNode, 400);
		createBean(beanPath, "通知", ActionRegainSendMessage.class.getName(), resourceNode, 500);
		beanPathService.save(beanPath);
	}
}