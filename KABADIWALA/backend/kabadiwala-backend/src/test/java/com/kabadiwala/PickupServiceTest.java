package com.kabadiwala;

import com.kabadiwala.entity.Pickup;
import com.kabadiwala.exception.InvalidTransactionException;
import com.kabadiwala.service.PickupService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PickupServiceTest {

    @Test
    @DisplayName("Valid pickup transitions: REQUESTED -> ACCEPTED -> ON_THE_WAY -> COLLECTED -> COMPLETED")
    void shouldAllowValidPickupTransitions() {
        assertDoesNotThrow(() -> PickupService.validateTransition(Pickup.Status.REQUESTED, Pickup.Status.ACCEPTED));
        assertDoesNotThrow(() -> PickupService.validateTransition(Pickup.Status.ACCEPTED, Pickup.Status.ON_THE_WAY));
        assertDoesNotThrow(() -> PickupService.validateTransition(Pickup.Status.ON_THE_WAY, Pickup.Status.COLLECTED));
        assertDoesNotThrow(() -> PickupService.validateTransition(Pickup.Status.COLLECTED, Pickup.Status.COMPLETED));
        assertDoesNotThrow(() -> PickupService.validateTransition(Pickup.Status.REQUESTED, Pickup.Status.CANCELLED));
        assertDoesNotThrow(() -> PickupService.validateTransition(Pickup.Status.ACCEPTED, Pickup.Status.CANCELLED));
    }

    @Test
    @DisplayName("Invalid transitions should be rejected with InvalidTransactionException")
    void shouldRejectInvalidTransitions() {
        // Cannot skip directly from REQUESTED to COLLECTED
        assertThrows(InvalidTransactionException.class, () ->
                PickupService.validateTransition(Pickup.Status.REQUESTED, Pickup.Status.COLLECTED));

        // Cannot skip from REQUESTED to COMPLETED
        assertThrows(InvalidTransactionException.class, () ->
                PickupService.validateTransition(Pickup.Status.REQUESTED, Pickup.Status.COMPLETED));

        // Cannot go backwards from COLLECTED to ACCEPTED
        assertThrows(InvalidTransactionException.class, () ->
                PickupService.validateTransition(Pickup.Status.COLLECTED, Pickup.Status.ACCEPTED));

        // Cannot transition from COMPLETED to any status
        assertThrows(InvalidTransactionException.class, () ->
                PickupService.validateTransition(Pickup.Status.COMPLETED, Pickup.Status.CANCELLED));
    }
}
