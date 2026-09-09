
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template archivoEx, choose Tools | Templates
 * and open the template in the editor.
 */
package atm.gob.ec.consultaeventos;

/**
 * 
 * @author erik.flores
 * 
 * 
 */

import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import atm.gob.ec.encriptacion.Encriptador;
import atm.gob.ec.entidad.ConsultaEventosMessages;
import atm.gob.ec.mail.SendMail;
import atm.gob.ec.utils.Utils;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import java.util.Properties;

public class ConsultaUltimoEventoCamara {
    
    LoggerContext context;
    static Logger logger, logger2; 
    String directorioSistema;
    Utils utl;
    String dirLog4j2;
    Properties propertie;
    String fechaReporte, emailBody; 
    File archivoEx = null;
    FileOutputStream outputStream = null;
    DateFormat dateFormat = null;
    Date date = null;  
    
    List<ConsultaEventosMessages> consultaEventos;
    
    static String strTelegramUrl = "";
    static String strapiToken = "";
    static String strChatId = ""; // SUPERGROUP ID ¿?
    
    String strTTranscurrido = "" ;
    String strPattern = "";
    
    String strCodError = "";
    
    public ConsultaUltimoEventoCamara() throws Exception {
        // TODO Auto-generated constructor stub
        super();
        
        /** 
         * OBTENER DIRECTORIO DEL SISTEMA
        */
        
        directorioSistema = Utils.getDirectorioSistema();

        propertie = Utils.getProperties();        
        
        /**
         * OBTENER LA RUTA DEL ARCHIVO log4j2.xml         
         */
        dirLog4j2 = directorioSistema + propertie.getProperty("LOG4J2.SUBDIRECTORY");
        System.out.println("Ruta LOG4J2: " + dirLog4j2);
        
        context = Utils.configureLogging();
        logger2 = LogManager.getLogger(ConsultaUltimoEventoCamara.class);  
        
        strTTranscurrido = this.getClass().getSimpleName() + ": " ;
        
        strPattern = "[^A-Za-z0-9.+()'@:%/]";
        
    }
    
    public void verificaEventos()  {
                
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet result2 = null;
        
        String us = propertie.getProperty("DB.MYSQLUSER");
        String pw = propertie.getProperty("DB.MYSQLPASSWD");
        String driver = propertie.getProperty("DB.MYSQLDRIVER");
        
        String url = propertie.getProperty("DB.MYSQLURL") ;
        
        String mensaje = propertie.getProperty("MAIL.BODY");
        
        String strSentencia = propertie.getProperty("SQL.Q1");

        String strIntervaloD = propertie.getProperty("SQL.INTERVALO_DIAS");
        String strIntervaloT = propertie.getProperty("SQL.INTERVALO_TIEMPO");
        
        consultaEventos = new ArrayList<>();
        
        String excelFilePath = "";
        
        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        date = new Date();
        
        int intContador = 0;
        int intTiempo = 0;
        
        logger2.info("Proceso de verificacion"); 
                
        emailBody = "<br>Resumen de camaras de fotoradar con un intervalo mayor a 1h sin transmitir eventos, emitido en " + dateFormat.format(date)+ "</b><br>";
        
        strTTranscurrido = strTTranscurrido + "Fecha Consulta: " + dateFormat.format(date) + "%0A" ; // + " - Camaras sin transmitir%0A ";
        
        try {
            
            connection = DriverManager.getConnection(url, us, Encriptador.decriptar(pw));
            
            preparedStatement = connection.prepareStatement(strSentencia);
            preparedStatement.setString(1, strIntervaloD);
            preparedStatement.setString(2, strIntervaloT);
            
            result2 = preparedStatement.executeQuery();
            
            String strSentencia2 = result2.getStatement().toString().substring(43);
            
            logger2.info("strSentencia2: " + strSentencia2);
            propertie.put("SQL.Q1", strSentencia2);
            
            ResultSetMetaData rstMetaData = result2.getMetaData();
            //int numberOfColumns = rstMetaData.getColumnCount(); 
            
            emailBody = emailBody + "<br><table border=2><tbody><tr>";
            
            for (int i = 1; i <= rstMetaData.getColumnCount(); i++)
                emailBody = emailBody + "<td align=\"center\"><b>" + rstMetaData.getColumnName(i).toUpperCase() + "</b></td>";                
            
            
            emailBody = emailBody + "</tr>";
                        
            while (result2.next()) {
                
                intTiempo = Integer.valueOf(result2.getString("tiempo_transcurrido")); 
                
                Duration d = Duration.ofSeconds(intTiempo);
                long longDias = d.toDays();
                long longHoras = d.toHours() % 24;
                long longMinutos = d.toMinutes() % 60;
                
                if (longHoras > 0){                     

                    String strIntervaloTranscurrido = (longDias>0?longDias + " dia(s) ":"") + (longHoras>0?longHoras + " hora(s) ":"") + (longMinutos>0?longMinutos + " minuto(s)":"");
                    
                    logger2.info("Camara: " + result2.getString("camara_serial") + " Tiempo H: " + intTiempo + " Tiempo T: " + strIntervaloTranscurrido);
                                        
                    intContador += 1;                    
                    
                    strTTranscurrido =  strTTranscurrido + "Camara: " + result2.getString("camara_serial") + " sin transmitir: " + strIntervaloTranscurrido + "%0A";
                    
                    emailBody = longHoras > 1 ? emailBody + "<tr style=\"background-color:#FFFFE0;\" >":emailBody + "<tr>";
                //else if (Integer.valueOf(result2.getString("tiempo_transcurrido")) >= 2)
                //    emailBody = emailBody + "<tr style=\"background-color: red; \" >";
                                
                    emailBody = emailBody 
                            + "<td align=\"center\" width:15px>" + (long) result2.getDouble("item") + "</td>"
                            + "<td align=\"center\" width:15px>" + result2.getString("grupo_camara") + "</td>"
                            + "<td align=\"center\" width:15px>" + result2.getString("location_id") + "</td>"
                            + "<td align=\"center\" width:15px>" + result2.getString("camara_serial") + "</td>"
                            + "<td align=\"left\" width:100px>" + result2.getString("direccion") + "</td>"
                            + "<td align=\"center\" width:15px>" + result2.getString("fecha_ultimo_evento") + "</td>"
                            + "<td align=\"center\" width:15px>" + strIntervaloTranscurrido + "</td>"
                            + "</tr>";
                }
                strCodError = "0";
            }            
            
            emailBody = emailBody + "</tbody></table><br>";
                          
        } catch (SQLException e) {
            e.printStackTrace();
            excelFilePath = "";
            strCodError = "1";
            strTTranscurrido =  strTTranscurrido + " - SQLException. Review log: " + e.getMessage().replaceAll(strPattern," ");
            
            emailBody = emailBody + "<br>" + e.getMessage();
            
            logger2.warn(e.getMessage().replaceAll(strPattern," "));
        } catch (NullPointerException e) {
            e.printStackTrace();
            excelFilePath = "";
            strCodError = "1";
            strTTranscurrido =  strTTranscurrido + " - NullPointerException. Review log: " + e.getMessage().replaceAll(strPattern," ");
            
            emailBody = emailBody + "<br>" + e.getMessage();
            
            logger2.warn(e.getMessage().replaceAll(strPattern," "));
        } catch (Exception e) {
            e.printStackTrace();
            excelFilePath = "";
            strCodError = "1";
            strTTranscurrido =  strTTranscurrido + " - Exception. Review log: " + e.getMessage().replaceAll(strPattern," ");
            
            emailBody = emailBody + "<br>" + e.getMessage();
            
            logger2.warn(e.getMessage().replaceAll(strPattern," "));
        } finally {
            try {
                if (result2 != null) result2.close();
                if (preparedStatement != null) preparedStatement.close();
                if (connection != null) connection.close();
            } catch (SQLException ex) {
                logger2.warn(ex);
            }
        }
        
        excelFilePath = "";
        
        if (intContador > 0){ 
//            logger2.info(emailBody);
            if (propertie.getProperty("MAIL.NOTIFICACION").toUpperCase().equals("Y")){
                enviaNotificacion(emailBody, excelFilePath);
            }
        }
        
        if (propertie.getProperty("TELEGRAM.NOTIFICACION").toUpperCase().equals("Y") && !strCodError.equals("")){
            strTelegramUrl = propertie.getProperty("TELEGRAM.URL");
            strapiToken = propertie.getProperty("TELEGRAM.BOTAPITOKEN");
            strChatId = propertie.getProperty("TELEGRAM.CHATID");
            SendTMessage.sendToTelegram(strTelegramUrl,strapiToken,strChatId, strTTranscurrido);
            logger2.info("Notificacion por Telegram");
        }
        
        logger2.info("Fin de verificacion");
        
    }
          
    private void enviaNotificacion(String ps_param, String ps_nombreArchivo){
        String mensaje;
        
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        date = new Date();
        
        String asunto =  propertie.getProperty("MAIL.SUBJECT") + " del " + dateFormat.format(date);
        String email;
        try{
            if (!ps_param.equals("")) 
                mensaje = ps_param;
            else 
                throw new Exception("El cuerpo del mensaje de correo no puede ser nulo");
            
            mensaje = mensaje + "<br>"; 
            mensaje = mensaje + "<br>Este es un mensaje informativo por lo que le solicitamos no responder. "; 
            mensaje = mensaje + "<br>Atentamente, "; 
            mensaje = mensaje + "<br>";             
            
            if (!ps_nombreArchivo.equals("")){
                email = SendMail.sendWithAttachments(
                        propertie.getProperty("MAIL.SERVER"),
                        propertie.getProperty("MAIL.FROM"),
                        Encriptador.decriptar(propertie.getProperty("MAIL.PASS")),
                        propertie.getProperty("MAIL.PORT"),
                        propertie.getProperty("MAIL.TO"),
                        propertie.getProperty("MAIL.CC"),
                        propertie.getProperty("MAIL.BCC"),
                        asunto,
                        mensaje,
                        ps_nombreArchivo);
            }else{
                email = SendMail.send4(
                        propertie.getProperty("MAIL.SERVER"),
                        propertie.getProperty("MAIL.FROM"),
                        propertie.getProperty("MAIL.TO"),
                        propertie.getProperty("MAIL.CC"),
                        propertie.getProperty("MAIL.BCC"),
                        asunto,
                        mensaje,
                        Encriptador.decriptar(propertie.getProperty("MAIL.PASS")),
                        propertie.getProperty("MAIL.PORT")
                        );
            }            
            
            logger2.info("Notificacion por email");
            
            if (!email.equals(""))
                throw new Exception("Error al enviar notificacion por correo: " + email);
            
        }catch(Exception e){
            logger2.warn(e);
        }finally{
            ;
        }
    }
        
    /**
     * @param args the command line arguments
     * @throws java.lang.Exception
     */
    public static void main(String[] args) throws Exception {
        // TODO code application logic here
        
        ConsultaUltimoEventoCamara evento = new ConsultaUltimoEventoCamara();
        
        logger2.info("Inicio"); 
        evento.verificaEventos();
        logger2.info("Fin");
        
    }
    
}
