package com.gmail.gabezter.Main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;

public class Players {
	private Plugin plugin;
	private File players;
	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	DocumentBuilder builder = null;
	Document doc;
	Element root;

	public Players(Plugin plugin) {
		this.plugin = plugin;
		players = new File(plugin.getDataFolder() + "/players.xml");
		if (!players.exists()) {
			try {
				players.createNewFile();
				FileWriter w = new FileWriter(players);
				BufferedWriter bw = new BufferedWriter(w);
				bw.write("<?xml version = \"1.0\"?>\r\n" + "<!-- DO NOT MESS WITH THIS FILE -->\r\n"
						+ "<!-- MESSING WITH THIS FILE HAS -->\r\n" + "<!-- THE POTENTIAL TO BREAK THE PLUGIN -->\r\n"
						+ "<players></players>");
				bw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else {
			try {
				builder = factory.newDocumentBuilder();
				doc = builder.parse(players);
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			root = doc.getDocumentElement();
		}
	}

	public int registerUser(UUID uuid, String mName, String dName, String code) {
		int success = 0;
		NodeList list = root.getElementsByTagName(uuid.toString());
		ZonedDateTime date = ZonedDateTime.now(ZoneId.of("UTC"));
		if (list.getLength() == 0) {
			Element base = doc.createElement(uuid.toString());
			Element discordE = doc.createElement("discord");
			Element minecraftE = doc.createElement("minecraft");
			Element codeE = doc.createElement("code");
			Element linkTimeE = doc.createElement("RegisterTime");
			Element timeout = doc.createElement("timeoutTime");
			Element dId = doc.createElement("id");
			Element Dname = doc.createElement("Name");
			ZonedDateTime timeOUT = timeoutDate(date);
			plugin.getLogger().info(timeOUT.format(DateTimeFormatter.ofPattern("yyyy-MM-dd H:mm:ss z")).toString());
			timeout.appendChild(
					doc.createTextNode(timeOUT.format(DateTimeFormatter.ofPattern("yyyy-MM-dd H:mm:ss z")).toString()));
			codeE.appendChild(doc.createTextNode(code));
			linkTimeE.appendChild(
					doc.createTextNode(date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd H:mm:ss z")).toString()));
			Dname.appendChild(doc.createTextNode(dName));
			minecraftE.appendChild(doc.createTextNode(mName));
			long uId = dmUser(dName, code, Main.timeoutLength, Main.timeoutType);
			dId.appendChild(doc.createTextNode(Long.toString(uId)));
			discordE.appendChild(dId);
			discordE.appendChild(Dname);
			codeE.appendChild(timeout);
			base.appendChild(minecraftE);
			base.appendChild(discordE);
			base.appendChild(codeE);
			base.appendChild(linkTimeE);
			root.appendChild(base);
			success = 1;
		} else if (list.getLength() == 1) {
			Element e = (Element) list.item(0);
			Element codeE = (Element) e.getElementsByTagName("code").item(0);
			if (codeE.getChildNodes().getLength() == 2) {
				ZonedDateTime timeout = ZonedDateTime.parse(codeE.getChildNodes().item(1).getTextContent(),
						DateTimeFormatter.ofPattern("yyyy-MM-dd H:mm:ss z"));
				ZonedDateTime now = ZonedDateTime.parse(ZonedDateTime.now(ZoneId.of("UTC"))
						.format(DateTimeFormatter.ofPattern("yyyy-MM-dd H:mm:ss z")).toString(),
						DateTimeFormatter.ofPattern("yyyy-MM-dd H:mm:ss z"));
				if (timeout.compareTo(now) == -1) {
					codeE.setTextContent(code);
//					codeE.getElementsByTagName("");
					dmUser(dName, code, Main.timeoutLength, Main.timeoutType);
					success = 1;
				}
			}
		} else {
			success = 0;
		}
		saveXML();
		return success;
	}

	public int linkUser(UUID uuid, String code) {
		NodeList list = root.getElementsByTagName(uuid.toString());
		int success = 0;
		if (list.getLength() == 1) {
			Element e = (Element) list.item(0);
			Element codeE = (Element) e.getElementsByTagName("code").item(0);
			if (codeE.getChildNodes().getLength() == 2) {
				ZonedDateTime timeout = ZonedDateTime.parse(codeE.getChildNodes().item(1).getTextContent(),
						DateTimeFormatter.ofPattern("yyyy-MM-dd H:mm:ss z"));
				ZonedDateTime now = ZonedDateTime.parse(ZonedDateTime.now(ZoneId.of("UTC"))
						.format(DateTimeFormatter.ofPattern("yyyy-MM-dd H:mm:ss z")).toString(),
						DateTimeFormatter.ofPattern("yyyy-MM-dd H:mm:ss z"));
				if (timeout.compareTo(now) == 1 || timeout.compareTo(now) == 0) {
					switch (codeE.getChildNodes().item(0).getTextContent().compareTo(code)) {
					case 0:
						success = 1;
						codeE.removeChild(codeE.getChildNodes().item(1));
						Element linkT = doc.createElement("LinkTime");
						linkT.appendChild(doc.createTextNode(
								now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd H:mm:ss z")).toString()));
						e.appendChild(linkT);
						break;
					case 1:
					case -1:
					default:
						success = -1;
						break;
					}
				} else {
					success = 2;
				}
			}
		}
		saveXML();
		return success;
	}

	public long dmUser(String dName, String code, int time, String type) {
		JDA bot = Main.bot;
		Member member = bot.getGuildById(Main.id).getMembersByName(dName, true).get(0);
		long id = member.getIdLong();
		bot.getUserById(id).openPrivateChannel().queue(channel -> {
			String temp = "minute";
			channel.sendMessage(id
					+ "Hello, someone has requested to link this discord account to a minecraft account on the minecraft server "
					+ plugin.getServer().getName() + " that is linked to the Discord server: "
					+ bot.getGuildById(Main.id).getName()).queue();
			channel.sendMessage("The code that needs to be entered on the server is: " + code).queue();
			channel.sendMessage("To enter this code you must issue the command /dc link " + code).queue();
			switch (type.charAt(0)) {
			case 's':
				temp = "seconds";
				break;
			case 'd':
				temp = "day";
				break;
			case 'h':
				temp = "hour";
				break;
			case 'm':
			default:
				temp = "minute";
				break;
			}
			channel.sendMessage("This code will expire in " + time + " " + temp + "(s)");
		});
		saveXML();
		return id;
	}

	private ZonedDateTime timeoutDate(ZonedDateTime date) {
		ZonedDateTime date2 = date;
		long timeoutTime = (long) Main.timeoutLength;
		char timeoutType = Main.timeoutType.charAt(0);
		switch (timeoutType) {
		case 's':
			return date2.plusSeconds(timeoutTime);
		case 'd':
			return date2.plusDays(timeoutTime);
		case 'h':
			return date2.plusHours(timeoutTime);
		case 'm':
			return date2.plusMinutes(timeoutTime);
		default:
			return date2.plusMinutes(timeoutTime);
		}
	}

	private void saveXML() {
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer;
		try {
			transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(players);
			transformer.transform(source, result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public int unlink(UUID uuid, CommandSender sender) {
		NodeList list = root.getElementsByTagName(uuid.toString());
		int success = 0;
		if (list.getLength() == 1) {
			Element e = (Element) list.item(0);
			Element discord = (Element) e.getElementsByTagName("discord").item(0);
			String discordID = discord.getElementsByTagName("id").item(0).getTextContent();
			Main.bot.getUserById(discordID).openPrivateChannel().queue(channel -> {
				channel.sendMessage("You have unlinked your account with your minecraft account on the discord server: "
						+ Main.bot.getGuildById(Main.id).getName()).queue();
			});
			root.removeChild(e);
			success = 1;
		}
		saveXML();
		return success;
	}
	
	public long getDiscordID(UUID uuid) {
		NodeList list = root.getElementsByTagName(uuid.toString());
		if (list.getLength() == 1) {
			Element e = (Element) list.item(0);
			Element discord = (Element) e.getElementsByTagName("discord").item(0);
			String discordID = discord.getElementsByTagName("id").item(0).getTextContent();
			return Long.parseLong(discordID);
		}
		
		return 0L;
	}
	
	public static HashMap<UUID, Long> players(Plugin plugin){

		File playersFile;
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		Document doc = null;
		Element root = null;

		playersFile = new File(plugin.getDataFolder() + "/players.xml");
		if (!playersFile.exists()) {
			try {
				playersFile.createNewFile();
				FileWriter w = new FileWriter(playersFile);
				BufferedWriter bw = new BufferedWriter(w);
				bw.write("<?xml version = \"1.0\"?>\r\n" + "<!-- DO NOT MESS WITH THIS FILE -->\r\n"
						+ "<!-- MESSING WITH THIS FILE HAS -->\r\n" + "<!-- THE POTENTIAL TO BREAK THE PLUGIN -->\r\n"
						+ "<players></players>");
				bw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else {
			try {
				builder = factory.newDocumentBuilder();
				doc = builder.parse(playersFile);
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			root = doc.getDocumentElement();
		}
		
		HashMap<UUID, Long> players = new HashMap<UUID, Long>();
		int count = root.getChildNodes().getLength();
		for(int i = 0; i < count; i++) {
			Element list = (Element) root.getChildNodes().item(i);
			Element disc = (Element) list.getElementsByTagName("discord").item(0);
			Long dID = Long.parseLong(disc.getElementsByTagName("id").item(0).getTextContent());
			players.put(UUID.fromString(list.getNodeName()),dID);
		}
		return players;
	}
}
