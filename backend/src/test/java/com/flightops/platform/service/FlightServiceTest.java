package com.flightops.platform.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.flightops.platform.domain.Flight;
import com.flightops.platform.domain.FlightStatus;
import com.flightops.platform.dto.FlightStatusUpdateRequest;
import com.flightops.platform.repository.FlightRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FlightServiceTest {

    @Mock
    private FlightRepository flightRepository;

    @Mock
    private FlightEventPublisher flightEventPublisher;

    @InjectMocks
    private FlightService flightService;

    @Test
    void shouldPublishEventWhenStatusChangesToDelayed() {
        Flight flight = new Flight();
        flight.setId(42L);
        flight.setFlightNumber("AZ205");
        flight.setOrigin("ORD");
        flight.setDestination("MIA");
        flight.setStatus(FlightStatus.BOARDING);
        flight.setScheduledDeparture(LocalDateTime.now());
        flight.setScheduledArrival(LocalDateTime.now().plusHours(3));
        flight.setDelayMinutes(0);

        when(flightRepository.findById(42L)).thenReturn(Optional.of(flight));
        when(flightRepository.save(any(Flight.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Flight updated = flightService.updateFlightStatus(42L, new FlightStatusUpdateRequest(
                FlightStatus.DELAYED,
                45,
                null,
                null));

        assertThat(updated.getStatus()).isEqualTo(FlightStatus.DELAYED);
        assertThat(updated.getDelayMinutes()).isEqualTo(45);

        ArgumentCaptor<com.flightops.platform.event.FlightEvent> captor = ArgumentCaptor.forClass(com.flightops.platform.event.FlightEvent.class);
        verify(flightEventPublisher).publish(captor.capture());
        assertThat(captor.getValue().getEventType().name()).isEqualTo("FLIGHT_DELAYED");
    }

    @Test
    void shouldNotPublishEventForNonOperationalStatusChange() {
        Flight flight = new Flight();
        flight.setId(7L);
        flight.setFlightNumber("AZ101");
        flight.setOrigin("JFK");
        flight.setDestination("LAX");
        flight.setStatus(FlightStatus.SCHEDULED);
        flight.setScheduledDeparture(LocalDateTime.now());
        flight.setScheduledArrival(LocalDateTime.now().plusHours(6));
        flight.setDelayMinutes(0);

        when(flightRepository.findById(7L)).thenReturn(Optional.of(flight));
        when(flightRepository.save(any(Flight.class))).thenAnswer(invocation -> invocation.getArgument(0));

        flightService.updateFlightStatus(7L, new FlightStatusUpdateRequest(
                FlightStatus.BOARDING,
                0,
                null,
                null));

        verify(flightEventPublisher, never()).publish(any());
    }
}
