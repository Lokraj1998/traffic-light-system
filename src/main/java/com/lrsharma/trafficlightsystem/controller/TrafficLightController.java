package com.lrsharma.trafficlightsystem.controller;

import com.lrsharma.trafficlightsystem.enums.Direction;
import com.lrsharma.trafficlightsystem.domain.Intersection;
import com.lrsharma.trafficlightsystem.enums.LightColor;
import com.lrsharma.trafficlightsystem.domain.TrafficLight;
import com.lrsharma.trafficlightsystem.service.TrafficLightService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/intersections")
public class TrafficLightController {

    private final TrafficLightService service;

    public TrafficLightController(TrafficLightService service) {
        this.service = service;
    }

    @GetMapping("/{id}/state")
    public Map<Direction, LightColor> getState(@PathVariable String id) {
        Intersection i = service.getIntersection(id);
        return i.getLights().values().stream()
                .collect(Collectors.toMap(
                        TrafficLight::getDirection,
                        TrafficLight::getColor
                ));
    }

    @PostMapping("/{id}/change")
    public ResponseEntity<?> changeLight(
            @PathVariable String id,
            @RequestParam Direction direction,
            @RequestParam LightColor color) {

        service.changeLight(id, direction, color);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/pause")
    public void pause(@PathVariable String id) {
        service.pause(id);
    }

    @PostMapping("/{id}/resume")
    public void resume(@PathVariable String id) {
        service.resume(id);
    }
}
