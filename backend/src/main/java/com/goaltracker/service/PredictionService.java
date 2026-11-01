package com.goaltracker.service;

import com.goaltracker.domain.PredictionSnapshot;
import com.goaltracker.domain.Track;
import com.goaltracker.dto.prediction.PredictionResponse;
import com.goaltracker.exception.ResourceNotFoundException;
import com.goaltracker.repository.PredictionSnapshotRepository;
import com.goaltracker.repository.TrackRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

/** Orchestrates: cache-freshness check -> deterministic stats -> optional AI narrative -> snapshot upsert. */
@Service
public class PredictionService {

    private final PredictionSnapshotRepository predictionSnapshotRepository;
    private final PredictionStatsService predictionStatsService;
    private final AiInsightService aiInsightService;
    private final TrackRepository trackRepository;
    private final Duration cacheTtl;

    public PredictionService(PredictionSnapshotRepository predictionSnapshotRepository,
                              PredictionStatsService predictionStatsService,
                              AiInsightService aiInsightService,
                              TrackRepository trackRepository,
                              @Value("${app.prediction.cache-ttl}") String cacheTtl) {
        this.predictionSnapshotRepository = predictionSnapshotRepository;
        this.predictionStatsService = predictionStatsService;
        this.aiInsightService = aiInsightService;
        this.trackRepository = trackRepository;
        this.cacheTtl = Duration.parse(cacheTtl);
    }

    public PredictionResponse getForTrack(Long userId, Long trackId, boolean forceRefresh) {
        Track track = trackRepository.findByIdAndUserId(trackId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Track not found"));

        Optional<PredictionSnapshot> cached = predictionSnapshotRepository.findByUserIdAndTrackId(userId, trackId);
        if (!forceRefresh && cached.isPresent() && isFresh(cached.get())) {
            return toResponse(cached.get());
        }

        StatsSnapshot stats = predictionStatsService.computeForTrack(userId, track);
        return toResponse(persist(userId, trackId, stats));
    }

    public PredictionResponse getOverall(Long userId, boolean forceRefresh) {
        Optional<PredictionSnapshot> cached = predictionSnapshotRepository.findOverallByUserId(userId);
        if (!forceRefresh && cached.isPresent() && isFresh(cached.get())) {
            return toResponse(cached.get());
        }

        List<Track> tracks = trackRepository.findByUserIdAndArchivedFalseOrderBySortOrderAsc(userId);
        StatsSnapshot stats = predictionStatsService.computeOverall(userId, tracks);
        return toResponse(persist(userId, null, stats));
    }

    private PredictionSnapshot persist(Long userId, Long trackId, StatsSnapshot stats) {
        PredictionSnapshot snapshot = (trackId != null
                ? predictionSnapshotRepository.findByUserIdAndTrackId(userId, trackId)
                : predictionSnapshotRepository.findOverallByUserId(userId))
                .orElseGet(() -> {
                    PredictionSnapshot s = new PredictionSnapshot();
                    s.setUserId(userId);
                    s.setTrackId(trackId);
                    return s;
                });

        snapshot.setPredictedCompletionDate(stats.predictedCompletionDate());
        snapshot.setPaceTopicsPerWeek(stats.paceTopicsPerWeek());
        snapshot.setCoveragePct(stats.coveragePct());
        snapshot.setConfidence(stats.confidence());
        snapshot.setComputedAt(Instant.now());

        Optional<String> narrative = aiInsightService.generateNarrative(stats);
        if (narrative.isPresent()) {
            snapshot.setAiNarrative(narrative.get());
            snapshot.setAiNarrativeGeneratedAt(Instant.now());
        } else {
            snapshot.setAiNarrative(null);
            snapshot.setAiNarrativeGeneratedAt(null);
        }

        return predictionSnapshotRepository.save(snapshot);
    }

    private boolean isFresh(PredictionSnapshot snapshot) {
        return Duration.between(snapshot.getComputedAt(), Instant.now()).compareTo(cacheTtl) < 0;
    }

    private PredictionResponse toResponse(PredictionSnapshot snapshot) {
        String trackName = snapshot.getTrackId() != null
                ? trackRepository.findById(snapshot.getTrackId()).map(Track::getName).orElse(null)
                : "Overall";
        return new PredictionResponse(snapshot.getTrackId(), trackName, snapshot.getPredictedCompletionDate(),
                snapshot.getPaceTopicsPerWeek(), snapshot.getCoveragePct(),
                snapshot.getConfidence() != null ? snapshot.getConfidence().name() : null,
                snapshot.getAiNarrative(), snapshot.getComputedAt());
    }
}
