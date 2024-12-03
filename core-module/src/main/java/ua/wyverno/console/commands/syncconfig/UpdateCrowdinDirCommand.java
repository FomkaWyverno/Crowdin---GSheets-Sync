package ua.wyverno.console.commands.syncconfig;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ua.wyverno.config.ConfigLoader;
import ua.wyverno.config.SyncConfig;
import ua.wyverno.console.Command;
import ua.wyverno.console.ConsoleCommand;

import java.io.IOException;

@ConsoleCommand(command = "/update-crowdin-dir",
        description = """
                Update the Crowdin root directory for Sync-Config, where the localization is located.
                Args: path - root directory for localization. Must be style in Path. Can't contain: \\ / : * ? " < > |
                Example: /update-crowdin-dir 'Detroit Become Human'
                """)
@Component
public class UpdateCrowdinDirCommand implements Command {

    private final SyncConfig syncConfig;

    @Autowired
    public UpdateCrowdinDirCommand(ConfigLoader configLoader) {
        this.syncConfig = configLoader.getSyncConfig();
    }

    /**
     * Перевіряє чи валідний шлях
     * @param path шлях
     * @return повертає чи валідний шлях
     */
    private boolean isPathValid(String path) {
        String forbiddenCharacters = "\\/:*?\"<>|";
        for (char c : forbiddenCharacters.toCharArray()) {
            if (path.indexOf(c) >= 0) return false;
        }
        return true;
    }

    @Override
    public void execute(String[] args) {
        if (args.length != 1) {
            String warnMessage = """
                    Must 1 argument - "path"
                    path - root directory for localization. Must be style in Path. Can't contain: \\ / : * ? " < > |
                    Example: /update-crowdin-dir Detroit-Become-Human
                    """;
            System.out.println(warnMessage);
            return;
        }
        String path = args[0];
        if (!this.isPathValid(path)) {
            String warnMessage = String.format("""
                    Path: %s
                    Path can't contain: : \\ / * ? " < > |
                    """, path);
            System.out.println(warnMessage);
            return;
        }

        try {
            this.syncConfig.updateCrowdinDirectoryRoot(path);
            System.out.println("Update root dir in Sync-Config.");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
