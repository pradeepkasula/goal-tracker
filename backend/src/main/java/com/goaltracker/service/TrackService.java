package com.goaltracker.service;

import com.goaltracker.domain.Track;
import com.goaltracker.dto.track.TrackRequest;
import com.goaltracker.dto.track.TrackResponse;
import com.goaltracker.exception.ResourceNotFoundException;
import com.goaltracker.exception.ValidationException;
import com.goaltracker.repository.TrackRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TrackService {

    private final TrackRepository trackRepository;

    public TrackService(TrackRepository trackRepository) {
        this.trackRepository = trackRepository;
    }

    public List<TrackResponse> listTracks(Long userId, boolean includeArchived) {
        List<Track> tracks = includeArchived
                ? trackRepository.findByUserIdOrderBySortOrderAsc(userId)
                : trackRepository.findByUserIdAndArchivedFalseOrderBySortOrderAsc(userId);
        return tracks.stream().map(this::toResponse).toList();
    }

    public TrackResponse getTrack(Long userId, Long trackId) {
        return toResponse(findOwned(userId, trackId));
    }

    @Transactional
    public TrackResponse createTrack(Long userId, TrackRequest request) {
        if (trackRepository.existsByUserIdAndNameIgnoreCase(userId, request.name())) {
            throw new ValidationException("A track named '" + request.name() + "' already exists");
        }
        Track track = new Track();
        track.setUserId(userId);
        apply(track, request);
        return toResponse(trackRepository.save(track));
    }

    @Transactional
    public TrackResponse updateTrack(Long userId, Long trackId, TrackRequest request) {
        Track track = findOwned(userId, trackId);
        apply(track, request);
        return toResponse(trackRepository.save(track));
    }

    @Transactional
    public void deleteTrack(Long userId, Long trackId) {
        Track track = findOwned(userId, trackId);
        trackRepository.delete(track);
    }

    @Transactional
    public TrackResponse setArchived(Long userId, Long trackId, boolean archived) {
        Track track = findOwned(userId, trackId);
        track.setArchived(archived);
        return toResponse(trackRepository.save(track));
    }

    public Track findOwned(Long userId, Long trackId) {
        return trackRepository.findByIdAndUserId(trackId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Track not found"));
    }

    private void apply(Track track, TrackRequest request) {
        track.setName(request.name());
        track.setDescription(request.description());
        track.setColorHex(request.colorHex());
        if (request.sortOrder() != null) {
            track.setSortOrder(request.sortOrder());
        }
    }

    private TrackResponse toResponse(Track track) {
        return new TrackResponse(track.getId(), track.getName(), track.getDescription(),
                track.getColorHex(), track.getSortOrder(), track.getArchived());
    }
}
