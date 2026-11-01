package com.goaltracker.config;

import com.goaltracker.exception.mapper.ConstraintViolationExceptionMapper;
import com.goaltracker.exception.mapper.GenericExceptionMapper;
import com.goaltracker.exception.mapper.NotAuthorizedExceptionMapper;
import com.goaltracker.exception.mapper.NotFoundExceptionMapper;
import com.goaltracker.exception.mapper.ValidationExceptionMapper;
import com.goaltracker.resource.*;
import com.goaltracker.security.JwtAuthFilter;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.stereotype.Component;

import jakarta.ws.rs.ApplicationPath;

@Component
@ApplicationPath("/api")
public class JerseyConfig extends ResourceConfig {

    public JerseyConfig() {
        register(JacksonFeature.class);
        register(JacksonConfig.class);

        register(JwtAuthFilter.class);
        register(NotFoundExceptionMapper.class);
        register(NotAuthorizedExceptionMapper.class);
        register(ValidationExceptionMapper.class);
        register(ConstraintViolationExceptionMapper.class);
        register(GenericExceptionMapper.class);

        register(AuthResource.class);
        register(MeResource.class);
        register(VersionResource.class);
        register(TrackResource.class);
        register(TopicResource.class);
        register(TopicReferenceResource.class);
        register(DayHourConfigResource.class);
        register(TrackAllocationResource.class);
        register(HolidayResource.class);
        register(DailyLogResource.class);
        register(TopicLogResource.class);
        register(DashboardResource.class);
        register(ReportResource.class);
        register(PredictionResource.class);
    }
}
