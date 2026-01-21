package com.brundhavanam.product.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface ImageUploadService {

    /**
     * Uploads an image to Cloudinary.
     *
     * @param file Multipart file received from client
     * @param folder Cloudinary folder name (e.g., "products")
     * @return Map containing url and public_id
     */
    Map<String, String> uploadImage(MultipartFile file, String folder);

    /**
     * Deletes an image from Cloudinary using public_id.
     */
    void deleteImage(String publicId);
}
