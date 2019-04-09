package org.plchecker;

import java.util.Collections;

import com.google.devtools.common.options.OptionsParser;

import org.plchecker.utils.RunOptions;
import org.plchecker.utils.Utils;

public class PlayListChecker {
	public static void main(String[] args) {
		OptionsParser optionsParser = OptionsParser.newOptionsParser(RunOptions.class);
		optionsParser.parseAndExitUponError(args);
		RunOptions options = optionsParser.getOptions(RunOptions.class);
		if (options.type.isEmpty()) {
			printUsage(optionsParser);
			return;
		}

		Checker checker = new Checker(options.threads);
		Utils.setWriteRule(options.writeRule);
		String type = options.type.toUpperCase();
		String group = options.group;
		if (type.equals("ONE")) {
				if (!options.url.isEmpty())
					new Downloader(options.url, group, checker);
				else if(!options.inputfile.isEmpty())
					checker.check(options.inputfile, group);
		} else if (type.equals("DOWNLOAD"))
			new Downloader(checker);
		else if (type.equals("CUSTOM"))
			checker.check();
		else {
			checker.setAll(true);
			new Downloader(checker);
		}
	}

	private static void printUsage(OptionsParser parser) {
		System.out.println("Usage: java -jar plchecker.jar OPTIONS");
		System.out.println(parser.describeOptions(Collections.<String, String>emptyMap(), OptionsParser.HelpVerbosity.LONG));
	}
}
