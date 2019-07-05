package org.plchecker.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.stream.Stream;

import org.plchecker.objs.EntryChannel;

public class Utils {
	public static final String userAgent = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/72.0.3626.122 Safari/537.36";

	private static final String unk = "Unknown";
	private static final String regExpBegin = ".*(?:";
	private static final String regExpEnd = ").*";

	public static final String urlFolder = "lists";
	public static final String loadedLists = "custom";

	private static final String channelsFile = "channelGroups.txt";
	private static final String tmpFile = "tmp.m3u";
	private static final String dstListFile = "list-ready.m3u";

	private static final String extm3u = "#EXTM3U";
	private static final String extinf = "#EXTINF: -1 group-title=";

	private static final String movieYearTemplate = regExpBegin + "\\(\\d+\\)" + regExpEnd;
	private static final String movieWordsTemplate = regExpBegin
			+ "(?:kino)"
			+ "|(?:film)"
			+ "|(?:кино)"
			+ "|(?:фильм)"
		+ regExpEnd;
	private static final String serialsTemplate = regExpBegin
			+ "(?:[Ss](?:eason)?(?:[_\\-.])?\\d+)"
			+ "|(?:[Ee](?:p(?:isode)?)?(?:[_\\-.])?\\d+)"
			+ "|(?:\\d+[Xx]\\d+)"
			+ "|(?:[Сс](?:(?:езон)|(?:ерия)))"
		+ regExpEnd;

	private static final String xxxTemplate1 = ".*VOD.*";
	private static final String xxxTemplate2 = ".*[v|V]ideo.*";
	private static final String xxxTemplate3 = ".*(?:Other - ).*";
	private static final String xxxTemplate4 = ".*dlfr\\d+\\.cdntlsv\\.com.*";

	private static final String exceptUrl = regExpBegin
			+ "promoslynetiptv"
			+ "|promoslynet"
			+ "|gsnpromoiptv"
			+ "|onlypremiumiptv"
			+ "|1FwLpEb|kraslan\\.ru"
			+ "|krasview\\.ru"
			+ "|(?:msk\\d?\\.)?peers\\.tv"
			+ "|vs\\d+\\.cdn\\.ott\\.otautv\\.kz"
			+ "|FreeSlyNet IPTV$|545-tv\\.com"
			+ "|PREMIUM/NO/KANAL"
//			+ "|session="
			+ "|kazaktelekom\\.org"
			+ "|http://live\\.slynet\\.tv/promoslynetiptv1\\?\\.m3u8"
			+ "|live\\.planeta-online\\.tv:1935/public/channel_5"
		+ regExpEnd;
	private static final String exceptTitle = regExpBegin
			+ "знакомства"
			+ "|Обновл"
			+ "|Группа"
			+ "|Киноменю"
			+ "|UA"
			+ "|KZ"
		+ regExpEnd;

	private static TreeMap<String, ArrayList<EntryChannel>> channelsList = Utils.readChannelsList();
	private static ArrayList<String> forDublicates = new ArrayList<>();
	private static String writeRule;

	public static void setWriteRule(String rule) {
		writeRule = rule;
	}

	public static ArrayList<String> getLists(String dir) {
		ArrayList<String> list = new ArrayList<>();
		try (Stream<Path> files = Files.walk(Paths.get(dir))) {
			files.filter(Files::isRegularFile)
			.forEach(p -> list.add(p.toString()));
			Collections.sort(list);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return list;
	}

	public static TreeMap<String, ArrayList<EntryChannel>> readChannelsList() {
		TreeMap<String, ArrayList<EntryChannel>> list = new TreeMap<>();
		try (BufferedReader reader = new BufferedReader(new FileReader(channelsFile))) {
			while (true) {
				String line = reader.readLine();
				if (line == null)
					break;
				if (line.equals("XXL"))
					continue;
				list.put(line, null);
			}
			list.put("Movies", null);
			list.put("Serials", null);
			list.put("World", null);
			list.put("XXX", null);
			list.put("Other", null);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return list;
	}

	public static ArrayList<String> readFile(String fileName) {
		ArrayList<String> list = new ArrayList<>();
		try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
			while (true) {
				String line = reader.readLine();
				if (line == null)
					break;
				if (line.isEmpty() || line.startsWith("#") || line.startsWith("//"))
					continue;
				list.add(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return list;
	}

//	public static ArrayList<EntryChannel> readList2Array(String fileName) {
//		ArrayList<EntryChannel> list = new ArrayList<>();
//		try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
//			EntryChannel ch = null;
//			String line = "";
//			while (line != null) {
//				line = reader.readLine();
//				if (line == null || line.isEmpty() || line.equals("#EXTM3U") || line.contains("#EXTGRP"))
//					continue;
//				if (ch == null)
//					ch = new EntryChannel();
//				if (line.startsWith("#EXTINF:"))
//					ch.setTitle(line.substring(line.indexOf(',') + 1).trim());
//				else if (line.startsWith("http") && allowUrls(line)) {
//					if (ch.getTitle() == null || ch.getTitle().isEmpty()) {
//						ch.setTitle(unk);
//						System.out.println("TITLE NULL: " + ch.getUrl());
//					}
//					ch.setUrl(line);
//					list.add(ch);
//					ch = null;
//				}
//			}
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		return list;
//	}

	public static boolean downloadList(String address) {
		URL url = null;
		try {
			url = new URL(address);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		if (url != null) {
			ReadableByteChannel readableByteChannel = null;
			FileOutputStream fileOutputStream = null;
			FileChannel fileChannel = null;
			try {
				URLConnection connection = url.openConnection();
				connection.setRequestProperty("user-agent", Utils.userAgent);
				String redirect = connection.getHeaderField("Location");
				if (redirect != null)
					connection = new URL(redirect).openConnection();
				readableByteChannel = Channels.newChannel(connection.getInputStream());
				fileOutputStream = new FileOutputStream(tmpFile);
				fileChannel = fileOutputStream.getChannel();
				fileChannel.transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
				return true;
			} catch (IOException e) {
				System.err.println("Error: " + e.getCause() == null ? address : e.getCause());
//				e.printStackTrace();
			} finally {
				allClose(readableByteChannel, fileOutputStream, fileChannel);
			}
		}
		return false;
	}

	public static boolean prepList(String file) {
		try {
			Files.copy(Paths.get(file), Paths.get(tmpFile), StandardCopyOption.REPLACE_EXISTING);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static HashMap<String, String> readList2Map() {
		HashMap<String, String> list = new HashMap<>();
		try (BufferedReader reader = new BufferedReader(new FileReader(tmpFile))) {
			String line = "";
			String title = null;
			while (line != null) {
				line = reader.readLine();
				if (line == null || line.isEmpty() || line.startsWith("//") || line.equals(extm3u) || line.contains("#EXTGRP"))
					continue;
				if (line.startsWith("#EXTINF:")) {
					title = line.substring(line.indexOf(',') + 1).trim();
					if (title.contains("RU: "))
						title.replaceFirst("RU: ", "");
				} else if (line.startsWith("http") && allowUrls(line)) {
					line = line.trim();
					if (title == null || title.isEmpty()) {
						title = unk;
					}
					if (!list.containsKey(line) || (list.get(line).equals(unk) && !title.equals(unk)))
						list.put(line, title);
					title = null;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return list;
	}

	public static boolean checkEro(String str) {
		if (str.contains("18+") || str.contains("+18") || str.contains("sex") || checkMatch(str, "sex") || str.contains("brazzers") || checkMatch(str, "brazzers") || str.contains("xxx") || checkMatch(str, "xxx")
		|| checkMatch(str, "adult") || str.matches(xxxTemplate1) || str.matches(xxxTemplate2) || str.matches(xxxTemplate3) || str.matches(xxxTemplate4) || checkMatch(str, "stream")
		|| checkMatch(str, "Girl") || str.contains("Ero") || str.contains("Erotic") || checkMatch(str, "porn") || str.contains("Babestation") || str.contains("Jasmin") || str.contains("Miami")
		|| checkMatch(str, "Visit-X") || str.contains("porn") || str.contains("www.flagras.blog.br") || str.contains("rubateen") || str.contains("cheerleader") || str.contains("Hustler")
		|| str.contains("drainmainvein"))
			return true;
		return false;
	}

	public static boolean checkMatch(String str, String substr) {
		return str.toLowerCase().contains(substr);
	}

	public static String getListName(String str) {
		String name = null;
			if (str.endsWith("txt"))
				name = str.substring(str.lastIndexOf('/') + 1, str.indexOf(".txt"));
			else if (str.endsWith("m3u"))
				name = str.substring(str.indexOf('/') + 1, str.lastIndexOf('/'));
		return name;
	}

	public static boolean allowTitle(String title) {
		return !title.matches(exceptTitle);
	}

	public static boolean allowUrls(String address) {
		return !address.matches(exceptUrl);
	}

	public static boolean isDublicate(String url) {
		return forDublicates.contains(url);
	}

	public static void prepChannelsList(EntryChannel ech, String group) {
		String set = "Other";
		String title = ech.getTitle().toLowerCase();
		if (group.equals("XXX") || group.equals("World") || group.equals("Movies") || group.equals("Serials"))
			setGroup(ech, group);
		else if (ech.getTitle().matches(movieYearTemplate) || ech.getTitle().matches(movieWordsTemplate) || ech.getUrl().contains("movie"))
			setGroup(ech, "Movies");
		else if (ech.getTitle().toLowerCase().matches(serialsTemplate) || ech.getUrl().contains("serial"))
			setGroup(ech, "Serials");
		else {
			int title_length = title.length();
			if (title.equals("че"))
				setGroup(ech, "Че");
			else if (title.equals("Ю"))
				setGroup(ech, "Ю");
			else {
				for (String key : channelsList.keySet()) {
					String tmp = key.toLowerCase();
					if (tmp.equals("че") && tmp.equals("ю"))
						continue;
					if (title.equals(tmp) || (title_length < 5 && title.startsWith(tmp)) || title.contains(tmp)) {
						setGroup(ech, key);
						set = key;
						break;
					}
				}
				if (set.equals("Other"))
					setGroup(ech, set);
			}
		}
		forDublicates.add(ech.getUrl());
	}

	public static void setGroup(EntryChannel ech, String group) {
		ech.setTitle(extinf + "\"" + group + "\"," + ech.getTitle());
		ArrayList<EntryChannel> list = channelsList.get(group);
		if (list == null)
			list = new ArrayList<>();
		list.add(ech);
		channelsList.put(group, list);
	}

	public static void saveList() {
		System.out.println("SAVING CHANNELS LIST!");
		int count = 0;
		StringBuilder sb = new StringBuilder(writeRule.equals("owervrite") ? extm3u + "\n" : "");
		for (Entry<String, ArrayList<EntryChannel>> entry : channelsList.entrySet()) {
			ArrayList<EntryChannel> list = entry.getValue();
			if (list != null && !list.isEmpty()) {
				count += list.size();
				list.forEach(ch -> {sb.append(ch.getTitle()).append("\n").append(ch.getUrl()).append("\n");});
			}
		}
		writeFile(dstListFile, sb);
		System.out.println("TOTAL WORKING CHANNELS: " + count);
	}

	public static void writeFile(String fileName, StringBuilder content) {
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName))) {
			bw.write(content.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void deleteTmpList() {
		try {
			Files.delete(Paths.get(tmpFile));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void allClose(ReadableByteChannel readableByteChannel, FileOutputStream fileOutputStream, FileChannel fileChannel) {
		try {
			if (fileChannel != null)
				fileChannel.close();
			if (fileOutputStream != null)
				fileOutputStream.close();
			if (readableByteChannel != null)
				readableByteChannel.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
