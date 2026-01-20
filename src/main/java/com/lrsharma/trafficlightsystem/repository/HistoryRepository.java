package com.lrsharma.trafficlightsystem.repository;

import com.lrsharma.trafficlightsystem.domain.Direction;
import com.lrsharma.trafficlightsystem.domain.LightColor;
import com.lrsharma.trafficlightsystem.domain.LightStateHistory;

import java.util.List;

public interface HistoryRepository {
    void save(String intersectionId, Direction direction, LightColor color);
    List<LightStateHistory> findAll(String intersectionId);
}