package de.bonndan.nivio.notification;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/events")
public class NotificationController {

    private final MessagingService messagingService;

    public NotificationController(MessagingService messagingService) {
        this.messagingService = messagingService;
    }

    @CrossOrigin(methods = RequestMethod.GET)
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<EventNotification[]> events() {
        return ResponseEntity.ok(messagingService.getLast());
    }
}
