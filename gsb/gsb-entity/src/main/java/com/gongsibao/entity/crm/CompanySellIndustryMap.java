package com.gongsibao.entity.crm;

import org.netsharp.core.annotations.Column;
import org.netsharp.core.annotations.Table;

import com.gongsibao.entity.BaseEntity;

/**   
 * @ClassName:  CompanySellIndustryMap   
 * @Description:TODO 数据很少
 * @author: 韩伟
 * @date:   2018年1月9日 上午11:12:24   
 *     
 * @Copyright: 2018 www.yikuaxiu.com Inc. All rights reserved. 
 */
@Table(name="crm_company_sell_industry_map",header="我要出售公司--行业特点关联表")
public class CompanySellIndustryMap extends BaseEntity {
    /**   
	 * @Fields serialVersionUID : TODO(用一句话描述这个变量表示什么)   
	 */   
	private static final long serialVersionUID = 8272541292887956686L;
	@Column(name="sell_id",header="")
    private Integer sellId;
    @Column(name="industry_id",header="")
    private Integer industryId;

    public Integer getSellId() {
        return sellId;
    }
    public void setSellId(Integer sellId) {
        this.sellId = sellId;
    }
    public Integer getIndustryId() {
        return industryId;
    }
    public void setIndustryId(Integer industryId) {
        this.industryId = industryId;
    }
}