package com.szxx.util;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Component
public class WordParserUtil {

    public ParsedDocument parseDocx(InputStream inputStream) throws IOException {
        try (XWPFDocument doc = new XWPFDocument(inputStream)) {
            List<XWPFParagraph> paragraphs = doc.getParagraphs();
            List<String> lines = new ArrayList<>();

            for (XWPFParagraph p : paragraphs) {
                String text = p.getText();
                if (text != null && !text.trim().isEmpty()) {
                    lines.add(text.trim());
                }
            }

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
