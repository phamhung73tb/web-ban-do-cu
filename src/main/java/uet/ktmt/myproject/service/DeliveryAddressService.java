package uet.ktmt.myproject.service;

import uet.ktmt.myproject.persistance.entity.DeliveryAddress;
import org.springframework.stereotype.Service;

@Service
public interface DeliveryAddressService {
    DeliveryAddress getInfoDelivery(long productId) throws Throwable;

    void changeDelivery(long orderId, DeliveryAddress convertToDeliveryAddress) throws Throwable;
}
