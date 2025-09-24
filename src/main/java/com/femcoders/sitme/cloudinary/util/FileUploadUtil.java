package com.femcoders.sitme.cloudinary.util;

import com.femcoders.sitme.cloudinary.exception.FileUploadException;
import lombok.experimental.UtilityClass;
import org.springframework.web.multipart.MultipartFile;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.regex.Pattern;

@UtilityClass
public class FileUploadUtil {
    public static final long MAX_FILE_SIZE = 2 * 1024 * 1024;
    public static final String IMAGE_PATTERN = "([^\\s]+(\\.(?i)(jpg|png|gif|bmp))$)";
    public static final String DATE_FORMAT = "yyyyMMddHHmmss";
    public static final String FILE_NAME_FORMAT = "%s_%s";

    public static void assertAllowed(MultipartFile file, String pattern) {
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new FileUploadException("Max file size is 2MB");
        }
        String fileName = file.getOriginalFilename();
        if(!Pattern.compile(pattern, Pattern.CASE_INSENSITIVE).matcher(fileName).matches()) {
            throw new FileUploadException("Only jpg, png, gif or bmp files are allowed");
        }
    }
    public static String getFileName(final String name) {
        DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        final String date = dateFormat.format(System.currentTimeMillis());
        return String.format(FILE_NAME_FORMAT, name, date);
    }
}
