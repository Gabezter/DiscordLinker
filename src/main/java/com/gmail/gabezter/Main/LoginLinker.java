package com.gmail.gabezter.Main;

import java.util.ArrayList;
import java.util.List;

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

	private List<Player> justIn = new ArrayList<>();

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		if (!Players.findPlayer(player.getUniqueId())) {
			if (!(Main.invite.equals("") || Main.invite.equals(" ") || Main.invite.equalsIgnoreCase("NULL"))) {
				justIn.add(player);
				player.sendMessage(Commands.id + "Make sure to join our Discord at https://discord.gg/" + Main.invite);
			}
			TextComponent linkTxt = new TextComponent(
					Commands.id + "Make sure to link your Discord to your Minecraft account! using ");
			TextComponent cmd = new TextComponent("/dc register <Discord Username>");
			cmd.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/dc register"));
			cmd.setUnderlined(true);
			cmd.setHoverEvent(
					new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Join the Discord!").create()));
			linkTxt.addExtra(cmd);
			player.spigot().sendMessage(linkTxt);
		}

	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onPlayerMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		if (justIn.contains(player) && Main.forceLink == true && !(Players.findPlayer(player.getUniqueId()))) {
			if (!player.hasPermission("Discordlink.bypass")) {
				event.setCancelled(true);
				player.sendMessage(Commands.id
						+ "This server requires you to join the Discord server:  https://discord.gg/" + Main.invite);
			} else {
				justIn.remove(player);
				event.setCancelled(false);
			}
		} else {
			justIn.remove(player);
			event.setCancelled(false);
		}
	}

}
