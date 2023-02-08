package org.nosov.fincost;

import com.opencsv.*;
import com.opencsv.exceptions.CsvException;

import java.io.*;
import java.util.List;

import static org.nosov.fincost.Starter.property;

public class CSV {
    String csvLocation = property.getProperty("csv.location");

    public void writeToCsv(String msg) {
        Writer writer = new StringWriter();

        ICSVWriter csvWriter = new CSVWriterBuilder(writer)
                .withSeparator(';')
                .withQuoteChar(ICSVParser.DEFAULT_QUOTE_CHARACTER)
                .withEscapeChar(ICSVParser.DEFAULT_ESCAPE_CHARACTER)
                .withLineEnd(ICSVWriter.DEFAULT_LINE_END)
                .build();

        String[] record = msg.split(";");
        csvWriter.writeNext(record);
        try (FileWriter fileWriter = new FileWriter(csvLocation, true)) {
            File file = new File(csvLocation);
            file.createNewFile();
            fileWriter.write(writer.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<String[]> readCSVFile() {
        final CSVParser parser = getCsvParser();
        List<String[]> allRows = null;
        try (final CSVReader csvReader = getCsvReader(parser)) {
            allRows = csvReader.readAll();
        } catch (IOException | CsvException e) {
            e.printStackTrace();
        }
        return allRows;
    }

    private static CSVParser getCsvParser() {
        return new CSVParserBuilder()
                .withSeparator(';')
                .withIgnoreQuotations(true)
                .build();
    }

    private CSVReader getCsvReader(CSVParser parser) {
        return new CSVReaderBuilder(new StringReader(csvLocation))
                .withSkipLines(1)
                .withCSVParser(parser)
                .build();
    }

}
