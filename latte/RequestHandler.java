//latte RequestHandler v1.1
import com.sun.net.httpserver.*;
import java.io.*;
import java.util.Date;
import java.nio.file.*;
import java.nio.channels.SeekableByteChannel;
import java.nio.ByteBuffer;
import java.util.HashMap;
public class RequestHandler implements HttpHandler {
    private HashMap<String, String> contentTypes = new HashMap<String, String>();
    public RequestHandler() {
        String[] rawContentTypes = Helper.getResourceAsStringArray("content-types.csv");
        for (String type : rawContentTypes) {
            String[] row = type.trim().split(",");
            contentTypes.put(row[0], row[1]);
        }
    }
    private void sendMsg(String msg, HttpExchange exchange, int code, OutputStream out) {
        try {
            exchange.getResponseHeaders().set("Content-Type", "text/plain");
            exchange.sendResponseHeaders(code, msg.length());
            out.write(msg.getBytes());
        } catch (IOException e) {
            Main.error(e);
        }
    }
    public void handle(HttpExchange exchange) throws IOException {
        String uri = exchange.getRequestURI().normalize().getPath();
        System.out.println(new Date().toString() + ": " + uri);

        int responseCode = 200;
        String contentType = "application/octet-stream";
        OutputStream out = exchange.getResponseBody();
        File requested = new File(Main.path + uri);
        if (requested.exists() && !requested.isDirectory() && requested.canRead()) {
            Path path = requested.toPath();
            String pathString = path.toString();
            if (pathString.lastIndexOf(".") > -1) { //make sure there is an extension before lookup
                String ext = pathString.substring(pathString.lastIndexOf("."), pathString.length());
                if (contentTypes.containsKey(ext)) {
                    contentType = contentTypes.get(ext);
                }
            }
            
            exchange.getResponseHeaders().set("Content-Type", contentType);
            exchange.sendResponseHeaders(responseCode, requested.length());
            try (SeekableByteChannel channel = Files.newByteChannel(path, StandardOpenOption.READ)) {
                int toAllocate = 1000;
                int written = 0;
                ByteBuffer buffer = ByteBuffer.allocate(toAllocate);
                while (channel.read(buffer) != -1) {
                    buffer.flip();
                    byte[] bytes = buffer.array();
                    if (channel.position() == channel.size()) { //manually write bytes when risk of going over channel size
                        for (int i=0; written < channel.size(); i++) {
                            out.write(bytes[i]);
                            written++;
                        }
                    } else {
                        out.write(bytes);
                        written += bytes.length;
                    }
                    buffer.clear();
                }
            } catch (Exception e) {
                Main.error(e);
            }
        } else if (requested.isDirectory() && uri.toString().endsWith("/") && requested.canRead()) {
            String listing = "<h2>" + requested.getAbsolutePath() + "</h2><ul>";
            listing += "<li><a href=\"../\">../</a></li>";
            File[] dir = requested.listFiles();
            for (File entry : dir) {
                String append = "";
                if (entry.isDirectory()) { //to ensure links work right!
                    append = "/";
                }
                listing += "<li><a href=\"" + entry.getName() + append + "\">" + entry.getName() + append + "</a></li>";
            }
            listing += "</ul>";
            
            exchange.getResponseHeaders().set("Content-Type", "text/html");
            exchange.sendResponseHeaders(responseCode, listing.length());
            out.write(listing.getBytes());
        } else {
            sendMsg("File not found!", exchange, 404, out);
        }
        
        out.flush();
        out.close();
    }
}
