package net.exposedrecords.web.service;

import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.stereotype.Service;

@Service
public class MailingService {

    public void send(String email, String subject, String message) {

        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);

        try {
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(
                    "no-reply@exposedrecordsnet.appspotmail.com",
                    "No-Reply Mailer at Exposed Records"));
            msg.addRecipient(Message.RecipientType.TO, new InternetAddress(
                    email, "Mr. User"));
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
