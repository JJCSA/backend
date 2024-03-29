package com.jjcsa.util;

import com.jjcsa.model.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;

@Slf4j
public class ImageUtil {
    public static String generateProfilePictureName(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1);
        return "PROFILE_PICTURE." + (fileExtension.isEmpty() ? "jpg" : fileExtension);
    }

    public static String generateProfilePictureKeyForUserProfile(User user) {
        // Generate the S3 file key - user id/PROFILE_PICTURE.{FILE EXT}
        String key = user.getId() + "/PROFILE_PICTURE."
                + user.getProfilePicture().substring(user.getProfilePicture().lastIndexOf(".")+1);

        return key;
    }

    public static String generateCommunityDocumentName(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1);
        return "COMMUNITY_DOCUMENT." + (fileExtension.isEmpty() ? "jpg" : fileExtension);
    }

    public static String generateCommunityDocumentKeyForUserProfile(User user) {
        // Generate the S3 file key - user id/COMMUNITY_DOCUMENT.{FILE EXT}
        String key = user.getId() + "/COMMUNITY_DOCUMENT."
                + user.getCommunityDocumentURL().substring(user.getCommunityDocumentURL().lastIndexOf(".")+1);

        return key;
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

    public static String saveFileLocally(File file, String fileKey) {
        try {
            String tmpdir = Files.createTempDirectory("jjcsaTmpDir").toFile().getAbsolutePath();
            File tmpFile = new File(tmpdir + File.separator + fileKey);
            FileUtils.copyFile(file, tmpFile);
            return tmpFile.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}
