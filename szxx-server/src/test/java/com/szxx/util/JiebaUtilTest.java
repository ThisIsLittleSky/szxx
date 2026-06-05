package com.szxx.util;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JiebaUtilTest {

    private final JiebaUtil jiebaUtil = new JiebaUtil();

    @Test
    void testSegmentChinese() {
        List<String> result = jiebaUtil.segment("儒家思想");
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void testSegmentEmpty() {
        List<String> result = jiebaUtil.segment("");
        assertTrue(result.isEmpty());
    }

    @Test
    void testSegmentNull() {
        List<String> result = jiebaUtil.segment(null);
        assertTrue(result.isEmpty());
    }

    @Test
    void testFilterStopWords() {
        List<String> result = jiebaUtil.segment("的了的和");
        // Stop words should be filtered out
        for (String word : result) {
            assertFalse(word.equals("的"), "Stop word '的' should be filtered");
        }
    }
}
