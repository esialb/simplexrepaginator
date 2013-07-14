package com.robinkirkman.repaginate;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.TransferHandler;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOCase;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.lang.StringUtils;
import org.apache.pdfbox.pdmodel.PDDocument;

public class RepaginateFrame extends JFrame {
	private static IOFileFilter IS_PDF_FILE = new SuffixFileFilter("pdf", IOCase.INSENSITIVE);

	protected List<File> inputFiles = Collections.emptyList();
	protected JButton input;
	protected JButton repaginate;
	protected JButton unrepaginate;
	protected JButton output;
	protected List<File> outputFiles = Collections.emptyList();

	public RepaginateFrame() {
		super("Repaginate");

		input = createInputButton();
		repaginate = createRepaginateButton();
		unrepaginate = createUnrepaginateButton();
		output = creatOutputButton();

		setLayout(new GridBagLayout());

		GridBagConstraints c = new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(10, 10, 10, 10), 10, 10);

		c.gridwidth = 2; add(input, c);

		c.gridy++; c.gridwidth = 1; c.fill = GridBagConstraints.NONE; add(repaginate, c);
		c.gridx++; add(unrepaginate, c);

		c.gridy++; c.gridx = 0; c.gridwidth = 2; c.fill = GridBagConstraints.BOTH; add(output, c);

		pack();
		setSize(800, 400);
	}

	protected JButton createRepaginateButton() {
		JButton b = new JButton("<html><center>Click to<br>repaginate");

		b.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				repaginate();
			}
		});
		
		return b;
	}

	protected void repaginate() {
		Repaginator repaginator = new Repaginator();

		List<File[]> pairs;
		try {
			pairs = computeFilePairs();
		} catch(RuntimeException re) {
			JOptionPane.showMessageDialog(this, re);
			return;
		}
		
		List<Exception> exs = new ArrayList<Exception>();

		int count = 0;
		
		for(File[] io : pairs) {
			File in = io[0];
			File out = io[1];

			try {
				PDDocument doc = PDDocument.load(in);
				PDDocument rdoc = repaginator.repaginated(doc);
				rdoc.save(out);
				count++;
			} catch(Exception ex) {
				exs.add(ex);
			}
		}

		if(exs.size() > 0) {
			JOptionPane.showMessageDialog(this, StringUtils.join(exs, "\n"));
		}
		
		JOptionPane.showMessageDialog(this, "Repaginated " + count + " documents");
	}

	protected JButton createUnrepaginateButton() {
		JButton b = new JButton("<html><center>Click to<br>un-repaginate");

		b.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				unrepaginate();
			}
		});

		return b;
	}
	
	protected void unrepaginate() {
		Repaginator repaginator = new Repaginator();

		List<File[]> pairs;
		try {
			pairs = computeFilePairs();
		} catch(RuntimeException re) {
			JOptionPane.showMessageDialog(this, re);
			return;
		}
		
		List<Exception> exs = new ArrayList<Exception>();

		int count = 0;
		
		for(File[] io : pairs) {
			File in = io[0];
			File out = io[1];

			try {
				PDDocument doc = PDDocument.load(in);
				PDDocument rdoc = repaginator.unrepaginated(doc);
				rdoc.save(out);
				count++;
			} catch(Exception ex) {
				exs.add(ex);
			}
		}

		if(exs.size() > 0) {
			JOptionPane.showMessageDialog(this, StringUtils.join(exs, "\n"));
		}
		
		JOptionPane.showMessageDialog(this, "Repaginated " + count + " documents");
	}

	protected JButton createInputButton() {
		JButton b = new JButton("Click or drag to set input files");

		b.setTransferHandler(new InputButtonTransferHandler());
		
		b.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				chooser.setMultiSelectionEnabled(true);
				chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
				if(chooser.showOpenDialog(RepaginateFrame.this) != JFileChooser.APPROVE_OPTION)
					return;
				setInput(Arrays.asList(chooser.getSelectedFiles()));
			}
		});

		return b;
	}

	protected JButton creatOutputButton() {
		JButton b = new JButton("Click or drag to set output file");

		b.setTransferHandler(new OutputButtonTransferHandler());
		
		b.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				chooser.setMultiSelectionEnabled(false);
				chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
				if(chooser.showOpenDialog(RepaginateFrame.this) != JFileChooser.APPROVE_OPTION)
					return;
				setInput(Arrays.asList(chooser.getSelectedFiles()));
			}
		});

		return b;
	}

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

	public void setInput(List<File> files) {
		input.setText("<html><center>" + StringUtils.join(files, "<br>"));
		inputFiles = new ArrayList<File>(files);
		setOutput(files);
	}

	public void setOutput(List<File> files) {
		output.setText("<html><center>" + StringUtils.join(files, "<br>"));
		outputFiles = new ArrayList<File>(files);
	}

	protected class InputButtonTransferHandler extends TransferHandler {
		@Override
		public boolean canImport(TransferSupport support) {
			for(DataFlavor f : support.getDataFlavors()) {
				if(DataFlavor.javaFileListFlavor.equals(f))
					return true;
			}
			return false;
		}

		@Override
		public boolean importData(TransferSupport support) {
			try {
				setInput((List<File>) support.getTransferable().getTransferData(DataFlavor.javaFileListFlavor));
				return true;
			} catch(IOException ioe) {
			} catch(UnsupportedFlavorException ufe) {
			}
			return false;
		}
	}

	protected class OutputButtonTransferHandler extends TransferHandler {
		@Override
		public boolean canImport(TransferSupport support) {
			for(DataFlavor f : support.getDataFlavors()) {
				if(DataFlavor.javaFileListFlavor.equals(f))
					return true;
			}
			return false;
		}

		@Override
		public boolean importData(TransferSupport support) {
			try {
				setOutput((List<File>) support.getTransferable().getTransferData(DataFlavor.javaFileListFlavor));
				return true;
			} catch(IOException ioe) {
			} catch(UnsupportedFlavorException ufe) {
			}
			return false;
		}
	}
}

