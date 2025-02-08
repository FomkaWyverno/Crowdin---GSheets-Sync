package ua.wyverno.google.sheets;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.AuthorizationCodeRequestUrl;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.java6.auth.oauth2.VerificationCodeReceiver;
import com.google.api.client.util.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

public class WrapperAuthorizationCodeInstalledApp extends AuthorizationCodeInstalledApp {
    private static final Logger logger = LoggerFactory.getLogger(WrapperAuthorizationCodeInstalledApp.class);

    public WrapperAuthorizationCodeInstalledApp(AuthorizationCodeFlow flow, VerificationCodeReceiver receiver) {
        super(flow, receiver);
    }

    public WrapperAuthorizationCodeInstalledApp(AuthorizationCodeFlow flow, VerificationCodeReceiver receiver, Browser browser) {
        super(flow, receiver, browser);
    }

    @Override
    protected void onAuthorization(AuthorizationCodeRequestUrl authorizationUrl) throws IOException {
        String url = authorizationUrl.build();
        Preconditions.checkNotNull(url);
        try {
            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("win")) {
                List<String> cmd = List.of("rundll32", "url.dll", "FileProtocolHandler", url);
                new ProcessBuilder()
                        .command(cmd)
                        .start();
            } else {
                // Якщо система не Windows, виводимо посилання для відкриття вручну
                System.out.println("Неможливо автоматично відкрити браузер. Відкрийте наступний URL вручну:");
                System.out.println(url);
            }
        } catch (IOException e) {
            logger.error("Browser could not be opened. Copy the URL and open it manually.");
            System.out.println(url);
            throw e;
        }
    }
}
