package ro.swrr.cc.email.model;

public record EmailMessage(
        String to,
        String from,
        String subject,
        String message
) {
}
