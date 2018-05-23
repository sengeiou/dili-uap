package com.dili.uap.dao;

import com.dili.ss.base.MyMapper;
import com.dili.uap.domain.Menu;

import java.util.List;

public interface MenuMapper extends MyMapper<Menu> {

    /**
     * 根据用户id查询用户拥有的所有菜单权限
     * @param userId
     * @return
     */
    List<Menu> findByUserId(Long userId);

    /**
     * 根据用户id查询目录和链接菜单权限
     * @param userId
     * @return
     */
    List<Menu> findDirAndLinksByUserId(Long userId);

    /**
     * 获取上级菜单的id列表，以逗号分隔
     * @param id
     * @return
     */
    String getParentMenus(String id);
}