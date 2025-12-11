import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.awt.Font;
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;

// --- ENUMERATION SECTION (OOP Principle) ---
enum EditorAction {
    NEW("New"), 
    OPEN("Open"), 
    SAVE("Save"),
    INCREASE_FONT("Increase Font (+)", 2f), 
    DECREASE_FONT("Decrease Font (-)", -2f);

    private final String command;

    EditorAction(String command) {
        this.command = command;
    }
    
    // Surcharge pour les actions de police (bien que non utilisées ici, c'est une bonne pratique)
    EditorAction(String command, float delta) {
        this.command = command;
    }

    public String getCommand() {
        return command;
    }
}

public class SimpleTextEditor extends JFrame implements ActionListener {

    // 1. COMPONENT AND STATE DECLARATION SECTION (Encapsulation)
    private JTextArea textArea;
    private JMenuBar menuBar;
    
    private JMenu fileMenu, formatMenu;
    private JMenuItem newItem, openItem, saveItem, increaseFontItem, decreaseFontItem;
    
    private JFileChooser fileChooser;
    
    // State variables
    private float currentFontSize = 16f; 
    private boolean isModified = false;
    private File currentFile = null; 

    // --- CONSTRUCTOR ---
    public SimpleTextEditor() {
        super("Simple Text Editor");

        // 2. INITIALIZATION AND SETUP METHODS
        initializeComponents();
        setupLayout();
        setupListeners();

        // 3. FRAME SETUP
        setSize(800, 600);
        // Utilise DO_NOTHING_ON_CLOSE pour que l'écouteur de fenêtre gère la fermeture
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        
        updateTitle(); // Initialise le titre avec "Untitled"
        setVisible(true);
    }

    // --- SETUP METHODS ---
    private void initializeComponents() {
        textArea = new JTextArea();
        menuBar = new JMenuBar();
        fileChooser = new JFileChooser();
        
        fileMenu = new JMenu("File");
        newItem = new JMenuItem(EditorAction.NEW.getCommand());
        openItem = new JMenuItem(EditorAction.OPEN.getCommand());
        saveItem = new JMenuItem(EditorAction.SAVE.getCommand());
        
        formatMenu = new JMenu("Format");
        increaseFontItem = new JMenuItem(EditorAction.INCREASE_FONT.getCommand());
        decreaseFontItem = new JMenuItem(EditorAction.DECREASE_FONT.getCommand());
    }

    private void setupLayout() {
        // Setup Menu Bar
        fileMenu.add(newItem);
        fileMenu.add(openItem);
        fileMenu.add(saveItem);
        menuBar.add(fileMenu);

        formatMenu.add(increaseFontItem);
        formatMenu.add(decreaseFontItem);
        menuBar.add(formatMenu);
        
        this.setJMenuBar(menuBar);

        // Setup Text Area
        JScrollPane scrollPane = new JScrollPane(textArea);
        Font initialFont = new Font(Font.MONOSPACED, Font.PLAIN, (int) currentFontSize);
        textArea.setFont(initialFont);
        
        add(scrollPane);
    }

    private void setupListeners() {
        // Attach ActionListener to Menu Items
        newItem.addActionListener(this);
        openItem.addActionListener(this);
        saveItem.addActionListener(this);
        increaseFontItem.addActionListener(this);
        decreaseFontItem.addActionListener(this);

        // Set action commands
        newItem.setActionCommand(EditorAction.NEW.getCommand());
        openItem.setActionCommand(EditorAction.OPEN.getCommand());
        saveItem.setActionCommand(EditorAction.SAVE.getCommand());
        increaseFontItem.setActionCommand(EditorAction.INCREASE_FONT.getCommand());
        decreaseFontItem.setActionCommand(EditorAction.DECREASE_FONT.getCommand());
        
        // Document Listener for tracking modifications (Step 5)
        textArea.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                isModified = true;
                updateTitle();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                isModified = true;
                updateTitle();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                // Not relevant for JTextArea
            }
        });
        
        // Window Listener for closing confirmation (Step 5)
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (checkSaved()) {
                    dispose();
                    System.exit(0);
                }
            }
        });
    }


    // --- ACTION LISTENER (Polymorphism) ---
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
        if (!checkSaved()) {
            return;
        }
        
        textArea.setText("");
        currentFile = null;
        isModified = false;
        updateTitle();
        JOptionPane.showMessageDialog(this, "New File created.", "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private void handleOpenFile() {
        if (!checkSaved()) {
            return;
        }
        
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
                
                // Update state variables (Step 5)
                currentFile = selectedFile;
                isModified = false;
                updateTitle();
                
            } catch (FileNotFoundException ex) {
                JOptionPane.showMessageDialog(this, "File not found: " + selectedFile.getName(), "Error", JOptionPane.ERROR_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error reading file: " + selectedFile.getName(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleSaveFile() {
        File fileToSave = currentFile;

        // Si currentFile est null, force l'ouverture du dialogue Save As
        if (fileToSave == null) {
            int returnValue = fileChooser.showSaveDialog(this);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                fileToSave = fileChooser.getSelectedFile();
            } else {
                return; // Annulation de la sauvegarde
            }
        }

        if (fileToSave != null) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileToSave))) {
                String content = textArea.getText();
                writer.write(content);
                
                // Update state variables (Step 5)
                currentFile = fileToSave;
                isModified = false;
                updateTitle();
                
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
    
    private void handleFontSize(float delta) {
        float newSize = currentFontSize + delta;
        
        if (newSize < 8f) {
            newSize = 8f;
        } else if (newSize > 72f) {
            newSize = 72f;
        }

        currentFontSize = newSize;
        Font currentFont = textArea.getFont();
        textArea.setFont(currentFont.deriveFont(currentFontSize));
    }

    // --- UTILITARY METHODS (Step 5) ---

    private void updateTitle() {
        String title = "Simple Text Editor";
        String fileName = (currentFile != null) ? currentFile.getName() : "Untitled";
        
        if (isModified) {
            setTitle(title + " - " + fileName + "*");
        } else {
            setTitle(title + " - " + fileName);
        }
    }

    private boolean checkSaved() {
        if (!isModified) {
            return true;
        }
        
        int option = JOptionPane.showConfirmDialog(
            this, 
            "The file has been modified. Do you want to save changes?", 
            "Unsaved Changes", 
            JOptionPane.YES_NO_CANCEL_OPTION
        );

        if (option == JOptionPane.YES_OPTION) {
            // Tente de sauvegarder. Si l'utilisateur annule la boîte de dialogue de sauvegarde, 
            // isModified restera true, et checkSaved retournera false.
            handleSaveFile(); 
            return !isModified; 
        } else if (option == JOptionPane.NO_OPTION) {
            return true; // Poursuit sans sauvegarder
        } else {
            return false; // Annule l'action
        }
    }

    // --- MAIN METHOD ---
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new SimpleTextEditor();
        });
    }
}