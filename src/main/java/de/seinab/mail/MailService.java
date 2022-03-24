package de.seinab.mail;

import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailSendException;
import org.springframework.stereotype.Service;

@Service
public class MailService {
    private static final Logger log = LoggerFactory.getLogger(MailService.class);

    @Value("${de.seinab.email.senderEmail}")
    private String senderEmail;

    @Value("${de.seinab.email.user}")
    private String emailUser;

    @Value("${de.seinab.email.password}")
    private String emailPassword;

    @Value("${de.seinab.email.host}")
    private String emailHost;

    public void sendEmail(EmailData emailData) throws MailSendException {
        sendEmail(emailData, null);
    }


    public void sendEmail(EmailData emailData, EmailAttachment emailAttachment) throws MailSendException {
        try
        {
            HtmlEmail htmlEmail = new HtmlEmail();
            htmlEmail.setHostName(emailHost);
            htmlEmail.setAuthentication(emailUser, emailPassword);
            htmlEmail.setCharset("UTF-8");
            htmlEmail.setFrom(senderEmail, emailData.getSenderName());
            htmlEmail.addTo(emailData.getRecipientEmail());
            htmlEmail.setSubject(emailData.getSubject());
            htmlEmail.setHtmlMsg(emailData.getHtmlMessage());

            if(emailAttachment != null) {
                htmlEmail.attach(emailAttachment.getDataSource(), emailAttachment.getName(), emailAttachment.getDescription());
            }

            htmlEmail.send();
        }
        catch (EmailException e)
        {
            log.error(e.getMessage(), e);
            throw new MailSendException("Email konnte nicht gesendet werden.");
        }

    }

}
