/**
 *
 * @author erik.flores
 */

package atm.gob.ec.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import atm.gob.ec.security.CryptoService;
import atm.gob.ec.security.AesCryptoService;

public class TelegramService {

    private static final Logger logger = LogManager.getLogger(TelegramService.class);
    private static String secret = System.getProperty("atm.crypto.key");
    private final String botToken;
    private final String chatId;

    public TelegramService(Properties config) {
        CryptoService crypto = new AesCryptoService(secret);
        this.botToken = crypto.decrypt(config.getProperty("TELEGRAM.TOKEN"));
        this.chatId = crypto.decrypt(config.getProperty("TELEGRAM.CHATID"));
    }

    // 🌟 SE ELIMINÓ EL 'throws Exception' PARA GARANTIZAR LA CONTINUIDAD DEL SERVICIO
    public void sendMessage(String message) {
        logger.info("Enviando mensaje a Telegram... ");
        
        try {
            String urlString =
                    "https://api.telegram.org/bot"
                    + botToken
                    + "/sendMessage";

            URL url = new URL(urlString);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");

            String parameters = "chat_id=" + URLEncoder.encode(chatId, "UTF-8") + "&text=" + URLEncoder.encode(message, "UTF-8");

            try (OutputStream output = connection.getOutputStream()) {
                output.write(parameters.getBytes(StandardCharsets.UTF_8));
            }

            int responseCode = connection.getResponseCode();

            if (responseCode != HttpURLConnection.HTTP_OK) {
                StringBuilder response = new StringBuilder();

                try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getErrorStream(), StandardCharsets.UTF_8))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                }

                // 🌟 SE LOGUEA EL ERROR PERO NO SE LANZA EXCEPCIÓN
                logger.error("Telegram respondió HTTP " + responseCode + ": " + response);
                return;
            }

            connection.disconnect();
            logger.info("Notificación enviada via Telegram.");

        } catch (Exception ex) {
            // 🌟 CUALQUIER FALLO DE PROTOCOLO, PROXY O HANDSHAKE MUERE AQUÍ EN EL LOG
            logger.error("No se pudo enviar la notificacion por Telegram: " + ex.getMessage(), ex);
        }
    }
}
