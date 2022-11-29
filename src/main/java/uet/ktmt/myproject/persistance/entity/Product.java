package uet.ktmt.myproject.persistance.entity;

import com.github.slugify.Slugify;
import uet.ktmt.myproject.base.BaseEntity;
import uet.ktmt.myproject.common.myEnum.ProductStatusEnum;
import uet.ktmt.myproject.common.text.VNCharacterUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.util.Collection;
import java.util.Optional;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
public class Product extends BaseEntity {
    @Column(name = "name")
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 30)
    private ProductStatusEnum status;

    @Column(name = "slug")
    private String slug;

    @Column(name = "current_status")
    private String currentStatus;

    @Column(name = "price")
    private long price;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "view")
    private int view;

    @OneToOne(mappedBy = "product", cascade = CascadeType.ALL)
    private Address address;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private Collection<ImageProduct> imageProducts;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private Collection<ProductProperty> productProperties;

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.DETACH})
    @JoinTable(name = "wishlist", joinColumns = @JoinColumn(name = "product_id"), inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Collection<User> wishlistUsers;

    @OneToMany(mappedBy = "product")
    private Collection<HistoryClick> historyClick;


    @PrePersist
    public void onPrePersist() {
        super.onPrePersist();
        slug = new Slugify().slugify(VNCharacterUtil.removeAccent(name) + this.getId());
    }

    @PreUpdate
    public void onPreUpdate() {
        slug = new Slugify().slugify(VNCharacterUtil.removeAccent(name) + this.getId());
    }

    @Transient
    private String keyword;

    @PostLoad
    public void onPostLoad() {
        String propertyKeyword = "";
        Optional<ProductProperty> propertyHang = productProperties
                .stream()
                .filter(p -> p.getProperty()
                        .getName().equals("Hãng")).findFirst();
        if (propertyHang.isPresent()) {
            propertyKeyword = propertyHang.get().getValue();
        }
        keyword = (" " + (VNCharacterUtil.removeAccent(name) + " "
                + VNCharacterUtil.removeAccent(description) + " "
                + VNCharacterUtil.removeAccent(propertyKeyword)) + " ")
                .toLowerCase();
    }
}
