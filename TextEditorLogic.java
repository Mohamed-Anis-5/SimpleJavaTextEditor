import javax.swing.*;
import java.io.*;
import java.awt.Font;
import java.awt.Component;

public class TextEditorLogic {
    private JTextArea textArea;
    private Component parent; // Used for JOptionPane dialogs

    // State variables
    private File currentFile = null; 
    private boolean isModified = false;
    private float currentFontSize = 16f; 

    // Constructor requires the JTextArea and a parent component for dialogs
    public TextEditorLogic(JTextArea textArea, Component parent) {
        this.textArea = textArea;
        this.parent = parent;
    }

    // --- State Accessors ---
    public boolean isModified() {
        return isModified;
    }

    public void setModified(boolean modified) {
        isModified = modified;
    }

    public File getCurrentFile() {
        return currentFile;
    }
    
    // --- Core Methods ---

    public void handleNewFile() {
        textArea.setText("");
        currentFile = null;
        isModified = false;
        // The frame (TextEditorFrame) will be responsible for calling updateTitle()
    }

    public void handleOpenFile(JFileChooser fileChooser) {
        int returnValue = fileChooser.showOpenDialog(parent);

        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            
            try (BufferedReader reader = new BufferedReader(new FileReader(selectedFile))) {
                StringBuilder content = new StringBuilder();
                String line;
                
                while ((line = reader.readLine()) != null) {
                    content.append(line).append("\n");
                }
                
                textArea.setText(content.toString());
                currentFile = selectedFile;
                isModified = false;
                
            } catch (FileNotFoundException ex) {
                JOptionPane.showMessageDialog(parent, "File not found: " + selectedFile.getName(), "Error", JOptionPane.ERROR_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(parent, "Error reading file: " + selectedFile.getName(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void handleSaveFile(JFileChooser fileChooser) {
        File fileToSave = currentFile;

        if (fileToSave == null) {
            int returnValue = fileChooser.showSaveDialog(parent);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                fileToSave = fileChooser.getSelectedFile();
            } else {
                return;
            }
        }

        if (fileToSave != null) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileToSave))) {
                String content = textArea.getText();
                writer.write(content);
                
                currentFile = fileToSave;
                isModified = false;
                
                JOptionPane.showMessageDialog(parent, 
                    "File saved successfully!", 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE
                );
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(parent, 
                    "Error saving file: " + ex.getMessage(), 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }
    
    public void handleFontSize(float delta) {
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
    
    public float getCurrentFontSize() {
        return currentFontSize;
    }
}
