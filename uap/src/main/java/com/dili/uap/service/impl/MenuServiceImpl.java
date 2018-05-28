package com.dili.uap.service.impl;

import com.dili.ss.base.BaseServiceImpl;
import com.dili.ss.dto.DTOUtils;
import com.dili.uap.dao.MenuMapper;
import com.dili.uap.domain.Menu;
import com.dili.uap.domain.dto.MenuCondition;
import com.dili.uap.service.MenuService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 由MyBatis Generator工具自动生成
 * This file was generated on 2018-05-21 16:08:04.
 */
@Service
public class MenuServiceImpl extends BaseServiceImpl<Menu, Long> implements MenuService {

    @Autowired
    private MenuMapper menuMapper;

    public MenuMapper getActualDao() {
        return (MenuMapper)getDao();
    }

    @Override
    public List<Menu> listDirAndLinksByUserId(String userId){
        if (StringUtils.isBlank(userId)) {
            throw new RuntimeException("用户id为空");
        }
        return this.menuMapper.listDirAndLinksByUserId(Long.valueOf(userId));
    }

    @Override
    public List<Menu> getParentMenus(String id) {
        String parentIds = getActualDao().getParentMenus(id);
        if(StringUtils.isBlank(parentIds)){
            return null;
        }
        String[] parentIdArr = parentIds.split(",");
        MenuCondition menuCondition = DTOUtils.newDTO(MenuCondition.class);
        //递归查出来的父id需要反转
        List ids = Arrays.asList(parentIdArr);
        Collections.reverse(ids);
        menuCondition.setIds(ids);
        //然而in查询无法按in的顺序获得结果，还是要根据ids的顺序重排
        List<Menu> menus = listByExample(menuCondition);
        List<Menu> sortedMenus = new ArrayList<>(menus.size());
        for(int i=0; i<ids.size(); i++){
            for(Menu menu : menus){
                if(ids.get(i).equals(menu.getId().toString())){
                    sortedMenus.add(menu);
                    break;
                }
            }
        }
        return sortedMenus;
    }

    @Override
    public List<Menu> getParentMenusByUrl(String url){
        Menu menu = DTOUtils.newDTO(Menu.class);
        menu.setUrl(url);
        List<Menu> menus = getActualDao().select(menu);
        if(menus == null || menus.isEmpty()){
            return null;
        }
        return getParentMenus(menus.get(0).getId().toString());
    }

    @Override
    public Map<String, Object> getMenuDetailByUrl(String url) {
        return getActualDao().getMenuDetailByUrl(url);
    }

    @Override
    public List<Menu> listSystemMenu(){
        return getActualDao().listSystemMenu();
    }
}