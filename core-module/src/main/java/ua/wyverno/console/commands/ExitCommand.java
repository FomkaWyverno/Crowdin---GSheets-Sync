package ua.wyverno.console.commands;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ua.wyverno.AppState;
import ua.wyverno.console.Command;
import ua.wyverno.console.ConsoleCommand;

@ConsoleCommand(command = "/exit", description = "To exit from app.")
@Component
public class ExitCommand implements Command {
    private final AppState appState;

    @Autowired
    public ExitCommand(AppState appState) {
        this.appState = appState;
    }

    @Override
    public void execute(String[] args) {
        this.appState.stop();
    }
}
