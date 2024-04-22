//jotepad Main v0.3.0
public class Main {
    /**
     * Don't use Main.window in the Tab or Window class outside of event handlers
     */
    public static Window window;
    public final static String NAME = "Jotepad";
    public final static String VERSION = "0.3.0";
    public static void main(String[] args) {
        String defaultFileName = null;
        if (args.length > 0) {
            defaultFileName = args[0];
        }
        window = new Window(defaultFileName);
    }
}
