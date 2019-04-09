package org.plchecker.utils;

import com.google.devtools.common.options.Option;
import com.google.devtools.common.options.OptionsBase;

public class RunOptions extends OptionsBase {
	@Option(
		name = "help",
		abbrev = 'h',
		help = "Usage info.",
		defaultValue = "true"
	)
	public boolean help;

	@Option(
		name = "checktype",
		abbrev = 'c',
		help = "checking type.\n"
				+ "	ALL - checks \"lists\" directory (files with urls to playlists) and\n"
				+ "		\"custom\" directory (prepared playlists)\n"
				+ "	DOWNLOAD - checks \"lists\" directory ONLY\n"
				+ "	CUSTOM - checks \"custom\" directory ONLY\n"
				+ "	ONE - checks playlist from INPUTFILE (prepared playlist) or URL (prepared playlist)",
		defaultValue = ""
	)
	public String type;

	@Option(
		name = "write",
		abbrev = 'w',
		help = "set write rule to output file save available channels. [owervrite, append]",
		defaultValue = "owerwrite"
	)
	public String writeRule;

	@Option(
		name = "inputfile",
		abbrev = 'i',
		help = "path to input playlist file.",
		defaultValue = ""
	)
	public String inputfile;

	@Option(
		name = "url",
		abbrev = 'u',
		help = "url of playlist file.",
		defaultValue = ""
	)
	public String url;

	@Option(
		name = "outputfile",
		abbrev = 'o',
		help = "path to outputfile.",
		defaultValue = "list-ready.m3u"
	)
	public String outputfile;

	@Option(
		name = "group",
		abbrev = 'g',
		help = "group of list.\nIf group is Movie, Serial, World and XXX - all available channels will be in this group.",
		defaultValue = "General"
	)
	public String group;

	@Option(
		name = "threads",
		abbrev = 't',
		help = "number of threads for availability check",
		defaultValue = "10"
	)
	public int threads;
}
