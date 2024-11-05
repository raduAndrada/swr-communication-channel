package ro.swrr.cc.email.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;
import ro.swrr.cc.email.model.EmailMessage;
import ro.swrr.cc.email.model.ReservationConfirmation;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private static final String RESERVATION_DETAILS_SUBJECT = "Avem o noua rezervare pentru: %s in data de: %s";


    private final JavaMailSender emailSender;
    private final SpringTemplateEngine thymeleafTemplateEngine;

    @Value("${spring.mail.username}")
    private String username;

    @Value("${communication.channel.reservation-confirmation.template.name}")
    private String reservationConfirmationTemplateName;

    @Value("${communication.channel.reservation-details.template.name}")
    private String reservationDetailsTemplateName;

    @Value("${communication.channel.reservation-confirmation.template.cc}")
    private String mailCC;

    @Value("${communication.channel.reservation-confirmation.template.sender}")
    private String sender;

    @Value("${communication.channel.reservation-confirmation.template.subject-ro}")
    private String subjectRo;

    @Value("${communication.channel.reservation-confirmation.template.subject-en}")
    private String subjectEn;

    @Value("${communication.channel.ar-confirmation.template.name}")
    private String arConfirmationTemplateName;

    @Override
    public String sendEmail(EmailMessage emailMessage) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(username);
        message.setTo(emailMessage.to());
        message.setSubject(emailMessage.subject());
        message.setText(emailMessage.message());
        emailSender.send(message);
        return emailMessage.message();
    }

    @Override
    public void sendArConfirmation(String email, String name) throws MessagingException {
        Map<String, Object> template = Map.of("name", name);
        Context thymeleafContext = new Context();
        thymeleafContext.setVariables(template);
        String htmlBody = thymeleafTemplateEngine.process(arConfirmationTemplateName, thymeleafContext);
        String subject = "Set up a meeting with me";
        sendHtmlMessage(email, subject, htmlBody);
    }

    @Override
    public void reservationConfirmation(ReservationConfirmation reservationConfirmation, String lang) throws MessagingException {

        Map<String, Object> templateModel = getReservationConfirmationMap(reservationConfirmation);
        Context thymeleafContext = new Context();
        thymeleafContext.setVariables(templateModel);
        String htmlBody = thymeleafTemplateEngine.process(reservationConfirmationTemplateName, thymeleafContext);
        sendHtmlMessage(reservationConfirmation.email(), lang.equals("ro") ? subjectRo : subjectEn, htmlBody);

        //send message to gabriel
        sendReservationDetails(reservationConfirmation);
    }

    private void sendReservationDetails(ReservationConfirmation reservationConfirmation) throws MessagingException {
        Map<String, Object> templateModel = getReservationDetailsMap(reservationConfirmation);
        Context thymeleafContext = new Context();
        thymeleafContext.setVariables(templateModel);
        String htmlBody = thymeleafTemplateEngine.process(reservationDetailsTemplateName, thymeleafContext);
        sendHtmlMessage(mailCC, String.format(RESERVATION_DETAILS_SUBJECT, reservationConfirmation.name(), reservationConfirmation.requestDate()), htmlBody);
    }

    private Map<String, Object> getReservationDetailsMap(ReservationConfirmation reservationConfirmation) {
        return new HashMap<>() {{
            put("name", reservationConfirmation.name());
            put("email", reservationConfirmation.email());
            put("tel", reservationConfirmation.tel());
            put("date", reservationConfirmation.requestDate());
            put("company", reservationConfirmation.company());
            put("noOfPeople", reservationConfirmation.noOfPeople());
            put("additionalInfo", reservationConfirmation.additionalInfo());
        }};

    }


    private Map<String, Object> getReservationConfirmationMap(ReservationConfirmation reservationConfirmation) {
        return new HashMap<>() {{
            put("recipientName", reservationConfirmation.name());
            put("reservationDate", reservationConfirmation.requestDate());
            put("reservationNoOfPeople", reservationConfirmation.noOfPeople());
            put("senderName", sender);
        }};

    }

    private void sendHtmlMessage(String to, String subject, String htmlBody) throws MessagingException {

        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setFrom(username);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlBody, true);
        emailSender.send(message);
    }
}
