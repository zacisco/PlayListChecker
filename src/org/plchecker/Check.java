package org.plchecker;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import org.plchecker.objs.EntryChannel;
import org.plchecker.objs.ReturnObject;

public class Check implements Supplier<ReturnObject> {
	private EntryChannel channel;
	private String group;

	public Check(EntryChannel channel, String group) {
		this.channel = channel;
		this.group = group;
	}

	@Override
	public ReturnObject get() {
		Process p = null;
		Runtime rt = Runtime.getRuntime();
		String message = "CHANNEL NOT AVAILABLE";
		int exitCode = -1;
		try {
			p = rt.exec("ffprobe -hide_banner " + channel.getUrl());
			if (!p.waitFor(5, TimeUnit.SECONDS))
				p.destroyForcibly();
			exitCode = p.waitFor();
//			exitCode = rt.exec("timeout --preserve-status 5 ffprobe -hide_banner " + channel.getUrl()).waitFor();
		} catch (Exception e) {
			if (p != null)
				p.destroyForcibly();
			e.printStackTrace();
		}
		if (exitCode == 0)
			message = "CHANNEL AVAILABLE";
		return new ReturnObject(channel, group, message, exitCode);
	}
}
