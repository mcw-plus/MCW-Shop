package com.league.lugas.shop.events.shop;

import java.io.File;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.Inventory;

import com.league.lugas.shop.Main;

public class ShopOpenEvent extends Event implements Cancellable {

	private Main plugin = Main.getPlugin(Main.class);
	private static final HandlerList handlers = new HandlerList();
	private boolean isCancelled;
	
	private Player player;
	private Inventory inventory;
	private Integer shopId;
	
	public ShopOpenEvent(Player player, Inventory inventory, Integer shopId) {
		this.player = player;
		this.inventory = inventory;
		this.shopId = shopId;
	}
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}

	@Override
	public boolean isCancelled() {
		return isCancelled;
	}

	@Override
	public void setCancelled(boolean isCancelled) {
		this.isCancelled = isCancelled;
	}
	
	public Player getPlayer() {
		return player;
	}

	public Inventory getInventory() {
		return inventory;
	}

	public Integer getShopId() {
		return shopId;
	}
	
	public String getName() {
		File file = new File(plugin.getDataFolder() + "config.yml");
		YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
		
		return yml.getString("shopList." + shopId + ".shopName");
	}

}
