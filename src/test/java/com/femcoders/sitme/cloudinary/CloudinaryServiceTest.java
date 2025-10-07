package com.femcoders.sitme.cloudinary;

import com.cloudinary.Cloudinary;
import com.cloudinary.Uploader;
import com.femcoders.sitme.cloudinary.dto.CloudinaryDTO;
import com.femcoders.sitme.cloudinary.exception.FileUploadException;
import com.femcoders.sitme.cloudinary.service.CloudinaryService;
import com.femcoders.sitme.shared.model.ImageUpdatable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CloudinaryServiceTest {

    @Mock
    private Cloudinary cloudinary;

    @Mock
    private Uploader uploader;

    @Mock
    private MultipartFile file;

    @InjectMocks
    private CloudinaryService cloudinaryService;

    private static final String PUBLIC_ID = "test/test-image_20240101120000";
    private static final String URL = "https://res.cloudinary.com/test/image/upload/test/test-image.jpg";
    private static final String FOLDER = "test-folder";
    private static final String FILE_NAME = "test-image_20240101120000.jpg";

    @BeforeEach
    void setUp() {
        lenient().when(cloudinary.uploader()).thenReturn(uploader);
    }

    @Test
    void uploadFile_Success_ReturnsCloudinaryDTO() throws Exception {
        byte[] fileBytes = "test content".getBytes();
        when(file.getBytes()).thenReturn(fileBytes);

        Map<String, Object> uploadResult = Map.of(
                "public_id", PUBLIC_ID,
                "url", URL
        );
        when(uploader.upload(eq(fileBytes), anyMap())).thenReturn(uploadResult);

        CloudinaryDTO result = cloudinaryService.uploadFile(file, FOLDER, FILE_NAME);

        assertNotNull(result);
        assertEquals(PUBLIC_ID, result.publicId());
        assertEquals(URL, result.url());
        verify(uploader).upload(eq(fileBytes), argThat(map ->
                map.get("public_id").equals(FOLDER + "/" + FILE_NAME)
        ));
    }

    @Test
    void uploadFile_FileGetBytesFails_ThrowsFileUploadException() throws Exception {
        when(file.getBytes()).thenThrow(new IOException("Error reading file"));

        FileUploadException exception = assertThrows(FileUploadException.class,
                () -> cloudinaryService.uploadFile(file, FOLDER, FILE_NAME)
        );

        assertEquals("Failed to upload file", exception.getMessage());
    }

    @Test
    void uploadFile_CloudinaryUploadFails_ThrowsFileUploadException() throws Exception {
        byte[] fileBytes = "test content".getBytes();
        when(file.getBytes()).thenReturn(fileBytes);
        when(uploader.upload(any(), anyMap())).thenThrow(new RuntimeException("Cloudinary error"));

        FileUploadException exception = assertThrows(FileUploadException.class,
                () -> cloudinaryService.uploadFile(file, FOLDER, FILE_NAME)
        );

        assertEquals("Failed to upload file", exception.getMessage());
    }

    @Test
    void deleteFile_Success_CallsCloudinaryDestroy() throws Exception {
        when(uploader.destroy(eq(PUBLIC_ID), anyMap())).thenReturn(Map.of("result", "ok"));

        cloudinaryService.deleteFile(PUBLIC_ID);

        verify(uploader).destroy(eq(PUBLIC_ID), anyMap());
    }

    @Test
    void deleteFile_CloudinaryFails_ThrowsFileUploadException() throws Exception {
        when(uploader.destroy(anyString(), anyMap())).thenThrow(new RuntimeException("Error eliminando"));

        FileUploadException exception = assertThrows(FileUploadException.class,
                () -> cloudinaryService.deleteFile(PUBLIC_ID)
        );

        assertEquals("Failed to delete file", exception.getMessage());
    }

    @Test
    void uploadEntityImage_ValidImage_UpdatesEntityWithImageData() throws Exception {
        TestImageEntity entity = new TestImageEntity();
        when(file.getOriginalFilename()).thenReturn("foto-perfil.jpg");
        when(file.getSize()).thenReturn(1024L);

        byte[] fileBytes = "image content".getBytes();
        when(file.getBytes()).thenReturn(fileBytes);

        Map<String, Object> uploadResult = Map.of(
                "public_id", PUBLIC_ID,
                "url", URL
        );
        when(uploader.upload(any(), anyMap())).thenReturn(uploadResult);

        TestImageEntity result = cloudinaryService.uploadEntityImage(entity, file, FOLDER);

        assertNotNull(result);
        assertEquals(URL, result.getImageUrl());
        assertEquals(PUBLIC_ID, result.getCloudinaryImageId());
    }

    @Test
    void uploadEntityImage_InvalidFileExtension_ThrowsFileUploadException() {
        TestImageEntity entity = new TestImageEntity();
        when(file.getOriginalFilename()).thenReturn("documento.pdf");
        when(file.getSize()).thenReturn(1024L);

        FileUploadException exception = assertThrows(FileUploadException.class,
                () -> cloudinaryService.uploadEntityImage(entity, file, FOLDER)
        );

        assertEquals("Only jpg, png, gif or bmp files are allowed", exception.getMessage());
    }

    @Test
    void deleteEntityImage_EntityWithImage_DeletesAndClearsImageData() throws Exception {
        TestImageEntity entity = new TestImageEntity();
        entity.setImageUrl(URL);
        entity.setCloudinaryImageId(PUBLIC_ID);

        when(uploader.destroy(eq(PUBLIC_ID), anyMap())).thenReturn(Map.of("result", "ok"));

        TestImageEntity result = cloudinaryService.deleteEntityImage(entity);

        assertNull(result.getImageUrl());
        assertNull(result.getCloudinaryImageId());
        verify(uploader).destroy(eq(PUBLIC_ID), anyMap());
    }

    @Test
    void deleteEntityImage_EntityWithoutImage_DoesNotCallCloudinary() throws Exception {
        TestImageEntity entity = new TestImageEntity();
        entity.setImageUrl(null);
        entity.setCloudinaryImageId(null);

        TestImageEntity result = cloudinaryService.deleteEntityImage(entity);

        assertNull(result.getImageUrl());
        assertNull(result.getCloudinaryImageId());
        verify(uploader, never()).destroy(anyString(), anyMap());
    }

    @Test
    void deleteEntityImage_CloudinaryFails_ThrowsFileUploadException() throws Exception {
        TestImageEntity entity = new TestImageEntity();
        entity.setImageUrl(URL);
        entity.setCloudinaryImageId(PUBLIC_ID);

        when(uploader.destroy(anyString(), anyMap())).thenThrow(new RuntimeException("Error de red"));

        FileUploadException exception = assertThrows(FileUploadException.class,
                () -> cloudinaryService.deleteEntityImage(entity)
        );

        assertEquals("Failed to delete image from Cloudinary", exception.getMessage());
    }

    @Test
    void deleteEntityImage_PublicIdBlank_DoesNotCallCloudinary() throws Exception {
        TestImageEntity entity = new TestImageEntity();
        entity.setImageUrl(URL);
        entity.setCloudinaryImageId("   ");

        TestImageEntity result = cloudinaryService.deleteEntityImage(entity);

        assertEquals("https://res.cloudinary.com/test/image/upload/test/test-image.jpg", result.getImageUrl());
        assertEquals("   ", result.getCloudinaryImageId());
        verify(uploader, never()).destroy(anyString(), anyMap());
    }


    private static class TestImageEntity implements ImageUpdatable {
        private String imageUrl;
        private String cloudinaryImageId;

        @Override
        public String getImageUrl() {
            return imageUrl;
        }

        @Override
        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        @Override
        public String getCloudinaryImageId() {
            return cloudinaryImageId;
        }

        @Override
        public void setCloudinaryImageId(String cloudinaryImageId) {
            this.cloudinaryImageId = cloudinaryImageId;
        }
    }
}