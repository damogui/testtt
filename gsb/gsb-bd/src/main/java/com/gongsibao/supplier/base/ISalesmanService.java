package com.gongsibao.supplier.base;

import org.netsharp.base.IPersistableService;

import com.gongsibao.entity.supplier.Salesman;

public interface ISalesmanService  extends IPersistableService<Salesman>{

	/**   
	 * @Title: getSupplierId   
	 * @Description: TODO(根据当前登录人获取服务商Id)   
	 * @param: @param employeeId
	 * @param: @return      
	 * @return: Integer      
	 * @throws   
	 */
	Integer getSupplierId(Integer employeeId);
	
	/**   
	 * @Title: getDepartmentId   
	 * @Description: TODO(根据当前登录人获取服务商下对应部门Id)   
	 * @param: @param employeeId
	 * @param: @return      
	 * @return: Integer      
	 * @throws   
	 */
	Integer getDepartmentId(Integer employeeId);
	
	/**   
	 * @Title: byEmployeeId   
	 * @Description: TODO(根据employeeId获取)   
	 * @param: @param employeeId
	 * @param: @return      
	 * @return: Salesman      
	 * @throws   
	 */
	Salesman byEmployeeId(Integer employeeId);
}
