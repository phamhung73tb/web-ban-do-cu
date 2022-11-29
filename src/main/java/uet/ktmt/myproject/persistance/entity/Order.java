package uet.ktmt.myproject.persistance.entity;

import uet.ktmt.myproject.base.BaseEntity;
import uet.ktmt.myproject.common.myEnum.OrderStatusEnum;
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
@Table(name = "ordered")
public class Order extends BaseEntity {
    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 30)
    private OrderStatusEnum status;

    @Column(name = "price_product")
    private long priceProduct;

    @Column(name = "fee_shipping")
    private long feeShipping;

    @Column(name = "method_payment")
    private String methodPayment;

    @Column(name = "rating")
    private Integer rating;

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL)
    private DeliveryAddress deliveryAddress;

    @OneToOne
    @JoinColumn(name = "product_id", referencedColumnName = "id")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
