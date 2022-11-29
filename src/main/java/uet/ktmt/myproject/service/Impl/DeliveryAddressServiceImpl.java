package uet.ktmt.myproject.service.Impl;

import uet.ktmt.myproject.common.exception.BadRequestException;
import uet.ktmt.myproject.common.myEnum.OrderStatusEnum;
import uet.ktmt.myproject.persistance.entity.DeliveryAddress;
import uet.ktmt.myproject.persistance.entity.Order;
import uet.ktmt.myproject.persistance.repository.DeliveryAddressRepository;
import uet.ktmt.myproject.persistance.repository.OrderRepository;
import uet.ktmt.myproject.service.DeliveryAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

@Component
public class DeliveryAddressServiceImpl implements DeliveryAddressService {
    @Autowired
    private DeliveryAddressRepository deliveryAddressRepository;
    @Autowired
    private OrderRepository orderRepository;

    public DeliveryAddress getInfoDelivery(long productId) throws Throwable {
        Order foundOrder = orderRepository.findByProductId(productId).orElseThrow(
                () -> {
                    throw new BadRequestException("Không tìm thấy đơn hàng đặt sản phẩm này !!!");
                }
        );
        return deliveryAddressRepository.findByOrderId(foundOrder.getId()).orElseThrow(
                () -> {
                    throw new BadRequestException("Không tìm thấy địa chỉ giao hàng !!!");
                }
        );
    }

    @Transactional
    public void changeDelivery(long orderId, DeliveryAddress convertToDeliveryAddress) throws Throwable {
        DeliveryAddress foundDeliveryAddress = deliveryAddressRepository.findByOrderId(orderId).orElseThrow(
                () -> {
                    throw new BadRequestException("Không tìm thấy địa chỉ giao hàng đặt sản phẩm này !!!");
                }
        );

        if (foundDeliveryAddress.getOrder().getStatus().equals(OrderStatusEnum.WAITING_CONFIRM)
            || foundDeliveryAddress.getOrder().getStatus().equals(OrderStatusEnum.PAID)) {
            foundDeliveryAddress.setCodeProvince(convertToDeliveryAddress.getCodeProvince());
            foundDeliveryAddress.setProvince(convertToDeliveryAddress.getProvince());
            foundDeliveryAddress.setCodeDistrict(convertToDeliveryAddress.getCodeDistrict());
            foundDeliveryAddress.setDistrict(convertToDeliveryAddress.getDistrict());
            foundDeliveryAddress.setCodeCommune(convertToDeliveryAddress.getCodeCommune());
            foundDeliveryAddress.setCommune(convertToDeliveryAddress.getCommune());
            foundDeliveryAddress.setDetail(convertToDeliveryAddress.getDetail());
            foundDeliveryAddress.setCellphone(convertToDeliveryAddress.getCellphone());
            foundDeliveryAddress.setFullName(convertToDeliveryAddress.getFullName());
            deliveryAddressRepository.save(foundDeliveryAddress);
        }
        else {
            throw new BadRequestException("Không thể đổi địa chỉ lúc này !!!");
        }
    }
}
