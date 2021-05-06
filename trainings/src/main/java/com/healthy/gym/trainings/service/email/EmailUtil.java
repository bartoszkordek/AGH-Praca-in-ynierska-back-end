package com.healthy.gym.trainings.service.email;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;

public class EmailUtil {

    private static String toEmailsParser(List<String> toEmails){
        StringBuilder toEmailsParsed = new StringBuilder();
        String delim = "";
        for (String i : toEmails) {
            toEmailsParsed.append(delim).append(i);
            delim = ",";
        }
        String result = toEmailsParsed.toString();
        System.out.println(result);
        return toEmailsParsed.toString();
    }

    public static void sendEmail(Session session, String fromEmail, String personal, List<String> toEmails,
                                 String subject, String body, String filePath){
        try {
            MimeMessage msg = new MimeMessage(session);
            //set message headers
            msg.addHeader("Content-type", "text/HTML; charset=UTF-8");
            msg.addHeader("format", "flowed");
            msg.addHeader("Content-Transfer-Encoding", "8bit");
            msg.setFrom(new InternetAddress(fromEmail, personal));
            msg.setReplyTo(InternetAddress.parse(fromEmail, false));
            msg.setSubject(subject, "UTF-8");
            msg.setText(body, "UTF-8");
            msg.setSentDate(new Date());


            msg.setRecipients(Message.RecipientType.BCC, InternetAddress.parse(toEmailsParser(toEmails), false));

            BodyPart messageBodyPart = new MimeBodyPart();

            messageBodyPart.setText(body);

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart);


            if(filePath != null){
                messageBodyPart = new MimeBodyPart();
                DataSource source = new FileDataSource(filePath);
                messageBodyPart.setDataHandler(new DataHandler(source));
                messageBodyPart.setFileName(filePath);
                multipart.addBodyPart(messageBodyPart);
                msg.setContent(multipart);
            }

            System.out.println("Message is ready");
            Transport.send(msg);

            System.out.println("Email Sent Successfully!!");
        } catch (MessagingException e){
            e.printStackTrace();
        } catch (UnsupportedEncodingException e){
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
