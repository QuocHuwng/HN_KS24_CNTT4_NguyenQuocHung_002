package vn.rikkei.exam.vehiclereservation.dto;

public record ApproveRequest(
        String requestId,
        String decision,
        String note
) {
}