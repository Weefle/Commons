package com.caved_in.commons.menus.configmenu.items;

import com.caved_in.commons.inventory.menu.MenuItem;
import com.caved_in.commons.menus.configmenu.ConfigMenu;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

public class SwitchConfigMenuItem extends MenuItem {
	private static SwitchConfigMenuItem instance = null;

	protected SwitchConfigMenuItem() {
		super("&dSwitch to Config Menu");
		setDescriptions("&eClick to return to the main configuration menus");
	}

	public static SwitchConfigMenuItem getInstance() {
		if (instance == null) {
			instance = new SwitchConfigMenuItem();
		}
		return instance;
	}

	@Override
	public void onClick(Player player, ClickType type) {
		getMenu().switchMenu(player, ConfigMenu.getInstance());
	}
}
