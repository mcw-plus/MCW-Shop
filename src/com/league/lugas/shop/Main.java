package com.league.lugas.shop;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import com.league.lugas.shop.system.CommandExcuter;
import com.league.lugas.shop.system.ListenerExcuter;
import com.league.lugas.shop.system.PluginStart;


public class Main extends JavaPlugin {
	
	private String pluginName = getDescription().getName();
	
	@Override
	public void onEnable() {
		
		if (!isVaultExists()) {
            this.getLogger().severe("Vault 플러그인이 없어 플러그인을 비활성화 합니다");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
		
		new PluginStart();
		new CommandExcuter();
		new ListenerExcuter();
		Bukkit.getConsoleSender().sendMessage("§a[" + pluginName + "] 플러그인이 활성화 되었습니다");
		
	}
	
	@Override
	public void onDisable() {
		Bukkit.getConsoleSender().sendMessage("§c[" + pluginName + "] 플러그인이 비활성화 되었습니다");
	}
	
	private boolean isVaultExists() {
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
            return false;
        } else {
        	return true;
        }
    }

}
