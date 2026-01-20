package com.lrsharma.trafficlightsystem.domain;

import com.lrsharma.trafficlightsystem.enums.Direction;

public class TrafficRules {

    public static boolean isConflict(Direction d1, Direction d2) {
        if ((d1 == Direction.NORTH || d1 == Direction.SOUTH) &&
            (d2 == Direction.EAST || d2 == Direction.WEST)) {
            return true;
        }

        if ((d2 == Direction.NORTH || d2 == Direction.SOUTH) &&
            (d1 == Direction.EAST || d1 == Direction.WEST)) {
            return true;
        }

        return false;
    }
}
