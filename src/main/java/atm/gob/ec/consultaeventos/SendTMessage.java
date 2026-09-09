/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package atm.gob.ec.consultaeventos;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 *
 * @author erik.flores 
 * 
 * https://api.telegram.org/bot5565295977:AAHS1KW10CMcDuxTVD4olf6Py8xxvrVU9dk/sendMessage?chat_id=-4058484447&text=HolaTest
 */
public class SendTMessage { 
    
    static String strTelegramUrl = "https://api.telegram.org/bot%s/sendMessage?chat_id=%s&text=%s";
    static String strapiToken = "5565295977:AAHS1KW10CMcDuxTVD4olf6Py8xxvrVU9dk";
//    static String strChatId="1136961221";
    static String strChatId = "-4058484447";
    
    public static void sendToTelegram() {
        String urlString = strTelegramUrl; 

        //Add Telegram token (given Token is fake)
        String apiToken = strapiToken; 
      
        //Add chatId (given chatId is fake)
        String chatId = strChatId; 
        String text = "Hello world! desde Java";

        urlString = String.format(urlString, apiToken, chatId, text);

        try {
            URL url = new URL(urlString);
            URLConnection conn = url.openConnection();
            InputStream is = new BufferedInputStream(conn.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static void sendToTelegram(String strTelegramUrl, String strapiToken, String strChatId, String strText) {
        String urlString = strTelegramUrl;

        //Add Telegram token (given Token is fake)
        String apiToken = strapiToken;
      
        //Add chatId (given chatId is fake)
        String chatId = strChatId;
        String text = strText;
        
        if (!strText.equals("")){
            urlString = String.format(urlString, apiToken, chatId, text);

            try {
                URL url = new URL(urlString);
                URLConnection conn = url.openConnection();
                InputStream is = new BufferedInputStream(conn.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    public static void main(String[] arg){
        sendToTelegram();
    }
}


