import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

public class GoogleDocsChecker {
    private static final String DOC_URL_PREFIX = "https://docs.google.com/spreadsheets/d/" ;
    private static String docId;
    private static String botToken;
    private static String chatId;
    private static String lastContentHash;

    public static void main(String[] args) {
        try {
            JsonObject credentials = loadCredentials();
            Timer timer = new Timer();
            timer.schedule(new CheckDocumentTask(), 0, 10 * 60 * 1000); // каждые 10 минут
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static JsonObject loadCredentials() throws Exception {
        FileReader reader = new FileReader("src/main/resources/credentials.json");
        return JsonParser.parseReader(reader).getAsJsonObject();
    }
    static class CheckDocumentTask extends TimerTask {
        @Override
        public void run() {
            try {
                String currentContent = getDocumentContent();
                String currentContentHash = Integer.toString(currentContent.hashCode());

                if (lastContentHash == null || !lastContentHash.equals(currentContentHash)) {
                    lastContentHash = currentContentHash;
                    sendTelegramMessage("Документ был отредактирован!");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private String getDocumentContent() throws Exception {
            // Получение HTML-содержимого страницы
            // TODO
//            CloseableHttpClient httpClient = HttpClients.createDefault();
//            HttpGet request = new HttpGet(DOC_URL);
//            HttpResponse response = httpClient.execute(request);
//            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
//            StringBuilder content = new StringBuilder();
//            String line;
//
//            while ((line = reader.readLine()) != null) {
//                content.append(line);
//            }
//
//            reader.close();
//            return content.toString();
            return "";
        }

        private void sendTelegramMessage(String message) throws Exception {
            //TODO
//            String url = "https://api.telegram.org/bot" + TELEGRAM_BOT_TOKEN + "/sendMessage?chat_id=" + CHAT_ID + "&text=" + message;
//
//            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
//            conn.setRequestMethod("GET");
//
//            if (conn.getResponseCode() == 200) {
//                System.out.println("Сообщение отправлено: " + message);
//            } else {
//                System.out.println("Ошибка отправки сообщения: " + conn.getResponseCode());
//            }
        }
    }
}
