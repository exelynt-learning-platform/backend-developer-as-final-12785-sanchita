package com.Sanchita.Resource_Booking_System.Services;

import com.Sanchita.Resource_Booking_System.DTO.resources.ResourceRequest;
import com.Sanchita.Resource_Booking_System.DTO.resources.ResourceResponse;
import com.Sanchita.Resource_Booking_System.Entity.Resource;
import com.Sanchita.Resource_Booking_System.Entity.ResourceType;
import com.Sanchita.Resource_Booking_System.Exception.ResourceAlreadyExistsException;
import com.Sanchita.Resource_Booking_System.Exception.ResourceNotFoundException;
import com.Sanchita.Resource_Booking_System.Exception.ResourceTypeNotFoundException;
import com.Sanchita.Resource_Booking_System.Repository.ResourceRepo;
import com.Sanchita.Resource_Booking_System.Repository.ResourceTypeRepo;
import com.Sanchita.Resource_Booking_System.Services.Impl.ResourceServiceImpl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResourceServiceTest {

    @Mock
    private ResourceRepo resourceRepo;

    @Mock
    private ResourceTypeRepo resourceTypeRepo;

    @InjectMocks
    private ResourceServiceImpl resourceService;


    // =========================================================
    // CREATE RESOURCE
    // =========================================================

    @Test
    void createResource_shouldCreateSuccessfully() {

        ResourceRequest request = new ResourceRequest();

        request.setName("Conference Room");
        request.setDescription("Meeting room");
        request.setLocation("Pune Office");
        request.setResourceTypeId(1L);
        request.setPricePerHour(new BigDecimal("500.00"));
        request.setActive(true);

        ResourceType resourceType = ResourceType.builder()
                .id(1L)
                .name("Room")
                .build();

        Resource resource = Resource.builder()
                .id(1L)
                .name("Conference Room")
                .description("Meeting room")
                .location("Pune Office")
                .resourceType(resourceType)
                .pricePerHour(new BigDecimal("500.00"))
                .active(true)
                .build();

        when(resourceRepo.existsByNameIgnoreCase("Conference Room"))
                .thenReturn(false);

        when(resourceTypeRepo.findById(1L))
                .thenReturn(Optional.of(resourceType));

        when(resourceRepo.save(any(Resource.class)))
                .thenReturn(resource);

        ResourceResponse response =
                resourceService.createResource(request);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Conference Room", response.getName());
        assertEquals("Pune Office", response.getLocation());
        assertEquals("Room", response.getResourceTypeName());

        assertEquals(
                0,
                new BigDecimal("500.00")
                        .compareTo(response.getPricePerHour())
        );

        assertTrue(response.isActive());

        verify(resourceRepo).save(any(Resource.class));
    }


    @Test
    void createResource_shouldThrowException_whenResourceAlreadyExists() {

        ResourceRequest request = new ResourceRequest();

        request.setName("Conference Room");
        request.setResourceTypeId(1L);
        request.setPricePerHour(new BigDecimal("500.00"));

        when(resourceRepo.existsByNameIgnoreCase("Conference Room"))
                .thenReturn(true);

        assertThrows(
                ResourceAlreadyExistsException.class,
                () -> resourceService.createResource(request)
        );

        verify(resourceRepo, never())
                .save(any(Resource.class));

        verifyNoInteractions(resourceTypeRepo);
    }


    @Test
    void createResource_shouldThrowException_whenResourceTypeNotFound() {

        ResourceRequest request = new ResourceRequest();

        request.setName("Conference Room");
        request.setResourceTypeId(1L);
        request.setPricePerHour(new BigDecimal("500.00"));

        when(resourceRepo.existsByNameIgnoreCase("Conference Room"))
                .thenReturn(false);

        when(resourceTypeRepo.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceTypeNotFoundException.class,
                () -> resourceService.createResource(request)
        );

        verify(resourceRepo, never())
                .save(any(Resource.class));
    }


    // =========================================================
    // GET ALL RESOURCES
    // =========================================================

    @Test
    void getAllResources_shouldReturnResources() {

        ResourceType resourceType = ResourceType.builder()
                .id(1L)
                .name("Room")
                .build();

        Resource resource = Resource.builder()
                .id(1L)
                .name("Conference Room")
                .description("Meeting room")
                .location("Pune Office")
                .resourceType(resourceType)
                .pricePerHour(new BigDecimal("500.00"))
                .active(true)
                .build();

        when(resourceRepo.findAll())
                .thenReturn(List.of(resource));

        List<ResourceResponse> response =
                resourceService.getAllResources();

        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals(1L, response.get(0).getId());
        assertEquals(
                "Conference Room",
                response.get(0).getName()
        );

        verify(resourceRepo).findAll();
    }


    // =========================================================
    // GET RESOURCE BY ID
    // =========================================================

    @Test
    void getResourceById_shouldReturnResource() {

        ResourceType resourceType = ResourceType.builder()
                .id(1L)
                .name("Room")
                .build();

        Resource resource = Resource.builder()
                .id(1L)
                .name("Conference Room")
                .resourceType(resourceType)
                .pricePerHour(new BigDecimal("500.00"))
                .active(true)
                .build();

        when(resourceRepo.findById(1L))
                .thenReturn(Optional.of(resource));

        ResourceResponse response =
                resourceService.getResourceById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(
                "Conference Room",
                response.getName()
        );
    }


    @Test
    void getResourceById_shouldThrowException_whenNotFound() {

        when(resourceRepo.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> resourceService.getResourceById(1L)
        );
    }


    // =========================================================
    // UPDATE RESOURCE
    // =========================================================

    @Test
    void updateResource_shouldUpdateSuccessfully() {

        ResourceRequest request = new ResourceRequest();

        request.setName("Updated Room");
        request.setDescription("Updated description");
        request.setLocation("Pune Office");
        request.setResourceTypeId(1L);
        request.setPricePerHour(new BigDecimal("600.00"));
        request.setActive(true);

        ResourceType resourceType = ResourceType.builder()
                .id(1L)
                .name("Room")
                .build();

        Resource resource = Resource.builder()
                .id(1L)
                .name("Old Room")
                .description("Old description")
                .location("Old Location")
                .resourceType(resourceType)
                .pricePerHour(new BigDecimal("500.00"))
                .active(true)
                .build();

        when(resourceRepo.findById(1L))
                .thenReturn(Optional.of(resource));

        when(resourceRepo.existsByNameIgnoreCaseAndIdNot(
                "Updated Room",
                1L
        )).thenReturn(false);

        when(resourceTypeRepo.findById(1L))
                .thenReturn(Optional.of(resourceType));

        when(resourceRepo.save(resource))
                .thenReturn(resource);

        ResourceResponse response =
                resourceService.updateResource(
                        1L,
                        request
                );

        assertNotNull(response);
        assertEquals(
                "Updated Room",
                resource.getName()
        );

        assertEquals(
                "Updated description",
                resource.getDescription()
        );

        assertEquals(
                "Pune Office",
                resource.getLocation()
        );

        assertEquals(
                0,
                new BigDecimal("600.00")
                        .compareTo(resource.getPricePerHour())
        );

        verify(resourceRepo).save(resource);
    }


    @Test
    void updateResource_shouldThrowException_whenNotFound() {

        ResourceRequest request = new ResourceRequest();

        request.setName("Updated Room");
        request.setResourceTypeId(1L);
        request.setPricePerHour(new BigDecimal("600.00"));

        when(resourceRepo.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> resourceService.updateResource(
                        1L,
                        request
                )
        );

        verify(resourceRepo, never())
                .save(any(Resource.class));
    }


    @Test
    void updateResource_shouldThrowException_whenDuplicateName() {

        ResourceRequest request = new ResourceRequest();

        request.setName("Conference Room");
        request.setResourceTypeId(1L);
        request.setPricePerHour(new BigDecimal("600.00"));

        Resource resource = Resource.builder()
                .id(1L)
                .name("Old Room")
                .build();

        when(resourceRepo.findById(1L))
                .thenReturn(Optional.of(resource));

        when(resourceRepo.existsByNameIgnoreCaseAndIdNot(
                "Conference Room",
                1L
        )).thenReturn(true);

        assertThrows(
                ResourceAlreadyExistsException.class,
                () -> resourceService.updateResource(
                        1L,
                        request
                )
        );

        verify(resourceRepo, never())
                .save(any(Resource.class));
    }


    // =========================================================
    // DELETE RESOURCE
    // =========================================================

    @Test
    void deleteResource_shouldDeleteSuccessfully() {

        Resource resource = Resource.builder()
                .id(1L)
                .name("Conference Room")
                .build();

        when(resourceRepo.findById(1L))
                .thenReturn(Optional.of(resource));

        resourceService.deleteResource(1L);

        verify(resourceRepo).delete(resource);
    }


    @Test
    void deleteResource_shouldThrowException_whenNotFound() {

        when(resourceRepo.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> resourceService.deleteResource(1L)
        );

        verify(resourceRepo, never())
                .delete(any(Resource.class));
    }
}
