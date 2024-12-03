package ua.wyverno.console.commands;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import ua.wyverno.console.Command;
import ua.wyverno.console.CommandCollector;
import ua.wyverno.console.ConsoleCommand;

import java.util.*;

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

        int maxLineLength = 0;
        List<String> formattedCommands = new ArrayList<>();
        // Обробляємо кожну команду з описом
        for (Map.Entry<String, String> entry : this.commandCollector.getCommandDescription().entrySet()) {
            String command = entry.getKey();
            String description = entry.getValue();

            String paddedCommand = String.format("%-"+maxCommandLength+"s", command);
            StringBuilder formattedCommand = new StringBuilder(paddedCommand).append(" - ");

            // Обробляємо багаторядковий опис
            String[] descriptionLines = description.split("\n");
            for (int i = 0; i < descriptionLines.length; i++) {
                if (i > 0) {
                    // Вирівнюємо після першого рядка опису, під довжину команди
                    formattedCommand.append(" ".repeat(maxCommandLength+3));
                }
                formattedCommand.append(descriptionLines[i]).append("\n");
            }
            String fullCommandWithDescription = formattedCommand.toString();
            formattedCommands.add(fullCommandWithDescription);

            // Оновлюємо максимальну довжину рядка
            maxLineLength = Math.max(maxLineLength, fullCommandWithDescription.lines()
                    .mapToInt(String::length)
                    .max()
                    .orElse(0));
        }
        // Заголовок листа з командами
        String titleList = this.createCenteredTitle("List Commands", maxLineLength);
        // Лінія розмежування між командами
        String line = "-".repeat(maxLineLength) + "\n";

        StringBuilder messageBuilder = new StringBuilder(titleList);
        Collections.sort(formattedCommands);
        for (String formatedCommand : formattedCommands) {
            messageBuilder.append(formatedCommand).append(line);
        }

        System.out.println(messageBuilder);
    }

    private String createCenteredTitle(String title, int maxLineLength) {
        int leftSideLength = (maxLineLength - title.length()) / 2;
        int rightSideLength = maxLineLength - title.length() - leftSideLength;
        return "-".repeat(leftSideLength) + title + "-".repeat(rightSideLength) + "\n";
    }
}
