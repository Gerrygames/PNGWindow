import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;
import java.awt.image.MemoryImageSource;
import java.net.URL;
import javax.imageio.ImageIO;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;

public class PNGAnzeige extends JWindow {
  private JLabel label = new JLabel();
  private JPanel panel = new JPanel();
  private ImageIcon icon;
  private int X = 0;
  private int Y = 0;
  private double scale = 1;
  private int rotation = 0;   
  private JWindow thiswindow = this;
  private boolean isgif = false;
  
  public PNGAnzeige() { 
    int x = 10;
    int y = 10;
    setLocation(x, y);
    this.setAlwaysOnTop(true);
    this.setBackground(new Color(0,0,0,1));
    Container cp = getContentPane();
    
    String file = (String)JOptionPane.showInputDialog(this, "File/URL:", "File/URL:", JOptionPane.PLAIN_MESSAGE);
    isgif = file.endsWith(".gif");
    icon = new ImageIcon(file); 
    if (icon.getImageLoadStatus()==MediaTracker.ERRORED) {
      icon = new ImageIcon(file + ".png");
      if (icon.getImageLoadStatus()==MediaTracker.ERRORED) {
        try {
          URL url = new URL(file);
          icon = new ImageIcon(url);
        } catch (Exception e) {
          e.printStackTrace();
          System.out.println("File not found");
          System.exit(0);
        }
      } 
    } 
    panel.setBackground(new Color(0,0,0,0));
    label.setIcon(icon);
    panel.add(label);
    setSize(icon.getImage().getWidth(null), icon.getImage().getHeight(null));     
    cp.add(panel); 
    
    addMouseListener(new MouseAdapter() {
      public void mousePressed(MouseEvent e) {
        if (e.getButton()== 1) {
          X=e.getXOnScreen()-getX();
          Y=e.getYOnScreen()-getY();
        }
      }
      public void mouseClicked(MouseEvent e) {
        if (e.getButton()== 3) {
          System.exit(0);
        }
      }
    });
    
    addMouseMotionListener(new MouseMotionAdapter() {
      public void mouseDragged(MouseEvent e) {
        if (e.getModifiersEx() == MouseEvent.BUTTON1_DOWN_MASK) {
          setLocation(e.getXOnScreen()-X,e.getYOnScreen()-Y);
          setSize(label.getIcon().getIconWidth(), label.getIcon().getIconHeight());
          toFront();
        }
      }
    });
    
    addMouseWheelListener(new MouseAdapter() {
      public void mouseWheelMoved(MouseWheelEvent e) {
        if (isgif) return;
        if (!e.isControlDown()) {
          if (e.getWheelRotation()<0) scale = scale*1.1;        
          else scale = scale*0.9;        
        } else {
          if (e.getWheelRotation()<0) rotation = rotation+5;         
          else rotation = rotation-5;         
        }
        int w = (int) (icon.getImage().getWidth(null)*scale);
        int h = (int) (icon.getImage().getHeight(null)*scale);
        Image img = icon.getImage();
        Image newimg = rotateImageX(toBufferedImage(img), rotation); 
        newimg = newimg.getScaledInstance(w, h, java.awt.Image.SCALE_FAST);
        ImageIcon newIcon = new ImageIcon(newimg);
        setSize(newIcon.getImage().getWidth(null), newIcon.getImage().getHeight(null));
        label.setIcon(newIcon);
        repaint();
      }
    });  
    setVisible(true);
    System.out.println(" asdf");
  }

  public static void main(String[] args) {
    new PNGAnzeige();
  }
  
  public static BufferedImage rotateImageX(BufferedImage img, double angle) {
    double sin = Math.abs(Math.sin(Math.toRadians(angle))), cos = Math.abs(Math.cos(Math.toRadians(angle)));
    int w = img.getWidth(null), h = img.getHeight(null);
    int neww = (int) Math.floor(w * cos + h * sin);
    int newh = (int) Math.floor(h * cos + w * sin);
    BufferedImage bimg = new BufferedImage(neww, newh, BufferedImage.TYPE_INT_ARGB);
    //BufferedImage bimg = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g = bimg.createGraphics();
    g.translate((neww - w) / 2, (newh - h) / 2);
    //g.translate(w / 2,h / 2);
    g.rotate(Math.toRadians(angle), w / 2, h / 2);
    g.drawRenderedImage(img, null);
    g.dispose();
    return bimg;
  }
  
  public BufferedImage toBufferedImage(Image img)
  {
    if (img instanceof BufferedImage)
    {
      return (BufferedImage) img;
    }
    BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
    Graphics2D bGr = bimage.createGraphics();
    bGr.drawImage(img, 0, 0, null);
    bGr.dispose();
    return bimage;
  }
  
  public void setSize(int w, int h) {
    super.setSize(w,h+5);
  }
  
  public void setBounds(int x, int y, int w, int h) {
    super.setBounds(x,y,w,h+5);
  }
}
