package com.league.lugas.shop.events.shop;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.league.lugas.shop.Main;

public class ShopOpenEventListener implements Listener {
	
	public ShopOpenEventListener() {
		Main plugin = Main.getPlugin(Main.class);
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onShopOpen(ShopOpenEvent event) {
		if (!event.isCancelled()) {
			event.getPlayer().openInventory(event.getInventory());
		}
	}

}
