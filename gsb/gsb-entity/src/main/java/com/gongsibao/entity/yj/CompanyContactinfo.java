package com.gongsibao.entity.yj;

import org.netsharp.core.annotations.Column;
import org.netsharp.core.annotations.Table;

import com.gongsibao.entity.BaseEntity;

@Table(name="yj_company_contactinfo")
public class CompanyContactinfo extends BaseEntity {
    /**   
	 * @Fields serialVersionUID : TODO(用一句话描述这个变量表示什么)   
	 */   
	private static final long serialVersionUID = 4770702834501532819L;
	@Column(name="yj_company_id",header="YjCompanyId")
    private Integer yjCompanyId;
    @Column(name="phone_number",header="PhoneNumber")
    private String phoneNumber;
    @Column(header="email")
    private String email;

    public Integer getYjCompanyId() {
        return yjCompanyId;
    }
    public void setYjCompanyId(Integer yjCompanyId) {
        this.yjCompanyId = yjCompanyId;
    }
    public String getPhoneNumber() {
        return phoneNumber;
    }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
}