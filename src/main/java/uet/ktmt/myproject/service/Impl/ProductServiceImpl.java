package uet.ktmt.myproject.service.Impl;

import org.springframework.util.CollectionUtils;
import uet.ktmt.myproject.common.exception.BadRequestException;
import uet.ktmt.myproject.common.exception.BadRequestReturnPageException;
import uet.ktmt.myproject.common.file.FileUploadUtil;
import uet.ktmt.myproject.common.myEnum.OrderStatusEnum;
import uet.ktmt.myproject.common.myEnum.ProductStatusEnum;
import uet.ktmt.myproject.common.text.VNCharacterUtil;
import uet.ktmt.myproject.persistance.repository.*;
import uet.ktmt.myproject.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import uet.ktmt.myproject.persistance.entity.*;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class ProductServiceImpl implements ProductService {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private PropertyRepository propertyRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private OrderRepository orderRepository;
    static final String UPLOAD_DIR_IMAGE_PRODUCT = "src/main/resources/static/product/";

    @Transactional
    public Product create(Product product) throws Throwable {
        Category foundCategory = categoryRepository.findById(product.getCategory().getId()).orElse(null);
        product.setCategory(foundCategory);

        List<ProductProperty> productProperties = new ArrayList<>();
        for (ProductProperty productProperty : product.getProductProperties()) {
            Property foundProperty = propertyRepository.findById(productProperty.getProperty().getId()).orElse(null);
            productProperty.setProperty(foundProperty);
            productProperty.setProduct(product);
            productProperties.add(productProperty);
        }
        product.setProductProperties(productProperties);

        product.getAddress().setProduct(product);

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User foundUser = userRepository.findByUsername(username).orElseThrow(
                () -> {
                    throw new BadRequestException("User không tồn tại !!!");
                }
        );
        product.setUser(foundUser);

        product.setStatus(ProductStatusEnum.STOCKING);

        return productRepository.save(product);
    }

    @Transactional
    public void uploadImage(long productId, MultipartFile[] multipartFiles) throws IOException, Throwable {
        Product foundProduct = productRepository.findById(productId).orElseThrow(() -> {
            throw new BadRequestException("Sản phẩm không tồn tại !!!");
        });
        String uploadDir = UPLOAD_DIR_IMAGE_PRODUCT + productId;

        List<ImageProduct> imageProductList = new ArrayList<>();
        int index = 0;
        for (MultipartFile image : multipartFiles) {
            String fileName = image.getName();
            String typeOfImage = image.getOriginalFilename().substring(image.getOriginalFilename().lastIndexOf("."));
            ImageProduct imageProduct = ImageProduct.builder()
                    .fileName(fileName + index + typeOfImage)
                    .isMainImage(index == 0 ? true : false)
                    .build();
            imageProduct.setProduct(foundProduct);
            imageProductList.add(imageProduct);
            FileUploadUtil.saveFile(uploadDir, imageProduct.getFileName(), multipartFiles[index]);
            index++;
        }

        foundProduct.setImageProducts(imageProductList);
        productRepository.save(foundProduct);
    }

    @Transactional
    public Product getDetailProductBySlug(String slug) throws Throwable {
        Product foundProduct = productRepository.findBySlug(slug).orElseThrow(() -> {
            throw new BadRequestException("Sản phẩm không tồn tại !!!");
        });

        foundProduct.setView(foundProduct.getView() + 1);
        return productRepository.save(foundProduct);
    }

    public Product getDetailProduct(long productId) throws Throwable {
        Product foundProduct = productRepository.findById(productId).orElseThrow(() -> {
            throw new BadRequestException("Sản phẩm không tồn tại !!!");
        });

        return foundProduct;
    }

    @Transactional
    public Product getMyDetailProduct(long productId) throws Throwable {
        Product foundProduct = productRepository.findById(productId).orElseThrow(() -> {
            throw new BadRequestException("Sản phẩm không tồn tại !!!");
        });
        if (!foundProduct.getUser().getUsername()
                .equals(SecurityContextHolder.getContext().getAuthentication().getName())) {
            throw new BadRequestReturnPageException("Bạn không thể truy cập trang này !!!");
        }
        return foundProduct;
    }

    @Transactional
    public void edit(Product product) throws Throwable {
        Product foundProduct = productRepository.findById(product.getId()).orElseThrow(() -> {
            throw new BadRequestException("Sản phẩm không tồn tại !!!");
        });
        if (!foundProduct.getStatus().equals(ProductStatusEnum.STOCKING)) {
            throw new BadRequestException("Chỉ có thể cập nhật khi sản phẩm ở trạng thái đang bán !!!");
        }
        foundProduct.setName(product.getName());
        foundProduct.setPrice(product.getPrice());
        foundProduct.setCurrentStatus(product.getCurrentStatus());
        foundProduct.setDescription(product.getDescription());

        List<ProductProperty> productProperties = foundProduct.getProductProperties().stream().collect(Collectors.toList());
        for (ProductProperty productProperty : productProperties) {
            String value = product.getProductProperties()
                    .stream()
                    .filter(p -> p.getProperty().getId().equals(productProperty.getProperty().getId()))
                    .findFirst().orElseThrow(
                            () -> {
                                throw new BadRequestException("Thuộc tính sản phẩm không tồn tại !!!");
                            })
                    .getValue();
            productProperty.setValue(value);
        }

        Address foundAddress = foundProduct.getAddress();
        foundAddress.setProvince(product.getAddress().getProvince());
        foundAddress.setDistrict(product.getAddress().getDistrict());
        foundAddress.setCommune(product.getAddress().getCommune());
        foundAddress.setDetail(product.getAddress().getDetail());

        productRepository.save(foundProduct);
    }

    @Transactional
    public void updateStatus(long productId) throws Throwable {
        Product foundProduct = productRepository.findById(productId).orElseThrow(
                () -> {
                    throw new BadRequestException("Sản phẩm không tồn tại !!!");
                }
        );
        foundProduct.setStatus(ProductStatusEnum.SOLD_OUT);
        productRepository.save(foundProduct);
    }

    @Transactional
    public void delete(long productId) throws Throwable {
        Product foundProduct = productRepository.findById(productId).orElseThrow(
                () -> {
                    throw new BadRequestException("Sản phẩm không tồn tại !!!");
                }
        );

        if (!foundProduct.getStatus().equals(ProductStatusEnum.STOCKING)) {
            throw new BadRequestException("Không thể xóa sản phẩm đang ở trạng thái chờ xác nhận hoặc đã bán !!!");
        }
        String uploadDir = UPLOAD_DIR_IMAGE_PRODUCT + productId;
        FileUploadUtil.deleteDir(uploadDir);
        productRepository.delete(foundProduct);
    }

    public List<Product> getRandomProduct(int limit) {
        return productRepository.getRandomProduct(limit);
    }

    public List<Product> getNewProduct(int limit) {
        return productRepository.getNewProduct(limit);
    }

    public List<Product> getHotProduct(int limit) {
        return productRepository.getHotProduct(limit);
    }

    public Page<Product> searchProduct(String keyword, int page, String slugCategory
            , int min, int max, String sort, int codeProvince, String status) {
        List<Product> productList = productRepository.filterProductList(slugCategory, min, max, codeProvince, status);
        String formatKeyword = VNCharacterUtil.removeAccent(keyword).toLowerCase();
        String[] listKey = formatKeyword.split(" ");
        Set<Product> setProduct = new HashSet<>();

        for (String key : listKey) {
            setProduct.addAll(productList.stream()
                    .filter(p -> p.getKeyword().contains(key)
                            && p.getStatus() == ProductStatusEnum.STOCKING)
                    .collect(Collectors.toList()));
        }
        productList = new ArrayList<>(setProduct);
        System.out.println(getCosSimForProduct(2L));

        if (sort.equals("price")) {
            Collections.sort(productList, Comparator.comparingLong(Product::getPrice));
        }
        if (sort.equals("created_date")) {
            Collections.sort(productList, (lhs, rhs) -> {
                if (lhs.getCreatedDate().isBefore(rhs.getCreatedDate())) {
                    return 1;
                }
                return -1;
            });
        }


        Pageable pageable = PageRequest.of(page, 12);
        final int start = (int) pageable.getOffset();
        final int end = Math.min((start + pageable.getPageSize()), productList.size());
        return new PageImpl<>(productList.subList(start, end), pageable, productList.size());
    }

    @Transactional
    public Order deliveryConfirmation(long productId) throws Throwable {
        Product foundProduct = productRepository.findById(productId).orElseThrow(
                () -> {
                    throw new BadRequestException("Không tìm thấy sản phẩm !!!");
                }
        );
        foundProduct.setStatus(ProductStatusEnum.SOLD_OUT);
        productRepository.save(foundProduct);

        Order foundOrder = orderRepository.findByProductId(productId).orElseThrow(
                () -> {
                    throw new BadRequestException("Không tìm thấy đơn hàng đặt sản phẩm này !!!");
                }
        );
        foundOrder.setStatus(OrderStatusEnum.DELIVERY);
        return orderRepository.save(foundOrder);
    }

    @Transactional
    public void cancelDelivery(long productId) throws Throwable {
        Product foundProduct = productRepository.findById(productId).orElseThrow(
                () -> {
                    throw new BadRequestException("Không tìm thấy sản phẩm !!!");
                }
        );

        foundProduct.setStatus(ProductStatusEnum.STOCKING);
        productRepository.save(foundProduct);

        Order foundOrder = orderRepository.findByProductId(productId).orElseThrow(
                () -> {
                    throw new BadRequestException("Không tìm thấy đơn hàng đặt sản phẩm này !!!");
                }
        );
        if (foundOrder.getStatus().equals(OrderStatusEnum.PAID)) {
            foundOrder.setStatus(OrderStatusEnum.WAITING_REFUND);
        } else {
            foundOrder.setStatus(OrderStatusEnum.CANCELED);
        }
        orderRepository.save(foundOrder);
    }

    public int getNewProduct(String from, String to) {
        return productRepository.getNewProduct(from, to);
    }

    public List<Product> getRandomAllProduct(int limit) {
        return productRepository.getRandomProduct(limit);
    }

    public List<Product> getRandomQuanAo(int limit) {
        return productRepository.getRandomQuanAo(limit);
    }

    public List<Product> getRandomDoDienTu(int limit) {
        return productRepository.getRandomDoDienTu(limit);
    }

    public List<Product> getRandomSachTruyen(int limit) {
        return productRepository.getRandomSachTruyen(limit);
    }

    public List<Product> getRecommendList(long productId, int limit) throws Throwable {

        List<Product> getRecommendList = new ArrayList<>();
        Map<Long, Double> getCosSimProduct = getCosSimForProduct(productId);

        System.out.println(productRepository.getById(productId).getName() + " " + productRepository.getById(productId).getPrice());

        getCosSimProduct.forEach((key, val) -> {
            if (productRepository.getById(key + 1).getStatus() == ProductStatusEnum.STOCKING
                    && key + 1 != productId) {
                getRecommendList.add(productRepository.getById(key + 1));
                System.out.println(productRepository.getById(key + 1).getName() + " " + productRepository.getById(key + 1).getPrice()+ "vnd");
                System.out.println("cosine similarity: " + val);
            }
        });

        return getRecommendList;
    }

    @Override
    public List<Product> getProductRecommendInHomePage(Long userId) {
        List<Product> getProductRecommendInHomePage = new ArrayList<>();
        List<Long> lastedProduct = productRepository.getLastProduct(userId);
        lastedProduct.addAll(productRepository.getListInClickingHistory(userId));
        Map<Long, Double> res = new HashMap<>();
        if (!CollectionUtils.isEmpty(lastedProduct)) {
            for (Long productId : lastedProduct) {
                Map<Long, Double> getCosSimProduct = getCosSimForProduct(productId);
                getCosSimProduct.forEach(res::putIfAbsent);
                System.out.println(productRepository.getById(productId).getName() + productRepository.getById(productId).getPrice());
            }

            Map<Long, Double> sorted = res.entrySet().stream()
                    .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                    .limit(10)
                    .collect(Collectors.toMap(
                            Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

            sorted.forEach((key, val) -> {
                if (productRepository.getById(key + 1).getStatus() == ProductStatusEnum.STOCKING
                        && !lastedProduct.contains(key + 1)) {
                    getProductRecommendInHomePage.add(productRepository.getById(key + 1));
                    System.out.println(productRepository.getById(key + 1).getName() + " " + productRepository.getById(key + 1).getPrice()+ "vnd");
                    System.out.println("cosine similarity: " + val);
                }
            });
        }
        return getProductRecommendInHomePage;
    }

    public Map<Long, Double> getCosSimForProduct(Long productId) {
        Map<Long, Double> res = new HashMap<>();
        List<Product> list = productRepository.findAll();
        for (int i = 0; i < list.size(); i ++) {
            if (productRepository.getById((long) (i + 1)).getStatus() == ProductStatusEnum.STOCKING) {
                res.put((long) i, cosineSimilarity(getTfIdfOfProduct(Math.toIntExact(productId) - 1),
                        getTfIdfOfProduct((int) (list.get(i).getId() - 1))));
            }
        }
        Map<Long, Double> sorted = res.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .limit(10)
                .collect(Collectors.toMap(
                        Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
        return sorted;
    }

    public List<Double> getTfIdfOfProduct(int index) {
        List<String> nameOfAll = productRepository.getNameOfAllProduct();
        List<List<String>> docs = new ArrayList<>();
        Set<String> allText = new HashSet<>();
        List<Double> res = new ArrayList<>();
        for (int i = 0; i < nameOfAll.size(); i ++) {
            List<String> doc = List.of(nameOfAll.get(i).split(" "));
            docs.add(doc);
            for (String text : doc) {
                allText.add(text);
            }
        }

        for (String text : allText) {
            if (docs.get(index).contains(text)) {
                res.add(tfIdf(docs.get(index), docs, text));
            } else {
                res.add(0D);
            }
        }
        Product product = productRepository.getById((long) index + 1);
        res.add((double) product.getPrice()/getAveragePrice());
        return res;
    }

    private Double getAveragePrice() {
        OptionalDouble averagePrice = productRepository.findAll().stream()
                .mapToLong(Product::getPrice)
                .average();
        return averagePrice.getAsDouble();
    }

    public static double cosineSimilarity(List<Double> vectorA, List<Double> vectorB) {
        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;
        for (int i = 0; i < vectorA.size(); i++) {
            dotProduct += vectorA.get(i) * vectorB.get(i);
            normA += Math.pow(vectorA.get(i), 2);
            normB += Math.pow(vectorB.get(i), 2);
        }
        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }

    public double tf(List<String> doc, String term) {
        double result = 0;
        for (String word : doc) {
            if (term.equalsIgnoreCase(word))
                result++;
        }
        return result / doc.size();
    }

    public double idf(List<List<String>> docs, String term) {
        double n = 0;
        for (List<String> doc : docs) {
            for (String word : doc) {
                if (term.equalsIgnoreCase(word)) {
                    n++;
                    break;
                }
            }
        }
        return Math.log((double)docs.size() / (double) n);
    }

    public double tfIdf(List<String> doc, List<List<String>> docs, String term) {
        return tf(doc, term) * idf(docs, term);
    }
}
