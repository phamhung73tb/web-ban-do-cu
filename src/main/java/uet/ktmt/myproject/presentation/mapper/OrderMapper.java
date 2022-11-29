package uet.ktmt.myproject.presentation.mapper;

import uet.ktmt.myproject.common.myEnum.OrderStatusEnum;
import uet.ktmt.myproject.persistance.entity.Order;
import uet.ktmt.myproject.persistance.entity.Product;
import uet.ktmt.myproject.presentation.request.OrderRequest;
import uet.ktmt.myproject.presentation.response.OrderResponse;

import java.time.format.DateTimeFormatter;

public class OrderMapper {
    private OrderMapper() {
        super();
    }

    public static Order convertToOrder(OrderRequest orderRequest) {
        return Order.builder()
                .id(orderRequest.getId())
                .feeShipping(orderRequest.getFeeShipping())
                .methodPayment(orderRequest.getMethodPayment())
                .product(Product.builder().id(orderRequest.getProductId()).build())
                .deliveryAddress(DeliveryAddressMapper.convertToDeliveryAddress(orderRequest.getDeliveryAddressRequest()))
                .build();
    }

    public static OrderResponse convertToOrderResponse(Order order) {
        String status = "Chờ xác nhận";

        if (order.getStatus().equals(OrderStatusEnum.CANCELED)) {
            status = "Đã hủy";
        }
        if (order.getStatus().equals(OrderStatusEnum.COMPLETED)) {
            status = "Hoàn thành";
        }
        if (order.getStatus().equals(OrderStatusEnum.WAITING_CONFIRM)) {
            status = "Chờ xác nhận";
        }
        if (order.getStatus().equals(OrderStatusEnum.PENDING)) {
            status = "Chưa thanh toán";
        }
        if (order.getStatus().equals(OrderStatusEnum.DELIVERY)) {
            status = "Chờ giao hàng";
        }
        if (order.getStatus().equals(OrderStatusEnum.WAITING_REFUND)) {
            status = "Chờ hoàn tiền";
        }
        if (order.getStatus().equals(OrderStatusEnum.REFUNDED)) {
            status = "Đã hoàn tiền";
        }
        if (order.getStatus().equals(OrderStatusEnum.WAITING_DELIVERY)) {
            status = "Chờ lấy hàng";
        }

        return OrderResponse.builder()
                .id(order.getId())
                .status(status)
                .deliveryAddressResponse(DeliveryAddressMapper.convertToDeliveryAddressResponse(order.getDeliveryAddress()))
                .createTime(order.getCreatedDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")))
                .feeShipping(order.getFeeShipping())
                .priceProduct(order.getPriceProduct())
                .methodPayment(order.getMethodPayment())
                .productResponse(ProductMapper.convertToProductResponse(order.getProduct()))
                .createBy(order.getCreatedBy())
                .build();
    }
}
