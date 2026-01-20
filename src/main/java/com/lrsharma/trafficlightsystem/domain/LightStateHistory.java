package com.lrsharma.trafficlightsystem.domain;

import com.lrsharma.trafficlightsystem.enums.Direction;
import com.lrsharma.trafficlightsystem.enums.LightColor;

import java.time.LocalDateTime;

public class LightStateHistory {

    private final Direction direction;
    private final LightColor color;
    private final LocalDateTime timestamp;

    public LightStateHistory(Direction direction, LightColor color, LocalDateTime timestamp) {
        this.direction = direction;
        this.color = color;
        this.timestamp = timestamp;
    }

    public Direction getDirection() {
        return direction;
    }

    public LightColor getColor() {
        return color;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
