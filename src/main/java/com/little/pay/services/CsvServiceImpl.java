package com.little.pay.services;

import com.little.pay.entities.TapRecord;
import com.little.pay.entities.TapType;
import com.little.pay.entities.Trip;
import com.little.pay.exceptions.LittlePayException;
import com.little.pay.exceptions.LittlePayFileNotFoundException;
import com.opencsv.CSVReader;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.*;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.List;

@Component
public class CsvServiceImpl implements CsvService {
  private static final DateTimeFormatter formatter =
          new DateTimeFormatterBuilder()
                  .appendPattern("dd-MM-yyyy HH:mm:ss")
                  .parseDefaulting(ChronoField.OFFSET_SECONDS, 0)
                  .toFormatter();

  @Override
  public List<TapRecord> readCsv(final String fileName) {
    final List<TapRecord> tapRecordList = new ArrayList<>();
    try (CSVReader reader = new CSVReader(new FileReader(getFile(fileName)))) {
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

  }


  private File getFile(final String fileName) {
    try {
      final ClassPathResource resource = new ClassPathResource(fileName);
      if (resource.exists()) {
        return resource.getFile();
      } else {
        final File file = new File(fileName);
        if (file.exists()) {
          return file;
        } else {
          throw new LittlePayFileNotFoundException("File not found for " + fileName);
        }
      }
    } catch (final IOException ioException) {
      throw new LittlePayException("Failed to read file from " + fileName, ioException);
    }
  }

  private OffsetDateTime convertDateTime(final String dateTimeStr) {
    return OffsetDateTime.parse(dateTimeStr, formatter).withOffsetSameInstant(ZoneOffset.UTC);
  }
}
