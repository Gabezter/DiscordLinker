package com.gmail.gabezter.Main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import org.bukkit.plugin.Plugin;

public class Config {
	private Map<Object, Object> config;
	private Plugin plugin;
	private ArrayList<String> conf = new ArrayList<String>();
	private int addLines = 0;

	public Config(Plugin plugin) {
		this.plugin = plugin;
		config = new HashMap<Object, Object>();
		File f = new File(plugin.getDataFolder(), "config.yml");
		if (!f.exists())
			saveDefaultConfig();
		readToMap();
	}

	public void reload() {
		File f;
		try {
			f = new File(plugin.getDataFolder(), "config.yml");
			Scanner reader = new Scanner(f);
			while (reader.hasNext()) {
				String line = reader.nextLine();
				conf.add(line);
			}
			for (String comments : conf) {
				if (!comments.contains("#")) {
					String[] configLine = comments.split(":");
					if (configLine.length == 1) {
						if (!configLine[0].equalsIgnoreCase("id"))
							config.put(configLine[0], " ");
					} else
						config.put(configLine[0], configLine[1]);
				}
			}
			reader.close();
		} catch (IOException e) {
//			e.printStackTrace();
			plugin.getPluginLoader().disablePlugin(plugin);
		}
	}

	public Map<Object, Object> read() {
		return config;
	}

	private Map<Object, Object> readTimeout(Scanner reader) {
		Map<Object, Object> timeout = new HashMap<Object, Object>();
		String line = reader.nextLine();
		while (!line.contains("# Update")) {
			if (!line.contains("#")) {
				String[] split = line.split(": ");
				if (split.length == 1)
					timeout.put(split[0].replace(" ", ""), "");
				else
					timeout.put(split[0].replace(" ", ""), split[1]);
			}
			conf.add(line);
			line = reader.nextLine();
		}
		conf.add(line);
		return timeout;
	}

	private Map<Object, Object> readUpdate(Scanner reader) {
		Map<Object, Object> update = new HashMap<Object, Object>();
		String line = reader.nextLine();
		while (!line.contains("# Discord")) {
			if (!line.contains("#")) {
				String[] split = line.split(": ");
				if (split.length == 1)
					update.put(split[0].replace(" ", ""), " ");
				else
					update.put(split[0].replace(" ", ""), split[1]);
			}
			conf.add(line);
			line = reader.nextLine();
		}
		conf.add(line);
		return update;
	}

	private void readToMap() {
		conf.clear();
		File f;
		try {
			f = new File(plugin.getDataFolder(), "config.yml");
			Scanner reader = new Scanner(f);
			while (reader.hasNext()) {
				boolean skip = false;
				String line = reader.nextLine();
				if (!line.contains("#")) {
					String[] tmp = line.split(": ");
					if (tmp[0].equalsIgnoreCase("id"))
						if (tmp.length == 1)
							config.put("id", "");
						else
							config.put("id", tmp[1]);
					else if (tmp[0].equalsIgnoreCase("timeout:") || tmp[0].equalsIgnoreCase("timeout")
							|| tmp[0].equalsIgnoreCase("timeout: ")) {
						conf.add(line);
						config.put("timeout", readTimeout(reader));
						skip = true;
					} else if (tmp[0].equalsIgnoreCase("update:") || tmp[0].equalsIgnoreCase("update")
							|| tmp[0].equalsIgnoreCase("update: ")) {
						conf.add(line);
						config.put("update", readUpdate(reader));
						skip = true;
					} else if (tmp[0].equalsIgnoreCase("token"))
						config.put("token", tmp[1]);
					else {
						config.put(tmp[0], tmp[1]);
					}
				}
				if (!skip)
					conf.add(line);
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void save() throws IOException {
		PrintWriter pw = new PrintWriter(new FileWriter(plugin.getDataFolder() + "/config.yml", false));
		ArrayList<String> tmp = merge();
		conf = tmp;
		for (String line : conf) {
			pw.println(line);
		}
		pw.close();
	}

	@SuppressWarnings("unchecked")
	public boolean writeToConfig(Configure conf, Object object) {
		try {
			Map<Object, Object> timeout = (Map<Object, Object>) config.get("timeout");
			Map<Object, Object> update = (Map<Object, Object>) config.get("update");
			switch (conf) {
			case ID:
				config.put("id", object);
				break;
			case FORCELINK:
				config.put("forceLink", object);
				break;
			case TIMEOUT_LENGTH:
				timeout.put("length", object);
				break;
			case TIMEOUT_TYPE:
				timeout.put("type", object);
				break;
			case UPDATE_FROM:
				update.put("FromMinecraft", object);
				break;
			case UPDATE_RATE:
				update.put("rate", object);
			case UPDATE_TEMPORAL:
				update.put("temporal", object);
				break;
			case UPDATE_MESSAGE:
				update.put("message", object);
				break;
			case TOKEN:
				config.put("token", object);
				break;
			default:
				break;
			}
			save();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@SuppressWarnings("unchecked")
	private ArrayList<String> merge() {
		ArrayList<String> fileLines = new ArrayList<String>();
		for (int i = 0; i < conf.size(); i++) {
			if (!conf.get(i).contains("#")) {
				String confLine = conf.get(i);
				if (confLine.contains("timeout:")) {
					fileLines.addAll(readTimeoutMap((Map<Object, Object>) config.get("timeout"), i));
					i += addLines - 1;
				} else if (confLine.contains("update:")) {
					fileLines.addAll(readUpdateMap((Map<Object, Object>) config.get("update"), i));
					i += addLines - 1;
				} else {
					String configLine = config.get(confLine.split(":")[0]).toString();
					if (configLine == null)
						configLine = " ";
					String line = confLine.split(":")[0] + ": " + configLine;
					fileLines.add(line);
				}
			} else
				fileLines.add(conf.get(i));
		}
		return fileLines;
	}

	private Collection<String> readTimeoutMap(Map<Object, Object> timeout, int lineNumber) {
		Collection<String> timeoutLines = new ArrayList<String>();
		String line = conf.get(lineNumber);
		addLines = 0;
		while (!line.contains("# Update")) {
			addLines++;
			if (!conf.get(lineNumber).contains("#") && !conf.get(lineNumber).contains("timeout")) {
				String configLine = timeout.get(line.split(":")[0].replace(" ", "")).toString(); // EXECEPTION
				if (configLine == null)
					configLine = " ";
				timeoutLines.add(line.split(":")[0] + ": " + configLine);
			} else
				timeoutLines.add(conf.get(lineNumber));
			lineNumber++;
			line = conf.get(lineNumber);
		}
		return timeoutLines;
	}

	private Collection<String> readUpdateMap(Map<Object, Object> update, int lineNumber) {
		Collection<String> updateLines = new ArrayList<String>();
		String line = conf.get(lineNumber);
		addLines = 0;
		while (!line.contains("# Discord")) {
			addLines++;
			if (!line.contains("#") && !line.contains("update")) {
				String configLine = update.get(line.split(":")[0].replace(" ", "")).toString();
				if (configLine == null)
					configLine = " ";
				updateLines.add(line.split(":")[0] + ": " + configLine);
			} else
				updateLines.add(conf.get(lineNumber));
			lineNumber++;
			line = conf.get(lineNumber);
		}
		return updateLines;
	}

	public boolean saveConfig() {
		try {
			save();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	@SuppressWarnings("unchecked")
	public Object getConfig(Configure conf) {
		Map<Object, Object> timeout = (Map<Object, Object>) config.get("timeout");
		Map<Object, Object> update = (Map<Object, Object>) config.get("update");
		switch (conf) {
		case ID:
			return (String) config.get("id");
		case FORCELINK:
			String link = (String) config.get("forceLink");
			if (link.equalsIgnoreCase("false"))
				return false;
			else if (link.equalsIgnoreCase("true"))
				return true;
			else
				return null;
		case TIMEOUT_LENGTH:
			return Integer.parseInt((String) timeout.get("length"));
		case TIMEOUT_TYPE:
			return (String) timeout.get("type");
		case UPDATE_FROM:
			String from = update.get("FromMinecraft").toString();
			if (from.equalsIgnoreCase("false"))
				return false;
			else if (from.equalsIgnoreCase("true"))
				return true;
			else
				return null;
		case UPDATE_RATE:
			return Integer.parseInt((String) update.get("rate").toString());
		case UPDATE_TEMPORAL:
			return (String) update.get("temporal");
		case UPDATE_MESSAGE:
			String message = update.get("message").toString();
			if (message.equalsIgnoreCase("false"))
				return false;
			else if (message.equalsIgnoreCase("true"))
				return true;
			else
				return null;
		case TOKEN:
			return (String) config.get("token");
		default:
			return null;
		}
	}

	public boolean saveDefaultConfig() {
		try {
			conf.add("# Used to verfy that that the discord server is owned by an admin of the server");
			conf.add("id: 1234896798");
			conf.add("# Forces player to link to discord. Will freeze player when they log in,"
					+ " until they link their discord to thier minecraft account");
			conf.add("forceLink: false");
			conf.add("timeout:");
			conf.add("  length: 5");
			conf.add("  # The type of time that the code will be active");
			conf.add("  # m: minutes (default)");
			conf.add("  # d: days");
			conf.add("  # s: seconds");
			conf.add("  # h: hours");
			conf.add("  type: m");
			conf.add("# Update is used to determine the rate and direction that the ranks are updated.");
			conf.add("update:");
			conf.add("  # True: The ranks are read from Minecraft Server");
			conf.add("  # False: The ranks are read from Discord Server");
			conf.add("  FromMinecraft: true");
			conf.add("  # The time that the ranks will be updated. Default is 5 minutes");
			conf.add("  rate: 4");
			conf.add("  # The type of time that the ranks will be updated");
			conf.add("  # m: minutes ");
			conf.add("  # d: days");
			conf.add("  # s: seconds");
			conf.add("  # h: hours (default)");
			conf.add("  temporal: h");
			conf.add("  # Writes message to server logger when the ranks are updated.");
			conf.add("  # True: writes message");
			conf.add("  # False: does NOT write message");
			conf.add("  message: true");
			conf.add("# Discord bot token.");
			conf.add("token: ChangeMe");
			conf.add("invite-code: NULL");
			BufferedWriter bw = new BufferedWriter(new FileWriter(plugin.getDataFolder() + "/config.yml"));
			for (String line : conf) {
				bw.write(line);
				bw.write('\n');
			}
			bw.close();
		} catch (Exception e) {
			plugin.getLogger().warning(e.getStackTrace().toString());
			return false;
		}
		return true;
	}

	void setupConfig() {

		Main.token = (String) getConfig(Configure.TOKEN);
		Main.id = (String) getConfig(Configure.ID);
		Main.forceLink = (boolean) getConfig(Configure.FORCELINK);
		Main.timeoutLength = (int) getConfig(Configure.TIMEOUT_LENGTH);
		Main.timeoutType = (String) getConfig(Configure.TIMEOUT_TYPE);
		Main.updateFrom = (boolean) getConfig(Configure.UPDATE_FROM);
		Main.updateRate = (int) getConfig(Configure.UPDATE_RATE);
		Main.updateType = (String) getConfig(Configure.UPDATE_TEMPORAL);
		Main.updateMessage = (boolean) getConfig(Configure.UPDATE_MESSAGE);

		Configure.TOKEN.setObj(Main.token);
		Configure.ID.setObj(Main.id);
		Configure.FORCELINK.setObj(Main.forceLink);
		Configure.TIMEOUT_LENGTH.setObj(Main.timeoutLength);
		Configure.TIMEOUT_TYPE.setObj(Main.timeoutType);
		Configure.UPDATE_FROM.setObj(Main.updateFrom);
		Configure.UPDATE_RATE.setObj(Main.updateRate);
		Configure.UPDATE_TEMPORAL.setObj(Main.updateType);
		Configure.UPDATE_MESSAGE.setObj(Main.updateMessage);

		/*
		 * token = getConfig().getString("token"); id = getConfig().getString("id");
		 * forceLink = getConfig().getBoolean("forceLink"); timeoutLength =
		 * getConfig().getInt("timeout.length"); timeoutType =
		 * getConfig().getString("timeout.type"); updateFrom =
		 * getConfig().getBoolean("update.FromMinecraft"); updateRate =
		 * getConfig().getInt("update.rate"); updateType =
		 * getConfig().getString("update.temporal"); updateMessage =
		 * getConfig().getBoolean("update.message");
		 */
		if (Main.id == null || Main.id.isEmpty()) {
			Main.id = Main.createCode();
			plugin.getLogger().info(Main.id);
			writeToConfig(Configure.ID, Main.id);
			setupConfig();
//			saveConfig();
//			reloadConfig();
		}
	}
}
