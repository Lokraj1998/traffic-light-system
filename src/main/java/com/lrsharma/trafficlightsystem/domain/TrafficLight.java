package com.lrsharma.trafficlightsystem.domain;

public class TrafficLight {
    private final Direction direction;
    private LightColor color;

    public TrafficLight(Direction direction, LightColor color) {
        this.direction = direction;
        this.color = color;
    }

    public Direction getDirection() { return direction; }
    public LightColor getColor() { return color; }

    public void setColor(LightColor color) {
        this.color = color;
    }
}
