package com.jjcsa.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@Slf4j
public class ImageUtil {
    public static String generateProfilePictureName(MultipartFile file) {
        String fileExtension = file.getName().substring(file.getName().lastIndexOf(".") + 1);
        return "PROFILE_PICTURE." + (fileExtension.isEmpty() ? "jpg" : fileExtension);
    }

    public static String generateCommunityDocumentName(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1);
        return "COMMUNITY_DOCUMENT." + (fileExtension.isEmpty() ? "jpg" : fileExtension);
    }

    public static File convertMultiPartFileToFile(final MultipartFile multipartFile) {
        final File file = new File(multipartFile.getName());
        try (final FileOutputStream outputStream = new FileOutputStream(file)) {
            outputStream.write(multipartFile.getBytes());
        } catch (final IOException ex) {
            log.error("Error converting multi-part file to file: " + ex.getMessage());
        }
        return file;
    }
}
