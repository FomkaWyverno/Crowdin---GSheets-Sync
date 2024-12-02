package ua.wyverno.console;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import ua.wyverno.console.commands.HelpCommand;

import java.util.Map;

@Component
public class ConsoleCommandHandler implements CommandHandler {
    private final Map<String, Command> commandsMap;
    private final HelpCommand helpCommand;

    @Autowired
    public ConsoleCommandHandler(HelpCommand helpCommand, CommandCollector commandCollector) {
        this.commandsMap = commandCollector.getCommandMap();
        this.helpCommand = helpCommand;
    }

    @EventListener
    @Override
    public void handle(ConsoleCommandEvent event) {
        if (this.commandsMap.containsKey(event.getCommand())) {
            try {
                this.commandsMap.get(event.getCommand()).execute(event.getArgs());
            } catch (Exception e) {
                System.err.println("An error occurred while executing the command: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("Unknown command.");
            this.helpCommand.execute(new String[0]);
        }
    }
}
