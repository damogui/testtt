package com.gongsibao.bd.service;

import com.gongsibao.entity.bd.Preferential;
import org.netsharp.communication.Service;
import org.netsharp.core.DataTable;
import org.netsharp.core.Oql;
import org.netsharp.core.Paging;
import org.netsharp.core.QueryParameters;
import org.netsharp.service.PersistableService;

import com.gongsibao.bd.base.IPreferentialCodeService;
import com.gongsibao.entity.bd.PreferentialCode;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

@Service
public class PreferentialCodeService extends PersistableService<PreferentialCode> implements IPreferentialCodeService {

    public PreferentialCodeService(){
        super();
        this.type=PreferentialCode.class;
    }

    @Override
    public int countActiveByStatus(Integer accountId, Integer status) {
        final int[] count = new int[1];
        StringBuilder sql = new StringBuilder();
        QueryParameters queryParameters = new QueryParameters();
        sql.append("SELECT ");
        sql.append("COUNT(1) as count ");
        sql.append("FROM bd_preferential_code,bd_preferential ");
        sql.append("WHERE bd_preferential_code.preferential_id = bd_preferential.pkid ");
        if (accountId > 0) {
            sql.append(" AND bd_preferential_code.account_id = ? ");
            queryParameters.add("accountId", accountId, Types.INTEGER);
        }
        if (status == 1) {
            sql.append(" AND bd_preferential_code.status = 2");
        } else if (status == 2) {
            sql.append(" AND bd_preferential_code.status = 1 AND bd_preferential.end_date >= NOW() ");
        } else if (status == 3) {
            sql.append(" AND bd_preferential_code.status = 1 AND bd_preferential.end_date < NOW() ");
        }
        sql.append(" AND bd_preferential_code.is_enabled = 1 AND bd_preferential.is_enabled = 1 ");
        DataTable rows = this.pm.executeTable(sql.toString(), queryParameters);
        rows.forEach(row -> {
            count[0] = row.getLong("count").intValue();
        });
        return count[0];
    }

    @Override
    public List<PreferentialCode> queryActiveList(Integer accountId, Integer status) {
        List<PreferentialCode> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder();
        QueryParameters queryParameters = new QueryParameters();
        sql.append("SELECT ");
        sql.append(" bd_preferential_code.*,bd_preferential.* ");
        sql.append("FROM bd_preferential_code,bd_preferential  ");
        sql.append("WHERE bd_preferential_code.preferential_id = bd_preferential.pkid ");
        if (accountId > 0) {
            sql.append(" AND bd_preferential_code.account_id = ? ");
            queryParameters.add("accountId", accountId, Types.INTEGER);
        }
        if (status == 1) {
            sql.append(" AND bd_preferential_code.status = 2 ");
        } else if (status == 2) {
            sql.append(" AND bd_preferential_code.status = 1 AND bd_preferential.end_date >= NOW() ");
        } else if (status == 3) {
            sql.append(" AND bd_preferential_code.status = 1 AND bd_preferential.end_date < NOW() ");
        }
        sql.append(" AND bd_preferential_code.is_enabled = 1 AND bd_preferential.is_enabled = 1 ");
        DataTable rows = this.pm.executeTable(sql.toString(), queryParameters);
        sql.append("ORDER BY bd_preferential_code.activate_time DESC ");
        System.out.println(sql.toString());
        DataTable table = this.pm.executeTable(sql.toString(), queryParameters);
        table.forEach(row -> {
            Preferential preferential = new Preferential();{
                preferential.setCategory(row.getInteger("category"));
                preferential.setDiscount(row.getDouble("discount"));
                preferential.setPreferentialAmount(row.getInteger("preferential_amount"));
                preferential.setIsFirstOrderUse(row.getInteger("is_first_order_use"));
                preferential.setIsOverlay(row.getInteger("is_overlay"));
                preferential.setIsOverlay(row.getInteger("is_overlay"));
                preferential.setStartDate(row.getDate("start_date"));
                preferential.setRemark(row.getString("remark"));
                preferential.setAmountLimit(row.getInteger("amount_limit"));
                preferential.setEndDate(row.getDate("end_date"));
                preferential.setUserDescription(row.getString("user_description"));
                preferential.setUsePlatform(row.getString("use_platform"));
            }
            PreferentialCode code = new PreferentialCode();{
                code.setId(row.getInteger("pkid"));
                code.setPreferentialId(row.getInteger("preferential_id"));
                code.setNo(row.getString("no"));
                code.setStatus(row.getInteger("status"));
                code.setAccountId(row.getInteger("account_id"));
                code.setOrderId(row.getInteger("order_id"));
                code.setActivateTime(row.getDate("activate_time"));
                code.setIsEnabled(1);
                code.setPreferential(preferential);
            }
            list.add(code);
        });
        return list;
    }

    @Override
    public PreferentialCode byNo(String no) {
        Oql oql = new Oql();
        oql.setType(this.type);
        oql.setSelects("*");
        oql.setFilter(" no=? ");
        oql.getParameters().add("@no",no,Types.VARCHAR);
        return this.pm.queryFirst(oql);
    }

    @Override
    public int updateActive(String no, Integer accountId) {
        String sql = "UPDATE bd_preferential_code SET account_id = ?, status = 1, activate_time = NOW() WHERE `no` = ? AND account_id = 0 ";
        QueryParameters queryParameters = new QueryParameters();
        queryParameters.add("accountId",accountId,Types.INTEGER);
        queryParameters.add("no",no,Types.VARCHAR);
        return this.pm.executeNonQuery(sql,queryParameters);
    }
}