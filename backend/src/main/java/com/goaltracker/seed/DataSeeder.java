package com.goaltracker.seed;

import com.goaltracker.domain.*;
import com.goaltracker.domain.enums.DayType;
import com.goaltracker.domain.enums.Role;
import com.goaltracker.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Idempotent startup seeder: creates the single demo user (password BCrypt-hashed here, never in
 * a SQL migration) plus default hour config and the 3 starter tracks/topics/references the very
 * first time the app runs against an empty database.
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private static final Map<DayType, BigDecimal> DEFAULT_DAY_HOURS = Map.of(
            DayType.WEEKDAY, new BigDecimal("3.00"),
            DayType.WEEKEND, new BigDecimal("5.00"),
            DayType.HOLIDAY, new BigDecimal("5.00")
    );

    /** Per-track hours for weekday / weekend / holiday, in the same order as {@link SeedData#defaultTracks()}. */
    private static final Map<DayType, List<BigDecimal>> DEFAULT_TRACK_SPLIT = Map.of(
            DayType.WEEKDAY, List.of(new BigDecimal("1.00"), new BigDecimal("1.00"), new BigDecimal("1.00")),
            DayType.WEEKEND, List.of(new BigDecimal("2.00"), new BigDecimal("1.50"), new BigDecimal("1.50")),
            DayType.HOLIDAY, List.of(new BigDecimal("2.00"), new BigDecimal("1.50"), new BigDecimal("1.50"))
    );

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final DayTypeHourConfigRepository dayTypeHourConfigRepository;
    private final TrackRepository trackRepository;
    private final TopicRepository topicRepository;
    private final TopicReferenceRepository topicReferenceRepository;
    private final TrackHourAllocationRepository trackHourAllocationRepository;
    private final String seedUsername;
    private final String seedPassword;

    public DataSeeder(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       DayTypeHourConfigRepository dayTypeHourConfigRepository, TrackRepository trackRepository,
                       TopicRepository topicRepository, TopicReferenceRepository topicReferenceRepository,
                       TrackHourAllocationRepository trackHourAllocationRepository,
                       @Value("${app.seed.username}") String seedUsername,
                       @Value("${app.seed.password}") String seedPassword) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.dayTypeHourConfigRepository = dayTypeHourConfigRepository;
        this.trackRepository = trackRepository;
        this.topicRepository = topicRepository;
        this.topicReferenceRepository = topicReferenceRepository;
        this.trackHourAllocationRepository = trackHourAllocationRepository;
        this.seedUsername = seedUsername;
        this.seedPassword = seedPassword;
    }

    @Override
    @Transactional
    public void run(String... args) {
        User user = userRepository.findByUsername(seedUsername).orElseGet(this::createSeedUser);
        seedDayHourConfig(user.getId());
        seedTracksIfEmpty(user.getId());
    }

    private User createSeedUser() {
        User user = new User();
        user.setUsername(seedUsername);
        user.setPasswordHash(passwordEncoder.encode(seedPassword));
        user.setDisplayName(seedUsername);
        user.setRole(Role.USER);
        User saved = userRepository.save(user);
        log.info("Seeded default user '{}'", seedUsername);
        return saved;
    }

    private void seedDayHourConfig(Long userId) {
        if (!dayTypeHourConfigRepository.findByUserId(userId).isEmpty()) {
            return;
        }
        DEFAULT_DAY_HOURS.forEach((dayType, hours) -> {
            DayTypeHourConfig config = new DayTypeHourConfig();
            config.setUserId(userId);
            config.setDayType(dayType);
            config.setTotalHours(hours);
            dayTypeHourConfigRepository.save(config);
        });
        log.info("Seeded default day-type hour budgets for user {}", userId);
    }

    private void seedTracksIfEmpty(Long userId) {
        if (!trackRepository.findByUserIdOrderBySortOrderAsc(userId).isEmpty()) {
            return;
        }

        List<SeedData.TrackSeed> trackSeeds = SeedData.defaultTracks();
        for (int i = 0; i < trackSeeds.size(); i++) {
            SeedData.TrackSeed trackSeed = trackSeeds.get(i);

            Track track = new Track();
            track.setUserId(userId);
            track.setName(trackSeed.name());
            track.setDescription(trackSeed.description());
            track.setColorHex(trackSeed.colorHex());
            track.setSortOrder(i);
            Track savedTrack = trackRepository.save(track);

            int topicOrder = 0;
            for (SeedData.TopicSeed topicSeed : trackSeed.topics()) {
                Topic topic = new Topic();
                topic.setUserId(userId);
                topic.setTrackId(savedTrack.getId());
                topic.setTitle(topicSeed.title());
                topic.setDescription(topicSeed.description());
                topic.setEstimatedMinutes(topicSeed.estimatedMinutes());
                topic.setSortOrder(topicOrder++);
                Topic savedTopic = topicRepository.save(topic);

                int refOrder = 0;
                for (SeedData.ReferenceSeed refSeed : topicSeed.references()) {
                    TopicReference reference = new TopicReference();
                    reference.setUserId(userId);
                    reference.setTopicId(savedTopic.getId());
                    reference.setReferenceType(refSeed.type());
                    reference.setTitle(refSeed.title());
                    reference.setUrl(refSeed.url());
                    reference.setSortOrder(refOrder++);
                    topicReferenceRepository.save(reference);
                }
            }

            for (DayType dayType : DayType.values()) {
                TrackHourAllocation allocation = new TrackHourAllocation();
                allocation.setUserId(userId);
                allocation.setTrackId(savedTrack.getId());
                allocation.setDayType(dayType);
                allocation.setAllocatedHours(DEFAULT_TRACK_SPLIT.get(dayType).get(i));
                trackHourAllocationRepository.save(allocation);
            }
        }
        log.info("Seeded {} default tracks with topics/references for user {}", trackSeeds.size(), userId);
    }
}
