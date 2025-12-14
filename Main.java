import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        // Ensure GUI updates happen on the Event Dispatch Thread (thread safety)
        SwingUtilities.invokeLater(() -> {
            new TextEditorFrame();
        });
    }
}
