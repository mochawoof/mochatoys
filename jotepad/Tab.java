//jotepad Tab v0.3.0
import javax.swing.*;
import java.awt.BorderLayout;

import java.io.File;
import java.nio.file.*;
import java.nio.charset.StandardCharsets;
import java.nio.charset.Charset;

import org.fife.ui.rtextarea.*;
import org.fife.ui.rsyntaxtextarea.*;
public class Tab extends JPanel {
    public File openedFile;
    public String selectedCharset = "utf_8";
    public RSyntaxTextArea textArea;
    public void openFileName(String filename) {
        try {
            Charset gotCharset = (Charset)StandardCharsets.class.getField(selectedCharset.toUpperCase()).get(null);
            textArea.setText(new String(Files.readAllBytes(Paths.get(filename)), gotCharset));
            textArea.setSyntaxEditingStyle(Window.detectType(filename));
        } catch (Exception e) {
            ErrorHandler.error(e, "Failed to open file", true);
        }
    }
    public Tab(File defaultFile) {
        textArea = new RSyntaxTextArea(20, 60);
        textArea.setCodeFoldingEnabled(true);
        textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_NONE);
        RTextScrollPane pane = new RTextScrollPane(textArea);
        setLayout(new BorderLayout());
        add(pane, BorderLayout.CENTER);
        if (defaultFile != null) {
            openedFile = defaultFile;
            openFileName(defaultFile.getAbsolutePath());
        }
    }
}
