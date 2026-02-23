package temp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class HttpMain {

	public static void main(String[] args) throws IOException {
		String uriStr =
                "https://raw.githubusercontent.com/jdenny/jardenApps/refs/heads/master/questions.txt";
		List<String> questions = new HttpMain().downloadQuestions(uriStr);
        if (true) return;
	}
    private List<String> downloadQuestions(String urlString) {
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
                        new InputStreamReader(connection.getInputStream())
                );
                String line;
                while ((line = reader.readLine()) != null) {
                    if (!line.trim().isEmpty()) {
                        questions.add(line.trim());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace(); // Replace with Log.e in production
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

