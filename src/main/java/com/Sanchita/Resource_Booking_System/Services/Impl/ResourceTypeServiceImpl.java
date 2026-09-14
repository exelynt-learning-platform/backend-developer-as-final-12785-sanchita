package com.Sanchita.Resource_Booking_System.Services.Impl;


import com.Sanchita.Resource_Booking_System.DTO.ResourceType.ResourceTypeRequest;
import com.Sanchita.Resource_Booking_System.DTO.ResourceType.ResourceTypeResponse;
import com.Sanchita.Resource_Booking_System.Entity.ResourceType;
import com.Sanchita.Resource_Booking_System.Exception.ResourceTypeAlreadyExistsException;
import com.Sanchita.Resource_Booking_System.Exception.ResourceTypeInUseException;
import com.Sanchita.Resource_Booking_System.Exception.ResourceTypeNotFoundException;
import com.Sanchita.Resource_Booking_System.Repository.ResourceRepo;
import com.Sanchita.Resource_Booking_System.Repository.ResourceTypeRepo;
import com.Sanchita.Resource_Booking_System.Services.ResourceTypeService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ResourceTypeServiceImpl implements ResourceTypeService {

    private final ResourceTypeRepo resourceTypeRepo;
    private final ResourceRepo resourceRepo;


    @Override
    public ResourceTypeResponse createResourceType(ResourceTypeRequest request) {
        String name = request.getName().trim();

        if (resourceTypeRepo.existsByNameIgnoreCase(name)) {

            throw new ResourceTypeAlreadyExistsException(
                    "Resource type already exists with name: " + name
            );
        }

        ResourceType resourceType = ResourceType.builder()
                .name(name)
                .description(request.getDescription())
                .active(true)
                .build();

        ResourceType savedResourceType =
                resourceTypeRepo.save(resourceType);

        return mapToResponse(savedResourceType);
    }

    @Override
    public List<ResourceTypeResponse> getAllResourceTypes() {

        return resourceTypeRepo.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public ResourceTypeResponse getResourceTypeById(Long id) {
        ResourceType resourceType =
                resourceTypeRepo.findById(id)
                        .orElseThrow(() ->
                                new ResourceTypeNotFoundException(
                                        "Resource type not found with id: "
                                                + id
                                )
                        );

        return mapToResponse(resourceType);
    }

    @Override
    public ResourceTypeResponse updateResourceType(Long id, ResourceTypeRequest request) {
        ResourceType resourceType =
                resourceTypeRepo.findById(id)
                        .orElseThrow(() ->
                                new ResourceTypeNotFoundException(
                                        "Resource type not found with id: "
                                                + id
                                )
                        );
        String name = request.getName().trim();

        resourceTypeRepo.findByNameIgnoreCase(name)
                .filter(existing ->
                        !existing.getId().equals(id)
                )
                .ifPresent(existing -> {
                    throw new ResourceTypeAlreadyExistsException(
                            "Resource type already exists with name: "
                                    + name
                    );
                });
        resourceType.setName(name);
        resourceType.setDescription(request.getDescription());

        return mapToResponse(resourceType);

    }

    @Override
    public void deleteResourceType(Long id) {

        ResourceType resourceType =
                resourceTypeRepo.findById(id)
                        .orElseThrow(() ->
                                new ResourceTypeNotFoundException(
                                        "Resource type not found with id: "
                                                + id
                                )
                        );

        if (resourceRepo.existsByResourceTypeId(id)) {

            throw new ResourceTypeInUseException(
                    "Resource type cannot be deleted because it is being used"
            );
        }
        resourceTypeRepo.delete(resourceType);
    }

    private ResourceTypeResponse mapToResponse(
            ResourceType resourceType) {

        return ResourceTypeResponse.builder()
                .id(resourceType.getId())
                .name(resourceType.getName())
                .description(resourceType.getDescription())
                .active(resourceType.isActive())
                .build();
    }
}
