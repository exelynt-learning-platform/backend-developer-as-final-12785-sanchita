package com.Sanchita.Resource_Booking_System.Controller;

import com.Sanchita.Resource_Booking_System.DTO.ResourceType.ResourceTypeRequest;
import com.Sanchita.Resource_Booking_System.DTO.ResourceType.ResourceTypeResponse;
import com.Sanchita.Resource_Booking_System.Services.ResourceTypeService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ResourceTypeController.class)
@AutoConfigureMockMvc(addFilters = false)
class ResourceTypeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ResourceTypeService resourceTypeService;


    @Test
    void createResourceType() throws Exception {

        Mockito.when(resourceTypeService.createResourceType(
                any(ResourceTypeRequest.class)
        )).thenReturn(new ResourceTypeResponse());

        mockMvc.perform(
                post("/resource-types/create-resource-type")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": "Room",
                                    "description": "Meeting Room"
                                }
                                """)
        ).andExpect(status().isCreated());
    }


    @Test
    void getAllResourceTypes() throws Exception {

        Mockito.when(resourceTypeService.getAllResourceTypes())
                .thenReturn(List.of(new ResourceTypeResponse()));

        mockMvc.perform(
                get("/resource-types/get-resource-type")
        ).andExpect(status().isOk());
    }


    @Test
    void getResourceTypeById() throws Exception {

        Mockito.when(resourceTypeService.getResourceTypeById(1L))
                .thenReturn(new ResourceTypeResponse());

        mockMvc.perform(
                get("/resource-types/get-resource-type-by-Id/1")
        ).andExpect(status().isOk());
    }


    @Test
    void updateResourceType() throws Exception {

        Mockito.when(resourceTypeService.updateResourceType(
                Mockito.eq(1L),
                any(ResourceTypeRequest.class)
        )).thenReturn(new ResourceTypeResponse());

        mockMvc.perform(
                put("/resource-types/update-resource-type/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": "Updated Room",
                                    "description": "Updated Meeting Room"
                                }
                                """)
        ).andExpect(status().isOk());
    }


    @Test
    void deleteResourceType() throws Exception {

        mockMvc.perform(
                delete("/resource-types/delete-resource-type/1")
        ).andExpect(status().isNoContent());
    }
}