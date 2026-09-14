package com.Sanchita.Resource_Booking_System.Repository;

import com.Sanchita.Resource_Booking_System.Entity.Resource;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResourceRepo extends JpaRepository<Resource,Long> {
    boolean existsByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);

    boolean existsByResourceTypeId(Long id);
}
