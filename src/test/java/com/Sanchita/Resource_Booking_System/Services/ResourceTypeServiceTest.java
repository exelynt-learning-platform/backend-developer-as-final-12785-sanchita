package com.Sanchita.Resource_Booking_System.Services;

import com.Sanchita.Resource_Booking_System.DTO.ResourceType.ResourceTypeRequest;
import com.Sanchita.Resource_Booking_System.DTO.ResourceType.ResourceTypeResponse;
import com.Sanchita.Resource_Booking_System.Entity.ResourceType;
import com.Sanchita.Resource_Booking_System.Exception.ResourceTypeAlreadyExistsException;
import com.Sanchita.Resource_Booking_System.Exception.ResourceTypeInUseException;
import com.Sanchita.Resource_Booking_System.Exception.ResourceTypeNotFoundException;
import com.Sanchita.Resource_Booking_System.Repository.ResourceRepo;
import com.Sanchita.Resource_Booking_System.Repository.ResourceTypeRepo;
import com.Sanchita.Resource_Booking_System.Services.Impl.ResourceTypeServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResourceTypeServiceImplTest {

    @Mock
    private ResourceTypeRepo resourceTypeRepo;

    @Mock
    private ResourceRepo resourceRepo;

    @InjectMocks
    private ResourceTypeServiceImpl resourceTypeService;


    // =========================================================
    // CREATE RESOURCE TYPE
    // =========================================================

    @Test
    void createResourceType_shouldCreateSuccessfully() {

        ResourceTypeRequest request = new ResourceTypeRequest();
        request.setName("Vehicle");
        request.setDescription("Vehicles available for booking");

        ResourceType savedResourceType = ResourceType.builder()
                .id(1L)
                .name("Vehicle")
                .description("Vehicles available for booking")
                .active(true)
                .build();

        when(resourceTypeRepo.existsByNameIgnoreCase("Vehicle"))
                .thenReturn(false);

        when(resourceTypeRepo.save(any(ResourceType.class)))
                .thenReturn(savedResourceType);

        ResourceTypeResponse response =
                resourceTypeService.createResourceType(request);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Vehicle", response.getName());
        assertEquals(
                "Vehicles available for booking",
                response.getDescription()
        );
        assertTrue(response.isActive());

        verify(resourceTypeRepo)
                .existsByNameIgnoreCase("Vehicle");

        verify(resourceTypeRepo)
                .save(any(ResourceType.class));
    }


    @Test
    void createResourceType_shouldThrowException_whenNameAlreadyExists() {

        ResourceTypeRequest request = new ResourceTypeRequest();
        request.setName("Vehicle");
        request.setDescription("Vehicles");

        when(resourceTypeRepo.existsByNameIgnoreCase("Vehicle"))
                .thenReturn(true);

        assertThrows(
                ResourceTypeAlreadyExistsException.class,
                () -> resourceTypeService.createResourceType(request)
        );

        verify(resourceTypeRepo)
                .existsByNameIgnoreCase("Vehicle");

        verify(resourceTypeRepo, never())
                .save(any(ResourceType.class));
    }


    // =========================================================
    // GET ALL RESOURCE TYPES
    // =========================================================

    @Test
    void getAllResourceTypes_shouldReturnAllResourceTypes() {

        ResourceType vehicle = ResourceType.builder()
                .id(1L)
                .name("Vehicle")
                .description("Vehicles")
                .active(true)
                .build();

        ResourceType equipment = ResourceType.builder()
                .id(2L)
                .name("Equipment")
                .description("Office equipment")
                .active(true)
                .build();

        when(resourceTypeRepo.findAll())
                .thenReturn(List.of(vehicle, equipment));

        List<ResourceTypeResponse> response =
                resourceTypeService.getAllResourceTypes();

        assertNotNull(response);
        assertEquals(2, response.size());

        assertEquals(1L, response.get(0).getId());
        assertEquals("Vehicle", response.get(0).getName());

        assertEquals(2L, response.get(1).getId());
        assertEquals("Equipment", response.get(1).getName());

        verify(resourceTypeRepo).findAll();
    }


    // =========================================================
    // GET RESOURCE TYPE BY ID
    // =========================================================

    @Test
    void getResourceTypeById_shouldReturnResourceType() {

        ResourceType resourceType = ResourceType.builder()
                .id(1L)
                .name("Vehicle")
                .description("Vehicles")
                .active(true)
                .build();

        when(resourceTypeRepo.findById(1L))
                .thenReturn(Optional.of(resourceType));

        ResourceTypeResponse response =
                resourceTypeService.getResourceTypeById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Vehicle", response.getName());
        assertEquals("Vehicles", response.getDescription());
        assertTrue(response.isActive());

        verify(resourceTypeRepo).findById(1L);
    }


    @Test
    void getResourceTypeById_shouldThrowException_whenNotFound() {

        when(resourceTypeRepo.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceTypeNotFoundException.class,
                () -> resourceTypeService.getResourceTypeById(1L)
        );

        verify(resourceTypeRepo).findById(1L);
    }


    // =========================================================
    // UPDATE RESOURCE TYPE
    // =========================================================

    @Test
    void updateResourceType_shouldUpdateSuccessfully() {

        ResourceTypeRequest request = new ResourceTypeRequest();
        request.setName("Vehicle Updated");
        request.setDescription("Updated vehicle description");

        ResourceType existingResourceType = ResourceType.builder()
                .id(1L)
                .name("Vehicle")
                .description("Vehicles")
                .active(true)
                .build();

        when(resourceTypeRepo.findById(1L))
                .thenReturn(Optional.of(existingResourceType));

        when(resourceTypeRepo.findByNameIgnoreCase("Vehicle Updated"))
                .thenReturn(Optional.empty());

        ResourceTypeResponse response =
                resourceTypeService.updateResourceType(1L, request);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Vehicle Updated", response.getName());
        assertEquals(
                "Updated vehicle description",
                response.getDescription()
        );
        assertTrue(response.isActive());

        verify(resourceTypeRepo).findById(1L);

        verify(resourceTypeRepo)
                .findByNameIgnoreCase("Vehicle Updated");

        // Your service does not call save() during update.
        // JPA dirty checking handles the update.
        verify(resourceTypeRepo, never())
                .save(any(ResourceType.class));
    }


    @Test
    void updateResourceType_shouldThrowException_whenNotFound() {

        ResourceTypeRequest request = new ResourceTypeRequest();
        request.setName("Vehicle Updated");
        request.setDescription("Updated description");

        when(resourceTypeRepo.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceTypeNotFoundException.class,
                () -> resourceTypeService.updateResourceType(1L, request)
        );

        verify(resourceTypeRepo).findById(1L);

        verify(resourceTypeRepo, never())
                .findByNameIgnoreCase(anyString());
    }


    @Test
    void updateResourceType_shouldThrowException_whenNameAlreadyExists() {

        ResourceTypeRequest request = new ResourceTypeRequest();
        request.setName("Equipment");
        request.setDescription("Equipment description");

        ResourceType existingResourceType = ResourceType.builder()
                .id(1L)
                .name("Vehicle")
                .description("Vehicles")
                .active(true)
                .build();

        ResourceType anotherResourceType = ResourceType.builder()
                .id(2L)
                .name("Equipment")
                .description("Equipment")
                .active(true)
                .build();

        when(resourceTypeRepo.findById(1L))
                .thenReturn(Optional.of(existingResourceType));

        when(resourceTypeRepo.findByNameIgnoreCase("Equipment"))
                .thenReturn(Optional.of(anotherResourceType));

        assertThrows(
                ResourceTypeAlreadyExistsException.class,
                () -> resourceTypeService.updateResourceType(1L, request)
        );

        verify(resourceTypeRepo).findById(1L);

        verify(resourceTypeRepo)
                .findByNameIgnoreCase("Equipment");
    }


    // =========================================================
    // DELETE RESOURCE TYPE
    // =========================================================

    @Test
    void deleteResourceType_shouldDeleteSuccessfully() {

        ResourceType resourceType = ResourceType.builder()
                .id(1L)
                .name("Vehicle")
                .description("Vehicles")
                .active(true)
                .build();

        when(resourceTypeRepo.findById(1L))
                .thenReturn(Optional.of(resourceType));

        when(resourceRepo.existsByResourceTypeId(1L))
                .thenReturn(false);

        resourceTypeService.deleteResourceType(1L);

        verify(resourceTypeRepo).findById(1L);

        verify(resourceRepo)
                .existsByResourceTypeId(1L);

        verify(resourceTypeRepo)
                .delete(resourceType);
    }


    @Test
    void deleteResourceType_shouldThrowException_whenNotFound() {

        when(resourceTypeRepo.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceTypeNotFoundException.class,
                () -> resourceTypeService.deleteResourceType(1L)
        );

        verify(resourceTypeRepo).findById(1L);

        verify(resourceRepo, never())
                .existsByResourceTypeId(anyLong());

        verify(resourceTypeRepo, never())
                .delete(any(ResourceType.class));
    }


    @Test
    void deleteResourceType_shouldThrowException_whenResourceTypeInUse() {

        ResourceType resourceType = ResourceType.builder()
                .id(1L)
                .name("Vehicle")
                .description("Vehicles")
                .active(true)
                .build();

        when(resourceTypeRepo.findById(1L))
                .thenReturn(Optional.of(resourceType));

        when(resourceRepo.existsByResourceTypeId(1L))
                .thenReturn(true);

        assertThrows(
                ResourceTypeInUseException.class,
                () -> resourceTypeService.deleteResourceType(1L)
        );

        verify(resourceTypeRepo).findById(1L);

        verify(resourceRepo)
                .existsByResourceTypeId(1L);

        verify(resourceTypeRepo, never())
                .delete(any(ResourceType.class));
    }
}