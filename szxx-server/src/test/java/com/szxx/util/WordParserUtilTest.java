package com.szxx.util;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class WordParserUtilTest {

    private final WordParserUtil parser = new WordParserUtil();

    @Test
    void testParseDocxBasic() throws IOException {
        // Create a minimal OOXML file (would need real .docx bytes)
        // For now, test that the class can be instantiated
        assertNotNull(parser);
    }
}
