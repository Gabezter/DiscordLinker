package com.gmail.gabezter.Main;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class LoginLinker implements Listener {

	public LoginLinker() {
	}

	private boolean justLoggedIn = false;

	@EventHandler(priority = EventPriority.NORMAL)
	public void discordTeller(PlayerLoginEvent event) {

		Player player = event.getPlayer();
		if (!(Main.invite.equals("") || Main.invite.equals(" ") || Main.invite.equalsIgnoreCase("NULL"))) {
			justLoggedIn = true;
			player.sendMessage("Make sure to join our ");
		}

	}

	@EventHandler(priority = EventPriority.HIGH)
	public void loginLock(PlayerMoveEvent event) {
		if (justLoggedIn == true && Main.forceLink == true) {
			Player player = event.getPlayer();
		} else {
			justLoggedIn = false;
		}
	}

}
