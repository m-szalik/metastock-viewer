package com.gruszecm.mstock.browser;

import org.apache.commons.io.IOUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class ErrorRaportDialog extends JDialog implements ActionListener {
	private JButton buttonOK = new JButton("OK");
	private JButton buttonSave = new JButton("Save error log");
	private List<ErrorRecord> errors;
	private File archive;
	
	public ErrorRaportDialog(JFrame parent, File archive, List<ErrorRecord> errorRecords) {
		super(parent, "Errors in " + archive.getName(), true);
		this.errors = errorRecords;
		this.archive = archive;
		if (parent != null) {
			Dimension parentSize = parent.getSize();
			setSize(parentSize);
			Point p = parent.getLocation();
			setLocation(p.x + parentSize.width / 4, p.y + parentSize.height / 4);
		}
		JPanel messagePane = new JPanel();
		JList jlist = new JList(errorRecords.toArray());
		jlist.setAutoscrolls(true);
		messagePane.add(jlist);
		getContentPane().setLayout(new GridLayout(3, 1));
		getContentPane().add(messagePane);
		
		JPanel buttonPane = new JPanel();
		buttonPane.add(new JLabel("You can help me to inprove this software\nby sending the log file to szalik@jsoftware.org."));
		buttonPane.add(buttonSave);
		buttonSave.addActionListener(this);
		getContentPane().add(buttonPane);
		
		JPanel buttonPane2 = new JPanel();
		buttonPane2.add(buttonOK);
		buttonOK.addActionListener(this);
		getContentPane().add(buttonPane2);
		
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		pack();
		setVisible(true);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == buttonOK) {
			setVisible(false);
			dispose();
		}
		if (e.getSource() == buttonSave) {
		    JFileChooser fileChooser = new JFileChooser(System.getProperty("user.dir")); 
		    fileChooser.setSelectedFile(new File("metastockViewer-log.xml"));
		    switch (fileChooser.showSaveDialog(this)) {
			case JFileChooser.APPROVE_OPTION:
				File file = fileChooser.getSelectedFile();
				if (file != null) {
					try {
						saveLogToFile(file);
					} catch (Exception ex) {
						JOptionPane.showMessageDialog(this, "Cannot save log file - " + ex, "Error!", JOptionPane.ERROR_MESSAGE);
						ex.printStackTrace();
					}
				}
				break;
			default:
				break;
			}
		    
		}
		
	}

	private void saveLogToFile(File file) throws IOException {
		FileWriter fw = new FileWriter(file);
		fw.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n");
		fw.append("<log>\n");
		fw.append("\t<evn>\n");
			for(Object key : System.getProperties().keySet()) {
				if (key == null) {
					continue;
				}
				Object val = System.getProperty(key.toString());
				fw.append("\t\t<entry><key>" + key + "</key><value>" + val + "</value></entry>\n");
			}
		fw.append("\t</evn>\n");
		fw.append("\t<entries>\n");
		for(ErrorRecord er : errors) {
			fw.append("\t\t<entry type=\"").append(er.getType().name()).append("\">\n");
			fw.append("\t\t\t<message>").append(er.getMessage()).append("</message>\n");
			fw.append("\t\t\t<stacktrace>\n").append(er.getStacktraceAsString()).append("\n</stacktrace>\n");
			fw.append("\t\t</entry>\n");
		}
		fw.append("\t</entries>\n");
		fw.append("\t<data file=\"" + archive.getName() + "\"><![CDATA[");
		try {
			IOUtils.copy(new FileInputStream(archive), fw);
		} catch (Exception exx) {
			fw.append("problem ").append(exx.toString());
		}
		fw.append("]]></data>\n");
		fw.append("</log>\n");
		fw.close();
	}

	private static final long serialVersionUID = 4238399989390123213L;

}
