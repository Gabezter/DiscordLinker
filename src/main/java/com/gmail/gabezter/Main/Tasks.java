package com.gmail.gabezter.Main;

import org.bukkit.plugin.Plugin;

public class Tasks implements Runnable {

	private Plugin plugin;

	public Tasks(Plugin plugin) {
		this.plugin = plugin;
	}

	@Override
	public void run() {
		if(Main.updateMessage)
			plugin.getLogger().info("Updating Ranks");
		Updater.update(plugin);
		if(Main.updateMessage)
			plugin.getLogger().info("Update Complete");
	}

}
