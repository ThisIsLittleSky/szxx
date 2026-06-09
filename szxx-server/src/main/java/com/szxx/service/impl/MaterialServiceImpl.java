package com.szxx.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.szxx.common.exception.BusinessException;
import com.szxx.dto.request.MaterialQuery;
import com.szxx.dto.response.BatchUploadResponse;
import com.szxx.dto.response.MaterialDetailResponse;
import com.szxx.dto.response.MaterialListResponse;
import com.szxx.entity.*;
import com.szxx.mapper.*;
import com.szxx.service.FileService;
import com.szxx.service.MaterialService;
import com.szxx.service.VideoParseService;
import com.szxx.util.ParsedDocument;
import com.szxx.util.PdfParserUtil;
import com.szxx.util.WordParserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MaterialServiceImpl implements MaterialService {

    private final MaterialMapper materialMapper;
    private final MaterialTagMapper materialTagMapper;
    private final MaterialAttachmentMapper materialAttachmentMapper;
    private final KnowledgePointMapper knowledgePointMapper;
    private final FavoriteMapper favoriteMapper;
    private final TagMapper tagMapper;
    private final UserMapper userMapper;
    private final CategoryDictMapper categoryDictMapper;
    private final FileService fileService;
    private final VideoParseService videoParseService;
    private final WordParserUtil wordParserUtil;
    private final PdfParserUtil pdfParserUtil;

    @Override
    @Transactional
    public Material createMaterial(String title, String author, String dynasty, String category,
                                   String educationLevel, String tags, String content, String videoUrl,
                                   MultipartFile coverImage, List<MultipartFile> attachments, Long uploaderId) {
        Material material = new Material();
        material.setTitle(title);
        material.setAuthor(author);
        material.setDynasty(dynasty);
        material.setCategory(category);
        material.setEducationLevel(educationLevel);
        material.setContent(content);
        material.setVideoUrl(videoUrl);
        material.setUploaderId(uploaderId);
        material.setStatus("pending");
        material.setViewCount(0);
        material.setFavoriteCount(0);
        material.setDownloadCount(0);

        if (coverImage != null && !coverImage.isEmpty()) {
            material.setCoverImage(fileService.uploadImage(coverImage));
        } else if (videoUrl != null && !videoUrl.isBlank()) {
            try {
                String coverUrl = videoParseService.parseVideoUrl(videoUrl).getCoverUrl();
                if (coverUrl != null && !coverUrl.isBlank()) {
                    String localPath = fileService.downloadAndStoreCoverImage(coverUrl);
                    material.setCoverImage(localPath != null ? localPath : coverUrl);
                }
            } catch (Exception ignored) {
                // cover is optional, continue without it
            }
        }

        materialMapper.insert(material);

        // bind tags
        if (tags != null && !tags.isBlank()) {
            List<String> tagNames = Arrays.stream(tags.split(","))
                    .map(String::trim).filter(s -> !s.isEmpty()).collect(Collectors.toList());
            bindTags(material.getId(), tagNames);
        }

        // save attachments
        if (attachments != null) {
            for (MultipartFile file : attachments) {
                if (!file.isEmpty()) {
                    saveAttachment(file, material.getId());
                }
            }
        }

        // update redundant tags field
        material.setTags(getTagString(material.getId()));
        materialMapper.updateById(material);

        return material;
    }

    @Override
    @Transactional
    public Material updateMaterial(Long id, String title, String author, String dynasty, String category,
                                   String educationLevel, String tags, String content, String videoUrl,
                                   MultipartFile coverImage, List<MultipartFile> attachments, Long userId) {
        Material material = materialMapper.selectById(id);
        if (material == null) {
            throw BusinessException.notFound("素材不存在");
        }
        checkOwnership(material, userId);

        if (title != null) material.setTitle(title);
        if (author != null) material.setAuthor(author);
        if (dynasty != null) material.setDynasty(dynasty);
        if (category != null) material.setCategory(category);
        if (educationLevel != null) material.setEducationLevel(educationLevel);
        if (content != null) material.setContent(content);
        if (videoUrl != null) material.setVideoUrl(videoUrl);

        if (coverImage != null && !coverImage.isEmpty()) {
            material.setCoverImage(fileService.uploadImage(coverImage));
        }

        materialMapper.updateById(material);

        if (tags != null) {
            materialTagMapper.delete(new LambdaQueryWrapper<MaterialTag>().eq(MaterialTag::getMaterialId, id));
            if (!tags.isBlank()) {
                List<String> tagNames = Arrays.stream(tags.split(","))
                        .map(String::trim).filter(s -> !s.isEmpty()).collect(Collectors.toList());
                bindTags(id, tagNames);
            }
        }

        if (attachments != null) {
            for (MultipartFile file : attachments) {
                if (!file.isEmpty()) {
                    saveAttachment(file, id);
                }
            }
        }

        material.setTags(getTagString(id));
        materialMapper.updateById(material);

        return material;
    }

    @Override
    @Transactional
    public void deleteMaterial(Long id, Long userId) {
        Material material = materialMapper.selectById(id);
        if (material == null) {
            throw BusinessException.notFound("素材不存在");
        }
        checkOwnership(material, userId);

        materialAttachmentMapper.delete(new LambdaQueryWrapper<MaterialAttachment>().eq(MaterialAttachment::getMaterialId, id));
        materialTagMapper.delete(new LambdaQueryWrapper<MaterialTag>().eq(MaterialTag::getMaterialId, id));
        knowledgePointMapper.delete(new LambdaQueryWrapper<KnowledgePoint>().eq(KnowledgePoint::getMaterialId, id));
        favoriteMapper.delete(new LambdaQueryWrapper<Favorite>().eq(Favorite::getMaterialId, id));
        materialMapper.deleteById(id);
    }

    @Override
    public MaterialDetailResponse getMaterialDetail(Long id, Long currentUserId) {
        Material material = materialMapper.selectById(id);
        if (material == null || (!"approved".equals(material.getStatus())
                && (currentUserId == null || !material.getUploaderId().equals(currentUserId)))) {
            throw BusinessException.notFound("素材不存在");
        }

        List<KnowledgePoint> knowledgePoints = knowledgePointMapper.selectList(
                new LambdaQueryWrapper<KnowledgePoint>()
                        .eq(KnowledgePoint::getMaterialId, id)
                        .orderByAsc(KnowledgePoint::getSortOrder));

        List<MaterialAttachment> attachments = materialAttachmentMapper.selectList(
                new LambdaQueryWrapper<MaterialAttachment>().eq(MaterialAttachment::getMaterialId, id));

        User uploader = userMapper.selectById(material.getUploaderId());

        boolean isFavorited = false;
        if (currentUserId != null) {
            isFavorited = favoriteMapper.selectCount(new LambdaQueryWrapper<Favorite>()
                    .eq(Favorite::getUserId, currentUserId)
                    .eq(Favorite::getMaterialId, id)) > 0;
        }

        // 仅已审核素材增加浏览量
        if ("approved".equals(material.getStatus())) {
            material.setViewCount(material.getViewCount() + 1);
            materialMapper.updateById(material);
        }

        List<String> tagList = material.getTags() != null
                ? Arrays.asList(material.getTags().split(",")) : new ArrayList<>();

        return MaterialDetailResponse.builder()
                .id(material.getId())
                .title(material.getTitle())
                .author(material.getAuthor())
                .dynasty(getCategoryName("dynasty", material.getDynasty()))
                .category(getCategoryName("category", material.getCategory()))
                .educationLevel(getCategoryName("education_level", material.getEducationLevel()))
                .tags(tagList)
                .coverImage(material.getCoverImage())
                .summary(material.getSummary())
                .content(material.getContent())
                .videoUrl(material.getVideoUrl())
                .status(material.getStatus())
                .knowledgePoints(knowledgePoints)
                .attachments(attachments)
                .uploader(MaterialDetailResponse.UploaderInfo.builder()
                        .id(uploader.getId()).nickname(uploader.getNickname()).build())
                .viewCount(material.getViewCount())
                .favoriteCount(material.getFavoriteCount())
                .isFavorited(isFavorited)
                .createdAt(material.getCreatedAt())
                .build();
    }

    @Override
    public IPage<MaterialListResponse> getMaterialPage(MaterialQuery query) {
        Page<Material> page = new Page<>(query.getPage(), query.getSize());
        IPage<Material> result = materialMapper.selectPageWithFilters(page,
                "approved", null, query.getDynasty(), query.getCategory(), query.getEducationLevel(),
                query.getKeyword(), query.getSort());

        return result.convert(this::toListResponse);
    }

    @Override
    public IPage<MaterialListResponse> getMyMaterialPage(MaterialQuery query, Long userId) {
        Page<Material> page = new Page<>(query.getPage(), query.getSize());
        IPage<Material> result = materialMapper.selectPageWithFilters(page,
                null, userId, query.getDynasty(), query.getCategory(), query.getEducationLevel(),
                query.getKeyword(), query.getSort());

        return result.convert(this::toListResponse);
    }

    @Override
    @Transactional
    public BatchUploadResponse batchUpload(List<MultipartFile> files, String dynasty,
                                           String category, String educationLevel, Long uploaderId) {
        int total = files.size();
        int success = 0;
        int failed = 0;
        List<BatchUploadResponse.BatchUploadItem> results = new ArrayList<>();

        for (MultipartFile file : files) {
            try {
                String originalFilename = file.getOriginalFilename();
                String ext = originalFilename != null && originalFilename.contains(".")
                        ? originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase() : "";

                ParsedDocument parsed;
                if ("docx".equals(ext)) {
                    parsed = wordParserUtil.parseDocx(file.getInputStream());
                } else if ("pdf".equals(ext)) {
                    parsed = pdfParserUtil.parsePdf(file.getInputStream());
                } else {
                    results.add(BatchUploadResponse.BatchUploadItem.builder()
                            .filename(originalFilename).status("failed").error("不支持的文件格式").build());
                    failed++;
                    continue;
                }

                Material material = new Material();
                material.setTitle(parsed.getTitle());
                material.setContent(parsed.getContent());
                material.setDynasty(dynasty);
                material.setCategory(category);
                material.setEducationLevel(educationLevel);
                material.setUploaderId(uploaderId);
                material.setStatus("pending");
                material.setViewCount(0);
                material.setFavoriteCount(0);
                material.setDownloadCount(0);
                materialMapper.insert(material);

                saveAttachment(file, material.getId());

                results.add(BatchUploadResponse.BatchUploadItem.builder()
                        .filename(originalFilename).materialId(material.getId()).status("success").build());
                success++;
            } catch (Exception e) {
                results.add(BatchUploadResponse.BatchUploadItem.builder()
                        .filename(file.getOriginalFilename()).status("failed").error("文件解析失败: " + e.getMessage()).build());
                failed++;
            }
        }

        return BatchUploadResponse.builder()
                .total(total).success(success).failed(failed).results(results).build();
    }

    private void bindTags(Long materialId, List<String> tagNames) {
        for (String name : tagNames) {
            Tag tag = tagMapper.selectOne(new LambdaQueryWrapper<Tag>().eq(Tag::getName, name));
            if (tag == null) {
                tag = new Tag();
                tag.setName(name);
                tagMapper.insert(tag);
            }
            MaterialTag mt = new MaterialTag();
            mt.setMaterialId(materialId);
            mt.setTagId(tag.getId());
            materialTagMapper.insert(mt);
        }
    }

    private String getTagString(Long materialId) {
        List<MaterialTag> mts = materialTagMapper.selectList(
                new LambdaQueryWrapper<MaterialTag>().eq(MaterialTag::getMaterialId, materialId));
        if (mts.isEmpty()) return "";

        List<Long> tagIds = mts.stream().map(MaterialTag::getTagId).collect(Collectors.toList());
        List<Tag> tags = tagMapper.selectBatchIds(tagIds);
        return tags.stream().map(Tag::getName).collect(Collectors.joining(","));
    }

    private void saveAttachment(MultipartFile file, Long materialId) {
        String diskPath = fileService.uploadAttachment(file, materialId);
        MaterialAttachment attachment = new MaterialAttachment();
        attachment.setMaterialId(materialId);
        attachment.setFilename(file.getOriginalFilename());
        attachment.setFilePath(diskPath);
        attachment.setFileSize(file.getSize());
        String ext = file.getOriginalFilename() != null && file.getOriginalFilename().contains(".")
                ? file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1).toLowerCase() : "";
        attachment.setFileType(ext);
        materialAttachmentMapper.insert(attachment);
    }

    private void checkOwnership(Material material, Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) throw BusinessException.notFound("用户不存在");
        if (!material.getUploaderId().equals(userId) && !"admin".equals(user.getRole())) {
            throw BusinessException.forbidden("无权操作此素材");
        }
    }

    private MaterialListResponse toListResponse(Material m) {
        List<String> tagList = m.getTags() != null && !m.getTags().isEmpty()
                ? Arrays.asList(m.getTags().split(",")) : new ArrayList<>();

        String dynastyName = getCategoryName("dynasty", m.getDynasty());
        String categoryName = getCategoryName("category", m.getCategory());
        String educationName = getCategoryName("education_level", m.getEducationLevel());

        String summary = m.getContent();
        if (summary != null) {
            summary = summary.replaceAll("<[^>]+>", "");
            if (summary.length() > 200) summary = summary.substring(0, 200);
        }

        return MaterialListResponse.builder()
                .id(m.getId())
                .title(m.getTitle())
                .author(m.getAuthor())
                .dynasty(dynastyName)
                .dynastyCode(m.getDynasty())
                .category(categoryName)
                .categoryCode(m.getCategory())
                .educationLevel(educationName)
                .educationCode(m.getEducationLevel())
                .tags(tagList)
                .coverImage(m.getCoverImage())
                .summary(summary)
                .status(m.getStatus())
                .viewCount(m.getViewCount())
                .favoriteCount(m.getFavoriteCount())
                .createdAt(m.getCreatedAt())
                .build();
    }

    private String getCategoryName(String type, String code) {
        if (code == null) return "";
        CategoryDict dict = categoryDictMapper.selectOne(
                new LambdaQueryWrapper<CategoryDict>()
                        .eq(CategoryDict::getType, type)
                        .eq(CategoryDict::getCode, code));
        return dict != null ? dict.getName() : code;
    }
}
