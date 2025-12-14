import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;

public class TextEditorFrame extends JFrame implements ActionListener {

    // 1. COMPONENT DECLARATION
    private JTextArea textArea;
    private JFileChooser fileChooser;
    
    // 2. LOGIC INSTANCE (Composition: holds the logic object)
    private TextEditorLogic logic; 

    // --- CONSTRUCTOR ---
    public TextEditorFrame() {
        super("Simple Text Editor");

        // 3. INITIALIZATION
        textArea = new JTextArea();
        fileChooser = new JFileChooser();
        
        // Initialize logic and pass the JTextArea reference
        logic = new TextEditorLogic(textArea, this); 
        
        initializeComponents();
        setupLayout();
        setupListeners();
        
        // 4. FRAME SETUP
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); // Handled by WindowListener
        updateTitle();
        setVisible(true);
    }

    private void initializeComponents() {
        // Initialize Menu Items (can be done inline or in a method)
        // ... (JMenuBar, JMenu, JMenuItem declarations remain in this class)
        
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenu formatMenu = new JMenu("Format");
        
        JMenuItem newItem = new JMenuItem(EditorAction.NEW.getCommand());
        JMenuItem openItem = new JMenuItem(EditorAction.OPEN.getCommand());
        JMenuItem saveItem = new JMenuItem(EditorAction.SAVE.getCommand());
        
        JMenuItem increaseFontItem = new JMenuItem(EditorAction.INCREASE_FONT.getCommand());
        JMenuItem decreaseFontItem = new JMenuItem(EditorAction.DECREASE_FONT.getCommand());
        
        // Setup Menu Bar
        fileMenu.add(newItem);
        fileMenu.add(openItem);
        fileMenu.add(saveItem);
        menuBar.add(fileMenu);

        formatMenu.add(increaseFontItem);
        formatMenu.add(decreaseFontItem);
        menuBar.add(formatMenu);
        
        this.setJMenuBar(menuBar);

        // Set action commands and add listeners
        newItem.setActionCommand(EditorAction.NEW.getCommand());
        openItem.setActionCommand(EditorAction.OPEN.getCommand());
        saveItem.setActionCommand(EditorAction.SAVE.getCommand());
        increaseFontItem.setActionCommand(EditorAction.INCREASE_FONT.getCommand());
        decreaseFontItem.setActionCommand(EditorAction.DECREASE_FONT.getCommand());
        
        newItem.addActionListener(this);
        openItem.addActionListener(this);
        saveItem.addActionListener(this);
        increaseFontItem.addActionListener(this);
        decreaseFontItem.addActionListener(this);
    }

    private void setupLayout() {
        JScrollPane scrollPane = new JScrollPane(textArea);
        textArea.setFont(textArea.getFont().deriveFont(logic.getCurrentFontSize()));
        add(scrollPane);
    }

    private void setupListeners() {
        // Document Listener: updates the logic and the frame title
        textArea.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                logic.setModified(true);
                updateTitle();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                logic.setModified(true);
                updateTitle();
            }

            @Override
            public void changedUpdate(DocumentEvent e) { }
        });
        
        // Window Listener: handles close confirmation
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

    // --- ACTION LISTENER: Delegates action to the Logic layer ---
    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();

        if (command.equals(EditorAction.NEW.getCommand())) {
            if (checkSaved()) {
                logic.handleNewFile();
                updateTitle();
            }
        } else if (command.equals(EditorAction.OPEN.getCommand())) {
            if (checkSaved()) {
                logic.handleOpenFile(fileChooser);
                updateTitle();
            }
        } else if (command.equals(EditorAction.SAVE.getCommand())) {
            logic.handleSaveFile(fileChooser);
            updateTitle();
        } else if (command.equals(EditorAction.INCREASE_FONT.getCommand())) { 
            logic.handleFontSize(2f);
        } else if (command.equals(EditorAction.DECREASE_FONT.getCommand())) { 
            logic.handleFontSize(-2f);
        }
    }
    
    // --- Helper Methods ---
    
    private void updateTitle() {
        String title = "Simple Text Editor";
        String fileName = (logic.getCurrentFile() != null) ? logic.getCurrentFile().getName() : "Untitled";
        
        if (logic.isModified()) {
            setTitle(title + " - " + fileName + "*");
        } else {
            setTitle(title + " - " + fileName);
        }
    }

    private boolean checkSaved() {
        if (!logic.isModified()) {
            return true;
        }
        
        int option = JOptionPane.showConfirmDialog(
            this, 
            "The file has been modified. Do you want to save changes?", 
            "Unsaved Changes", 
            JOptionPane.YES_NO_CANCEL_OPTION
        );

        if (option == JOptionPane.YES_OPTION) {
            logic.handleSaveFile(fileChooser);
            return !logic.isModified(); // Check if logic successfully reset isModified
        } else if (option == JOptionPane.NO_OPTION) {
            return true;
        } else {
            return false;
        }
    }
}
