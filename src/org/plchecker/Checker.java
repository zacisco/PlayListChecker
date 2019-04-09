package org.plchecker;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.Consumer;
import java.util.function.Function;

import org.plchecker.objs.EntryChannel;
import org.plchecker.objs.ReturnObject;
import org.plchecker.utils.Utils;

public class Checker implements Consumer<ReturnObject>, Function<Throwable, Void> {
	private ExecutorService executor;
	private boolean all = false;

	public Checker(int threads) {
		executor = Executors.newFixedThreadPool(threads);
	}

	public void setAll(boolean value) {
		this.all = value;
	}

	@Override
	public void accept(ReturnObject obj) {
		if (obj != null) {
			Integer code = obj.getCode();
			String message = obj.getMessage();
			String group = obj.getGroup();
			EntryChannel ech = obj.getChannel();
//			System.out.println("OBJ MSG: " + message + " (T: " + ech.getTitle() + ", URL: " + ech.getUrl() + "), OBJ GRP: " + group);
			if (code == 0) {
				System.out.println(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME) + " > " + message + " (T: " + ech.getTitle() + ", URL: " + ech.getUrl() + ", GRP: " + group + ")");
				Utils.prepChannelsList(ech, group);
			}
			if (((ThreadPoolExecutor) executor).getActiveCount() <= 1 && ((ThreadPoolExecutor) executor).getQueue().isEmpty()) {
				if (all) {
					System.out.println("START CUSTOM CHECKS");
					all = false;
					check();
				} else {
					Utils.saveList();
					System.out.println("WORK DONE!");
					System.exit(0);
				}
			}
		}

	}

	private void addTask(String url, String title, String group) {
		EntryChannel ech = new EntryChannel(title, url);
		CompletableFuture.supplyAsync(new Check(ech, group), executor).thenAccept(this).exceptionally(this);
	}

	public void check() {
		ArrayList<String> list = Utils.getLists(Utils.loadedLists);
		for (String fpath : list) {
			String listName = Utils.getListName(fpath);
			check(fpath, listName);
		}
	}

	public void check(String file, String group) {
		if (Utils.prepList(file))
			check(group);
	}

	public void check(String curList) {
		Utils.readList2Map().forEach((url, title) -> {
			if (Utils.allowTitle(title) && !Utils.isDublicate(url))
				addTask(url, title, curList);
		});
	}

	@Override
	public Void apply(Throwable t) {
		System.err.println("TH: " + t.getMessage());
		t.printStackTrace();
		return null;
	}
}
