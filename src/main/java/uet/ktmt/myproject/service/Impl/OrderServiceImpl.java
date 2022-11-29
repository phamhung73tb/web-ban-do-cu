package uet.ktmt.myproject.service.Impl;

import uet.ktmt.myproject.common.exception.BadRequestException;
import uet.ktmt.myproject.common.myEnum.OrderStatusEnum;
import uet.ktmt.myproject.common.myEnum.ProductStatusEnum;
import uet.ktmt.myproject.persistance.entity.Order;
import uet.ktmt.myproject.persistance.entity.Product;
import uet.ktmt.myproject.persistance.entity.User;
import uet.ktmt.myproject.persistance.repository.OrderRepository;
import uet.ktmt.myproject.persistance.repository.ProductRepository;
import uet.ktmt.myproject.persistance.repository.UserRepository;
import uet.ktmt.myproject.service.OrderService;
import org.apache.commons.codec.digest.HmacUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private UserRepository userRepository;

    @Transactional
    public Order create(Order order) throws Throwable {
        Product foundProduct = productRepository.findProductOnSale(order.getProduct().getId())
                .orElseThrow(() -> {
                    throw new BadRequestException("Sản phẩm đã bán hết hoặc không tìm thấy sản phẩm !!!");
                });

        order.setProduct(foundProduct);
        order.setPriceProduct(foundProduct.getPrice());
        order.getDeliveryAddress().setOrder(order);

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User foundUser = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    throw new BadRequestException("Không tìm thấy user !!!");
                });
        if (foundUser.getId() == foundProduct.getUser().getId()) {
            throw new BadRequestException("Không thể mua sản phẩm của chính mình !!!");
        }
        order.setUser(foundUser);

        order.setStatus(OrderStatusEnum.PENDING);

        if (order.getMethodPayment().equals("COD")) {
            foundProduct.setStatus(ProductStatusEnum.WAITING_CONFIRM);
            productRepository.save(foundProduct);
            order.setStatus(OrderStatusEnum.WAITING_CONFIRM);
        }
        return orderRepository.save(order);
    }

    @Transactional
    public void cancelOrder(long orderId) throws Throwable {
        Order foundOrder = orderRepository.findById(orderId)
                .orElseThrow(() -> {
                    throw new BadRequestException("Không tìm thấy hóa đơn !!!");
                });
        if (foundOrder.getStatus().equals(OrderStatusEnum.PENDING)
                || foundOrder.getStatus().equals(OrderStatusEnum.WAITING_CONFIRM)) {
            foundOrder.setStatus(OrderStatusEnum.CANCELED);
            foundOrder.getProduct().setStatus(ProductStatusEnum.STOCKING);
            orderRepository.save(foundOrder);
        } else if (foundOrder.getStatus().equals(OrderStatusEnum.PAID)) {
            foundOrder.setStatus(OrderStatusEnum.WAITING_REFUND);
            foundOrder.getProduct().setStatus(ProductStatusEnum.STOCKING);
            orderRepository.save(foundOrder);
        } else {
            throw new BadRequestException("Không thể hủy đơn !!!");
        }
    }

    final String VNP_TMNCODE = "HIOELGKH";
    final static String VNP_HASHSECRET = "TJRRFIAEXKPYCXRLYTNKGATELIKKKHRR";
    final String VNP_URL = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html?";
    final String VNP_VERSION = "2.1.0";
    final String VNP_COMMAND = "pay";
    final String ORDER_TYPE = "110000";

    public String createLink(long orderId, long totalPrice, String ipClient, String returnUrl) throws Exception {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        LocalDateTime dateTimeNow = LocalDateTime.now();

        String vnp_OrderInfo = "DH" + dtf.format(dateTimeNow);
        String vnp_TxnRef = String.valueOf(orderId);
        String vnp_IpAddress = ipClient;
        //String vnp_BankCode = "NCB";
        String vnp_CreateDate = dtf.format(dateTimeNow);
        String vnp_Amount = String.valueOf(totalPrice * 100);
        String vnp_CurrCode = "VND";
        String vnp_Locale = "vn";
        String vnp_ReturnUrl = returnUrl;

        String rawHash = "vnp_Amount=" + vnp_Amount +
//                         "&vnp_BankCode=" + vnp_BankCode +
                "&vnp_Command=" + URLEncoder.encode(VNP_COMMAND, StandardCharsets.US_ASCII.toString()) +
                "&vnp_CreateDate=" + URLEncoder.encode(vnp_CreateDate, StandardCharsets.US_ASCII.toString()) +
                "&vnp_CurrCode=" + URLEncoder.encode(vnp_CurrCode, StandardCharsets.US_ASCII.toString()) +
                "&vnp_IpAddr=" + URLEncoder.encode(vnp_IpAddress, StandardCharsets.US_ASCII.toString()) +
                "&vnp_Locale=" + URLEncoder.encode(vnp_Locale, StandardCharsets.US_ASCII.toString()) +
                "&vnp_OrderInfo=" + URLEncoder.encode(vnp_OrderInfo, StandardCharsets.US_ASCII.toString()) +
                "&vnp_OrderType=" + URLEncoder.encode(ORDER_TYPE, StandardCharsets.US_ASCII.toString()) +
                "&vnp_ReturnUrl=" + URLEncoder.encode(vnp_ReturnUrl, StandardCharsets.US_ASCII.toString()) +
                "&vnp_TmnCode=" + URLEncoder.encode(VNP_TMNCODE, StandardCharsets.US_ASCII.toString()) +
                "&vnp_TxnRef=" + URLEncoder.encode(vnp_TxnRef, StandardCharsets.US_ASCII.toString()) +
                "&vnp_Version=" + URLEncoder.encode(VNP_VERSION, StandardCharsets.US_ASCII.toString());

        String vnp_SecureHash = new HmacUtils("HmacSHA512", VNP_HASHSECRET).hmacHex(rawHash);

        String urlResult = VNP_URL + rawHash + "&vnp_SecureHash=" + vnp_SecureHash;
        return urlResult;
    }

    @Transactional
    public void checkResultPaidVnpay(String vnp_ResponseCode, String vnp_TxnRef, String vnp_Amount) throws Throwable {
        if ("00".equals(vnp_ResponseCode)) {
            long orderId = Long.parseLong(vnp_TxnRef);

            Order foundOrder = orderRepository.findById(orderId).orElseThrow(
                    () -> {
                        throw new BadRequestException("Không thể tìm thấy thông tin đơn hàng !!!");
                    }
            );
            foundOrder.setStatus(OrderStatusEnum.PAID);
            foundOrder.getProduct().setStatus(ProductStatusEnum.WAITING_CONFIRM);
            orderRepository.save(foundOrder);
        }
    }

    @Transactional
    public void completed(long orderId) throws Throwable {
        Order foundOrder = orderRepository.findById(orderId)
                .orElseThrow(() -> {
                    throw new BadRequestException("Không tìm thấy hóa đơn !!!");
                });
        if (!foundOrder.getStatus().equals(OrderStatusEnum.DELIVERY)) {
            throw new BadRequestException("Không thể xác nhận đã nhận hàng vào lúc này !!!");
        } else {
            foundOrder.setStatus(OrderStatusEnum.COMPLETED);
            orderRepository.save(foundOrder);
        }
    }

    public Page<Order> getAllOfUser(int page, String status) throws Throwable {
        Pageable pageable = PageRequest.of(page, 10);
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User foundUser = userRepository.findByUsername(username).orElseThrow(
                () -> {
                    throw new BadRequestException("Không tìm thấy thông tin user !!!");
                }
        );
        if (status.equals("all")) {
            return orderRepository.getAllOrder(pageable, foundUser.getId());
        } else {
            return orderRepository.filterOrder(pageable, foundUser.getId(), status);
        }
    }

    public Order getDetail(long orderId) throws Throwable {
        return orderRepository.findById(orderId).orElseThrow(() -> {
            throw new BadRequestException("Hóa đơn k tồn tại !!!");
        });
    }

    public Page<Order> getAll(int page, String id, String status, String from, String to) throws Throwable {
        Pageable pageable = PageRequest.of(page, 10);
        if (from.equals("")) {
            from = "1900-01-01";
        }
        if (to.equals("")) {
            to = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }
        return orderRepository.filterAllOrder(pageable, id, status, from, to);
    }

    @Transactional
    public void confirmRefund(long orderId) throws Throwable {
        Order foundOrder = orderRepository.findById(orderId)
                .orElseThrow(() -> {
                    throw new BadRequestException("Không tìm thấy hóa đơn !!!");
                });
        if (!foundOrder.getStatus().equals(OrderStatusEnum.WAITING_REFUND)) {
            throw new BadRequestException("Không thể hoàn trả đơn này !!!");
        } else {
            foundOrder.setStatus(OrderStatusEnum.REFUNDED);
            orderRepository.save(foundOrder);
        }
    }

    @Transactional
    public void paySeller(long orderId) throws Throwable {
        Order foundOrder = orderRepository.findById(orderId)
                .orElseThrow(() -> {
                    throw new BadRequestException("Không tìm thấy hóa đơn !!!");
                });
        if (!foundOrder.getStatus().equals(OrderStatusEnum.COMPLETED)) {
            throw new BadRequestException("Không thể hoàn trả đơn này !!!");
        } else {
            Product foundProduct = productRepository.findById(foundOrder.getProduct().getId()).orElseThrow(
                    () -> {
                        throw new BadRequestException("Không tìm thấy sản phẩm !!!");
                    }
            );
            foundProduct.setStatus(ProductStatusEnum.COMPLETED);
            productRepository.save(foundProduct);
        }
    }

    public int getNewOrder(String from, String to) {
        return orderRepository.getNewOrder(from, to);
    }

    public Map<String, Integer> getOrderByMonth(String from, String to) {

        LocalDate fromDate = LocalDate.parse(from, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        LocalDate toDate = LocalDate.parse(to, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        HashMap<String, Integer> result = new LinkedHashMap<>();

        if (fromDate.plus(12, ChronoUnit.MONTHS).isBefore(toDate)) {
            fromDate = toDate.minus(11, ChronoUnit.MONTHS);
            while (fromDate.isBefore(toDate)) {
                int orders = orderRepository.getOrderOfMonth(fromDate.getMonth().getValue(), fromDate.getYear());
                result.put(fromDate.getMonth().getValue() + "/" + fromDate.getYear(), orders);
                fromDate = fromDate.plus(1, ChronoUnit.MONTHS);
            }
            if (fromDate.getMonth().getValue() == toDate.getMonth().getValue()) {
                int orders = orderRepository.getOrderOfMonth(fromDate.getMonth().getValue(), fromDate.getYear());
                result.put(fromDate.getMonth().getValue() + "/" + fromDate.getYear(), orders);
            }
            return result;
        } else if (fromDate.plus(6, ChronoUnit.MONTHS).isAfter(toDate)) {
            fromDate = toDate.minus(5, ChronoUnit.MONTHS);
            while (fromDate.isBefore(toDate)) {
                int orders = orderRepository.getOrderOfMonth(fromDate.getMonth().getValue(), fromDate.getYear());
                result.put(fromDate.getMonth().getValue() + "/" + fromDate.getYear(), orders);
                fromDate = fromDate.plus(1, ChronoUnit.MONTHS);
            }
            if (fromDate.getMonth().getValue() == toDate.getMonth().getValue()) {
                int orders = orderRepository.getOrderOfMonth(fromDate.getMonth().getValue(), fromDate.getYear());
                result.put(fromDate.getMonth().getValue() + "/" + fromDate.getYear(), orders);
            }

            return result;
        } else {
            while (fromDate.isBefore(toDate)) {
                int orders = orderRepository.getOrderOfMonth(fromDate.getMonth().getValue(), fromDate.getYear());
                result.put(fromDate.getMonth().getValue() + "/" + fromDate.getYear(), orders);
                fromDate = fromDate.plus(1, ChronoUnit.MONTHS);
            }
            if (fromDate.getMonth().getValue() == toDate.getMonth().getValue()) {
                int orders = orderRepository.getOrderOfMonth(fromDate.getMonth().getValue(), fromDate.getYear());
                result.put(fromDate.getMonth().getValue() + "/" + fromDate.getYear(), orders);
            }
        }
        return result;
    }

    @Transactional
    public Order repayment(long orderId, String methodPayment) throws Throwable {
        Order foundOrder = orderRepository.findById(orderId).orElseThrow(() -> {
            throw new BadRequestException("Không tìm thấy hóa đơn !!!");
        });

        Product foundProduct = productRepository.findProductOnSale(foundOrder.getProduct().getId())
                .orElseThrow(() -> {
                    throw new BadRequestException("Sản phẩm đã bán hết hoặc không tìm thấy sản phẩm !!!");
                });

        foundOrder.setProduct(foundProduct);
        foundOrder.setPriceProduct(foundProduct.getPrice());

        foundOrder.setStatus(OrderStatusEnum.PENDING);

        if (methodPayment.equals("COD")) {
            foundProduct.setStatus(ProductStatusEnum.WAITING_CONFIRM);
            productRepository.save(foundProduct);
            foundOrder.setStatus(OrderStatusEnum.WAITING_CONFIRM);
        }
        return orderRepository.save(foundOrder);
    }

    public Page<Order> getMyOrderSale(int page, String status, String from, String to) {
        LocalDate fromDate = LocalDate.parse(from, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        LocalDate toDate = LocalDate.parse(to, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        Pageable pageable = PageRequest.of(page, 10);
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if (status.equals("all")) {
            return orderRepository
                    .filterOrderSale(pageable, username, fromDate.toString(), toDate.toString());
        }
        return orderRepository
                .filterOrderSaleByStatus(pageable, username, status, fromDate.toString(), toDate.toString());
    }
}
