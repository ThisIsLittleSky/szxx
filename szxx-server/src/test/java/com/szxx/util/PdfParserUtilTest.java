package com.szxx.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PdfParserUtilTest {

    private final PdfParserUtil parser = new PdfParserUtil();

    @Test
    void testParsePdfInstantiation() {
        assertNotNull(parser);
    }
}
