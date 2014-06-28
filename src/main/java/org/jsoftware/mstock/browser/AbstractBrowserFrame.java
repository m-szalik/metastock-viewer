package org.jsoftware.mstock.browser;

import javax.swing.*;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;
import java.util.HashSet;
import java.util.Set;

abstract class AbstractBrowserFrame extends JInternalFrame implements InternalFrameListener {
    protected final JDesktopPane desktop;
    protected final JInternalFrame parent;
    protected final Set<JInternalFrame> childrenFrames;

    private static final long serialVersionUID = 1108677867617271438L;

    protected AbstractBrowserFrame(JInternalFrame parent, JDesktopPane desktopPane) {
        super("", true, true, true);
        setIconifiable(true);
        this.desktop = desktopPane;
        this.parent = parent;
        this.childrenFrames = new HashSet<JInternalFrame>();
        if (parent instanceof AbstractBrowserFrame) {
            ((AbstractBrowserFrame) parent).childrenFrames.add(this);
        }
        if (parent != null) {
//			try {
////				TODO this.setMaximum(parent.isMaximum());
//			} catch (PropertyVetoException e) {
//				e.printStackTrace();
//			}
            if (!parent.isMaximum()) {
                setLocation((int) parent.getLocation().getX() + 30, (int) parent.getLocation().getY() + 30);
            }
        }
        this.addInternalFrameListener(this);
    }

    public void setFrameIconByName(String name) {
        setFrameIcon(GUIUtil.getIcon(name));
    }

    @Override
    public void setVisible(boolean flag) {
        super.setVisible(flag);
        if (!flag) {
            for (JInternalFrame jif : childrenFrames) {
                jif.setVisible(flag);
            }
        }
        if (flag) {
            this.toFront();
        }
    }

    public void internalFrameActivated(InternalFrameEvent e) {
    }

    public void internalFrameClosed(InternalFrameEvent e) {
//		this.dispose();
        if (desktop != null) {
            desktop.remove(this);
        }
    }

    public void internalFrameClosing(InternalFrameEvent e) {
    }

    public void internalFrameDeactivated(InternalFrameEvent e) {
    }

    public void internalFrameDeiconified(InternalFrameEvent e) {
    }

    public void internalFrameIconified(InternalFrameEvent e) {
    }

    public void internalFrameOpened(InternalFrameEvent e) {
    }
}
