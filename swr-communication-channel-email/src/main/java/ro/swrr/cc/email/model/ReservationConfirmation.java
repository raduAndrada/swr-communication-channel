package ro.swrr.cc.email.model;

import lombok.NonNull;

public record ReservationConfirmation(
        String name,
        @NonNull String email,
        String partyType,
        String tel,
        String company,
        @NonNull String requestDate,
        @NonNull Integer noOfPeople,
        String additionalInfo

) {
}
