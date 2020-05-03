package com.league.lugas.shop.system;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import com.league.lugas.shop.Main;

import net.milkbowl.vault.economy.Economy;

public class VaultEconomy {

	private Main plugin = Main.getPlugin(Main.class);
	
	private Economy economy;
	
	public VaultEconomy() {
		RegisteredServiceProvider<Economy> rsp = plugin.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
        	plugin.getLogger().severe("Vault 연동 중 오류가 발생하여 플러그인을 비활성화 합니다");
            Bukkit.getPluginManager().disablePlugin(plugin);
        }
        economy = rsp.getProvider();
	}
	
	public double balance(Player player) {
		return economy.getBalance(player);
	}
	
	public void take(Player player, double money) {
		economy.withdrawPlayer(player, money);
	}
	
	public void give(Player player, double money) {
		economy.depositPlayer(player, money);
	}
	
}
