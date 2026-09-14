package com.Sanchita.Resource_Booking_System.Controller;


import com.Sanchita.Resource_Booking_System.DTO.ResourceType.ResourceTypeRequest;
import com.Sanchita.Resource_Booking_System.DTO.ResourceType.ResourceTypeResponse;
import com.Sanchita.Resource_Booking_System.Services.ResourceTypeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/resource-types")
@RequiredArgsConstructor
public class ResourceTypeController {

    private final ResourceTypeService resourceTypeService;


    @PostMapping("/create-resource-type")
    public ResponseEntity<ResourceTypeResponse> createResourceType(@Valid @RequestBody ResourceTypeRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        resourceTypeService.createResourceType(request)
                );
    }

    @GetMapping("/get-resource-type")
    public ResponseEntity<List<ResourceTypeResponse>> getAllResourceTypes() {

        return ResponseEntity.ok(
                resourceTypeService.getAllResourceTypes()
        );
    }

    @GetMapping("/get-resource-type-by-Id/{id}")
    public ResponseEntity<ResourceTypeResponse> getResourceTypeById(@PathVariable Long id) {

        return ResponseEntity.ok(
                resourceTypeService.getResourceTypeById(id)
        );
    }

    @PutMapping("/update-resource-type/{id}")
    public ResponseEntity<ResourceTypeResponse> updateResourceType(@PathVariable Long id, @Valid @RequestBody ResourceTypeRequest request) {

        return ResponseEntity.ok(
                resourceTypeService.updateResourceType(
                        id,
                        request
                )
        );
    }

    @DeleteMapping("/delete-resource-type/{id}")
    public ResponseEntity<Void> deleteResourceType(
            @PathVariable Long id) {

        resourceTypeService.deleteResourceType(id);

        return ResponseEntity.noContent().build();
    }



}
