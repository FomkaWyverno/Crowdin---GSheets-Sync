package ua.wyverno.console.commands;

import org.springframework.stereotype.Component;

@ConsoleCommand(command = "/exit", description = "To exit from app.")
@Component
public class ExitCommand implements Command {
    @Override
    public void execute(String[] args) {}
}
