package com.little.pay.entities;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Optional;

public class Trip {
  private Optional<OffsetDateTime> started;
  private Optional<OffsetDateTime> finished;
  private Optional<String> fromStopId;
  private Optional<String> toStopId;
  private BigDecimal chargeAmount;
  private String companyId;
  private String busId;
  private String pan;
  private TripStatus tripStatus;

  public Trip(final OffsetDateTime started, final OffsetDateTime finished, final String fromStopId, final String toStopId, final BigDecimal chargeAmount, final String companyId, final String busId, final String pan, final TripStatus tripStatus) {
    this.started = Optional.ofNullable(started);
    this.finished = Optional.ofNullable(finished);
    this.fromStopId = Optional.ofNullable(fromStopId);
    this.toStopId = Optional.ofNullable(toStopId);
    this.chargeAmount = chargeAmount;
    this.companyId = companyId;
    this.busId = busId;
    this.pan = pan;
    this.tripStatus = tripStatus;
  }

  public Optional<OffsetDateTime> getStarted() {
    return started;
  }

  public Optional<OffsetDateTime> getFinished() {
    return finished;
  }

  public Long getDurationSecs() {
    if (started.isPresent() && finished.isPresent()) {
      return Duration.between(started.get(), finished.get()).getSeconds();
    } else {
      return null;
    }
  }

  public Optional<String> getFromStopId() {
    return fromStopId;
  }

  public Optional<String> getToStopId() {
    return toStopId;
  }

  public BigDecimal getChargeAmount() {
    return chargeAmount;
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

  public TripStatus getTripStatus() {
    return tripStatus;
  }
}
