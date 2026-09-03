package vn.rikkei.exam.vehiclereservation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.rikkei.exam.vehiclereservation.model.ResourceType;

import java.util.Optional;

public interface ResourceTypeRepository extends JpaRepository<ResourceType, String> {

    Optional<ResourceType> findByResourceCodeAndActiveTrue(String resourceCode);

    Optional<ResourceType> findByDisplayNameIgnoreCaseAndActiveTrue(String displayName);
}