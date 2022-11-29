package uet.ktmt.myproject.presentation.controller.basic;

import uet.ktmt.myproject.common.file.FileUploadUtil;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

// @Slf4j
@Controller
public class ImageController {
    //get image's url
    @GetMapping("/{folder}/{id}/image/{fileName:.+}")
    public ResponseEntity<byte[]> readDetailFile(@PathVariable String folder, @PathVariable String id
            , @PathVariable String fileName) {
        //log.info("Mapped readDetailFile method GET");
        try {
            byte[] bytes = FileUploadUtil.readFileContent(folder + "/" + id + "/" + fileName);
            return ResponseEntity
                    .ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(bytes);
        } catch (Exception exception) {
            return ResponseEntity.noContent().build();
        }
    }
}
