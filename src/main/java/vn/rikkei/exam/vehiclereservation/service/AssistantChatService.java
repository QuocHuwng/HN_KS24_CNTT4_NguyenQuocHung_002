package vn.rikkei.exam.vehiclereservation.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.stereotype.Service;
import vn.rikkei.exam.vehiclereservation.dto.ChatRequest;
import vn.rikkei.exam.vehiclereservation.dto.ChatResponse;
import vn.rikkei.exam.vehiclereservation.tool.ToolExecutionTracker;
import vn.rikkei.exam.vehiclereservation.tool.VehicleReservationTools;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
public class AssistantChatService {

    private final ChatClient chatClient;
    private final ToolExecutionTracker toolExecutionTracker;

    public AssistantChatService(
            ChatClient.Builder chatClientBuilder,
            VehicleReservationTools vehicleReservationTools,
            ToolExecutionTracker toolExecutionTracker
    ) {
        this.toolExecutionTracker = toolExecutionTracker;

        ChatMemory chatMemory = MessageWindowChatMemory.builder()
                .maxMessages(20)
                .build();

        MessageChatMemoryAdvisor memoryAdvisor =
                MessageChatMemoryAdvisor.builder(chatMemory).build();

        this.chatClient = chatClientBuilder
                .defaultSystem("""
                        Bạn là trợ lý nghiệp vụ quản lý xe công tác.

                        QUY TẮC:
                        1. Không được tự suy đoán dữ liệu xe hoặc dữ liệu đặt xe.
                        2. Khi người dùng hỏi tình trạng xe, bắt buộc sử dụng
                           getVehicleAvailability.
                        3. Khi người dùng muốn tạo yêu cầu đặt xe, bắt buộc sử dụng
                           createVehicleReservationRequest.
                        4. Chỉ trả lời dựa trên kết quả tool.
                        """)
                .defaultTools(vehicleReservationTools)
                .defaultAdvisors(memoryAdvisor)
                .build();
    }

    public ChatResponse ask(ChatRequest request) {

        String conversationId = request.conversationId();

        if (conversationId == null || conversationId.isBlank()) {
            conversationId = UUID.randomUUID().toString();
        }

        String finalConversationId = conversationId;

        toolExecutionTracker.start();

        try {
            String answer = chatClient.prompt()
                    .advisors(a -> a.param(
                            ChatMemory.CONVERSATION_ID,
                            finalConversationId
                    ))
                    .user(request.message())
                    .call()
                    .content();

            List<String> toolsUsed =
                    toolExecutionTracker.getToolsUsed();

            return new ChatResponse(
                    answer,
                    finalConversationId,
                    Collections.emptyList(),
                    toolsUsed
            );

        } finally {
            toolExecutionTracker.clear();
        }
    }
}