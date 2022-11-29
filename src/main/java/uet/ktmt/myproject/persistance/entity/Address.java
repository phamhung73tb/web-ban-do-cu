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
public class Address extends BaseEntity {
    @Column(name = "province")
    private String province;

    @Column(name = "code_province")
    private int codeProvince;

    @Column(name = "district")
    private String district;

    @Column(name = "code_district")
    private int codeDistrict;

    @Column(name = "commune")
    private String commune;

    @Column(name = "code_commune")
    private int codeCommune;

    @Column(name = "detail")
    private String detail;

    @OneToOne
    @JoinColumn(name = "product_id", referencedColumnName = "id")
    private Product product;
}
