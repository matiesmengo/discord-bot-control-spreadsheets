package com.posaderos.services;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.BatchGetValuesResponse;
import com.google.api.services.sheets.v4.model.Sheet;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.posaderos.configuration.GoogleAuthorizationConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class GoogleSheetsService {
    @Value("${spreadsheet.id}")
    private String spreadsheetId;

    private final GoogleAuthorizationConfig googleAuthorizationConfig;

    @Autowired
    public GoogleSheetsService(final GoogleAuthorizationConfig googleAuthorizationConfig) {
        this.googleAuthorizationConfig = googleAuthorizationConfig;
    }

    public void buyClanCoins(final String discordUser, final String oro) {
        try {
            final Sheets sheetsService = googleAuthorizationConfig.getSheetsService();

            final ValueRange body = new ValueRange()
                    .setValues(Arrays.asList(
                            Arrays.asList(discordUser, oro)));

            sheetsService.spreadsheets().values()
                    .append(spreadsheetId, "Sheet2!A1", body)
                    .setValueInputOption("USER_ENTERED")
                    .setInsertDataOption("INSERT_ROWS")
                    .setIncludeValuesInResponse(true)
                    .execute();

        } catch (final Exception ignored) {}
    }

    public String getMyClanCoins(final String discordUser) {
        try {
            final Sheets sheetsService = googleAuthorizationConfig.getSheetsService();

            final BatchGetValuesResponse readResult = sheetsService.spreadsheets().values()
                    .batchGet(spreadsheetId)
                    .setRanges(getRangeColumn(1))
                    .execute();

            final ValueRange januaryTotal = readResult.getValueRanges().get(0);

            return String.valueOf(januaryTotal.getValues()
                    .stream()
                    .filter(object -> discordUser.equalsIgnoreCase(String.valueOf(object.get(0))))
                    .map(object -> object.get(1))
                    .findFirst()
                    .orElse("This user are not configured into data base"));
        } catch (final Exception e) {
            return "?";
        }
    }

    private List<String> getRangeColumn(final Integer numColumn) {
        try {
            final Sheets sheetsService = googleAuthorizationConfig.getSheetsService();
            final Sheets.Spreadsheets.Get request = sheetsService.spreadsheets().get(spreadsheetId);
            final Spreadsheet spreadsheet = request.execute();
            final Sheet sheet = spreadsheet.getSheets().get(0);
            final int row = sheet.getProperties().getGridProperties().getRowCount();


            return Collections.singletonList("R1C" + numColumn + ":R".concat(String.valueOf(row))
                    .concat("C" + numColumn).concat(String.valueOf(numColumn + 1)));
        } catch (final Exception e) {
            return null;
        }
    }
}
