package com.little.pay.entities;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class TapRecord {
  private int id;
  private OffsetDateTime dateTimeUtc;
  private TapType tapType;
  private String stopId;
  private String companyId;
  private String busId;
  private String pan;

  public TapRecord(final int id, final OffsetDateTime dateTimeUtc, final TapType tapType, final String stopId, final String companyId, final String busId, final String pan) {
    this.id = id;
    this.dateTimeUtc = dateTimeUtc;
    this.tapType = tapType;
    this.stopId = stopId;
    this.companyId = companyId;
    this.busId = busId;
    this.pan = pan;
  }

  public int getId() {
    return id;
  }

  public OffsetDateTime getDateTimeUtc() {
    return dateTimeUtc;
  }

  public TapType getTapType() {
    return tapType;
  }

  public String getStopId() {
    return stopId;
  }

  public String getCompanyId() {
    return companyId;
  }

  public String getBusId() {
    return busId;
  }

  public String getPan() {
    return pan;
  }
}
