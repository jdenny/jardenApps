package jarden.tcp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by john.denny@gmail.com on 23/02/2026.
 */
public class HttpClient {
    public List<String> downloadQuestions(String urlString) throws IOException, URISyntaxException {
        List<String> questions = new ArrayList<>();

        HttpURLConnection connection = null;
        BufferedReader reader = null;

        try {
            URI uri = new URI(urlString);
            URL url = uri.toURL();
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                reader = new BufferedReader(
                        new InputStreamReader(connection.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    if (!line.trim().isEmpty()) {
                        questions.add(line.trim());
                    }
                }
            } else {
                throw new IOException("HttpURLConnection.responseCode=" + responseCode);
            }
        } finally {
            try {
                if (reader != null) reader.close();
            } catch (IOException ignored) {}
            if (connection != null) {
                connection.disconnect();
            }
        }
        return questions;
    }
}
