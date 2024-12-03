package ua.wyverno.console;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConsoleCommandEvent {
    private final String command;
    private final String[] args;

    public ConsoleCommandEvent(String rawCommand) {
        Objects.requireNonNull(rawCommand);
        if (rawCommand.isEmpty()) throw new IllegalArgumentException("Command string can't be empty!");

        List<String> parts = getParts(rawCommand);

        this.command = parts.get(0);
        this.args = parts.subList(1, parts.size()).toArray(new String[0]);
    }

    private List<String> getParts(String rawCommand) {
        Pattern pattern = Pattern.compile("\"([^\"]*)\"|'([^']*)'|\\S+");
        Matcher matcher = pattern.matcher(rawCommand);

        List<String> parts = new ArrayList<>();
        while (matcher.find()) {
            if (matcher.group(1) != null) {
                parts.add(matcher.group(1));
            } else if (matcher.group(2) != null) {
                parts.add(matcher.group(2));
            } else {
                parts.add(matcher.group());
            }
        }

        if (parts.isEmpty()) throw new IllegalArgumentException("Command string can't be empty!");
        return parts;
    }

    public String getCommand() {
        return command;
    }

    public String[] getArgs() {
        return args;
    }
}
