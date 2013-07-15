package com.simplexrepaginator;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Comparator;

import org.apache.commons.io.IOUtils;

public class UpdateChecker {
	public static final Comparator<String> VERSION_ORDER = new Comparator<String>() {
		@Override
		public int compare(String o1, String o2) {
			o1 = o1.replaceAll("[^\\d\\.]", "");
			o2 = o2.replaceAll("[^\\d\\.]", "");
			String[] v1 = o1.split("\\.");
			String[] v2 = o2.split("\\.");
			
			for(int i = 0; i < v1.length && i < v2.length; i++) {
				int vi1 = Integer.parseInt(v1[i]);
				int vi2 = Integer.parseInt(v2[i]);
				int cmp = ((Integer) vi1).compareTo(vi2);
				if(cmp != 0)
					return cmp;
			}
			
			if(v1.length != v2.length)
				return ((Integer) v1.length).compareTo(v2.length);
			
			return 0;
		}
	};
	
	public URL checkForUpdates() throws IOException {
		String latest = IOUtils.toString(new URL("http://www.simplexrepaginator.com/downloads/version.txt"));
		String current = Repaginate.getVersion();
		
		if(VERSION_ORDER.compare(current, latest) < 0)
			return new URL("http://www.simplexrepaginator.com/downloads/SimplexRepaginator-" + latest + ".jar");
		else
			return null;
	}
}
