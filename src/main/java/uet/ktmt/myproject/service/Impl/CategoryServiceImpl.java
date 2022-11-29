package uet.ktmt.myproject.service.Impl;

import com.github.slugify.Slugify;
import uet.ktmt.myproject.common.exception.BadRequestException;
import uet.ktmt.myproject.common.file.FileUploadUtil;
import uet.ktmt.myproject.common.text.VNCharacterUtil;
import uet.ktmt.myproject.persistance.entity.Category;
import uet.ktmt.myproject.persistance.entity.Product;
import uet.ktmt.myproject.persistance.entity.Property;
import uet.ktmt.myproject.persistance.repository.CategoryRepository;
import uet.ktmt.myproject.persistance.repository.ProductRepository;
import uet.ktmt.myproject.persistance.repository.PropertyRepository;
import uet.ktmt.myproject.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    CategoryRepository categoryRepository;
    @Autowired
    PropertyRepository propertyRepository;
    @Autowired
    ProductRepository productRepository;

    static final String UPLOAD_DIR_CATEGORY = "src/main/resources/static/category/";

    public List<Category> getListCategoryHasNotParent() {
        return categoryRepository.getListCategoryHasNotParent();
    }

    public List<Category> getListCategoryChild(long parentId) {
        return categoryRepository.findByCategoryParentId(parentId);
    }

    public Category getDetail(long id) throws Throwable {
        return categoryRepository.findById(id).orElseThrow(
                () -> {
                    throw new BadRequestException("loại sản phẩm không tồn tại!!!");
                });
    }

    @Transactional
    public Category create(Category category, String originalFilename) {

        String typeOfFile = originalFilename.substring(originalFilename.lastIndexOf("."));

        String newFileName = new Slugify()
                .slugify(VNCharacterUtil.removeAccent(category.getName()) + System.currentTimeMillis());
        category.setImage(newFileName + typeOfFile);

        Category categoryParent = categoryRepository.findById(category.getCategoryParent().getId()).orElse(null);
        category.setCategoryParent(categoryParent);

        List<Property> propertyList = new ArrayList<>();
        for (Property item : category.getProperties()) {
            Property foundProperty = propertyRepository.findById(item.getId()).orElse(null);
            propertyList.add(foundProperty);
        }
        category.setProperties(propertyList);

        return categoryRepository.save(category);
    }

    @Transactional
    public Category edit(Category category, String originalFilename) throws Throwable {
        Category foundCategory = categoryRepository.findById(category.getId()).orElseThrow(
                () -> {
                    throw new BadRequestException("Loại sản phẩm không tồn tại!!!");
                });

        foundCategory.setName(category.getName());

        if (originalFilename != null) {
            String typeOfFile = originalFilename.substring(originalFilename.lastIndexOf("."));

            String newFileName = new Slugify()
                    .slugify(VNCharacterUtil.removeAccent(category.getName()) + System.currentTimeMillis());
            category.setImage(newFileName + typeOfFile);
        }

        return categoryRepository.save(foundCategory);
    }

    @Transactional
    public void delete(long id) throws Throwable {
        Category foundCategory = categoryRepository.findById(id).orElseThrow(
                () -> {
                    throw new BadRequestException("Loại sản phẩm không tồn tại!!!");
                });
        List<Category> listChild = getListCategoryChild(id);
        for (Category i : listChild) {
            delete(i.getId());
        }

        String uploadDir = UPLOAD_DIR_CATEGORY + id;

        FileUploadUtil.deleteDir(uploadDir);

        categoryRepository.deleteAll(listChild);
        categoryRepository.delete(foundCategory);
    }

    public Map<Category, List<Category>> getTreeCategory() {
        List<Category> categoryList = getListCategoryHasNotParent();

        HashMap<Category, List<Category>> treeCategory = new HashMap<>();

        for (Category item : categoryList) {
            treeCategory.put(item, getListCategoryChild(item.getId()));
        }

        return treeCategory;
    }

    @Transactional
    // chạy khởi tạo loại sản phẩm
    public void createTest(Category category) {
        category.setImage("non_image.png");
        categoryRepository.save(category);
    }

    @Transactional
    public void createTest2(Category category, String nameCategoryParent, List<String> properties) {

        List<Property> propertyList = new ArrayList<>();
        if (properties != null) {
            for (String item : properties) {
                Property property = propertyRepository.findByName(item);
                propertyList.add(property);
            }
        }

        Category categoryParent = categoryRepository.findByName(nameCategoryParent);
        category.setCategoryParent(categoryParent);
        category.setProperties(propertyList);
        category.setImage("non_image.png");
        categoryRepository.save(category);
    }

    public List<Property> getListProperty(long categoryId) throws Throwable {
        return categoryRepository.findById(categoryId).orElseThrow(() -> {
            throw new BadRequestException("Loại sản phẩm không tồn tại !!!");
        }).getProperties().stream().collect(Collectors.toList());
    }

    public Page<Product> getProductByCategory(String slug, int page, int min, int max, String sort, int codeProvince,
            String status) throws Throwable {
        Pageable pageable = PageRequest.of(page, 12);
        if (sort.equals("price")) {
            return productRepository.filterProductAndSortByPrice(pageable, slug, min, max, codeProvince, status);
        }
        // sắp xếp theo ngày tạo
        return productRepository.filterProductAndSortByCreateDate(pageable, slug, min, max, codeProvince, status);
    }
}
