package com.simplexrepaginator;

import java.io.IOException;
import java.util.Properties;

import javax.swing.JFrame;
import javax.swing.UIManager;

public class Repaginate {

	public static void main(String[] args) {
		try {
			_main(args);
		} catch(Exception ex) {
			throw new RuntimeException(ex);
		}
	}
	
	private static void _main(String[] args) throws Exception {
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		
		RepaginateFrame frame = new RepaginateFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

	public static String getVersion() {
		Properties p = new Properties();
		try {
			p.load(Repaginate.class.getResourceAsStream("version.properties"));
		} catch(IOException ioe) {
		}
		return p.getProperty("version", "Unknown Version");
	}
	
}
