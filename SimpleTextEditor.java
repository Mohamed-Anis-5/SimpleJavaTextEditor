import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.*;

public class SimpleTextEditor extends JFrame implements ActionListener {

    // 1. COMPONENT DECLARATION SECTION
    // These are the core GUI components used throughout the class
    private JTextArea textArea;
    private JMenuBar menuBar;
    private JMenu fileMenu;
    private JMenuItem newItem, openItem, saveItem;
    // JFileChooser is used for file dialogs
    private JFileChooser fileChooser;

    // --- CONSTRUCTOR ---
    public SimpleTextEditor() {
        super("Simple Text Editor"); // Set the window title

        // 2. INITIALIZATION METHODS
        initializeComponents();
        setupLayout();
        setupListeners();

        // 3. FRAME SETUP
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    // --- SETUP METHODS ---
    private void initializeComponents() {
        // Initialize all declared components here
        textArea = new JTextArea();
        menuBar = new JMenuBar();
        fileMenu = new JMenu("File");
        newItem = new JMenuItem("New");
        openItem = new JMenuItem("Open");
        saveItem = new JMenuItem("Save");
        fileChooser = new JFileChooser();
    }

    private void setupLayout() {
        // Add components to the frame and menu bar

        // 1. Setup Menu
        fileMenu.add(newItem);
        fileMenu.add(openItem);
        fileMenu.add(saveItem);
        menuBar.add(fileMenu);
        this.setJMenuBar(menuBar);

        // 2. Setup Text Area (with Scroll Pane)
        JScrollPane scrollPane = new JScrollPane(textArea);
        // By default, JFrame uses BorderLayout. Adding it to the CENTER
        // makes it fill the entire available space.
        add(scrollPane);
    }

    private void setupListeners() {
        // Attach the ActionListener (this object) to the menu items
        newItem.addActionListener(this);
        openItem.addActionListener(this);
        saveItem.addActionListener(this);

        // Set action commands for easy identification in actionPerformed()
        newItem.setActionCommand("New");
        openItem.setActionCommand("Open");
        saveItem.setActionCommand("Save");
    }


    // --- ACTION LISTENER (Event Handling) ---
    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();

        switch (command) {
            case "New":
                handleNewFile();
                break;
            case "Open":
                handleOpenFile();
                break;
            case "Save":
                handleSaveFile();
                break;
            default:
                // Handle unknown commands if necessary
                break;
        }
    }

    // --- HANDLER METHODS (Core Logic) ---
    private void handleNewFile() {
        // Clear the text area and reset the title
        textArea.setText("");
        setTitle("Simple Text Editor");
        JOptionPane.showMessageDialog(this, "New File created.", "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private void handleOpenFile() {
        JOptionPane.showMessageDialog(this, "Open File logic is pending...", "Info", JOptionPane.INFORMATION_MESSAGE);
        // Placeholder: We will write the file I/O code here next!
    }

    private void handleSaveFile() {
        JOptionPane.showMessageDialog(this, "Save File logic is pending...", "Info", JOptionPane.INFORMATION_MESSAGE);
        // Placeholder: We will write the file I/O code here next!
    }

    // --- MAIN METHOD ---
    public static void main(String[] args) {
        // Use SwingUtilities.invokeLater to ensure thread safety
        SwingUtilities.invokeLater(() -> {
            new SimpleTextEditor();
        });
    }
}
