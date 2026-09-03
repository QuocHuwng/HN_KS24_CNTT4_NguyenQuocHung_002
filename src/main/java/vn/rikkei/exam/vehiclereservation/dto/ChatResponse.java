package vn.rikkei.exam.vehiclereservation.dto;

import java.util.List;

public record ChatResponse(
        String answer,
        String conversationId,
        List<SourceResponse> sources,
        List<String> toolsUsed
) {
}