package com.gongsibao.panda.supplier.order.workspace.salesman;

import com.gongsibao.controls.DictComboBox;
import com.gongsibao.entity.crm.CompanyIntention;
import com.gongsibao.entity.supplier.Supplier;
import com.gongsibao.entity.supplier.SupplierDepartment;
import com.gongsibao.entity.trade.SoOrder;
import com.gongsibao.tools.PToolbarHelper;
import com.gongsibao.trade.web.OrderProdItemDetailPart;
import com.gongsibao.trade.web.SalesmanAddOrderFormPart;
import com.gongsibao.trade.web.SoCreatOrderPerformanceFormPart;
import org.junit.Before;
import org.junit.Test;
import org.netsharp.core.EntityState;
import org.netsharp.core.MtableManager;
import org.netsharp.meta.base.WorkspaceCreationBase;
import org.netsharp.organization.dic.OperationTypes;
import org.netsharp.organization.entity.Employee;
import org.netsharp.organization.entity.OperationType;
import org.netsharp.panda.controls.ControlTypes;
import org.netsharp.panda.dic.DatagridAlign;
import org.netsharp.panda.dic.DockType;
import org.netsharp.panda.dic.PartType;
import org.netsharp.panda.entity.*;
import org.netsharp.panda.plugin.dic.ToolbarType;
import org.netsharp.panda.plugin.entity.PToolbar;
import org.netsharp.panda.plugin.entity.PToolbarItem;
import org.netsharp.resourcenode.entity.ResourceNode;
import org.netsharp.util.ReflectManager;
import org.netsharp.util.StringManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by win on 2018/3/5.
 */
/*创建订单业绩*/
public class SoCreatOrderPerformanceWorkspaceTest extends WorkspaceCreationBase {

    @Before
    public void setup() {
        super.setup ();
        entity = SoOrder.class;
        urlForm = "/crm/order/salesman/coperformance";
        listPartName = formPartName = "订单信息";
        meta = MtableManager.getMtable (entity);
        resourceNodeCode = "Gsb_Supplier_Order_Salesman_CoPerformance";
        listToolbarPath = "crm/order/coperformance/edit";
        List<String> ss = new ArrayList<String> ();
//        ss.add("/package/easyui/datagrid-cellediting.js");
        ss.add ("/gsb/trade/js/order_performance-form.part.js");
        ss.add ("/gsb/gsb.customer.controls.js");
        formJsImport = StringManager.join ("|", ss);
        formJsController = SoCreatOrderPerformanceFormPart.class.getName ();
        formServiceController = SoCreatOrderPerformanceFormPart.class.getName ();
    }

    @Test
    public void run() {

        createFormWorkspace ();
    }


    public PToolbar createListToolbar() {

        ResourceNode node = this.resourceService.byCode (resourceNodeCode);
        OperationType ot1 = operationTypeService.byCode (OperationTypes.add);
        PToolbar toolbar = new PToolbar ();
        {
            toolbar.toNew ();
            toolbar.setPath (listToolbarPath);
            toolbar.setName ("所有订单操作");
            toolbar.setResourceNode (node);
            toolbar.setToolbarType (ToolbarType.BASE);
        }



        PToolbarItem item = PToolbarHelper.getPToolbarItem (EntityState.New, "orderDetail", PToolbarHelper.iconExtr,
                "订单详情", ot1, 1, "{controller}.add();");
        toolbar.getItems ().add (item);

        return toolbar;
    }


    /*进行设置工具栏*/
    @Test
    public void saveListToolbar() {

        PToolbar toolbar = createListToolbar ();
        if (toolbar != null) {

            toolbarService.save (toolbar);
        }
    }
    // 默认的表单配置信息
    protected PForm createForm(ResourceNode node) {

        PForm form = super.createForm (node);
        form.setColumnCount (3);
        PFormField formField = null;

        String groupName = null;
        formField = addFormFieldRefrence (form, "supplier.name", "订单编号", null, Supplier.class.getSimpleName (), false, true);
        {
            formField.setReadonly (true);

        }
        addFormFieldRefrence (form, "department.name", "订单金额", null, SupplierDepartment.class.getSimpleName (), false, true);
        {
            formField.setReadonly (true);

        }
        addFormFieldRefrence (form, "owner.name", "已划分金额", null, Employee.class.getSimpleName (), false, true);
        {
            formField.setReadonly (true);

        }

        return form;
    }

    protected void addDetailGridPart(PWorkspace workspace) {

        // 业绩划分
        performancePart (workspace);
    }

    // 业绩划分
    public void performancePart(PWorkspace workspace) {

        ResourceNode node = this.resourceService.byCode ("Gsb_Supplier_Order_Salesman_performance");
        PDatagrid datagrid = new PDatagrid (node, "业绩划分");
        {
            datagrid.setShowCheckbox (true);
            datagrid.setSingleSelect (true);
            datagrid.setReadOnly (false);
            datagrid.setShowTitle (true);

            PDatagridColumn column = null;

            column = addColumn (datagrid, "productName", "服务商", ControlTypes.TEXT_BOX, 150);
            column = addColumn (datagrid, "quantity", "部门", ControlTypes.NUMBER_BOX, 60);
            {

                column.setAlign (DatagridAlign.CENTER);
            }

            column = addColumn (datagrid, "cityName", "业务员", ControlTypes.TEXT_BOX, 150);
            column = addColumn (datagrid, "serviceName", "订单业绩分配金额", ControlTypes.TEXT_BOX, 150);

        }

        PPart part = new PPart ();
        {
            part.toNew ();
            part.setName ("产品信息");
            part.setCode ("products");
            part.setParentCode (ReflectManager.getFieldName (meta.getCode ()));
            part.setRelationRole ("products");
            part.setResourceNode (node);
            part.setPartTypeId (PartType.DETAIL_PART.getId ());
            part.setDatagrid (datagrid);
            part.setDockStyle (DockType.DOCUMENTHOST);
            part.setToolbar ("panda/datagrid/detail");
            part.setServiceController (OrderProdItemDetailPart.class.getName ());
            part.setJsController (OrderProdItemDetailPart.class.getName ());
        }
        workspace.getParts ().add (part);

        part = workspace.getParts ().get (0);
        {
            part.setName ("新增订单");
            part.setDockStyle (DockType.TOP);
            part.setHeight (500);
        }
    }

    // 默认的表单操作
    @Override
    protected void doOperation() {

        ResourceNode node = this.getResourceNode ();
        operationService.addOperation (node, OperationTypes.view);
        operationService.addOperation (node, OperationTypes.add);
    }
}