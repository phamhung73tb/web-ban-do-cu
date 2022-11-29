package uet.ktmt.myproject.persistance.entity;

import com.github.slugify.Slugify;
import uet.ktmt.myproject.base.BaseEntity;
import uet.ktmt.myproject.common.text.VNCharacterUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.util.Collection;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
public class Category extends BaseEntity {
    @Column(unique = true)
    private String name;

    @Column(unique = true)
    private String slug;

    private String image;

    @ManyToOne
    @JoinColumn(name = "category_parent_id")
    private Category categoryParent;

    @OneToMany(mappedBy = "category")
    private Collection<Product> products;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "category_properties", joinColumns = @JoinColumn(name = "category_id"), inverseJoinColumns = @JoinColumn(name = "property_id"))
    private Collection<Property> properties;

    @Override
    @PrePersist
    public void onPrePersist(){
        super.onPrePersist();
        slug = new Slugify().slugify(VNCharacterUtil.removeAccent(name));
    }

    @PreUpdate
    public void onPreUpdate(){
        slug = new Slugify().slugify(VNCharacterUtil.removeAccent(name));
    }
}
