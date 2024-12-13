package ua.wyverno.google.sheets;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponseException;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Component
public class GoogleSheetsAuth {
    private static final Logger logger = LoggerFactory.getLogger(GoogleSheetsAuth.class);
    private static final String APPLICATION_NAME = "Google Sheets API Java Crowdin-Sync";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    /**
     * Global instance of the scopes required by this quickstart.
     * If modifying these scopes, delete your previously saved tokens/ folder.
     */
    private static final List<String> SCOPES = Collections.singletonList(SheetsScopes.SPREADSHEETS);
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

    private Sheets service;
    private Credential credential;

    @Autowired
    public GoogleSheetsAuth() throws GeneralSecurityException, IOException {
        this.initializeService();
    }

    private void initializeService() throws IOException, GeneralSecurityException {
        this.credential = this.authorize();
        this.service = new Sheets.Builder(GoogleNetHttpTransport.newTrustedTransport(),
                    GsonFactory.getDefaultInstance(),
                    this.credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    /**
     * Creates an authorized Credential object.
     *
     * @return An authorized Credential object.
     * @throws IOException If the credentials.json file cannot be found.
     */
    protected Credential authorize()
            throws IOException, GeneralSecurityException {
        // Load client secrets.
        InputStream in = GoogleSheetsService.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets =
                GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    /**
     * Ensures the token is valid before making API requests.
     *
     * @throws IOException If the token refresh fails.
     */
    private void ensureValidToken() throws IOException {
        if (this.credential == null) throw new IllegalStateException("Credential has not been initialized.");

        try {
            this.credential.refreshToken();
        } catch (TokenResponseException e) {
            if (e.getStatusCode() == 400 && e.getStatusMessage().equals("Bad Request") &&
                e.getContent().contains("\"error_description\" : \"Token has been expired or revoked.\"")) {
                logger.warn("Token refresh failed. Requesting new authorization...");
                try {
                    this.clearStoredTokens();
                    this.initializeService(); // Переініціалізовуємо сервіс
                } catch (GeneralSecurityException | IOException exception) {
                    throw new IOException("Failed reauthorized: " + exception.getMessage(), exception);
                }
            } else {
                throw e;
            }
        }
    }

    private void clearStoredTokens()  {
        File tokenDir = new File(TOKENS_DIRECTORY_PATH);
        if (tokenDir.exists()) {
            for (File file : Objects.requireNonNull(tokenDir.listFiles())) {
                //noinspection ResultOfMethodCallIgnored
                file.delete(); // Delete all stored tokens
            }
        }
    }

    public Sheets getService() throws IOException {
        this.ensureValidToken();
        return service;
    }
}
