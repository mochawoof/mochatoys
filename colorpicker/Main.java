import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.datatransfer.*;
public class Main {
    private static Robot r;
    private static JWindow f;
    private static Color color;
    private static BufferedImage screen;
    private static JPanel previewPane;
    private static void updatePreview(int x, int y) {
        previewPane.setLocation(x + 20, y + 20);
        
        int rgb = screen.getRGB(x, y);
        Main.color = new Color(rgb);
        previewPane.setBackground(Main.color);
        f.repaint();
    }
    public static void main(String[] args) {
        try {
            r = new Robot();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.toString(), "Error!", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
        f = new JWindow();
        Dimension s = Toolkit.getDefaultToolkit().getScreenSize();
        f.setSize(s);
        f.setLocation(0, 0);
        f.setLayout(new BorderLayout());
        f.setAlwaysOnTop(true);
        
        screen = r.createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
        JPanel container = new JPanel() {
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(screen, 0, 0, null);
            }
        };
        container.setLayout(null);
        f.add(container, BorderLayout.CENTER);
        
        JComponent contentPane = (JComponent) f.getContentPane();
        previewPane = new JPanel();
        previewPane.setSize(50, 50);
        previewPane.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
        container.add(previewPane);
        
        Point m = MouseInfo.getPointerInfo().getLocation();
        updatePreview(m.x, m.y);
        f.setVisible(true);
        contentPane.addMouseListener(new MouseListener() {
            public void mouseExited(MouseEvent e) {}
            public void mouseEntered(MouseEvent e) {}
            public void mouseReleased(MouseEvent e) {}
            public void mousePressed(MouseEvent e) {}
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    f.dispose();
                    String out = color.getRed() + ", " + color.getGreen() + ", " + color.getBlue();
                    Clipboard c = Toolkit.getDefaultToolkit().getSystemClipboard();
                    StringSelection ss = new StringSelection(out);
                    c.setContents(ss, ss);
                    
                    if (JOptionPane.showConfirmDialog(null, "Copied RGB " + out + " to clipboard! Copy hex instead?", "Copied!", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                        out = String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
                        ss = new StringSelection(out);
                        c.setContents(ss, ss);
                        JOptionPane.showMessageDialog(null, "Copied hex " + out + " to clipboard!", "Copied!", JOptionPane.INFORMATION_MESSAGE);
                    }
                }
                System.exit(0);
            }
        });
        contentPane.addMouseMotionListener(new MouseMotionListener() {
            public void mouseDragged(MouseEvent e) {}
            public void mouseMoved(MouseEvent e) {
                updatePreview(e.getX(), e.getY());
            }
        });
    }
}