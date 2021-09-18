package com.posaderos.configuration;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

@Configuration
public class GoogleAuthorizationConfig {
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    private static final String APPLICATION_NAME = "posaderosBot";
    private static final String CREDENTIALS_PATH = "/google-sheets-client-secret.json";
    private static final String TOKENS_PATH = "googleSpreadSheets.xls";

    private Credential authorize() throws IOException, GeneralSecurityException {
        final InputStream in = GoogleAuthorizationConfig.class.getResourceAsStream(CREDENTIALS_PATH);
        final GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        final List<String> scopes = Collections.singletonList(SheetsScopes.SPREADSHEETS);

        final GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY, clientSecrets, scopes)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_PATH)))
                .setAccessType("offline")
                .build();

        final LocalServerReceiver localServerReceiver = new LocalServerReceiver.Builder()
                .setHost("https://bot-posaderos.herokuapp.com")
                .setPort(8888)
                .build();

        return new AuthorizationCodeInstalledApp(flow, localServerReceiver)
                .authorize("user");
    }

    public Sheets getSheetsService() throws IOException, GeneralSecurityException {
        return new Sheets.Builder(GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY, authorize())
                .setApplicationName(APPLICATION_NAME)
                .build();
    }
}
