package uet.ktmt.myproject.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public interface ImageProductService {
    void deleteImage(long imageProductId) throws Throwable;

    void addImage(long productId, MultipartFile multipartFile) throws IOException, Throwable;

    void changeMainImage(long productId, long imageProductId) throws Throwable;
}
