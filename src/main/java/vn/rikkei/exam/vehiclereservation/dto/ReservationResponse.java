package vn.rikkei.exam.vehiclereservation.dto;

public record ReservationResponse(
        String requestId,
        String status,
        String summary
) {
}