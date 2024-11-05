package ro.swrr.cc.email.service;

import jakarta.mail.MessagingException;
import ro.swrr.cc.email.model.EmailMessage;
import ro.swrr.cc.email.model.ReservationConfirmation;

public interface EmailService {

    String sendEmail(EmailMessage message);

    void sendArConfirmation(String email, String name) throws MessagingException;

    void reservationConfirmation(ReservationConfirmation reservationConfirmation, String lang) throws MessagingException;
}
