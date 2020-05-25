package com.gmail.gabezter.Main;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class LoginLinker implements Listener {

	private boolean justLoggedIn = false;

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onPlayerJoin(PlayerJoinEvent event) {
		try {
			Player player = event.getPlayer();
			if (!(Main.invite.equals("") || Main.invite.equals(" ") || Main.invite.equalsIgnoreCase("NULL"))) {
				justLoggedIn = true;
				player.sendMessage(Commands.id + "Make sure to join our Discord at https://discord.gg/" + Main.invite);
			}
			if (!Players.findPlayer(player.getUniqueId())) {
				TextComponent linkTxt = new TextComponent(
						Commands.id + "Make sure to link your Discord to your Minecraft account! using ");
				TextComponent cmd = new TextComponent("/dc register <Discord Username>");
				cmd.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/dc register"));
				cmd.setUnderlined(true);
				cmd.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
						new ComponentBuilder("Join the Discord!").create()));
				linkTxt.addExtra(cmd);
				player.spigot().sendMessage(linkTxt);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onPlayerMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		if (justLoggedIn == true && Main.forceLink == true && !(Players.findPlayer(player.getUniqueId()))) {
			if (player.hasPermission("Discordlink.bypass")) {
				event.setCancelled(true);
				player.sendMessage(Commands.id
						+ "This server requires you to join the Discord server:  https://discord.gg/" + Main.invite);
			} else {
				justLoggedIn = false;
				event.setCancelled(false);
			}
		} else {
			justLoggedIn = false;
			event.setCancelled(false);
		}
	}

}
