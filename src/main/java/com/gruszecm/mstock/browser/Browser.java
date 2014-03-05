package com.gruszecm.mstock.browser;

import com.mac.verec.datafeed.metastock.Reader;
import org.apache.commons.io.FileUtils;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 * @author szalik
 */
public class Browser implements ActionListener, WindowListener {
    JMenu chartsMI = new JMenu("Charts");
    private JMenuItem open = new JMenuItem("Open...", GUIUtil.getIcon("open.png"));
    private JMenuItem exit = new JMenuItem("Exit", null);
    private JMenu recent = new JMenu("Recent");
    private JMenuItem test = new JMenuItem("Test");
    JMenuItem chart1 = new JMenuItem("OHLC");

    private Preferences prefs = Preferences.userNodeForPackage(this.getClass());

    static JFrame jFrame;
    private JFileChooser fileChooser;
    private File tmp;
    private JDesktopPane desktop;

    private Set<String> recentFiles;

    public Browser() {
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                cleanup();
            }
        }));
        chartsMI.setEnabled(false);
        recentFiles = new LinkedHashSet<String>();
        fileChooser = new JFileChooser(new File(prefs.get("path", ".")));
        fileChooser.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                String name = f.getName().toLowerCase();
                boolean meta = (name.endsWith(".zip") && name.startsWith("meta"));
                return f.isDirectory() || meta;
            }

            @Override
            public String getDescription() {
                return "metastock";
            }
        });
        File tmp = new File(System.getProperty("java.io.tmpdir", "/tmp"));
        this.tmp = new File(tmp, "metastockBrowser_" + System.currentTimeMillis());
        // add recent files
        for (int r = 0; r < prefs.getInt("recent", 0); r++) {
            String fn = prefs.get("recent_" + r, "");
            if (fn.length() == 0)
                continue;
            File f = new File(fn);
            if (f.canRead()) {
                addToRecent(f, false);
            }
        }

        jFrame = new JFrame("Metastock browser");	/* firebug ok */
        jFrame.setJMenuBar(getMenu());
        desktop = new JDesktopPane();
        jFrame.getContentPane().add(desktop);
        jFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        jFrame.setSize(new Dimension(600, 500));
        jFrame.addWindowListener(this);
        try {
            jFrame.setIconImage(ImageIO.read(getClass().getResourceAsStream("/icon/browser.gif")));
        } catch (Exception e) {
            e.printStackTrace();
        }
        jFrame.setVisible(true);
    }

    public static void main(String[] args) throws Exception {
        new Browser();
    }

    private JMenuBar getMenu() {
        JMenuBar menu = new JMenuBar();
        // open.set
        JMenu fileMI = new JMenu("File");
        fileMI.setMnemonic(KeyEvent.VK_F);
        fileMI.add(open);
        open.setMnemonic(KeyEvent.VK_O);
        open.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0));
        fileMI.add(recent);
        menu.add(fileMI);
        chartsMI.add(chart1);
        menu.add(chartsMI);
        ButtonGroup group = new ButtonGroup();
        JMenu filter = new JMenu("Filters");
        filter.setMnemonic(KeyEvent.VK_T);
        addFilterToMenu(filter, group, "none (no filter)", DataFilter.NONE, KeyEvent.VK_1);
        addFilterToMenu(filter, group, "all (all invalid)", DataFilter.ALL_INVALID, KeyEvent.VK_2);
        addFilterToMenu(filter, group, "one (one or more invalid)", DataFilter.ONE_INVALID, KeyEvent.VK_3);
        ((JRadioButtonMenuItem) group.getElements().nextElement()).setSelected(true);
        fileMI.add(new JSeparator());
        fileMI.add(exit);
        menu.add(filter);
        exit.addActionListener(this);
        exit.setMnemonic(KeyEvent.VK_X);
        open.addActionListener(this);
        final JMenu testMenu = new JMenu("Test");
        testMenu.addMenuListener(new MenuListener() {
            public void menuCanceled(MenuEvent e) {
            }

            public void menuDeselected(MenuEvent e) {
            }

            public void menuSelected(MenuEvent e) {
                testMenu.removeAll();
                List<MasterFrame> masterFrames = new ArrayList<MasterFrame>();
                for (Component co : desktop.getComponents()) {
                    if (co instanceof MasterFrame)
                        masterFrames.add((MasterFrame) co);
                }
                Collections.sort(masterFrames);
                if (masterFrames.isEmpty()) {
                    testMenu.add(new JLabel("No metastocks."));
                } else {
                    for (final MasterFrame mf : masterFrames) {
                        JMenuItem item = new JMenuItem(mf.getFile().getName());
                        item.addActionListener(new ActionListener() {
                            public void actionPerformed(ActionEvent e) {
                                TestFrame tf = new TestFrame(mf, desktop, mf.getReader());
                                tf.start();
                            }
                        });
                        testMenu.add(item);
                    } // for
                }
            }
        });
        menu.add(testMenu);
        JMenu help = new JMenu("Help");
        help.setMnemonic(KeyEvent.VK_H);
        JMenuItem aboutMI = new JMenuItem(new AbstractAction("About") {
            private static final long serialVersionUID = -470524606409942575L;

            public void actionPerformed(ActionEvent e) {
                Runnable runnable = new Runnable() {
                    public void run() {
                        ResourceBundle bundle = ResourceBundle.getBundle("messages");
                        JOptionPane.showMessageDialog(jFrame, "Metastock viewer and validator.\nhttp://jsoftware.org/metastock\nVersion " + bundle.getString("version"), "About...", JOptionPane.INFORMATION_MESSAGE);
                    }
                };
                EventQueue.invokeLater(runnable);
            }
        });
        help.add(aboutMI);
        aboutMI.setMnemonic(KeyEvent.VK_U);
        menu.add(Box.createHorizontalGlue());
        menu.add(help);
        return menu;
    }

    private void addFilterToMenu(JMenu addTo, ButtonGroup group, String label, final RowFilter<DataTableModel, Integer> filter, int key) {
        JRadioButtonMenuItem rb = new JRadioButtonMenuItem(label);
        rb.setAccelerator(KeyStroke.getKeyStroke(key, InputEvent.CTRL_MASK));
        addTo.add(rb);
        group.add(rb);
        rb.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                DataFrame.setFilterType(filter, desktop);
            }
        });
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == open) {
            int returnVal = fileChooser.showOpenDialog(Browser.jFrame);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                loadMetastockFile(file);
            } else {
                System.out.println("Open command cancelled by user.");
            }
        }
        if (e.getSource() == exit) {
            exit();
        }
        if (e.getSource() == test) {
            JInternalFrame f = desktop.getSelectedFrame();
            System.out.println("Selected frame " + f);
            if (f instanceof MasterFrame) {
                Reader r = ((MasterFrame) f).getReader();
                final TestFrame testFrame = new TestFrame(f, desktop, r);
                testFrame.setTitle("Tests of " + f.getTitle());
                testFrame.pack();
                testFrame.setVisible(true);
                desktop.add(testFrame);
                Thread thread = new Thread(new Runnable() {
                    public void run() {
                        testFrame.doTests();
                    }
                });
                thread.setName("testMaker");
                thread.setDaemon(false);
                thread.start();
            }
        }
    }

    private void loadMetastockFile(final File zipFile) {
        final ProgressMonitor processMonitor = new ProgressMonitor(jFrame, "Processing " + zipFile.getName() + "...", "", 0, 100);
        System.out.println("Open command - " + zipFile.getAbsolutePath() + ".");
        int i = 0;
        File dir;
        do {
            dir = new File(tmp, "metastockviewer-" + i);
            i++;
        } while (dir.exists());
        processMonitor.setMaximum(2 * i);
        processMonitor.setProgress(i);
        final File finalDir = dir;
        SwingWorker<List<ErrorRecord>, Void> loadTask = new SwingWorker<List<ErrorRecord>, Void>() {
            @Override
            protected List<ErrorRecord> doInBackground() throws Exception {
                List<ErrorRecord> errorRecords = new LinkedList<ErrorRecord>();
                try {
                    MasterFrame frame = new MasterFrame(Browser.this, finalDir, zipFile, desktop, processMonitor, errorRecords);
                    frame.setVisible(true);
                    frame.pack();
                    desktop.add(frame);
                    addToRecent(zipFile, true);
                } catch (InterruptedException e) {
                    // do noting
                    System.out.println("INT");
                } catch (NegativeArraySizeException e) {
                    JOptionPane.showMessageDialog(jFrame, "This is not a zip file.", "Error!", JOptionPane.ERROR_MESSAGE);
                } catch (Throwable throwable) {
                    errorRecords.add(ErrorRecord.fatal(throwable));
                } finally {
                    processMonitor.close();
                    processMonitor.setMaximum(-1);
                    if (!errorRecords.isEmpty()) {
                        new ErrorRaportDialog(jFrame, zipFile, errorRecords);
                        for (ErrorRecord err : errorRecords) {
                            System.out.println("EWRecord: " + err);
                        }
                    }
                }
                return errorRecords;
            }
        };
        loadTask.execute();
    }

    private void addToRecent(final File file, boolean update) {
        if (!recentFiles.contains(file.getAbsolutePath())) {
            recentFiles.add(file.getAbsolutePath());
            JMenuItem mi = new JMenuItem(file.getAbsolutePath());
            mi.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    loadMetastockFile(file);
                }
            });
            recent.add(mi);
            if (update) {
                prefs.putInt("recent", recentFiles.size());
                int r = 0;
                for (String fn : recentFiles)
                    prefs.put("recent_" + (r++), fn);
                prefs.put("path", file.getParentFile().getAbsolutePath());
                try {
                    prefs.flush();
                } catch (BackingStoreException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    public void windowActivated(WindowEvent e) {
    }

    public void windowClosed(WindowEvent e) {
        exit();
    }

    public void windowClosing(WindowEvent e) {
    }

    public void windowDeactivated(WindowEvent e) {
    }

    public void windowDeiconified(WindowEvent e) {
    }

    public void windowIconified(WindowEvent e) {
    }

    public void windowOpened(WindowEvent e) {
    }

    private void exit() {
        cleanup();
        System.exit(0);
    }

    private void cleanup() {
        System.out.println("Cleaning up...");
        if (tmp.exists()) {
            try {
                FileUtils.forceDelete(tmp);
            } catch (IOException e1) { /* ignore */ }
        }
    }
}
