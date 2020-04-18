package de.bonndan.nivio.notification;

import de.bonndan.nivio.ProcessingEvent;
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
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<ProcessingEvent[]> event() {
        return ResponseEntity.ok(
                messagingService.getLast()
        );
    }
}
