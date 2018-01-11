package com.gongsibao.bd.service;

import java.sql.Types;
import java.util.List;

import org.netsharp.communication.Service;
import org.netsharp.core.Oql;
import org.netsharp.service.PersistableService;
import org.netsharp.util.StringManager;
import org.netsharp.util.sqlbuilder.UpdateBuilder;

import com.gongsibao.bd.base.IDictService;
import com.gongsibao.entity.bd.Dict;

@Service
public class DictService extends PersistableService<Dict> implements IDictService {

	public DictService() {
		super();
		this.type = Dict.class;
	}

	@Override
	public List<Dict> byType(Integer type) {

		Oql oql = new Oql();
		{
			oql.setType(this.type);
			oql.setSelects("*");
			oql.setFilter("type=? and enabled=1");
			oql.getParameters().add("type", type, Types.INTEGER);
		}
		return this.queryList(oql);
	}

	@Override
	public List<Dict> byParentId(Integer parentId) {

		Oql oql = new Oql();
		{
			oql.setType(this.type);
			oql.setSelects("*");
			oql.setFilter("parentId=? and enabled=1");
			oql.getParameters().add("parentId", parentId, Types.INTEGER);
		}
		return this.queryList(oql);
	}

	@Override
	public boolean delete(String ids) {
		String[] idsarr = ids.split("_");
		String isList = StringManager.join(",", idsarr);
		String sql = "delete from bd_dict where pkid in (" + isList + ")";
		return this.pm.executeNonQuery(sql, null) > 0;
	}

}