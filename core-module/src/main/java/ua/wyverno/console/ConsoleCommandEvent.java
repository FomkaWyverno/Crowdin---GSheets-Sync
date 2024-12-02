package ua.wyverno.console;

import java.util.Arrays;
import java.util.Objects;

public class ConsoleCommandEvent {
    private final String command;
    private final String[] args;

    public ConsoleCommandEvent(String rawCommand) {
        Objects.requireNonNull(rawCommand);
        if (rawCommand.isEmpty()) throw new IllegalArgumentException("Command string can't be empty!");

        String[] splitRawCommand = rawCommand.split(" ");
        this.command = splitRawCommand[0];

        if (splitRawCommand.length > 1) {
            this.args = Arrays.copyOfRange(splitRawCommand, 1, splitRawCommand.length);
        } else {
            this.args = new String[0];
        }
    }

    public String getCommand() {
        return command;
    }

    public String[] getArgs() {
        return args;
    }
}
