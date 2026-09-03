package vn.rikkei.exam.vehiclereservation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.rikkei.exam.vehiclereservation.dto.VehicleAvailabilityResponse;
import vn.rikkei.exam.vehiclereservation.model.ResourceInventory;
import vn.rikkei.exam.vehiclereservation.model.ResourceType;
import vn.rikkei.exam.vehiclereservation.repository.ResourceInventoryRepository;
import vn.rikkei.exam.vehiclereservation.repository.ResourceTypeRepository;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VehicleService {

    private final ResourceTypeRepository resourceTypeRepository;
    private final ResourceInventoryRepository resourceInventoryRepository;

    @Transactional(readOnly = true)
    public VehicleAvailabilityResponse getVehicleAvailability(
            String resourceType,
            LocalDate startDate,
            LocalDate endDate
    ) {

        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException(
                    "startDate và endDate không được để trống"
            );
        }

        if (!startDate.isBefore(endDate)) {
            throw new IllegalArgumentException(
                    "startDate phải nhỏ hơn endDate"
            );
        }

        ResourceType type = findResourceType(resourceType);

        List<ResourceInventory> inventories =
                resourceInventoryRepository
                        .findByResourceType_ResourceCodeAndAvailableDateBetween(
                                type.getResourceCode(),
                                startDate,
                                endDate
                        );

        List<VehicleAvailabilityResponse.DailyAvailability>
                dailyAvailability =
                inventories.stream()
                        .map(item ->
                                new VehicleAvailabilityResponse
                                        .DailyAvailability(
                                        item.getAvailableDate(),
                                        item.getAvailableSlots()
                                )
                        )
                        .toList();


        boolean available =
                !inventories.isEmpty()
                        && inventories.stream()
                        .anyMatch(item ->
                                item.getAvailableSlots() != null
                                        && item.getAvailableSlots() > 0
                        );

        return new VehicleAvailabilityResponse(
                type.getResourceCode(),
                type.getDisplayName(),
                startDate,
                endDate,
                available,
                type.getMaxParticipants(),
                dailyAvailability
        );
    }

    private ResourceType findResourceType(
            String resourceType
    ) {

        if (resourceType == null ||
                resourceType.isBlank()) {

            throw new IllegalArgumentException(
                    "resourceType không được để trống"
            );
        }

        return resourceTypeRepository
                .findByResourceCodeAndActiveTrue(resourceType)
                .orElseGet(() ->
                        resourceTypeRepository
                                .findByDisplayNameIgnoreCaseAndActiveTrue(
                                        resourceType
                                )
                                .orElseThrow(() ->
                                        new IllegalArgumentException(
                                                "Không tìm thấy loại xe: "
                                                        + resourceType
                                        )
                                )
                );
    }
}