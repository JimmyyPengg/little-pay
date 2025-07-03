package com.little.pay.service;

import com.little.pay.entities.TapRecord;
import com.little.pay.entities.TapType;
import com.little.pay.entities.Trip;
import com.little.pay.entities.TripStatus;
import com.little.pay.services.PriceInquiryServiceImpl;
import com.little.pay.services.TripService;
import com.little.pay.services.TripServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TripServiceImplTest {
  private static final String COMPANY_ID = "CompanyA";
  private static final String BUS_ID = "Bus110";
  private static final String PAN = "5500005555555559";

  private TripService tripService;

  @BeforeEach
  public void setup() {
    this.tripService = new TripServiceImpl(new PriceInquiryServiceImpl());
  }

  @Test
  public void testProcessCompletedTrip() {
    final List<TapRecord> tapRecordList =
            List.of(
                 createTapRecord(1, OffsetDateTime.now(), TapType.ON, "Stop1"),
                 createTapRecord(2, OffsetDateTime.now().plusMinutes(10), TapType.OFF, "Stop3")
            );
    final List<Trip> tripList = tripService.process(tapRecordList);
    assertEquals(tripList.size(), 1);

    final Trip trip = tripList.get(0);
    assertTrip(trip, TripStatus.COMPLETED, Optional.of("Stop1"), Optional.of("Stop3"));
  }

  @Test
  public void testProcessCancelledTrip() {
    final List<TapRecord> tapRecordList =
            List.of(
                createTapRecord(1, OffsetDateTime.now(), TapType.ON, "Stop1"),
                createTapRecord(2, OffsetDateTime.now(), TapType.OFF, "Stop1")
            );
    final List<Trip> tripList = tripService.process(tapRecordList);
    assertEquals(tripList.size(), 1);

    final Trip trip = tripList.get(0);
    assertTrip(trip, TripStatus.CANCELLED, Optional.of("Stop1"), Optional.of("Stop1"));
  }

  @Test
  public void testProcessInCompleteTripOnlyTapOn() {
    final List<TapRecord> tapRecordList =
            List.of(
                createTapRecord(1, OffsetDateTime.now(), TapType.ON, "Stop1")
            );
    final List<Trip> tripList = tripService.process(tapRecordList);
    assertEquals(1, tripList.size());

    final Trip trip = tripList.get(0);
    assertTrip(trip, TripStatus.INCOMPLETE, Optional.empty(), Optional.of("Stop1"));
  }

  @Test
  public void testProcessInCompleteTripOnlyTapOff() {
    final List<TapRecord> tapRecordList =
            List.of(
                 createTapRecord(1, OffsetDateTime.now(), TapType.OFF, "Stop1")
            );
    final List<Trip> tripList = tripService.process(tapRecordList);
    assertEquals(1, tripList.size());

    final Trip trip = tripList.get(0);
    assertTrip(trip, TripStatus.INCOMPLETE, Optional.empty(), Optional.of("Stop1"));
  }

  private TapRecord createTapRecord(final int id, final OffsetDateTime dateTime, final TapType tapType, final String stopId) {
    return new TapRecord(id, dateTime, tapType, stopId, COMPANY_ID, BUS_ID, PAN);
  }

  private void assertTrip(final Trip trip, final TripStatus expectedTripStatus, final Optional<String> expectedFromStopId, final Optional<String> expectedToStopId) {
    assertEquals(expectedTripStatus, trip.getTripStatus());
    assertEquals(BUS_ID, trip.getBusId());
    assertEquals(COMPANY_ID, trip.getCompanyId());
    assertEquals(PAN, trip.getPan());
    assertEquals(expectedFromStopId, trip.getFromStopId());
    assertEquals(expectedToStopId, trip.getToStopId());
  }
}
