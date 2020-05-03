package com.league.lugas.shop.events.shop;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import com.league.lugas.shop.Main;
import com.league.lugas.shop.system.VaultEconomy;
import com.league.lugas.shop.utils.Util;

public class ShopClickEventListener implements Listener {
	
	private Main plugin = Main.getPlugin(Main.class);
	
	public ShopClickEventListener() {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}
	
	@EventHandler
	public void onShopOpen(InventoryClickEvent event) {
		Inventory inventory = event.getInventory();
		String inventoryName = event.getView().getTitle();
		
		if (inventoryName.contains("§r§r§r§r§r")) {
			event.setCancelled(true);

			if (event.getClickedInventory() == null) return;
			if (event.getClickedInventory().getType().equals(InventoryType.PLAYER)) return;
			
			File configFile = new File(plugin.getDataFolder(), "config.yml");
			YamlConfiguration configYml = YamlConfiguration.loadConfiguration(configFile);
			String shopId = inventoryName.substring(0, inventoryName.indexOf("§r§r§r§r§r")).replaceAll("§", "");
			String shopName = configYml.getString("shopList."+shopId+".shopName");
			Player player = (Player) event.getWhoClicked();
			Integer slot = event.getSlot();
			VaultEconomy vault =  new VaultEconomy();
			double money = vault.balance(player);
			File file = new File(plugin.getDataFolder() + "/shop/"+shopName+".yml");
			YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
			Integer shopPage = 1;
			
			if (inventory.getItem(52) != null) {
				shopPage = inventory.getItem(52).getAmount() - 1;
			} else if (inventory.getItem(46) != null) {
				shopPage = inventory.getItem(46).getAmount() + 1;
			}
			
			if (slot < 45) {
				for (int i = 0; i < 45; i++) {
					ItemStack item = inventory.getItem(i);
					if (item != null && item.getItemMeta().getLore() == null) {
						return;
					}
				}
				
				if (yml.get("shopItems."+((shopPage-1)*45+slot)) != null) {
					Integer itemAmount = event.getCurrentItem().getAmount();
					ItemStack item = (ItemStack) yml.get("shopItems."+((shopPage-1)*45+slot)+".item");
					if (event.getClick().equals(ClickType.LEFT)) {
						Integer itemBuy = yml.getInt("shopItems."+((shopPage-1)*45+slot)+".buy") * itemAmount;
						if (itemBuy > money) {
							player.sendMessage(Util.color("&f[&bTR&f] &c소지금이 부족합니다"));
							Integer index = slot;
							ItemStack originItem = inventory.getItem(index);
							ItemStack warningItem = new ItemStack(Material.RED_STAINED_GLASS_PANE);
							setItemTitle(warningItem, "&c소지금 부족");
							inventory.setItem(index, warningItem);
							new BukkitRunnable() {
					            @Override
					            public void run() {
					            	inventory.setItem(index, originItem);
					            }
					        }.runTaskLater(plugin, 10);
						} else if (player.getInventory().firstEmpty() == -1) {
							player.sendMessage(Util.color("&f[&bTR&f] &c인벤토리가 가득 차있습니다"));
							Integer index = slot;
							ItemStack originItem = inventory.getItem(index);
							ItemStack warningItem = new ItemStack(Material.RED_STAINED_GLASS_PANE);
							setItemTitle(warningItem, "&c인벤토리 부족");
							
							inventory.setItem(index, warningItem);
							new BukkitRunnable() {
					            @Override
					            public void run() {
					            	inventory.setItem(index, originItem);
					            }
					        }.runTaskLater(plugin, 10);
						} else if (yml.getInt("shopItems."+((shopPage-1)*45+slot)+".buy") == -1) {
							player.sendMessage(Util.color("&f[&bTR&f] &c구매가 불가능한 상품입니다"));
							Integer index = slot;
							ItemStack originItem = inventory.getItem(index);
							ItemStack warningItem = new ItemStack(Material.RED_STAINED_GLASS_PANE);
							setItemTitle(warningItem, "&c구매불가");
							
							inventory.setItem(index, warningItem);
							new BukkitRunnable() {
					            @Override
					            public void run() {
					            	inventory.setItem(index, originItem);
					            }
					        }.runTaskLater(plugin, 10);
						} else {
							vault.take(player, itemBuy);
							ItemMeta itemMeta = item.getItemMeta();
							List<String> lore = itemMeta.getLore();
							List<String> newLore = new ArrayList<>();
							Integer loreCount;
							if (lore == null) {
								loreCount = 0;
							} else {
								loreCount = lore.size();
							}
							for (int i = 0; i < (loreCount-4); i++) {
								newLore.add(lore.get(i));
							}
							itemMeta.setLore(newLore);
							item.setItemMeta(itemMeta);
							item.setAmount(itemAmount);
							player.getInventory().addItem(item);
							String itemName = item.getType().toString();
							if (item.getItemMeta().getDisplayName() != null) {
								itemName = item.getItemMeta().getDisplayName();
							}
							ItemStack playerHead = inventory.getItem(49);
							setLore(playerHead, 0, "&6잔고: &c"+vault.balance(player)+"&6골드");
							player.sendMessage(Util.color("&f[&bTR&f] &f"+itemName+" &6"+item.getAmount()+"&7개를 &6"+itemBuy+"&7원에 구매 하였습니다"));
						}
					} else if (event.getClick().equals(ClickType.RIGHT)) {
						Integer itemSell = yml.getInt("shopItems."+((shopPage-1)*45+slot)+".sell") * itemAmount;
						item.setAmount(itemAmount);
						String itemName = item.getType().toString();
						if (item.getItemMeta().getDisplayName() != null) {
							itemName = item.getItemMeta().getDisplayName();
						}
						if (yml.getInt("shopItems."+((shopPage-1)*45+slot)+".sell") == -1) {
							player.sendMessage(Util.color("&f[&bTR&f] &c판매가 불가능한 상품입니다"));
							Integer index = slot;
							ItemStack originItem = inventory.getItem(index);
							ItemStack warningItem = new ItemStack(Material.RED_STAINED_GLASS_PANE);
							setItemTitle(warningItem, "&c판매불가");
							
							inventory.setItem(index, warningItem);
							new BukkitRunnable() {
					            @Override
					            public void run() {
					            	inventory.setItem(index, originItem);
					            }
					        }.runTaskLater(plugin, 10);
						} else if (player.getInventory().containsAtLeast(item, itemAmount)) {
							player.getInventory().removeItem(item);
							vault.give(player, itemSell);
							ItemStack playerHead = inventory.getItem(49);
							setLore(playerHead, 0, "&6잔고: &c"+vault.balance(player)+"&6골드");
							player.sendMessage(Util.color("&f[&bTR&f] &f"+itemName+" &6"+itemAmount+"&7개를 &6"+itemSell+"&7원에 판매 하였습니다"));
						} else {
							player.sendMessage(Util.color("&f[&bTR&f] &c판매할 아이템이 부족합니다"));
							Integer index = slot;
							ItemStack originItem = inventory.getItem(index);
							ItemStack warningItem = new ItemStack(Material.RED_STAINED_GLASS_PANE);
							setItemTitle(warningItem, "&c아이템 부족");
							
							inventory.setItem(index, warningItem);
							new BukkitRunnable() {
					            @Override
					            public void run() {
					            	inventory.setItem(index, originItem);
					            }
					        }.runTaskLater(plugin, 10);
						}
					}
				}
			} else {
				if (slot == 53) {
					player.closeInventory();
					return;
				}
				for (int i = 0; i < 45; i++) {
					ItemStack item = inventory.getItem(i);
					if (item != null && item.getItemMeta().getLore() == null) {
						return;
					}
				}
				if (slot == 46) {
					if (inventory.getItem(slot) != null) {
						Integer prevPage = inventory.getItem(slot).getAmount();
						for (int i = 0; i < 45; i++) {
							ItemStack item = (ItemStack) yml.get("shopItems."+((prevPage-1)*45+i)+".item");
							if (item == null) {
								inventory.setItem(i, null);
							} else {
								Integer itemBuy = yml.getInt("shopItems."+((prevPage-1)*45+i)+".buy") * item.getAmount();
								Integer itemSell = yml.getInt("shopItems."+((prevPage-1)*45+i)+".sell") * item.getAmount();
								addLore(item, "&f--------------------");
								if (itemBuy == -1) {
									addLore(item, "&a구매: &c구매불가");
								} else {
									addLore(item, "&a구매: &c"+itemBuy+"&6원");
								}
								if (itemSell == -1) {
									addLore(item, "&b판매: &c판매불가");
								} else {
									addLore(item, "&b판매: &c"+itemSell+"&6원");
								}
								addLore(item, "&f--------------------");
								inventory.setItem(i, item);
							}
						}
						if (prevPage == 1) {
							inventory.setItem(46, null);
						} else {
							inventory.getItem(46).setAmount(prevPage - 1);
						}
						ItemStack pageNext = new ItemStack(Material.PAPER, prevPage+1);
						setItemTitle(pageNext, "&6다음 페이지");
						inventory.setItem(52, pageNext);
					}
				} else if (slot == 47) {
					for (int i = 0; i < 45; i++) {
						ItemStack item = inventory.getItem(i);
						if (item != null) {
							if (item.getAmount() < 9) {
								item.setAmount(1);
							} else {
								item.setAmount(item.getAmount() - 8);
							}
							Integer loreCount = item.getItemMeta().getLore().size();
							Integer itemBuy = yml.getInt("shopItems."+((shopPage-1)*45+i)+".buy") * item.getAmount();
							Integer itemSell = yml.getInt("shopItems."+((shopPage-1)*45+i)+".sell") * item.getAmount();
							if (yml.getInt("shopItems."+((shopPage-1)*45+i)+".buy") == -1) {
								setLore(item, loreCount-3, "&a구매: &c구매불가");
							} else {
								setLore(item, loreCount-3, "&a구매: &c"+itemBuy+"&6원");
							}
							if (yml.getInt("shopItems."+((shopPage-1)*45+i)+".sell") == -1) {
								setLore(item, loreCount-2, "&b판매: &c판매불가");
							} else {
								setLore(item, loreCount-2, "&b판매: &c"+itemSell+"&6원");
							}
						}
					}
				} else if (slot == 48) {
					for (int i = 0; i < 45; i++) {
						ItemStack item = inventory.getItem(i);
						if (item != null) {
							if (item.getAmount() == 1) {
								return;
							}
							item.setAmount(item.getAmount() - 1);
							Integer loreCount = item.getItemMeta().getLore().size();
							Integer itemBuy= yml.getInt("shopItems."+((shopPage-1)*45+i)+".buy") * item.getAmount();
							Integer itemSell = yml.getInt("shopItems."+((shopPage-1)*45+i)+".sell") * item.getAmount();
							if (yml.getInt("shopItems."+((shopPage-1)*45+i)+".buy") == -1) {
								setLore(item, loreCount-3, "&a구매: &c구매불가");
							} else {
								setLore(item, loreCount-3, "&a구매: &c"+itemBuy+"&6원");
							}
							if (yml.getInt("shopItems."+((shopPage-1)*45+i)+".sell") == -1) {
								setLore(item, loreCount-2, "&b판매: &c판매불가");
							} else {
								setLore(item, loreCount-2, "&b판매: &c"+itemSell+"&6원");
							}
						}
					}
				} else if (slot == 50) {
					for (int i = 0; i < 45; i++) {
						ItemStack item = inventory.getItem(i);
						if (item != null) {
							if (item.getAmount() == 64) {
								return;
							}
							item.setAmount(item.getAmount() + 1);
							Integer loreCount = item.getItemMeta().getLore().size();
							Integer itemBuy = yml.getInt("shopItems."+((shopPage-1)*45+i)+".buy") * item.getAmount();
							Integer itemSell = yml.getInt("shopItems."+((shopPage-1)*45+i)+".sell") * item.getAmount();
							if (yml.getInt("shopItems."+((shopPage-1)*45+i)+".buy") == -1) {
								setLore(item, loreCount-3, "&a구매: &c구매불가");
							} else {
								setLore(item, loreCount-3, "&a구매: &c"+itemBuy+"&6원");
							}
							if (yml.getInt("shopItems."+((shopPage-1)*45+i)+".sell") == -1) {
								setLore(item, loreCount-2, "&b판매: &c판매불가");
							} else {
								setLore(item, loreCount-2, "&b판매: &c"+itemSell+"&6원");
							}
						}
					}
				} else if (slot == 51) {
					for (int i = 0; i < 45; i++) {
						ItemStack item = inventory.getItem(i);
						if (item != null) {
							if (item.getAmount() > 56) {
								item.setAmount(64);
							} else {
								if (item.getAmount() % 8 == 0) {
									item.setAmount(item.getAmount()+8);
								} else {
									item.setAmount(item.getAmount()+7);
								}
							}
							Integer loreCount = item.getItemMeta().getLore().size();
							Integer itemBuy = yml.getInt("shopItems."+((shopPage-1)*45+i)+".buy") * item.getAmount();
							Integer itemSell = yml.getInt("shopItems."+((shopPage-1)*45+i)+".sell") * item.getAmount();
							if (yml.getInt("shopItems."+((shopPage-1)*45+i)+".buy") == -1) {
								setLore(item, loreCount-3, "&a구매: &c구매불가");
							} else {
								setLore(item, loreCount-3, "&a구매: &c"+itemBuy+"&6원");
							}
							if (yml.getInt("shopItems."+((shopPage-1)*45+i)+".sell") == -1) {
								setLore(item, loreCount-2, "&b판매: &c판매불가");
							} else {
								setLore(item, loreCount-2, "&b판매: &c"+itemSell+"&6원");
							}
						}
					}
				} else if (slot == 52) {
					if (inventory.getItem(slot) != null) {
						Integer nextPage = inventory.getItem(slot).getAmount();
						for (int i = 0; i < 45; i++) {
							ItemStack item = (ItemStack) yml.get("shopItems."+((nextPage-1)*45+i)+".item");
							if (item == null) {
								inventory.setItem(i, null);
							} else {
								Integer itemBuy = yml.getInt("shopItems."+((nextPage-1)*45+i)+".buy") * item.getAmount();
								Integer itemSell = yml.getInt("shopItems."+((nextPage-1)*45+i)+".sell") * item.getAmount();
								addLore(item, "&f--------------------");
								if (itemBuy == -1) {
									addLore(item, "&a구매: &c구매불가");
								} else {
									addLore(item, "&a구매: &c"+itemBuy+"&6원");
								}
								if (itemSell == -1) {
									addLore(item, "&b판매: &c판매불가");
								} else {
									addLore(item, "&b판매: &c"+itemSell+"&6원");
								}
								addLore(item, "&f--------------------");
								inventory.setItem(i, item);
							}
						}
						if (yml.getInt("shopPage") > nextPage) {
							inventory.getItem(52).setAmount(nextPage + 1);
						} else {
							inventory.setItem(52, null);
						}
						ItemStack pagePre = new ItemStack(Material.PAPER, nextPage-1);
						setItemTitle(pagePre, "&6이전 페이지");
						inventory.setItem(46, pagePre);
					}
				}
			}
		}
	}

	private void setItemTitle(ItemStack item, String title) {
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(title.replaceAll("&", "§"));
		item.setItemMeta(meta);
	}
	
	private void setLore(ItemStack item, Integer line, String lore) {
		ItemMeta meta = item.getItemMeta();
		List<String> itemLores;
		if (item.getItemMeta().hasLore()) {
			itemLores = item.getItemMeta().getLore();
		} else {
			itemLores = new ArrayList<String>();
		}
		itemLores.set(line, lore.replaceAll("&", "§"));
		meta.setLore(itemLores);
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
