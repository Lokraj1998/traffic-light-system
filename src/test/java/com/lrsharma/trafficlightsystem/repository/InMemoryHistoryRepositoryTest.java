package com.lrsharma.trafficlightsystem.repository;

import com.lrsharma.trafficlightsystem.enums.Direction;
import com.lrsharma.trafficlightsystem.enums.LightColor;
import com.lrsharma.trafficlightsystem.domain.LightStateHistory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryRepositoryTest {

    private InMemoryHistoryRepository repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryHistoryRepository();
    }


    @Test
    void shouldSaveAndRetrieveHistory() {
        // Act
        repository.save("default", Direction.NORTH, LightColor.GREEN);
        repository.save("default", Direction.NORTH, LightColor.YELLOW);

        // Assert
        List<LightStateHistory> history = repository.findAll("default");

        assertEquals(2, history.size());

        assertEquals(Direction.NORTH, history.get(0).getDirection());
        assertEquals(LightColor.GREEN, history.get(0).getColor());

        assertEquals(Direction.NORTH, history.get(1).getDirection());
        assertEquals(LightColor.YELLOW, history.get(1).getColor());

        assertNotNull(history.get(0).getTimestamp());
        assertNotNull(history.get(1).getTimestamp());
    }


    @Test
    void shouldKeepHistorySeparatePerIntersection() {
        repository.save("intersection-1", Direction.NORTH, LightColor.GREEN);
        repository.save("intersection-2", Direction.EAST, LightColor.RED);

        List<LightStateHistory> history1 = repository.findAll("intersection-1");
        List<LightStateHistory> history2 = repository.findAll("intersection-2");

        assertEquals(1, history1.size());
        assertEquals(1, history2.size());

        assertEquals(Direction.NORTH, history1.get(0).getDirection());
        assertEquals(Direction.EAST, history2.get(0).getDirection());
    }


    @Test
    void shouldReturnEmptyListForUnknownIntersection() {
        List<LightStateHistory> history = repository.findAll("unknown");

        assertNotNull(history);
        assertTrue(history.isEmpty());
    }


    @Test
    void shouldHandleConcurrentWrites() throws InterruptedException {
        int threads = 10;
        int writesPerThread = 50;

        Thread[] workers = new Thread[threads];

        for (int i = 0; i < threads; i++) {
            workers[i] = new Thread(() -> {
                for (int j = 0; j < writesPerThread; j++) {
                    repository.save("default", Direction.NORTH, LightColor.GREEN);
                }
            });
        }

        // Act
        for (Thread t : workers) t.start();
        for (Thread t : workers) t.join();

        // Assert
        List<LightStateHistory> history = repository.findAll("default");
        assertEquals(threads * writesPerThread, history.size());
    }
}
