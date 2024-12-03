package ua.wyverno.console.commands.syncconfig;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ua.wyverno.config.ConfigLoader;
import ua.wyverno.config.SyncConfig;
import ua.wyverno.console.Command;
import ua.wyverno.console.ConsoleCommand;

import java.io.IOException;

@ConsoleCommand(command = "/update-crowdin-dir-title",
        description = """
                    Update of the crowdin root directory Title for Sync-Config
                    Args: Title - title for root folder
                    Example: /update-crowdin-dir-title "Detroit: Become Human"
                             /update-crowdin-dir-title 'Beyond Two Souls'
                    """)
@Component
public class UpdateCrowdinDirTitleCommand implements Command {

    private final SyncConfig syncConfig;

    @Autowired
    public UpdateCrowdinDirTitleCommand(ConfigLoader configLoader) {
        this.syncConfig = configLoader.getSyncConfig();
    }

    @Override
    public void execute(String[] args) {
        if (args.length != 1) {
            String warnMessage = """
                    Must 1 argument - "title"
                    title - title for root folder
                    Example: /update-crowdin-dir-title "Detroit: Become Human"
                    """;
            System.out.println(warnMessage);
            return;
        }

        try {
            this.syncConfig.updateCrowdinDirectoryRootTitle(args[0]);
            System.out.println("Update dir title in Sync-Config.");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
