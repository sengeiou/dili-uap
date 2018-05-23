package com.dili.uap.dao;

import com.dili.ss.base.MyMapper;
import com.dili.uap.domain.DataAuth;
import com.dili.uap.domain.Department;

import java.util.List;

public interface DepartmentMapper extends MyMapper<Department> {

    /**
     * 根据用户id查询所有部门
     * @param userId
     * @return
     */
    List<Department> findByUserId(Long userId);

    /**
     * 根据用户id查询所有部门数据权限
     * @param userId
     * @return
     */
    List<DataAuth> findDataAuthByUserId(Long userId);
}