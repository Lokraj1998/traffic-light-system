package com.lrsharma.trafficlightsystem.repository;

import com.lrsharma.trafficlightsystem.domain.Direction;
import com.lrsharma.trafficlightsystem.domain.LightColor;
import com.lrsharma.trafficlightsystem.domain.LightStateHistory;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Repository
public class InMemoryHistoryRepository implements HistoryRepository {

    private final Map<String, List<LightStateHistory>> store = new ConcurrentHashMap<>();

    @Override
    public void save(String id, Direction d, LightColor c) {
        store.computeIfAbsent(id, k -> new CopyOnWriteArrayList<>())
             .add(new LightStateHistory(d, c, LocalDateTime.now()));
    }

    @Override
    public List<LightStateHistory> findAll(String id) {
        return store.getOrDefault(id, List.of());
    }
}
