package com.lrsharma.trafficlightsystem.domain;

import com.lrsharma.trafficlightsystem.enums.Direction;
import com.lrsharma.trafficlightsystem.enums.LightColor;

import java.util.EnumMap;
import java.util.Map;

public class Intersection {

    private final String id;
    private final Map<Direction, TrafficLight> lights = new EnumMap<>(Direction.class);
    private boolean paused = false;

    public Intersection(String id) {
        this.id = id;
        for (Direction d : Direction.values()) {
            lights.put(d, new TrafficLight(d, LightColor.RED));
        }
    }

    public synchronized Map<Direction, TrafficLight> getLights() {
        return new EnumMap<>(lights);
    }

    public synchronized void setLight(Direction direction, LightColor color) {
        lights.get(direction).setColor(color);
    }

    public synchronized boolean isPaused() {
        return paused;
    }

    public synchronized void pause() {
        paused = true;
    }

    public synchronized void resume() {
        paused = false;
    }
}
