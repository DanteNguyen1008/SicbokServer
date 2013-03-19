/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kent;

import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 *
 * @author Kent
 */
public class SendMail {
    
    public Message message = null;
    public static final boolean MAIL_AUTHENTICATION = true;
    public static String MAIL_USERNAME = "mailer.tcg@gmail.com";
    public static final String MAIL_PASSWORD = "48gg@S(3FRme";
    public static final boolean MAIL_START_TTS_ENABLE = true;
    public static final String MAIL_HOST_NAME = "smtp.gmail.com";
    public static final String MAIL_HOST_PORT = "587";
    private String strMailTitle;
    private String strMailContent;
    
    public SendMail(String emailTo) throws MessagingException {
        Properties props = new Properties();
        props.put("mail.smtp.auth", MAIL_AUTHENTICATION);
        props.put("mail.smtp.starttls.enable", MAIL_START_TTS_ENABLE);
        props.put("mail.smtp.host", MAIL_HOST_NAME);
        props.put("mail.smtp.port", MAIL_HOST_PORT);
        
        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(MAIL_USERNAME, MAIL_PASSWORD);
                    }
                });
        
        this.message = new MimeMessage(session);
        this.message.setFrom(new InternetAddress("support@thietkeweb-seo.com"));
        this.message.setRecipients(Message.RecipientType.TO,
                InternetAddress.parse(emailTo));
        
    }
    
    public void sendMail(String emailTitle, String emailContent) throws MessagingException {
        
        this.message.setSubject(emailTitle);
        this.message.setContent(emailContent, "text/html; charset=utf-8");
        Transport.send(message);
    }
    
    public void addRecipient(String recipientType, String emailAdress) throws MessagingException {
        
        Message.RecipientType re = Message.RecipientType.TO;
        if ("cc".equals(recipientType)) {
            re = javax.mail.Message.RecipientType.CC;
        } else if ("bcc".equals(recipientType)) {
            re = javax.mail.Message.RecipientType.BCC;
        } else {
            re = javax.mail.Message.RecipientType.TO;
        }
        
        this.message.addRecipient(re, new InternetAddress(emailAdress));
    }
    

    //<editor-fold defaultstate="collapsed" desc="Encapsulate fields">
    /**
     * @return the strMailTitle
     */
    public String getStrMailTitle() {
        return strMailTitle;
    }

    /**
     * @param strMailTitle the strMailTitle to set
     */
    public void setStrMailTitle(String strMailTitle) {
        this.strMailTitle = strMailTitle;
    }

    /**
     * @return the strMailContent
     */
    public String getStrMailContent() {
        return strMailContent;
    }

    /**
     * @param strMailContent the strMailContent to set
     */
    public void setStrMailContent(String strMailContent) {
        this.strMailContent = strMailContent;
    }
    //</editor-fold>
}
