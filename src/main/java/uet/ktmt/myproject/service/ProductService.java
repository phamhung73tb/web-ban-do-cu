package uet.ktmt.myproject.service;

import uet.ktmt.myproject.persistance.entity.Order;
import uet.ktmt.myproject.persistance.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public interface ProductService {
    Product create(Product product) throws Throwable;

    void uploadImage(long productId, MultipartFile[] multipartFiles) throws IOException, Throwable;

    Product getDetailProductBySlug(String slug) throws Throwable;

    Product getDetailProduct(long productId) throws Throwable;

    Product getMyDetailProduct(long productId) throws Throwable;

    void edit(Product product) throws Throwable;

    void updateStatus(long productId) throws Throwable;

    void delete(long productId) throws Throwable;

    List<Product> getRandomProduct(int limit);

    List<Product> getNewProduct(int limit);

    List<Product> getHotProduct(int limit);

    Page<Product> searchProduct(String keyword, int page, String slugCategory
            , int min, int max, String sort, int codeProvince, String status);

    Order deliveryConfirmation(long productId) throws Throwable;

    void cancelDelivery(long productId) throws Throwable;

    int getNewProduct(String from, String to);

    List<Product> getRandomAllProduct(int limit);

    List<Product> getRandomQuanAo(int limit);

    List<Product> getRandomDoDienTu(int limit);

    List<Product> getRandomSachTruyen(int limit);

    List<Product> getRecommendList(long productId, int limit) throws Throwable;

    List<Product> getProductRecommendInHomePage(Long userId);
}
