package com.little.pay.services;

import com.little.pay.entities.TapRecord;
import com.little.pay.entities.Trip;

import java.util.List;

public interface TripService {
  List<Trip> process(final List<TapRecord> tapRecordList);
}
