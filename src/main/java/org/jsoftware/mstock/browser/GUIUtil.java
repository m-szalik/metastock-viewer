package org.jsoftware.mstock.browser;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;

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
            AssertionError ex = new AssertionError("Error reading image \"" + path + "\".");
            ex.initCause(e);
            throw ex;
        }
    }
}
