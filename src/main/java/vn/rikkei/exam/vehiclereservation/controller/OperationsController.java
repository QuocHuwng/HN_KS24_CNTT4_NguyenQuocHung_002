package vn.rikkei.exam.vehiclereservation.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.rikkei.exam.vehiclereservation.dto.ApproveRequest;
import vn.rikkei.exam.vehiclereservation.dto.ReservationResponse;
import vn.rikkei.exam.vehiclereservation.service.ReservationService;

@RestController
@RequestMapping("/api/operations")
@RequiredArgsConstructor
public class OperationsController {

    private final ReservationService reservationService;

    @PostMapping("/approve-request")
    public ResponseEntity<ReservationResponse> approveRequest(
            @RequestBody ApproveRequest request
    ) {

        return ResponseEntity.ok(
                reservationService.approveOrReject(request)
        );
    }
}