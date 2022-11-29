package uet.ktmt.myproject.service.Impl;

import com.github.slugify.Slugify;
import uet.ktmt.myproject.common.exception.BadRequestException;
import uet.ktmt.myproject.common.file.FileUploadUtil;
import uet.ktmt.myproject.persistance.entity.ImageProduct;
import uet.ktmt.myproject.persistance.entity.Product;
import uet.ktmt.myproject.persistance.repository.ImageProductRepository;
import uet.ktmt.myproject.persistance.repository.ProductRepository;
import uet.ktmt.myproject.service.ImageProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ImageProductServiceImpl implements ImageProductService {
    @Autowired
    private ImageProductRepository imageProductRepository;
    @Autowired
    private ProductRepository productRepository;

    static final String UPLOAD_DIR_IMAGE_PRODUCT = "src/main/resources/static/product/";

    @Transactional
    public void deleteImage(long imageProductId) throws Throwable {
        ImageProduct imageProduct = imageProductRepository.findById(imageProductId).orElseThrow(
                () -> {
                    throw new BadRequestException("Ảnh không tồn tại !!!");
                }
        );
        if (imageProduct.isMainImage()) {
            throw new BadRequestException("Không thể xóa ảnh bìa !!!");
        }
        String uploadDir = UPLOAD_DIR_IMAGE_PRODUCT + imageProduct.getProduct().getId();
        FileUploadUtil.deleteFile(uploadDir, imageProduct.getFileName());
        imageProductRepository.delete(imageProduct);
    }

    @Transactional
    public void addImage(long productId, MultipartFile multipartFile) throws IOException, Throwable {
        Product foundProduct = productRepository.findById(productId).orElseThrow(
                () -> {
                    throw new BadRequestException("Sản phẩm không tồn tại !!!");
                }
        );
        String uploadDir = UPLOAD_DIR_IMAGE_PRODUCT + productId;
        String fileName = new Slugify().slugify(foundProduct.getName() + LocalDateTime.now());
        String typeOfImage = multipartFile.getOriginalFilename().substring(multipartFile.getOriginalFilename().lastIndexOf("."));
        ImageProduct imageProduct = ImageProduct.builder()
                .fileName(fileName + typeOfImage)
                .isMainImage(false)
                .product(foundProduct)
                .build();
        FileUploadUtil.saveFile(uploadDir, imageProduct.getFileName(), multipartFile);
        imageProductRepository.save(imageProduct);
    }

    @Transactional
    public void changeMainImage(long productId, long imageProductId) throws Throwable {
        Product foundProduct = productRepository.findById(productId).orElseThrow(
                () -> {
                    throw new BadRequestException("Sản phẩm không tồn tại !!!");
                }
        );
        List<ImageProduct> imageProductList = foundProduct.getImageProducts().stream().collect(Collectors.toList());
        for (ImageProduct imageProduct : imageProductList) {
            if (imageProduct.isMainImage()) {
                imageProduct.setMainImage(false);
            }
            if (imageProduct.getId() == imageProductId) {
                imageProduct.setMainImage(true);
            }
        }
        foundProduct.setImageProducts(imageProductList);
        productRepository.save(foundProduct);
    }
}
