package com.szxx.util;

import com.huaban.analysis.jieba.JiebaSegmenter;
import com.huaban.analysis.jieba.SegToken;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class JiebaUtil {

    private final JiebaSegmenter segmenter;

    private static final Set<String> STOP_WORDS = new HashSet<>(Arrays.asList(
            "的", "了", "是", "在", "和", "与", "等", "及", "或", "之", "为", "以",
            "就", "也", "还", "而", "但", "却", "又", "再", "才", "都", "很", "更",
            "最", "非常", "可以", "可能", "已经", "因为", "所以", "如果", "虽然",
            "而且", "另外", "包括", "例如", "较", "个", "所", "要"
    ));

    public JiebaUtil() {
        this.segmenter = new JiebaSegmenter();
    }

    public List<String> segment(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return List.of();
        }
        return segmenter.process(keyword, JiebaSegmenter.SegMode.SEARCH).stream()
                .map(token -> token.word)
                .filter(w -> w.length() > 1)
                .filter(w -> !STOP_WORDS.contains(w))
                .distinct()
                .collect(Collectors.toList());
    }
}
