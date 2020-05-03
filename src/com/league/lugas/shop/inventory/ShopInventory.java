package com.league.lugas.shop.inventory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import com.league.lugas.shop.Main;
import com.league.lugas.shop.events.shop.ShopOpenEvent;
import com.league.lugas.shop.system.VaultEconomy;
import com.league.lugas.shop.utils.Util;

@SuppressWarnings("deprecation")
public class ShopInventory implements InventoryHolder {

	private Main plugin = Main.getPlugin(Main.class);
	private String prefix = plugin.getDescription().getName();
	
	private Inventory inventory;
	private Player player;
	private String shopName;
	
	public ShopInventory(Player player, String shopName) {
		this.player = player;
		this.shopName = shopName;
	}
	
	@Override
	public Inventory getInventory() {
		return inventory;
	}
	
	public void open() {
		ItemStack item;
		String shopIdToData, shopDisplayName;
		Integer itemBuy, itemSell;
		VaultEconomy vault = new VaultEconomy();
		
		File file = new File(plugin.getDataFolder() + "/shop/" + shopName + ".yml");
		YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
		
		if (!file.exists()) {
			player.sendMessage(Util.color("&f[&b" + prefix + "&f] &c존재하지 않은 상점입니다"));
			return;
		}
		
		shopIdToData = yml.get("shopId").toString().replaceAll("", "§");
		shopDisplayName = yml.getString("shopDisplayName");
		inventory = Bukkit.createInventory(null, 54, shopIdToData+"r§r§r§r§r"+shopDisplayName.replaceAll("&", "§"));
		
		for (int i = 0; i < 45; i++) {
			if (yml.get("shopItems."+i) != null) {
				item = (ItemStack) yml.get("shopItems."+i+".item");
				itemBuy = yml.getInt("shopItems."+i+".buy");
				itemSell = yml.getInt("shopItems."+i+".sell");
				addLore(item, "&f--------------------");
				if (itemBuy == -1)
					addLore(item, "&a구매: &c구매불가");
				else
					addLore(item, "&a구매: &c"+itemBuy+"&6원");
				
				if (itemSell == -1)
					addLore(item, "&b판매: &c판매불가");
				else
					addLore(item, "&b판매: &c"+itemSell+"&6원");
				
				addLore(item, "&f--------------------");
				inventory.setItem(i, item);
			}
		}
		
		ItemStack description = new ItemStack(Material.BOOK, 1);
		setItemTitle(description, "&6상점 설명");
		addLore(description, "&f--------------------");
		addLore(description, "&7좌클릭: &a구매");
		addLore(description, "&7우클릭: &b판매");
		addLore(description, "&f--------------------");
		inventory.setItem(45, description);
		
		ItemStack pagePre = new ItemStack(Material.PAPER, 0);
		setItemTitle(pagePre, "&6이전 페이지");
		inventory.setItem(46, pagePre);

		ItemStack itemMinus8 = new ItemStack(Material.RED_STAINED_GLASS_PANE, 8);
		setItemTitle(itemMinus8, "&7아이템 &c8개 감소");
		inventory.setItem(47, itemMinus8);

		ItemStack itemMinus1 = new ItemStack(Material.RED_STAINED_GLASS_PANE, 1);
		setItemTitle(itemMinus1, "&7아이템 &c1개 감소");
		inventory.setItem(48, itemMinus1);

		ItemStack playerHead = new ItemStack(Material.LEGACY_SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal());
		SkullMeta meta = (SkullMeta) playerHead.getItemMeta();
		meta.setOwner(player.getName());
		playerHead.setItemMeta(meta);
		setItemTitle(playerHead, "&6환영합니다, &c"+player.getName()+"&6님");
		addLore(playerHead, "&6잔고: &c"+vault.balance(player)+"&6골드");
		inventory.setItem(49, playerHead);
		
		ItemStack itemPlus1 = new ItemStack(Material.GREEN_STAINED_GLASS_PANE, 1);
		setItemTitle(itemPlus1, "&7아이템 &a1개 증가");
		inventory.setItem(50, itemPlus1);
		
		ItemStack itemPlus8 = new ItemStack(Material.GREEN_STAINED_GLASS_PANE, 8);
		setItemTitle(itemPlus8, "&7아이템 &a8개 증가");
		inventory.setItem(51, itemPlus8);
		
		if (yml.getInt("shopPage") > 1) {
			ItemStack pageNext = new ItemStack(Material.PAPER, 2);
			setItemTitle(pageNext, "&6다음 페이지");
			inventory.setItem(52, pageNext);
		}
		
		ItemStack exitButton = new ItemStack(Material.BARRIER, 1);
		setItemTitle(exitButton, "&c나가기");
		inventory.setItem(53, exitButton);
		
		ShopOpenEvent event = new ShopOpenEvent(player, inventory, Integer.parseInt(shopIdToData.replaceAll("§", "")));
		Bukkit.getPluginManager().callEvent(event);
	}
	
	private void setItemTitle(ItemStack item, String title) {
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(title.replaceAll("&", "§"));
		item.setItemMeta(meta);
	}
	
	private void addLore(ItemStack item, String lore) {
		ItemMeta meta = item.getItemMeta();
		List<String> itemLores;
		if (item.getItemMeta().hasLore()) {
			itemLores = item.getItemMeta().getLore();
		} else {
			itemLores = new ArrayList<String>();
		}
		itemLores.add(lore.replaceAll("&", "§"));
		meta.setLore(itemLores);
		item.setItemMeta(meta);
	}

}
