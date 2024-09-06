package com.unach.api_pp_sc_rp.service.export.csv;

import com.unach.api_pp_sc_rp.exception.ExportException;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;

@Service
public class CsvExportServiceImpl implements CsvExportService{


    private static final Logger logger = LoggerFactory.getLogger(CsvExportServiceImpl.class);

    @Override
    public Resource exportToCSV(List<String> headers, List<List<String>> data) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             OutputStreamWriter writer = new OutputStreamWriter(out);
             CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader(headers.toArray(new String[0])))) {

            for (List<String> rowData : data) {
                csvPrinter.printRecord(rowData);
            }
            csvPrinter.flush();//CsvPrinter se vacie compatmetne

            ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
            return new InputStreamResource(in);

        } catch (IOException e) {
            logger.error("Error al generar el archivo CSV", e);
            throw new ExportException("Error al generar el archivo CSV", e);
        }
    }
}
