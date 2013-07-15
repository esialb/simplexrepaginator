package com.simplexrepaginator;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.swing.JOptionPane;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOCase;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.lang.StringUtils;
import org.apache.pdfbox.pdmodel.PDDocument;

public class FileRepaginator extends Repaginator {
	public static final IOFileFilter IS_PDF_FILE = new SuffixFileFilter("pdf", IOCase.INSENSITIVE);

	protected List<File> inputFiles = Collections.emptyList();
	protected List<File> outputFiles = Collections.emptyList();

	protected Collection<File> findPdfs(File f) {
		if(f.isFile() && IS_PDF_FILE.accept(f))
			return Collections.singleton(f);
		else if(f.isDirectory())
			return FileUtils.listFiles(f, IS_PDF_FILE, TrueFileFilter.INSTANCE);
		else
			return Collections.emptySet();
	}

	protected List<File[]> computeFilePairs() {
		List<File[]> ret = new ArrayList<File[]>();
		if(inputFiles.size() > 0 && outputFiles.size() == 1 && outputFiles.get(0).isDirectory()) {
			File out = outputFiles.get(0);
			for(File in : inputFiles) {
				for(File pdf : findPdfs(in)) {
					ret.add(new File[] {pdf, new File(out, pdf.getName())});
				}
			}
		} else if(inputFiles.size() == outputFiles.size()) {
			Iterator<File> ifi = inputFiles.iterator();
			Iterator<File> ofi = outputFiles.iterator();
			while(ifi.hasNext() && ofi.hasNext()) {
				File iFile = ifi.next();
				File oFile = ofi.next();
				if(iFile.isFile() && oFile.isFile()) {
					if(IS_PDF_FILE.accept(iFile) && IS_PDF_FILE.accept(oFile))
						ret.add(new File[] {iFile, oFile});
				} else if(iFile.isDirectory() && oFile.isDirectory()) {
					for(File pdf : findPdfs(iFile)) {
						ret.add(new File[] {pdf, new File(oFile, pdf.getName())});
					}
				} else throw new RuntimeException("Mismatched file-file/directory-directory: " + iFile + " and " + oFile);
			}
		} else {
			throw new RuntimeException("Not multiple input files with output file a directory and input file list differing in size from output file list.");
		}
		return ret;
	}

	public int[] repaginate() throws RepaginatorException {
		RepaginatorException rex = new RepaginatorException("Unable to repaginate");
		
		List<File[]> pairs;
		try {
			pairs = computeFilePairs();
		} catch(RuntimeException re) {
			throw rex.addCause(re);
		}
		
		int documents = 0;
		int pages = 0;
		
		for(File[] io : pairs) {
			File in = io[0];
			File out = io[1];

			try {
				PDDocument doc = PDDocument.load(in);
				PDDocument rdoc = repaginated(doc);
				rdoc.save(out);
				pages += doc.getNumberOfPages();
				rdoc.close();
				doc.close();
				documents++;
			} catch(Exception ex) {
				rex.addCause(ex);
			}
		}

		if(rex.getCauses().size() > 0)
			throw rex;
		
		return new int[] {documents, pages};
	}

	public int[] unrepaginate() throws RepaginatorException {
		RepaginatorException rex = new RepaginatorException("Unable to unrepaginate");
		
		List<File[]> pairs;
		try {
			pairs = computeFilePairs();
		} catch(RuntimeException re) {
			throw rex.addCause(re);
		}
		
		int documents = 0;
		int pages = 0;
		
		for(File[] io : pairs) {
			File in = io[0];
			File out = io[1];

			try {
				PDDocument doc = PDDocument.load(in);
				PDDocument rdoc = unrepaginated(doc);
				rdoc.save(out);
				pages += doc.getNumberOfPages();
				rdoc.close();
				doc.close();
				documents++;
			} catch(Exception ex) {
				rex.addCause(ex);
			}
		}

		if(rex.getCauses().size() > 0)
			throw rex;
		
		return new int[] {documents, pages};
	}

	public List<File> getInputFiles() {
		return Collections.unmodifiableList(inputFiles);
	}

	public void setInputFiles(List<File> inputFiles) {
		this.inputFiles = new ArrayList<File>(inputFiles);
	}

	public List<File> getOutputFiles() {
		return Collections.unmodifiableList(outputFiles);
	}

	public void setOutputFiles(List<File> outputFiles) {
		this.outputFiles = new ArrayList<File>(outputFiles);
	}

}
