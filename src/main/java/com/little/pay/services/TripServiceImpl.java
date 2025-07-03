package com.little.pay.services;

import com.little.pay.entities.TapRecord;
import com.little.pay.entities.TapType;
import com.little.pay.entities.Trip;
import com.little.pay.entities.TripStatus;
import com.little.pay.exceptions.LittlePayException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;

@Component
public class TripServiceImpl implements TripService {
  private static final String STOP_MAX = "StopMax";
  private final PriceInquiryService priceInquiryService;

  @Autowired
  public TripServiceImpl(final PriceInquiryService priceInquiryService) {
    this.priceInquiryService = priceInquiryService;
  }

  @Override
  public List<Trip> process(final List<TapRecord> tapRecordList) {
    final Map<String, List<TapRecord>> tapRecordsGroupByPan = tapRecordList.stream().collect(groupingBy(TapRecord::getPan));
    // each grouped tapRecord entry can be an SQS event here,
    // we can convert calculateTrip() method to be SQS event consumer in the real world.
    final List<List<Trip>> result = tapRecordsGroupByPan.entrySet()
            .parallelStream()
            .map(tapRecordEntry -> calculateTrip(tapRecordEntry.getValue()))
            .toList();

    final List<Trip> tripList = new ArrayList<>();
    result.forEach(tripList::addAll);
    return tripList;
  }

  private List<Trip> calculateTrip(final List<TapRecord> tapRecordList) {
    final List<TapRecord> sortedTapRecordList =
            tapRecordList.stream()
                    .sorted((r1, r2) -> r1.getDateTimeUtc().isAfter(r2.getDateTimeUtc()) ? 1 : -1).toList();

    final List<Trip> tripResult = new ArrayList<>();
    TapRecord previousRecord = null;

    for (final TapRecord currentRecord : sortedTapRecordList) {
      if (TapType.ON == currentRecord.getTapType() && previousRecord == null) {

        previousRecord = currentRecord;

      } else if (TapType.ON == currentRecord.getTapType() && previousRecord != null) {

        final Trip incompleteTrip = processIncompleteTripOnlyTapOn(previousRecord);
        tripResult.add(incompleteTrip);
        previousRecord = currentRecord;

      } else if (TapType.OFF == currentRecord.getTapType() && previousRecord != null) {

        if (previousRecord.getStopId().equals(currentRecord.getStopId())) {
          final Trip cancelledTrip = processCancelledTrip(previousRecord, currentRecord);
          tripResult.add(cancelledTrip);
        } else {
          final Trip completedTrip = processCompletedTrip(previousRecord, currentRecord);
          tripResult.add(completedTrip);
        }
        previousRecord = null;

      } else if (TapType.OFF == currentRecord.getTapType() && previousRecord == null) {

        final Trip incompleteTrip = processIncompleteTripOnlyTapOff(currentRecord);
        tripResult.add(incompleteTrip);
        previousRecord = null;

      } else {
        throw new LittlePayException("Unknown tap type: " + currentRecord.getTapType());
      }
    }

    if (previousRecord != null) {
      final Trip incompleteTrip = processIncompleteTripOnlyTapOff(previousRecord);
      tripResult.add(incompleteTrip);
    }
    return tripResult;
  }

  private Trip processCompletedTrip(final TapRecord previousRecord, final TapRecord currentRecord) {
    final BigDecimal chargeAmount = priceInquiryService.checkPrice(previousRecord.getStopId(), currentRecord.getStopId());
    return new Trip(
            previousRecord.getDateTimeUtc(),
            currentRecord.getDateTimeUtc(),
            previousRecord.getStopId(),
            currentRecord.getStopId(),
            chargeAmount,
            previousRecord.getCompanyId(),
            previousRecord.getBusId(),
            previousRecord.getPan(),
            TripStatus.COMPLETED);
  }

  private Trip processIncompleteTripOnlyTapOn(final TapRecord tapRecord) {
    final BigDecimal chargeAmount = priceInquiryService.checkPrice(tapRecord.getStopId(), STOP_MAX);
    return new Trip(
            tapRecord.getDateTimeUtc(),
            null,
            tapRecord.getStopId(),
            null,
            chargeAmount,
            tapRecord.getCompanyId(),
            tapRecord.getBusId(),
            tapRecord.getPan(),
            TripStatus.INCOMPLETE);
  }

  private Trip processIncompleteTripOnlyTapOff(final TapRecord tapRecord) {
    final BigDecimal chargeAmount = priceInquiryService.checkPrice(tapRecord.getStopId(), STOP_MAX);
    return new Trip(
            null,
            tapRecord.getDateTimeUtc(),
            null,
            tapRecord.getStopId(),
            chargeAmount,
            tapRecord.getCompanyId(),
            tapRecord.getBusId(),
            tapRecord.getPan(),
            TripStatus.INCOMPLETE);
  }

  private Trip processCancelledTrip(final TapRecord previousRecord, final TapRecord currentRecord) {
    return new Trip(
            previousRecord.getDateTimeUtc(),
            currentRecord.getDateTimeUtc(),
            previousRecord.getStopId(),
            currentRecord.getStopId(),
            BigDecimal.ZERO,
            previousRecord.getCompanyId(),
            previousRecord.getBusId(),
            previousRecord.getPan(),
            TripStatus.CANCELLED);
  }
}
