package com.simplexrepaginator;

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

}
