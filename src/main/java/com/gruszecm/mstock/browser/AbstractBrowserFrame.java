package com.gruszecm.mstock.browser;

import java.util.HashSet;
import java.util.Set;

import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

public abstract class AbstractBrowserFrame extends JInternalFrame implements InternalFrameListener{
	protected JDesktopPane desktop;
	protected JInternalFrame parent;
	protected Set<JInternalFrame> childrenFrames; 
	
	private static final long serialVersionUID = 1108677867617271438L;
	
	public AbstractBrowserFrame(JInternalFrame parent, JDesktopPane desktopPane) {
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
			if (! parent.isMaximum()) {
				setLocation((int)parent.getLocation().getX() + 30, (int)parent.getLocation().getY() + 30);
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
		if (! flag && childrenFrames != null) {
			for(JInternalFrame jif : childrenFrames) {
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
		desktop.remove(this);
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
