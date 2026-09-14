package com.Sanchita.Resource_Booking_System.Repository;

import com.Sanchita.Resource_Booking_System.Entity.ResourceType;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ResourceTypeRepo extends JpaRepository<ResourceType, Long> {


    boolean existsByNameIgnoreCase(String name);

    Optional<ResourceType> findByNameIgnoreCase(String name);
}
