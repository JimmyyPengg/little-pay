package com.little.pay;

import com.little.pay.config.AppConfig;
import com.little.pay.entities.TapRecord;
import com.little.pay.entities.Trip;
import com.little.pay.services.CsvService;
import com.little.pay.services.CsvServiceImpl;
import com.little.pay.services.TripService;
import com.little.pay.services.TripServiceImpl;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

public class Main {
  public static void main(String[] args) {
    Instant start = Instant.now();
    final String inputFile = args.length == 2 ? args[0] : "src/main/resources/input.csv";
    final String outputFile = args.length == 2 ? args[1] : "src/main/resources/output.csv";

    final ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
    final CsvService csvService = context.getBean(CsvServiceImpl.class);
    final TripService tripService = context.getBean(TripServiceImpl.class);

    final List<TapRecord> tapRecordList = csvService.readCsv(inputFile);
    final List<Trip> tripList = tripService.process(tapRecordList);
    csvService.writeCsv(tripList, outputFile);
    Instant end = Instant.now();
    System.out.println(String.format("Finish processing within: %d ms", Duration.between(start, end).getNano()/1000000));
  }
}