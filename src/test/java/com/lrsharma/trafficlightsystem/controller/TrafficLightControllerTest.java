package com.lrsharma.trafficlightsystem.controller;

import com.lrsharma.trafficlightsystem.domain.*;
import com.lrsharma.trafficlightsystem.exception.TrafficLightException;
import com.lrsharma.trafficlightsystem.service.TrafficLightService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TrafficLightController.class)
class TrafficLightControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TrafficLightService service;


    @Test
    void shouldReturnCurrentState() throws Exception {
        Intersection intersection = new Intersection("default");
        intersection.setLight(Direction.NORTH, LightColor.GREEN);
        intersection.setLight(Direction.SOUTH, LightColor.RED);
        intersection.setLight(Direction.EAST, LightColor.RED);
        intersection.setLight(Direction.WEST, LightColor.RED);

        Mockito.when(service.getIntersection("default"))
                .thenReturn(intersection);

        mockMvc.perform(get("/api/intersections/default/state"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.NORTH", is("GREEN")))
                .andExpect(jsonPath("$.SOUTH", is("RED")))
                .andExpect(jsonPath("$.EAST", is("RED")))
                .andExpect(jsonPath("$.WEST", is("RED")));
    }


    @Test
    void shouldChangeLightSuccessfully() throws Exception {
        Mockito.doNothing()
                .when(service)
                .changeLight("default", Direction.NORTH, LightColor.GREEN);

        mockMvc.perform(post("/api/intersections/default/change")
                        .param("direction", "NORTH")
                        .param("color", "GREEN"))
                .andExpect(status().isOk());

        Mockito.verify(service)
                .changeLight("default", Direction.NORTH, LightColor.GREEN);
    }

    @Test
    void shouldReturnConflictWhenDirectionIsInvalid() throws Exception {
        Mockito.doThrow(new TrafficLightException(
                        "Cannot set EAST to GREEN because NORTH is already GREEN"))
                .when(service)
                .changeLight("default", Direction.EAST, LightColor.GREEN);

        mockMvc.perform(post("/api/intersections/default/change")
                        .param("direction", "EAST")
                        .param("color", "GREEN"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error", is("Traffic Light Rule Violation")))
                .andExpect(jsonPath("$.message",
                        is("Cannot set EAST to GREEN because NORTH is already GREEN")));
    }


    @Test
    void shouldPauseIntersection() throws Exception {
        Mockito.doNothing()
                .when(service)
                .pause("default");

        mockMvc.perform(post("/api/intersections/default/pause"))
                .andExpect(status().isOk());

        Mockito.verify(service).pause("default");
    }


    @Test
    void shouldResumeIntersection() throws Exception {
        Mockito.doNothing()
                .when(service)
                .resume("default");

        mockMvc.perform(post("/api/intersections/default/resume"))
                .andExpect(status().isOk());

        Mockito.verify(service).resume("default");
    }


    @Test
    void shouldReturnBadRequestForInvalidDirection() throws Exception {
        mockMvc.perform(post("/api/intersections/default/change")
                        .param("direction", "INVALID")
                        .param("color", "GREEN"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestForInvalidColor() throws Exception {
        mockMvc.perform(post("/api/intersections/default/change")
                        .param("direction", "NORTH")
                        .param("color", "BLUE"))
                .andExpect(status().isBadRequest());
    }
}
