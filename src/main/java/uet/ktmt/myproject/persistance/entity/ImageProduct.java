package uet.ktmt.myproject.persistance.entity;

import uet.ktmt.myproject.base.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "image_product")
public class ImageProduct extends BaseEntity {
    @Column(name = "file_name")
    private String fileName;

    @Column(name = "is_main_image")
    private boolean isMainImage;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
}
