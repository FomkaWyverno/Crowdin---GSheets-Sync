package ua.wyverno.crowdin;

import com.crowdin.client.Client;
import com.crowdin.client.core.model.Credentials;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ua.wyverno.config.CoreConfig;
import ua.wyverno.config.ConfigLoader;

@Component
public class CrowdinApiClient {

    private final Client crowdinClient;

    @Autowired
    public CrowdinApiClient(ConfigLoader configLoader) {
        CoreConfig coreConfig = configLoader.getCoreConfig();
        Credentials credentials = new Credentials(coreConfig.getCrowdinToken(), null);
        this.crowdinClient = new Client(credentials);
    }

    public Client getCrowdinClient() {
        return this.crowdinClient;
    }
}
