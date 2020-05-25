package com.gmail.gabezter.Main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.Plugin;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;

public class Updater {

	public Updater() {

	}

	public static boolean update(Plugin plugin) {
		boolean fromMC = Main.updateFrom;
		if (fromMC) {
			return updateFrom(plugin);
		} else {
			return updateTo(plugin);
		}
	}

	private static boolean updateFrom(Plugin plugin) {
		try {
			HashMap<UUID, Long> players = Players.players();
			ArrayList<String[]> ranks = new Ranks(plugin).listRanks();
			for (UUID uuid : players.keySet()) {
				Long dID = players.get(uuid);
				Guild guild = Main.bot.getGuildById(Main.id);
				OfflinePlayer player = plugin.getServer().getOfflinePlayer(uuid);
				String group = Main.perms.getPrimaryGroup("world", player);
				for (String[] rank : ranks) {
					if (rank[0].equalsIgnoreCase(group)) {
						List<Role> role = guild.getRolesByName(rank[1], true);
						if (role.size() == 1) {
							guild.addRoleToMember(dID + "", role.get(0)).queue();
							return true;
						} else {
							plugin.getLogger().warning("Multiple ranks found.. Please fix. -> Rank:" + rank[1]);
							return false;
						}
					}
				}
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	private static boolean updateTo(Plugin plugin) {
		return false;
	}
}
