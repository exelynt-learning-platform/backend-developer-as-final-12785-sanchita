package com.Sanchita.Resource_Booking_System.Services;

import com.Sanchita.Resource_Booking_System.DTO.resources.ResourceRequest;
import com.Sanchita.Resource_Booking_System.DTO.resources.ResourceResponse;

import java.util.List;

public interface ResourceService {

    ResourceResponse createResource(ResourceRequest request);

    List<ResourceResponse> getAllResources();

    ResourceResponse getResourceById(Long id);

    ResourceResponse updateResource(Long id, ResourceRequest request);

    void deleteResource(Long id);

}
