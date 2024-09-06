package com.unach.api_pp_sc_rp.service.export.pdf;

import com.unach.api_pp_sc_rp.exception.ExportException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
public class PdfExportServiceImpl implements PdfExportService {

    private static final Logger logger = LoggerFactory.getLogger(PdfExportServiceImpl.class);

    @Override
    public Resource exportToPDF(String title, List<String> headers, List<List<String>> data) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            PDDocument document = new PDDocument();
            PDPage page = new PDPage();
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                // Set up font
                PDType1Font font = PDType1Font.HELVETICA_BOLD;

                // Title
                contentStream.beginText();
                contentStream.setFont(font, 18);
                contentStream.newLineAtOffset(50, 750);
                contentStream.showText(title);
                contentStream.endText();

                // Table
                float yStart = 700f;
                float tableWidth = 500f;
                float yPosition = yStart;
                float rowHeight = 15f;
                float tableHeight = rowHeight * (headers.size() + data.size() + 1);

                // Draw table
                contentStream.setFont(font, 12);

                // Header
                contentStream.beginText();
                contentStream.newLineAtOffset(50, yPosition);
                for (String header : headers) {
                    contentStream.showText(header + " ");
                }
                contentStream.endText();
                yPosition -= rowHeight;

                // Rows
                for (List<String> row : data) {
                    contentStream.beginText();
                    contentStream.newLineAtOffset(50, yPosition);
                    for (String cell : row) {
                        contentStream.showText(cell + " ");
                    }
                    contentStream.endText();
                    yPosition -= rowHeight;
                }
            }

            document.save(out);
            document.close();

            ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
            return new InputStreamResource(in);

        } catch (IOException e) {
            logger.error("Error al generar el archivo PDF", e);
            throw new ExportException("Error al generar el archivo PDF", e);
        }
    }
}
