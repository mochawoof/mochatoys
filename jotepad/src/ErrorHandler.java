//jotepad ErrorHandler v0.3.0
import javax.swing.JOptionPane;
public class ErrorHandler {
    /**
     * Handles an Exception
     * String msg: The optional message to be printed before the Exception's stack trace
     * boolean loud: Whether or not to show a message box with the message and Exception
     */
    public static void error(Exception e, String msg, boolean loud) {
        System.err.println(msg + ":");
        e.printStackTrace();
        if (loud) {
            JOptionPane.showMessageDialog(Main.window, msg + ":\n\n" + e.toString(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    public static void error(Exception e, String msg) {
        error(e, msg, false);
    }
    public static void error(Exception e) {
        error(e, "Error", false);
    }
}
