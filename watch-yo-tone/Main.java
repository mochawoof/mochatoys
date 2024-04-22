import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
//watch-yo-tone v1.1.0
public class Main {
    private static JFrame frame;
    private static JPanel panel;
    private static Dial dial;
    private static JLabel valueLabel;
    private static boolean started = false;
    private static void resize() {
        Dimension frameSize = frame.getContentPane().getSize();
        Dimension panelSize = panel.getSize();
        panel.setSize(panelSize.width, frameSize.height);
        panel.setLocation(((int)frameSize.width / 2 - (panelSize.width / 2)), 0);
	frame.revalidate();
	frame.repaint();
    }
    private static void updateValueLabel() {
        valueLabel.setText(dial.getValue() + "hz");
    }
    private static void stop() {
        started = false;
    }
    private static void generate() {
        started = true;
        new Thread() {
            public void run() {
                int i = 0;
                while (started) {
                    if (i > StdAudio.SAMPLE_RATE) {
                        i = 0;
                    } else {
                        i++;
                    }
                    StdAudio.play(Math.sin(2 * Math.PI * i * dial.getValue() / StdAudio.SAMPLE_RATE));
                }
            }
        }.start();
    }
    public static void main(String[] args) {
        frame = new JFrame("watch yo tone");
        frame.setSize(300, 400);
        frame.setLayout(null);
        frame.setDefaultCloseOperation(frame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        
        panel = new JPanel();
        panel.setSize(300, 400);
        panel.setLocation(5, 5);
        panel.setLayout(new BorderLayout());
        //panel.setBackground(Color.RED);
        panel.setBorder(new EmptyBorder(0, 20, 10, 20));
        frame.add(panel, BorderLayout.CENTER);
        
        dial = new Dial(200, 20000, 441);
        panel.add(dial, BorderLayout.CENTER);
        
        valueLabel = new JLabel("", SwingConstants.CENTER);
        valueLabel.setFont(new Font("Monospaced", Font.PLAIN, 20));
        updateValueLabel();
        panel.add(valueLabel, BorderLayout.PAGE_START);
        //made final to get compiler off my ass
        final JButton toggleButton = new JButton("Play");
        panel.add(toggleButton, BorderLayout.PAGE_END);
        
        toggleButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (started) {
                    toggleButton.setText("Play");
                    stop();
                } else {
                    toggleButton.setText("Stop");
                    generate();
                }
            }
        });
        
        dial.addDialListener(new DialListener() {
            public void dialAdjusted(DialEvent e) {
                updateValueLabel();
            }
        });
        
        frame.addComponentListener(new ComponentListener() {
            public void componentResized(ComponentEvent e) {
                resize();
            }
            public void componentHidden(ComponentEvent e) {}
            public void componentShown(ComponentEvent e) {}
            public void componentMoved(ComponentEvent e) {}
        });
        frame.addWindowStateListener(new WindowAdapter() {
            public void windowStateChanged(WindowEvent e) {
                resize();
            }
        });
        
        resize();
        frame.revalidate();
        frame.repaint();
    }
}
