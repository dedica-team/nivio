package de.bonndan.nivio.service;

import de.bonndan.nivio.ProcessingException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.util.StringUtils;


@Service
public class NotificationService {


    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    private final JavaMailSender emailSender;

    @Autowired
    public NotificationService(JavaMailSender sender) {
        this.emailSender = sender;
    }

    public void sendError(ProcessingException exception, String subject) {

        if (exception.getLandscape() == null || StringUtils.isEmpty(exception.getLandscape().getContact())) {
            logger.error("Cannot send error, landscape is not configured");
            return;
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(exception.getLandscape().getContact());
        message.setSubject(exception.getLandscape().getIdentifier() + ": " + subject);
        message.setText(exception.getMessage());

        try {
            emailSender.send(message);
        } catch (Exception ex) {
            logger.warn("Could not send email '" + subject + "' in landscape " + exception.getLandscape());
        }

        logger.info("Sent mail to user ");
    }


}

