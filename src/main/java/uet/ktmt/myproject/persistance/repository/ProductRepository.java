package uet.ktmt.myproject.persistance.repository;

import uet.ktmt.myproject.persistance.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query(value = "select * from product where slug = :slug", nativeQuery = true)
    Optional<Product> findBySlug(@Param(value = "slug") String slug);
    @Query(value = "select * from product order by rand() limit :limit", nativeQuery = true)
    List<Product> getRandomProduct(@Param(value = "limit") int limit);

    @Query(value = "select * from product where status = 'STOCKING' order by id desc limit :limit", nativeQuery = true)
    List<Product> getNewProduct(@Param(value = "limit") int limit);

    @Query(value = "select * " +
            "from product inner join category child on product.category_id = child.id " +
            "inner join category parent on parent.id = child.category_parent_id " +
            "where (parent.id = 1) " +
            "order by rand() limit :limit", nativeQuery = true)
    List<Product> getRandomDoDienTu(@Param(value = "limit")int limit);

    @Query(value = "select * " +
            "from product inner join category child on product.category_id = child.id " +
            "inner join category parent on parent.id = child.category_parent_id " +
            "where (parent.id = 4) " +
            "order by rand() limit :limit", nativeQuery = true)
    List<Product> getRandomSachTruyen(@Param(value = "limit")int limit);

    @Query(value = "select * " +
            "from product inner join category child on product.category_id = child.id " +
            "inner join category parent on parent.id = child.category_parent_id " +
            "where (parent.id = 3) " +
            "order by rand() limit :limit", nativeQuery = true)
    List<Product> getRandomQuanAo(@Param(value = "limit")int limit);

    @Query(value = "select * from product where product.view > 10 order by rand() limit :limit", nativeQuery = true)
    List<Product> getHotProduct(@Param(value = "limit") int limit);

    @Query(value = "select * " +
            "from product inner join category child on product.category_id = child.id " +
            "inner join category parent on parent.id = child.category_parent_id " +
            "inner join address on product.id = address.product_id " +
            "where (:status = 'all' or product.status = :status) and (parent.slug = :slug or child.slug = :slug) and price >= :min and price <= :max and (code_province = :code_province or 0 = :code_province) " +
            "order by price", nativeQuery = true)
    Page<Product> filterProductAndSortByPrice(Pageable pageable
            , @Param(value = "slug") String slug, @Param(value = "min") int min
            , @Param(value = "max") int max, @Param(value = "code_province") int codeProvince
            , @Param(value = "status") String status);

    @Query(value = "select * " +
            "from product inner join category child on product.category_id = child.id " +
            "inner join category parent on parent.id = child.category_parent_id " +
            "inner join address on product.id = address.product_id " +
            "where (:status = 'all' or product.status = :status) and (parent.slug = :slug or child.slug = :slug) and price >= :min and price <= :max and (code_province = :code_province or 0 = :code_province)  " +
            "order by product.created_date desc", nativeQuery = true)
    Page<Product> filterProductAndSortByCreateDate(Pageable pageable
            , @Param(value = "slug") String slug, @Param(value = "min") int min
            , @Param(value = "max") int max, @Param(value = "code_province") int codeProvince
            , @Param(value = "status") String status);

    @Query(value = "select * " +
            "from product inner join category child on product.category_id = child.id " +
            "inner join category parent on parent.id = child.category_parent_id " +
            "inner join address on product.id = address.product_id " +
            "where (:status = 'all' or product.status = :status) and (parent.slug = :slug or child.slug = :slug or '' = :slug) and price >= :min and price <= :max and (code_province = :code_province or 0 = :code_province) ", nativeQuery = true)
    List<Product> filterProductList(
            @Param(value = "slug") String slug, @Param(value = "min") int min
            , @Param(value = "max") int max, @Param(value = "code_province") int codeProvince
            , @Param(value = "status") String status);

    @Query(value = "select * from product where product.id = :productId and status = 'STOCKING'", nativeQuery = true)
    Optional<Product> findProductOnSale(@Param(value = "productId") long id);

    @Query(value = "select count(*) from product where (date(created_date) between :from and :to)", nativeQuery = true)
    int getNewProduct(@Param(value = "from") String from, @Param(value = "to") String to);

    @Query(value = "select * " +
            "from product "+
            "where (category_id = :categoryId) and id != :productId " +
            "order by rand() limit :limit", nativeQuery = true)
    List<Product> getRecommendList(@Param(value = "categoryId")long categoryId, @Param(value = "productId")long productId, @Param(value = "limit") int limit);

    @Query(value = "select name from product", nativeQuery = true)
    List<String> getNameOfAllProduct();

    @Query(value = "select o.product_id  from ordered o " +
            "where user_id = :user_id " +
            "union " +
            "select w.product_id from wishlist w " +
            "where user_id = :user_id " +
            "limit 3", nativeQuery = true)
    List<Long> getLastProduct(@Param(value = "user_id") Long userId);

    @Query(value = "select product_id from history_click " +
            "where user_id = :user_id limit 3", nativeQuery = true)
    List<Long> getListInClickingHistory(@Param(value = "user_id") Long userId);
}
