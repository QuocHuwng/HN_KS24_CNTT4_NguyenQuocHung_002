package vn.rikkei.exam.vehiclereservation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.rikkei.exam.vehiclereservation.model.ReservationRequest;
import vn.rikkei.exam.vehiclereservation.model.ReservationStatus;

import java.util.Optional;

public interface ReservationRequestRepository
        extends JpaRepository<ReservationRequest, String> {

    Optional<ReservationRequest> findByRequestIdAndStatus(
            String requestId,
            ReservationStatus status
    );
}