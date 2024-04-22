//latte Main v1.1
import com.sun.net.httpserver.*;
import java.net.InetSocketAddress;
import java.io.*;
import java.util.Scanner;
public class Main {
    public static String path = System.getProperty("user.dir");
    public static int port = 8000;
    public static HttpServer server;
    public static String version = "latte v1.1";
    
    private static void parseArgs(String[] args) {
        if (args.length > 0) {
            path = args[0];
            if (args.length > 1) {
                port = Integer.parseInt(args[1]);
            }
        }
    }
    public static void error(Exception e) {
        System.out.println("ERROR:");
        e.printStackTrace();
        if (Gui.on) {
            Gui.error(e.toString());
        }
    }
    public static void main(String[] args) {
        System.out.println(version);
        System.out.println("----------");
        Scanner scanner = new Scanner(System.in);
        parseArgs(args);
        System.out.println("Starting...");
        System.out.println();
        try {
            boolean running = true;
            server = HttpServer.create(new InetSocketAddress("localhost", port), 0);
            server.createContext("/", new RequestHandler());
            server.start();
            System.out.println("Started on port " + port + "!");
            
            if (!Gui.on) {
                System.out.println("Type \"help\" for a list of commands.");            
                while (running) {
                    String in = scanner.nextLine();
                    System.out.println();
                    if (in.equals("help")) {
                        System.out.println("Commands:");
                        System.out.println("---------");
                        System.out.println("help: Spits out this list");
                        System.out.println("q: Quits");
                    } else if (in.equals("q")) {
                        running = false;
                    }
                }
                server.stop(0);
            }
        } catch (Exception e) {
            error(e);
        }
    }
}
