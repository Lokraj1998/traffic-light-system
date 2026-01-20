package com.lrsharma.trafficlightsystem.service;

import com.lrsharma.trafficlightsystem.domain.Direction;
import com.lrsharma.trafficlightsystem.domain.Intersection;
import com.lrsharma.trafficlightsystem.domain.LightColor;
import com.lrsharma.trafficlightsystem.domain.TrafficRules;
import com.lrsharma.trafficlightsystem.exception.TrafficLightException;
import com.lrsharma.trafficlightsystem.repository.HistoryRepository;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TrafficLightService {

    private final Map<String, Intersection> intersections = new ConcurrentHashMap<>();
    private final HistoryRepository historyRepository;

    public TrafficLightService(HistoryRepository historyRepository) {
        this.historyRepository = historyRepository;
        intersections.put("default", new Intersection("default"));
    }

    public Intersection getIntersection(String id) {
        return intersections.get(id);
    }

    public synchronized void changeLight(String id, Direction direction, LightColor newColor) {
        Intersection intersection = getIntersection(id);

        if (intersection.isPaused()) {
            throw new TrafficLightException("Intersection is paused. Resume before changing lights.");
        }

        if (newColor == LightColor.GREEN) {
            validateNoConflicts(intersection, direction);
        }

        intersection.setLight(direction, newColor);
        historyRepository.save(id, direction, newColor);
    }

    private void validateNoConflicts(Intersection intersection, Direction direction) {
        intersection.getLights().forEach((dir, light) -> {
            if (light.getColor() == LightColor.GREEN &&
                    TrafficRules.isConflict(dir, direction)) {

                throw new TrafficLightException("Cannot set " + direction + " to GREEN because " + dir + " is already GREEN");
            }
        });
    }

    public void pause(String id) {
        getIntersection(id).pause();
    }

    public void resume(String id) {
        getIntersection(id).resume();
    }
}
