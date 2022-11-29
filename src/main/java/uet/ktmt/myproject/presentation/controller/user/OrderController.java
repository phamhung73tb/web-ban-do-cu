package uet.ktmt.myproject.presentation.controller.user;

import uet.ktmt.myproject.presentation.mapper.DeliveryAddressMapper;
import uet.ktmt.myproject.presentation.mapper.OrderMapper;
import uet.ktmt.myproject.presentation.mapper.ProductMapper;
import uet.ktmt.myproject.presentation.request.DeliveryAddressRequest;
import uet.ktmt.myproject.presentation.request.OrderRequest;
import uet.ktmt.myproject.presentation.response.OrderResponse;
import uet.ktmt.myproject.presentation.response.ProductResponse;
import uet.ktmt.myproject.service.DeliveryAddressService;
import uet.ktmt.myproject.service.OrderService;
import uet.ktmt.myproject.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Controller
@RequestMapping("/user/order")
public class OrderController {
    @Autowired
    private OrderService orderService;
    @Autowired
    private ProductService productService;
    @Autowired
    private DeliveryAddressService deliveryAddressService;

    @GetMapping("/create/{id}")
    public String getPageCreateOrder(Model model, @PathVariable(value = "id") long productId) throws Throwable {
        ProductResponse productResponse = ProductMapper.convertToProductResponse(productService.getDetailProduct(productId));
        model.addAttribute("productResponse", productResponse);
        model.addAttribute("orderRequest", new OrderRequest());
        return "user/checkout";
    }

    @PostMapping("/create")
    public ResponseEntity<String> create(HttpServletRequest request, HttpServletResponse response,
                                         Model model, @RequestBody OrderRequest orderedRequest) throws Throwable {
        OrderResponse orderResponse = OrderMapper.convertToOrderResponse(
                orderService.create(OrderMapper.convertToOrder(orderedRequest))
        );

        if (orderedRequest.getMethodPayment().equals("VNPAY")) {
            String link = orderService.createLink(
                    orderResponse.getId()
                    , orderResponse.getPriceProduct() + orderResponse.getFeeShipping()
                    , request.getRemoteAddr()
                    , MvcUriComponentsBuilder.fromController(OrderController.class).toUriString() + "/response/vnpay");
            return ResponseEntity.ok().body(link);
        }
        return ResponseEntity.ok().body("Tạo đơn thành công !!!");
    }

    @GetMapping(value = "/response/vnpay", name = "responsePaymentVnpay")
    public String checkPaymentVnpay(@RequestParam(name = "vnp_ResponseCode") String vnp_ResponseCode,
                                    @RequestParam(name = "vnp_TxnRef") String vnp_TxnRef,
                                    @RequestParam(name = "vnp_Amount") String vnp_Amount,
                                    HttpServletRequest request, HttpServletResponse response) throws Throwable {

        //log.info("Mapped checkPaymentVnpay method {{GET: /payment/zalopay/orderId}}");
        orderService.checkResultPaidVnpay(vnp_ResponseCode, vnp_TxnRef, vnp_Amount);
        return "redirect:/user/order/" + Long.parseLong(vnp_TxnRef);
    }

    @GetMapping("/get-all")
    public String getAll(Model model, @RequestParam(name = "page", required = false, defaultValue = "1") int page
            , @RequestParam(name = "status", required = false, defaultValue = "all") String status) throws Throwable {
        Page<OrderResponse> orderResponseList = orderService.getAllOfUser(page - 1, status)
                .map(OrderMapper::convertToOrderResponse);
        model.addAttribute("list_order", orderResponseList.getContent());
        model.addAttribute("current_page", page);
        model.addAttribute("total_page", orderResponseList.getTotalPages());
        model.addAttribute("status", status);
        return "user/my_list_order";
    }

    @GetMapping("/{id}")
    public String getDetail(Model model, @PathVariable(value = "id") long orderId) throws Throwable {
        OrderResponse orderResponse = OrderMapper.convertToOrderResponse(orderService.getDetail(orderId));
        model.addAttribute("orderResponse", orderResponse);
        return "user/my_order_detail";
    }

    @GetMapping("/completed/{id}")
    public String completed(Model model, @PathVariable(value = "id") long orderId) throws Throwable {
        orderService.completed(orderId);
        return "redirect:/user/order/" + orderId;
    }

    @PostMapping("/change-delivery-address/{id}")
    public ResponseEntity<String> changeDeliveryAddress(Model model
            , @PathVariable(value = "id") long orderId, @RequestBody DeliveryAddressRequest deliveryAddressRequest) throws Throwable {
        deliveryAddressService.changeDelivery(orderId, DeliveryAddressMapper.convertToDeliveryAddress(deliveryAddressRequest));
        return ResponseEntity.ok().body("Đổi địa chỉ thành công !!!");
    }

    @PutMapping("/cancel-order/{id}")
    public ResponseEntity<String> cancelOrder(Model model, @PathVariable(value = "id") long orderId) throws Throwable {
        orderService.cancelOrder(orderId);
        return ResponseEntity.ok().body("Đã hủy đơn hàng !!!");
    }

    @PostMapping("/repayment/{id}")
    public ResponseEntity<String> changeDeliveryAddress(@PathVariable(value = "id") long orderId
            , @RequestParam(value = "methodPayment", required = true) String methodPayment
            , HttpServletRequest request) throws Throwable {
        OrderResponse orderResponse = OrderMapper.convertToOrderResponse(orderService.repayment(orderId, methodPayment));
        if (orderResponse.getMethodPayment().equals("VNPAY")) {
            String link = orderService.createLink(
                    orderResponse.getId()
                    , orderResponse.getPriceProduct() + orderResponse.getFeeShipping()
                    , request.getRemoteAddr()
                    , MvcUriComponentsBuilder.fromController(OrderController.class).toUriString() + "/response/vnpay");
            return ResponseEntity.ok().body(link);
        }
        return ResponseEntity.ok().body("Tạo đơn thành công !!!");
    }

    @GetMapping("/get-all-sale")
    public String getAllOrderedSale(Model model, @RequestParam(name = "page", required = false, defaultValue = "1") int page
            , @RequestParam(name = "status", required = false, defaultValue = "all") String status
            , @RequestParam(name = "from", required = false, defaultValue = "") String from
            , @RequestParam(name = "to", required = false, defaultValue = "") String to) throws Throwable {
        if (from.equals("")) {
            from = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }
        if (to.equals("")) {
            to = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }
        Page<OrderResponse> orderResponseList = orderService.getMyOrderSale(page - 1, status, from, to)
                .map(OrderMapper::convertToOrderResponse);
        int total_order = orderResponseList.getContent().size();
        long total_money = orderResponseList.getContent().stream().mapToLong(o -> o.getPriceProduct()).sum();
        model.addAttribute("total_order", total_order);
        model.addAttribute("total_money", total_money);
        model.addAttribute("list_order", orderResponseList.getContent());
        model.addAttribute("current_page", page);
        model.addAttribute("total_page", orderResponseList.getTotalPages());
        model.addAttribute("status", status);
        model.addAttribute("from", from);
        model.addAttribute("to", to);
        return "user/my_list_order_sale";
    }

    @GetMapping("/sale/{id}")
    public String getDetailOrderSale(Model model, @PathVariable(value = "id") long orderId) throws Throwable {
        OrderResponse orderResponse = OrderMapper.convertToOrderResponse(orderService.getDetail(orderId));
        model.addAttribute("orderResponse", orderResponse);
        return "user/my_order_sale_detail";
    }
}
