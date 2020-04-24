package com.gmail.gabezter.Main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.bukkit.plugin.Plugin;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Ranks {

	private Plugin plugin;
	private File ranks;
	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	DocumentBuilder builder = null;
	Document doc;
	Element root;

	public Ranks(Plugin plugin) {
		this.plugin = plugin;
		ranks = new File(plugin.getDataFolder(), "ranks.xml");
		if (!ranks.exists()) {
			try {
				ranks.createNewFile();
				FileWriter w = new FileWriter(ranks);
				BufferedWriter bw = new BufferedWriter(w);
				bw.write("<?xml version = \"1.0\"?>\r\n" + "<!-- DO NOT MESS WITH THIS FILE -->\r\n"
						+ "<!-- MESSING WITH THIS FILE HAS -->\r\n" + "<!-- THE POTENTIAL TO BREAK THE PLUGIN -->\r\n"
						+ "<ranks></ranks>");
				bw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else {
			try {
				builder = factory.newDocumentBuilder();
				doc = builder.parse(ranks);
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

	public int addRank(String minecraft, String discord) {
		boolean found = false;
		int success = -1;
		NodeList list = root.getElementsByTagName("rank");
		for (int i = 0; i < list.getLength(); i++) {
			Node node = list.item(i);
			Element e = (Element) node;
			if (e.getElementsByTagName("minecraft").item(0).getTextContent().equals(minecraft)
					|| e.getElementsByTagName("discord").item(0).getTextContent().equals(discord)) {
				found = true;
				success = 0;
				break;
			}
		}
		if (!found) {
			Element base = doc.createElement("rank");
			Element discordE = doc.createElement("discord");
			Element minecraftE = doc.createElement("minecraft");
			discordE.appendChild(doc.createTextNode(discord));
			minecraftE.appendChild(doc.createTextNode(minecraft));
			base.appendChild(minecraftE);
			base.appendChild(discordE);
			root.appendChild(base);
			success = 1;
		}
		saveXML();
		return success;
	}

	public int removeRank(String minecraft, String discord) {
		int success = 0;
		NodeList list = root.getElementsByTagName("rank");
		for (int i = 0; i < list.getLength(); i++) {
			Node node = list.item(i);
			Element e = (Element) node;
			if (e.getElementsByTagName("minecraft").item(0).getTextContent().equals(minecraft)
					|| e.getElementsByTagName("discord").item(0).getTextContent().equals(discord)) {
				root.removeChild(node);
				success = 1;
				break;
			}
		}
		saveXML();
		return success;
	}

	public ArrayList<String[]> listRanks() {
		ArrayList<String[]> ranks = new ArrayList<String[]>();
		NodeList list = root.getElementsByTagName("rank");
		for (int i = 0; i < list.getLength(); i++) {
			Node node = list.item(i);
			Element e = (Element) node;
			String[] rank = { e.getElementsByTagName("minecraft").item(0).getTextContent(),
					e.getElementsByTagName("discord").item(0).getTextContent() };
			ranks.add(rank);
		}
		return ranks;
	}

	private void saveXML() {
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer;
		try {
			transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(ranks);
			transformer.transform(source, result);
			StreamResult consoleResult = new StreamResult(System.out);
			transformer.transform(source, consoleResult);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
