package com.example.aspen.Service;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.util.UUID;

@Service
public class FileStorageService {

    private final String uploadDir = System.getProperty("user.dir") + File.separator + "uploads";

    public String saveImage(MultipartFile image) throws Exception {

        File folder = new File(uploadDir);

        if (!folder.exists()) {
            folder.mkdirs();
        }

        String fileName = UUID.randomUUID() + "_" + image.getOriginalFilename();

        File destination = new File(folder , fileName);

        image.transferTo(destination);

        return "/uploads/" + fileName;
    }

    public void deleteImage(String imageUrl) {
        if (imageUrl == null || imageUrl.isBlank()) {
            return;
        }

        String fileName = imageUrl.replace("/uploads/" , "");

        File imageFile = new File(uploadDir , fileName);

        if (imageFile.exists()) {
            imageFile.delete();
        }

    }
}
