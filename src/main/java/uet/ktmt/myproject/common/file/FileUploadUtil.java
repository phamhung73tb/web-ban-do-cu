package uet.ktmt.myproject.common.file;

import org.springframework.core.io.UrlResource;
import org.springframework.util.StreamUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;

// @Slf4j
public class FileUploadUtil {
    private FileUploadUtil() {
        super();
    }

    public static void saveFile(String uploadDir, String fileName,
                                MultipartFile multipartFile) throws IOException {
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        try (InputStream inputStream = multipartFile.getInputStream()) {
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ioe) {
            throw new IOException("Could not save image file: " + fileName, ioe);
        }
    }

    public static void deleteFile(String uploadDir, String fileName) throws IOException {
        Path uploadPath = Paths.get(uploadDir);
        Path filePath = uploadPath.resolve(fileName);
        Files.deleteIfExists(filePath);
    }

    public static void deleteDir(String uploadDir) {
        File index = new File(uploadDir);
        String[] entries = index.list();
        if (entries != null) {
            for (String s : entries) {
                File currentFile = new File(index.getPath(), s);
                //Files.delete(currentFile.toPath());
                currentFile.delete();
            }
        }
        //Files.delete(index.toPath());
        index.delete();
    }

    public static byte[] readFileContent(String pathOrFileName) throws IOException {
        Path storageFolder = Paths.get("src/main/resources/static");
        Path file = storageFolder.resolve(pathOrFileName);
        UrlResource resource = new UrlResource(file.toUri());
        //log.info(resource.toString());
        if (resource.exists() || resource.isReadable()) {
            try (InputStream image = resource.getInputStream()) {
                return StreamUtils.copyToByteArray(image);
            } catch (FileNotFoundException e) {
                throw new FileNotFoundException("File Not Found.");
            } catch (IOException e) {
                throw new IOException("An I/O Error Occurred");
            }
        } else {
            throw new IOException("Could not read file: " + pathOrFileName);
        }
    }
}
