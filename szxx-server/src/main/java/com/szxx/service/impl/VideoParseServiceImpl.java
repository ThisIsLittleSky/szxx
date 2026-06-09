package com.szxx.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.szxx.common.exception.BusinessException;
import com.szxx.dto.response.VideoParseResponse;
import com.szxx.service.VideoParseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class VideoParseServiceImpl implements VideoParseService {

    private final RestTemplate restTemplate;
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static final Pattern BV_PATTERN = Pattern.compile("BV[0-9A-Za-z]{10}");
    private static final Pattern BVID_PARAM_PATTERN = Pattern.compile("[?&]bvid=(BV[0-9A-Za-z]{10})");
    private static final String BILIBILI_API = "https://api.bilibili.com/x/web-interface/view?bvid=";

    @Override
    public VideoParseResponse parseVideoUrl(String url) {
        String bvid = extractBvid(url);
        if (bvid == null) {
            throw BusinessException.badRequest("无法识别视频链接中的BV号，请确认链接格式正确");
        }

        try {
            String apiUrl = BILIBILI_API + bvid;
            HttpHeaders headers = new HttpHeaders();
            headers.set("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36");
            headers.set("Referer", "https://www.bilibili.com/");
            HttpEntity<Void> entity = new HttpEntity<>(headers);
            String json = restTemplate.exchange(apiUrl, HttpMethod.GET, entity, String.class).getBody();
            JsonNode root = MAPPER.readTree(json);

            int code = root.path("code").asInt(-1);
            if (code != 0) {
                String msg = root.path("message").asText("未知错误");
                throw BusinessException.badRequest("获取视频信息失败：" + msg);
            }

            JsonNode data = root.path("data");
            String title = data.path("title").asText("");
            String desc = data.path("desc").asText("");
            String coverUrl = data.path("pic").asText("");
            if (!coverUrl.isEmpty()) {
                // B站可能返回 HTTP 或协议相对 URL，统一转为 HTTPS
                if (coverUrl.startsWith("http://")) {
                    coverUrl = coverUrl.replaceFirst("^http://", "https://");
                } else if (coverUrl.startsWith("//")) {
                    coverUrl = "https:" + coverUrl;
                }
            }

            if (title.isEmpty()) {
                throw BusinessException.badRequest("获取视频信息失败：未找到标题");
            }

            return new VideoParseResponse(title, desc, coverUrl);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw BusinessException.badRequest("解析视频链接失败，请检查链接是否正确或稍后重试");
        }
    }

    private String extractBvid(String url) {
        // Try direct BV号 match in the URL
        Matcher m = BV_PATTERN.matcher(url);
        if (m.find()) {
            return m.group();
        }
        // Try bvid parameter (e.g., in iframe src)
        m = BVID_PARAM_PATTERN.matcher(url);
        if (m.find()) {
            return m.group(1);
        }
        return null;
    }
}
