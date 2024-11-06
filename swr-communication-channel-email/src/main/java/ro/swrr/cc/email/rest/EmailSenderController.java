package ro.swrr.cc.email.rest;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ro.swrr.cc.email.model.EmailMessage;
import ro.swrr.cc.email.model.ReservationConfirmation;
import ro.swrr.cc.email.service.EmailService;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/email", produces = "application/json")
public class EmailSenderController {


    private final EmailService emailService;

    @GetMapping
    public String test() {
        return "Email sender is working!";
    }

    @PostMapping
    public String sendEmail(@RequestBody EmailMessage message)
    {
        return emailService.sendEmail(message);
    }

    @PostMapping("/ar-confirmation")
    public void sendArConfirmationEmail(@RequestParam String to, @RequestParam String name) throws MessagingException {
        emailService.sendArConfirmation(to, name);
    }

    @PostMapping("/reservations")
    public void sendReservationConfirmation(@RequestBody ReservationConfirmation reservationConfirmation, @RequestParam String lang) throws MessagingException {
        emailService.reservationConfirmation(reservationConfirmation, lang);
    }
}
