import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

public class GoogleDocsChecker {

    // Префікс для доступу до Google Sheets та постфікс для експорту до CSV
    private static final String DOC_URL_PREFIX = "https://docs.google.com/spreadsheets/d/";
    private static final String CSV_POSTFIX = "/export?format=csv&gid=0";  // Використовуємо CSV замість HTML

    private static String docId;
    private static String botToken;
    private static String chatId;
    private static String lastContentHash;

    public static void main(String[] args) {
        try {
            JsonObject credentials = loadCredentials();
            docId = credentials.get("google.dockId").getAsString();
            botToken = credentials.get("telegram.token").getAsString();
            chatId = credentials.get("telegram.chatId").getAsString();

            Timer timer = new Timer();
            timer.schedule(new CheckDocumentTask(), 0, 10 * 60 * 1000); // Кожні 10 хвилин
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
                //System.out.println(currentContent);
                String currentContentHash = Integer.toString(currentContent.hashCode());
                System.out.println("Checking...");
                // Перевіряємо на зміну
                if (lastContentHash == null || !lastContentHash.equals(currentContentHash)) {
                    lastContentHash = currentContentHash;
                    System.out.println("Document has been edited.");
                    sendTelegramMessage("Document edited");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Отримання вмісту Google Sheets у форматі CSV
        private String getDocumentContent() throws Exception {
            String csvUrl = DOC_URL_PREFIX + docId + CSV_POSTFIX;  // Формуємо URL для експорту до CSV
            CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpGet request = new HttpGet(csvUrl);
            HttpResponse response = httpClient.execute(request);

            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            StringBuilder content = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");  // Зчитуємо CSV построчно
            }

            reader.close();
            return content.toString();  // Повертаємо вміст таблиці
        }

        // Надсилання повідомлення до Telegram
        private void sendTelegramMessage(String message) throws Exception {
            String url = "https://api.telegram.org/bot" + botToken + "/sendMessage?chat_id=" + chatId + "&text=" + message;

            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("GET");

            if (conn.getResponseCode() == 200) {
                System.out.println("Повідомлення надіслано: " + message);
            } else {
                System.out.println("Помилка надсилання повідомлення: " + conn.getResponseCode());
            }
        }
    }
}
