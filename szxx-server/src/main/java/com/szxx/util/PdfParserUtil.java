package com.szxx.util;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class PdfParserUtil {

    public ParsedDocument parsePdf(InputStream inputStream) throws IOException {
        try (PDDocument doc = Loader.loadPDF(inputStream.readAllBytes())) {
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(doc);

            List<String> lines = Arrays.stream(text.split("\n"))
                    .map(String::trim)
                    .filter(l -> !l.isEmpty())
                    .collect(Collectors.toList());

            String title = lines.isEmpty() ? "未命名素材" : lines.get(0);
            if (title.length() > 200) {
                title = title.substring(0, 200);
            }

            StringBuilder contentBuilder = new StringBuilder();
            for (String line : lines) {
                contentBuilder.append("<p>").append(line).append("</p>\n");
            }

            return new ParsedDocument(title, contentBuilder.toString(), new ArrayList<>());
        }
    }
}
