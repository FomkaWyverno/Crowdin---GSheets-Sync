package ua.wyverno.console;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import ua.wyverno.console.commands.Command;
import ua.wyverno.console.commands.ConsoleCommand;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class CommandCollector {
    private final Map<String, Command> commandMap;
    private final Map<String, String> commandDescription;

    @Autowired
    public CommandCollector(ApplicationContext context) {
        Map<String, Object> beansMap = context.getBeansWithAnnotation(ConsoleCommand.class);
        this.commandMap = Collections.unmodifiableMap(beansMap.values().stream()
                .filter(bean -> Command.class.isAssignableFrom(bean.getClass()))
                .map(bean -> (Command) bean)
                .collect(Collectors.toMap(
                        bean -> bean.getClass().getAnnotation(ConsoleCommand.class).command(),
                        bean -> bean,
                        (exists, replacement) -> replacement)));
        this.commandDescription = Collections.unmodifiableMap(this.commandMap.values().stream()
                .collect(Collectors.toMap(
                        bean -> bean.getClass().getAnnotation(ConsoleCommand.class).command(),
                        bean -> bean.getClass().getAnnotation(ConsoleCommand.class).description(),
                        (exists, replacement) -> replacement)));
    }

    public Map<String, Command> getCommandMap() {
        return commandMap;
    }

    public Map<String, String> getCommandDescription() {
        return commandDescription;
    }
}
