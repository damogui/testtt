package com.gongsibao.entity.supplier;

import org.netsharp.core.annotations.*;
import org.netsharp.entity.Entity;
import org.netsharp.organization.entity.Employee;
import org.netsharp.organization.entity.RoleEmployee;

import java.util.List;

@Table(name = "sp_salesman", header = "服务商业务员")
public class Salesman extends Entity {

	/**
	 * @Fields serialVersionUID : TODO(用一句话描述这个变量表示什么)
	 */
	private static final long serialVersionUID = 2235508906235938620L;

	@Column(name = "employee_id", header = "员工Id")
	private Integer employeeId = 0;

	@Reference(foreignKey = "employeeId", header = "员工")
	private Employee employee;

	@Column(name = "department_id", header = "部门Id")
	private Integer departmentId = 0;

	@Reference(foreignKey = "departmentId", header = "部门")
	private SupplierDepartment department;

	@Column(name = "supplier_id", header = "服务商Id")
	private Integer supplierId = 0;

	@Reference(foreignKey = "supplierId", header = "服务商")
	private Supplier supplier;

	@Column(name = "disabled", header = "停用")
	private Boolean disabled = false;

    @Exclusive
    @Subs(subType=RoleSalesman.class,foreignKey="salesmanId",header="角色用户")
    private List<RoleSalesman> roles;

	// 配置

	public Integer getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(Integer employeeId) {
		this.employeeId = employeeId;
	}

	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	public Integer getDepartmentId() {
		return departmentId;
	}

	public void setDepartmentId(Integer departmentId) {
		this.departmentId = departmentId;
	}

	public SupplierDepartment getDepartment() {
		return department;
	}

	public void setDepartment(SupplierDepartment department) {
		this.department = department;
	}

	public Integer getSupplierId() {
		return supplierId;
	}

	public void setSupplierId(Integer supplierId) {
		this.supplierId = supplierId;
	}

	public Supplier getSupplier() {
		return supplier;
	}

	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
	}

	public Boolean getDisabled() {
		return disabled;
	}

	public void setDisabled(Boolean disabled) {
		this.disabled = disabled;
	}
}
