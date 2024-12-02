package ua.wyverno.console.commands;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import ua.wyverno.console.CommandCollector;

@ConsoleCommand(command = "/help", description = "Print all commands with description")
@Component
public class HelpCommand implements Command {

    @Autowired
    @Lazy
    private CommandCollector commandCollector;

    @Override
    public void execute(String[] args) {
        int maxCommandLength = this.commandCollector.getCommandDescription().keySet().stream()
                .mapToInt(String::length)
                .max()
                .orElse(1);


        StringBuilder messageBuilder = new StringBuilder("List commands -\n");
        this.commandCollector.getCommandDescription().forEach((command, description) -> {
            String paddedCommand = String.format("%-"+maxCommandLength+"s", command);
            messageBuilder.append(paddedCommand).append(" - ").append(description).append("\n");
        });

        System.out.println(messageBuilder.deleteCharAt(messageBuilder.length() - 1));
    }
}
