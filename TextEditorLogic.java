import javax.swing.*;//Importe toutes les classes Swing
import java.io.*;//Importe toutes les classes d’entrée/sortie (Input/Output)
import java.awt.Font;//Importe la classe Font
import java.awt.Component;//Importe la classe Component

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
    public boolean isModified() {//Indique si le texte a été modifié
        return isModified;
    }

    public void setModified(boolean modified) {// Modifie l’état de modificationUtilisée quand l’utilisateur écrit dans le texte
        isModified = modified;
    }

    public File getCurrentFile() {//Retourne le fichier actuellement ouvert
        return currentFile;
    }
    
    // --- Core Methods ---

    public void handleNewFile() {
        textArea.setText("");
        currentFile = null;
        isModified = false;
      //Efface le contenu de la zone de texte Réinitialise le fichier courant (currentFile = null)Indique qu’il n’y a pas de modification
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
    }//Ouvre une fenêtre pour choisir un fichier Si l’utilisateur valide : lit le fichier ligne par ligne affiche le contenu dans la zone de texte Met à jour :currentFile isModified = false

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
    }//Le fichier existe déjà :Enregistre directement 🔹 Nouveau fichier :Ouvre une fenêtre “Enregistrer sous” Après l’enregistrement : Écrit le texte dans le fichier Met à jour : currentFile isModified = false Affiche un message de succès
    
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
    }//Empêche une taille trop petite ou trop grande
    
    public float getCurrentFontSize() {
        return currentFontSize;
    }//Retourne la taille actuelle de la police Utile pour afficher la taille dans le menu ou la barre d’état
}
