package com.gmail.gabezter.Discord;

import com.gmail.gabezter.Main.Main;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class MessageListener extends ListenerAdapter {

	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		if (event.isFromType(ChannelType.TEXT)) {
			Member member = event.getGuild().getMember(event.getAuthor());
			if (member.getIdLong() != Main.bot.getSelfUser().getIdLong()) {
				Message msg = event.getMessage();
				char call = '~';
				char check = msg.getContentRaw().charAt(0);
				String message = msg.getContentRaw().substring(1);
				String[] args = message.split(" ");
				String cmd = args[0];
				if (Character.compare(call, check) == 0) {
					MessageChannel channel = event.getChannel();
					if (cmd.equalsIgnoreCase("id")) {
						if (member.hasPermission(Permission.MANAGE_SERVER)
								|| member.hasPermission(Permission.ADMINISTRATOR))
							if (Main.checkID(event.getGuild().getId())) {
								if (Main.testID(args[1], event.getGuild().getId())) {
									channel.sendMessage("Successfully Linked\n" + event.getGuild().getId()).queue();
								}
							} else
								channel.sendMessage(member.getAsMention() + " The discord has already been linked.")
										.queue();
					}
				}
			}
		}
	}

}
