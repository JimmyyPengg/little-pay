package com.little.pay.services;

import com.little.pay.entities.TapRecord;
import com.little.pay.entities.Trip;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TripServiceImpl implements TripService {

  @Override
  public List<Trip> process(final List<TapRecord> tapRecordList) {
    return List.of();
  }
}
