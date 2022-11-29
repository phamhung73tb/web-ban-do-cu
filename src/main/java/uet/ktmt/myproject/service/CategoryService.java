package uet.ktmt.myproject.service;

import uet.ktmt.myproject.persistance.entity.Category;
import uet.ktmt.myproject.persistance.entity.Product;
import uet.ktmt.myproject.persistance.entity.Property;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface CategoryService {
    public List<Category> getListCategoryHasNotParent();

    public List<Category> getListCategoryChild(long parentId);

    public Category getDetail(long id) throws Throwable;

    public Category create(Category category, String originalFilename);

    public Category edit(Category category, String originalFilename) throws Throwable;

    public void delete(long id) throws Throwable;

    public Map<Category, List<Category>> getTreeCategory();

    public void createTest(Category category);

    public void createTest2(Category category, String nameCategoryParent, List<String> properties);

    public List<Property> getListProperty(long categoryId) throws Throwable;

    public Page<Product> getProductByCategory(String slug, int page, int min, int max
            , String sort, int codeProvince, String status) throws Throwable;
}
