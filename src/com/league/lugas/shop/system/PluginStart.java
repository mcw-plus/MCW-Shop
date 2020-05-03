package com.league.lugas.shop.system;

import java.io.File;

import com.league.lugas.shop.Main;

public class PluginStart {
	
	private Main plugin = Main.getPlugin(Main.class);
	private File configFile = new File(plugin.getDataFolder(), "config.yml");
	private File shopFolder = new File(plugin.getDataFolder() + "/shop/");
	
	private String pluginName = plugin.getDescription().getName();
	
	public PluginStart() {
		
		if(!configFile.exists()) {
			plugin.getServer().getConsoleSender().sendMessage("[" + pluginName + "] 플러그인이 설정파일을 생성중입니다");
			plugin.saveResource("config.yml", false);
			plugin.getServer().getConsoleSender().sendMessage("[" + pluginName + "] 플러그인이 설정파일을 생성하였습니다");
		}
		if (!shopFolder.exists()) {
			shopFolder.mkdir();
		}
		
	}
	
}
