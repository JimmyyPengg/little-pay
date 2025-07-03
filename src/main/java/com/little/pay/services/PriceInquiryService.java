package com.little.pay.services;

import java.math.BigDecimal;

public interface PriceInquiryService {
  BigDecimal checkPrice(String beginStop, String endStop);
}
