package com.little.pay.services;

import com.little.pay.entities.TapRecord;
import com.little.pay.entities.TapType;
import com.little.pay.entities.Trip;
import com.little.pay.exceptions.LittlePayException;
import com.little.pay.exceptions.LittlePayFileNotFoundException;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.*;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class CsvServiceImpl implements CsvService {
  private static final String[] HEADER = {"Started", "Finished", "DurationSecs", "FromStopId", "ToStopId", "ChargeAmount", "CompanyId", "BusId", "PAN", "Status"};
  private static final DateTimeFormatter FORMATTER =
          new DateTimeFormatterBuilder()
                  .appendPattern("dd-MM-yyyy HH:mm:ss")
                  .parseDefaulting(ChronoField.OFFSET_SECONDS, 0)
                  .toFormatter();

  @Override
  public List<TapRecord> readCsv(final String fileName) {
    final List<TapRecord> tapRecordList = new ArrayList<>();
    try (final CSVReader reader = new CSVReader(new FileReader(getFile(fileName)))) {
      reader.skip(1); // skip the header
      while (reader.peek() != null) {
        final String[] row = reader.readNext();
        final TapRecord tapRecord =
                new TapRecord(
                        Integer.parseInt(row[0].strip()),
                        convertDateTime(row[1].strip()),
                        TapType.valueOf(row[2].strip()),
                        row[3].strip(),
                        row[4].strip(),
                        row[5].strip(),
                        row[6].strip());
        tapRecordList.add(tapRecord);
      }
    } catch (Exception ex) {
      throw new LittlePayException("Failed to process tap record csv file.", ex);
    }
    return tapRecordList;
  }

  public void writeCsv(final List<Trip> tripList, final String fileName) {
    final File file = getFile(fileName);

    try (final CSVWriter writer = new CSVWriter(new FileWriter(file),
            CSVWriter.DEFAULT_SEPARATOR,
            CSVWriter.NO_QUOTE_CHARACTER,
            CSVWriter.DEFAULT_ESCAPE_CHARACTER,
            CSVWriter.DEFAULT_LINE_END)) {
      writer.writeNext(processRow(HEADER));
      tripList.stream().map(this::toCsvRow).forEach(tripRow -> writer.writeNext(processRow(tripRow)));
    }
    catch (final Exception ex) {
      throw new LittlePayException("Failed to write content into csv.", ex);
    }
  }

  private String[] toCsvRow(final Trip trip) {
    return new String[] {
            trip.getStarted().map(started -> started.format(FORMATTER)).orElse("null"),
            trip.getFinished().map(finished -> finished.format(FORMATTER)).orElse("null"),
            String.valueOf(trip.getDurationSecs()),
            trip.getFromStopId().orElse("null"),
            trip.getToStopId().orElse("null"),
            trip.getChargeAmount().setScale(2, RoundingMode.HALF_UP).toString(),
            trip.getCompanyId(),
            trip.getBusId(),
            trip.getPan(),
            trip.getTripStatus().toString()
    };
  }

  private String[] processRow(final String[] row) {
    final String[] processedRow = new String[row.length];
    for (int i = 0; i < row.length; i++) {
      processedRow[i] = i == 0 ? row[i] : StringUtils.SPACE + row[i];
    }
    return processedRow;
  }

  private File getFile(final String fileName) {
    final File file = new File(fileName);
    if (file.exists()) {
      return file;
    } else {
      throw new LittlePayFileNotFoundException("File not found for " + fileName);
    }
  }

  private OffsetDateTime convertDateTime(final String dateTimeStr) {
    return OffsetDateTime.parse(dateTimeStr, FORMATTER).withOffsetSameInstant(ZoneOffset.UTC);
  }
}
