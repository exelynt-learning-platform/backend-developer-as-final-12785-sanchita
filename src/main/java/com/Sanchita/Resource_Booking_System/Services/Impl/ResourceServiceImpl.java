package com.Sanchita.Resource_Booking_System.Services.Impl;

import com.Sanchita.Resource_Booking_System.DTO.resources.ResourceRequest;
import com.Sanchita.Resource_Booking_System.DTO.resources.ResourceResponse;
import com.Sanchita.Resource_Booking_System.Entity.Resource;
import com.Sanchita.Resource_Booking_System.Entity.ResourceType;
import com.Sanchita.Resource_Booking_System.Exception.ResourceAlreadyExistsException;
import com.Sanchita.Resource_Booking_System.Exception.ResourceNotFoundException;
import com.Sanchita.Resource_Booking_System.Exception.ResourceTypeNotFoundException;
import com.Sanchita.Resource_Booking_System.Repository.ResourceRepo;
import com.Sanchita.Resource_Booking_System.Repository.ResourceTypeRepo;
import com.Sanchita.Resource_Booking_System.Services.ResourceService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
@Transactional
public class ResourceServiceImpl implements ResourceService {

    private final ResourceRepo resourceRepo;

    private final ResourceTypeRepo resourceTypeRepo;


    @Override
    public ResourceResponse createResource(ResourceRequest request) {
        String name = request.getName().trim();

        if (resourceRepo.existsByNameIgnoreCase(name)) {
            throw new ResourceAlreadyExistsException(
                    "Resource already exists with name: " + name
            );
        }

        ResourceType resourceType =
                resourceTypeRepo.findById(request.getResourceTypeId())
                        .orElseThrow(() ->
                                new ResourceTypeNotFoundException(
                                        "Resource type not found with id: "
                                                + request.getResourceTypeId()
                                )
                        );
        Resource resource = Resource.builder()
                .name(name)
                .description(request.getDescription())
                .location(request.getLocation())
                .resourceType(resourceType)
                .pricePerHour(request.getPricePerHour())
                .active(
                        request.getActive() != null
                                ? request.getActive()
                                : true
                )
                .build();
        Resource savedResource = resourceRepo.save(resource);

        return mapToResponse(savedResource);
    }

    @Override
    public List<ResourceResponse> getAllResources() {

        return resourceRepo.findAll()
                .stream()
                .map(resource -> mapToResponse(resource))
                .toList();
    }

    @Override
    public ResourceResponse getResourceById(Long id) {
        Resource resource =
                resourceRepo.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Resource not found with id: " + id
                                )
                        );

        return mapToResponse(resource);
    }

    @Override
    public ResourceResponse updateResource(Long id, ResourceRequest request) {
        Resource resource =
                resourceRepo.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Resource not found with id: " + id
                                )
                        );

        String name = request.getName().trim();
        if (resourceRepo.existsByNameIgnoreCaseAndIdNot(name, id)) {
            throw new ResourceAlreadyExistsException(
                    "Resource already exists with name: " + name
            );
        }

        ResourceType resourceType = resourceTypeRepo.findById(request.getResourceTypeId())
                .orElseThrow(() ->
                        new ResourceTypeNotFoundException(
                                "Resource type not found with id: "
                                        + request.getResourceTypeId()
                        )
                );

        resource.setName(name);
        resource.setDescription(request.getDescription());
        resource.setLocation(request.getLocation());
        resource.setResourceType(resourceType);
        resource.setPricePerHour(request.getPricePerHour());

        if (request.getActive() != null) {
            resource.setActive(request.getActive());
        }

        Resource savedResource = resourceRepo.save(resource);

        return mapToResponse(savedResource);
    }

    @Override
    public void deleteResource(Long id) {


        Resource resource =
                resourceRepo.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Resource not found with id: " + id
                                )
                        );

        resourceRepo.delete(resource);

    }

    private ResourceResponse mapToResponse(Resource resource) {

        return ResourceResponse.builder()
                .id(resource.getId())
                .name(resource.getName())
                .description(resource.getDescription())
                .location(resource.getLocation())
                .resourceTypeId(resource.getResourceType().getId())
                .resourceTypeName(resource.getResourceType().getName())
                .pricePerHour(resource.getPricePerHour())
                .active(resource.isActive())
                .build();
    }
}
