package com.gruszecm.mstock.browser;

import java.awt.Image;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;

public class GUIUtil {

	private GUIUtil() {
	}
	
	public static Icon getIcon(String name) {
		Image image = getImage("/icon/" + name);
		return new ImageIcon(image);
	}
	
	public static Image getImage(String path) {
		try {
			InputStream input = GUIUtil.class.getResourceAsStream(path);
			return ImageIO.read(input);
		} catch (IOException e) {
			throw new RuntimeException("Error reading image \"" + path + "\".", e);
		} 		
	}
}
