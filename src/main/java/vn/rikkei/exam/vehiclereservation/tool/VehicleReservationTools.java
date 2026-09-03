package vn.rikkei.exam.vehiclereservation.tool;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;
import vn.rikkei.exam.vehiclereservation.dto.ReservationResponse;
import vn.rikkei.exam.vehiclereservation.dto.VehicleAvailabilityResponse;
import vn.rikkei.exam.vehiclereservation.service.ReservationService;
import vn.rikkei.exam.vehiclereservation.service.VehicleService;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class VehicleReservationTools {

    private final VehicleService vehicleService;
    private final ReservationService reservationService;
    private final ToolExecutionTracker toolExecutionTracker;

    @Tool(
            name = "getVehicleAvailability",
            description = """
                    Tra cứu tình trạng xe công tác còn khả dụng
                    theo loại xe và khoảng ngày.

                    Bắt buộc:
                    - resourceType: mã xe hoặc tên loại xe
                    - startDate: ngày bắt đầu theo YYYY-MM-DD
                    - endDate: ngày kết thúc theo YYYY-MM-DD

                    startDate phải nhỏ hơn endDate.
                    Không được tự suy đoán dữ liệu xe.
                    """
    )
    public VehicleAvailabilityResponse getVehicleAvailability(
            String resourceType,
            LocalDate startDate,
            LocalDate endDate
    ) {

        toolExecutionTracker.record("getVehicleAvailability");

        System.out.println("========== TOOL EXECUTED ==========");
        System.out.println("Tool: getVehicleAvailability");
        System.out.println("resourceType: " + resourceType);
        System.out.println("startDate: " + startDate);
        System.out.println("endDate: " + endDate);

        VehicleAvailabilityResponse result =
                vehicleService.getVehicleAvailability(
                        resourceType,
                        startDate,
                        endDate
                );

        System.out.println("Tool result: " + result);
        System.out.println("===================================");

        return result;
    }

    @Tool(
            name = "createVehicleReservationRequest",
            description = """
                    Tạo yêu cầu đặt xe công tác.

                    Yêu cầu:
                    - userId phải tồn tại
                    - startDate phải nhỏ hơn endDate
                    - thời gian tối đa 14 ngày
                    - participantCount phải phù hợp sức chứa
                    - nhóm PREMIUM phải có tối thiểu 2 người
                    - purpose phải từ 10 đến 200 ký tự

                    Request mới luôn có trạng thái PENDING.
                    Không tự tạo request nếu thiếu thông tin bắt buộc.
                    """
    )
    public ReservationResponse createVehicleReservationRequest(
            String userId,
            String resourceType,
            LocalDate startDate,
            LocalDate endDate,
            Integer participantCount,
            String purpose
    ) {

        toolExecutionTracker.record("createVehicleReservationRequest");

        System.out.println("========== TOOL EXECUTED ==========");
        System.out.println("Tool: createVehicleReservationRequest");
        System.out.println("userId: " + userId);
        System.out.println("resourceType: " + resourceType);
        System.out.println("startDate: " + startDate);
        System.out.println("endDate: " + endDate);
        System.out.println("participantCount: " + participantCount);
        System.out.println("purpose: " + purpose);

        ReservationResponse result =
                reservationService.createReservation(
                        userId,
                        resourceType,
                        startDate,
                        endDate,
                        participantCount,
                        purpose
                );

        System.out.println("Tool result: " + result);
        System.out.println("===================================");

        return result;
    }
}