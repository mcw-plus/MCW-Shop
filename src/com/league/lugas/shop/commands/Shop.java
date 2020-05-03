package com.league.lugas.shop.commands;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.league.lugas.shop.Main;
import com.league.lugas.shop.inventory.ShopInventory;
import com.league.lugas.shop.utils.Util;

public class Shop implements CommandExecutor {

	private Main plugin = Main.getPlugin(Main.class);
	private String prefix = plugin.getDescription().getName();
	
	private File configFile = new File(plugin.getDataFolder() + "/config.yml");
	private YamlConfiguration configYml = YamlConfiguration.loadConfiguration(configFile);
	
	public Shop() {
		plugin.getCommand("shop").setExecutor(this);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if(!(sender instanceof Player)) {
			sender.sendMessage(Util.color("&c[Server] 콘솔에서는 해당 명령어를 실행할 수 없습니다"));
			return true;
		}
		
		Player player = (Player) sender;
		if (!player.hasPermission("mcw.shop.admin")) {
			player.sendMessage(Util.color("&f[&b" + prefix + "&f] &c명령어 사용 권한이 없습니다"));
			return true;
		}
		
		if (args.length == 1) {
			if (args[0].equals("help")) {
				player.sendMessage(Util.color("&7----- &6" + prefix + " v"+plugin.getDescription().getVersion()+" &7-----"));
				player.sendMessage(Util.color("&f[&b" + prefix + "&f] &b/trshop create <name> <GUI^Name> &8- &7상점을 만듭니다. gui 이름에 띄어스기를 원할경우 ^로 대체하세요"));
				player.sendMessage(Util.color("&f[&b" + prefix + "&f] &b/trshop delete <name> &8- &7상점을 삭제합니다"));
				player.sendMessage(Util.color("&f[&b" + prefix + "&f] &b/trshop set <name> <page> <index> <buy> <sell> &8- &7상점에 들고있는 아이템을 추가합니다. /trshop set 광물상점 1 2 10 8 이는 광물상점 1페이지 2번째 칸에 손에 들고있는 물건을 유저가 개당 10원에 사고, 개당 8원에 팔게 됩니다. 가격이 -1 일경우 구매/판매가 불가하게 됩니다."));
				player.sendMessage(Util.color("&f[&b" + prefix + "&f] &b/trshop del <name> <page> <index> &8- &7상점에 page번째 페이지에 index칸에 있는 아이템을 삭제합니다"));
				player.sendMessage(Util.color("&f[&b" + prefix + "&f] &b/trshop open <name> &8- &7상점을 오픈합니다"));
				player.sendMessage(Util.color("&f[&b" + prefix + "&f] &b/trshop list &8- &7상점 리스트를 보여줍니다"));
				player.sendMessage(Util.color("&f[&b" + prefix + "&f] &b/trshop version &8- &7" + prefix + " 트리거의 버전을 확인합니다"));
			} else if (args[0].equals("list")) {
				String result = "";
				File[] files = new File(plugin.getDataFolder() + "/shop/").listFiles();
				if (files.length == 0) {
					player.sendMessage(Util.color("&f[&b" + prefix + "&f] &b등록된 상점이 없습니다"));
					return true;
				}
				
				result = files[0].getName().substring(0, files[0].getName().lastIndexOf("."));
				for (int i = 1; i < files.length; i++) {
					result += ", " + files[i].getName().substring(0, files[i].getName().lastIndexOf("."));
				}
				player.sendMessage(Util.color("&f[&b" + prefix + "&f] &b상점 리스트(&6" + files.length + "&b): &7" + result));
			} else if (args[0].equals("version")) {
				player.sendMessage(Util.color("&f[&bTR&f] &b현재 버전: &f"+plugin.getDescription().getVersion()));
			} else {
				player.sendMessage(Util.color("&f[&b" + prefix + "&f] &c/trshop help&6 로 도움말을 확인하세요"));
			}
		} else if (args.length == 2) {
			if (args[0].equals("delete")) {
				String shopName = args[1];
				
				File file = new File(plugin.getDataFolder() + "/shop/" + shopName + ".yml");
				YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
				if (!file.exists() ) {
					player.sendMessage(Util.color("&f[&bTR&f] &c존재하지 않은 상점입니다"));
					return true;
				}
				
				Integer shopId = yml.getInt("shopId");
				configYml.set("shopList."+shopId, null);
				try {
					configYml.save(configFile);
				} catch (IOException e) {
					e.printStackTrace();
				}
				file.delete();
				player.sendMessage(Util.color("&f[&b" + prefix + "&f] &6"+shopName+"&7(이)가 삭제되었습니다"));
			} else if (args[0].equals("open")) {
				ShopInventory inventory = new ShopInventory(player, args[1]);
				inventory.open();
			} else {
				player.sendMessage(Util.color("&f[&b" + prefix + "&f] &c/trshop help&6 로 도움말을 확인하세요"));
			}
		} else if (args.length == 3) {
			if (args[0].equals("create")) {
				String shopName = args[1];
				String shopDisplayName = args[2];
				
				if (!shopName.matches("^[a-zA-Z0-9]*$")) {
					player.sendMessage(Util.color("&f[&bTR&f] &c상점 이름은 영어와 숫자만 가능합니다"));
					return true;
				}
				
				File file = new File(plugin.getDataFolder() + "/shop/" + shopName + ".yml");
				YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
				Integer shopId = configYml.getInt("shopCount");
				yml.set("shopId", shopId);
				yml.set("shopPage", 1);
				yml.set("shopDisplayName", shopDisplayName);
				yml.set("shopItems", new ArrayList<>());
				try {
					yml.save(file);
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				configYml.set("shopCount", shopId + 1);
				configYml.set("shopList."+shopId+".shopName", shopName);
				try {
					configYml.save(configFile);
				} catch (IOException e) {
					e.printStackTrace();
				}
				player.sendMessage(Util.color("&f[&b" + prefix + "&f] &6"+shopName+"&7(이)가 생성되었습니다"));
			} else {
				player.sendMessage(Util.color("&f[&b" + prefix + "&f] &c/trshop help&6 로 도움말을 확인하세요"));
			}
		} else if (args.length == 4) {
			if (args[0].equals("del")) {
				String shopName = args[1];
				String shopPageString = args[2];
				String shopIndexString = args[3];
				Integer shopPage, shopIndex;
				
				File file = new File(plugin.getDataFolder() + "/shop/" + shopName + ".yml");
				YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
				if (!file.exists()) {
					player.sendMessage(Util.color("&f[&b" + prefix + "&f] &c존재하지 않은 상점입니다"));
					return true;
				}
				if (!isNumeric(shopPageString)) {
					player.sendMessage(Util.color("&f[&b" + prefix + "&f] &c페이지는 숫자만 가능합니다"));
					return true;
				} else {
					shopPage = Integer.parseInt(shopPageString);
				}
				if (!isNumeric(shopIndexString)) {
					player.sendMessage(Util.color("&f[&b" + prefix + "&f] &c슬롯은 숫자만 가능합니다"));
					return true;
				} else {
					shopIndex = Integer.parseInt(shopIndexString) - 1;
				}
				if (shopIndex < 0 || shopIndex > 44) {
					player.sendMessage(Util.color("&f[&b" + prefix + "&f] &c해당 칸은 설정이 불가능한 칸입니다"));
					return true;
				}
				if (yml.getInt("shopPage") < shopPage) {
					player.sendMessage(Util.color("&f[&b" + prefix + "&f] &c해당 페이지는 존재하지 않습니다"));
					return true;
				}
				
				shopIndex += ((shopPage-1)*45);
				
				if (yml.get("shopItems." + shopIndex) == null) {
					player.sendMessage(Util.color("&f[&b" + prefix + "&f] &c해당 칸에는 아이템이 존재하지 않습니다"));
					return true;
				}
				if (yml.getInt("shopPage") == shopPage) {
					Integer lastItems = 0;
					Integer startCount = (shopPage-1) * 45;
					for (int i = startCount; i < startCount+45; i++) {
						if (yml.get("shopItems." + i) != null) {
							lastItems++;
						}
					}
					if (lastItems==1) {
						yml.set("shopPage", shopPage-1);
					}
				}
				yml.set("shopItems."+shopIndex, null);
				try {
					yml.save(file);
				} catch (IOException e) {
					e.printStackTrace();
				}
				player.sendMessage(Util.color("&f[&b" + prefix + "&f] &7아이템이 삭제되었습니다"));
			} else {
				player.sendMessage(Util.color("&f[&b" + prefix + "&f] &c/trshop help&6 로 도움말을 확인하세요"));
			}
		} else if (args.length == 6) {
			if (args[0].equals("set")) {
				String shopName = args[1];
				String shopPageString = args[2];
				String shopIndexString = args[3];
				String itemBuyString = args[4];
				String itemSellString = args[5];
				@SuppressWarnings("deprecation")
				ItemStack item = player.getItemInHand();
				String itemName;
				Integer shopPage, shopIndex, itemBuy, itemSell;
				
				File file = new File(plugin.getDataFolder() + "/shop/" + shopName + ".yml");
				YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
				
				if (!file.exists()) {
					player.sendMessage(Util.color("&f[&b" + prefix + "&f] &c존재하지 않은 상점입니다"));
					return true;
				}
				if (!isNumeric(shopPageString)) {
					player.sendMessage(Util.color("&f[&b" + prefix + "&f] &c페이지는 숫자만 가능합니다"));
					return true;
				} else {
					shopPage = Integer.parseInt(shopPageString);
				}
				if (!isNumeric(shopIndexString)) {
					player.sendMessage(Util.color("&f[&b" + prefix + "&f] &c슬롯은 숫자만 가능합니다"));
					return true;
				} else {
					shopIndex = Integer.parseInt(shopIndexString) - 1;
				}
				if (shopIndex < 0 || shopIndex > 44) {
					player.sendMessage(Util.color("&f[&b" + prefix + "&f] &c해당 칸은 설정이 불가능한 칸입니다"));
					return true;
				}
				if (!isNumeric(itemBuyString) || !isNumeric(itemSellString)) {
					player.sendMessage(Util.color("&f[&b" + prefix + "&f] &c가격은 숫자만 가능합니다"));
					return true;
				} else {
					itemBuy = Integer.parseInt(itemBuyString);
					itemSell = Integer.parseInt(itemSellString);
				}
				if (itemBuy < -1 || itemSell < -1) {
					player.sendMessage(Util.color("&f[&b" + prefix + "&f] &c가격은 -1 이상만 가능합니다s"));
					return true;
				}
				if (item.getType().equals(Material.AIR)) {
					player.sendMessage(Util.color("&f[&b" + prefix + "&f] &c손에 아무것도 들고 있지 않습니다"));
					return true;
				}
				itemName = item.getType().toString();
				if (item.getItemMeta().getDisplayName() != null) {
					itemName = item.getItemMeta().getDisplayName();
				}
				if (shopPage > yml.getInt("shopPage")) {
					yml.set("shopPage", shopPage);
				}
				shopIndex += (shopPage-1) * 45;
				yml.set("shopItems."+shopIndex+".buy", itemBuy);
				yml.set("shopItems."+shopIndex+".sell",  itemSell);
				yml.set("shopItems."+shopIndex+".item", item);
				try {
					yml.save(file);
				} catch (IOException e) {
					e.printStackTrace();
				}
				player.sendMessage(Util.color("&f[&b" + prefix + "&f] &6" + itemName + "&7(을)를 추가했습니다"));
			} else {
				player.sendMessage(Util.color("&f[&b" + prefix + "&f] &c/trshop help&6 로 도움말을 확인하세요"));
			}
		} else {
			player.sendMessage(Util.color("&f[&b" + prefix + "&f] &c/trshop help&6 로 도움말을 확인하세요"));
		}
		
		return false;
	}
	
	private boolean isNumeric(String string) {   
		if(string.equals("")){
			return false;
		}
		return string.matches("-?\\d+(\\.\\d+)?");
	}

}
