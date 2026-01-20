package com.lrsharma.trafficlightsystem.service;

import com.lrsharma.trafficlightsystem.domain.*;
import com.lrsharma.trafficlightsystem.exception.TrafficLightException;
import com.lrsharma.trafficlightsystem.repository.HistoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;

@ExtendWith(MockitoExtension.class)
class TrafficLightServiceTest {

    @Mock
    private HistoryRepository historyRepository;

    @InjectMocks
    private TrafficLightService service;

    @BeforeEach
    void setUp() {
    }


    @Test
    void shouldChangeLightSuccessfully() {
        // Act
        service.changeLight("default", Direction.NORTH, LightColor.GREEN);

        // Assert
        Intersection intersection = service.getIntersection("default");
        assertEquals(LightColor.GREEN,
                intersection.getLights().get(Direction.NORTH).getColor());

        Mockito.verify(historyRepository)
                .save("default", Direction.NORTH, LightColor.GREEN);
    }


    @Test
    void shouldAllowSameAxisGreen() {
        service.changeLight("default", Direction.NORTH, LightColor.GREEN);
        service.changeLight("default", Direction.SOUTH, LightColor.GREEN);

        Intersection intersection = service.getIntersection("default");

        assertEquals(LightColor.GREEN,
                intersection.getLights().get(Direction.NORTH).getColor());
        assertEquals(LightColor.GREEN,
                intersection.getLights().get(Direction.SOUTH).getColor());

        Mockito.verify(historyRepository)
                .save("default", Direction.NORTH, LightColor.GREEN);
        Mockito.verify(historyRepository)
                .save("default", Direction.SOUTH, LightColor.GREEN);
    }

    @Test
    void shouldNotAllowConflictingGreens() {
        service.changeLight("default", Direction.NORTH, LightColor.GREEN);

        TrafficLightException ex = assertThrows(
                TrafficLightException.class,
                () -> service.changeLight("default", Direction.EAST, LightColor.GREEN)
        );

        assertEquals(
                "Cannot set EAST to GREEN because NORTH is already GREEN",
                ex.getMessage()
        );

        Mockito.verify(historyRepository)
                .save("default", Direction.NORTH, LightColor.GREEN);

        // EAST should NOT be saved
        Mockito.verify(historyRepository, Mockito.never())
                .save("default", Direction.EAST, LightColor.GREEN);
    }


    @Test
    void shouldPauseIntersection() {
        service.pause("default");

        Intersection intersection = service.getIntersection("default");
        assertTrue(intersection.isPaused());
    }

    @Test
    void shouldNotAllowChangeWhenPaused() {
        service.pause("default");

        TrafficLightException ex = assertThrows(
                TrafficLightException.class,
                () -> service.changeLight("default", Direction.NORTH, LightColor.GREEN)
        );

        assertEquals(
                "Intersection is paused. Resume before changing lights.",
                ex.getMessage()
        );

        Mockito.verify(historyRepository, Mockito.never())
                .save(Mockito.any(), Mockito.any(), Mockito.any());
    }

    @Test
    void shouldResumeIntersection() {
        service.pause("default");
        service.resume("default");

        Intersection intersection = service.getIntersection("default");
        assertFalse(intersection.isPaused());
    }


    @Test
    void shouldAllowYellowEvenIfConflictExists() {
        service.changeLight("default", Direction.NORTH, LightColor.GREEN);

        // Should NOT throw because only GREEN is validated
        service.changeLight("default", Direction.EAST, LightColor.YELLOW);

        Intersection intersection = service.getIntersection("default");
        assertEquals(LightColor.YELLOW,
                intersection.getLights().get(Direction.EAST).getColor());

        Mockito.verify(historyRepository)
                .save("default", Direction.EAST, LightColor.YELLOW);
    }


    @Test
    void shouldHandleNullIntersectionGracefully() {
        assertThrows(
                NullPointerException.class,
                () -> service.changeLight("unknown", Direction.NORTH, LightColor.GREEN)
        );
    }

    @Test
    void shouldNotAllowConflictingGreensUnderConcurrency() throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(2);

        Runnable northTask = () ->
                service.changeLight("default", Direction.NORTH, LightColor.GREEN);

        Runnable eastTask = () -> {
            try {
                service.changeLight("default", Direction.EAST, LightColor.GREEN);
            } catch (TrafficLightException ignored) {}
        };

        executor.submit(northTask);
        executor.submit(eastTask);

        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);

        Intersection i = service.getIntersection("default");

        long greenCount = i.getLights().values().stream()
                .filter(l -> l.getColor() == LightColor.GREEN)
                .count();

        assertTrue(greenCount <= 2);
    }
}
