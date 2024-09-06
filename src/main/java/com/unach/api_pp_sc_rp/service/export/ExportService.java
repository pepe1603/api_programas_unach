package com.unach.api_pp_sc_rp.service.export;

import org.springframework.core.io.Resource;

import java.util.List;

public interface ExportService {
    Resource exportToCSV(List<String> headers, List<List<String>> data);

    Resource exportToPDF(String title, List<String> headers, List<List<String>> data);
}
