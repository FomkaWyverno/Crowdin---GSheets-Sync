package ua.wyverno.crowdin;

import com.crowdin.client.Client;
import com.crowdin.client.core.model.Credentials;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ua.wyverno.config.Config;
import ua.wyverno.config.ConfigLoader;

@Component
public class CrowdinApiClient {

    private final Client crowdinClient;

    @Autowired
    public CrowdinApiClient(ConfigLoader configLoader) {
        Config config = configLoader.getConfig();
        Credentials credentials = new Credentials(config.getToken(), null);
        this.crowdinClient = new Client(credentials);
    }

    public Client getCrowdinClient() {
        return this.crowdinClient;
    }
}
