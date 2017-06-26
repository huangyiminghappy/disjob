package com.huangyiming.disjob.console.web.tool;
/*package com.huangyiming.disjob.console.web.tool;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.huangyiming.disjob.console.model.SysMenu;

*//**
 * <pre>
 * 
 *  File: WebFrontHelper.java
 * 
 *  Copyright (c) 2016, huangyiming.com All Rights Reserved.
 * 
 *  Description:
 *  web端菜单节点的排序，菜单树的构建
 * 
 *  Revision History
 *
 *  Date：		2016年5月19日
 *  Author：		Disjob
 *
 * </pre>
 *//*
public class WebFrontHelper {
	
	public static SysMenu buildMenuTree(List<SysMenu> menus) {
		
		Map<String, SysMenu> menuMap = new LinkedHashMap<String, SysMenu>();
		SysMenu rootMenu = new SysMenu();
		rootMenu.setMenuId("root");
		rootMenu.setMenuName("Root");
		menuMap.put("root", rootMenu);
		
		for (SysMenu menu : menus) {
			menuMap.put(menu.getMenuId(), menu);
		}
		
		for (SysMenu menu : menus) {
			String parentMenuId = menu.getParentMenuId();
			if (parentMenuId == null || "".equals(parentMenuId)) {
				parentMenuId = "root";
			} 
			
			menuMap.get(parentMenuId).addChild(menu);
		}

		return rootMenu;
	}

	public static EasyuiTreeNode buildTreeForEasyuiTree(List<SysMenu> menus, List<String> menuIdsForChecked) {
		
		Map<String, EasyuiTreeNode> map = new LinkedHashMap<String, EasyuiTreeNode>();
		EasyuiTreeNode root = new EasyuiTreeNode();
		root.setId("root");
		root.setText("Root");
		map.put("root", root);
		
		for (SysMenu menu : menus) {
			EasyuiTreeNode node = new EasyuiTreeNode();
			node.setId(menu.getMenuId());
			node.setText(menu.getMenuName());
//			node.setParentId(menu.getParentMenuId());
			node.setAttributes(menu);
			
			map.put(node.getId(), node);
		}
		
		for (Map.Entry<String, EasyuiTreeNode> entry : map.entrySet()) {
			EasyuiTreeNode node = entry.getValue();
			
			if ("root".equals(node.getId())) {
				continue;
			}
			
			String parentId = node.getParentId();
			
			if (parentId == null || "".equals(parentId)) {
				parentId = "root";
			}
			
			map.get(parentId).addChild(node);
		}
		
				
		if (menuIdsForChecked != null && menuIdsForChecked.size() > 0) {
			for (Map.Entry<String, EasyuiTreeNode> entry : map.entrySet()) {
				EasyuiTreeNode node = entry.getValue();
			
				if (menuIdsForChecked.contains(node.getId())) {
					node.setChecked(true);
				}
			}
		}

		return root;
	}
	
	public static EasyuiTreeNode buildTreeForEasyuiTree(List<SysMenu> menus) {
		return buildTreeForEasyuiTree(menus, null);
	}
	
}
*/