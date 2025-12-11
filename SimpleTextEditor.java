import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.*;
import java.awt.Font; // Import Font class for size manipulation

// --- ENUMERATION SECTION (OOP Principle) ---
// Defines all possible actions to be handled, making code cleaner and safer than using raw strings.
enum EditorAction {
    NEW("New"), 
    OPEN("Open"), 
    SAVE("Save"),
    INCREASE_FONT("IncreaseFont"), 
    DECREASE_FONT("DecreaseFont"); 

    private final String command;

    EditorAction(String command) {
        this.command = command;
    }

    public String getCommand() {
        return command;
    }
}

public class SimpleTextEditor extends JFrame implements ActionListener {

    // 1. COMPONENT DECLARATION SECTION (Encapsulation: private access)
    private JTextArea textArea;
    private JMenuBar menuBar;
    
    // File Menu components
    private JMenu fileMenu;
    private JMenuItem newItem, openItem, saveItem;
    
    // Format Menu components
    private JMenu formatMenu;
    private JMenuItem increaseFontItem, decreaseFontItem;
    
    private JFileChooser fileChooser;
    
    // State variable for font size
    private float currentFontSize = 16f; 

    // --- CONSTRUCTOR ---
    public SimpleTextEditor() {
        super("Simple Text Editor"); // Set the window title

        // 2. INITIALIZATION AND SETUP METHODS
        initializeComponents();
        setupLayout();
        setupListeners();

        // 3. FRAME SETUP
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    // --- SETUP METHODS (Encapsulation: Hiding setup complexity) ---
    private void initializeComponents() {
        // Initialize Core Components
        textArea = new JTextArea();
        menuBar = new JMenuBar();
        fileChooser = new JFileChooser();
        
        // File Menu Initialization
        fileMenu = new JMenu("File");
        newItem = new JMenuItem(EditorAction.NEW.getCommand());
        openItem = new JMenuItem(EditorAction.OPEN.getCommand());
        saveItem = new JMenuItem(EditorAction.SAVE.getCommand());
        
        // Format Menu Initialization
        formatMenu = new JMenu("Format");
        increaseFontItem = new JMenuItem(EditorAction.INCREASE_FONT.getCommand());
        decreaseFontItem = new JMenuItem(EditorAction.DECREASE_FONT.getCommand());
    }

    private void setupLayout() {
        // 1. Setup Menu Bar
        
        // File Menu
        fileMenu.add(newItem);
        fileMenu.add(openItem);
        fileMenu.add(saveItem);
        menuBar.add(fileMenu);

        // Format Menu
        formatMenu.add(increaseFontItem);
        formatMenu.add(decreaseFontItem);
        menuBar.add(formatMenu);
        
        this.setJMenuBar(menuBar);

        // 2. Setup Text Area (with Scroll Pane)
        JScrollPane scrollPane = new JScrollPane(textArea);
        
        // Set Initial Font
        Font initialFont = new Font(Font.MONOSPACED, Font.PLAIN, (int) currentFontSize);
        textArea.setFont(initialFont);
        
        add(scrollPane);
    }

    private void setupListeners() {
        // Attach the ActionListener (this object) to all JMenuItems
        newItem.addActionListener(this);
        openItem.addActionListener(this);
        saveItem.addActionListener(this);
        increaseFontItem.addActionListener(this);
        decreaseFontItem.addActionListener(this);

        // Set action commands using the Enum
        newItem.setActionCommand(EditorAction.NEW.getCommand());
        openItem.setActionCommand(EditorAction.OPEN.getCommand());
        saveItem.setActionCommand(EditorAction.SAVE.getCommand());
        increaseFontItem.setActionCommand(EditorAction.INCREASE_FONT.getCommand());
        decreaseFontItem.setActionCommand(EditorAction.DECREASE_FONT.getCommand());
    }


    // --- ACTION LISTENER (Polymorphism: Implementing the interface method) ---
    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();

        if (command.equals(EditorAction.NEW.getCommand())) {
            handleNewFile();
        } else if (command.equals(EditorAction.OPEN.getCommand())) {
            handleOpenFile();
        } else if (command.equals(EditorAction.SAVE.getCommand())) {
            handleSaveFile();
        } else if (command.equals(EditorAction.INCREASE_FONT.getCommand())) { 
            handleFontSize(2f);
        } else if (command.equals(EditorAction.DECREASE_FONT.getCommand())) { 
            handleFontSize(-2f);
        }
    }

    // --- HANDLER METHODS (Core Logic) ---

    private void handleNewFile() {
        textArea.setText("");
        setTitle("Simple Text Editor");
        JOptionPane.showMessageDialog(this, "New File created.", "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private void handleOpenFile() {
        int returnValue = fileChooser.showOpenDialog(this);

        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            
            try (BufferedReader reader = new BufferedReader(new FileReader(selectedFile))) {
                StringBuilder content = new StringBuilder();
                String line;
                
                while ((line = reader.readLine()) != null) {
                    content.append(line).append("\n");
                }
                
                textArea.setText(content.toString());
                setTitle("Simple Text Editor - " + selectedFile.getName());
                
            } catch (FileNotFoundException ex) {
                JOptionPane.showMessageDialog(this, "File not found: " + selectedFile.getName(), "Error", JOptionPane.ERROR_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error reading file: " + selectedFile.getName(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleSaveFile() {
        int returnValue = fileChooser.showSaveDialog(this);

        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileToSave))) {
                String content = textArea.getText();
                writer.write(content);
                
                setTitle("Simple Text Editor - " + fileToSave.getName());
                
                JOptionPane.showMessageDialog(this, 
                    "File saved successfully!", 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE
                );

            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, 
                    "Error saving file: " + ex.getMessage(), 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }
    
    // New handler method for Font control
    private void handleFontSize(float delta) {
        float newSize = currentFontSize + delta;
        
        // Clamp the size to reasonable limits
        if (newSize < 8f) {
            newSize = 8f;
        } else if (newSize > 72f) {
            newSize = 72f;
        }

        currentFontSize = newSize;
        
        // Get the current font and create a new font with the updated size
        Font currentFont = textArea.getFont();
        textArea.setFont(currentFont.deriveFont(currentFontSize));
    }

    // --- MAIN METHOD ---
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new SimpleTextEditor();
        });
    }
}