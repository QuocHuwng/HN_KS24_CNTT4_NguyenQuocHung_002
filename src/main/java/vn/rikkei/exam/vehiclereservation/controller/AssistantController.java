package vn.rikkei.exam.vehiclereservation.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.rikkei.exam.vehiclereservation.dto.ChatRequest;
import vn.rikkei.exam.vehiclereservation.dto.ChatResponse;
import vn.rikkei.exam.vehiclereservation.service.AssistantChatService;

@RestController
@RequestMapping("/api/assistant")
@RequiredArgsConstructor
public class AssistantController {

    private final AssistantChatService assistantChatService;

    @PostMapping("/ask")
    public ResponseEntity<ChatResponse> ask(@RequestBody ChatRequest request) {
        if (request == null || request.message() == null || request.message().isBlank()) {
            throw new IllegalArgumentException("message không được để trống");
        }
        return ResponseEntity.ok(assistantChatService.ask(request));
    }
}