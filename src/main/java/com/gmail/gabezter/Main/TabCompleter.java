package com.gmail.gabezter.Main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.util.StringUtil;

public class TabCompleter implements org.bukkit.command.TabCompleter {

	private static final String[] BLANK_COMMANDS = {};
	private static final String[] BASE_COMMANDS = { "link", "unlink", "admin", "help" };
	private static final String[] SECOND_COMMANDS = { "rank", "config", "reload", "help" };
	private static final String[] THIRD_RANK_COMMANDS = { "add", "remove", "list" };
	private static final String[] THIRD_CONFIG_COMMANDS = { "update", "timeout" };
	private static final String[] FOURTH_CONFIG_UPDATE_COMMANDS = { "temporal", "rate" };
	private static final String[] FOURTH_CONFIG_TIMEOUT_COMMANDS = { "length", "temporal" };

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		final List<String> completions = new ArrayList<>();
		List<String> COMMANDS = Arrays.asList(BLANK_COMMANDS);
		switch (args.length) {
		case 1:
			COMMANDS = Arrays.asList(BASE_COMMANDS);
			StringUtil.copyPartialMatches(args[0], COMMANDS, completions);
			break;
		case 2:
			if (args[0].equalsIgnoreCase("admin"))
				COMMANDS = Arrays.asList(SECOND_COMMANDS);
			else
				COMMANDS = Arrays.asList(BLANK_COMMANDS);
			StringUtil.copyPartialMatches(args[1], COMMANDS, completions);
			break;
		case 3:
			if (args[1].equalsIgnoreCase("rank"))
				COMMANDS = Arrays.asList(THIRD_RANK_COMMANDS);
			else if (args[1].equalsIgnoreCase("config"))
				COMMANDS = Arrays.asList(THIRD_CONFIG_COMMANDS);
			else
				COMMANDS = Arrays.asList(BLANK_COMMANDS);
			StringUtil.copyPartialMatches(args[2], COMMANDS, completions);
			break;
		case 4:
			if (args[2].equalsIgnoreCase("update") && args[1].equalsIgnoreCase("config"))
				COMMANDS = Arrays.asList(FOURTH_CONFIG_UPDATE_COMMANDS);
			else if (args[2].equalsIgnoreCase("timeout") && args[1].equalsIgnoreCase("config"))
				COMMANDS = Arrays.asList(FOURTH_CONFIG_TIMEOUT_COMMANDS);
			else
				COMMANDS = Arrays.asList(BLANK_COMMANDS);
			StringUtil.copyPartialMatches(args[3], COMMANDS, completions);
			break;
		default:
			COMMANDS = Arrays.asList(BLANK_COMMANDS);
			StringUtil.copyPartialMatches(args[args.length - 1], COMMANDS, completions);
			break;
		}
		Collections.sort(completions);
		return completions;
	}

}
