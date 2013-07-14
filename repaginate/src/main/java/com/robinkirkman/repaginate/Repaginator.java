package com.robinkirkman.repaginate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;

public class Repaginator {
	public PDDocument repaginated(PDDocument doc) {
		List<PDPage> pages = new ArrayList<PDPage>();
		doc.getDocumentCatalog().getPages().getAllKids(pages);
		
		PDDocument ret;
		try {
			ret = new PDDocument();
		} catch(IOException ioe) {
			throw new RuntimeException("Unable to create PDDocument", ioe);
		}

		int front = 0;
		int back = pages.size() - 1;
		while(front < back) {
			ret.addPage(pages.get(front++));
			if(front < back)
				ret.addPage(pages.get(back--));
		}
		
		return ret;
	}
	
	public PDDocument unrepaginated(PDDocument doc) {
		List<PDPage> pages = new ArrayList<PDPage>();
		doc.getDocumentCatalog().getPages().getAllKids(pages);
		
		List<PDPage> fronts = new ArrayList<PDPage>();
		List<PDPage> backs = new ArrayList<PDPage>();
		
		int front = 0;
		int back = pages.size() - 1;
		while(front < back) {
			fronts.add(pages.get(front++));
			if(front < back)
				backs.add(pages.get(back--));
		}
		
		PDDocument ret;
		try {
			ret = new PDDocument();
		} catch(IOException ioe) {
			throw new RuntimeException("Unable to create PDDocument", ioe);
		}
		
		for(PDPage page : fronts) {
			ret.addPage(page);
		}
		for(PDPage page : backs) {
			ret.addPage(page);
		}
		
		return ret;
	}
}
