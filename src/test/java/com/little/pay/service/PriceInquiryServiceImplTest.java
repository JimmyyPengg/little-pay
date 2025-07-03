package com.little.pay.service;

import com.little.pay.services.PriceInquiryService;
import com.little.pay.services.PriceInquiryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PriceInquiryServiceImplTest {
  private PriceInquiryService priceInquiryService;

  @BeforeEach
  public void setup() {
    this.priceInquiryService = new PriceInquiryServiceImpl();
  }

  private static Stream<Arguments> getPriceParameters() {
    return Stream.of(
            Arguments.of("Stop1", "Stop2", new BigDecimal(3.25)),
            Arguments.of("Stop2", "Stop1", new BigDecimal(3.25)),

            Arguments.of("Stop1", "Stop3", new BigDecimal(7.30)),
            Arguments.of("Stop3", "Stop1", new BigDecimal(7.30)),

            Arguments.of("Stop2", "Stop3", new BigDecimal(5.50)),
            Arguments.of("Stop3", "Stop2", new BigDecimal(5.50)),

            Arguments.of("Stop1", "StopMax", new BigDecimal(7.30)),
            Arguments.of("StopMax", "Stop1", new BigDecimal(7.30)),

            Arguments.of("Stop2", "StopMax", new BigDecimal(5.50)),
            Arguments.of("StopMax", "Stop2", new BigDecimal(5.50)),

            Arguments.of("Stop3", "StopMax", new BigDecimal(7.30)),
            Arguments.of("StopMax", "Stop3", new BigDecimal(7.30))
    );
  }

  @ParameterizedTest
  @MethodSource("getPriceParameters")
  public void testCheckPriceStop1ToStop2(final String fromStop, final String toStop, final BigDecimal expectedPrice) {
    final BigDecimal price = priceInquiryService.checkPrice(fromStop, toStop);
    assertEquals(expectedPrice, price);
  }
}
