package com.szxx.service.impl;

import com.szxx.common.exception.BusinessException;
import com.szxx.entity.Material;
import com.szxx.mapper.MaterialMapper;
import com.szxx.service.ExportService;
import lombok.RequiredArgsConstructor;
import org.apache.poi.xwpf.usermodel.*;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExportServiceImpl implements ExportService {

    private final MaterialMapper materialMapper;

    @Override
    public byte[] exportWord(List<Long> materialIds, String exportTitle) {
        List<Material> materials = materialMapper.selectBatchIds(materialIds);
        if (materials.isEmpty()) {
            throw BusinessException.badRequest("没有找到要导出的素材");
        }

        try (XWPFDocument doc = new XWPFDocument();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            // title page
            XWPFParagraph titlePara = doc.createParagraph();
            titlePara.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun titleRun = titlePara.createRun();
            titleRun.setText(exportTitle);
            titleRun.setBold(true);
            titleRun.setFontSize(24);

            // separator
            XWPFParagraph sep = doc.createParagraph();
            sep.createRun().addBreak();

            for (Material m : materials) {
                // material title
                XWPFParagraph matTitle = doc.createParagraph();
                XWPFRun matRun = matTitle.createRun();
                matRun.setText(m.getTitle());
                matRun.setBold(true);
                matRun.setFontSize(18);

                // metadata
                XWPFParagraph meta = doc.createParagraph();
                XWPFRun metaRun = meta.createRun();
                metaRun.setText("作者: " + (m.getAuthor() != null ? m.getAuthor() : "未知")
                        + "  朝代: " + m.getDynasty()
                        + "  分类: " + m.getCategory());
                metaRun.setFontSize(11);
                metaRun.setColor("666666");

                // content (strip HTML)
                XWPFParagraph contentPara = doc.createParagraph();
                XWPFRun contentRun = contentPara.createRun();
                String content = m.getContent() != null
                        ? m.getContent().replaceAll("<[^>]+>", "\n").trim() : "";
                contentRun.setText(content);
                contentRun.setFontSize(12);

                // page break between materials
                XWPFParagraph breakPara = doc.createParagraph();
                breakPara.setPageBreak(true);

                if (!materials.get(materials.size() - 1).getId().equals(m.getId())) {
                    doc.createParagraph();
                }
            }

            doc.write(out);
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("导出失败: " + e.getMessage(), e);
        }
    }
}
