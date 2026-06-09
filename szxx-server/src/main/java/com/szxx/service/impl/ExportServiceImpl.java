package com.szxx.service.impl;

import com.szxx.common.exception.BusinessException;
import com.szxx.entity.Material;
import com.szxx.entity.User;
import com.szxx.mapper.MaterialMapper;
import com.szxx.mapper.UserMapper;
import com.szxx.security.SecurityContextUtil;
import com.szxx.service.ExportService;
import lombok.RequiredArgsConstructor;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xwpf.usermodel.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
@RequiredArgsConstructor
public class ExportServiceImpl implements ExportService {

    private final MaterialMapper materialMapper;
    private final UserMapper userMapper;

    @Value("${app.upload.path}")
    private String uploadPath;

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

    @Override
    public byte[] exportPpt(Long materialId) {
        Material material = materialMapper.selectById(materialId);
        if (material == null) {
            throw BusinessException.notFound("素材不存在");
        }
        if (material.getVideoUrl() == null || material.getVideoUrl().isBlank()) {
            throw BusinessException.badRequest("该素材没有视频链接，无法导出PPT");
        }

        Long userId = SecurityContextUtil.getCurrentUserId();
        User user = userMapper.selectById(userId);
        String nickname = user != null ? user.getNickname() : "未知用户";
        String summary = material.getSummary() != null && !material.getSummary().isBlank()
                ? material.getSummary() : "暂无描述";

        // Clean video URL: extract real URL from iframe embed code if needed
        String videoUrl = material.getVideoUrl();
        java.util.regex.Matcher iframeMatcher = java.util.regex.Pattern
                .compile("<iframe[^>]+src=[\"']([^\"']+)[\"'][^>]*>", java.util.regex.Pattern.CASE_INSENSITIVE)
                .matcher(videoUrl);
        if (iframeMatcher.find()) {
            videoUrl = iframeMatcher.group(1);
            if (videoUrl.startsWith("//")) videoUrl = "https:" + videoUrl;
        }

        byte[] coverBytes = loadCoverImage(material.getCoverImage());
        String coverExt = (coverBytes != null) ? inferExt(coverBytes) : null;

        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ZipOutputStream zos = new ZipOutputStream(bos)) {

            // -- [Content_Types].xml --
            String contentTypes = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
                    + "<Types xmlns=\"http://schemas.openxmlformats.org/package/2006/content-types\">"
                    + "<Default Extension=\"rels\" ContentType=\"application/vnd.openxmlformats-package.relationships+xml\"/>"
                    + "<Default Extension=\"xml\" ContentType=\"application/xml\"/>"
                    + "<Override PartName=\"/ppt/presentation.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.presentationml.presentation.main+xml\"/>"
                    + "<Override PartName=\"/ppt/slideMasters/slideMaster1.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.presentationml.slideMaster+xml\"/>"
                    + "<Override PartName=\"/ppt/slideLayouts/slideLayout1.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.presentationml.slideLayout+xml\"/>"
                    + "<Override PartName=\"/ppt/slides/slide1.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.presentationml.slide+xml\"/>"
                    + "<Override PartName=\"/ppt/theme/theme1.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.theme+xml\"/>";
            if (coverBytes != null) {
                contentTypes += "<Override PartName=\"/ppt/media/cover." + coverExt + "\" ContentType=\"image/" + (coverExt.equals("jpg") ? "jpeg" : coverExt) + "\"/>";
            }
            contentTypes += "</Types>";
            addZipEntry(zos, "[Content_Types].xml", contentTypes);

            // -- _rels/.rels --
            addZipEntry(zos, "_rels/.rels",
                    "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
                            + "<Relationships xmlns=\"http://schemas.openxmlformats.org/package/2006/relationships\">"
                            + "<Relationship Id=\"rId1\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument\" Target=\"ppt/presentation.xml\"/>"
                            + "</Relationships>");

            // -- ppt/_rels/presentation.xml.rels --
            String presRels = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
                    + "<Relationships xmlns=\"http://schemas.openxmlformats.org/package/2006/relationships\">"
                    + "<Relationship Id=\"rId1\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/slide\" Target=\"slides/slide1.xml\"/>"
                    + "<Relationship Id=\"rId2\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/slideMaster\" Target=\"slideMasters/slideMaster1.xml\"/>"
                    + "<Relationship Id=\"rId3\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/theme\" Target=\"theme/theme1.xml\"/>"
                    + "</Relationships>";
            addZipEntry(zos, "ppt/_rels/presentation.xml.rels", presRels);

            // -- ppt/presentation.xml --
            addZipEntry(zos, "ppt/presentation.xml",
                    "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
                            + "<p:presentation xmlns:a=\"http://schemas.openxmlformats.org/drawingml/2006/main\""
                            + " xmlns:r=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships\""
                            + " xmlns:p=\"http://schemas.openxmlformats.org/presentationml/2006/main\""
                            + " embedTrueTypeFonts=\"0\" saveSubsetFonts=\"0\">"
                            + "<p:sldMasterIdLst><p:sldMasterId id=\"2147483648\" r:id=\"rId2\"/></p:sldMasterIdLst>"
                            + "<p:sldIdLst><p:sldId id=\"256\" r:id=\"rId1\"/></p:sldIdLst>"
                            + "<p:sldSz cx=\"12192000\" cy=\"6858000\"/>"
                            + "<p:notesSz cx=\"6858000\" cy=\"9144000\"/>"
                            + "</p:presentation>");

            // -- ppt/slideMasters/slideMaster1.xml --
            addZipEntry(zos, "ppt/slideMasters/slideMaster1.xml",
                    "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
                            + "<p:sldMaster xmlns:a=\"http://schemas.openxmlformats.org/drawingml/2006/main\""
                            + " xmlns:r=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships\""
                            + " xmlns:p=\"http://schemas.openxmlformats.org/presentationml/2006/main\">"
                            + "<p:cSld><p:bg><p:bgRef idx=\"1001\"><a:schemeClr val=\"bg1\"/></p:bgRef></p:bg>"
                            + "<p:spTree><p:nvGrpSpPr><p:cNvPr id=\"1\" name=\"\"/><p:cNvGrpSpPr/><p:nvPr/></p:nvGrpSpPr>"
                            + "<p:grpSpPr><a:xfrm><a:off x=\"0\" y=\"0\"/><a:ext cx=\"0\" cy=\"0\"/><a:chOff x=\"0\" y=\"0\"/><a:chExt cx=\"0\" cy=\"0\"/></a:xfrm></p:grpSpPr>"
                            + "</p:spTree></p:cSld>"
                            + "<p:sldLayoutIdLst><p:sldLayoutId id=\"2147483649\" r:id=\"rId1\"/></p:sldLayoutIdLst>"
                            + "</p:sldMaster>");

            // -- ppt/slideMasters/_rels/slideMaster1.xml.rels --
            addZipEntry(zos, "ppt/slideMasters/_rels/slideMaster1.xml.rels",
                    "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
                            + "<Relationships xmlns=\"http://schemas.openxmlformats.org/package/2006/relationships\">"
                            + "<Relationship Id=\"rId1\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/slideLayout\" Target=\"../slideLayouts/slideLayout1.xml\"/>"
                            + "<Relationship Id=\"rId2\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/theme\" Target=\"../theme/theme1.xml\"/>"
                            + "</Relationships>");

            // -- ppt/slideLayouts/slideLayout1.xml (blank) --
            addZipEntry(zos, "ppt/slideLayouts/slideLayout1.xml",
                    "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
                            + "<p:sldLayout xmlns:a=\"http://schemas.openxmlformats.org/drawingml/2006/main\""
                            + " xmlns:r=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships\""
                            + " xmlns:p=\"http://schemas.openxmlformats.org/presentationml/2006/main\""
                            + " type=\"blank\" preserve=\"1\">"
                            + "<p:cSld name=\"Blank\"><p:spTree><p:nvGrpSpPr><p:cNvPr id=\"1\" name=\"\"/><p:cNvGrpSpPr/><p:nvPr/></p:nvGrpSpPr>"
                            + "<p:grpSpPr><a:xfrm><a:off x=\"0\" y=\"0\"/><a:ext cx=\"0\" cy=\"0\"/><a:chOff x=\"0\" y=\"0\"/><a:chExt cx=\"0\" cy=\"0\"/></a:xfrm></p:grpSpPr>"
                            + "</p:spTree></p:cSld></p:sldLayout>");

            // -- ppt/slideLayouts/_rels/slideLayout1.xml.rels --
            addZipEntry(zos, "ppt/slideLayouts/_rels/slideLayout1.xml.rels",
                    "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
                            + "<Relationships xmlns=\"http://schemas.openxmlformats.org/package/2006/relationships\">"
                            + "<Relationship Id=\"rId1\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/slideMaster\" Target=\"../slideMasters/slideMaster1.xml\"/>"
                            + "</Relationships>");

            // -- ppt/theme/theme1.xml (minimal) --
            addZipEntry(zos, "ppt/theme/theme1.xml",
                    "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
                            + "<a:theme xmlns:a=\"http://schemas.openxmlformats.org/drawingml/2006/main\" name=\"Default\">"
                            + "<a:themeElements>"
                            + "<a:clrScheme name=\"Default\"><a:dk1><a:srgbClr val=\"000000\"/></a:dk1><a:lt1><a:srgbClr val=\"FFFFFF\"/></a:lt1>"
                            + "<a:dk2><a:srgbClr val=\"44546A\"/></a:dk2><a:lt2><a:srgbClr val=\"E7E6E6\"/></a:lt2>"
                            + "<a:accent1><a:srgbClr val=\"4472C4\"/></a:accent1><a:accent2><a:srgbClr val=\"ED7D31\"/></a:accent2>"
                            + "<a:accent3><a:srgbClr val=\"A5A5A5\"/></a:accent3><a:accent4><a:srgbClr val=\"FFC000\"/></a:accent4>"
                            + "<a:accent5><a:srgbClr val=\"5B9BD5\"/></a:accent5><a:accent6><a:srgbClr val=\"70AD47\"/></a:accent6>"
                            + "<a:hlink><a:srgbClr val=\"0563C1\"/></a:hlink><a:folHlink><a:srgbClr val=\"954F72\"/></a:folHlink>"
                            + "</a:clrScheme>"
                            + "<a:fontScheme name=\"Default\"><a:majorFont><a:latin typeface=\"Calibri\"/><a:ea typeface=\"\"/><a:cs typeface=\"\"/></a:majorFont>"
                            + "<a:minorFont><a:latin typeface=\"Calibri\"/><a:ea typeface=\"\"/><a:cs typeface=\"\"/></a:minorFont></a:fontScheme>"
                            + "<a:fmtScheme name=\"Default\"><a:fillStyleLst/><a:lnStyleLst/><a:effectStyleLst/><a:bgFillStyleLst/></a:fmtScheme>"
                            + "</a:themeElements></a:theme>");

            // -- ppt/slides/slide1.xml (the actual content) --
            String slideRelXml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
                    + "<Relationships xmlns=\"http://schemas.openxmlformats.org/package/2006/relationships\">"
                    + "<Relationship Id=\"rId1\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/slideLayout\" Target=\"../slideLayouts/slideLayout1.xml\"/>";

            StringBuilder slideXml = new StringBuilder();
            slideXml.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>");
            slideXml.append("<p:sld xmlns:a=\"http://schemas.openxmlformats.org/drawingml/2006/main\"");
            slideXml.append(" xmlns:r=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships\"");
            slideXml.append(" xmlns:p=\"http://schemas.openxmlformats.org/presentationml/2006/main\">");
            slideXml.append("<p:cSld><p:spTree>");
            slideXml.append("<p:nvGrpSpPr><p:cNvPr id=\"1\" name=\"\"/><p:cNvGrpSpPr/><p:nvPr/></p:nvGrpSpPr>");
            slideXml.append("<p:grpSpPr><a:xfrm><a:off x=\"0\" y=\"0\"/><a:ext cx=\"0\" cy=\"0\"/><a:chOff x=\"0\" y=\"0\"/><a:chExt cx=\"0\" cy=\"0\"/></a:xfrm></p:grpSpPr>");

            int shapeId = 2;

            if (coverBytes != null) {
                // Embed cover image
                addZipEntry(zos, "ppt/media/cover." + coverExt, coverBytes);
                slideRelXml += "<Relationship Id=\"rId2\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/image\" Target=\"../media/cover." + coverExt + "\"/>";
                // External hyperlink to video URL
                slideRelXml += "<Relationship Id=\"rId3\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/hyperlink\" Target=\""
                        + escapeXml(videoUrl) + "\" TargetMode=\"External\"/>";

                // Picture shape
                slideXml.append("<p:pic>");
                slideXml.append("<p:nvPicPr>");
                slideXml.append("<p:cNvPr id=\"").append(shapeId++).append("\" name=\"cover\">")
                        .append("<a:hlinkClick xmlns:r=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships\" r:id=\"rId3\"/>")
                        .append("</p:cNvPr>");
                slideXml.append("<p:cNvPicPr><a:picLocks noChangeAspect=\"1\"/></p:cNvPicPr>");
                slideXml.append("<p:nvPr/></p:nvPicPr>");
                slideXml.append("<p:blipFill><a:blip r:embed=\"rId2\"/>")
                        .append("<a:stretch><a:fillRect/></a:stretch></p:blipFill>");
                slideXml.append("<p:spPr><a:xfrm><a:off x=\"457200\" y=\"914400\"/>")
                        .append("<a:ext cx=\"5638800\" cy=\"5029200\"/></a:xfrm>")
                        .append("<a:prstGeom prst=\"rect\"><a:avLst/></a:prstGeom></p:spPr>");
                slideXml.append("</p:pic>");
            } else {
                // Placeholder rectangle
                slideXml.append("<p:sp>");
                slideXml.append("<p:nvSpPr><p:cNvPr id=\"").append(shapeId++).append("\" name=\"placeholder\"/>");
                slideXml.append("<p:cNvSpPr/><p:nvPr/></p:nvSpPr>");
                slideXml.append("<p:spPr><a:xfrm><a:off x=\"457200\" y=\"914400\"/>")
                        .append("<a:ext cx=\"5638800\" cy=\"5029200\"/></a:xfrm>")
                        .append("<a:prstGeom prst=\"rect\"><a:avLst/></a:prstGeom>")
                        .append("<a:solidFill><a:srgbClr val=\"F5F0E8\"/></a:solidFill>")
                        .append("<a:ln w=\"12700\"><a:solidFill><a:srgbClr val=\"CCCCCC\"/></a:solidFill></a:ln>")
                        .append("</p:spPr>");
                slideXml.append("<p:txBody><a:bodyPr anchor=\"ctr\"/>");
                slideXml.append("<a:p><a:pPr algn=\"ctr\"/><a:r><a:rPr sz=\"2000\">")
                        .append("<a:solidFill><a:srgbClr val=\"AAAAAA\"/></a:solidFill></a:rPr>")
                        .append("<a:t>").append(escapeXml("暂无封面图片")).append("</a:t></a:r></a:p>");
                slideXml.append("</p:txBody></p:sp>");
            }

            // Title text
            slideXml.append("<p:sp>");
            slideXml.append("<p:nvSpPr><p:cNvPr id=\"").append(shapeId++).append("\" name=\"title\"/>");
            slideXml.append("<p:cNvSpPr txBox=\"1\"/><p:nvPr/></p:nvSpPr>");
            slideXml.append("<p:spPr><a:xfrm><a:off x=\"6400800\" y=\"1371600\"/>")
                    .append("<a:ext cx=\"5334000\" cy=\"914400\"/></a:xfrm>")
                    .append("<a:prstGeom prst=\"rect\"><a:avLst/></a:prstGeom></p:spPr>");
            slideXml.append("<p:txBody><a:bodyPr wrap=\"square\"/>");
            slideXml.append("<a:p><a:pPr algn=\"l\"/><a:r><a:rPr sz=\"2800\" b=\"1\">")
                    .append("<a:solidFill><a:srgbClr val=\"1A1A1A\"/></a:solidFill></a:rPr>")
                    .append("<a:t>").append(escapeXml(material.getTitle())).append("</a:t></a:r></a:p>");
            slideXml.append("</p:txBody></p:sp>");

            // Summary text
            slideXml.append("<p:sp>");
            slideXml.append("<p:nvSpPr><p:cNvPr id=\"").append(shapeId++).append("\" name=\"summary\"/>");
            slideXml.append("<p:cNvSpPr txBox=\"1\"/><p:nvPr/></p:nvSpPr>");
            slideXml.append("<p:spPr><a:xfrm><a:off x=\"6400800\" y=\"2500000\"/>")
                    .append("<a:ext cx=\"5334000\" cy=\"1800000\"/></a:xfrm>")
                    .append("<a:prstGeom prst=\"rect\"><a:avLst/></a:prstGeom></p:spPr>");
            slideXml.append("<p:txBody><a:bodyPr wrap=\"square\"/>");
            slideXml.append("<a:p><a:pPr algn=\"l\"/><a:r><a:rPr sz=\"1600\">")
                    .append("<a:solidFill><a:srgbClr val=\"666666\"/></a:solidFill></a:rPr>")
                    .append("<a:t>").append(escapeXml(summary)).append("</a:t></a:r></a:p>");
            slideXml.append("</p:txBody></p:sp>");

            // Video URL
            slideXml.append("<p:sp>");
            slideXml.append("<p:nvSpPr><p:cNvPr id=\"").append(shapeId++).append("\" name=\"videourl\"/>");
            slideXml.append("<p:cNvSpPr txBox=\"1\"/><p:nvPr/></p:nvSpPr>");
            slideXml.append("<p:spPr><a:xfrm><a:off x=\"6400800\" y=\"4500000\"/>")
                    .append("<a:ext cx=\"5334000\" cy=\"400000\"/></a:xfrm>")
                    .append("<a:prstGeom prst=\"rect\"><a:avLst/></a:prstGeom></p:spPr>");
            slideXml.append("<p:txBody><a:bodyPr wrap=\"square\"/>");
            slideXml.append("<a:p><a:pPr algn=\"l\"/><a:r><a:rPr sz=\"1100\">")
                    .append("<a:solidFill><a:srgbClr val=\"0563C1\"/></a:solidFill>")
                    .append("<a:u sng=\"1\"/></a:rPr>")
                    .append("<a:t>").append(escapeXml(videoUrl)).append("</a:t></a:r></a:p>");
            slideXml.append("</p:txBody></p:sp>");

            // Watermark
            slideXml.append("<p:sp>");
            slideXml.append("<p:nvSpPr><p:cNvPr id=\"").append(shapeId++).append("\" name=\"watermark\"/>");
            slideXml.append("<p:cNvSpPr txBox=\"1\"/><p:nvPr/></p:nvSpPr>");
            slideXml.append("<p:spPr><a:xfrm><a:off x=\"457200\" y=\"6350000\"/>")
                    .append("<a:ext cx=\"11430000\" cy=\"300000\"/></a:xfrm>")
                    .append("<a:prstGeom prst=\"rect\"><a:avLst/></a:prstGeom></p:spPr>");
            slideXml.append("<p:txBody><a:bodyPr/>");
            slideXml.append("<a:p><a:pPr algn=\"r\"/><a:r><a:rPr sz=\"900\">")
                    .append("<a:solidFill><a:srgbClr val=\"AAAAAA\"/></a:solidFill></a:rPr>")
                    .append("<a:t>").append(escapeXml("导出者: " + nickname + " | 思政素材平台"))
                    .append("</a:t></a:r></a:p>");
            slideXml.append("</p:txBody></p:sp>");

            slideXml.append("</p:spTree></p:cSld></p:sld>");

            addZipEntry(zos, "ppt/slides/slide1.xml", slideXml.toString());

            // Close the relationships
            slideRelXml += "</Relationships>";
            addZipEntry(zos, "ppt/slides/_rels/slide1.xml.rels", slideRelXml);

            zos.finish();

            // Debug: write to file
            try (java.io.FileOutputStream fos = new java.io.FileOutputStream(
                    uploadPath + "/debug_export.pptx")) {
                fos.write(bos.toByteArray());
            } catch (Exception ignored) {}

            material.setDownloadCount(
                    (material.getDownloadCount() != null ? material.getDownloadCount() : 0) + 1);
            materialMapper.updateById(material);

            return bos.toByteArray();

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("PPT导出失败: " + e.getMessage(), e);
        }
    }

    @Override
    public String buildPptFilename(Long materialId) {
        Material material = materialMapper.selectById(materialId);
        String title = material != null ? material.getTitle() : "素材";
        Long userId = SecurityContextUtil.getCurrentUserId();
        User user = userMapper.selectById(userId);
        String nickname = user != null ? user.getNickname() : "unknown";

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String rawName = timestamp + "_" + title + "_" + nickname + ".pptx";

        try {
            return java.net.URLEncoder.encode(rawName, StandardCharsets.UTF_8)
                    .replace("+", "%20");
        } catch (Exception e) {
            return "export.pptx";
        }
    }

    private void addZipEntry(ZipOutputStream zos, String name, String content) throws Exception {
        zos.putNextEntry(new ZipEntry(name));
        zos.write(content.getBytes(StandardCharsets.UTF_8));
        zos.closeEntry();
    }

    private void addZipEntry(ZipOutputStream zos, String name, byte[] data) throws Exception {
        zos.putNextEntry(new ZipEntry(name));
        zos.write(data);
        zos.closeEntry();
    }

    private String inferExt(byte[] data) {
        if (data.length < 4) return "png";
        int b1 = data[0] & 0xFF, b2 = data[1] & 0xFF, b3 = data[2] & 0xFF, b4 = data[3] & 0xFF;
        if (b1 == 0xFF && b2 == 0xD8 && b3 == 0xFF) return "jpg";
        if (b1 == 0x89 && b2 == 0x50 && b3 == 0x4E && b4 == 0x47) return "png";
        if (b1 == 0x47 && b2 == 0x49 && b3 == 0x46) return "gif";
        return "png";
    }

    private String escapeXml(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
                .replace("\"", "&quot;").replace("'", "&apos;");
    }

    private byte[] loadCoverImage(String coverImage) {
        if (coverImage == null || coverImage.isBlank()) return null;
        try {
            if (coverImage.startsWith("http://") || coverImage.startsWith("https://")) {
                RestTemplate rest = new RestTemplate();
                byte[] data = rest.execute(URI.create(coverImage), HttpMethod.GET,
                        req -> req.getHeaders().setAccept(List.of(MediaType.IMAGE_JPEG, MediaType.IMAGE_PNG,
                                MediaType.parseMediaType("image/webp"), MediaType.ALL)),
                        response -> {
                            try (var is = response.getBody()) {
                                return IOUtils.toByteArray(is);
                            }
                        });
                // Skip webp
                if (data != null && data.length > 12) {
                    String header = bytesToHex(data, 12);
                    if (header.startsWith("52494646") && header.contains("57454250")) return null;
                }
                return data;
            } else {
                String path = coverImage;
                if (path.startsWith("/uploads/")) {
                    path = uploadPath + path.substring("/uploads/".length());
                }
                try (FileInputStream fis = new FileInputStream(path)) {
                    return IOUtils.toByteArray(fis);
                }
            }
        } catch (Exception e) {
            return null;
        }
    }

    private String bytesToHex(byte[] bytes, int maxLen) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < Math.min(bytes.length, maxLen); i++) {
            sb.append(String.format("%02X", bytes[i]));
        }
        return sb.toString();
    }
}
