package com.simplexrepaginator;

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;

import javax.swing.JFrame;
import javax.swing.UIManager;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

/**
 * Main entry point.
 * @author robin
 *
 */
public class Repaginate {
	private static final Options opt = new Options();
	static {
		opt.addOption("h", "help", false, "Show this help.");
		opt.addOption("o", "out", true, "Output file/directory.  May be repeated, or separated with " + File.pathSeparator);
		opt.addOption("b", "batch", false, "Perform batch processing, without the GUI.");
		opt.addOption("u", "unrepaginate", false, "In batch mode, unrepaginate rather than repaginate.");
		opt.addOption("q", "quiet", false, "Quiet operation.  In batch mode, will produce no console output.");
	}

	public static void main(String[] args) {
		try {
			System.exit(_main(args));
		} catch(Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	private static int _main(String[] args) throws Exception {
		// use system look and feel.  Shouldn't fail.
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

		// parse the command line
		CommandLine cli = new PosixParser().parse(opt, args);

		if(cli.hasOption("help")) {
			new HelpFormatter().printHelp("java -jar SimplexRepaginator-" + getVersion() + ".jar [options] [inputfile...]", "Simplex Repaginator", opt, "(c) 2013 Robin Kirkman");
			return 0;
		}

		FileRepaginator repaginator = new FileRepaginator();
		
		// input paths
		List<File> input = new ArrayList<File>();
		// output paths
		List<File> output = new ArrayList<File>();

		if(cli.getArgs().length > 0) {
			// Parse input paths, either as separate args or separated with the path separator
			for(String inArg : cli.getArgs()) {
				for(String in : inArg.split(Pattern.quote(File.pathSeparator))) {
					input.add(new File(in));
				}
			}
			repaginator.setInputFiles(input);
		}

		if(cli.getOptionValues("out") != null) {
			// parse output paths, either as separate args or separated with the path separator
			for(String outArg : cli.getOptionValues("out")) {
				for(String out : outArg.split(Pattern.quote(File.pathSeparator))) {
					output.add(new File(out));
				}
			}
			repaginator.setOutputFiles(output);
		} else if(input.size() > 0) {
			// there were no output paths but were input paths, so use the inputs as the outputs
			repaginator.setOutputFiles(input);
		}

		if(!cli.hasOption("batch") && !GraphicsEnvironment.isHeadless()) {
			// Frame which overrides #dispose() to exit
			RepaginateFrame frame = new RepaginateFrame(repaginator) {
				@Override
				public void dispose() {
					super.dispose();
					System.exit(0);
				}
			};

			frame.setVisible(true);
		} else {
			if(input.size() == 0) {
				if(!cli.hasOption("quiet"))
					System.out.println("No input files.  Try --help");
				return 0;
			}
			
			int count;
			try {
				if(cli.hasOption("unrepaginate"))
					count = repaginator.unrepaginate();
				else
					count = repaginator.repaginate();
			} catch(RepaginatorException re) {
				if(!cli.hasOption("quiet"))
					System.out.println("Exceptions during command processing:\n\t" + StringUtils.join(re.getCauses(), "\n\t"));
				return -1;
			} catch(Exception ex) {
				if(!cli.hasOption("quiet"))
					System.out.println("Exception during command processing:\n\t" + ex);
				return -1;
			}
			if(!cli.hasOption("quiet"))
				System.out.println("Processed " + count + " documents");
		}
		
		return 0;
	}

	/**
	 * Return the current version of Simplex Repaginator as a string
	 * @return
	 */
	public static String getVersion() {
		try {
			return IOUtils.toString(Repaginate.class.getResource("version.txt"));
		} catch(IOException ioe) {
		}
		return "Unknown Version";
	}

}
