package com.gongsibao.crm.service;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import org.netsharp.communication.Service;
import org.netsharp.core.BusinessException;
import org.netsharp.core.EntityState;
import org.netsharp.core.MtableManager;
import org.netsharp.core.Oql;
import org.netsharp.util.StringManager;
import org.netsharp.util.sqlbuilder.UpdateBuilder;

import com.gongsibao.bd.service.SupplierPersistableService;
import com.gongsibao.crm.base.INCustomerService;
import com.gongsibao.entity.crm.NCustomer;

@Service
public class NCustomerService extends SupplierPersistableService<NCustomer> implements INCustomerService {

	public NCustomerService() {
		super();
		this.type = NCustomer.class;
	}

	@Override
	public boolean updateIsMember(Integer customerId) {
		
		UpdateBuilder updateSql = UpdateBuilder.getInstance();
		{
			updateSql.update("n_crm_customer");
			updateSql.set("is_member", 1);
			updateSql.where("id=" + customerId);
		}
		String cmdText = updateSql.toSQL();
		
		//这里要生成Account bug
		return this.pm.executeNonQuery(cmdText, null)>0;
	}

	@Override
	public NCustomer validationContactWay(Integer id, String contactWay, String type) {

		Oql oql = new Oql();
		{
			oql.setType(this.type);
			oql.setSelects("*");
		}

		List<String> ss = new ArrayList<String>();
		ss.add(type + "=?");
		oql.getParameters().add("contactWay", contactWay, Types.VARCHAR);
		if (id != null) {
			ss.add("id<>?");
			oql.getParameters().add("id", id, Types.INTEGER);
		}
		String filter = StringManager.join(" AND ", ss);
		oql.setFilter(filter);

		return this.queryFirst(oql);
	}
	
	@Override
	public NCustomer bySwtCustomerId(String swtCustomerId) {
		
		String selectFields = getSelectFullFields();
		Oql oql = new Oql();
		{
			oql.setType(this.type);
			oql.setSelects(selectFields);
			oql.setFilter("swtCustomerId=?");
			oql.getParameters().add("swtCustomerId", swtCustomerId, Types.VARCHAR);
		}

		NCustomer entity = this.queryFirst(oql);
		return entity;
	}

	@Override
	public NCustomer byContactWay(String contactWay, String type) {

		String selectFields = getSelectFullFields();
		Oql oql = new Oql();
		{
			oql.setType(this.type);
			oql.setSelects(selectFields);
			oql.setFilter(type + "=?");
			oql.getParameters().add("contactWay", contactWay, Types.VARCHAR);
		}

		NCustomer entity = this.queryFirst(oql);
		return entity;
	}

	@Override
	public NCustomer bindSwtCustomerId(String swtCustomerId, int customerId) {

		UpdateBuilder updateBuilder = new UpdateBuilder();
		{
			updateBuilder.update(MtableManager.getMtable(this.type).getTableName());
			updateBuilder.set("swt_customer_id", swtCustomerId);
			updateBuilder.where("id =" + customerId);
		}
		this.pm.executeNonQuery(updateBuilder.toSQL(), null);
		NCustomer customer = byId(customerId);
		return customer;
	}
	

	@Override
	public NCustomer save(NCustomer entity) {
		
		EntityState state = entity.getEntityState();
		if (state == EntityState.Deleted) {
			
			throw new BusinessException("客户信息不允许删除！");
		}
		
		entity = super.save(entity);
		return entity;
	}

	private String getSelectFullFields() {

		StringBuilder builder = new StringBuilder();
		builder.append("NCustomer.*,");
		builder.append("NCustomer.tasks.*,");
		builder.append("NCustomer.products.*,");
		builder.append("NCustomer.products.product.{id,name},");
		builder.append("NCustomer.products.province.*,");
		builder.append("NCustomer.products.city.*,");
		builder.append("NCustomer.products.county.*,");
		builder.append("NCustomer.companys.*,");
		builder.append("NCustomer.companys.company.{id,companyName},");
		builder.append("NCustomer.follows.*,");
		return builder.toString();
	}
}