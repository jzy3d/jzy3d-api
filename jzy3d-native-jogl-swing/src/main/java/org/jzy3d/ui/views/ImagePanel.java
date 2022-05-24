package org.jzy3d.ui.views;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

public class ImagePanel extends JPanel {
  public static Image getImageByPath(String img) {
    return new ImageIcon(img).getImage();
  }

  public ImagePanel(String img) {
    this(getImageByPath(img));
  }

  public ImagePanel(Image img) {
    setImage(img);
    setLayout(null);
  }

  public ImagePanel() {
    setLayout(null);
  }

  public void setImage(Image img) {
    this.img = img;
    Dimension size = new Dimension(img.getWidth(null), img.getHeight(null));
    setPreferredSize(size);
    setMinimumSize(size);
    setMaximumSize(size);
    setSize(size);
  }

  public Image getImage() {
    return img;
  }

  @Override
  public void paintComponent(Graphics g) {
    g.drawImage(img, 0, 0, null);
  }

  protected Image img;
  private static final long serialVersionUID = 1L;
}
