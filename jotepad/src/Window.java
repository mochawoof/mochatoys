//jotepad Window v0.3.0
import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.*;
import java.nio.charset.StandardCharsets;
import java.nio.charset.Charset;
import java.net.URI;
import java.lang.reflect.Field;
import java.util.Enumeration;

import org.fife.ui.rtextarea.*;
import org.fife.ui.rsyntaxtextarea.*;
public class Window extends JFrame {
    private JTabbedPane tabPane;
    
    private static Field[] supportedTypes = SyntaxTypes.class.getFields();
    /**
     * @return the Tab currently selected in tabPane
     */
    private Tab getTab() {
        return (Tab)tabPane.getSelectedComponent();
    }
    /**
     * Updates the window title based on the selected file
     */
    public void updateTitle() {
        if (tabPane.getTabCount() > 0) {
            if (getTab().openedFile != null) {
                String newTitle = Main.NAME + " - " + getTab().openedFile.getAbsolutePath();
                setTitle(newTitle);
            } else {
                setTitle(Main.NAME);
            }
        } else {
            setTitle(Main.NAME);
        }
    }
    /**
     * Updates the selected tab's title
     */
    public void updateTabTitle(boolean unsaved) {
        String title = "New";
        if (getTab().openedFile != null) {
            title = getTab().openedFile.getName();
        }
        if (unsaved) {
            title += " *";
        }
        tabPane.setTitleAt(tabPane.getSelectedIndex(), title);
    }
    /**
     * Returns the detected content type as according to SyntaxConstants
     * String filename: The name of the file to detect from
     */
    public static String detectType(String filename) {
        try {
            for (Field f : supportedTypes) {
                String[] rule = ((String)f.get(null)).split(";");
                if (rule.length > 1) {
                    String[] exts = rule[1].split(",");
                    for (String e : exts) {
                        if (filename.toLowerCase().endsWith(e)) {
                            return rule[0];
                        }
                    }
                }
            }
        } catch (Exception e) {
            ErrorHandler.error(e, "Failed to detect type");
        }
        return SyntaxConstants.SYNTAX_STYLE_NONE;
    }
    /**
     * Selects the AbstractButton in a ButtonGroup which matches a given actionCommand.
     * ButtonGroup group: The ButtonGroup to select from
     * String command: The actionCommand to match, not case-sensitive
     */
    private void setOption(ButtonGroup group, String command) {
        Enumeration<AbstractButton> options = group.getElements();
        while (options.hasMoreElements()) {
            AbstractButton option = options.nextElement();
            if (option.getActionCommand().toLowerCase().equals(command.toLowerCase())) {
                option.setSelected(true);
                break;
            }
        }
    }
    /**
     * Creates a new tab with a blank file.
     * File defaultFile: A File to load automatically, can be null
     */
    private void newFile(File defaultFile) {
        if (tabPane.getTabCount() > 0) {
            if (getTab().openedFile == null && getTab().textArea.getText().isEmpty() && defaultFile != null) {
                closeFile(false);
            }
        }
        Tab newTab = new Tab(defaultFile);
        tabPane.addTab("New", newTab);
        tabPane.setSelectedComponent(newTab);
        updateTabTitle(false);
    }
    /**
     * Prompts the user to open a file. Actual file opening is handled by Tab
     */
    private void openFile() {
        JFileChooser chooser = new JFileChooser();
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            newFile(chooser.getSelectedFile());
        }
    }
    /**
     * Saves the selected file. If it doesn't have one, it invokes saveFileAs
     */
    private void saveFile() {
        try {
            if (getTab().openedFile != null) {
                Files.write(Paths.get(getTab().openedFile.getAbsolutePath()), getTab().textArea.getText().getBytes());
            } else {
                saveFileAs();
            }
        } catch (Exception e) {
            ErrorHandler.error(e, "Failed to save file", true);
        }
    }
    private void saveFileAs() {
        JFileChooser chooser = new JFileChooser();
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            getTab().openedFile = chooser.getSelectedFile();
            updateTitle();
            updateTabTitle(false);
            saveFile();
        }
    }
    /**
     * boolean canExit: Whether or not the program can exit if it closes the last tab
     * TODO: Prompt the user if the selected file is unsaved
     */
    public void closeFile(boolean canExit) {
        tabPane.removeTabAt(tabPane.getSelectedIndex());
        if (tabPane.getTabCount() < 1) {
            if (canExit) {
                System.exit(0);
            }
        }
    }
    private void reloadFile() {
        if (getTab().openedFile != null) {
            getTab().openFileName(getTab().openedFile.getAbsolutePath());
        }
    }
    private void github() {
        try {
            Desktop.getDesktop().browse(new URI("https://github.com/mochawoof/jotepad"));
        } catch (Exception e) {
            ErrorHandler.error(e, "Failed to open GitHub link", true);
        }
    }
    /**
     * String defaultFileName: Creates the window and loads the given file automatically
     */
    public Window(String defaultFileName) {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setIconImage(Helper.getResourceAsImage("icon.png"));
        setSize(500,400);
        setVisible(true);
        
        JMenuBar bar = new JMenuBar();
        add(bar, BorderLayout.PAGE_START);
        
        //File menu
        JMenu fileMenu = new JMenu("File");
        bar.add(fileMenu);
        JMenuItem newItem = new JMenuItem("New");
        fileMenu.add(newItem);
        newItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                newFile(null);
            }
        });
        JMenuItem openItem = new JMenuItem("Open...");
        fileMenu.add(openItem);
        openItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openFile();
            }
        });
        JMenuItem saveItem = new JMenuItem("Save");
        fileMenu.add(saveItem);
        saveItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                saveFile();
            }
        });
        JMenuItem saveAsItem = new JMenuItem("Save as...");
        fileMenu.add(saveAsItem);
        saveAsItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                saveFileAs();
            }
        });
        JMenuItem closeItem = new JMenuItem("Close");
        fileMenu.add(closeItem);
        closeItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                closeFile(true);
            }
        });
        JMenuItem reloadItem = new JMenuItem("Reload");
        fileMenu.add(reloadItem);
        reloadItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                reloadFile();
            }
        });
        
        //Edit menu
        JMenu editMenu = new JMenu("Edit");
        bar.add(editMenu);
        JMenuItem cutItem = new JMenuItem("Cut");
        editMenu.add(cutItem);
        cutItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                getTab().textArea.cut();
            }
        });
        JMenuItem copyItem = new JMenuItem("Copy");
        editMenu.add(copyItem);
        copyItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                getTab().textArea.copy();
            }
        });
        JMenuItem pasteItem = new JMenuItem("Paste");
        editMenu.add(pasteItem);
        pasteItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                getTab().textArea.paste();
            }
        });
        
        //Options menu
        JMenu optionsMenu = new JMenu("Options");
        bar.add(optionsMenu);
        JMenu encodingSubMenu = new JMenu("Encoding");
        optionsMenu.add(encodingSubMenu);
        
        ButtonGroup encodingGroup = new ButtonGroup();
        Field[] encodings = StandardCharsets.class.getFields();
        try {
            for (Field f : encodings) {
                Charset charset = (Charset)f.get(null);
                JRadioButtonMenuItem fItem = new JRadioButtonMenuItem(charset.displayName());
                fItem.setActionCommand(f.getName());
                encodingSubMenu.add(fItem);
                encodingGroup.add(fItem);
                fItem.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        try {
                            String actionCommand = encodingGroup.getSelection().getActionCommand();
                            getTab().selectedCharset = actionCommand;
                            reloadFile();
                        } catch (Exception x) {
                            ErrorHandler.error(x, "Failed to set charset");
                        }
                    }
                });
            }
            setOption(encodingGroup, "utf-8");
        } catch (Exception e) {
            ErrorHandler.error(e, "Failed to get charsets! Please restart the program", true);
        }
        
        //Help menu
        JMenu helpMenu = new JMenu("Help");
        bar.add(helpMenu);
        JMenuItem aboutItem = new JMenuItem("About");
        helpMenu.add(aboutItem);
        aboutItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(Main.window, Main.NAME + " version " + Main.VERSION + "\n\nOnline help and updates are available at the GitHub repository.", "About " + Main.NAME, JOptionPane.PLAIN_MESSAGE);
            }
        });
        JMenuItem githubItem = new JMenuItem("GitHub");
        helpMenu.add(githubItem);
        githubItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                github();
            }
        });
        
        tabPane = new JTabbedPane();
        add(tabPane, BorderLayout.CENTER);
        tabPane.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                if (tabPane.getTabCount() > 0) {
                    updateTitle();
                    setOption(encodingGroup, getTab().selectedCharset);
                }
            }
        });
        
        File defaultFile = null;
        //Handle starting a file with it
        if (defaultFileName != null) {
            File checkDefaultFile = new File(defaultFileName);
            if (checkDefaultFile.exists()) {
                defaultFile = checkDefaultFile;
            }
        }
        
        newFile(defaultFile);
        revalidate();
        repaint();
    }
}
