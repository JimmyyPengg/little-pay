package com.little.pay.services;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;

@Component
public class PriceInquiryServiceImpl implements PriceInquiryService {
  private final Map<String, BigDecimal> price;

  public PriceInquiryServiceImpl() {
    price = Map.of(
            "Stop1-Stop2", new BigDecimal(3.25),
            "Stop1-Stop3", new BigDecimal(7.30),
            "Stop2-Stop3", new BigDecimal(5.50),
            "Stop1-StopMax", new BigDecimal(7.30),
            "Stop2-StopMax", new BigDecimal(5.50),
            "Stop3-StopMax", new BigDecimal(7.30));
  }

  @Override
  public BigDecimal checkPrice(final String beginStop, final String endStop) {
    final String tripKey = beginStop.compareTo(endStop) < 0 ? (beginStop + "-" + endStop) : (endStop + "-" + beginStop);
    return price.get(tripKey);
  }
}
