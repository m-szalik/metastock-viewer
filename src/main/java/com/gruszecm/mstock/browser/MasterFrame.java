package com.gruszecm.mstock.browser;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.swing.JDesktopPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ProgressMonitor;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import com.mac.verec.datafeed.metastock.Reader;
import com.mac.verec.models.Instrument;
import com.mac.verec.models.NumberDate;

public class MasterFrame extends AbstractBrowserFrame implements MouseListener, Comparable<MasterFrame>{

	private MasterTabModel model;
	private Reader reader;
	private File file;
	
	public Reader getReader() {
		return reader;
	}
	
	public File getFile() {
		return file;
	}
	
	public MasterFrame(File dir, File file, JDesktopPane desktopPane, ProgressMonitor progressMonitor) throws IOException {
		super(null, desktopPane);
		this.file = file;
		setTitle("Index of " + file.getAbsolutePath());
		dir.mkdirs();
		System.out.println("Temporary directory " + dir);
		FileUtils.writeStringToFile(new File(dir, ".info.txt"), "Data from " + file.getAbsolutePath());
		ZipInputStream stream = new ZipInputStream(new FileInputStream(file));
		progressMonitor.setNote("Extracting files...");
		try {
			ZipEntry ze;
			int processc = progressMonitor.getMaximum()/2;
			while ((ze=stream.getNextEntry()) != null) {
				processc ++;
				File f = new File(dir, ze.getName());
				FileOutputStream out = new FileOutputStream(f);
				IOUtils.copy(stream, out);
				progressMonitor.setProgress(processc);
			}
		} finally {
			IOUtils.closeQuietly(stream);
		}
		Reader reader = new Reader(dir.getAbsolutePath() + "/", true, progressMonitor);
		model = new MasterTabModel(reader);
		JTable table = new JTable(model);
		table.addMouseListener(this);
		table.setDefaultRenderer(Date.class, new DateRenderer("yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss"));
		table.setDefaultRenderer(NumberDate.class, new DateRenderer("yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss"));
		table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		table.setAutoCreateRowSorter(true);
		JScrollPane pane = new JScrollPane(table);
		getContentPane().add(pane);
		setVisible(true);
		setFrameIconByName("index.gif");
		pack();
		this.reader = reader;
	}



	private static final long serialVersionUID = -3281351392506584562L;

	public void mouseClicked(MouseEvent e) {
		if (e.getClickCount() > 1 && e.getSource() instanceof JTable) {
			JTable tab = (JTable) e.getSource();
			int index = tab.getSelectedRow();
			Instrument instrument = model.getInstrument(index);
			DataFrame dataFrame = new DataFrame(this, desktop, instrument);
			desktop.add(dataFrame);
		}
	}
	public void mouseEntered(MouseEvent e) {	
	}
	public void mouseExited(MouseEvent e) {	
	}
	public void mousePressed(MouseEvent e) {
	}
	public void mouseReleased(MouseEvent e) {
	}
	public int compareTo(MasterFrame o) {
		return this.file.compareTo(o.file);
	}

}
