package com.simplexrepaginator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;

import javax.swing.JFrame;
import javax.swing.UIManager;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.io.IOUtils;

public class Repaginate {
	private static final Options opt = new Options();
	static {
		opt.addOption("o", "out", true, "Output file/directory.  May be repeated, or separated with " + File.pathSeparator);
	}

	public static void main(String[] args) {
		try {
			_main(args);
		} catch(Exception ex) {
			throw new RuntimeException(ex);
		}
	}
	
	private static void _main(String[] args) throws Exception {
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		
		CommandLine cli = new PosixParser().parse(opt, args);
		
		RepaginateFrame frame = new RepaginateFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		List<File> input = new ArrayList<File>();
		List<File> output = new ArrayList<File>();

		if(cli.getArgs().length > 0) {
			for(String in : cli.getArgs()) {
				input.add(new File(in));
			}
			frame.setInput(input);
		}
		
		if(cli.getOptionValues("out") != null) {
			for(String outArg : cli.getOptionValues("out")) {
				for(String out : outArg.split(Pattern.quote(File.pathSeparator))) {
					output.add(new File(out));
				}
			}
			frame.setOutput(output);
		} else if(input.size() > 0) {
			frame.setOutput(new ArrayList<File>(input));
		}
		
		frame.setVisible(true);
	}

	public static String getVersion() {
		try {
			return IOUtils.toString(Repaginate.class.getResource("version.txt"));
		} catch(IOException ioe) {
		}
		return "Unknown Version";
	}
	
}
