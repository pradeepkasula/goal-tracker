package com.goaltracker.resource;

import com.goaltracker.dto.holiday.HolidayRequest;
import com.goaltracker.dto.holiday.HolidayResponse;
import com.goaltracker.security.CurrentUserResolver;
import com.goaltracker.service.HolidayService;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.SecurityContext;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@Path("holidays")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class HolidayResource {

    private final HolidayService holidayService;

    public HolidayResource(HolidayService holidayService) {
        this.holidayService = holidayService;
    }

    @GET
    public List<HolidayResponse> list(@Context SecurityContext sc,
                                       @QueryParam("from") String from,
                                       @QueryParam("to") String to) {
        LocalDate fromDate = from != null ? LocalDate.parse(from) : LocalDate.now().withDayOfYear(1);
        LocalDate toDate = to != null ? LocalDate.parse(to) : fromDate.plusYears(1);
        return holidayService.listHolidays(CurrentUserResolver.requireUserId(sc), fromDate, toDate);
    }

    @POST
    public HolidayResponse add(@Context SecurityContext sc, @Valid HolidayRequest request) {
        return holidayService.addHoliday(CurrentUserResolver.requireUserId(sc), request);
    }

    @DELETE
    @Path("{holidayId}")
    public void delete(@Context SecurityContext sc, @PathParam("holidayId") Long holidayId) {
        holidayService.deleteHoliday(CurrentUserResolver.requireUserId(sc), holidayId);
    }
}
