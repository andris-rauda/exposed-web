package net.exposedrecords.web.service;

import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javax.annotation.Resource;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.stereotype.Service;

import net.exposedrecords.web.configuration.ApplicationSettings;

@Service
public class MailingService {

    private String mailerPassword;

    @Resource
    public void setApplicationSettings(ApplicationSettings applicationSettings) {
        this.mailerPassword = applicationSettings.getMailerPassword();
    }
    
    public void send(String email, String subject, String message) {

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");

        props.put("mail.smtp.host", "smtp.zoho.com");
        props.put("mail.smtp.port", "465");

        props.setProperty("mail.pop3.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.setProperty("mail.smtp.socketFactory.fallback", "false");
        props.setProperty("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.startssl.enable", "true");

        Authenticator authenticator = new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                PasswordAuthentication pa = new PasswordAuthentication("noreply@exposedrecords.net",
                        mailerPassword);
                return pa;
            }
        };
        Session session = Session.getDefaultInstance(props, authenticator);

        try {
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress("noreply@exposedrecords.net", "NoReply ExposedRecords.NET"));
            msg.addRecipient(Message.RecipientType.TO, new InternetAddress(email, "Mr. User"));
            msg.setSubject(subject);
            msg.setText(message);
            Transport.send(msg);
        } catch (AddressException e) {
            throw new RuntimeException("failed to send email to " + email, e);
        } catch (MessagingException e) {
            throw new RuntimeException("failed to send email to " + email, e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("failed to send email to " + email, e);
        }
    }
}
