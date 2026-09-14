package com.Sanchita.Resource_Booking_System.Controller;

import com.Sanchita.Resource_Booking_System.DTO.resources.ResourceRequest;
import com.Sanchita.Resource_Booking_System.DTO.resources.ResourceResponse;
import com.Sanchita.Resource_Booking_System.Services.ResourceService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ResourceController.class)
@AutoConfigureMockMvc(addFilters = false)
class ResourceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ResourceService resourceService;


    @Test
    void createResource() throws Exception {

        Mockito.when(resourceService.createResource(
                any(ResourceRequest.class)
        )).thenReturn(new ResourceResponse());

        mockMvc.perform(
                post("/resources/createResource")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": "Conference Room",
                                    "description": "Meeting room",
                                    "location": "Pune",
                                    "resourceTypeId": 1,
                                    "pricePerHour": 500.00,
                                    "active": true
                                }
                                """)
        ).andExpect(status().isCreated());
    }


    @Test
    void getAllResources() throws Exception {

        Mockito.when(resourceService.getAllResources())
                .thenReturn(List.of(new ResourceResponse()));

        mockMvc.perform(
                get("/resources/getResource")
        ).andExpect(status().isOk());
    }


    @Test
    void getResourceById() throws Exception {

        Mockito.when(resourceService.getResourceById(1L))
                .thenReturn(new ResourceResponse());

        mockMvc.perform(
                get("/resources/getResourceById/1")
        ).andExpect(status().isOk());
    }


    @Test
    void updateResource() throws Exception {

        Mockito.when(resourceService.updateResource(
                Mockito.eq(1L),
                any(ResourceRequest.class)
        )).thenReturn(new ResourceResponse());

        mockMvc.perform(
                put("/resources/updateResource/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": "Updated Room",
                                    "description": "Updated meeting room",
                                    "location": "Pune",
                                    "resourceTypeId": 1,
                                    "pricePerHour": 600.00,
                                    "active": true
                                }
                                """)
        ).andExpect(status().isOk());
    }


    @Test
    void deleteResource() throws Exception {

        mockMvc.perform(
                delete("/resources/deleteResource/1")
        ).andExpect(status().isNoContent());
    }
}