package com.lrsharma.trafficlightsystem.domain;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TrafficRulesTest {

    @ParameterizedTest
    @CsvSource({
            "NORTH,EAST,true",
            "NORTH,WEST,true",
            "SOUTH,EAST,true",
            "SOUTH,WEST,true",
            "NORTH,SOUTH,false",
            "EAST,WEST,false"
    })
    void shouldDetectConflicts(Direction d1, Direction d2, boolean expected) {
        assertEquals(expected, TrafficRules.isConflict(d1, d2));
    }

}
