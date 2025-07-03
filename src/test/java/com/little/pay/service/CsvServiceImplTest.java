package com.little.pay.service;

import com.little.pay.entities.TapRecord;
import com.little.pay.entities.TapType;
import com.little.pay.entities.Trip;
import com.little.pay.entities.TripStatus;
import com.little.pay.services.CsvService;
import com.little.pay.services.CsvServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CsvServiceImplTest {
  private static final String TEST_INPUT_FILE_NAME = "src/test/resources/test-input.csv";
  private static final String TEST_OUTPUT_FILE_NAME = "src/test/resources/test-output.csv";
  private static final DateTimeFormatter FORMATTER =
          new DateTimeFormatterBuilder()
                  .appendPattern("dd-MM-yyyy HH:mm:ss")
                  .parseDefaulting(ChronoField.OFFSET_SECONDS, 0)
                  .toFormatter();

  private CsvService csvService;

  @BeforeEach
  public void setup() {
    csvService = new CsvServiceImpl();
  }


  @Test
  public void testReadCsv() {
    final List<TapRecord> result = csvService.readCsv(TEST_INPUT_FILE_NAME);
    assertEquals(result.size(), 2);

    final TapRecord tapOnRecord = result.get(0);
    assertEquals(tapOnRecord.getId(), 1);
    assertEquals(tapOnRecord.getDateTimeUtc().format(FORMATTER), "22-01-2023 13:00:00");
    assertEquals(tapOnRecord.getTapType(), TapType.ON);
    assertEquals(tapOnRecord.getStopId(), "Stop1");
    assertEquals(tapOnRecord.getCompanyId(), "Company1");
    assertEquals(tapOnRecord.getBusId(), "Bus37");
    assertEquals(tapOnRecord.getPan(), "5500005555555559");

    final TapRecord tapOffRecord = result.get(1);
    assertEquals(tapOffRecord.getId(), 2);
    assertEquals(tapOffRecord.getDateTimeUtc().format(FORMATTER), "22-01-2023 13:05:00");
    assertEquals(tapOffRecord.getTapType(), TapType.OFF);
    assertEquals(tapOffRecord.getStopId(), "Stop2");
    assertEquals(tapOffRecord.getCompanyId(), "Company1");
    assertEquals(tapOffRecord.getBusId(), "Bus37");
    assertEquals(tapOffRecord.getPan(), "5500005555555559");
  }

  @Test
  public void testWriteCsv() throws IOException {
    final OffsetDateTime started = OffsetDateTime.now();
    final OffsetDateTime finished = OffsetDateTime.now().plusMinutes(10);
    final Trip targetTrip = createTrip(started, finished);

    csvService.writeCsv(List.of(targetTrip), TEST_OUTPUT_FILE_NAME);

    final String expected = "Started, Finished, DurationSecs, FromStopId, ToStopId, ChargeAmount, CompanyId, BusId, PAN, Status\n" +
            started.format(FORMATTER) + ", " + finished.format(FORMATTER) + ", 600, Stop000, Stop111, 10.00, CompanyAAA, Bus110, 5500005555551239, COMPLETED\n";
    final String outputResult = Files.readString(Path.of(TEST_OUTPUT_FILE_NAME));
    assertEquals(outputResult, expected);
  }

  private Trip createTrip(final OffsetDateTime started, final OffsetDateTime finished) {
    return new Trip(
            started,
            finished,
            "Stop000",
            "Stop111",
            BigDecimal.TEN,
            "CompanyAAA",
            "Bus110",
            "5500005555551239",
            TripStatus.COMPLETED);
  }
}
