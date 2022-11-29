package uet.ktmt.myproject.persistance.entity;

import uet.ktmt.myproject.base.BaseEntity;
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
public class Property extends BaseEntity {
    @Column(name = "name", unique = true)
    private String name;

    @Column(name = "note")
    private String note;

    @Column(name = "unit")
    private String unit;

    @ManyToMany
    @JoinTable(name = "category_properties", joinColumns = @JoinColumn(name = "property_id"), inverseJoinColumns = @JoinColumn(name = "category_id"))
    private Collection<Category> categories;

    @OneToMany(mappedBy = "property")
    private Collection<ProductProperty> productProperties;
}
