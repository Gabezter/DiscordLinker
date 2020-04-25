package com.gmail.gabezter.Main;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import net.md_5.bungee.api.ChatColor;

public class Commands implements CommandExecutor {
	Plugin plugin;
	Main main;
	Players players;
	Ranks ranks;
	Config config;
	String id = ChatColor.BLUE + Main.consoleID + ChatColor.RESET + " ";

	public Commands(Plugin plugin) {
		this.plugin = plugin;
		players = new Players(plugin);
		ranks = new Ranks(plugin);
		config = new Config(plugin);
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("discordlink")) {
			if (sender instanceof Player) {
				UUID uuid = ((Player) sender).getUniqueId();
				if (args[0].equalsIgnoreCase("register")) {
					if (checkPermission(Cmds.REG, sender)) {
						if (args.length == 2) {
							switch (players.registerUser(uuid, sender.getName(), args[1],
									Main.createCode(uuid.hashCode()))) {
							case 1:
								sender.sendMessage(id
										+ "You have registered now check your direct messages on Discord for a code from the "
										+ Main.bot.getGuildById(Main.id).getSelfMember().getUser().getName()
										+ " Bot on the Discord Server");
								break;
							case 0:
								sender.sendMessage(
										id + "You could not be registered. Your account may already be registered.");
							default:
								break;
							}
						} else {
							sender.sendMessage(id + "Command is: /dc register [discord name]");
						}
					} else {
						noPermission(sender);
					}
					return true;
				}
				if (args[0].equalsIgnoreCase("link")) {
					if (checkPermission(Cmds.LINK, sender)) {
						if (args.length == 2) {
							switch (players.linkUser(uuid, args[1])) {
							case 1:
								sender.sendMessage(id
										+ "You have successfully linked your minecraft account with your discord account");
								plugin.getLogger().info(sender.getName() + " has been linked. ");
								break;
							case 0:
								sender.sendMessage(id + "There was an error please try again later.");
								break;
							case 2:
								sender.sendMessage(id + "That code has expired. Please register.");
								break;
							case -1:
								sender.sendMessage(id + "That code is invalid, please try again.");
								break;
							default:
								sender.sendMessage(id + "There was an error please try again later.");
								break;
							}
						} else {
							sender.sendMessage(id + "Command is: /dc link [code]");
						}
					} else {
						noPermission(sender);
					}
					return true;
				}
				if (args[0].equalsIgnoreCase("unlink")) {
					if (checkPermission(Cmds.UNLINK, sender)) {
						if (args.length == 1) {
							switch (players.unlink(uuid, sender)) {
							case 1:
								sender.sendMessage(id + "Successfully unlinked your accounts");
								break;
							case 0:
							default:
								sender.sendMessage(id + "An error has occured. Could not unlink your accounts.");
								break;
							}
						} else {
							sender.sendMessage(id + "Command is: /dc unlink");
						}
					} else {
						noPermission(sender);
					}
					return true;

				}
				if (args[0].equalsIgnoreCase("admin")) {
					String command = args[1];
					if (checkPermission(Cmds.ADMIN, sender)) {
						if (command.equalsIgnoreCase("rank")) {
							if (args.length > 2) {
								if (args[2].equalsIgnoreCase("add")) {
									if (checkPermission(Cmds.ADD, sender)) {
										if (args.length == 5) {
											ranks.addRank(args[3], args[4]);
											sender.sendMessage(id + "The ranks [" + args[3] + "] : [" + args[4]
													+ "] have been added");
										} else {
											sender.sendMessage(id
													+ "Command is: /dc admin rank add [Minecraft Rank] [Discord Rank]");
										}
									} else {
										noPermission(sender);
									}

								}
								if (args[2].equalsIgnoreCase("remove")) {
									if (checkPermission(Cmds.REMOVE, sender)) {
										if (args.length == 5) {
											ranks.removeRank(args[3], args[4]);
											sender.sendMessage(id + "The ranks [" + args[3] + "] : [" + args[4]
													+ "] have been removed");
										} else {
											sender.sendMessage(id
													+ "Command is: /dc admin rank remove [Minecraft Rank] [Discord Rank]");
										}
									} else {
										noPermission(sender);
									}
								}
								if (args[2].equalsIgnoreCase("list")) {
									if (checkPermission(Cmds.LIST, sender)) {
										if (args.length == 3) {
											ArrayList<String[]> rankList = ranks.listRanks();
											sender.sendMessage("  [Minecraft Rank] : [Discord Rank]");
											for (String[] rank : rankList) {
												sender.sendMessage(ChatColor.RED + "- " + rank[0] + " : " + rank[1]);
											}
										} else {
											sender.sendMessage(id + "Command is: /dc admin rank list");
										}
									} else {
										noPermission(sender);
									}
								}
							} else {
								sender.sendMessage(id + "Command is: /dc admin [add,remove,list]");
							}
						}
					} else {
						noPermission(sender);
					}
					if (command.equalsIgnoreCase("config")) {
						if (args[2].equalsIgnoreCase("UPDATE")) {
							if (args[3].equalsIgnoreCase("temporal")) {
								if (checkPermission(Cmds.UPDATE_TEMPORAL, sender)) {
									configUpdate(Configure.UPDATE_TEMPORAL, args[4], sender);
									config.writeToConfig(Configure.UPDATE_TEMPORAL, "");
//									plugin.getConfig().set("", "");
//									plugin.saveConfig();
									config.setupConfig();
								} else {
									noPermission(sender);
								}
							}
							if (args[3].equalsIgnoreCase("rate")) {
								if (checkPermission(Cmds.RATE, sender)) {
									configUpdate(Configure.UPDATE_RATE, args[4], sender);
								} else {
									noPermission(sender);
								}
							}
						}
						if (args[2].equalsIgnoreCase("TIMEOUT")) {
							if (args[3].equalsIgnoreCase("length")) {
								if (checkPermission(Cmds.LENGTH, sender)) {
									configTimeout(Configure.TIMEOUT_LENGTH, args[4], sender);
								} else {
									noPermission(sender);
								}
							}
							if (args[3].equalsIgnoreCase("temporal")) {
								if (checkPermission(Cmds.TIMEOUT_TEMPORAL, sender)) {
									configTimeout(Configure.TIMEOUT_TYPE, args[4], sender);

								} else {
									noPermission(sender);
								}
							}
						}
					}
					if (command.equalsIgnoreCase("reload")) {
						if (checkPermission(Cmds.RELOAD, sender)) {
							plugin.getPluginLoader().disablePlugin(plugin);
							plugin.getPluginLoader().enablePlugin(plugin);
							sender.sendMessage(id + "Reload Complete");
						} else {
							noPermission(sender);
						}
					}
					if (command.equalsIgnoreCase("update")) {
						if (Updater.update(plugin))
							sender.sendMessage(id + "Succesfully updated ranks");
						else
							sender.sendMessage(id + "Failed to update ranks");
					}
					if (command.equalsIgnoreCase("help")) {
						sender.sendMessage("-------- " + id + "--------");
					}
					return true;
				}
				return true;
			} else {
				if (args[0].equalsIgnoreCase("admin")) {
					String command = args[1];
					if (command.equalsIgnoreCase("update")) {
						if (Updater.update(plugin))
							sender.sendMessage(id + "Succesfully updated ranks");
						else
							sender.sendMessage(id + "Failed to update ranks");
					}
					if (command.equalsIgnoreCase("rank")) {
						if (args[2].equalsIgnoreCase("add")) {
							ranks.addRank(args[3], args[4]);
							sender.sendMessage(id + "The ranks [" + args[3] + "] : [" + args[4] + "] have been added");
						}
						if (args[2].equalsIgnoreCase("remove")) {
							ranks.removeRank(args[3], args[4]);
							sender.sendMessage(
									id + "The ranks [" + args[3] + "] : [" + args[4] + "] have been removed");
						}
						if (args[2].equalsIgnoreCase("list")) {
							ArrayList<String[]> rankList = ranks.listRanks();
							sender.sendMessage("  [Minecraft Rank] : [Discord Rank]");
							for (String[] rank : rankList) {
								sender.sendMessage(ChatColor.RED + "- " + rank[0] + " : " + rank[1]);
							}
						}
					}
					if (command.equalsIgnoreCase("config")) {
						if (args[2].equalsIgnoreCase("UPDATE")) {
							if (args[3].equalsIgnoreCase("temporal")) {
								configUpdate(Configure.UPDATE_TEMPORAL, args[4], sender);
//								config.writeToConfig(Configure.UPDATE_TEMPORAL, "");
//								plugin.getConfig().set("", "");
//								plugin.saveConfig();
								config.setupConfig();
							} else if (args[3].equalsIgnoreCase("rate")) {
								configUpdate(Configure.UPDATE_RATE, args[4], sender);
//								config.writeToConfig(Configure.UPDATE_RATE, "");
//								plugin.getConfig().set("", "");
//								plugin.saveConfig();
								config.setupConfig();
							} else if (args[3].equalsIgnoreCase("from")) {
								configUpdate(Configure.UPDATE_FROM, args[4], sender);
								/*
								 * if (args[4].equalsIgnoreCase("true")) {
								 * config.writeToConfig(Configure.UPDATE_FROM, true); //
								 * plugin.getConfig().set("update.FromMinecraft", true);
								 * sender.sendMessage("The plugin will now update from the Minecraft server");
								 * // plugin.saveConfig(); main.setupConfig(); } else if
								 * (args[4].equalsIgnoreCase("false")) {
								 * config.writeToConfig(Configure.UPDATE_FROM, false); //
								 * plugin.getConfig().set("update.FromMinecraft", false); sender.sendMessage(id
								 * + "The plugin will now update from the Discord server!"); //
								 * plugin.saveConfig(); main.setupConfig();
								 * 
								 * } else sender.sendMessage("Value must be " + ChatColor.BOLD + "true" +
								 * ChatColor.RESET + " or " + ChatColor.BOLD + "false" + ChatColor.RESET +
								 * ". not " + args[4]);
								 */
							} else {
								sender.sendMessage(id + "Command must be " + ChatColor.BOLD + "temporal"
										+ ChatColor.RESET + "," + ChatColor.BOLD + " rate" + ChatColor.RESET + ", or "
										+ ChatColor.BOLD + "from");
							}

						}
						if (args[2].equalsIgnoreCase("TIMEOUT")) {
							if (args[3].equalsIgnoreCase("length")) {
								config.writeToConfig(Configure.TIMEOUT_LENGTH, args[4]);
								config.setupConfig();
							}
							if (args[3].equalsIgnoreCase("temporal")) {
								config.writeToConfig(Configure.TIMEOUT_TYPE, args[4]);
								config.setupConfig();
							}
						}
					}
					if (command.equalsIgnoreCase("reload")) {
						plugin.getPluginLoader().disablePlugin(plugin);
						plugin.getPluginLoader().enablePlugin(plugin);
						sender.sendMessage(id + "Reload Complete");
					}
					return true;
				} else
					sender.sendMessage("You must be a player!");
				return false;
			}
		}
		return false;
	}

	private boolean checkPermission(Cmds cmd, CommandSender sender) {
		String base = "discordlink.";
		switch (cmd) {
		case LINK:
			return sender.hasPermission(base + "link");
		case REG:
			return sender.hasPermission(base + "register");
		case UNLINK:
			return sender.hasPermission(base + "unlink");
		case RELOAD:
			return sender.hasPermission(base + "admin.reload");
		case FORCELINK:
			return sender.hasPermission(base + "admin.config.set.forcelink");
		case LENGTH:
			return sender.hasPermission(base + "admin.config.set.timeout.length");
		case TIMEOUT_TEMPORAL:
			return sender.hasPermission(base + "admin.config.set.timeout.type");
		case RATE:
			return sender.hasPermission(base + "admin.config.set.update.rate");
		case FROM:
			return sender.hasPermission(base + "admin.config.set.update.from");
		case UPDATE_TEMPORAL:
			return sender.hasPermission(base + "admin.config.set.update.temporal");
		case ID:
			return sender.hasPermission(base + "admin.id");
		case LIST:
			return sender.hasPermission(base + "admin.rank.list");
		case REMOVE:
			return sender.hasPermission(base + "admin.rank.remove");
		case ADD:
			return sender.hasPermission(base + "admin.rank.add");
		case ADMIN:
			return sender.hasPermission(base + "admin");
		default:
			return false;
		}
	}

	private void noPermission(CommandSender sender) {
		sender.sendMessage(id + ChatColor.RED + "You do not have permission for that!");
	}

	private boolean configUpdate(Configure conf, String arg, CommandSender sender) {
		switch (conf) {
		case UPDATE_TEMPORAL:
			config.writeToConfig(Configure.UPDATE_TEMPORAL, arg);
//			plugin.getConfig().set("", "");
//			plugin.saveConfig();
			config.setupConfig();
			return true;
		case UPDATE_RATE:
			config.writeToConfig(Configure.UPDATE_RATE, arg);
//			plugin.getConfig().set("", "");
//			plugin.saveConfig();
			config.setupConfig();
			return true;
		case UPDATE_FROM:
			if (arg.equalsIgnoreCase("true")) {
				config.writeToConfig(Configure.UPDATE_FROM, true);
//				plugin.getConfig().set("update.FromMinecraft", true);
				sender.sendMessage("The plugin will now update from the Minecraft server");
//				plugin.saveConfig();
				config.setupConfig();
			} else if (arg.equalsIgnoreCase("false")) {
				config.writeToConfig(Configure.UPDATE_FROM, false);
//				plugin.getConfig().set("update.FromMinecraft", false);
				sender.sendMessage(id + "The plugin will now update from the Discord server!");
//				plugin.saveConfig();
				config.setupConfig();
			} else
				sender.sendMessage("Value must be " + ChatColor.BOLD + "true" + ChatColor.RESET + " or "
						+ ChatColor.BOLD + "false" + ChatColor.RESET + ". not " + arg);
			return true;
		default:
			sender.sendMessage(id + "Command must be " + ChatColor.BOLD + "temporal" + ChatColor.RESET + ","
					+ ChatColor.BOLD + " rate" + ChatColor.RESET + ", or " + ChatColor.BOLD + "from");
			return true;
		}
	}

	/*
	 * if (cmd.equalsIgnoreCase("temporal")) {
	 * config.writeToConfig(Configure.UPDATE_TEMPORAL, ""); //
	 * plugin.getConfig().set("", ""); // plugin.saveConfig(); main.setupConfig(); }
	 * else if (cmd.equalsIgnoreCase("rate")) {
	 * config.writeToConfig(Configure.UPDATE_RATE, ""); //
	 * plugin.getConfig().set("", ""); // plugin.saveConfig(); main.setupConfig(); }
	 * else if (cmd.equalsIgnoreCase("from")) { if (arg.equalsIgnoreCase("true")) {
	 * config.writeToConfig(Configure.UPDATE_FROM, true); //
	 * plugin.getConfig().set("update.FromMinecraft", true);
	 * sender.sendMessage("The plugin will now update from the Minecraft server");
	 * // plugin.saveConfig(); main.setupConfig(); } else if
	 * (arg.equalsIgnoreCase("false")) { config.writeToConfig(Configure.UPDATE_FROM,
	 * false); // plugin.getConfig().set("update.FromMinecraft", false);
	 * sender.sendMessage(id +
	 * "The plugin will now update from the Discord server!"); //
	 * plugin.saveConfig(); main.setupConfig();
	 */
//	}
//
//	}else{sender.sendMessage(id+"Command must be "+ChatColor.BOLD+"temporal"+ChatColor.RESET+","+ChatColor.BOLD+" rate"+ChatColor.RESET+", or "+ChatColor.BOLD+"from");}return false;}

	private boolean configTimeout(Configure conf, String arg, CommandSender sender) {
		switch (conf) {
		case TIMEOUT_LENGTH:
			config.writeToConfig(conf, arg);
//			plugin.getConfig().set("timeout.length", args[4]);
//			plugin.saveConfig();
//			plugin.reloadConfig();
			config.setupConfig();
			return true;
		case TIMEOUT_TYPE:
			config.writeToConfig(conf, arg);
			return true;
		default:
			return true;
		}
	}
}

enum Cmds {
	REG("register"), LINK("link"), ID(""), ADD("add"), REMOVE("remove"), LIST("list"), RELOAD("reload"),
	FORCELINK("forcelink"), LENGTH("length"), TIMEOUT_TEMPORAL("type"), FROM("from"), RATE("rate"),
	UPDATE_TEMPORAL("temporal"), UNLINK("unlink"), ADMIN("admin");

	private final String cmd;

	private Cmds(String cmd) {
		this.cmd = cmd;
	}

	@Override
	public String toString() {
		return cmd;
	}
}
