package com.gmail.gabezter.Main;

import java.io.File;
import java.util.Date;
import java.util.EnumSet;
import java.util.Random;

import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import com.gmail.gabezter.Discord.MessageListener;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.utils.Compression;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import net.milkbowl.vault.permission.Permission;

public class Main extends JavaPlugin {

	public static String consoleID = "[Discord Ranking]";
	public static JDABuilder builder;
	public static JDA bot;
	public static Permission perms = null;
	public static String token = "";
	public static boolean forceLink = false;
	public static int timeoutLength = 0;
	public static String timeoutType = "m";
	public static String id = "";
	public static boolean updateFrom = true;
	public static String updateType = "h";
	public static int updateRate = 5;
	public static boolean updateMessage = false;

	private static boolean success = false;

	Players players = new Players(this);
	Ranks ranks = new Ranks(this);
	Config config = new Config(this);

	@Override
	public void onEnable() {
		File conf = new File(this.getDataFolder(), "config.yml");
		if (!conf.exists())
			config.saveDefaultConfig();
		config.setupConfig();
		if (token != null && !token.equalsIgnoreCase("") && !token.equalsIgnoreCase("ChangeMe"))
			success = botSetup();
		else {
			getLogger().severe(consoleID + "Token has not been entered. Please enter token in config.yml file.");
		}
		setupPermissions();
		startTask();
		getCommand("discordlink").setExecutor(new Commands(this));
		getCommand("discordlink").setTabCompleter(new TabCompleter());
		if (!success)
			this.onDisable();
		else
			getLogger().info("Successfully Enabled");
	}

	@Override
	public void onDisable() {
		config.writeToConfig(Configure.ID, id);
		if (success)
			bot.shutdownNow();
		getLogger().info("Discord bot has been shutdown");
		getLogger().info("Disabled");
	}

	private boolean startTask() {
		try {
			BukkitScheduler task = getServer().getScheduler();
			task.scheduleSyncRepeatingTask(this, new Tasks(this), 0L, (20 * getTimer()));
		} catch (Exception e) {
			getLogger().warning(e.getStackTrace().toString());
		}
		return true;
	}

	private long getTimer() {
		long time = 0L;
		switch (updateType.toCharArray()[0]) {
		case 'm':
			time = 60L;
			break;
		case 's':
			time = 1L;
			break;
		case 'd':
			time = 86400L;
			break;
		case 'h':
		default:
			time = 3600L;
			break;
		}
		return time * updateRate;
	}

	private boolean setupPermissions() {
		RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
		perms = rsp.getProvider();
		getLogger().info(perms.getName());
		return perms != null;
	}

	

	static String createCode() {
		Random rand = new Random();
		Date date = new Date();
		long seed = date.getTime();
		return createCode(rand.nextInt(Math.abs((int) seed)));
	}

	public static String createCode(int id) {
		long seed = new Date().getTime();
		String preCode = Long.toString(seed);
		int length = preCode.length() / 2;
		String[] code = { preCode.substring(length), preCode.substring(0, length) };
		String postCode = code[0] + id + code[1];
		return Integer.toHexString(Math.abs(postCode.hashCode()));
	}

	private boolean botSetup() {
		boolean success = false;
		try {
			builder = new JDABuilder(token);
			builder.setCompression(Compression.NONE);
			builder.setDisabledCacheFlags(EnumSet.of(CacheFlag.ACTIVITY, CacheFlag.VOICE_STATE));
			builder.addEventListeners(new MessageListener());
			builder.setActivity(Activity.playing("Minecraft"));
			bot = builder.build();

			getLogger().info("Discord Bot has been turned on.");
			success = true;
		} catch (Exception e) {
			e.printStackTrace();
			getLogger().severe("An error has occured in connecting to bot.");
			success = false;
		}
		return success;
	}

	public static boolean testID(String idC, String guildId) {
		if (idC.equalsIgnoreCase(id)) {
			id = guildId;
			return true;
		}
		return false;
	}

	public static boolean checkID(String id2) {
		return !id2.equalsIgnoreCase(id);
	}
}
