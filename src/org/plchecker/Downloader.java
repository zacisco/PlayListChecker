package org.plchecker;

import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.plchecker.utils.Utils;

public class Downloader {
	private Map<String, ArrayList<String>> urls = new TreeMap<>();
	private Checker checker;

	public Downloader(Checker checker) {
		this.checker = checker;
		ArrayList<String> lists = Utils.getLists(Utils.urlFolder);
		for (String fpath : lists) {
			String listName = Utils.getListName(fpath);
			if (!urls.containsKey(listName)) {
				urls.put(listName, Utils.readFile(fpath));
			}
		}
		download();
	}

	public Downloader(String url, String listName, Checker checker) {
		this.checker = checker;
		start(url, listName);
	}

	private void download() {
		for (Entry<String, ArrayList<String>> entry : urls.entrySet()) {
			String curList = entry.getKey();
			System.out.println("START LISTS: " + curList);
			for (String url : entry.getValue())
				start(url, curList);
		}
	}

	private void start(String url, String listName) {
		if (Utils.downloadList(url)) {
			System.out.println("PARSE AND CHECK: " + url);
			checker.check(listName);
		}
	}
}
