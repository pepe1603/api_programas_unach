package com.unach.api_pp_sc_rp.service.export.csv;

import org.springframework.core.io.Resource;

import java.util.List;

public interface CsvExportService {
    Resource exportToCSV(List<String> headers, List<List<String>> data);
}
