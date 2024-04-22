//latte Gui v1.1
import javax.swing.*;
import java.awt.event.*;
public class Gui {
    public static boolean on = false;
    public static boolean started = false;
    public static void error(String msg) {
        JOptionPane.showMessageDialog(null, msg, "ERROR", JOptionPane.ERROR_MESSAGE);
    }
    public static void main(String[] args) {
        on = true;
        JFrame frame = new JFrame();
        frame.setSize(200, 200);
        frame.setResizable(false);
	frame.setTitle(Main.version);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        
        frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.PAGE_AXIS));
        JLabel pathLabel = new JLabel("Path:");
        JTextField pathField = new JTextField(Main.path);
        JLabel portLabel = new JLabel("Port:");
        JTextField portField = new JTextField(Integer.toString(Main.port));
        JButton startStopButton = new JButton("Start server");
        startStopButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (!started) {
                    Main.main(new String[] {pathField.getText(), portField.getText()});
                    Gui.started = true;
                    startStopButton.setText("Stop");
                } else {
                    Main.server.stop(0);
                    frame.dispose();
                }
            }
        });
        frame.add(pathLabel);
        frame.add(pathField);
        frame.add(portLabel);
        frame.add(portField);
        frame.add(startStopButton);
        
        frame.revalidate();
        frame.repaint();
    }
}
