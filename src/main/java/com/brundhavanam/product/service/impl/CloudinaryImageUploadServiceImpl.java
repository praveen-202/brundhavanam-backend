package com.brundhavanam.product.service.impl;

import com.brundhavanam.common.exception.BadRequestException;
import com.brundhavanam.product.service.ImageUploadService;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * Cloudinary-based image upload service.
 */
@Service
public class CloudinaryImageUploadServiceImpl implements ImageUploadService {

    private final Cloudinary cloudinary;

    public CloudinaryImageUploadServiceImpl(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    @Override
    public Map<String, String> uploadImage(MultipartFile file, String folder) {
        try {
            if (file == null || file.isEmpty()) {
                throw new BadRequestException("Image file is required");
            }

            // Optional validation for file types
            String contentType = file.getContentType();
            if (contentType == null || (!contentType.startsWith("image/"))) {
                throw new BadRequestException("Only image files are allowed");
            }

            Map<?, ?> uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "folder", folder,
                            "resource_type", "image"
                    )
            );

            String url = (String) uploadResult.get("secure_url");
            String publicId = (String) uploadResult.get("public_id");

            return Map.of(
                    "url", url,
                    "publicId", publicId
            );

        } catch (Exception e) {
            throw new BadRequestException("Image upload failed: " + e.getMessage());
        }
    }

    @Override
    public void deleteImage(String publicId) {
        try {
            cloudinary.uploader().destroy(
                    publicId,
                    ObjectUtils.emptyMap()
            );
        } catch (Exception e) {
            throw new BadRequestException("Image delete failed: " + e.getMessage());
        }
    }
}
