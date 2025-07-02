package com.little.pay.services;

import com.little.pay.entities.TapRecord;
import com.little.pay.entities.Trip;

import java.util.List;

public interface CsvService {
  List<TapRecord> readCsv(final String fileName);

  void writeCsv(final List<Trip> tripList, final String fileName);
}
