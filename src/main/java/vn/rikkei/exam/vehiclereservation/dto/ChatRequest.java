package vn.rikkei.exam.vehiclereservation.dto;

public record ChatRequest(
        String conversationId,
        String userId,
        String message
) {
}