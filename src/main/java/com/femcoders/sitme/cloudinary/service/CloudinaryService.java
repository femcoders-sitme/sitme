package com.femcoders.sitme.cloudinary.service;

import com.cloudinary.Cloudinary;
import com.femcoders.sitme.cloudinary.dto.CloudinaryDTO;
import com.femcoders.sitme.cloudinary.exception.FileUploadException;
import com.femcoders.sitme.cloudinary.util.FileUploadUtil;
import com.femcoders.sitme.shared.model.ImageUpdatable;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Service
public class CloudinaryService {
    @Autowired
    private Cloudinary cloudinary;

    @Transactional
    public CloudinaryDTO uploadFile(MultipartFile file, String folder, String fileName) {
        try {
            Map<?,?> result = cloudinary.uploader().upload(
                    file.getBytes(),
                    Map.of("public_id", folder + "/" + fileName));
            return new CloudinaryDTO(
                    (String) result.get("public_id"),
                    (String) result.get("url")
            );
        } catch (Exception e) {
            throw new FileUploadException("Failed to upload file");
        }
    }

    public void deleteFile(String publicId) {
        try {
            cloudinary.uploader().destroy(publicId, Map.of());
        } catch (Exception e) {
            throw new FileUploadException("Failed to delete file");
        }
    }

    public <T extends ImageUpdatable> T uploadEntityImage(T entity, MultipartFile file, String folder) {
        FileUploadUtil.assertAllowed(file, FileUploadUtil.IMAGE_PATTERN);
        String fileName = FileUploadUtil.getFileName(file.getOriginalFilename());
        CloudinaryDTO dto = uploadFile(file, folder, fileName);

        entity.setImageUrl(dto.url());
        entity.setCloudinaryImageId(dto.publicId());

        return entity;
    }

    public <T extends ImageUpdatable> T deleteEntityImage(T entity) {
        try {
            String publicId = entity.getCloudinaryImageId();

            if (publicId != null && !publicId.isBlank()) {
                deleteFile(publicId);

                entity.setImageUrl(null);
                entity.setCloudinaryImageId(null);
            }

            return entity;
        } catch (Exception e) {
            throw new FileUploadException("Failed to delete image from Cloudinary");
        }
    }

}
