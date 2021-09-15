package de.bonndan.nivio.notification;

import de.bonndan.nivio.input.ProcessingErrorEvent;
import de.bonndan.nivio.input.ProcessingException;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import org.springframework.context.ApplicationListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.util.StringUtils;


@Service
public class EmailNotificationService implements ApplicationListener<ProcessingErrorEvent> {


    private static final Logger logger = LoggerFactory.getLogger(EmailNotificationService.class);

    private final JavaMailSender emailSender;

    public EmailNotificationService(JavaMailSender sender) {
        this.emailSender = sender;
    }

    public void sendError(ProcessingException exception, String subject) {

        LandscapeDescription landscapeDescription = exception.getLandscapeDescription();
        if (landscapeDescription == null || !StringUtils.hasLength(landscapeDescription.getContact())) {
            logger.warn("Cannot send error, landscape is not configured");
            return;
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(landscapeDescription.getContact());
        message.setSubject(landscapeDescription.getIdentifier() + ": " + subject);
        message.setText(exception.getMessage());

        try {
            emailSender.send(message);
        } catch (Exception ex) {
            logger.warn("Could not send email '{}' in landscape {}", subject, landscapeDescription);
        }

        logger.info("Sent mail to user ");
    }


    @Override
    public void onApplicationEvent(ProcessingErrorEvent processingErrorEvent) {
        ProcessingException ex = processingErrorEvent.getException();
        sendError(ex, ex.getMessage());
    }
}

