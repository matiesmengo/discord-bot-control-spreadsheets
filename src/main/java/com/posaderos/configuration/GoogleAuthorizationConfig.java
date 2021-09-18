package com.posaderos.configuration;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.Collections;

@Configuration
public class GoogleAuthorizationConfig {
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    private static final String APPLICATION_NAME = "posaderosBot";
    private static final String CREDENTIALS_PATH = "/google-sheets-client-secret.json";
    private static final String TOKENS_PATH = "googleSpreadSheets.xls";

    private Credential authorize() throws IOException {
        final InputStream in = GoogleAuthorizationConfig.class.getResourceAsStream(CREDENTIALS_PATH);

        return GoogleCredential.fromStream(in)
                .createScoped(Collections.singleton(SheetsScopes.SPREADSHEETS));
    }

    public Sheets getSheetsService() throws IOException, GeneralSecurityException {
        return new Sheets.Builder(GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY, authorize())
                .setApplicationName(APPLICATION_NAME)
                .build();
    }
}
