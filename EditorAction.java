enum EditorAction {
    NEW("New"), 
    OPEN("Open"), 
    SAVE("Save"),
    INCREASE_FONT("Increase Font (+)"), 
    DECREASE_FONT("Decrease Font (-)");

    private final String command;

    EditorAction(String command) {
        this.command = command;
    }

    public String getCommand() {
        return command;
    }
}
