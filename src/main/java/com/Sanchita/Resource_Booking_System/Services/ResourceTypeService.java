package com.Sanchita.Resource_Booking_System.Services;

import com.Sanchita.Resource_Booking_System.DTO.ResourceType.ResourceTypeRequest;
import com.Sanchita.Resource_Booking_System.DTO.ResourceType.ResourceTypeResponse;

import java.util.List;


public interface ResourceTypeService {


    ResourceTypeResponse createResourceType(ResourceTypeRequest request);

    List<ResourceTypeResponse> getAllResourceTypes();

    ResourceTypeResponse getResourceTypeById(Long id);

    ResourceTypeResponse updateResourceType(Long id, ResourceTypeRequest request);

    void deleteResourceType(Long id);



}
