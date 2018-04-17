package com.gongsibao.account.service;

import java.sql.Types;

import org.netsharp.communication.Service;
import org.netsharp.core.Oql;
import org.netsharp.service.PersistableService;

import com.gongsibao.account.base.IAccountService;
import com.gongsibao.entity.acount.Account;
import org.netsharp.util.sqlbuilder.UpdateBuilder;

@Service
public class AccountService extends PersistableService<Account> implements IAccountService {

	public AccountService() {
		super();
		this.type = Account.class;
	}

	@Override
	public Boolean hasMobile(String mobile) {

		Oql oql = new Oql();
		{
			oql.setType(type);
			oql.setFilter("mobilePhone=?");
			oql.getParameters().add("@mobile", mobile, Types.VARCHAR);
		}
		return this.queryCount(oql) > 0;
	}

	@Override
	public Account byMobile(String mobile) {

		Oql oql = new Oql();
		{
			oql.setType(type);
			oql.setSelects("Account.*,company.{id,companyName}");
			oql.setFilter("mobilePhone=?");
			oql.getParameters().add("@mobile", mobile, Types.VARCHAR);
		}
		return this.queryFirst(oql);
	}

	@Override
	public Integer updateTicket(Integer accountPkid, String ticket) {
		UpdateBuilder updateBuilder = UpdateBuilder.getInstance ();
		{
			updateBuilder.update ("uc_account");
			updateBuilder.set ("ticket", ticket);
			updateBuilder.where ("pkid=" + accountPkid);
		}
		String cmdText = updateBuilder.toSQL ();
		return this.pm.executeNonQuery (cmdText, null) ;
	}
}