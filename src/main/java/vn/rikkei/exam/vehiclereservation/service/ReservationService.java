package vn.rikkei.exam.vehiclereservation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.rikkei.exam.vehiclereservation.dto.ApproveRequest;
import vn.rikkei.exam.vehiclereservation.dto.ReservationResponse;
import vn.rikkei.exam.vehiclereservation.model.*;
import vn.rikkei.exam.vehiclereservation.repository.*;

import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final AppUserRepository userRepo;
    private final ResourceTypeRepository typeRepo;
    private final ResourceInventoryRepository inventoryRepo;
    private final ReservationRequestRepository requestRepo;

    @Transactional
    public ReservationResponse createReservation(
            String userId, String resourceType,
            LocalDate startDate, LocalDate endDate,
            Integer participantCount, String purpose) {

        if (userId == null || userId.isBlank())
            throw new IllegalArgumentException("userId không được để trống");

        if (resourceType == null || resourceType.isBlank())
            throw new IllegalArgumentException("resourceType không được để trống");

        if (startDate == null || endDate == null || !startDate.isBefore(endDate))
            throw new IllegalArgumentException("startDate phải nhỏ hơn endDate");

        if (ChronoUnit.DAYS.between(startDate, endDate) > 14)
            throw new IllegalArgumentException("Thời gian đặt xe không được vượt quá 14 ngày");

        if (participantCount == null || participantCount <= 0)
            throw new IllegalArgumentException("participantCount phải lớn hơn 0");

        if (purpose == null || purpose.length() < 10 || purpose.length() > 200)
            throw new IllegalArgumentException("purpose phải có từ 10 đến 200 ký tự");

        AppUser user = userRepo.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy user: " + userId));

        ResourceType type = findType(resourceType);

        if (participantCount > type.getMaxParticipants())
            throw new IllegalArgumentException("Số người vượt quá sức chứa của xe");

        if ("PREMIUM".equalsIgnoreCase(type.getResourceCode()) && participantCount < 2)
            throw new IllegalArgumentException("Xe PREMIUM yêu cầu tối thiểu 2 người");

        List<ResourceInventory> inventories =
                inventoryRepo.findByResourceType_ResourceCodeAndAvailableDateBetween(
                        type.getResourceCode(), startDate, endDate);

        checkAvailability(inventories, startDate, endDate, participantCount);

        Instant now = Instant.now();

        ReservationRequest request = ReservationRequest.builder()
                .requestId(UUID.randomUUID().toString())
                .requester(user)
                .resourceType(type)
                .startDate(startDate)
                .endDate(endDate)
                .participantCount(participantCount)
                .purpose(purpose)
                .status(ReservationStatus.PENDING)
                .createdAt(now)
                .updatedAt(now)
                .build();

        requestRepo.save(request);

        return new ReservationResponse(
                request.getRequestId(),
                "PENDING",
                "Đã tạo yêu cầu đặt " + type.getDisplayName()
                        + " từ " + startDate + " đến " + endDate
                        + " cho " + participantCount + " người"
        );
    }

    private ResourceType findType(String code) {
        return typeRepo.findByResourceCodeAndActiveTrue(code)
                .orElseGet(() -> typeRepo
                        .findByDisplayNameIgnoreCaseAndActiveTrue(code)
                        .orElseThrow(() ->
                                new IllegalArgumentException("Không tìm thấy loại xe: " + code)));
    }

    private void checkAvailability(
            List<ResourceInventory> list,
            LocalDate start, LocalDate end,
            int participants) {

        for (LocalDate date = start; date.isBefore(end); date = date.plusDays(1)) {
            LocalDate finalDate = date;
            LocalDate finalDate1 = date;
            ResourceInventory item = list.stream()
                    .filter(x -> finalDate.equals(x.getAvailableDate()))
                    .findFirst()
                    .orElseThrow(() ->
                            new IllegalArgumentException("Không có xe ngày " + finalDate1));

            if (item.getAvailableSlots() == null
                    || item.getAvailableSlots() < participants)
                throw new IllegalArgumentException("Không đủ xe ngày " + date);
        }
    }

    @Transactional
    public ReservationResponse approveOrReject(ApproveRequest input) {

        if (input == null || input.requestId() == null || input.requestId().isBlank())
            throw new IllegalArgumentException("requestId không được để trống");

        if (input.decision() == null || input.decision().isBlank())
            throw new IllegalArgumentException("decision không được để trống");

        ReservationRequest request = requestRepo
                .findByRequestIdAndStatus(
                        input.requestId(), ReservationStatus.PENDING)
                .orElseThrow(() ->
                        new IllegalArgumentException("Chỉ request PENDING mới được xử lý"));

        String decision = input.decision().trim().toUpperCase();

        if ("APPROVE".equals(decision)) {
            ResourceType type = request.getResourceType();

            List<ResourceInventory> list =
                    inventoryRepo.findByResourceType_ResourceCodeAndAvailableDateBetween(
                            type.getResourceCode(),
                            request.getStartDate(),
                            request.getEndDate());

            checkAvailability(
                    list,
                    request.getStartDate(),
                    request.getEndDate(),
                    request.getParticipantCount());

            request.setStatus(ReservationStatus.APPROVED);

        } else if ("REJECT".equals(decision)) {
            request.setStatus(ReservationStatus.REJECTED);

        } else {
            throw new IllegalArgumentException(
                    "decision chỉ được là APPROVE hoặc REJECT");
        }

        request.setDecisionNote(input.note());
        request.setUpdatedAt(Instant.now());
        requestRepo.save(request);

        return new ReservationResponse(
                request.getRequestId(),
                request.getStatus().name(),
                "Request đã được " + request.getStatus().name()
        );
    }
}