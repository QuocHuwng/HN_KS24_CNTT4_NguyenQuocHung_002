package vn.rikkei.exam.vehiclereservation.dto;

import java.time.LocalDate;
import java.util.List;

public record VehicleAvailabilityResponse(
        String resourceCode,
        String displayName,
        LocalDate startDate,
        LocalDate endDate,
        boolean available,
        Integer maxParticipants,
        List<DailyAvailability> dailyAvailability
) {

    public record DailyAvailability(
            LocalDate date,
            Integer availableSlots
    ) {
    }

}