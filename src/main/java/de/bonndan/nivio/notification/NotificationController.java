package de.bonndan.nivio.notification;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/events")
public class NotificationController {

    private final MessagingService messagingService;

    public NotificationController(MessagingService messagingService) {
        this.messagingService = messagingService;
    }

    @CrossOrigin(methods = RequestMethod.GET)
    @GetMapping()
    public ResponseEntity<EventNotification[]> events() {
        return ResponseEntity.ok(messagingService.getLast());
    }
}
